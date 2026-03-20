const neo4j = require("neo4j-driver");

const driver = neo4j.driver(
  process.env.MEMGRAPH_URI || "bolt://memgraph:7687",
  neo4j.auth.basic("", "")
);

const getSession = () => driver.session();

module.exports = { getSession };




const express = require("express");
const router = express.Router();

const { sendEvent } = require("../kafka/producer");
const { getSession } = require("../db/memgraph");


// =====================================================
// ✅ POST - Submit Ownership (Kafka → Consumer → Memgraph)
// =====================================================
router.post("/cases/:caseId/ownership", async (req, res) => {
  try {
    console.log("🔥 Ownership API HIT");

    const { caseId } = req.params;
    const payload = req.body;

    const event = {
      caseId,
      ...payload,
      timestamp: new Date().toISOString(),
    };

    // 🔥 Send event to Kafka
    await sendEvent("kyb.ownership.submitted", event);

    res.json({
      message: "Ownership submitted successfully ✅",
      caseId,
    });

  } catch (err) {
    console.error("❌ Error in ownership POST:", err);
    res.status(500).send("Error sending ownership event");
  }
});


// =====================================================
// ✅ GET - Fetch Ownership Graph from Memgraph
// =====================================================
router.get("/cases/:caseId/ownership", async (req, res) => {
  const { caseId } = req.params;
  const session = getSession();

  try {
    console.log("📥 Fetching ownership for:", caseId);

    const result = await session.run(
      `
      MATCH (c:KybCase {caseId: $caseId})-[:TARGET]->(comp:Company)
      OPTIONAL MATCH (a)-[r:OWNS]->(b)
      RETURN c, comp, a, r, b
      `,
      { caseId }
    );

    const data = result.records.map(record => ({
      case: record.get("c")?.properties || null,
      company: record.get("comp")?.properties || null,
      from: record.get("a")?.properties || null,
      relation: record.get("r")?.properties || null,
      to: record.get("b")?.properties || null,
    }));

    res.json({
      caseId,
      count: data.length,
      data,
    });

  } catch (err) {
    console.error("❌ Error fetching ownership:", err);
    res.status(500).send("Error fetching ownership data");
  } finally {
    await session.close();
  }
});


// =====================================================
// ✅ OPTIONAL - Simple Health Message (Browser Friendly)
// =====================================================
router.get("/cases/:caseId/ownership/info", (req, res) => {
  const { caseId } = req.params;

  res.send(`
    🚀 Ownership API is working!

    Case ID: ${caseId}

    👉 Use POST method to submit ownership data
    👉 Use GET /cases/${caseId}/ownership to fetch graph
  `);
});


module.exports = router;
