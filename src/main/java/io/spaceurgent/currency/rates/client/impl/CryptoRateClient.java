package io.spaceurgent.currency.rates.client.impl;

import io.spaceurgent.currency.rates.client.AbstractCurrencyRateClient;
import io.spaceurgent.currency.rates.client.dto.CryptoRateInfo;
import io.spaceurgent.currency.rates.model.CryptoRate;
import io.spaceurgent.currency.rates.service.ToModelMapping;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class CryptoRateClient extends AbstractCurrencyRateClient<CryptoRate> {

    public CryptoRateClient(WebClient webClient) {
        super(webClient);
    }

    @Override
    public Flux<CryptoRate> fetchRates() {
        return doGetAndMap(
                "/crypto-currency-rates",
                CryptoRateInfo.class,
                ToModelMapping::toCryptoRate
        );
    }
}
