const { Kafka } = require("kafkajs");
const { broadcast } = require("./server");

const kafka = new Kafka({ clientId: "ws-live-feed", brokers: ["kafka:29092"] });
const consumer = kafka.consumer({ groupId: "ws-live-feed" });

async function start() {
  await consumer.connect();
  await consumer.subscribe({
    topic: "shipment.tracking",
    fromBeginning: false,
  });

  await consumer.run({
    eachMessage: async ({ message }) => {
      const ping = JSON.parse(message.value.toString());
      broadcast(ping);
    },
  });
}

start();
