package io.spaceurgent.currency.rates.dao;

import io.spaceurgent.currency.rates.model.CurrencyRate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CurrencyRateDao<T extends CurrencyRate> {
    Mono<T> save(T currencyRate);

    Flux<T> findAll();

    Flux<T> findLastUniqueCurrencyRates();
}
