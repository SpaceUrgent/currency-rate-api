package io.spaceurgent.currency.rates.api;

import io.spaceurgent.currency.rates.api.dto.CurrencyRateDto;
import io.spaceurgent.currency.rates.model.CryptoRate;
import io.spaceurgent.currency.rates.model.FiatRate;

public final class ToDtoMapping {
    private ToDtoMapping() {
    }

    public static CurrencyRateDto toDto(CryptoRate cryptoRate) {
        return new CurrencyRateDto(cryptoRate.getName(), cryptoRate.getValue());
    }

    public static CurrencyRateDto toDto(FiatRate fiatRate) {
        return new CurrencyRateDto(fiatRate.getCurrency(), fiatRate.getRate());
    }
}
