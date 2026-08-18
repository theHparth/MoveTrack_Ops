package main

import (
	"encoding/binary"
	"flag"
	"fmt"
	"net"
	"os"
	"time"
)

func main() {
	host := flag.String("host", "localhost", "target host")
	udpPort := flag.String("udp-port", "8091", "udp port")
	tcpPort := flag.String("tcp-port", "8092", "tcp port")
	retries := flag.Int("retries", 3, "udp attempts before falling back to tcp")
	flag.Parse()

	// One RequestID per logical ping, reused across every UDP retry — lets
	// the server dedup a retry caused by a lost ack instead of processing
	// the same ping multiple times.
	requestID := fmt.Sprintf("%d", time.Now().UnixNano())
	payload := fmt.Sprintf(
		`{"device_id":"DEVICE-FAILOVER","latitude":40.0,"longitude":-75.0,"timestamp":"2026-08-17T10:10:00Z","request_id":%q}`,
		requestID,
	)

	if trySendUDP(*host, *udpPort, *retries, payload) {
		fmt.Println("delivered via UDP")
		return
	}

	fmt.Println("UDP delivery unconfirmed, falling back to TCP")
	if err := sendTCP(*host, *tcpPort, payload); err != nil {
		fmt.Fprintln(os.Stderr, "TCP fallback also failed:", err)
		os.Exit(1)
	}
	fmt.Println("delivered via TCP fallback")
}

func trySendUDP(host, port string, retries int, payload string) bool {
	addr := net.JoinHostPort(host, port)
	conn, err := net.Dial("udp", addr)
	if err != nil {
		return false
	}
	defer conn.Close()

	for i := 0; i < retries; i++ {
		conn.Write([]byte(payload))
		conn.SetReadDeadline(time.Now().Add(1 * time.Second))
		buf := make([]byte, 16)
		if n, err := conn.Read(buf); err == nil && string(buf[:n]) == "ACK" {
			return true
		}
	}
	return false
}

// sendTCP writes a length-prefixed message (4-byte big-endian length, then
// the payload) rather than writing the payload and half-closing to signal
// "done." A half-close (net.TCPConn.CloseWrite) doesn't survive Docker
// Desktop's port-publishing proxy intact — it tears the whole connection
// down on the client's FIN, so a server ack written afterward never arrives.
// A length prefix means the connection never needs to half-close at all.
func sendTCP(host, port string, payload string) error {
	addr := net.JoinHostPort(host, port)
	conn, err := net.Dial("tcp", addr)
	if err != nil {
		return err
	}
	defer conn.Close()

	length := uint32(len(payload))
	if err := binary.Write(conn, binary.BigEndian, length); err != nil {
		return err
	}
	if _, err := conn.Write([]byte(payload)); err != nil {
		return err
	}

	conn.SetReadDeadline(time.Now().Add(2 * time.Second))
	buf := make([]byte, 16)
	n, err := conn.Read(buf)
	if err != nil {
		return fmt.Errorf("no ack received: %w", err)
	}
	if string(buf[:n]) != "ACK" {
		return fmt.Errorf("unexpected response: %q", buf[:n])
	}
	return nil
}
