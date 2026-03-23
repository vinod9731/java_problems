#!/bin/bash

echo "🚀 Starting Kafka setup..."

# Step 1: Start Kafka
echo "📦 Starting Kafka container..."
docker-compose up -d kafka

echo "⏳ Waiting for Kafka to be ready..."
sleep 15

# Step 2: Create topics
echo "🧱 Creating topics..."

docker exec -it kafka kafka-topics.sh --create \
--topic kyb.ownership.submitted \
--bootstrap-server kafka:29092 \
--partitions 1 --replication-factor 1

docker exec -it kafka kafka-topics.sh --create \
--topic kyb.ubo.discovered \
--bootstrap-server kafka:29092 \
--partitions 1 --replication-factor 1

docker exec -it kafka kafka-topics.sh --create \
--topic kyb.screening.completed \
--bootstrap-server kafka:29092 \
--partitions 1 --replication-factor 1

docker exec -it kafka kafka-topics.sh --create \
--topic kyb.risk.scored \
--bootstrap-server kafka:29092 \
--partitions 1 --replication-factor 1

# Step 3: List topics
echo "📋 Verifying topics..."
docker exec -it kafka kafka-topics.sh --list --bootstrap-server kafka:29092

echo "✅ Kafka topics setup completed!"
