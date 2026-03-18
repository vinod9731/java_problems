version: "3.8"

services:

  kafka:
    image: docker-remote.artifactory.cib.echonet/confluentinc/cp-kafka:7.4.0
    container_name: kafka
    ports:
      - "9092:9092"
    environment:
      KAFKA_NODE_ID: 1
      KAFKA_PROCESS_ROLES: broker,controller
      KAFKA_CONTROLLER_QUORUM_VOTERS: 1@kafka:9093

      # 🔥 DUAL LISTENER FIX
      KAFKA_LISTENERS: PLAINTEXT://:9092,PLAINTEXT_INTERNAL://:29092,CONTROLLER://:9093
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092,PLAINTEXT_INTERNAL://kafka:29092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_INTERNAL:PLAINTEXT,CONTROLLER:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT_INTERNAL
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER

      KAFKA_LOG_DIRS: /tmp/kraft-combined-logs
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: "true"

      # 🔥 SINGLE NODE FIX
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1

      CLUSTER_ID: "MkU3OEVBNTcwNTJENDM2Qk"

  kafka-connect:
    image: docker-remote.artifactory.cib.echonet/confluentinc/cp-kafka-connect:6.2.0
    container_name: kafka-connect
    depends_on:
      - kafka
    ports:
      - "8083:8083"
    environment:
      # 🔥 IMPORTANT FIX
      CONNECT_BOOTSTRAP_SERVERS: kafka:29092

      CONNECT_REST_PORT: 8083
      CONNECT_REST_ADVERTISED_HOST_NAME: kafka-connect

      CONNECT_GROUP_ID: "connect-group"
      CONNECT_CONFIG_STORAGE_TOPIC: connect-configs
      CONNECT_OFFSET_STORAGE_TOPIC: connect-offsets
      CONNECT_STATUS_STORAGE_TOPIC: connect-status

      CONNECT_CONFIG_STORAGE_REPLICATION_FACTOR: 1
      CONNECT_OFFSET_STORAGE_REPLICATION_FACTOR: 1
      CONNECT_STATUS_STORAGE_REPLICATION_FACTOR: 1

      CONNECT_KEY_CONVERTER: org.apache.kafka.connect.storage.StringConverter
      CONNECT_VALUE_CONVERTER: org.apache.kafka.connect.json.JsonConverter
      CONNECT_VALUE_CONVERTER_SCHEMAS_ENABLE: "false"

      CONNECT_PLUGIN_PATH: /usr/share/java

  kafdrop:
    image: docker-remote.artifactory.cib.echonet/obsidiandynamics/kafdrop:latest
    container_name: kafdrop
    depends_on:
      - kafka
    ports:
      - "9000:9000"
    environment:
      KAFKA_BROKERCONNECT: kafka:29092

  memgraph:
    image: docker-remote.artifactory.cib.echonet/memgraph/memgraph:latest
    container_name: memgraph
    ports:
      - "7687:7687"
      - "7444:7444"

  memgraph-lab:
    image: docker-remote.artifactory.cib.echonet/memgraph/lab:latest
    container_name: memgraph-lab
    ports:
      - "3000:3000"
    depends_on:
      - memgraph
