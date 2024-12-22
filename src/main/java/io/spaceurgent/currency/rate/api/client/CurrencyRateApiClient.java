package io.spaceurgent.currency.rate.api.client;

import reactor.core.publisher.Flux;

public interface CurrencyRateApiClient {
    Flux<FiatCurrencyRateInfo> fetchFiatCurrencyRates();

    Flux<CryptoCurrencyRateInfo> fetchCryptoCurrencyRates();
}
