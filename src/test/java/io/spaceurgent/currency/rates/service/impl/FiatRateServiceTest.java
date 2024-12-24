package io.spaceurgent.currency.rates.service.impl;

import io.spaceurgent.currency.rates.client.CurrencyRateApiClient;
import io.spaceurgent.currency.rates.client.dto.FiatRateInfo;
import io.spaceurgent.currency.rates.dao.CurrencyRateDao;
import io.spaceurgent.currency.rates.model.FiatRate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FiatRateServiceTest {
    private static final String USD_CURRENCY_NAME = "USD";
    private static final BigDecimal USD_CURRENCY_RATE = BigDecimal.valueOf(43.89);

    @Mock
    private CurrencyRateDao<FiatRate> fiatRateDao;
    @Mock
    private CurrencyRateApiClient currencyRateApiClient;
    @InjectMocks
    private FiatRateService fiatRateService;

    @Test
    void getLatestRates_shouldSaveAndReturnNew_whenApiReturnsRateInfo() {
        final var usdRateInfo = new FiatRateInfo(USD_CURRENCY_NAME, USD_CURRENCY_RATE);
        doReturn(Flux.just(usdRateInfo)).when(currencyRateApiClient).fetchFiatCurrencyRates();
        doReturn(Flux.empty()).when(fiatRateDao).findLastUniqueCurrencyRates();
        doAnswer(invoc -> Mono.just(invoc.getArgument(0))).when(fiatRateDao).save(any());
        final var fiatRate = fiatRateService.getLatestRates().blockFirst();
        assertNotNull(fiatRate);
        Assertions.assertEquals(usdRateInfo.currency(), fiatRate.getCurrency());
        Assertions.assertEquals(usdRateInfo.rate(), fiatRate.getRate());
    }

    @Test
    void getLatestRates_shouldReturnExisting_whenApiReturnsEmpty() {
        final var usdRate = FiatRate.builder()
                .currency(USD_CURRENCY_NAME)
                .rate(USD_CURRENCY_RATE)
                .build();
        doReturn(Flux.empty()).when(currencyRateApiClient).fetchFiatCurrencyRates();
        doReturn(Flux.just(usdRate)).when(fiatRateDao).findLastUniqueCurrencyRates();
        doAnswer(invoc -> Mono.just(invoc.getArgument(0))).when(fiatRateDao).save(any());
        Assertions.assertEquals(usdRate, fiatRateService.getLatestRates().blockFirst());
    }
}