package main

import (
	"errors"
	"testing"
)

func TestParsePingPayload(t *testing.T) {
	cases := []struct {
		name    string
		raw     string
		want    ShipmentPing
		wantErr bool
	}{
		{
			name: "valid json payload",
			raw:  `{"device_id":"truck-1","latitude":38.9,"longitude":-77.0,"timestamp":"2026-08-17T10:00:00Z"}`,
			want: ShipmentPing{DeviceID: "truck-1", Latitude: 38.9, Longitude: -77.0, Timestamp: "2026-08-17T10:00:00Z"},
		},
		{
			name:    "malformed json",
			raw:     `{"device_id": "truck-1"`,
			wantErr: true,
		},
		{
			name:    "empty payload",
			raw:     ``,
			wantErr: true,
		},
	}

	for _, c := range cases {
		t.Run(c.name, func(t *testing.T) {
			got, err := parsePingPayload([]byte(c.raw))
			if (err != nil) != c.wantErr {
				t.Fatalf("parsePingPayload(%q) error = %v, wantErr %v", c.raw, err, c.wantErr)
			}
			if !c.wantErr && got != c.want {
				t.Errorf("parsePingPayload(%q) = %+v, want %+v", c.raw, got, c.want)
			}
		})
	}
}

func TestHandlePing_ValidationErrorMapsCorrectly(t *testing.T) {
	err := handlePing(ShipmentPing{Latitude: 38.9, Longitude: -77.0})

	var verr *ValidationError
	if err == nil {
		t.Fatal("expected an error for missing device_id, got nil")
	}
	if !errors.As(err, &verr) {
		t.Errorf("expected a *ValidationError, got %T: %v", err, err)
	}
}
