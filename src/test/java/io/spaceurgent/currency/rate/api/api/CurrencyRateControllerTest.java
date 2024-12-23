package io.spaceurgent.currency.rate.api.api;

import io.spaceurgent.currency.rate.api.model.CryptoRate;
import io.spaceurgent.currency.rate.api.model.FiatRate;
import io.spaceurgent.currency.rate.api.service.CurrencyRateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class CurrencyRateControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CurrencyRateService<FiatRate> fiatRateService;

    @MockitoBean
    private CurrencyRateService<CryptoRate> cryptoRateService;


    @Test
    void getCurrencyRates_shouldReturnCurrencyRatesDto_whenServicesReturnData() throws Exception {
        final var fiatRate = FiatRate.builder()
                .currency("USD")
                .rate(BigDecimal.valueOf(1.23))
                .build();
        final var cryptoRate = CryptoRate.builder()
                .name("BTC")
                .value(BigDecimal.valueOf(12345.67))
                .build();
        doReturn(Flux.just(fiatRate)).when(fiatRateService).getLatestRates();
        doReturn(Flux.just(cryptoRate)).when(cryptoRateService).getLatestRates();

        mockMvc.perform(get("/currency-rates")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fiatRates[0].currency").value(fiatRate.getCurrency()))
                .andExpect(jsonPath("$.fiatRates[0].rate").value(fiatRate.getRate()))
                .andExpect(jsonPath("$.cryptoRates[0].currency").value(cryptoRate.getName()))
                .andExpect(jsonPath("$.cryptoRates[0].rate").value(cryptoRate.getValue()));
    }

    @Test
    void getCurrencyRates_shouldReturnEmptyDto_whenServicesThrowError() throws Exception {
        doReturn(Flux.error(new RuntimeException("Fiat service error"))).when(fiatRateService).getLatestRates();
        doReturn(Flux.error(new RuntimeException("Crypto service error"))).when(cryptoRateService).getLatestRates();
        mockMvc.perform(get("/currency-rates")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fiatRates").isEmpty())
                .andExpect(jsonPath("$.cryptoRates").isEmpty());
    }

}