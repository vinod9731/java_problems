async function getOfficerQueue() {
  const session = neo4j.driver.session();

  try {
    const result = await session.run(`
      MATCH (c:KybCase)-[:HAS_RISK]->(r:Risk)
      RETURN c.id AS caseId, r.score AS riskScore
      ORDER BY r.score DESC
    `);

    return result.records.map(record => ({
      caseId: record.get("caseId"),
      riskScore: record.get("riskScore")
    }));

  } finally {
    await session.close();
  }
}

async function getCaseDetails(caseId) {
  const session = neo4j.driver.session();

  try {
    const result = await session.run(`
      MATCH (c:KybCase {id:$caseId})-[:LINKED_TO]->(company:Company)
      OPTIONAL MATCH (p:Person)-[:IS_UBO_OF]->(company)
      OPTIONAL MATCH (c)-[:HAS_RISK]->(r:Risk)
      RETURN company.name AS company,
             collect(p.name) AS ubos,
             r.score AS riskScore,
             r.factors AS factors
    `,{caseId});

    const record = result.records[0];

    return {
      caseId,
      company: record.get("company"),
      ubos: record.get("ubos"),
      riskScore: record.get("riskScore"),
      factors: record.get("factors")
    };

  } finally {
    await session.close();
  }
}

module.exports = {
  verifyOwnershipGraph,
  getOfficerQueue,
  getCaseDetails
};








controllers/queue.controller.js


const neo4jService = require('../services/neo4jService');

exports.getOfficerQueue = async (req,res) => {
  try {

    const cases = await neo4jService.getOfficerQueue();

    res.json({
      queue: "kyc-officer",
      cases
    });

  } catch(err) {

    res.status(500).json({
      error: err.message
    });

  }
};



case.controller

const neo4jService = require('../services/neo4jService');

exports.getCaseDetails = async (req,res) => {

  try {

    const { caseId } = req.params;

    const caseDetails = await neo4jService.getCaseDetails(caseId);

    res.json(caseDetails);

  } catch(err) {

    res.status(500).json({
      error: err.message
    });

  }
};



routes/queue.routes.js


const express = require('express');
const router = express.Router();

const queueController = require('../controllers/queue.controller');

router.get('/kyc-officer', queueController.getOfficerQueue);

module.exports = router;








const express = require('express');
const router = express.Router();

const ownershipRoutes = require('./ownership.routes');
const uboRoutes = require('./ubo.routes');

const queueRoutes = require('./queue.routes');
const caseController = require('../controllers/case.controller');
const decisionController = require('../controllers/decision.controller');

router.use('/ownership', ownershipRoutes);
router.use('/ubo', uboRoutes);

router.use('/queues', queueRoutes);

router.get('/cases/:caseId', caseController.getCaseDetails);

router.post('/cases/:caseId/decision', decisionController.submitDecision);

module.exports = router;









