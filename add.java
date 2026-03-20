const { Kafka } = require("kafkajs");
const { getSession } = require("./memgraph-client");

// Kafka config
const kafka = new Kafka({
  clientId: "kyb-consumer",
  brokers: [process.env.KAFKA_BROKER || "localhost:9092"],
});

const consumer = kafka.consumer({ groupId: "ownership-group" });

const run = async () => {
  await consumer.connect();

  await consumer.subscribe({
    topic: "kyb.ownership.submitted",
    fromBeginning: true,
  });

  console.log("🚀 Ownership consumer started...");

  const session = getSession();

  await consumer.run({
    eachMessage: async ({ message }) => {
      const data = JSON.parse(message.value.toString());
      console.log("📩 Received event:", data);

      const caseId = data.caseId;

      try {
        // ============================================
        // 1. CREATE / MERGE CASE NODE
        // ============================================
        await session.run(
          `
          MERGE (c:KybCase {caseId: $caseId})
          SET c.status = "NEW",
              c.createdAt = timestamp()
          `,
          { caseId }
        );

        // ============================================
        // 2. CREATE NODES (Company / Person)
        // ============================================
        for (const node of data.nodes) {
          if (node.type === "Company") {
            await session.run(
              `
              MERGE (c:Company {companyId: $id})
              SET c.name = $name
              `,
              { id: node.id, name: node.name }
            );
          }

          if (node.type === "Person") {
            await session.run(
              `
              MERGE (p:Person {personId: $id})
              SET p.fullName = $name
              `,
              { id: node.id, name: node.name }
            );
          }
        }

        // ============================================
        // 3. LINK CASE → ROOT COMPANY
        // ============================================
        const rootCompany = data.nodes.find(
          (n) => n.type === "Company"
        );

        if (rootCompany) {
          await session.run(
            `
            MATCH (c:KybCase {caseId: $caseId}),
                  (comp:Company {companyId: $companyId})
            MERGE (c)-[:TARGET]->(comp)
            `,
            {
              caseId,
              companyId: rootCompany.id,
            }
          );
        }

        // ============================================
        // 4. CREATE OWNERSHIP RELATIONSHIPS
        // ============================================
        for (const edge of data.edges) {
          await session.run(
            `
            MATCH (a)
            WHERE (a:Company AND a.companyId = $from)
               OR (a:Person AND a.personId = $from)

            MATCH (b)
            WHERE (b:Company AND b.companyId = $to)
               OR (b:Person AND b.personId = $to)

            MERGE (a)-[r:OWNS]->(b)
            SET r.pct = $pct
            `,
            {
              from: edge.from,
              to: edge.to,
              pct: edge.pct,
            }
          );
        }

        console.log("✅ Graph updated in Memgraph");
      } catch (err) {
        console.error("❌ Error processing event:", err);
      }
    },
  });
};

run().catch(console.error);
