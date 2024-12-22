package io.spaceurgent.currency.rate.api.client;

import io.spaceurgent.currency.rate.api.client.dto.CryptoCurrencyRateInfo;
import io.spaceurgent.currency.rate.api.client.dto.FiatCurrencyRateInfo;
import reactor.core.publisher.Flux;

public interface CurrencyRateApiClient {
    Flux<FiatCurrencyRateInfo> fetchFiatCurrencyRates();

    Flux<CryptoCurrencyRateInfo> fetchCryptoCurrencyRates();
}
