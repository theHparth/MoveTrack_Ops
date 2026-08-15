package main

import (
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
)

func TestHandleIngest_MissingDeviceID(t *testing.T) {
	body := strings.NewReader(`{"latitude": 38.9, "longitude": -77.0}`)
	req := httptest.NewRequest(http.MethodPost, "/ingest", body)
	w := httptest.NewRecorder()

	handleIngest(w, req)

	if w.Code != http.StatusBadRequest {
		t.Errorf("expected 400 for missing device_id, got %d", w.Code)
	}
}