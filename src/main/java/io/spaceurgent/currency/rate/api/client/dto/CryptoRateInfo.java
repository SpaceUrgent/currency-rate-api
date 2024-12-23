package io.spaceurgent.currency.rate.api.client.dto;

import java.math.BigDecimal;

public record CryptoRateInfo(
        String name,
        BigDecimal value
) {}
