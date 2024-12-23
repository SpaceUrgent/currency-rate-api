package io.spaceurgent.currency.rate.api.service.impl;

import io.spaceurgent.currency.rate.api.client.CurrencyRateApiClient;
import io.spaceurgent.currency.rate.api.client.dto.FiatCurrencyRateInfo;
import io.spaceurgent.currency.rate.api.dao.CurrencyRateDao;
import io.spaceurgent.currency.rate.api.model.FiatRate;
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
        final var usdRateInfo = new FiatCurrencyRateInfo(USD_CURRENCY_NAME, USD_CURRENCY_RATE);
        doReturn(Flux.just(usdRateInfo)).when(currencyRateApiClient).fetchFiatCurrencyRates();
        doReturn(Flux.empty()).when(fiatRateDao).findLastUniqueCurrencyRates();
        doAnswer(invoc -> Mono.just(invoc.getArgument(0))).when(fiatRateDao).save(any());
        final var fiatRate = fiatRateService.getLatestRates().blockFirst();
        assertNotNull(fiatRate);
        assertEquals(usdRateInfo.currency(), fiatRate.getCurrency());
        assertEquals(usdRateInfo.rate(), fiatRate.getRate());
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
        assertEquals(usdRate, fiatRateService.getLatestRates().blockFirst());
    }
}