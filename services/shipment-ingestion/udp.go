package main

import (
	"log"
	"net"
	"time"
)

var udpDedup = newDedupCache(30 * time.Second)

func startUDPListener(port string) {
	addr, err := net.ResolveUDPAddr("udp", ":"+port)
	if err != nil {
		log.Fatalf("udp: bad address: %v", err)
	}

	conn, err := net.ListenUDP("udp", addr)
	if err != nil {
		log.Fatalf("udp: failed to listen on %s: %v", port, err)
	}
	defer conn.Close()

	log.Printf("shipment-ingestion udp listener on :%s", port)

	buf := make([]byte, 1024)
	for {
		n, remoteAddr, err := conn.ReadFromUDP(buf)
		if err != nil {
			log.Printf("udp: read error: %v", err)
			continue
		}

		p, err := parsePingPayload(buf[:n])
		if err != nil {
			log.Printf("udp: invalid payload from %s: %v", remoteAddr, err)
			continue
		}

		if udpDedup.seenBefore(p.RequestID) {
			// Already processed this exact ping — the client's retry means
			// our earlier ack got lost, not that this is a new ping. Ack it
			// again without reprocessing, so the client still sees success.
			conn.WriteToUDP([]byte("ACK"), remoteAddr)
			continue
		}

		if err := handlePing(p); err != nil {
			log.Printf("udp: handlePing failed for %s: %v", remoteAddr, err)
			continue
		}

		// UDP has no built-in delivery confirmation — this ack is what lets
		// a client (see tools/failover-test-client) detect a drop and fail
		// over to TCP.
		conn.WriteToUDP([]byte("ACK"), remoteAddr)
	}
}
