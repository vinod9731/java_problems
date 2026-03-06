<!DOCTYPE html>
<html>

<head>
<meta charset="UTF-8">
<title>Starburst AML Demo</title>
<link rel="stylesheet" href="style.css">
</head>

<body>

<header>
<h1>Starburst / Trino AML Monitoring Console</h1>
</header>

<div class="top-section">

<div class="panel">
<h2>Trino Dashboard</h2>
<iframe src="http://localhost:8080"></iframe>
</div>

<div class="panel">
<h2>MinIO Console</h2>
<iframe src="http://localhost:9101"></iframe>
</div>

</div>

<div class="terminal-section">

<h2>SQL Terminal</h2>

<div id="output"></div>

<div class="input-area">

<span>SQL ></span>

<input type="text" id="queryInput" placeholder="Enter SQL query">

<button onclick="runQuery()">Run</button>

</div>

</div>

<script src="script.js"></script>

</body>
</html>







  body{
margin:0;
font-family:Segoe UI;
background:#f5f7fa;
}

header{
background:#0b7a3b;
color:white;
text-align:center;
padding:15px;
}

.top-section{
display:flex;
height:60vh;
}

.panel{
flex:1;
display:flex;
flex-direction:column;
border-right:2px solid #ccc;
}

.panel h2{
margin:0;
padding:10px;
background:#222;
color:white;
}

iframe{
flex:1;
border:none;
}

.terminal-section{
height:40vh;
background:black;
color:#00ff88;
display:flex;
flex-direction:column;
padding:10px;
}

.terminal-section h2{
color:white;
margin:0 0 10px 0;
}

#output{
flex:1;
overflow-y:auto;
font-family:monospace;
}

.input-area{
display:flex;
border-top:1px solid #444;
padding-top:10px;
}

input{
flex:1;
background:black;
color:#00ff88;
border:1px solid #333;
padding:6px;
font-family:monospace;
}

button{
margin-left:10px;
background:#0b7a3b;
border:none;
color:white;
padding:6px 12px;
cursor:pointer;
}

button:hover{
background:#095e2d;
}





function runQuery(){

let query = document.getElementById("queryInput").value;
let output = document.getElementById("output");

if(query.trim() === "") return;

let line = document.createElement("div");
line.innerHTML = "<span style='color:#00ff88'>SQL ></span> " + query;

output.appendChild(line);

let result = document.createElement("div");
result.innerHTML = "Executing query on Trino...";

output.appendChild(result);

document.getElementById("queryInput").value="";

output.scrollTop = output.scrollHeight;

}


