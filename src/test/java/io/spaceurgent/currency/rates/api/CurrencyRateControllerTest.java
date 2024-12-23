package io.spaceurgent.currency.rates.api;

import io.spaceurgent.currency.rates.model.CryptoRate;
import io.spaceurgent.currency.rates.model.FiatRate;
import io.spaceurgent.currency.rates.service.CurrencyRateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

import static org.mockito.Mockito.doReturn;

@WebFluxTest(CurrencyRateController.class)
class CurrencyRateControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CurrencyRateService<FiatRate> fiatRateService;

    @MockitoBean
    private CurrencyRateService<CryptoRate> cryptoRateService;

    @Test
    void getCurrencyRates_shouldReturnCurrencyRatesDto_whenServicesReturnData() {
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

        webTestClient.get()
                .uri("/currency-rates")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.fiat[0].currency").isEqualTo(fiatRate.getCurrency())
                .jsonPath("$.fiat[0].rate").isEqualTo(fiatRate.getRate())
                .jsonPath("$.crypto[0].currency").isEqualTo(cryptoRate.getName())
                .jsonPath("$.crypto[0].rate").isEqualTo(cryptoRate.getValue());
    }

    @Test
    void getCurrencyRates_shouldReturnEmptyDto_whenServicesThrowError() {
        doReturn(Flux.error(new RuntimeException("Fiat service error"))).when(fiatRateService).getLatestRates();
        doReturn(Flux.error(new RuntimeException("Crypto service error"))).when(cryptoRateService).getLatestRates();
        webTestClient.get()
                .uri("/currency-rates")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.fiat").isEmpty()
                .jsonPath("$.crypto").isEmpty();
    }

}