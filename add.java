{
  "name": "neo4j-sink-kyb",
  "config": {
    "connector.class": "org.neo4j.connectors.kafka.sink.Neo4jConnector",
    "tasks.max": "1",
    "topics": "kyb.screening.completed",

    "neo4j.uri": "bolt://neo4j:7687",
    "neo4j.authentication.basic.username": "neo4j",
    "neo4j.authentication.basic.password": "yourpassword",
    "neo4j.database": "neo4j",

    "key.converter": "org.apache.kafka.connect.storage.StringConverter",
    "value.converter": "org.apache.kafka.connect.json.JsonConverter",
    "value.converter.schemas.enable": "false",

    "neo4j.cypher.topic.kyb.screening.completed": "CREATE (:DebugTest {value:'working'})"
  }
}
