package main

import (
	"database/sql"
	"encoding/json"
	"errors"
	"fmt"
	"log"
	"net/http"
	"os"
)



func validatePing(p ShipmentPing) error {
	if p.DeviceID == "" {
		return errors.New("device_id is required")
	}
	return nil
}

type ShipmentPing struct {
	DeviceID  string  `json:"device_id"`
	Latitude  float64 `json:"latitude"`
	Longitude float64 `json:"longitude"`
	Timestamp string  `json:"timestamp"`
	// RequestID is optional — REST callers can omit it. UDP retries reuse
	// the same RequestID across attempts so the server can dedup, since a
	// dropped ack (not a dropped ping) is what triggers a client retry.
	RequestID string `json:"request_id,omitempty"`
}



var db *sql.DB

func handleIngest(w http.ResponseWriter, r *http.Request) {
	var p ShipmentPing
	if err := json.NewDecoder(r.Body).Decode(&p); err != nil {
		http.Error(w, "invalid JSON", http.StatusBadRequest)
		return
	}

	if err := handlePing(p); err != nil {
		var verr *ValidationError
		if errors.As(err, &verr) {
			http.Error(w, verr.Error(), http.StatusBadRequest)
		} else {
			http.Error(w, "failed to save ping", http.StatusInternalServerError)
		}
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(p)
}

func handleListIngest(w http.ResponseWriter, r *http.Request) {
	result, err := listPings(db)
	if err != nil {
		http.Error(w, "failed to list pings", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(result)
}

func envOrDefault(key, fallback string) string {
	if v := os.Getenv(key); v != "" {
		return v
	}
	return fallback
}

func main() {
	db = connectDB()
	defer db.Close()
	ensureSchema(db)

	if envOrDefault("UDP_LISTENER_ENABLED", "true") == "true" {
		go startUDPListener(envOrDefault("UDP_PORT", "8091"))
	}
	if envOrDefault("TCP_LISTENER_ENABLED", "true") == "true" {
		go startTCPListener(envOrDefault("TCP_PORT", "8092"))
	}

	mux := http.NewServeMux()
	mux.HandleFunc("GET /health", func(w http.ResponseWriter, r *http.Request) {
		fmt.Fprintln(w, `{"status":"ok"}`)
	})
	mux.HandleFunc("POST /ingest", handleIngest)
	mux.HandleFunc("GET /ingest", handleListIngest)

	log.Println("shipment-ingestion listening on :8090")
	if err := http.ListenAndServe(":8090", mux); err != nil {
		log.Fatal(err)
	}
}