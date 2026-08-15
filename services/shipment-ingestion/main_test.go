package main

import "testing"

func TestValidatePing(t *testing.T) {
	cases := []struct {
		name    string
		ping    ShipmentPing
		wantErr bool
	}{
		{"valid ping", ShipmentPing{DeviceID: "truck-1", Latitude: 38.9, Longitude: -77.0}, false},
		{"missing device id", ShipmentPing{Latitude: 38.9, Longitude: -77.0}, true},
	}

	for _, c := range cases {
		t.Run(c.name, func(t *testing.T) {
			err := validatePing(c.ping)
			if (err != nil) != c.wantErr {
				t.Errorf("validatePing(%+v) error = %v, wantErr %v", c.ping, err, c.wantErr)
			}
		})
	}
}