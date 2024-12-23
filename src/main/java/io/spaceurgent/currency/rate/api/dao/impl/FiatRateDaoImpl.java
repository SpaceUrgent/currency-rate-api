package io.spaceurgent.currency.rate.api.dao.impl;

import io.r2dbc.spi.Readable;
import io.spaceurgent.currency.rate.api.dao.CurrencyRateDao;
import io.spaceurgent.currency.rate.api.model.FiatRate;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.Function;

@Repository
@RequiredArgsConstructor
public class FiatRateDaoImpl implements CurrencyRateDao<FiatRate> {
    private final DatabaseClient databaseClient;

    @Override
    public Flux<FiatRate> saveAll(Flux<FiatRate> rates) {
        String sql = """
                INSERT INTO fiat_rate (currency, rate)\s
                VALUES (:currency, :rate)\s
                RETURNING *;
                """;
        return rates.flatMap(rate -> databaseClient.sql(sql)
                .bind("currency", rate.getCurrency())
                .bind("rate", rate.getRate())
                .map(rowToModelFunction())
                .one());
    }

    public Flux<FiatRate> findAll() {
        String sql = """
                SELECT f.*\s
                FROM fiat_rate f;
                """;
        return databaseClient.sql(sql)
                .map(rowToModelFunction())
                .all();
    }

    @Override
    public Flux<FiatRate> findLastUniqueCurrencyRates() {
        String sql = """
                SELECT DISTINCT ON (f.currency) f.*\s
                FROM fiat_rate f\s
                ORDER BY f.currency, f.inserted_time DESC\s
                """;
        return databaseClient.sql(sql)
                .map(rowToModelFunction())
                .all();
    }

    private static Function<Readable, FiatRate> rowToModelFunction() {
        return row -> FiatRate.builder()
                .id(row.get("id", Long.class))
                .insertedTime(row.get("inserted_time", Instant.class))
                .currency(row.get("currency", String.class))
                .rate(row.get("rate", BigDecimal.class))
                .build();
    }
}