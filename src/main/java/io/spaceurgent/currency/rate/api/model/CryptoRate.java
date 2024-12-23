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
public class CryptoRate extends CurrencyRate {
    private String name;
    private BigDecimal value;

    @Builder
    public CryptoRate(Long id,
                      Instant insertedTime,
                      String name,
                      BigDecimal value) {
        super(id, insertedTime);
        this.name = name;
        this.value = value;
    }
}
