Short answer 👇

👉 Yes, you can use memgraph-client.js
👉 But DO NOT use mgclient npm package (it caused your error ❌)


---

🧠 What is memgraph-client.js?

It’s just a custom wrapper file you create to:

connect to Memgraph

reuse connection across your app


👉 internally it should use:

neo4j-driver


---

✅ BEST PRACTICE (Recommended)

👉 Create your own client file
👉 Use neo4j-driver inside it


---

🪜 STEP 1 — Create File

📁 services/consumers/src/memgraph-client.js

const neo4j = require("neo4j-driver");

const driver = neo4j.driver(
  "bolt://localhost:7687",
  neo4j.auth.basic("", "") // default for Memgraph
);

const getSession = () => {
  return driver.session();
};

module.exports = { getSession };


---

🪜 STEP 2 — Use in Consumer

📁 ownership-consumer.js

Replace connection code with:

const { getSession } = require("./memgraph-client");

Then inside your consumer:

const session = getSession();


---

🪜 FULL CLEAN VERSION (IMPORTANT)

const { Kafka } = require("kafkajs");
const { getSession } = require("./memgraph-client");

const kafka = new Kafka({
  clientId: "kyb-consumer",
  brokers: ["localhost:9092"],
});

const consumer = kafka.consumer({ groupId: "ownership-group" });

const run = async () => {
  await consumer.connect();
  await consumer.subscribe({ topic: "kyb.ownership.submitted", fromBeginning: true });

  const session = getSession();

  await consumer.run({
    eachMessage: async ({ message }) => {
      const data = JSON.parse(message.value.toString());

      console.log("Received event:", data);

      try {
        for (const node of data.nodes) {
          await session.run(
            `MERGE (n:${node.type} {id: $id})
             SET n.name = $name`,
            { id: node.id, name: node.name }
          );
        }

        for (const edge of data.edges) {
          await session.run(
            `MATCH (a {id: $from}), (b {id: $to})
             MERGE (a)-[r:OWNS]->(b)
             SET r.pct = $pct`,
            {
              from: edge.from,
              to: edge.to,
              pct: edge.pct,
            }
          );
        }

        console.log("Graph updated in Memgraph ✅");

      } catch (err) {
        console.error("Error:", err);
      }
    },
  });
};

run();


---

🚨 IMPORTANT NOTE

Option	Status

mgclient ❌	Broken / avoid
neo4j-driver ✅	Correct
memgraph-client.js ✅	Just wrapper (recommended)



---

🔥 WHY THIS IS GOOD

You now have:

👉 clean architecture
👉 reusable DB connection
👉 production-style code


---

🎯 NEXT STEP

Run again:

node services/consumers/src/ownership-consumer.js



Then:

./scripts/seed-ownership.sh


---

🚀 AFTER THIS

Tell me:

👉 "Graph visible"

Then we go to:

🔥 UBO calculation (core feature)
🔥 graph traversal logic


---

You’re now writing real backend architecture code 💯
