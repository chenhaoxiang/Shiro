<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2017/9/11 0011
  Time: 11:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
登录成功，欢迎你
<shiro:hasRole name="admin">
    欢迎有admin角色的用户
</shiro:hasRole>

<shiro:hasPermission name="student:create">
欢迎有student:create权限的用户
</shiro:hasPermission>

<shiro:principal></shiro:principal>

</body>
</html>
