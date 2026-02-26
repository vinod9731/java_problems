const UBOModel = require('../models/ubo.model');

const getUBOFindings = async (req, res) => {
  try {
    const caseId = req.params.caseId;

    const result = await UBOModel.getUBOFindings(caseId);

    if (!result || !result.ubos || result.ubos.length === 0) {
      return res.status(404).json({
        message: `No UBO findings found for case ${caseId}`
      });
    }

    return res.status(200).json(result);

  } catch (error) {
    console.error('Error fetching UBO findings:', error);
    return res.status(500).json({
      error: error.message
    });
  }
};

module.exports = {
  getUBOFindings
};















const { driver } = require('../config/neo4j');

class UBOModel {

  /**
   * Calculate UBOs for a company
   */
  static async calculateUBOs(companyId, caseId) {
    const session = driver.session();

    try {
      const result = await session.run(
        `
        MATCH (c:KybCase {id: $caseId})-[:LINKED_TO]->(targetCompany:Company {companyId: $companyId})
        MATCH path = (p:Person)-[owns:OWNS*1..5]->(targetCompany)
        WITH p, targetCompany,
             reduce(total=1.0, r IN relationships(path) | total * r.pct) AS pathPct
        WITH p, targetCompany,
             sum(pathPct) AS totalPct,
             count(path) AS pathsCount
        WHERE totalPct > 25
        RETURN {
          personId: p.personId,
          personName: p.fullName,
          companyId: targetCompany.companyId,
          companyName: targetCompany.name,
          effectivePct: totalPct,
          pathsCount: pathsCount
        } AS ubo
        `,
        { companyId, caseId }
      );

      return result.records.map(record => record.get('ubo'));

    } catch (error) {
      console.error("Error calculating UBOs:", error);
      return [];
    } finally {
      await session.close();
    }
  }

  /**
   * Get UBO findings for a case
   */
  static async getUBOFindings(caseId) {
    const session = driver.session();

    try {
      const result = await session.run(
        `
        MATCH (c:KybCase {id: $caseId})-[:LINKED_TO]->(company:Company)
        MATCH (p:Person)-[r:IS_UBO_OF]->(company)
        RETURN {
          personId: p.personId,
          personName: p.fullName,
          companyId: company.companyId,
          companyName: company.name,
          effectivePct: r.effectivePct,
          pathsCount: r.pathsCount
        } AS ubo
        `,
        { caseId }
      );

      return {
        caseId,
        ubos: result.records.map(record => record.get('ubo'))
      };

    } catch (error) {
      console.error("Error getting UBO findings:", error);
      return null;
    } finally {
      await session.close();
    }
  }

  /**
   * Save UBO findings to Neo4j
   */
  static async saveUBOFindings(caseId, ubos) {
    const session = driver.session();

    try {
      for (const ubo of ubos) {
        await session.run(
          `
          MATCH (c:KybCase {id: $caseId})-[:LINKED_TO]->(company:Company {companyId: $companyId})
          MATCH (p:Person {personId: $personId})
          MERGE (p)-[r:IS_UBO_OF]->(company)
          SET r.effectivePct = $effectivePct,
              r.pathsCount = $pathsCount,
              r.lastUpdated = datetime()
          `,
          {
            caseId,
            companyId: ubo.companyId,
            personId: ubo.personId,
            effectivePct: ubo.effectivePct,
            pathsCount: ubo.pathsCount
          }
        );
      }

    } catch (error) {
      console.error("Error saving UBO findings:", error);
    } finally {
      await session.close();
    }
  }
}

module.exports = UBOModel;

