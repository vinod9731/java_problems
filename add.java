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

    const score = caseItem.riskScore?.low || 0;

    const row = document.createElement("tr");

    const riskClass = getRiskClass(score);

    row.innerHTML = `
      <td>
        <a href="case.html?id=${caseItem.caseId}">
          ${caseItem.caseId}
        </a>
      </td>
      <td>NEEDS_REVIEW</td>
      <td class="${riskClass}">${score}</td>
    `;

    table.appendChild(row);
  });
}

loadQueue();

</script>    <p><b>Case ID:</b> ${data.caseId}</p>
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

  
