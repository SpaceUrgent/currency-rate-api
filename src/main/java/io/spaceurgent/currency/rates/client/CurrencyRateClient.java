package io.spaceurgent.currency.rates.client;

import io.spaceurgent.currency.rates.model.CurrencyRate;
import reactor.core.publisher.Flux;

public interface CurrencyRateClient<T extends CurrencyRate> {
    Flux<T> fetchRates();
}
