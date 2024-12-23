package io.spaceurgent.currency.rate.api.service.impl;

import io.spaceurgent.currency.rate.api.client.CurrencyRateApiClient;
import io.spaceurgent.currency.rate.api.dao.CurrencyRateDao;
import io.spaceurgent.currency.rate.api.model.FiatRate;
import io.spaceurgent.currency.rate.api.service.CurrencyRateService;
import io.spaceurgent.currency.rate.api.service.ToModelMapping;
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
