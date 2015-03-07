<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>Admin &mdash; Cassandra</title>
    <link rel="stylesheet" href="/css/main.css"/>
    <script src="/js/lib/jquery.js"></script>
</head>
<body>
<a href="/"><h1>Cassandra</h1></a>

<h2>Admin</h2>

<#if stopping??>
<p>Server will be stopped now.</p>
<#else>
<p><span class="label">Registered:</span> ${registered_count}</p>

<p><span class="label">Online:</span> ${online_count}</p>

<form method="post">
    <input type="hidden" name="delay" value="1000">
    <input type="submit" value="Stop">
</form>
</#if>
</body>
</html>
