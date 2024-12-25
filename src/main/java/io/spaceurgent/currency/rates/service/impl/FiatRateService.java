package io.spaceurgent.currency.rates.service.impl;

import io.spaceurgent.currency.rates.client.CurrencyRateClient;
import io.spaceurgent.currency.rates.dao.CurrencyRateDao;
import io.spaceurgent.currency.rates.model.FiatRate;
import io.spaceurgent.currency.rates.service.AbstractCurrencyRatesService;
import org.springframework.stereotype.Service;

@Service
public class FiatRateService extends AbstractCurrencyRatesService<FiatRate> {

    public FiatRateService(CurrencyRateClient<FiatRate> currencyRateClient, CurrencyRateDao<FiatRate> currencyRateDao) {
        super(currencyRateClient, currencyRateDao);
    }
}
