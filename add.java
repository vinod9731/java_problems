<script>

async function loadQueue() {

  const res = await fetch('/api/queues/kyc-officer');
  const data = await res.json();

  console.log("API RESPONSE:", data);

  const table = document.querySelector("#queueTable tbody");
  table.innerHTML = "";

  // ✅ Correct usage
  const cases = data.cases || [];

  cases.forEach(caseItem => {

    const score = caseItem.riskScore?.low || 0;

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
