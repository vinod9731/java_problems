"neo4j.cypher.topic.kyb.ubo.discovered": "WITH $value AS event UNWIND event.ubos AS ubo MATCH (k:KybCase {id:event.caseId}) MATCH (p:Person {id:ubo.personId}) MATCH (k)-[:LINKED_TO]->(company:Company) MERGE (p)-[r:IS_UBO_OF]->(company) SET r.effectiveOwnership=ubo.effectiveOwnership"

  "neo4j.cypher.topic.kyb.screening.completed": "WITH $value AS event MATCH (p:Person {id:event.personId}) MERGE (c:Check {id:event.checkId}) SET c.type='SCREENING', c.status=event.status, c.details=event.details MERGE (p)-[:HAS_CHECK]->(c)"

  "neo4j.cypher.topic.kyb.risk.scored": "WITH $value AS event MATCH (k:KybCase {id:event.caseId}) MERGE (r:Risk {caseId:event.caseId}) SET r.score=event.score, r.factors=event.factors MERGE (k)-[:HAS_RISK]->(r)"

MATCH (p:Person)-[:HAS_CHECK]->(c:Check)
RETURN p.name, c.status, c.details;
