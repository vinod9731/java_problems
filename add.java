const { consumer, producer } = require('../config/kafka');

async function startScreeningProcessor() {
  await consumer.connect();
  await producer.connect();

  await consumer.subscribe({
    topic: 'kyb.ubo.discovered',
    fromBeginning: true
  });

  await consumer.run({
    eachMessage: async ({ message }) => {
      try {
        const payload = JSON.parse(message.value.toString());
        const { caseId, ubos } = payload;

        console.log(`Screening case ${caseId}`);

        // Deterministic screening rule (simulate sanctions list)
        const sanctionsList = ['John Doe', 'Vladimir Test'];

        const screeningResults = ubos.map(ubo => ({
          personId: ubo.personId,
          name: ubo.name,
          hit: sanctionsList.includes(ubo.name)
        }));

        await producer.send({
          topic: 'kyb.screening.completed',
          messages: [
            {
              value: JSON.stringify({
                caseId,
                screeningResults,
                timestamp: new Date().toISOString()
              })
            }
          ]
        });

        console.log(`Screening completed for ${caseId}`);

      } catch (error) {
        console.error('Screening processor error:', error);
      }
    }
  });
}

module.exports = { startScreeningProcessor };



"topics": "kyb.case.created,kyb.ownership.submitted,kyb.ubo.discovered,kyb.screening.completed"


  "neo4j.cypher.topic.kyb.screening.completed": "WITH event UNWIND event.screeningResults AS result MATCH (p:Person {id: result.personId}) MERGE (c:Check {type:'SCREENING', caseId:event.caseId, personId:result.personId}) SET c.status = CASE WHEN result.hit THEN 'HIT' ELSE 'CLEAR' END, c.details = 'Deterministic name screening' MERGE (p)-[:HAS_CHECK]->(c)"


  const { consumer, producer } = require('../config/kafka');

async function startRiskProcessor() {
  await consumer.connect();
  await producer.connect();

  await consumer.subscribe({
    topic: 'kyb.screening.completed',
    fromBeginning: true
  });

  await consumer.run({
    eachMessage: async ({ message }) => {
      try {
        const payload = JSON.parse(message.value.toString());
        const { caseId, screeningResults } = payload;

        console.log(`Calculating risk for case ${caseId}`);

        let score = 0;
        let factors = [];

        screeningResults.forEach(result => {
          if (result.hit) {
            score += 50;
            factors.push(`Sanctions hit: ${result.name}`);
          }
        });

        if (score === 0) {
          score = 10;
          factors.push('No screening hits');
        }

        await producer.send({
          topic: 'kyb.risk.scored',
          messages: [
            {
              value: JSON.stringify({
                caseId,
                score,
                factors,
                timestamp: new Date().toISOString()
              })
            }
          ]
        });

        console.log(`Risk scored for ${caseId}`);

      } catch (error) {
        console.error('Risk processor error:', error);
      }
    }
  });
}

module.exports = { startRiskProcessor };



"neo4j.cypher.topic.kyb.risk.scored": "MATCH (c:KybCase {id:event.caseId}) MERGE (r:Risk {caseId:event.caseId}) SET r.score = event.score, r.factors = event.factors MERGE (c)-[:HAS_RISK]->(r)"
