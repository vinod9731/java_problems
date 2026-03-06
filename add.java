<!DOCTYPE html>
<html>

<head>
<meta charset="UTF-8">
<title>Trino Query Console</title>
<link rel="stylesheet" href="style.css">
</head>

<body>

<header>
<h1>Starburst / Trino SQL Console</h1>
</header>

<div class="main-container">

<!-- LEFT PANEL -->
<div class="trino-panel">

<div class="panel-header">
Trino Dashboard
</div>

<iframe src="http://localhost:8080"></iframe>

</div>

<!-- RIGHT PANEL -->
<div class="terminal-panel">

<div class="panel-header">
SQL Terminal
</div>

<div id="terminalOutput" class="terminal-output"></div>

<div class="terminal-input">

<span class="prompt">SQL ></span>

<input type="text" id="queryInput" placeholder="Enter SQL Query">

<button onclick="runQuery()">Run</button>

</div>

</div>

</div>

<script src="script.js"></script>

</body>

</html>






  body{
margin:0;
font-family:Segoe UI;
background:linear-gradient(120deg,#1f2937,#111827);
color:white;
}

header{
text-align:center;
padding:15px;
background:#111;
border-bottom:2px solid #0b7a3b;
}

h1{
margin:0;
font-size:22px;
color:#22c55e;
}

.main-container{
display:flex;
height:90vh;
}

.trino-panel{
flex:1.4;
display:flex;
flex-direction:column;
border-right:2px solid #333;
}

.terminal-panel{
flex:1;
display:flex;
flex-direction:column;
background:#000;
}

.panel-header{
padding:10px;
background:#111827;
color:#22c55e;
font-weight:bold;
border-bottom:1px solid #333;
}

iframe{
flex:1;
border:none;
background:white;
}

.terminal-output{
flex:1;
padding:10px;
overflow-y:auto;
font-family:monospace;
color:#00ff9c;
}

.terminal-input{
display:flex;
padding:10px;
border-top:1px solid #333;
background:#111;
}

.prompt{
margin-right:10px;
color:#22c55e;
}

input{
flex:1;
background:#000;
color:#00ff9c;
border:1px solid #333;
padding:6px;
font-family:monospace;
}

button{
margin-left:10px;
background:#22c55e;
border:none;
padding:8px 14px;
cursor:pointer;
font-weight:bold;
}

button:hover{
background:#16a34a;
}







function runQuery(){

let input = document.getElementById("queryInput").value;
let terminal = document.getElementById("terminalOutput");

if(input.trim() === "") return;

let query = document.createElement("div");
query.innerHTML = "<span style='color:#22c55e'>SQL ></span> " + input;

terminal.appendChild(query);

let result = document.createElement("div");
result.innerHTML = "Executing query on Trino...";

terminal.appendChild(result);

document.getElementById("queryInput").value = "";

terminal.scrollTop = terminal.scrollHeight;

}
