Perfect timing 👍 — this is exactly what you need to debug + explain your project clearly.

I’ll give you a full verification checklist (step-by-step) with: ✔ Query
✔ What it checks
✔ Expected result


---

🚀 🔍 MEMGRAPH VERIFICATION QUERIES (FULL FLOW)


---

🧪 1. Check if ANY data exists

MATCH (n) RETURN n LIMIT 20;

✅ What it verifies:

👉 Whether your consumer is writing data to Memgraph

🎯 Expected:

Nodes like Person, Company, KybCase



---

👤 2. Verify PERSON nodes

MATCH (p:Person)
RETURN p.name, p.personId, p.caseId;

✅ What it verifies:

👉 Person nodes are created correctly

🎯 Expected:

John | person-1 | case-1
Alice | person-2 | case-1


---

🏢 3. Verify COMPANY nodes

MATCH (c:Company)
RETURN c.name, c.companyId, c.caseId;

✅ What it verifies:

👉 Company nodes exist and linked to case


---

🔗 4. Verify OWNERSHIP relationships

MATCH (a)-[r:OWNS]->(b)
RETURN a.name, b.name, r.pct;

✅ What it verifies:

👉 Ownership graph is built correctly

🎯 Example:

John → ABC Ltd → 50%
ABC Ltd → XYZ Ltd → 60%


---

📂 5. Verify CASE node

MATCH (c:KybCase)
RETURN c.caseId, c.status;

✅ What it verifies:

👉 Case node creation


---

🔗 6. Verify Case → Company link

MATCH (c:KybCase)-[:TARGET]->(comp:Company)
RETURN c.caseId, comp.name;

✅ What it verifies:

👉 Root company connection


---

🔥 7. Verify UBO calculation logic (RAW)

MATCH path = (p:Person)-[rels:OWNS*1..5]->(c:Company)
WITH p, c,
     reduce(total = 1.0, r IN rels | total * (r.pct / 100.0)) AS effectivePct
RETURN p.name, c.name, effectivePct
ORDER BY effectivePct DESC;

✅ What it verifies:

👉 Ownership chain calculation works


---

🎯 8. Verify FINAL UBO relationships

MATCH (p:Person)-[r:IS_UBO_OF]->(c:Company)
RETURN p.name, c.name, r.effectivePct
ORDER BY r.effectivePct DESC;

✅ What it verifies:

👉 Final UBO output stored in graph


---

🧠 9. Verify CASE-FILTERED UBO (IMPORTANT)

MATCH (p:Person)-[r:IS_UBO_OF]->(c:Company)
WHERE p.caseId = "case-1" AND c.caseId = "case-1"
RETURN p.name, c.name, r.effectivePct;

✅ What it verifies:

👉 Your API logic correctness


---

🧪 10. Check if data is missing caseId (COMMON BUG)

MATCH (n)
WHERE n.caseId IS NULL
RETURN labels(n), n;

✅ What it verifies:

👉 Why API returns empty


---

🔍 11. Debug missing relationships

MATCH (p:Person)
WHERE NOT (p)-[:OWNS]->()
RETURN p.name;

✅ What it verifies:

👉 Persons not connected → no UBO


---

📊 12. Count everything (quick health check)

MATCH (n)
RETURN labels(n), count(n);


---

🚀 🔁 KAFKA VERIFICATION


---

📥 13. Check ownership events

👉 Topic: kyb.ownership.submitted

Expected:

{
  "caseId": "case-1",
  "nodes": [...],
  "edges": [...]
}


---

📤 14. Check UBO output

👉 Topic: kyb.ubo.discovered

Expected:

{
  "caseId": "case-1",
  "ubos": [
    {
      "person": "John",
      "company": "XYZ Ltd",
      "effectivePct": 0.3
    }
  ]
}


---

🌐 API VERIFICATION


---

🔗 15. API check

GET http://localhost:3001/cases/case-1/ubo

✅ Expected:

{
  "caseId": "case-1",
  "count": 2,
  "ubos": [...]
}


---

🧠 FINAL DEBUG FLOW (VERY IMPORTANT)

If something fails, follow this order:

1. Kafka topic → data present? ✅
2. ownership-consumer → writing? ✅
3. Memgraph → nodes exist? ✅
4. Relationships → created? ✅
5. UBO query → working? ✅
6. UBO processor → emitting? ✅
7. API → reading correctly? ✅

