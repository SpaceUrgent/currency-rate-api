package io.spaceurgent.currency.rate.api.service;

import io.spaceurgent.currency.rate.api.client.dto.CryptoRateInfo;
import io.spaceurgent.currency.rate.api.client.dto.FiatRateInfo;
import io.spaceurgent.currency.rate.api.model.CryptoRate;
import io.spaceurgent.currency.rate.api.model.FiatRate;

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
