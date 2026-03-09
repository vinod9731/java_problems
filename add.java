const { createConsumer, producer } = require('../config/kafka');

async function startRiskProcessor() {

  const consumer = createConsumer('risk-group');

  await consumer.connect();
  await producer.connect();

  await consumer.subscribe({
    topic: 'kyb.screening.completed',
    fromBeginning: true
  });

  await consumer.run({
    eachMessage: async ({ message }) => {

      const payload = JSON.parse(message.value.toString());
      const { caseId, screeningResults } = payload;

      let score = 0;
      let factors = [];

      // ---------- Screening Hit Check ----------
      screeningResults.forEach(result => {
        if (result.hit) {
          score += 60;
          factors.push(`screening_hit:${result.name}`);
        }
      });

      // ---------- Industry Risk ----------
      const highRiskIndustries = ['crypto', 'gambling', 'arms'];

      if (payload.industry && highRiskIndustries.includes(payload.industry.toLowerCase())) {
        score += 20;
        factors.push(`high_risk_industry:${payload.industry}`);
      }

      // ---------- Country Risk ----------
      const highRiskCountries = ['iran', 'north korea', 'syria'];

      if (payload.country && highRiskCountries.includes(payload.country.toLowerCase())) {
        score += 20;
        factors.push(`high_risk_country:${payload.country}`);
      }

      // ---------- Default Low Risk ----------
      if (score === 0) {
        score = 10;
        factors.push('low_risk_case');
      }

      // ---------- Emit Risk Event ----------
      await producer.send({
        topic: 'kyb.risk.scored',
        messages: [
          {
            value: JSON.stringify({
              caseId,
              score,
              factors
            })
          }
        ]
      });

      console.log('Risk message emitted:', { caseId, score, factors });
    }
  });
}

module.exports = { startRiskProcessor };




MATCH (c:KybCase)-[:HAS_RISK]->(r:Risk)
RETURN c.id, r.score, r.factors;


MATCH (c:KybCase)-[:HAS_RISK]->(r:Risk)
WHERE r.score >= 70
RETURN c.id, r.score, r.factors;
