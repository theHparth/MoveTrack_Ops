package main

import (
	"encoding/binary"
	"io"
	"log"
	"net"
)

func startTCPListener(port string) {
	listener, err := net.Listen("tcp", ":"+port)
	if err != nil {
		log.Fatalf("tcp: failed to listen on %s: %v", port, err)
	}
	defer listener.Close()

	log.Printf("shipment-ingestion tcp listener on :%s", port)

	for {
		conn, err := listener.Accept()
		if err != nil {
			log.Printf("tcp: accept error: %v", err)
			continue
		}
		go handleTCPConn(conn)
	}
}

// handleTCPConn reads a single length-prefixed message (4-byte big-endian
// length, then that many bytes of JSON payload) rather than reading until
// EOF/connection-close. A length prefix means the connection never needs a
// half-close to signal "done sending" — confirmed the hard way that Docker
// Desktop's port-publishing proxy doesn't correctly pass a client's
// half-close (net.TCPConn.CloseWrite) through to this container: it tears
// down the whole connection on the client's FIN, so a server ack written
// after that point never reaches the client at all.
func handleTCPConn(conn net.Conn) {
	defer conn.Close()

	var length uint32
	if err := binary.Read(conn, binary.BigEndian, &length); err != nil {
		log.Printf("tcp: failed to read length prefix from %s: %v", conn.RemoteAddr(), err)
		return
	}

	data := make([]byte, length)
	if _, err := io.ReadFull(conn, data); err != nil {
		log.Printf("tcp: failed to read payload from %s: %v", conn.RemoteAddr(), err)
		return
	}

	p, err := parsePingPayload(data)
	if err != nil {
		log.Printf("tcp: invalid payload from %s: %v", conn.RemoteAddr(), err)
		return
	}

	if err := handlePing(p); err != nil {
		log.Printf("tcp: handlePing failed for %s: %v", conn.RemoteAddr(), err)
		return
	}

	conn.Write([]byte("ACK"))
}
