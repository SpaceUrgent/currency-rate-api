package io.spaceurgent.currency.rate.api.client;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import static io.spaceurgent.currency.rate.api.client.CurrencyRateApiConstants.API_KEY_HEADER_NAME;

@Configuration
@EnableConfigurationProperties(CurrencyRateApiConfigurationProperties.class)
public class CurrencyRateApiConfiguration {

    @Bean
    public CurrencyRateApiClient currencyRateApiClient(final CurrencyRateApiConfigurationProperties properties) {
        final var webClient = WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(API_KEY_HEADER_NAME, properties.getSecretKey())
                .build();
        return new CurrencyRateApiClientImpl(webClient);
    }
}
