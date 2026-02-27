"topics": "kyb.case.created,kyb.ownership.submitted,kyb.ubo.discovered"



  "neo4j.cypher.topic.kyb.ubo.discovered": "WITH event UNWIND event.ubos AS ubo MATCH (c:KybCase {id: event.caseId}) MATCH (p:Person {id: ubo.personId}) MATCH (c)-[:LINKED_TO]->(company:Company) MERGE (p)-[r:IS_UBO_OF]->(company) SET r.effectiveOwnership = ubo.effectiveOwnership"
