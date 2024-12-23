package io.spaceurgent.currency.rate.api.client.dto;

import java.math.BigDecimal;

public record FiatRateInfo(
        String currency,
        BigDecimal rate
) {}
