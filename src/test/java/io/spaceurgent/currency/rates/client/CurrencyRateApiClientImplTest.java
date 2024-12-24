package io.spaceurgent.currency.rates.client;

import io.spaceurgent.currency.rates.client.dto.CryptoRateInfo;
import io.spaceurgent.currency.rates.client.dto.FiatRateInfo;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static io.spaceurgent.currency.rates.client.CurrencyRateApiConstants.API_KEY_HEADER_NAME;
import static io.spaceurgent.currency.rates.dao.DaoTestUtils.convertJdbcToR2dbcUrl;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(classes = CurrencyRateApiConfiguration.class)
class CurrencyRateApiClientImplTest {
    private final static String MOCK_API_KEY = "test-key";
    private final static String FIAT_CURRENCY_RATE_URI = "/fiat-currency-rates";
    private final static String ERROR_RESPONSE_BODY = """
                { "error" : "Internal server error" }
            """;
    private final static String FIAT_CURRENCY_RATES_RESPONSE_BODY = """
            [
                { "currency" : "USD", "rate" : 45.67 },
                { "currency" : "EUR", "rate" : 56.78 }
            ]
            """;
    private final static String CRYPTO_CURRENCY_RATES_RESPONSE_BODY = """
            [
                { "name" : "BTC", "value" : 12345.67 },
                { "name" : "ETH", "value" : 234.56 }
            ]
            """;

    private static MockWebServer mockWebServer;

    @Autowired
    private CurrencyRateApiClient currencyRateApiClient;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("currency-rate-api.base-url", () -> "http://localhost:" + mockWebServer.getPort());
        registry.add("currency-rate-api.secret-key", () -> MOCK_API_KEY);
    }

    @BeforeAll
    static void beforeAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setUp() {
        final var httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(2));
        final var webClient = WebClient.builder()
                .baseUrl("http://localhost:" + mockWebServer.getPort())
                .defaultHeader(API_KEY_HEADER_NAME, MOCK_API_KEY)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
        currencyRateApiClient = new CurrencyRateApiClientImpl(webClient);
    }

    @Test
    void fetchFiatCurrencyRates_shouldReturnCurrencyRateInfoFlux() throws InterruptedException {
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(FIAT_CURRENCY_RATES_RESPONSE_BODY)
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        );

        final var rates = currencyRateApiClient.fetchFiatCurrencyRates();

        StepVerifier.create(rates)
                .expectNextMatches(fiatCurrencyRateEqualsPredicate("USD", BigDecimal.valueOf(45.67)))
                .expectNextMatches(fiatCurrencyRateEqualsPredicate("EUR", BigDecimal.valueOf(56.78)))
                .verifyComplete();
        final var request = mockWebServer.takeRequest();
        assertApiKeySent(request);
        assertGetRequestPathIs(FIAT_CURRENCY_RATE_URI, request);
    }

    @Test
    void fetchFiatCurrencyRates_shouldReturnEmptyFlux_whenApiReturnsError() throws InterruptedException {
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(500)
                        .setBody(ERROR_RESPONSE_BODY)
        );
        final var rates = currencyRateApiClient.fetchFiatCurrencyRates();
        assertEmptyFlux(rates);
        final var request = mockWebServer.takeRequest();
        assertApiKeySent(request);
        assertGetRequestPathIs(FIAT_CURRENCY_RATE_URI, request);
    }

    @Test
    void fetchFiatCurrencyRates_shouldReturnEmptyFlux_onRequestTimeout() throws InterruptedException {
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(FIAT_CURRENCY_RATES_RESPONSE_BODY)
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBodyDelay(5, TimeUnit.SECONDS)
        );

        final var rates = currencyRateApiClient.fetchFiatCurrencyRates();

        assertEmptyFlux(rates);
        final var request = mockWebServer.takeRequest();
        assertApiKeySent(request);
        assertGetRequestPathIs(FIAT_CURRENCY_RATE_URI, request);
    }

    @Test
    void fetchCryptoCurrencyRates_shouldReturnCurrencyRateInfoFlux() throws InterruptedException {
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(CRYPTO_CURRENCY_RATES_RESPONSE_BODY)
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        );

        final var rates = currencyRateApiClient.fetchCryptoCurrencyRates();

        StepVerifier.create(rates)
                .expectNextMatches(cryptoCurrencyRateEqualsPredicate("BTC", BigDecimal.valueOf(12345.67)))
                .expectNextMatches(cryptoCurrencyRateEqualsPredicate("ETH", BigDecimal.valueOf(234.56)))
                .verifyComplete();

        final var request = mockWebServer.takeRequest();
        assertApiKeySent(request);
        assertGetRequestPathIs("/crypto-currency-rates", request);
    }

    @Test
    void fetchCryptoCurrencyRates_shouldReturnEmptyFlux_whenApiReturnsError() throws InterruptedException {
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(500)
                        .setBody(ERROR_RESPONSE_BODY)
        );
        final var rates = currencyRateApiClient.fetchCryptoCurrencyRates();
        assertEmptyFlux(rates);
        final var request = mockWebServer.takeRequest();
        assertApiKeySent(request);
        assertGetRequestPathIs("/crypto-currency-rates", request);
    }

    @Test
    void fetchCryptoCurrencyRates_shouldReturnEmptyFlux_onRequestTimeout() throws InterruptedException {
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(CRYPTO_CURRENCY_RATES_RESPONSE_BODY)
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBodyDelay(5, TimeUnit.SECONDS)
        );
        final var rates = currencyRateApiClient.fetchCryptoCurrencyRates();
        assertEmptyFlux(rates);
        final var request = mockWebServer.takeRequest();
        assertApiKeySent(request);
        assertGetRequestPathIs("/crypto-currency-rates", request);
    }

    private static void assertApiKeySent(RecordedRequest recordedRequest) {
        assertEquals(MOCK_API_KEY, recordedRequest.getHeader(API_KEY_HEADER_NAME));
    }

    private static void assertGetRequestPathIs(String uri, RecordedRequest request) {
        assertEquals("GET", request.getMethod());
        assertEquals(uri, request.getPath());
    }

    private static void assertEmptyFlux(Flux<?> flux) {
        requireNonNull(flux, "Flux input is required");
        StepVerifier.create(flux)
                .expectNextCount(0)
                .verifyComplete();
    }

    private static Predicate<FiatRateInfo> fiatCurrencyRateEqualsPredicate(String currency, BigDecimal rate) {
        return currencyRateInfo -> currency.equals(currencyRateInfo.currency())
                && rate.equals(currencyRateInfo.rate());
    }

    private static Predicate<CryptoRateInfo> cryptoCurrencyRateEqualsPredicate(String name, BigDecimal value) {
        return currencyRateInfo -> name.equals(currencyRateInfo.name())
                && value.equals(currencyRateInfo.value());
    }
}