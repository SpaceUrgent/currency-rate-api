package io.spaceurgent.currency.rates.service.impl;

import io.spaceurgent.currency.rates.client.CurrencyRateClient;
import io.spaceurgent.currency.rates.dao.CurrencyRateDao;
import io.spaceurgent.currency.rates.model.CryptoRate;
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
class CryptoRateServiceTest {
    private static final CryptoRate DEFAULT_CRYPTO_RATE = CryptoRate.builder()
            .name("BTC")
            .value(BigDecimal.valueOf(12345.67))
            .build();

    @Mock
    private CurrencyRateDao<CryptoRate> cryptoRateDao;
    @Mock
    private CurrencyRateClient<CryptoRate> cryptoRateClient;
    @InjectMocks
    private CryptoRateService cryptoRateService;

    @Test
    void getLatestRates_shouldSaveAndReturnNew_whenApiReturnsRateInfo() {
        doReturn(Flux.just(DEFAULT_CRYPTO_RATE)).when(cryptoRateClient).fetchRates();
        doReturn(Flux.empty()).when(cryptoRateDao).findLastUniqueCurrencyRates();
        doAnswer(invoc -> Mono.just(invoc.getArgument(0))).when(cryptoRateDao).save(any());
        assertEquals(DEFAULT_CRYPTO_RATE, cryptoRateService.getLatestRates().blockFirst());
    }

    @Test
    void getLatestRates_shouldReturnExisting_whenApiReturnsEmpty() {
        doReturn(Flux.empty()).when(cryptoRateClient).fetchRates();
        doReturn(Flux.just(DEFAULT_CRYPTO_RATE)).when(cryptoRateDao).findLastUniqueCurrencyRates();
        doAnswer(invoc -> Mono.just(invoc.getArgument(0))).when(cryptoRateDao).save(any());
        assertEquals(DEFAULT_CRYPTO_RATE, cryptoRateService.getLatestRates().blockFirst());
    }
}