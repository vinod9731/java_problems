"neo4j.cypher.topic.kyb.risk.scored":
"MATCH (c:KybCase {id:value.caseId}) MERGE (r:Risk {caseId:value.caseId}) SET r.score = value.score, r.factors = value.factors MERGE (c)-[:HAS_RISK]->(r)"


  curl -X DELETE http://localhost:8083/connectors/neo4j-sink-kyb


  

curl -X POST -H "Content-Type: application/json" \
--data @connect/neo4j-sink-kyb.json \
http://localhost:8083/connectors
