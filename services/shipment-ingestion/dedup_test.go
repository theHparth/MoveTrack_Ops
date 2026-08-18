package main

import (
	"testing"
	"time"
)

func TestDedupCache_SeenBefore(t *testing.T) {
	c := newDedupCache(30 * time.Second)

	if c.seenBefore("req-1") {
		t.Error("first sighting of req-1 should not be marked as seen before")
	}
	if !c.seenBefore("req-1") {
		t.Error("second sighting of req-1 within the window should be marked as seen before")
	}
	if c.seenBefore("req-2") {
		t.Error("first sighting of a different request id should not be marked as seen before")
	}
}

func TestDedupCache_EmptyRequestIDNeverDeduped(t *testing.T) {
	c := newDedupCache(30 * time.Second)

	if c.seenBefore("") {
		t.Error("empty request id should never be marked as seen before")
	}
	if c.seenBefore("") {
		t.Error("empty request id should never be marked as seen before, even repeated")
	}
}

func TestDedupCache_ExpiresAfterWindow(t *testing.T) {
	c := newDedupCache(50 * time.Millisecond)

	c.seenBefore("req-1")
	time.Sleep(100 * time.Millisecond)

	if c.seenBefore("req-1") {
		t.Error("request id should no longer be considered seen after the dedup window expires")
	}
}
