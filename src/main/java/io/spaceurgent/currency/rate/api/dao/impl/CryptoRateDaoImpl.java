package io.spaceurgent.currency.rate.api.dao.impl;

import io.r2dbc.spi.Readable;
import io.spaceurgent.currency.rate.api.dao.CurrencyRateDao;
import io.spaceurgent.currency.rate.api.model.CryptoRate;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.Function;

@Repository
@RequiredArgsConstructor
public class CryptoRateDaoImpl implements CurrencyRateDao<CryptoRate> {
    private final DatabaseClient databaseClient;

    @Override
    public Mono<CryptoRate> save(CryptoRate cryptoRate) {
        String sql = """
                INSERT INTO crypto_rate (name, value)\s
                VALUES (:name, :value)\s
                RETURNING *;
                """;
        return databaseClient.sql(sql)
                .bind("name", cryptoRate.getName())
                .bind("value", cryptoRate.getValue())
                .map(rowToModelFunction())
                .one();
    }

    @Override
    public Flux<CryptoRate> findAll() {
        String sql = """
                SELECT rate.*\s
                FROM crypto_rate rate;
                """;

        return databaseClient.sql(sql)
                .map(rowToModelFunction())
                .all();
    }

    @Override
    public Flux<CryptoRate> findLastUniqueCurrencyRates() {
        String sql = """
                SELECT DISTINCT ON (rate.name) rate.*\s
                FROM crypto_rate rate\s
                ORDER BY rate.name, rate.inserted_time DESC\s
                """;

        return databaseClient.sql(sql)
                .map(rowToModelFunction())
                .all();
    }

    private Function<Readable, CryptoRate> rowToModelFunction() {
        return row -> CryptoRate.builder()
                .id(row.get("id", Long.class))
                .insertedTime(row.get("inserted_time", Instant.class))
                .name(row.get("name", String.class))
                .value(row.get("value", BigDecimal.class))
                .build();
    }
}
