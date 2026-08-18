#!/bin/sh
set -e

# Renders the real wg0.conf from the committed template + env vars at
# container start — the real keys never get baked into the image or
# committed to git, only passed in as environment variables.
envsubst < /etc/wireguard/wg0.conf.template > /etc/wireguard/wg0.conf

wg-quick up wg0

# wg-quick brings the interface up and returns immediately — this keeps
# the container (and the tunnel) alive for shipment-ingestion to share.
tail -f /dev/null
