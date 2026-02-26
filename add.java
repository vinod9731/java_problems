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

    return result.records.map(r => r.get('ubo'));

  } catch (error) {
    console.error("Error calculating UBOs:", error);
    return [];
  } finally {
    await session.close();
  }
}


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
