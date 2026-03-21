Good progress 👍 — your pipeline is running, but the issue is very clear from your screenshots.


---

❌ WHAT’S THE PROBLEM

1️⃣ Kafdrop shows:

"ubos": []

👉 That means: ✔ UBO processor is running
❌ But no UBO detected


---

2️⃣ Memgraph query:

MATCH (p:Person)-[r:IS_UBO_OF]->(c:Company)
RETURN p.name, r.effectivePct, c.name;

👉 Result:

No data returned ❌


---

🧠 ROOT CAUSE

👉 Your UBO query is NOT creating relationships

So:

OWNS graph ✅ exists
IS_UBO_OF ❌ not created


---

🔥 MOST COMMON REASONS (YOUR CASE)


---

❌ 1. caseId mismatch

Your query has:

WHERE p.caseId = $caseId AND c.caseId = $caseId

👉 But your nodes probably don’t have caseId stored


---

👉 VERIFY THIS NOW

Run in Memgraph:

MATCH (n) RETURN n LIMIT 10;

👉 Check:

Do nodes have caseId?

Or only id, name, type?



---

✅ FIX OPTION 1 (FASTEST)

Remove caseId filter

MATCH path = (p:Person)-[rels:OWNS*1..5]->(c:Company)

WITH p, c,
  reduce(total = 1.0, r IN rels | total * (r.pct / 100.0)) AS effectivePct

WHERE effectivePct >= 0.25

MERGE (p)-[r:IS_UBO_OF]->(c)
SET r.effectivePct = effectivePct

RETURN p.name, c.name, effectivePct;


---

✅ FIX OPTION 2 (CORRECT ARCHITECTURE)

👉 Add caseId when inserting nodes (BEST PRACTICE)

In your ownership-consumer.js, update:

MERGE (n:Person {id: node.id})
SET n.name = node.name,
    n.caseId = event.caseId

Same for Company.


---

⚡ ALSO CHECK THIS


---

❌ Your UBO processor might not be running query

👉 Add log inside ubo-processor.js:

console.log("Running UBO query for case:", caseId);


---

❌ Or query not executed

Make sure you have:

await session.run(query, { caseId });


---

🚀 DEBUG STEP-BY-STEP


---

✅ Step 1: Check OWNS graph

MATCH (a)-[r:OWNS]->(b)
RETURN a, r, b;

✔ If empty → ingestion issue
✔ If present → go next


---

✅ Step 2: Run UBO query manually

Paste this in Memgraph:

MATCH path = (p:Person)-[rels:OWNS*1..5]->(c:Company)
WITH p, c,
  reduce(total = 1.0, r IN rels | total * (r.pct / 100.0)) AS effectivePct
RETURN p.name, c.name, effectivePct
ORDER BY effectivePct DESC;

👉 If this returns values → logic is correct


---

✅ Step 3: Then add threshold

WHERE effectivePct >= 0.25


---

✅ Step 4: Then MERGE


---

🎯 WHY YOUR ubos: []

Because:

👉 No IS_UBO_OF relationships created
👉 So processor returns empty list


---

🔥 FINAL ANSWER

👉 Your system is working ✅
👉 But UBO logic is not matching data


---

⚡ QUICK FIX (DO THIS NOW)

1. Remove caseId filter


2. Run query manually


3. Confirm results


4. Restart ubo-processor




---

👉 NEXT STEP

If you want, I can give you:

✅ Fully corrected ubo-processor.js
✅ With logs + debug + correct query
✅ End-to-end working version

Just say:
👉 “give final ubo processor code” 🚀
