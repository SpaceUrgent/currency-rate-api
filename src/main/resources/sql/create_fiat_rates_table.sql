CREATE TABLE IF NOT EXISTS  public.fiat_rate (
    id BIGSERIAL PRIMARY KEY,
    inserted_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    currency VARCHAR NOT NULL,
    rate NUMERIC NOT NULL
);

CREATE INDEX IF NOT EXISTS fiat_rate_inserted_time_index ON public.fiat_rate (inserted_time);