package io.spaceurgent.currency.rates.service;

import io.spaceurgent.currency.rates.client.dto.CryptoRateInfo;
import io.spaceurgent.currency.rates.client.dto.FiatRateInfo;
import io.spaceurgent.currency.rates.model.CryptoRate;
import io.spaceurgent.currency.rates.model.FiatRate;

public final class ToModelMapping {
    private ToModelMapping() {
    }

    public static CryptoRate toCryptoRate(CryptoRateInfo rateInfo) {
        return CryptoRate.builder()
                .name(rateInfo.name())
                .value(rateInfo.value())
                .build();
    }

    public static FiatRate toFiatRate(FiatRateInfo rateInfo) {
        return FiatRate.builder()
                .currency(rateInfo.currency())
                .rate(rateInfo.rate())
                .build();
    }
}
