package io.spaceurgent.currency.rate.api.client.dto;

import java.math.BigDecimal;

public record CryptoCurrencyRateInfo(
        String name,
        BigDecimal value
) {}
