package main

import (
	"encoding/json"
	"math/rand"
	"net/http"
)

type FiatRate struct {
	Currency string  `json:"currency"`
	Rate     float64 `json:"rate"`
}

type CryptoRate struct {
	Name  string  `json:"name"`
	Value float64 `json:"value"`
}

type ErrorResponse struct {
	Error string `json:"error"`
}

const errorRate = 0.2

func main() {
	http.HandleFunc("/fiat-currency-rates", func(w http.ResponseWriter, r *http.Request) {
		apiKey := r.Header.Get("x-api-key")
		if apiKey != "secret-key" {
			w.Header().Set("Content-Type", "application/json")
			w.WriteHeader(http.StatusUnauthorized)
			json.NewEncoder(w).Encode(ErrorResponse{Error: "Invalid API key"})
			return
		}

		if rand.Float64() < errorRate {
			errorResponse(w)
			return
		}

		rates := []FiatRate{
			{"USD", rand.Float64() * 100},
			{"EUR", rand.Float64() * 100},
			{"GBP", rand.Float64() * 100},
		}

		w.Header().Set("Content-Type", "application/json")
		json.NewEncoder(w).Encode(rates)
	})

	http.HandleFunc("/crypto-currency-rates", func(w http.ResponseWriter, r *http.Request) {
		if rand.Float64() < errorRate {
			errorResponse(w)
			return
		}

		rates := []CryptoRate{
			{"BTC", rand.Float64() * 100000},
			{"ETH", rand.Float64() * 100000},
			{"LTC", rand.Float64() * 100000},
		}

		w.Header().Set("Content-Type", "application/json")
		json.NewEncoder(w).Encode(rates)
	})

	http.ListenAndServe(":8080", nil)
}

func errorResponse(w http.ResponseWriter) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusInternalServerError)
	json.NewEncoder(w).Encode(ErrorResponse{Error: "Internal Server Error"})
}
