$if(errorCode)$
<p> $errorCode$ </p>
$endif$

$if(newUser)$
<p>Registration was successful! 
Login with your new username and password below.</p>
$endif$

$if(changePass)$
<p>Password change was successful! 
Login with your username and new password below.</p>
$endif$

$if(logout)$
<p>Successfully logged out.</p>
$endif$

<form action="/login" method="post">
<table border="0">
<tr>
<td>Usename:</td>
<td><input type="text" name="user" size="30"></td>
</tr>
<tr>
<td>Password:</td>
<td><input type="password" name="pass" size="30"></td>
</tr>
</table>
<p><input type="submit" value="Login"></p>
</form>

<p>(<a href="/register">new user? register here.</a>)</p>


