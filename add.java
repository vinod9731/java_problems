"neo4j.cypher.topic.kyb.risk.scored":
"MATCH (c:KybCase {id:value.caseId}) MERGE (r:Risk {caseId:value.caseId}) SET r.score = value.score, r.factors = value.factors MERGE (c)-[:HAS_RISK]->(r)"


  curl -X DELETE http://localhost:8083/connectors/neo4j-sink-kyb


  
