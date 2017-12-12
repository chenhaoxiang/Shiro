<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2017/9/11 0011
  Time: 10:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form action="login" method="post">
    userName:<input type="text" name="userName"><br/>
    password:<input type="password" name="password"><br/>
    <input type="submit" value="登录" >${errorInfo}
</form>
</body>
</html>
