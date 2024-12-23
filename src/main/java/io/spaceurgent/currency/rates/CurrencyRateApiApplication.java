package io.spaceurgent.currency.rates;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@EnableR2dbcRepositories
@SpringBootApplication
public class CurrencyRateApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyRateApiApplication.class, args);
	}

}
