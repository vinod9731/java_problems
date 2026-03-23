Perfect 🚀 — now let’s build Week 4 (Intern B) completely end-to-end, clean and production-style, based on your current setup.

This will include:

✅ screening-processor

✅ risk-processor

✅ Memgraph updates (NO manual queries)

✅ Kafka flow (NO manual topic creation)

✅ Works with your existing UBO processor



---

📁 📂 Folder Structure (VERY IMPORTANT)

Inside your project:

mem-kyb/
 ├── services/
 │    └── consumers/
 │         └── src/
 │              ├── memgraph-client.js
 │              ├── screening-processor.js   ✅ NEW
 │              ├── risk-processor.js        ✅ NEW
 │              └── index.js                 (optional runner)


---

🧠 1. screening-processor.js (FULL CODE)

👉 Consumes: kyb.ubo.discovered
👉 Writes to Memgraph
👉 Emits: kyb.screening.completed

const { Kafka } = require("kafkajs");
const { getSession } = require("./memgraph-client");

const kafka = new Kafka({
  clientId: "screening-processor",
  brokers: [process.env.KAFKA_BROKER || "kafka:9092"],
});

const consumer = kafka.consumer({ groupId: "screening-group" });
const producer = kafka.producer();

const run = async () => {
  await consumer.connect();
  await producer.connect();

  await consumer.subscribe({
    topic: "kyb.ubo.discovered",
    fromBeginning: true,
  });

  console.log("🚀 Screening processor started...");

  await consumer.run({
    eachMessage: async ({ message }) => {
      const data = JSON.parse(message.value.toString());

      const { caseId, ubos } = data;

      console.log(`📥 Screening case: ${caseId}`);

      const session = getSession();

      // 🔍 Simulate screening (simple rule)
      const screened = ubos.map((ubo) => {
        const hit =
          ubo.person.toLowerCase().includes("john") ||
          ubo.person.toLowerCase().includes("alice");

        return {
          ...ubo,
          screeningHit: hit,
          status: hit ? "HIT" : "CLEAR",
        };
      });

      // ✅ Write to Memgraph
      for (const ubo of screened) {
        await session.run(
          `
          MATCH (p:Person {name: $person}), (c:KybCase {caseId: $caseId})
          MERGE (check:Check {type: "SCREENING", person: $person})
          SET check.status = $status,
              check.details = $details
          MERGE (p)-[:HAS_CHECK]->(check)
          MERGE (c)-[:HAS_CHECK]->(check)
          `,
          {
            person: ubo.person,
            caseId,
            status: ubo.status,
            details: ubo.screeningHit
              ? "Watchlist match"
              : "No issues",
          }
        );
      }

      // 📤 Emit event
      await producer.send({
        topic: "kyb.screening.completed",
        messages: [
          {
            value: JSON.stringify({
              caseId,
              ubos: screened,
              timestamp: new Date().toISOString(),
            }),
          },
        ],
      });

      console.log(`✅ Screening completed for ${caseId}`);
    },
  });
};

run().catch(console.error);


---

🧠 2. risk-processor.js (FULL CODE)

👉 Consumes: kyb.screening.completed
👉 Writes to Memgraph
👉 Emits: kyb.risk.scored

const { Kafka } = require("kafkajs");
const { getSession } = require("./memgraph-client");

const kafka = new Kafka({
  clientId: "risk-processor",
  brokers: [process.env.KAFKA_BROKER || "kafka:9092"],
});

const consumer = kafka.consumer({ groupId: "risk-group" });
const producer = kafka.producer();

