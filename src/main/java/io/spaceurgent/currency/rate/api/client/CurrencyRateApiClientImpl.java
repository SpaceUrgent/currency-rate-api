package io.spaceurgent.currency.rate.api.client;

import io.spaceurgent.currency.rate.api.client.dto.CryptoCurrencyRateInfo;
import io.spaceurgent.currency.rate.api.client.dto.FiatCurrencyRateInfo;
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
    public Flux<FiatCurrencyRateInfo> fetchFiatCurrencyRates() {
        return doGet("/fiat-currency-rates", FiatCurrencyRateInfo.class);
    }

    @Override
    public Flux<CryptoCurrencyRateInfo> fetchCryptoCurrencyRates() {
        return doGet("/crypto-currency-rates", CryptoCurrencyRateInfo.class);
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
