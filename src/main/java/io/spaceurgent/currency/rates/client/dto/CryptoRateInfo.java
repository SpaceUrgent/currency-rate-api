package io.spaceurgent.currency.rates.client.dto;

import java.math.BigDecimal;

public record CryptoRateInfo(
        String name,
        BigDecimal value
) {}
