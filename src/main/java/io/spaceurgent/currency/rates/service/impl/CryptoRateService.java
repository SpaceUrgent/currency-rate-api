package io.spaceurgent.currency.rates.service.impl;

import io.spaceurgent.currency.rates.client.CurrencyRateApiClient;
import io.spaceurgent.currency.rates.dao.CurrencyRateDao;
import io.spaceurgent.currency.rates.model.CryptoRate;
import io.spaceurgent.currency.rates.service.CurrencyRateService;
import io.spaceurgent.currency.rates.service.ToModelMapping;
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
