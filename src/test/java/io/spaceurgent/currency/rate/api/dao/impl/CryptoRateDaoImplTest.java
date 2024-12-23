package io.spaceurgent.currency.rate.api.dao.impl;

import io.r2dbc.spi.ConnectionFactory;
import io.spaceurgent.currency.rate.api.dao.CurrencyRateDao;
import io.spaceurgent.currency.rate.api.dao.DaoIntegrationTest;
import io.spaceurgent.currency.rate.api.dao.DaoTestUtils;
import io.spaceurgent.currency.rate.api.model.CryptoRate;
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
class CryptoRateDaoImplTest {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private CurrencyRateDao<CryptoRate> cryptoRateDao;

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
        final var btcRate = CryptoRate.builder()
                .name("BTC")
                .value(BigDecimal.valueOf(12345.67))
                .build();
        final var ethRate = CryptoRate.builder()
                .name("ETH")
                .value(BigDecimal.valueOf(234.56))
                .build();
        final var givenFiatRates = Flux.just(btcRate, ethRate);
        final var saved = cryptoRateDao.saveAll(givenFiatRates)
                .sort(Comparator.comparing(CryptoRate::getName))
                .collectList()
                .block();
        assertNotNull(saved);
        assertEquals(2, saved.size());
        assertSavedMatchesGiven(btcRate, saved.get(0));
        assertSavedMatchesGiven(ethRate, saved.get(1));
    }

    @Test
    void groupAllByLatestInsertTime_shouldReturnUniqueLatestRates() {
        DaoTestUtils.insertTestData("sql/insert_crypto_rates.sql", connectionFactory);
        final var expectedRates = cryptoRateDao.findAll()
                .sort(Comparator.comparing(CryptoRate::getInsertedTime).reversed())
                .distinct(CryptoRate::getName)
                .toStream()
                .collect(Collectors.toSet());
        assertFalse(expectedRates.isEmpty(), "Invalid test setup");

        final var latestCurrencyRates = cryptoRateDao.findLastUniqueCurrencyRates()
                .toStream()
                .collect(Collectors.toSet());
        assertEquals(expectedRates, latestCurrencyRates);
    }

    private void assertSavedMatchesGiven(CryptoRate given, CryptoRate saved) {
        assertNotNull(saved.getId());
        assertNotNull(saved.getInsertedTime());
        assertEquals(given.getName(), saved.getName());
        assertEquals(given.getValue(), saved.getValue());
    }
}