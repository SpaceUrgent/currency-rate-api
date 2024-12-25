package io.spaceurgent.currency.rates.service.impl;

import io.spaceurgent.currency.rates.client.CurrencyRateClient;
import io.spaceurgent.currency.rates.dao.CurrencyRateDao;
import io.spaceurgent.currency.rates.model.CryptoRate;
import io.spaceurgent.currency.rates.service.AbstractCurrencyRatesService;
import org.springframework.stereotype.Service;

@Service
public class CryptoRateService extends AbstractCurrencyRatesService<CryptoRate> {

    public CryptoRateService(CurrencyRateClient<CryptoRate> currencyRateClient, CurrencyRateDao<CryptoRate> currencyRateDao) {
        super(currencyRateClient, currencyRateDao);
    }
}
