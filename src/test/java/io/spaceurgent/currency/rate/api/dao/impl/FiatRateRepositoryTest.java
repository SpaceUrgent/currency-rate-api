package io.spaceurgent.currency.rate.api.dao.impl;

import io.r2dbc.spi.ConnectionFactory;
import io.spaceurgent.currency.rate.api.dao.CurrencyRateDao;
import io.spaceurgent.currency.rate.api.dao.DaoIntegrationTest;
import io.spaceurgent.currency.rate.api.dao.DaoTestUtils;
import io.spaceurgent.currency.rate.api.model.FiatRate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.stream.Collectors;

import static io.spaceurgent.currency.rate.api.dao.DaoTestUtils.convertJdbcToR2dbcUrl;
import static org.junit.jupiter.api.Assertions.*;

@DaoIntegrationTest
@Testcontainers
class FiatRateRepositoryTest {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private CurrencyRateDao<FiatRate> fiatRateDao;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> convertJdbcToR2dbcUrl(postgres.getJdbcUrl()));
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
    }

    @Test
    void saveAll_shouldReturnSaved() {
        final var usdRate = FiatRate.builder()
                .currency("USD")
                .rate(BigDecimal.valueOf(43.23))
                .build();
        final var eurRate = FiatRate.builder()
                .currency("EUR")
                .rate(BigDecimal.valueOf(53.28))
                .build();
        final var givenFiatRates = Flux.just(usdRate, eurRate);
        final var saved = fiatRateDao.saveAll(givenFiatRates)
                .sort(Comparator.comparing(FiatRate::getCurrency))
                .collectList()
                .block();
        assertNotNull(saved);
        assertEquals(2, saved.size());
        assertSavedMatchesGiven(eurRate, saved.get(0));
        assertSavedMatchesGiven(usdRate, saved.get(1));
    }

    @Test
    void groupAllByLatestInsertTime_shouldReturnUniqueLatestRates() {
        DaoTestUtils.insertTestData("sql/insert_fiat_rates.sql", connectionFactory);
        final var expectedRates = fiatRateDao.findAll()
                .sort(Comparator.comparing(FiatRate::getInsertedTime).reversed())
                .distinct(FiatRate::getCurrency)
                .toStream()
                .collect(Collectors.toSet());
        assertFalse(expectedRates.isEmpty(), "Invalid test setup");

        final var latestCurrencyRates = fiatRateDao.findLastUniqueCurrencyRates()
                .toStream()
                .collect(Collectors.toSet());
        assertEquals(expectedRates, latestCurrencyRates);
    }

    private void assertSavedMatchesGiven(FiatRate given, FiatRate saved) {
        assertNotNull(saved.getId());
        assertNotNull(saved.getInsertedTime());
        assertEquals(given.getCurrency(), saved.getCurrency());
        assertEquals(given.getRate(), saved.getRate());
    }
}