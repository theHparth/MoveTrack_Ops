def should_publish(severity: str) -> bool:
    return severity in ("HIGH", "CRITICAL")