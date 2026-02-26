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
