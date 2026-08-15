package main

import (
	"database/sql"
	"log"
	"os"

	_ "github.com/lib/pq"
)

func connectDB() *sql.DB {
	dsn := os.Getenv("DATABASE_URL")
	if dsn == "" {
		dsn = "postgres://movetrack:localdevpassword@localhost:5432/shipment_ingestion?sslmode=disable"
	}

	db, err := sql.Open("postgres", dsn)
	if err != nil {
		log.Fatal(err)
	}
	if err := db.Ping(); err != nil {
		log.Fatal(err)
	}
	return db
}

func ensureSchema(db *sql.DB) {
	_, err := db.Exec(`
		CREATE TABLE IF NOT EXISTS shipment_pings (
			id SERIAL PRIMARY KEY,
			device_id TEXT NOT NULL,
			latitude DOUBLE PRECISION NOT NULL,
			longitude DOUBLE PRECISION NOT NULL,
			recorded_at TIMESTAMPTZ NOT NULL,
			created_at TIMESTAMPTZ NOT NULL DEFAULT now()
		)
	`)
	if err != nil {
		log.Fatal(err)
	}
}

func insertPing(db *sql.DB, p ShipmentPing) error {
	_, err := db.Exec(
		`INSERT INTO shipment_pings (device_id, latitude, longitude, recorded_at) VALUES ($1, $2, $3, $4)`,
		p.DeviceID, p.Latitude, p.Longitude, p.Timestamp,
	)
	return err
}

func listPings(db *sql.DB) ([]ShipmentPing, error) {
	rows, err := db.Query(`SELECT device_id, latitude, longitude, recorded_at::text FROM shipment_pings ORDER BY id DESC`)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var result []ShipmentPing
	for rows.Next() {
		var p ShipmentPing
		if err := rows.Scan(&p.DeviceID, &p.Latitude, &p.Longitude, &p.Timestamp); err != nil {
			return nil, err
		}
		result = append(result, p)
	}
	return result, rows.Err()
}