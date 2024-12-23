package io.spaceurgent.currency.rate.api.service;

import io.spaceurgent.currency.rate.api.model.CurrencyRate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CurrencyRateService<T extends CurrencyRate> {

    Flux<T> getLatestRates();

    Mono<T> save(T rate);
}
