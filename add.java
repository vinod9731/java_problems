#!/bin/bash

echo "Starting KYB Demo..."

echo "----------------------------------"
echo "Scenario 1: Clean ownership → APPROVE"
echo "----------------------------------"

# Seed ownership (clean case)
./seed.sh

# Trigger UBO pipeline
./test-ubo-processor.sh

# Officer approves
curl -X POST http://localhost:3001/api/cases/101/decision \
-H "Content-Type: application/json" \
-d '{
"decision":"approve",
"decisionMaker":"officer1"
}'

sleep 3

echo ""
echo "----------------------------------"
echo "Scenario 2: Complex ownership → ESCALATE"
echo "----------------------------------"

# Trigger complex chain case
./seed-ownership.sh

./test-ubo-processor.sh

# Officer escalates
curl -X POST http://localhost:3001/api/cases/102/decision \
-H "Content-Type: application/json" \
-d '{
"decision":"escalate",
"decisionMaker":"officer1"
}'

sleep 3

echo ""
echo "----------------------------------"
echo "Scenario 3: Screening hit → DECLINE"
echo "----------------------------------"

# Screening hit case (John Doe already in sanctions list)
./test-ubo-processor.sh

# Officer declines
curl -X POST http://localhost:3001/api/cases/103/decision \
-H "Content-Type: application/json" \
-d '{
"decision":"decline",
"decisionMaker":"officer1"
}'

sleep 3

echo ""
echo "----------------------------------"
echo "Checking Kafka Topics"
echo "----------------------------------"

docker exec kyb_kafka kafka-console-consumer \
--bootstrap-server localhost:9092 \
--topic kyb.risk.scored \
--from-beginning \
--timeout-ms 5000

echo ""
echo "----------------------------------"
echo "Check Neo4j"
echo "----------------------------------"

echo "Open Neo4j Browser:"
echo "http://localhost:7474"

echo ""
echo "Demo completed successfully!"









http://localhost:3001/api/queues/kyc-officer

http://localhost:3001/api/cases/101


const path = require('path');

app.use(express.static(path.join(__dirname, 'public')));





  <!DOCTYPE html>
<html>
<head>
<title>KYB Officer Workbench</title>
<link rel="stylesheet" href="style.css">
</head>

<body>

<h1>KYC Officer Queue</h1>

<table id="queueTable">
<thead>
<tr>
<th>Case ID</th>
<th>Status</th>
<th>Risk Score</th>
</tr>
</thead>

<tbody></tbody>

</table>

<script>

async function loadQueue() {

const res = await fetch('/api/queues/kyc-officer');
const data = await res.json();

const table = document.querySelector("#queueTable tbody");

data.forEach(caseItem => {

const row = document.createElement("tr");

row.innerHTML = `
<td><a href="case.html?id=${caseItem.caseId}">${caseItem.caseId}</a></td>
<td>${caseItem.status}</td>
<td>${caseItem.score}</td>
`;

table.appendChild(row);

});

}

loadQueue();

</script>

</body>
</html>



  <!DOCTYPE html>
<html>

<head>
<title>Case Details</title>
<link rel="stylesheet" href="style.css">
</head>

<body>

<h1>Case Details</h1>

<div id="caseInfo"></div>

<h2>Decision</h2>

<button onclick="makeDecision('approve')">Approve</button>
<button onclick="makeDecision('decline')">Decline</button>
<button onclick="makeDecision('escalate')">Escalate</button>

<script>

const params = new URLSearchParams(window.location.search);
const caseId = params.get("id");

async function loadCase() {

const res = await fetch(`/api/cases/${caseId}`);
const data = await res.json();

document.getElementById("caseInfo").innerHTML =
`
<p>Case ID: ${data.caseId}</p>
<p>Status: ${data.status}</p>
<p>Risk Score: ${data.score}</p>
`;

}

async function makeDecision(decision) {

await fetch(`/api/cases/${caseId}/decision`, {
method: "POST",
headers: { "Content-Type": "application/json" },
body: JSON.stringify({
decision: decision,
decisionMaker: "officer1"
})
});

alert("Decision submitted");

}

loadCase();

</script>

</body>

</html>



  body {
font-family: Arial;
margin: 40px;
}

table {
border-collapse: collapse;
width: 60%;
}

th, td {
border: 1px solid #ddd;
padding: 10px;
}

button {
padding: 10px 15px;
margin: 10px;
cursor: pointer;
    }


  
