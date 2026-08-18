package main

import (
	"sync"
	"time"
)

// dedupCache tracks recently-seen RequestIDs so a UDP client's retry (sent
// because it never received an ack, not because the original ping was
// actually lost) doesn't get processed and persisted twice.
type dedupCache struct {
	mu     sync.Mutex
	seen   map[string]time.Time
	window time.Duration
}

func newDedupCache(window time.Duration) *dedupCache {
	return &dedupCache{
		seen:   make(map[string]time.Time),
		window: window,
	}
}

// seenBefore reports whether requestID was already recorded within the
// dedup window, and records it if not. An empty requestID is never deduped
// (REST callers that don't set one always process normally). Expired
// entries are purged opportunistically on each call — fine at this scale,
// no separate cleanup goroutine needed.
func (c *dedupCache) seenBefore(requestID string) bool {
	if requestID == "" {
		return false
	}

	c.mu.Lock()
	defer c.mu.Unlock()

	now := time.Now()
	for id, seenAt := range c.seen {
		if now.Sub(seenAt) >= c.window {
			delete(c.seen, id)
		}
	}

	if _, ok := c.seen[requestID]; ok {
		return true
	}
	c.seen[requestID] = now
	return false
}
