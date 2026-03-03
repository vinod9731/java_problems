"neo4j.cypher.topic.kyb.screening.completed": "UNWIND $value.screeningResults AS result MATCH (p:Person {id: result.personId}) MERGE (c:Check {type:'SCREENING', caseId:$value.caseId, personId:result.personId}) SET c.status = CASE WHEN result.hit THEN 'HIT' ELSE 'CLEAR' END, c.details = 'Deterministic name screening' MERGE (p)-[:HAS_CHECK]->(c)"



  "neo4j.cypher.topic.kyb.risk.scored": "MATCH (k:KybCase {id:$value.caseId}) MERGE (r:Risk {caseId:$value.caseId}) SET r.score = $value.score, r.factors = $value.factors MERGE (k)-[:HAS_RISK]->(r)"



  curl -X DELETE http://localhost:8083/connectors/neo4j-sink-kyb

  curl -X POST -H "Content-Type: application/json" \
--data @connect/neo4j-sink-kyb.json \
http://localhost:8083/connectors
