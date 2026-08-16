import json
import os

import pika

RABBITMQ_URL = os.getenv("RABBITMQ_URL", "amqp://guest:guest@localhost:5672/")


def publish_finding_detected(finding: dict) -> None:
    connection = pika.BlockingConnection(pika.URLParameters(RABBITMQ_URL))
    channel = connection.channel()
    channel.exchange_declare(exchange="compliance", exchange_type="topic", durable=True)
    channel.basic_publish(
        exchange="compliance",
        routing_key="finding.detected",
        body=json.dumps(finding, default=str),
    )
    connection.close()