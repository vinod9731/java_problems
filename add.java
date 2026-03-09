const { createConsumer, producer } = require('../config/kafka');

async function startScreeningProcessor() {

  const consumer = createConsumer('screening-group');

  await consumer.connect();
  await producer.connect();

  await consumer.subscribe({
    topic: 'kyb.ubo.discovered',
    fromBeginning: true
  });

  await consumer.run({
    eachMessage: async ({ message }) => {

      const payload = JSON.parse(message.value.toString());

      const { caseId, ubos, industry, country } = payload;

      console.log(`Screening case ${caseId}`);

      const sanctionsList = ['John Doe'];

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
              industry,
              country,
              screeningResults
            })
          }
        ]
      });

      console.log('Screening message emitted');
    }
  });
}

module.exports = { startScreeningProcessor };
