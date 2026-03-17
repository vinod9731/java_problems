<!DOCTYPE html>
<html>
<head>
  <title>KYC Officer Queue</title>
</head>

<body>

<h2>KYC Officer Queue</h2>

<table border="1" id="queueTable">
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

console.log("SCRIPT LOADED");

async function loadQueue() {

  console.log("Fetching API...");

  const res = await fetch('/api/queues/kyc-officer');
  const data = await res.json();

  console.log("API DATA:", data);

  const table = document.querySelector("#queueTable tbody");

  data.cases.forEach(caseItem => {

    const score = caseItem.riskScore.low;

    const row = document.createElement("tr");

    row.innerHTML = `
      <td>
        <a href="case.html?id=${caseItem.caseId}">
          ${caseItem.caseId}
        </a>
      </td>
      <td>NEEDS_REVIEW</td>
      <td>${score}</td>
    `;

    table.appendChild(row);
  });
}

loadQueue();

</script>

</body>
</html>
