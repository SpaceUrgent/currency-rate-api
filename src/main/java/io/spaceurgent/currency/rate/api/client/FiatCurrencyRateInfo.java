package io.spaceurgent.currency.rate.api.client;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FiatCurrencyRateInfo {
    private String currency;
    private BigDecimal rate;
}
