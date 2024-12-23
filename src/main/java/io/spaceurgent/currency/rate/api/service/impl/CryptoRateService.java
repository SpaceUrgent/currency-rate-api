package io.spaceurgent.currency.rate.api.service.impl;

import io.spaceurgent.currency.rate.api.dao.CurrencyRateDao;
import io.spaceurgent.currency.rate.api.model.CryptoRate;
import io.spaceurgent.currency.rate.api.service.AbstractCurrencyRateService;
import org.springframework.stereotype.Service;

@Service
public class CryptoRateService extends AbstractCurrencyRateService<CryptoRate> {

    public CryptoRateService(CurrencyRateDao<CryptoRate> currencyRateDao) {
        super(currencyRateDao);
    }
}
