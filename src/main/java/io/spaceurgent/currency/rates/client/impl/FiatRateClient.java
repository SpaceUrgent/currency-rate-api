package io.spaceurgent.currency.rates.client.impl;

import io.spaceurgent.currency.rates.client.AbstractCurrencyRateClient;
import io.spaceurgent.currency.rates.client.dto.FiatRateInfo;
import io.spaceurgent.currency.rates.model.FiatRate;
import io.spaceurgent.currency.rates.service.ToModelMapping;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class FiatRateClient extends AbstractCurrencyRateClient<FiatRate> {

    public FiatRateClient(WebClient webClient) {
        super(webClient);
    }

    @Override
    public Flux<FiatRate> fetchRates() {
        return doGetAndMap(
                "/fiat-currency-rates",
                FiatRateInfo.class,
                ToModelMapping::toFiatRate
        );
    }
}
