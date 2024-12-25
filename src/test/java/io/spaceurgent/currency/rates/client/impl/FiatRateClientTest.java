package io.spaceurgent.currency.rates.client.impl;

import io.spaceurgent.currency.rates.client.CurrencyRateApiConfiguration;
import io.spaceurgent.currency.rates.model.FiatRate;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static io.spaceurgent.currency.rates.client.RateClientTestUtils.ERROR_RESPONSE_BODY;
import static io.spaceurgent.currency.rates.client.RateClientTestUtils.MOCK_API_KEY;
import static io.spaceurgent.currency.rates.client.RateClientTestUtils.assertApiKeySent;
import static io.spaceurgent.currency.rates.client.RateClientTestUtils.assertEmptyFlux;
import static io.spaceurgent.currency.rates.client.RateClientTestUtils.assertGetRequestPathIs;
import static io.spaceurgent.currency.rates.client.RateClientTestUtils.webClientWithTimeout;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(classes = {CurrencyRateApiConfiguration.class, FiatRateClient.class})
class FiatRateClientTest {
    private final static String FIAT_CURRENCY_RATE_URI = "/fiat-currency-rates";
    private final static String FIAT_CURRENCY_RATES_RESPONSE_BODY = """
            [
                { "currency" : "USD", "rate" : 45.67 },
                { "currency" : "EUR", "rate" : 56.78 }
            ]
            """;

    private static MockWebServer mockWebServer;

    @Autowired
    private FiatRateClient fiatRateClient;

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

    @Test
    void fetchFiatCurrencyRates_shouldReturnCurrencyRateInfoFlux() throws InterruptedException {
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(FIAT_CURRENCY_RATES_RESPONSE_BODY)
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        );

        final var rates = fiatRateClient.fetchRates();

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
        final var rates = fiatRateClient.fetchRates();
        assertEmptyFlux(rates);
        final var request = mockWebServer.takeRequest();
        assertApiKeySent(request);
        assertGetRequestPathIs(FIAT_CURRENCY_RATE_URI, request);
    }

    @Test
    void fetchFiatCurrencyRates_shouldReturnEmptyFlux_onRequestTimeout() throws InterruptedException {
//        final var httpClient = HttpClient.create()
//                .responseTimeout(Duration.ofSeconds(2));
//        final var webClient = WebClient.builder()
//                .baseUrl("http://localhost:" + mockWebServer.getPort())
//                .defaultHeader(API_KEY_HEADER_NAME, MOCK_API_KEY)
//                .clientConnector(new ReactorClientHttpConnector(httpClient))
//                .build();
        final var fiatRateClientWithTimeout = new FiatRateClient(
                webClientWithTimeout("http://localhost:" + mockWebServer.getPort())
        );
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(FIAT_CURRENCY_RATES_RESPONSE_BODY)
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBodyDelay(5, TimeUnit.SECONDS)
        );

        final var rates = fiatRateClientWithTimeout.fetchRates();

        assertEmptyFlux(rates);
        final var request = mockWebServer.takeRequest();
        assertApiKeySent(request);
        assertGetRequestPathIs(FIAT_CURRENCY_RATE_URI, request);
    }


    private static Predicate<FiatRate> fiatCurrencyRateEqualsPredicate(String currency, BigDecimal rate) {
        return fiatRate -> currency.equals(fiatRate.getCurrency())
                && rate.equals(fiatRate.getRate());
    }
}