<!DOCTYPE html>
<html>
<head>
  <title>KYC Officer Queue</title>
  <style>
    body { font-family: Arial; margin: 20px; }
    table { border-collapse: collapse; width: 70%; }
    th, td { border: 1px solid #ccc; padding: 10px; text-align: center; }
    th { background-color: #f2f2f2; }
    .high { color: red; font-weight: bold; }
    .medium { color: orange; }
    .low { color: green; }
  </style>
</head>

<body>

<h2>KYC Officer Queue</h2>

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

function getRiskClass(score) {
  if (score >= 70) return 'high';
  if (score >= 40) return 'medium';
  return 'low';
}

async function loadQueue() {

  const res = await fetch('/api/queues/kyc-officer');
  const data = await res.json();

  const table = document.querySelector("#queueTable tbody");
  table.innerHTML = "";

  data.cases.forEach(caseItem => {

    const row = document.createElement("tr");

    const riskClass = getRiskClass(caseItem.riskScore);

    row.innerHTML = `
      <td>
        <a href="case.html?id=${caseItem.caseId}">
          ${caseItem.caseId}
        </a>
      </td>
      <td>NEEDS_REVIEW</td>
      <td class="${riskClass}">${caseItem.riskScore}</td>
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
  <style>
    body { font-family: Arial; margin: 20px; }
    button { margin: 5px; padding: 10px; }
  </style>
</head>

<body>

<h2>Case Details</h2>

<div id="caseInfo"></div>

<h3>Decision</h3>

<button onclick="makeDecision('approve')">Approve</button>
<button onclick="makeDecision('decline')">Decline</button>
<button onclick="makeDecision('escalate')">Escalate</button>

<script>

const params = new URLSearchParams(window.location.search);
const caseId = params.get("id");

async function loadCase() {

  const res = await fetch(`/api/cases/${caseId}`);
  const data = await res.json();

  document.getElementById("caseInfo").innerHTML = `
    <p><b>Case ID:</b> ${data.caseId}</p>
    <p><b>Company:</b> ${data.company}</p>
    <p><b>UBOs:</b> ${data.ubos.join(', ')}</p>
    <p><b>Risk Score:</b> ${data.riskScore}</p>
    <p><b>Factors:</b> ${data.factors.join(', ')}</p>
  `;
}

async function makeDecision(decision) {

  await fetch(`/api/cases/${caseId}/decision`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      decision: decision,
      decisionMaker: "officer1"
    })
  });

  alert("Decision submitted!");

}

loadCase();

</script>

</body>
</html>

  
