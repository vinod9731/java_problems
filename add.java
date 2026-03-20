Yes 👍 — 100% you should dockerize both workbench-api and consumer.
This is the correct production-style setup.

Right now you’re manually doing:

node src/index.js
node ownership-consumer.js

👉 We’ll automate this using Docker Compose 🔥


---

🧠 FINAL ARCHITECTURE (after this)

workbench-api (container)
        ↓
Kafka
        ↓
consumer (container)
        ↓
Memgraph


---

🚀 STEP 1 — Create Dockerfile (API)

📁 services/workbench-api/Dockerfile

FROM node:18

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

CMD ["node", "src/index.js"]


---

🚀 STEP 2 — Create Dockerfile (Consumer)

📁 services/consumers/Dockerfile

FROM node:18

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

CMD ["node", "src/ownership-consumer.js"]


---

🚀 STEP 3 — Update docker-compose.yml

Add these 2 services 👇

workbench-api:
    build: ./services/workbench-api
    container_name: workbench-api
    depends_on:
      - kafka
    ports:
      - "3001:3001"
    environment:
      KAFKA_BROKER: kafka:29092
    command: node src/index.js

  consumer:
    build: ./services/consumers
    container_name: consumer
    depends_on:
      - kafka
      - memgraph
    environment:
      KAFKA_BROKER: kafka:29092
      MEMGRAPH_HOST: memgraph
    command: node src/ownership-consumer.js


---

🔥 IMPORTANT CHANGES IN CODE

✅ API (producer.js)

Replace:

brokers: ["localhost:9092"]

👉 with:

brokers: [process.env.KAFKA_BROKER || "localhost:9092"]


---

✅ Consumer

Replace:

brokers: ["localhost:9092"]

👉 with:

brokers: [process.env.KAFKA_BROKER || "localhost:9092"]


---

✅ Memgraph connection

Replace:

bolt://localhost:7687

👉 with:

bolt://memgraph:7687


---

🚀 STEP 4 — Rebuild Everything

docker-compose down
docker-compose up --build


---

🔍 STEP 5 — Verify

Check containers:

docker ps

You should see:

kafka
kafka-connect
kafdrop
memgraph
workbench-api
consumer


---

🧪 STEP 6 — Test

./scripts/seed-ownership.sh


---

🎯 EXPECTED

✔ No manual node commands needed
✔ Everything auto-starts
✔ Consumer always listening
✔ Graph updates automatically


---

🔥 WHY THIS IS IMPORTANT

Without Docker ❌	With Docker ✅

manual start	auto start
error-prone	consistent
not scalable	production-ready



---

🚀 BONUS (VERY IMPORTANT)

Later you can:

deploy this to cloud ☁️

scale consumers 🔥

add more services



---

🎯 NEXT STEP

After running docker:

👉 tell me “All containers running + graph working”

Then we move to:

🔥 UBO calculation (core business logic)
🔥 graph traversal


---

You’re now building a real event-driven microservices system 💯
