"neo4j.topic.cypher.kyb.screening.completed": "WITH $value AS event MATCH (p:Person {id:event.personId}) MERGE (c:Check {id:event.checkId}) SET c.type='SCREENING', c.status=event.status, c.details=event.details MERGE (p)-[:HAS_CHECK]->(c)"


  "neo4j.topic.cypher.kyb.risk.scored": "WITH $value AS event MATCH (k:KybCase {id:event.caseId}) MERGE (r:Risk {caseId:event.caseId}) SET r.score=event.score, r.factors=event.factors MERGE (k)-[:HAS_RISK]->(r)"


  "neo4j.topic.cypher.kyb.screening.completed": "CREATE (:DebugTest {test:'working'})"

  MATCH (d:DebugTest) RETURN d;
