package io.spaceurgent.currency.rate.api.dao;

import io.spaceurgent.currency.rate.api.model.CurrencyRate;
import reactor.core.publisher.Flux;

public interface CurrencyRateDao<T extends CurrencyRate> {
    Flux<T> saveAll(Flux<T> flux);

    Flux<T> findAll();

    Flux<T> findLastUniqueCurrencyRates();
}
