POC-1 (KYB Case Workbench) is a proof-of-concept system developed to support Know Your Business (KYB) compliance and investigation processes. The project focuses on building a platform that processes business onboarding data and analyzes ownership structures to identify Ultimate Beneficial Owners (UBOs). It uses a graph database (Neo4j) to represent relationships between companies and individuals and applies graph queries to calculate effective ownership. The system follows an event-driven architecture using Kafka to process and manage KYB related events. It also generates derived insights such as UBO discovery and risk scoring. A workbench interface/API is provided to allow compliance officers to review cases, analyze ownership networks, and track investigation activities. The goal is to demonstrate how graph analytics and streaming technologies can improve compliance monitoring and business risk analysis.

Scope of the Project:

Build a graph-based ownership model to represent companies, persons, and their relationships.

Implement UBO discovery using graph traversal queries to calculate effective ownership percentages.

Process KYB onboarding data using an event-driven pipeline with Kafka.

Develop APIs and workflows for case management and investigation in the KYB Workbench.

Generate risk insights and maintain an audit trail of events for compliance analysis


  Tech stack used

Graph DB: Neo4j (Cypher, Browser/Bloom).

Streaming: Apache Kafka + Kafka Connect (connectors / sinks).

API / stream processing: Node.js (KafkaJS consumer + Workbench API).

Container/dev: Docker Compose for local stack (Kafka, Connect, Neo4j, optional Kafdrop).

Optional: small Spring Boot consumer for registry verification if needed.

Languages/tools: JavaScript/Node, Cypher queries, Docker, REST. 
