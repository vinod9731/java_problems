const neo4j = require('../config/neo4j');

async function verifyOwnershipGraph(caseId) {
  const session = neo4j.driver.session();

  try {
    const result = await session.run(`
      MATCH (c:KybCase {id: $caseId})<-[:LINKED_TO]-(company:Company)
      OPTIONAL MATCH (company)<-[r:OWNS]-(owner)
      RETURN company, owner, r
    `, { caseId });

    return result.records.map(record => ({
      company: record.get('company').properties,
      owner: record.get('owner')?.properties,
      relationship: record.get('r')?.properties
    }));

  } finally {
    await session.close();
  }
}

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

    if (result.records.length === 0) return null;

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
