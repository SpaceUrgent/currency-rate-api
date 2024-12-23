package io.spaceurgent.currency.rates.client.dto;

import java.math.BigDecimal;

public record FiatRateInfo(
        String currency,
        BigDecimal rate
) {}
