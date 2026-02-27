const { initProducer } = require('../config/kafka');
const { driver } = require('../config/neo4j');

let producer;

async function initializeProducer() {
  if (!producer) {
    producer = await initProducer();
  }
  return producer;
}

const submitOwnership = async (req, res) => {
  try {
    const { caseId } = req.params;
    const { owners } = req.body;

    if (!caseId || !owners || !Array.isArray(owners)) {
      return res.status(400).json({
        error: 'Case ID and owners array are required'
      });
    }

    const session = driver.session();

    try {
      // Create case
      await session.run(
        `
        MERGE (c:KybCase {id: $caseId})
        ON CREATE SET c.createdAt = datetime()
        `,
        { caseId }
      );

      // Create nodes
      for (const owner of owners) {
        if (owner.type === 'Company') {
          await session.run(
            `
            MERGE (c:Company {id: $id})
            ON CREATE SET c.name = $name,
                          c.jurisdiction = $jurisdiction
            `,
            {
              id: owner.id,
              name: owner.name,
              jurisdiction: owner.jurisdiction
            }
          );

          // Link company to case
          await session.run(
            `
            MATCH (k:KybCase {id: $caseId})
            MATCH (c:Company {id: $companyId})
            MERGE (k)-[:LINKED_TO]->(c)
            `,
            { caseId, companyId: owner.id }
          );
        } else {
          await session.run(
            `
            MERGE (p:Person {id: $id})
            ON CREATE SET p.name = $name
            `,
            {
              id: owner.id,
              name: owner.name
            }
          );
        }
      }

      // Create OWNS relationships (IMPORTANT FIX)
      for (const owner of owners) {
        if (owner.targetId && owner.ownershipPercentage) {
          await session.run(
            `
            MATCH (source {id: $sourceId})
            MATCH (target:Company {id: $targetId})
            MERGE (source)-[r:OWNS]->(target)
            SET r.percentage = $percentage,
                r.validFrom = datetime()
            `,
            {
              sourceId: owner.id,
              targetId: owner.targetId,
              percentage: owner.ownershipPercentage
            }
          );
        }
      }

    } finally {
      await session.close();
    }

    const kafkaProducer = await initializeProducer();

    await kafkaProducer.send({
      topic: 'kyb.ownership.submitted',
      messages: [{
        value: JSON.stringify({
          caseId,
          owners,
          timestamp: new Date().toISOString()
        })
      }]
    });

    res.status(202).json({
      message: 'Ownership submission initiated',
      caseId,
      status: 'ACCEPTED'
    });

  } catch (error) {
    console.error('Error submitting ownership:', error);
    res.status(500).json({
      error: 'Failed to submit ownership',
      details: error.message
    });
  }
};

module.exports = { submitOwnership };










const neo4j = require('neo4j-driver');

const driver = neo4j.driver(
  process.env.NEO4J_URI || 'bolt://neo4j:7687',
  neo4j.auth.basic(
    process.env.NEO4J_USER || 'neo4j',
    process.env.NEO4J_PASSWORD || 'password'
  )
);

const runUBOQuery = async (caseId) => {
  const session = driver.session();

  try {
    const query = `
      MATCH (c:KybCase {id: $caseId})-[:LINKED_TO]->(root:Company)

      MATCH path = (person:Person)-[rels:OWNS*1..5]->(root)

      WITH person,
           reduce(pct = 1.0, r IN rels | pct * (r.percentage / 100.0)) AS effectivePct

      WHERE effectivePct >= 0.25

      RETURN person, effectivePct
    `;

    return await session.run(query, { caseId });

  } finally {
    await session.close();
  }
};

module.exports = { driver, runUBOQuery };










const { driver, runUBOQuery } = require('../config/neo4j');

const processUBODiscovery = async (caseId) => {
  const result = await runUBOQuery(caseId);

  const session = driver.session();

  try {
    const ubos = [];

    for (const record of result.records) {
      const person = record.get('person');
      const effectivePct = record.get('effectivePct');

      await session.run(
        `
        MATCH (p:Person {id: $personId})
        MATCH (c:KybCase {id: $caseId})-[:LINKED_TO]->(company:Company)
        MERGE (p)-[r:IS_UBO_OF]->(company)
        SET r.effectivePct = $effectivePct
        `,
        {
          personId: person.properties.id,
          caseId,
          effectivePct
        }
      );

      ubos.push({
        personId: person.properties.id,
        name: person.properties.name,
        effectivePct
      });
    }

    return {
      caseId,
      ubos,
      timestamp: new Date().toISOString()
    };

  } finally {
    await session.close();
  }
};

module.exports = { processUBODiscovery };








const { consumer, producer } = require('../config/kafka');
const { processUBODiscovery } = require('./ubo.service');

async function startUBOProcessor() {

  await consumer.connect();
  await producer.connect();

  await consumer.subscribe({
    topic: 'kyb.ownership.submitted',
    fromBeginning: true
  });

  await consumer.run({
    eachMessage: async ({ message }) => {
      try {
        const payload = JSON.parse(message.value.toString());
        const { caseId } = payload;

        console.log(`Processing UBO discovery for case ${caseId}`);

        const uboEvent = await processUBODiscovery(caseId);

        await producer.send({
          topic: 'kyb.ubo.discovered',
          messages: [
            { value: JSON.stringify(uboEvent) }
          ]
        });

        console.log(`UBO discovery completed for case ${caseId}`);

      } catch (error) {
        console.error('UBO processor error:', error);
      }
    }
  });
}

module.exports = { startUBOProcessor };
      








const verifyOwnership = async (req, res) => {
  try {
    const { caseId } = req.params;
    const session = driver.session();

    try {
      const result = await session.run(
        `
        MATCH (k:KybCase {id: $caseId})
        OPTIONAL MATCH (k)-[:LINKED_TO]->(company:Company)
        OPTIONAL MATCH (company)<-[r:OWNS]-(owner)
        RETURN {
          case: properties(k),
          companies: collect(DISTINCT properties(company)),
          owners: collect(DISTINCT {
            owner: properties(owner),
            relationship: properties(r)
          })
        } AS graph
        `,
        { caseId }
      );

      const graph =
        result.records.length > 0
          ? result.records[0].get('graph')
          : null;

      if (!graph) {
        return res.status(404).json({
          success: false,
          message: `No ownership data found for case ${caseId}`
        });
      }

      res.status(200).json({
        success: true,
        caseId,
        graph
      });
    } finally {
      await session.close();
    }
  } catch (error) {
    console.error('Error verifying ownership:', error);
    res.status(500).json({
      success: false,
      error: error.message
    });
  }
};








const { driver } = require('../config/neo4j');

const getUBOs = async (req, res) => {
  const { caseId } = req.params;
  const session = driver.session();

  try {
    const result = await session.run(
      `
      MATCH (c:KybCase {id: $caseId})-[:LINKED_TO]->(company:Company)
      MATCH path = (person:Person)-[rels:OWNS*1..5]->(company)
      WITH person, company,
           reduce(pct = 1.0, r IN rels | pct * (r.percentage / 100.0)) AS effectivePct
      WHERE effectivePct >= 0.25
      RETURN person {.*, effectiveOwnership: effectivePct * 100 } AS ubo
      `,
      { caseId }
    );

    const ubos = result.records.map(r => r.get('ubo'));

    res.status(200).json({
      caseId,
      ubos
    });

  } catch (error) {
    console.error(error);
    res.status(500).json({ error: error.message });
  } finally {
    await session.close();
  }
};

module.exports = { getUBOs };

