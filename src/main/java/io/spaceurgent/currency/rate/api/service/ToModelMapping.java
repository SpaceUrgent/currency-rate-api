package io.spaceurgent.currency.rate.api.service;

import io.spaceurgent.currency.rate.api.client.dto.CryptoCurrencyRateInfo;
import io.spaceurgent.currency.rate.api.client.dto.FiatCurrencyRateInfo;
import io.spaceurgent.currency.rate.api.model.CryptoRate;
import io.spaceurgent.currency.rate.api.model.FiatRate;

public final class ToModelMapping {
    private ToModelMapping() {
    }

    public static CryptoRate toCryptoRate(CryptoCurrencyRateInfo rateInfo) {
        return CryptoRate.builder()
                .name(rateInfo.name())
                .value(rateInfo.value())
                .build();
    }

    public static FiatRate toFiatRate(FiatCurrencyRateInfo rateInfo) {
        return FiatRate.builder()
                .currency(rateInfo.currency())
                .rate(rateInfo.rate())
                .build();
    }
}
