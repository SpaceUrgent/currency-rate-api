package io.spaceurgent.currency.rates.service.impl;

import io.spaceurgent.currency.rates.client.CurrencyRateApiClient;
import io.spaceurgent.currency.rates.client.dto.CryptoRateInfo;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CryptoRateServiceTest {
    private static final String BTC_CURRENCY_NAME = "BTC";
    private static final BigDecimal BTC_CURRENCY_VALUE = BigDecimal.valueOf(12345.67);

    @Mock
    private CurrencyRateDao<CryptoRate> cryptoRateDao;
    @Mock
    private CurrencyRateApiClient currencyRateApiClient;
    @InjectMocks
    private CryptoRateService cryptoRateService;

    @Test
    void getLatestRates_shouldSaveAndReturnNew_whenApiReturnsRateInfo() {
        final var btcRateInfo = new CryptoRateInfo(BTC_CURRENCY_NAME, BTC_CURRENCY_VALUE);
        doReturn(Flux.just(btcRateInfo)).when(currencyRateApiClient).fetchCryptoCurrencyRates();
        doReturn(Flux.empty()).when(cryptoRateDao).findLastUniqueCurrencyRates();
        doAnswer(invoc -> Mono.just(invoc.getArgument(0))).when(cryptoRateDao).save(any());
        final var cryptoRate = cryptoRateService.getLatestRates().blockFirst();
        assertNotNull(cryptoRate);
        assertEquals(btcRateInfo.name(), cryptoRate.getName());
        assertEquals(btcRateInfo.value(), cryptoRate.getValue());
    }

    @Test
    void getLatestRates_shouldReturnExisting_whenApiReturnsEmpty() {
        final var btcRate = CryptoRate.builder()
                .name(BTC_CURRENCY_NAME)
                .value(BTC_CURRENCY_VALUE)
                .build();
        doReturn(Flux.empty()).when(currencyRateApiClient).fetchCryptoCurrencyRates();
        doReturn(Flux.just(btcRate)).when(cryptoRateDao).findLastUniqueCurrencyRates();
        doAnswer(invoc -> Mono.just(invoc.getArgument(0))).when(cryptoRateDao).save(any());
        assertEquals(btcRate, cryptoRateService.getLatestRates().blockFirst());
    }
}