const run = async () => {
  await consumer.connect();
  await producer.connect();

  await consumer.subscribe({
    topic: "kyb.screening.completed",
    fromBeginning: true,
  });

  console.log("🚀 Risk processor started...");

  await consumer.run({
    eachMessage: async ({ message }) => {
      const data = JSON.parse(message.value.toString());

      const { caseId, ubos } = data;

      console.log(`📥 Risk scoring case: ${caseId}`);

      const session = getSession();

      // 🧠 Calculate risk
      let score = 0;
      const reasons = [];

      for (const ubo of ubos) {
        if (ubo.screeningHit) {
          score += 50;
          reasons.push(`Screening hit: ${ubo.person}`);
        }

        if (ubo.effectivePct > 0.5) {
          score += 20;
          reasons.push(`High ownership: ${ubo.person}`);
        }
      }

      const level =
        score >= 70 ? "HIGH" : score >= 40 ? "MEDIUM" : "LOW";

      // ✅ Write to Memgraph
      await session.run(
        `
        MATCH (c:KybCase {caseId: $caseId})
        MERGE (r:Risk {caseId: $caseId})
        SET r.score = $score,
            r.level = $level,
            r.reason = $reason
        MERGE (c)-[:HAS_RISK]->(r)
        `,
        {
          caseId,
          score,
          level,
          reason: reasons.join(", "),
        }
      );

      // 📤 Emit event
      await producer.send({
        topic: "kyb.risk.scored",
        messages: [
          {
            value: JSON.stringify({
              caseId,
              score,
              level,
              reasons,
              timestamp: new Date().toISOString(),
            }),
          },
        ],
      });

      console.log(`🔥 Risk scored for ${caseId}: ${level}`);
    },
  });
};

run().catch(console.error);


---

🧠 3. memgraph-client.js (if not already correct)

const neo4j = require("neo4j-driver");

const driver = neo4j.driver(
  process.env.MEMGRAPH_URI || "bolt://memgraph:7687",
  neo4j.auth.basic("", "")
);

const getSession = () => driver.session();

module.exports = { getSession };


---

🐳 4. Docker Setup (IMPORTANT)

Add both processors in docker-compose.yml:

screening-processor:
  build: .
  command: node services/consumers/src/screening-processor.js
  depends_on:
    - kafka
    - memgraph

risk-processor:
  build: .
  command: node services/consumers/src/risk-processor.js
  depends_on:
    - kafka
    - memgraph


---

🚀 FINAL FLOW (END-TO-END)

kyb.ownership.submitted
        ↓
ownership-consumer
        ↓
Memgraph graph created
        ↓
ubo-processor
        ↓
kyb.ubo.discovered
        ↓
screening-processor
        ↓
kyb.screening.completed
        ↓
risk-processor
        ↓
kyb.risk.scored


---

🔍 FINAL VERIFICATION QUERIES

1. Check Screening

MATCH (c:KybCase)-[:HAS_CHECK]->(check)
RETURN c.caseId, check.status, check.details;


---

2. Check Risk

MATCH (c:KybCase)-[:HAS_RISK]->(r)
RETURN c.caseId, r.score, r.level;


---

3. High Risk Cases

MATCH (c:KybCase)-[:HAS_RISK]->(r)
WHERE r.level = "HIGH"
RETURN c.caseId, r.score;


---

🎯 DONE — You Now Have:

✅ Full Kafka event-driven pipeline
✅ No manual topic creation
✅ No manual queries
✅ Memgraph fully integrated
✅ Real-world KYB architecture


---

If you want next 🔥
👉 I can help you connect this to UI (API endpoint)
👉 Or prepare explanation for interview/demo





const { Kafka } = require("kafkajs");
const { getSession } = require("./memgraph-client");

// 🔴 Watchlist (Sanctions list simulation)
const WATCHLIST = [
  "John",
  "Alice",
  "Bob Fraud",
];

const kafka = new Kafka({
  clientId: "screening-processor",
  brokers: [process.env.KAFKA_BROKER || "kafka:9092"],
});

const consumer = kafka.consumer({ groupId: "screening-group" });
const producer = kafka.producer();

