<a href="/">
<img src="http://cs.usfca.edu/~bjherger/yotta/logo.jpg" alt="Yotta. 10^24 of knolwedge" width="480" height="216"></a>

<form method="post" action="/s">
<table cellspacing="0" cellpadding="2">
<h1>
<input type="text" name="query" maxlength="50" size="60" font-size:40px; style="width:600px; height:40px;"> 
<input type="submit"; value="Yotta Search!"; autocomplete="off"; style="border-style:none";>
</form>

<br>
$if(user)$
$loggedInHeaderAddOn()$
$else$
$loggedOutHeaderAddOn()$
$endif$
<div id = "content">
</div>

