package io.spaceurgent.currency.rate.api.service.impl;

import io.spaceurgent.currency.rate.api.client.CurrencyRateApiClient;
import io.spaceurgent.currency.rate.api.dao.CurrencyRateDao;
import io.spaceurgent.currency.rate.api.model.CryptoRate;
import io.spaceurgent.currency.rate.api.service.CurrencyRateService;
import io.spaceurgent.currency.rate.api.service.ToModelMapping;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class CryptoRateService implements CurrencyRateService<CryptoRate> {
    private final CurrencyRateApiClient currencyRateApi;
    private final CurrencyRateDao<CryptoRate> cryptoRateDao;

    public CryptoRateService(CurrencyRateDao<CryptoRate> currencyRateDao,
                             CurrencyRateApiClient currencyRateApi) {
        this.cryptoRateDao = currencyRateDao;
        this.currencyRateApi = currencyRateApi;
    }

    @Override
    public Flux<CryptoRate> getLatestRates() {
        return currencyRateApi.fetchCryptoCurrencyRates()
                .flatMap(rateInfo -> cryptoRateDao.save(ToModelMapping.toCryptoRate(rateInfo)))
                .switchIfEmpty(cryptoRateDao.findLastUniqueCurrencyRates());
    }
}
