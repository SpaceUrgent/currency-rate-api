package io.spaceurgent.currency.rate.api.service.impl;

import io.spaceurgent.currency.rate.api.dao.CurrencyRateDao;
import io.spaceurgent.currency.rate.api.model.FiatRate;
import io.spaceurgent.currency.rate.api.service.AbstractCurrencyRateService;
import org.springframework.stereotype.Service;

@Service
public class FiatRateService extends AbstractCurrencyRateService<FiatRate> {

    public FiatRateService(CurrencyRateDao<FiatRate> currencyRateDao) {
        super(currencyRateDao);
    }
}
