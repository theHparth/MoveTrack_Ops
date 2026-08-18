package main

import (
	"encoding/binary"
	"fmt"
	"net"
	"testing"
	"time"
)

func skipUnlessPostgresReachable(t *testing.T) {
	t.Helper()
	conn, err := net.DialTimeout("tcp", "localhost:5432", 2*time.Second)
	if err != nil {
		t.Skip("postgres not reachable on localhost:5432, skipping integration test")
	}
	conn.Close()

	if db == nil {
		db = connectDB()
		ensureSchema(db)
	}
}

func TestUDPListener_RealDatagramReachesHandler(t *testing.T) {
	skipUnlessPostgresReachable(t)

	port := "18091"
	go startUDPListener(port)
	time.Sleep(100 * time.Millisecond) // let the listener bind before sending

	conn, err := net.Dial("udp", "localhost:"+port)
	if err != nil {
		t.Fatalf("failed to dial udp listener: %v", err)
	}
	defer conn.Close()

	deviceID := fmt.Sprintf("UDP-TEST-%d", time.Now().UnixNano())
	payload := fmt.Sprintf(`{"device_id":%q,"latitude":38.9,"longitude":-77.0,"timestamp":"2026-08-17T10:00:00Z"}`, deviceID)

	if _, err := conn.Write([]byte(payload)); err != nil {
		t.Fatalf("failed to send udp payload: %v", err)
	}

	conn.SetReadDeadline(time.Now().Add(2 * time.Second))
	buf := make([]byte, 16)
	n, err := conn.Read(buf)
	if err != nil {
		t.Fatalf("did not receive udp ack: %v", err)
	}
	if string(buf[:n]) != "ACK" {
		t.Errorf("expected ACK, got %q", buf[:n])
	}

	pings, err := listPings(db)
	if err != nil {
		t.Fatalf("failed to query pings: %v", err)
	}
	if !containsDeviceID(pings, deviceID) {
		t.Errorf("expected a persisted ping for %s, none found", deviceID)
	}
}

func TestTCPListener_RealConnectionReachesHandler(t *testing.T) {
	skipUnlessPostgresReachable(t)

	port := "18092"
	go startTCPListener(port)
	time.Sleep(100 * time.Millisecond)

	conn, err := net.Dial("tcp", "localhost:"+port)
	if err != nil {
		t.Fatalf("failed to dial tcp listener: %v", err)
	}

	deviceID := fmt.Sprintf("TCP-TEST-%d", time.Now().UnixNano())
	payload := fmt.Sprintf(`{"device_id":%q,"latitude":39.0,"longitude":-76.0,"timestamp":"2026-08-17T10:05:00Z"}`, deviceID)

	length := uint32(len(payload))
	if err := binary.Write(conn, binary.BigEndian, length); err != nil {
		t.Fatalf("failed to write length prefix: %v", err)
	}
	if _, err := conn.Write([]byte(payload)); err != nil {
		t.Fatalf("failed to send tcp payload: %v", err)
	}

	conn.SetReadDeadline(time.Now().Add(2 * time.Second))
	buf := make([]byte, 16)
	n, err := conn.Read(buf)
	if err != nil {
		t.Fatalf("did not receive tcp ack: %v", err)
	}
	if string(buf[:n]) != "ACK" {
		t.Errorf("expected ACK, got %q", buf[:n])
	}
	conn.Close()

	pings, err := listPings(db)
	if err != nil {
		t.Fatalf("failed to query pings: %v", err)
	}
	if !containsDeviceID(pings, deviceID) {
		t.Errorf("expected a persisted ping for %s, none found", deviceID)
	}
}

func containsDeviceID(pings []ShipmentPing, deviceID string) bool {
	for _, p := range pings {
		if p.DeviceID == deviceID {
			return true
		}
	}
	return false
}
