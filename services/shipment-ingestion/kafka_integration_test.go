package main

import (
	"context"
	"fmt"
	"net"
	"testing"
	"time"

	"github.com/segmentio/kafka-go"
)

func TestKafkaProduceConsumeRoundTrip(t *testing.T) {
	conn, err := net.DialTimeout("tcp", "localhost:9092", 2*time.Second)
	if err != nil {
		t.Skip("kafka not reachable on localhost:9092, skipping integration test")
	}
	conn.Close()

	uniqueKey := fmt.Sprintf("TEST-%d", time.Now().UnixNano())
	testValue := []byte(fmt.Sprintf(`{"device_id":%q}`, uniqueKey))

	writer := &kafka.Writer{
		Addr:  kafka.TCP("localhost:9092"),
		Topic: "shipment.tracking",
	}
	defer writer.Close()

	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	if err := writer.WriteMessages(ctx, kafka.Message{
		Key:   []byte(uniqueKey),
		Value: testValue,
	}); err != nil {
		t.Fatalf("failed to produce test message: %v", err)
	}

	reader := kafka.NewReader(kafka.ReaderConfig{
		Brokers:     []string{"localhost:9092"},
		Topic:       "shipment.tracking",
		GroupID:     fmt.Sprintf("integration-test-%d", time.Now().UnixNano()),
		StartOffset: kafka.FirstOffset,
	})
	defer reader.Close()

	for {
		msg, err := reader.ReadMessage(ctx)
		if err != nil {
			t.Fatalf("failed to find test message before context timeout: %v", err)
		}
		if string(msg.Key) == uniqueKey {
			if string(msg.Value) != string(testValue) {
				t.Errorf("expected value %q, got %q", testValue, msg.Value)
			}
			return
		}
	}
}