<script>

function getRiskClass(score) {
  if (score >= 70) return 'high';
  if (score >= 40) return 'medium';
  return 'low';
}

async function loadQueue() {

  const res = await fetch('/api/queues/kyc-officer');
  const data = await res.json();

  console.log("API DATA:", data);

  const table = document.querySelector("#queueTable tbody");
  table.innerHTML = "";

  if (!data.cases || data.cases.length === 0) {
    console.log("No cases found");
    return;
  }

  data.cases.forEach(caseItem => {

    const score = caseItem.riskScore?.low || 0;
    const riskClass = getRiskClass(score);

    const row = document.createElement("tr");

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

</script>
