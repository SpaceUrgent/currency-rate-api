package io.spaceurgent.currency.rates.service.impl;

import io.spaceurgent.currency.rates.client.CurrencyRateClient;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FiatRateServiceTest {
    private static final FiatRate DEFAULT_FIAT_RATE = FiatRate.builder()
            .currency("USD")
            .rate(BigDecimal.valueOf(43.89))
            .build();

    @Mock
    private CurrencyRateDao<FiatRate> fiatRateDao;
    @Mock
    private CurrencyRateClient<FiatRate> fiatRateClient;
    @InjectMocks
    private FiatRateService fiatRateService;

    @Test
    void getLatestRates_shouldSaveAndReturnNew_whenApiReturnsRateInfo() {
        doReturn(Flux.just(DEFAULT_FIAT_RATE)).when(fiatRateClient).fetchRates();
        doReturn(Flux.empty()).when(fiatRateDao).findLastUniqueCurrencyRates();
        doAnswer(invoc -> Mono.just(invoc.getArgument(0))).when(fiatRateDao).save(any());
        assertEquals(DEFAULT_FIAT_RATE, fiatRateService.getLatestRates().blockFirst());
    }

    @Test
    void getLatestRates_shouldReturnExisting_whenApiReturnsEmpty() {
        doReturn(Flux.empty()).when(fiatRateClient).fetchRates();
        doReturn(Flux.just(DEFAULT_FIAT_RATE)).when(fiatRateDao).findLastUniqueCurrencyRates();
        doAnswer(invoc -> Mono.just(invoc.getArgument(0))).when(fiatRateDao).save(any());
        Assertions.assertEquals(DEFAULT_FIAT_RATE, fiatRateService.getLatestRates().blockFirst());
    }
}