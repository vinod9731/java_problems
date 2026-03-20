const express = require("express");
const router = express.Router();
const { sendEvent } = require("../kafka/producer");

// ✅ POST - Submit ownership (MAIN API)
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

    // Send to Kafka
    await sendEvent("kyb.ownership.submitted", event);

    res.json({
      message: "Ownership submitted successfully ✅",
      caseId,
    });

  } catch (err) {
    console.error("❌ Error in ownership API:", err);
    res.status(500).send("Error sending ownership event");
  }
});


// ✅ GET - For browser testing (OPTIONAL)
router.get("/cases/:caseId/ownership", (req, res) => {
  const { caseId } = req.params;

  res.send(`
    🚀 Ownership API is working!

    Case ID: ${caseId}

    👉 Use POST method to submit ownership data.
    👉 Example:
    POST /cases/${caseId}/ownership
  `);
});

module.exports = router;
