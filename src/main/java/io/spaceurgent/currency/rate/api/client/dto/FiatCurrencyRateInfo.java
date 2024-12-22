package io.spaceurgent.currency.rate.api.client.dto;

import java.math.BigDecimal;

public record FiatCurrencyRateInfo(
        String currency,
        BigDecimal rate
) {}
