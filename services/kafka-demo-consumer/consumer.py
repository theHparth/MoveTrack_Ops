import json

from kafka import KafkaConsumer

consumer = KafkaConsumer(
    "shipment.tracking",
    bootstrap_servers="kafka:29092",
    group_id="demo-consumer",
    value_deserializer=lambda v: json.loads(v.decode("utf-8")),
    auto_offset_reset="earliest",
)

for message in consumer:
    print(f"partition={message.partition} offset={message.offset} value={message.value}")