package main

import (
	"context"

	"github.com/segmentio/kafka-go"
)

var kafkaWriter = &kafka.Writer{
	Addr:     kafka.TCP("kafka:9092"),
	Topic:    "shipment.tracking",
	Balancer: &kafka.Hash{},
}

func publishPing(pingJSON []byte, shipmentID string) error {
	return kafkaWriter.WriteMessages(context.Background(), kafka.Message{
		Key:   []byte(shipmentID),
		Value: pingJSON,
	})
}