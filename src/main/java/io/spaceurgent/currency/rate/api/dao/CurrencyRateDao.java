package io.spaceurgent.currency.rate.api.dao;

import io.spaceurgent.currency.rate.api.model.CurrencyRate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CurrencyRateDao<T extends CurrencyRate> {
    Mono<T> save(T currencyRate);

    Flux<T> findAll();

    Flux<T> findLastUniqueCurrencyRates();
}
