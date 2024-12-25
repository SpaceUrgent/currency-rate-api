package io.spaceurgent.currency.rates.client;

import io.spaceurgent.currency.rates.model.CurrencyRate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

@Slf4j
public abstract class AbstractCurrencyRateClient<T extends CurrencyRate> implements CurrencyRateClient<T> {
    private final WebClient webClient;

    protected AbstractCurrencyRateClient(WebClient webClient) {
        this.webClient = webClient;
    }

    protected <ApiResponseType> Flux<T> doGetAndMap(
            String uri,
            Class<ApiResponseType> apiResponseType,
            Function<ApiResponseType, T> toCurrencyRateFunction) {
        requireNonNull(toCurrencyRateFunction, "Function mapping api response type to return type is required");
        return doGet(uri, apiResponseType).map(toCurrencyRateFunction);
    }

    protected <ApiResponseType> Flux<ApiResponseType> doGet(String uri, Class<ApiResponseType> returnClass) {
        requireNonNull(uri, "Uri path is required");
        requireNonNull(returnClass, "Return type is required");
        return webClient.get().uri(uri)
                .retrieve()
                .bodyToFlux(returnClass)
                .switchIfEmpty(s -> {
                    log.warn("GET {} returned empty rate list", uri);
                })
                .onErrorResume(WebClientResponseException.class, exception -> {
                    log.error("Error calling GET '{}'. Response status '{}', response body: {}",
                            uri, exception.getStatusCode(), exception.getResponseBodyAsString(), exception);
                    return Flux.empty();
                })
                .onErrorResume(Throwable.class, throwable -> {
                    log.error("Error calling GET '{}'. Error message: {}", uri, throwable.getMessage(), throwable);
                    return Flux.empty();
                });
    }
}
