package io.spaceurgent.currency.rates.service;

import io.spaceurgent.currency.rates.client.CurrencyRateClient;
import io.spaceurgent.currency.rates.dao.CurrencyRateDao;
import io.spaceurgent.currency.rates.model.CurrencyRate;
import reactor.core.publisher.Flux;

public abstract class AbstractCurrencyRatesService<T extends CurrencyRate> implements CurrencyRateService<T> {
    protected final CurrencyRateClient<T> currencyRateClient;
    protected final CurrencyRateDao<T> currencyRateDao;

    protected AbstractCurrencyRatesService(CurrencyRateClient<T> currencyRateClient,
                                           CurrencyRateDao<T> currencyRateDao) {
        this.currencyRateClient = currencyRateClient;
        this.currencyRateDao = currencyRateDao;
    }

    @Override
    public Flux<T> getLatestRates() {
        return currencyRateClient.fetchRates()
                .flatMap(currencyRateDao::save)
                .switchIfEmpty(currencyRateDao.findLastUniqueCurrencyRates());
    }
}
