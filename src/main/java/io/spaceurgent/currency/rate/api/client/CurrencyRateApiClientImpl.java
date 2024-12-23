package io.spaceurgent.currency.rate.api.client;

import io.spaceurgent.currency.rate.api.client.dto.CryptoRateInfo;
import io.spaceurgent.currency.rate.api.client.dto.FiatRateInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import static java.util.Objects.requireNonNull;

@Slf4j
public class CurrencyRateApiClientImpl implements CurrencyRateApiClient {
    private final WebClient webClient;

    public CurrencyRateApiClientImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Flux<FiatRateInfo> fetchFiatCurrencyRates() {
        return doGet("/fiat-currency-rates", FiatRateInfo.class);
    }

    @Override
    public Flux<CryptoRateInfo> fetchCryptoCurrencyRates() {
        return doGet("/crypto-currency-rates", CryptoRateInfo.class);
    }

    private <T> Flux<T> doGet(String uri, Class<T> returnClass) {
        requireNonNull(uri, "Uri path is required");
        requireNonNull(returnClass, "Return type is required");
        return webClient.get().uri(uri)
                .retrieve()
                .bodyToFlux(returnClass)
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
