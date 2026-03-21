Perfect 👌 your code is almost correct — I can see the issue clearly now from your screenshot.


---

🚨 ROOT CAUSE (IMPORTANT)

In your query, you are MISSING case filter ❌

MATCH path = (p:Person)-[rels:OWNS*1..5]->(c:Company)

👉 This means:

It calculates UBO across ALL cases

But your API filters by caseId

So API returns → ❌ empty



---

✅ FIX (JUST ADD THIS LINE)

Update your query like this 👇

const result = await session.run(
  `
  MATCH path = (p:Person)-[rels:OWNS*1..5]->(c:Company)
  WHERE p.caseId = $caseId AND c.caseId = $caseId   // 🔥 ADD THIS

  WITH p, c,
    reduce(total = 1.0, r IN rels | total * (r.pct / 100.0)) AS effectivePct

  WHERE effectivePct >= 0.25

  MERGE (p)-[r:IS_UBO_OF]->(c)
  SET r.effectivePct = effectivePct

  RETURN p.name AS person, c.name AS company, effectivePct
  ORDER BY effectivePct DESC
  `,
  { caseId }
);


---

🔥 ALSO VERIFY (VERY IMPORTANT)

👉 Do your nodes have caseId?

Run in Memgraph:

MATCH (p:Person)
RETURN p.name, p.caseId;

👉 If caseId = null → then problem is in ownership consumer


---

✅ IF caseId is missing → FIX THIS

In your ownership-consumer.js, ensure:

MERGE (p:Person {personId: node.id})
SET p.name = node.name,
    p.caseId = $caseId   // 🔥 MUST

MERGE (c:Company {companyId: node.id})
SET c.name = node.name,
    c.caseId = $caseId   // 🔥 MUST


---

🔁 AFTER FIX

docker-compose down
docker-compose up --build
./scripts/seed-ownership.sh


---

✅ FINAL EXPECTED RESULT

API:

{
  "caseId": "case-1",
  "count": 2,
  "ubos": [...]
}

Memgraph:

MATCH (p)-[r:IS_UBO_OF]->(c)
RETURN p.name, c.name, r.effectivePct;

➡️ Should return data ✅


---

🧠 WHY THIS HAPPENED

Component	Behavior

Consumer	Created UBO without case filter
API	Filters by caseId
Result	No match → empty



---

🚀 FINAL STATUS

👉 Your architecture is correct
👉 Your logic is correct
👉 Just missing caseId consistency


---

If you want next:

👉 I can review your full flow (Intern A + B complete check)
👉 Or help you prepare explanation for demo

Just tell 👍
