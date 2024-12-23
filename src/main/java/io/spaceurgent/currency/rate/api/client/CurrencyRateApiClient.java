package io.spaceurgent.currency.rate.api.client;

import io.spaceurgent.currency.rate.api.client.dto.CryptoRateInfo;
import io.spaceurgent.currency.rate.api.client.dto.FiatRateInfo;
import reactor.core.publisher.Flux;

public interface CurrencyRateApiClient {
    Flux<FiatRateInfo> fetchFiatCurrencyRates();

    Flux<CryptoRateInfo> fetchCryptoCurrencyRates();
}
