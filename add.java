const express = require("express");
const bodyParser = require("body-parser");
const { Kafka } = require("kafkajs");

const app = express();
app.use(bodyParser.json());

// Kafka setup
const kafka = new Kafka({
  clientId: "kyb-app",
  brokers: ["localhost:9092"]   // since you're running locally
});

const producer = kafka.producer();

async function start() {
  await producer.connect();
  console.log("Kafka connected");

  // POST /cases
  app.post("/cases", async (req, res) => {
    try {
      const event = {
        eventType: "kyb.case.created",
        payload: {
          ...req.body,
          eventId: `evt-${Date.now()}`,
          timestamp: new Date().toISOString()
        }
      };

      await producer.send({
        topic: "kyb.case.created",
        messages: [{ value: JSON.stringify(event) }]
      });

      res.json({ message: "Event sent to Kafka", event });

    } catch (err) {
      console.error(err);
      res.status(500).send("Error sending event");
    }
  });

  app.listen(3000, () => {
    console.log("Server running on port 3000");
  });
}

start();
