from app.logic import should_publish


def test_should_publish_high():
    assert should_publish("HIGH") is True


def test_should_publish_critical():
    assert should_publish("CRITICAL") is True


def test_should_publish_low():
    assert should_publish("LOW") is False