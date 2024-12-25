package io.spaceurgent.currency.rates.client;

import okhttp3.mockwebserver.RecordedRequest;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import reactor.test.StepVerifier;

import java.time.Duration;

import static io.spaceurgent.currency.rates.client.CurrencyRateApiConstants.API_KEY_HEADER_NAME;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class RateClientTestUtils {
    private RateClientTestUtils() {
    }

    public final static String MOCK_API_KEY = "test-key";
    public final static String ERROR_RESPONSE_BODY = """
                { "error" : "Internal server error" }
            """;

    public static void assertGetRequestPathIs(String uri, RecordedRequest request) {
        assertEquals("GET", request.getMethod());
        assertEquals(uri, request.getPath());
    }

    public static void assertApiKeySent(RecordedRequest recordedRequest) {
        assertEquals(MOCK_API_KEY, recordedRequest.getHeader(API_KEY_HEADER_NAME));
    }

    public static void assertEmptyFlux(Flux<?> flux) {
        requireNonNull(flux, "Flux input is required");
        StepVerifier.create(flux)
                .expectNextCount(0)
                .verifyComplete();
    }

    public static WebClient webClientWithTimeout(String baseUrl) {
        final var httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(2));
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(API_KEY_HEADER_NAME, MOCK_API_KEY)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
