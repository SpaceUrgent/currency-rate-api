package io.spaceurgent.currency.rates.client.impl;

import io.spaceurgent.currency.rates.client.CurrencyRateApiConfiguration;
import io.spaceurgent.currency.rates.model.CryptoRate;
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
@ContextConfiguration(classes = {CurrencyRateApiConfiguration.class, CryptoRateClient.class})
class CryptoRateClientTest {
    private final static String CRYPTO_CURRENCY_RATE_URI = "/crypto-currency-rates";
    private final static String CRYPTO_CURRENCY_RATES_RESPONSE_BODY = """
            [
                { "name" : "BTC", "value" : 12345.67 },
                { "name" : "ETH", "value" : 234.56 }
            ]
            """;

    private static MockWebServer mockWebServer;

    @Autowired
    private CryptoRateClient cryptoRateClient;

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
    void fetchCryptoCurrencyRates_shouldReturnCurrencyRateInfoFlux() throws InterruptedException {
        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(CRYPTO_CURRENCY_RATES_RESPONSE_BODY)
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        );

        final var rates = cryptoRateClient.fetchRates();

        StepVerifier.create(rates)
                .expectNextMatches(cryptoCurrencyRateEqualsPredicate("BTC", BigDecimal.valueOf(12345.67)))
                .expectNextMatches(cryptoCurrencyRateEqualsPredicate("ETH", BigDecimal.valueOf(234.56)))
                .verifyComplete();

        final var request = mockWebServer.takeRequest();
        assertApiKeySent(request);
        assertGetRequestPathIs(CRYPTO_CURRENCY_RATE_URI, request);
    }

    @Test
    void fetchCryptoCurrencyRates_shouldReturnEmptyFlux_whenApiReturnsError() throws InterruptedException {
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(500)
                        .setBody(ERROR_RESPONSE_BODY)
        );
        final var rates = cryptoRateClient.fetchRates();
        assertEmptyFlux(rates);
        final var request = mockWebServer.takeRequest();
        assertApiKeySent(request);
        assertGetRequestPathIs(CRYPTO_CURRENCY_RATE_URI, request);
    }

    @Test
    void fetchCryptoCurrencyRates_shouldReturnEmptyFlux_onRequestTimeout() throws InterruptedException {
        final var cryptoRateClientWithTimeout = new CryptoRateClient(
                webClientWithTimeout("http://localhost:" + mockWebServer.getPort())
        );

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(CRYPTO_CURRENCY_RATES_RESPONSE_BODY)
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBodyDelay(5, TimeUnit.SECONDS)
        );
        final var rates = cryptoRateClientWithTimeout.fetchRates();
        assertEmptyFlux(rates);
        final var request = mockWebServer.takeRequest();
        assertApiKeySent(request);
        assertGetRequestPathIs(CRYPTO_CURRENCY_RATE_URI, request);
    }

    private static Predicate<CryptoRate> cryptoCurrencyRateEqualsPredicate(String name, BigDecimal value) {
        return currencyRate -> name.equals(currencyRate.getName())
                && value.equals(currencyRate.getValue());
    }

}