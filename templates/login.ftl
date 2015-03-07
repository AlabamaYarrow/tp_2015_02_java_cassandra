<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>Login &mdash; Cassandra</title>
    <link rel="stylesheet" href="/css/main.css"/>
    <script src="/js/lib/jquery.js"></script>
</head>
<body>
<a href="/"><h1>Cassandra</h1></a>

<h2>Login</h2>

<#if user??>
<p>You're already logged in as ${user.getLogin()}. Do you need to <a href="/logout/">logout?</a></p>
<#elseif logged_in_user??>
<p>You was logged in as ${logged_in_user.getLogin()}. Now you can go to your <a href="/profile/">profile.</a></p>
<#else>
<form method="post">
<p>
    <label for="login">Login:</label>
    <input id="login" type="text" name="login" value="<#if login??>${login}</#if>"/>
    <#if login_error??>
        <div class="error">${login_error}</div></#if>
    </p>

<p>
    <label for="password">Password:</label>
    <input id="password" type="password" name="password" value="<#if password??>${password}</#if>"/>
    <#if password_error??>
        <div class="error">${password_error}</div></#if>
    </p>

    <#if login_procedure_error??>
        <div class="error">${login_procedure_error}</div></#if>

    <p>
        <input type="submit" value="Login">
    </p>
</form>
</#if>
</body>
</html>
