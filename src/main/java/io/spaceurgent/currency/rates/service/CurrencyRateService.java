package io.spaceurgent.currency.rates.service;

import io.spaceurgent.currency.rates.model.CurrencyRate;
import reactor.core.publisher.Flux;

public interface CurrencyRateService<T extends CurrencyRate> {
    Flux<T> getLatestRates();
}
