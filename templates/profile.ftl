<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title><#if user??>${user.getLogin()}'s </#if>Profile &mdash; Cassandra</title>
    <link rel="stylesheet" href="/css/main.css"/>
    <script src="/js/lib/jquery.js"></script>
</head>
<body>
<a href="/"><h1>Cassandra</h1></a>

<h2><#if user??>${user.getLogin()}'s </#if>Profile</h2>

<#if user??>
<p><span class="label">Login:</span> ${user.getLogin()}</p>

<p><span class="label">Email:</span> ${user.getEmail()}</p>

<p>You can <a href="/logout/">logout.</a></p>
</#if>
</body>
</html>
