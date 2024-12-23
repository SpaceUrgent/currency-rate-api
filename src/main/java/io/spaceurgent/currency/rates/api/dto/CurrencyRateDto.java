package io.spaceurgent.currency.rates.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CurrencyRateDto {
    private String currency;
    private BigDecimal rate;
}
