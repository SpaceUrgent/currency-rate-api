package io.spaceurgent.currency.rate.api.service.impl;

import io.spaceurgent.currency.rate.api.dao.CurrencyRateDao;
import io.spaceurgent.currency.rate.api.model.CryptoRate;
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
class CryptoRateServiceTest {

    @Mock
    private CurrencyRateDao<CryptoRate> cryptoRateDao;
    @InjectMocks
    private CryptoRateService cryptoRateService;

    @Test
    void save_shouldReturnSaved() {
        final var givenRate = testCryptoRate();
        doAnswer(invoc -> Mono.just(invoc.getArgument(0))).when(cryptoRateDao).save(any());
        assertEquals(givenRate, cryptoRateService.save(givenRate).block());
    }

    @Test
    void getLatestRates_shouldReturnFlux() {
        final var expectedRate = testCryptoRate();
        doReturn(Flux.just(expectedRate)).when(cryptoRateDao).findLastUniqueCurrencyRates();
        assertEquals(expectedRate, cryptoRateService.getLatestRates().next().block());
    }

    private static CryptoRate testCryptoRate() {
        return CryptoRate.builder()
                .name("BTC")
                .value(BigDecimal.valueOf(12345.23))
                .build();
    }
}