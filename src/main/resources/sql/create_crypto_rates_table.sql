CREATE TABLE public.crypto_rate (
    id BIGSERIAL PRIMARY KEY,
    inserted_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    name VARCHAR NOT NULL,
    value NUMERIC NOT NULL
);

CREATE INDEX crypto_rate_inserted_time_index ON public.crypto_rate (inserted_time);