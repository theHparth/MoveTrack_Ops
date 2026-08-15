package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"sync"
)

type ShipmentPing struct {
	DeviceID  string  `json:"device_id"`
	Latitude  float64 `json:"latitude"`
	Longitude float64 `json:"longitude"`
	Timestamp string  `json:"timestamp"`
}

var (
	pings   []ShipmentPing
	pingsMu sync.Mutex
)

func handleIngest(w http.ResponseWriter, r *http.Request) {
	var p ShipmentPing
	if err := json.NewDecoder(r.Body).Decode(&p); err != nil {
		http.Error(w, "invalid JSON", http.StatusBadRequest)
		return
	}

	pingsMu.Lock()
	pings = append(pings, p)
	pingsMu.Unlock()

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(p)
}

func main() {
	mux := http.NewServeMux()
	mux.HandleFunc("GET /health", func(w http.ResponseWriter, r *http.Request) {
		fmt.Fprintln(w, `{"status":"ok"}`)
	})
	mux.HandleFunc("POST /ingest", handleIngest)

	log.Println("shipment-ingestion listening on :8090")
	if err := http.ListenAndServe(":8090", mux); err != nil {
		log.Fatal(err)
	}
}