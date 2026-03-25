Perfect 👍 — your structure is actually very good and already aligned with Week 5.
Now we will complete Week 5 (Intern B) on top of your current structure (no breaking changes).


---

🚀 WHAT YOU ALREADY HAVE

From your screenshot:

✅ Consumers (Week 3 & 4 done)

ubo-processor.js

screening-processor.js

risk-processor.js


✅ Workbench API structure:

workbench-api/src/
  ├── db/memgraph.js
  ├── kafka/producer.js
  ├── routes/
  │    ├── cases.js
  │    ├── decision.js
  │    ├── ownership.js
  │    ├── ubo.js
  ├── index.js

👉 So we just need to complete missing logic + connect everything


---

✅ STEP 1: FIX YOUR ENTRY (IMPORTANT)

📄 workbench-api/src/index.js

const express = require("express");
const casesRoutes = require("./routes/cases");
const decisionRoutes = require("./routes/decision");

const app = express();
app.use(express.json());

// Routes
app.use("/queues", casesRoutes);
app.use("/cases", casesRoutes);
app.use("/cases", decisionRoutes);

app.listen(3000, () => {
  console.log("🚀 Workbench API running on port 3000");
});


---

✅ STEP 2: IMPLEMENT QUEUE API

📄 routes/cases.js

const express = require("express");
const router = express.Router();
const { getSession } = require("../db/memgraph");

// ✅ GET /queues/kyc-officer
router.get("/kyc-officer", async (req, res) => {
  const session = getSession();

  const result = await session.run(`
    MATCH (c:KybCase)
    OPTIONAL MATCH (c)-[:HAS_CHECK]->(check:Check)
    RETURN c.caseId AS caseId,
           collect(DISTINCT check.status) AS checks,
           c.status AS status
  `);

  const data = result.records.map(r => ({
    caseId: r.get("caseId"),
    status: r.get("status") || "NEW",
    checks: r.get("checks"),
  }));

  res.json(data);
});


// ✅ GET /cases/:caseId
router.get("/:caseId", async (req, res) => {
  const { caseId } = req.params;
  const session = getSession();

  const result = await session.run(`
    MATCH (c:KybCase {caseId: $caseId})
    OPTIONAL MATCH (c)<-[:IS_UBO_OF]-(p:Person)
    OPTIONAL MATCH (p)-[:HAS_CHECK]->(check:Check)
    RETURN c.caseId AS caseId,
           c.status AS status,
           collect(DISTINCT p.name) AS ubos,
           collect(DISTINCT check.status) AS checks
  `, { caseId });

  const record = result.records[0];

  res.json({
    caseId: record.get("caseId"),
    status: record.get("status"),
    ubos: record.get("ubos"),
    checks: record.get("checks"),
  });
});

module.exports = router;


---

✅ STEP 3: DECISION API (CRITICAL 🔥)

📄 routes/decision.js

const express = require("express");
const router = express.Router();
const { Kafka } = require("kafkajs");

const kafka = new Kafka({
  clientId: "workbench-api",
  brokers: [process.env.KAFKA_BROKER || "kafka:29092"],
});

const producer = kafka.producer();

(async () => {
  await producer.connect();
})();


// ✅ POST /cases/:caseId/decision
router.post("/:caseId/decision", async (req, res) => {
  const { caseId } = req.params;
  const { decision } = req.body;

  await producer.send({
    topic: "kyb.decision.made",
    messages: [
      {
        value: JSON.stringify({
          caseId,
          decision,
          timestamp: new Date().toISOString(),
        }),
      },
    ],
  });

  res.json({ message: "Decision sent successfully" });
});

module.exports = router;


---

✅ STEP 4: CREATE DECISION PROCESSOR (MISSING PIECE ⚠️)

👉 Create this inside:

services/consumers/src/decision-processor.js

const { Kafka } = require("kafkajs");
const { getSession } = require("./memgraph-client");

const kafka = new Kafka({
  clientId: "decision-processor",
  brokers: [process.env.KAFKA_BROKER || "kafka:29092"],
});

const consumer = kafka.consumer({ groupId: "decision-group" });

const run = async () => {
  await consumer.connect();

  await consumer.subscribe({
    topic: "kyb.decision.made",
    fromBeginning: true,
  });

  console.log("🚀 Decision processor started...");

  await consumer.run({
    eachMessage: async ({ message }) => {
      const data = JSON.parse(message.value.toString());

      const { caseId, decision, timestamp } = data;
      const session = getSession();

      // ✅ Update case status
      await session.run(`
        MATCH (c:KybCase {caseId: $caseId})
        SET c.status = $decision
      `, { caseId, decision });

      // ✅ Add event timeline
      await session.run(`
        MATCH (c:KybCase {caseId: $caseId})
        CREATE (e:Event {
          type: "DECISION",
          decision: $decision,
          timestamp: $timestamp
        })
        MERGE (c)-[:HAS_EVENT]->(e)
      `, { caseId, decision, timestamp });

      console.log(`✅ Decision processed for ${caseId}`);
    },
  });
};

run().catch(console.error);


---

✅ STEP 5: CREATE TOPIC (IMPORTANT)

kafka-topics.sh --create --topic kyb.decision.made --bootstrap-server kafka:9092


---

✅ STEP 6: TEST FLOW

1️⃣ Get queue

curl localhost:3000/queues/kyc-officer

2️⃣ Get case

curl localhost:3000/cases/case-1

3️⃣ Send decision

curl -X POST localhost:3000/cases/case-1/decision \
-H "Content-Type: application/json" \
-d '{"decision":"APPROVED"}'


---

✅ STEP 7: VERIFY IN MEMGRAPH

MATCH (c:KybCase)
RETURN c.caseId, c.status;

MATCH (c:KybCase)-[:HAS_EVENT]->(e:Event)
RETURN c.caseId, e.decision, e.timestamp;


---

🎯 FINAL FLOW (WHAT YOU SHOULD SEE)

Kafka (decision event)
        ↓
decision-processor
        ↓
Memgraph updated
        ↓
API reflects new status


---

💬 DEMO LINE

👉 Say this confidently:

> “In Week 5, we added a workbench layer where officers can review cases and take decisions. These decisions are sent as Kafka events and reflected in Memgraph, creating a complete audit trail.”




---

🚀 YOU ARE DONE WITH WEEK 5

You now have: ✅ Queue API
✅ Case details API
✅ Decision API
✅ Kafka integration
✅ Memgraph updates
✅ Event timeline


---

If you want next:

👉 I can give simple UI page (HTML)
👉 or perfect demo script for Week 5 (step-by-step speaking)
👉 or docker-compose integration for this API + processor

Just tell 👍
