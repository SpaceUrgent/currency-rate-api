package io.spaceurgent.currency.rate.api.client;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CryptoCurrencyRateInfo {
    private String name;
    private BigDecimal value;
}
