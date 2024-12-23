package io.spaceurgent.currency.rate.api.service;

import io.spaceurgent.currency.rate.api.dao.CurrencyRateDao;
import io.spaceurgent.currency.rate.api.model.CurrencyRate;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public abstract class AbstractCurrencyRateService<T extends CurrencyRate> implements CurrencyRateService<T> {
    private final CurrencyRateDao<T> currencyRateDao;

    @Override
    public Mono<T> save(T rate) {
        requireNonNull(rate, "Rate is required");
        return currencyRateDao.save(rate);
    }

    @Override
    public Flux<T> getLatestRates() {
        return currencyRateDao.findLastUniqueCurrencyRates();
    }
}
