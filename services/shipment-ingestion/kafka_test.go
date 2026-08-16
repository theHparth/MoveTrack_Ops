package main

import "testing"

func TestBuildKafkaMessageSetsDeviceIDAsKey(t *testing.T) {
	tests := []struct {
		name     string
		deviceID string
		pingJSON []byte
	}{
		{name: "simple device id", deviceID: "DEVICE-1", pingJSON: []byte(`{"device_id":"DEVICE-1"}`)},
		{name: "different device id", deviceID: "DEVICE-2", pingJSON: []byte(`{"device_id":"DEVICE-2"}`)},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			msg := buildKafkaMessage(tt.pingJSON, tt.deviceID)

			if string(msg.Key) != tt.deviceID {
				t.Errorf("expected key %q, got %q", tt.deviceID, string(msg.Key))
			}
			if string(msg.Value) != string(tt.pingJSON) {
				t.Errorf("expected value %q, got %q", tt.pingJSON, string(msg.Value))
			}
		})
	}
}