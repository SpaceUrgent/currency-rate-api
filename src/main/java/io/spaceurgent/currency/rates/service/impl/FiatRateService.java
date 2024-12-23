package io.spaceurgent.currency.rates.service.impl;

import io.spaceurgent.currency.rates.client.CurrencyRateApiClient;
import io.spaceurgent.currency.rates.dao.CurrencyRateDao;
import io.spaceurgent.currency.rates.model.FiatRate;
import io.spaceurgent.currency.rates.service.CurrencyRateService;
import io.spaceurgent.currency.rates.service.ToModelMapping;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class FiatRateService implements CurrencyRateService<FiatRate> {
    private final CurrencyRateApiClient currencyRateApiClient;
    private final CurrencyRateDao<FiatRate> fiatRateDao;

    public FiatRateService(CurrencyRateDao<FiatRate> fiatRateDao,
                           CurrencyRateApiClient currencyRateApiClient) {
        this.fiatRateDao = fiatRateDao;
        this.currencyRateApiClient = currencyRateApiClient;
    }

    @Override
    public Flux<FiatRate> getLatestRates() {
        return currencyRateApiClient.fetchFiatCurrencyRates()
                .flatMap(rateInfo -> fiatRateDao.save(ToModelMapping.toFiatRate(rateInfo)))
                .switchIfEmpty(fiatRateDao.findLastUniqueCurrencyRates());
    }
}
