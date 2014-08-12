<div id = "content">
<h1>Preferences: </h1>

$if(add)$
<br>
<h1> Add Sucsessful!</h1>
<hr>
$endif$

$if(addFailed)$
<br>
<h1> Add Failed!</h1>
<h2>Please make sure to format website URL correctly</h2>
<hr>
$endif$

<ul>
<li><a href = "/login?logout">Log Out</a> </li>
<br>
<li><a href = "/password">Change Password</a> </li>
<br>
<li><a href = "/queryHistory">Query History</a> </li>
<br>
<li><a href = "/preferences?altLayout">Change Theme (BETA)</a> </li>

$if(admin)$
<br>
<li>
Add Site (please include protocol):
<form action="/preferences" method="post">
<input type="text" name="newSite" size="30">
<input type="submit" value="Add Site">
</form>
</li>
$endif$
</ul>
</div>
