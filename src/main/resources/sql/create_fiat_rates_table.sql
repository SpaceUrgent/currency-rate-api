CREATE TABLE public.fiat_rate (
    id BIGSERIAL PRIMARY KEY,
    inserted_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    currency VARCHAR NOT NULL,
    rate NUMERIC NOT NULL
);

CREATE INDEX fiat_rate_inserted_time_index ON public.fiat_rate (inserted_time);