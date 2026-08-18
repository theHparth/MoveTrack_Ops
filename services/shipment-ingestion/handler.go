package main

import (
	"encoding/json"
	"fmt"
	"log"
)

// ValidationError marks a failure that should map to HTTP 400 in the REST
// handler — anything else returned from handlePing is treated as a
// server-side failure (500).
type ValidationError struct {
	msg string
}

func (e *ValidationError) Error() string { return e.msg }

// parsePingPayload decodes a raw ping body (JSON) shared by all three
// transports (REST, UDP, TCP) into a ShipmentPing.
func parsePingPayload(raw []byte) (ShipmentPing, error) {
	var p ShipmentPing
	if err := json.Unmarshal(raw, &p); err != nil {
		return ShipmentPing{}, err
	}
	return p, nil
}

// handlePing runs the shared validate -> persist -> publish pipeline used by
// all three transports. Each transport is responsible for decoding its own
// raw bytes into a ShipmentPing (via parsePingPayload) before calling this.
func handlePing(p ShipmentPing) error {
	if err := validatePing(p); err != nil {
		return &ValidationError{err.Error()}
	}

	if err := insertPing(db, p); err != nil {
		return fmt.Errorf("insert failed: %w", err)
	}

	pingJSON, err := json.Marshal(p)
	if err != nil {
		log.Printf("failed to marshal ping for kafka: %v", err)
		return nil
	}

	// Fired in the background so a slow or unreachable Kafka broker never
	// delays the caller's response/ack — matches the documented intent
	// (see tech/rabbitmq-mongodb.html: "the announcement doesn't block the
	// response") which the original synchronous call didn't actually honor.
	go func() {
		if err := publishPing(pingJSON, p.DeviceID); err != nil {
			log.Printf("failed to publish ping to kafka: %v", err)
		}
	}()

	return nil
}
