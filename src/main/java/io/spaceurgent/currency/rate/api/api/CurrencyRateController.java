package io.spaceurgent.currency.rate.api.api;

import io.spaceurgent.currency.rate.api.api.dto.CurrencyRatesDto;
import io.spaceurgent.currency.rate.api.model.CryptoRate;
import io.spaceurgent.currency.rate.api.model.FiatRate;
import io.spaceurgent.currency.rate.api.service.CurrencyRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/currency-rates")
@RequiredArgsConstructor
public class CurrencyRateController {
    private final CurrencyRateService<FiatRate> fiatRateService;
    private final CurrencyRateService<CryptoRate> cryptoRateService;

    @GetMapping
    public Mono<CurrencyRatesDto> getCurrencyRates() {
        return Mono.zip(
                fiatRateService.getLatestRates().map(ToDtoMapping::toDto).collectList(),
                cryptoRateService.getLatestRates().map(ToDtoMapping::toDto).collectList(),
                CurrencyRatesDto::new
        ).onErrorResume(throwable -> {
            log.error("Get rates error: {}", throwable.getMessage(), throwable);
            return Mono.just(new CurrencyRatesDto());
        });
    }
}
