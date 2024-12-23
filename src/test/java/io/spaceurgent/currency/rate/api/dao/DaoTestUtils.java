package io.spaceurgent.currency.rate.api.dao;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

public final class DaoTestUtils {
    private DaoTestUtils() {
    }

    public static void insertTestData(String scriptResourcePath, ConnectionFactory connectionFactory) {
        final var populator = new ResourceDatabasePopulator();
        populator.addScripts(new ClassPathResource(scriptResourcePath));
        populator.populate(connectionFactory).block();
    }

    public static String convertJdbcToR2dbcUrl(String jdbcUrl) {
        return jdbcUrl.replace("jdbc", "r2dbc");
    }
}
