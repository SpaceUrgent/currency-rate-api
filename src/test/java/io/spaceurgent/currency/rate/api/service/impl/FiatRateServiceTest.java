package io.spaceurgent.currency.rate.api.service.impl;

import io.spaceurgent.currency.rate.api.dao.CurrencyRateDao;
import io.spaceurgent.currency.rate.api.model.FiatRate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class FiatRateServiceTest {

    @Mock
    private CurrencyRateDao<FiatRate> fiatRateDao;
    @InjectMocks
    private FiatRateService fiatRateService;

    @Test
    void save_shouldReturnSaved() {
        final var givenRate = testFiatRate();
        doAnswer(invoc -> Mono.just(invoc.getArgument(0))).when(fiatRateDao).save(any());
        assertEquals(givenRate, fiatRateService.save(givenRate).block());
    }

    @Test
    void getLatestRates_shouldReturnFlux() {
        final var expectedRate = testFiatRate();
        doReturn(Flux.just(expectedRate)).when(fiatRateDao).findLastUniqueCurrencyRates();
        assertEquals(expectedRate, fiatRateService.getLatestRates().next().block());
    }

    private static FiatRate testFiatRate() {
        return FiatRate.builder()
                .currency("USD")
                .rate(BigDecimal.valueOf(43.23))
                .build();
    }
}