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

func buildKafkaMessage(pingJSON []byte, deviceID string) kafka.Message {
	return kafka.Message{
		Key:   []byte(deviceID),
		Value: pingJSON,
	}
}

func publishPing(pingJSON []byte, deviceID string) error {
	return kafkaWriter.WriteMessages(context.Background(), buildKafkaMessage(pingJSON, deviceID))
}