package io.spaceurgent.currency.rate.api.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FiatRate extends CurrencyRate {
    private String currency;
    private BigDecimal rate;

    @Builder
    public FiatRate(Long id,
                    Instant insertedTime,
                    String currency,
                    BigDecimal rate) {
        super(id, insertedTime);
        this.currency = currency;
        this.rate = rate;
    }
}
