package io.spaceurgent.currency.rates.client;

import io.spaceurgent.currency.rates.client.dto.CryptoRateInfo;
import io.spaceurgent.currency.rates.client.dto.FiatRateInfo;
import reactor.core.publisher.Flux;

public interface CurrencyRateApiClient {
    Flux<FiatRateInfo> fetchFiatCurrencyRates();

    Flux<CryptoRateInfo> fetchCryptoCurrencyRates();
}
