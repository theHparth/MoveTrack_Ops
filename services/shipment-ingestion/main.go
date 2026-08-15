package main

import (
	"database/sql"
	"encoding/json"
	"errors"
	"fmt"
	"log"
	"net/http"
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
}



var db *sql.DB

func handleIngest(w http.ResponseWriter, r *http.Request) {
	var p ShipmentPing
	if err := json.NewDecoder(r.Body).Decode(&p); err != nil {
		http.Error(w, "invalid JSON", http.StatusBadRequest)
		return
	}

	if err := validatePing(p); err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	if err := insertPing(db, p); err != nil {
		http.Error(w, "failed to save ping", http.StatusInternalServerError)
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

func main() {
	db = connectDB()
	defer db.Close()
	ensureSchema(db)

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