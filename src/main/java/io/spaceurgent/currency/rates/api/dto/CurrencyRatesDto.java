package io.spaceurgent.currency.rates.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyRatesDto {
    private List<CurrencyRateDto> fiat = new ArrayList<>();
    private List<CurrencyRateDto> crypto = new ArrayList<>();
}
