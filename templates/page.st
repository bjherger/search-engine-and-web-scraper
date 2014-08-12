<!DOCTYPE html><html>
$if(altLayout)$
<head>$CSS2()$</head> 
$else$
<head>$CSS()$</head> 
$endif$


<title>"Yotta: Thats 10^24 Of Knowledge"</title>
<body> 

$if(user)$
$loggedInHeader()$
$else$
$loggedOutHeader()$
$endif$

<br><br>

$body$

</body>
</html>