const run = async () => {
  await consumer.connect();
  await producer.connect();

  await consumer.subscribe({
    topic: "kyb.ubo.discovered",
    fromBeginning: true,
  });

  console.log("🚀 Screening processor started...");

  await consumer.run({
    eachMessage: async ({ message }) => {
      try {
        const data = JSON.parse(message.value.toString());

        const { caseId, ubos } = data;

        console.log(`📥 Screening case: ${caseId}`);
        console.log("UBOs received:", ubos);

        const session = getSession();

        // 🔍 Screening logic using WATCHLIST
        const screened = ubos.map((ubo) => {
          const hit = WATCHLIST.some((name) =>
            ubo.person.toLowerCase().includes(name.toLowerCase())
          );

          return {
            ...ubo,
            screeningHit: hit,
            status: hit ? "HIT" : "CLEAR",
          };
        });

        // ✅ Write screening results to Memgraph
        for (const ubo of screened) {
          await session.run(
            `
            MATCH (p:Person {name: $person}), (c:KybCase {caseId: $caseId})
            MERGE (check:Check {type: "SCREENING", person: $person})
            SET check.status = $status,
                check.details = $details
            MERGE (p)-[:HAS_CHECK]->(check)
            MERGE (c)-[:HAS_CHECK]->(check)
            `,
            {
              person: ubo.person,
              caseId,
              status: ubo.status,
              details: ubo.screeningHit
                ? "Watchlist match"
                : "No issues",
            }
          );
        }

        // 📤 Emit screening completed event
        const event = {
          caseId,
          ubos: screened,
          timestamp: new Date().toISOString(),
        };

        await producer.send({
          topic: "kyb.screening.completed",
          messages: [
            {
              value: JSON.stringify(event),
            },
          ],
        });

        console.log(`✅ Screening completed for ${caseId}`);
        await session.close();
      } catch (err) {
        console.error("❌ Error in screening processor:", err);
      }
    },
  });
};

run().catch(console.error);


MATCH (c:KybCase)-[:HAS_CHECK]->(check)
RETURN c.caseId, check.status, check.details;



const { Kafka } = require("kafkajs");
const { getSession } = require("./memgraph-client");

const kafka = new Kafka({
  clientId: "risk-processor",
  brokers: [process.env.KAFKA_BROKER || "kafka:9092"],
});

const consumer = kafka.consumer({ groupId: "risk-group" });
const producer = kafka.producer();

const run = async () => {
  await consumer.connect();
  await producer.connect();

  await consumer.subscribe({
    topic: "kyb.screening.completed",
    fromBeginning: true,
  });

  console.log("🚀 Risk processor started...");

  await consumer.run({
    eachMessage: async ({ message }) => {
      try {
        const data = JSON.parse(message.value.toString());

        const { caseId, ubos } = data;

        console.log(`📥 Risk scoring case: ${caseId}`);
        console.log("Screening input:", ubos);

        const session = getSession();

        let score = 0;
        let reasons = [];

        // 🔍 Risk calculation logic
        for (const ubo of ubos) {
          // 🚨 Screening hit → high risk
          if (ubo.screeningHit) {
            score += 50;
            reasons.push(`Screening hit: ${ubo.person}`);
          }

          // 📊 High ownership → additional risk
          if (ubo.effectivePct >= 0.5) {
            score += 20;
            reasons.push(`High ownership (>50%): ${ubo.person}`);
          }

          // ⚠️ Medium ownership
          if (ubo.effectivePct >= 0.25 && ubo.effectivePct < 0.5) {
            score += 10;
            reasons.push(`Moderate ownership (>25%): ${ubo.person}`);
          }
        }

        // 🧠 Risk level classification
        let level = "LOW";

        if (score >= 70) {
          level = "HIGH";
        } else if (score >= 40) {
          level = "MEDIUM";
        }

        console.log(`🔥 Risk Score: ${score}, Level: ${level}`);

        // ✅ Store in Memgraph
        await session.run(
          `
          MATCH (c:KybCase {caseId: $caseId})
          MERGE (r:Risk {caseId: $caseId})
          SET r.score = $score,
              r.level = $level,
              r.reasons = $reasons
          MERGE (c)-[:HAS_RISK]->(r)
          `,
          {
            caseId,
            score,
            level,
            reasons: reasons.join(", "),
          }
        );

        // 📤 Emit Kafka event
        const event = {
          caseId,
          score,
          level,
          reasons,
          timestamp: new Date().toISOString(),
        };

        await producer.send({
          topic: "kyb.risk.scored",
          messages: [
            {
              value: JSON.stringify(event),
            },
          ],
        });

        console.log(`✅ Risk processed for ${caseId}`);

        await session.close();
      } catch (err) {
        console.error("❌ Error in risk processor:", err);
      }
    },
  });
};

run().catch(console.error);

MATCH (c:KybCase)-[:HAS_RISK]->(r)
RETURN c.caseId, r.score, r.level, r.reasons;

