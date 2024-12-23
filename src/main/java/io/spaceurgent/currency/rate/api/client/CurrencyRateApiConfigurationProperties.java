package io.spaceurgent.currency.rate.api.client;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "currency-rate-api")
public class CurrencyRateApiConfigurationProperties {
    @NotNull
    private String baseUrl;
    @NotNull
    private String secretKey;
}
