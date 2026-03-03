const { Kafka } = require('kafkajs');

const kafka = new Kafka({
  clientId: 'workbench-api',
  brokers: [process.env.KAFKA_BROKER || 'kyb_kafka:29092'],
  retry: {
    initialRetryTime: 100,
    retries: 8
  }
});

// Shared producer (OK to share)
const producer = kafka.producer();

// Create separate consumer factory
function createConsumer(groupId) {
  return kafka.consumer({ groupId });
}

// Initialize producer once
async function initProducer() {
  await producer.connect();
  console.log('Kafka producer connected');
  return producer;
}

module.exports = {
  kafka,
  producer,
  createConsumer,
  initProducer
};



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
      const { caseId, ubos } = payload;

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

      screeningResults.forEach(result => {
        if (result.hit) {
          score += 50;
          factors.push(`Sanctions hit: ${result.name}`);
        }
      });

      if (score === 0) {
        score = 10;
        factors.push('No hits');
      }

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

      console.log('Risk message emitted');
    }
  });
}

module.exports = { startRiskProcessor };


              
