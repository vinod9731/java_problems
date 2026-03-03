"neo4j.cypher.topic.kyb.screening.completed": "WITH $event AS event MATCH (p:Person {id:event.personId}) MERGE (c:Check {id:event.checkId}) SET c.type='SCREENING', c.status=event.status, c.details=event.details MERGE (p)-[:HAS_CHECK]->(c)"



  MATCH (p:Person)-[:HAS_CHECK]->(c:Check)
RETURN p.name, c.status, c.details;


"neo4j.cypher.topic.kyb.risk.scored": "WITH $event AS event MATCH (k:KybCase {id:event.caseId}) MERGE (r:Risk {caseId:event.caseId}) SET r.score=event.score, r.factors=event.factors MERGE (k)-[:HAS_RISK]->(r)"

  MATCH (k:KybCase)-[:HAS_RISK]->(r:Risk)
RETURN k.id, r.score, r.factors;

"neo4j.cypher.topic.kyb.screening.completed": "WITH $event AS event UNWIND event.screeningResults AS result MATCH (p:Person {id: result.personId}) MERGE (c:Check {id: event.caseId + '-' + result.personId}) SET c.type='SCREENING', c.status = CASE WHEN result.hit THEN 'HIT' ELSE 'CLEAR' END, c.details = result.name MERGE (p)-[:HAS_CHECK]->(c)"
