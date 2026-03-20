const neo4j = require("neo4j-driver");

const driver = neo4j.driver(
  process.env.MEMGRAPH_HOST
    ? `bolt://${process.env.MEMGRAPH_HOST}:7687`
    : "bolt://localhost:7687",
  neo4j.auth.basic("", "")
);

const getSession = () => {
  return driver.session();
};

module.exports = { getSession };
