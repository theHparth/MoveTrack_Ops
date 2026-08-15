import hashlib
import json
import os
import redis

REDIS_HOST = os.environ.get("REDIS_HOST", "localhost")
REDIS_PORT = int(os.environ.get("REDIS_PORT", "6379"))
CACHE_TTL_SECONDS = 300

_client = redis.Redis(host=REDIS_HOST, port=REDIS_PORT, decode_responses=True)


def _cache_key(features: dict) -> str:
    payload = json.dumps(features, sort_keys=True)
    return "triage:" + hashlib.sha256(payload.encode()).hexdigest()


def get_cached_prediction(features: dict):
    key = _cache_key(features)
    return _client.get(key)


def set_cached_prediction(features: dict, priority: str):
    key = _cache_key(features)
    _client.set(key, priority, ex=CACHE_TTL_SECONDS)