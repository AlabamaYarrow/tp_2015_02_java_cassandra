<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>Signup &mdash; Cassandra</title>
    <link rel="stylesheet" href="/css/main.css"/>
    <script src="/js/lib/jquery.js"></script>
</head>
<body>
<a href="/"><h1>Cassandra</h1></a>

<h2>Sign Up</h2>

<#if user??>
<p>Before signing up you need to <a href="/logout/">logout</a> from ${user.getLogin()}.</p>
<#elseif new_user??>
<p>Congratulations, ${new_user.getLogin()}, you're succesfully registered.</p>

<p>Now you can <a href="/login/">log in.</a></p>
<#else>
<form method="post">
<p>
    <label for="login">Login:</label>
    <input id="login" type="text" name="login" value="<#if login??>${login}</#if>">
    <#if login_error??>
        <div class="error">${login_error}</div></#if>
    </p>

<p>
    <label for="email">Email:</label>
    <input id="email" type="email" name="email" value="<#if email??>${email}</#if>">
    <#if email_error??>
        <div class="error">${email_error}</div></#if>
    </p>

<p>
    <label for="password">Password:</label>
    <input id="password" type="password" name="password" value="<#if password??>${password}</#if>">
    <#if password_error??>
        <div class="error">${password_error}</div></#if>
    </p>

<p>
    <label for="password-confirmation">Confirm:</label>
    <input id="password-confirmation" type="password" name="password_confirmation"
           value="<#if password_confirmation??>${password_confirmation}</#if>">
    <#if password_confirmation_error??>
        <div class="error">${password_confirmation_error}</div></#if>
    </p>

    <#if signup_error??>
        <div class="error">${signup_error}</div></#if>

    <p>
        <input type="submit" value="Sign Up">
    </p>
</form>
</#if>
</body>
</html>
