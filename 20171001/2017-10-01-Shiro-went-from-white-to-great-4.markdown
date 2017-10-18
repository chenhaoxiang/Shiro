---
layout: post
title: "【Shiro】Shiro从小白到大神(四)-集成Web"
date: 2017-10-01 17:16:24 +0800
comments: true
categories: shiro
tags: [java, shiro]
keyword: 陈浩翔, 谙忆, java, Shiro简介
description: 本节讲集成Web(没有通过数据库-通过text) 实现登录经过Shiro验证后跳转另外的页面，以及没验证通过进行的权限拦截   
---

本节讲集成Web(没有通过数据库-通过text)  
实现登录经过Shiro验证后跳转另外的页面，以及没验证通过进行的权限拦截   

<!-- more -->
----------

本节讲集成Web(没有通过数据库-通过text)  
实现登录经过Shiro验证后跳转另外的页面，以及没验证通过进行的权限拦截   

shiro.ini文件配置
```java
[main]
authc.loginUrl=/login
;这里的配置为authc验证没通过请求的路径  loginUrl为一个属性名 org.apache.shiro.web.filter.authc.FormAuthenticationFilter类中
roles.unauthorizedUrl=unauthorized.jsp
;roles 角色认证未通过去请求的Url  在org.apache.shiro.web.filter.authz.AuthorizationFilter可以看到unauthorizedUrl这个属性
perms.unauthorizedUrl=unauthorized.jsp
;权限认证未通过

[users]
chx=123,admin
jack=123,teacher
marry=123
json=123
[roles]
admin=user:*
teacher=student:*
[urls]
/login=anon
;请求login需要的权限,只要游客就行，或者不进行验证 anon
/admin=authc
;会进行身份验证   authc 对应的是过滤器

;权限拦截是有顺序的，需要先进行身份验证，也就是登录后才有权限角色等认证
/student=roles[teacher]
;请求student url 必须拥有roles的teacher角色登录
/teacher=perms["user:create"]
;必须要有teacher的user:create操作才能访问teacher这个url
```

ShiroUtils工具类: 
```java
package cn.chenhaoxiang.common;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShiroUtils {
    private static Logger logger = LoggerFactory.getLogger(ShiroUtils.class);
    /**
     * 封装Subject
     * @param configFile 配置文件
     * @param userName 用户名
     * @param password 密码
     * @return
     */
    public static Subject login(String configFile, String userName, String password){
        //IniSecurityManagerFactory方法在1.4.0中被注解标志为不建议使用
        //读取配置文件，初始化SecurityManager工厂
        Factory<SecurityManager> factory = new IniSecurityManagerFactory(configFile);
        //获取securityManager 实例
        SecurityManager securityManager=factory.getInstance();
        //把securityManager实例绑定到SecurityUtils
        SecurityUtils.setSecurityManager(securityManager);
        //得到当前执行的用户
        Subject subject =  SecurityUtils.getSubject();//认证实体，当前进来的用户
        //创建token令牌，用户名/密码
        UsernamePasswordToken token = new UsernamePasswordToken(userName,password);
        //身份认证
        try {
            subject.login(token);
            logger.info("登录成功！");
        } catch (AuthenticationException e) {
            logger.info("登录失败！");
            e.printStackTrace();
        }
        return subject;
    }
}
```
pom.xml配置
```xml
<?xml version="1.0" encoding="UTF-8"?><project xmlns="http://maven.apache.org/POM/4.0.0" 
		 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>cn.chenhaoxiang</groupId>
    <artifactId>ShiroWeb</artifactId>
    <version>1.0-SNAPSHOT</version>
    <dependencies>
        <!-- https://mvnrepository.com/artifact/org.apache.shiro/shiro-core -->
        <dependency>
            <groupId>org.apache.shiro</groupId>
            <artifactId>shiro-core</artifactId>
            <version>1.3.2</version>
        </dependency>
        <!-- shiro web支持-->
        <dependency>
            <groupId>org.apache.shiro</groupId>
            <artifactId>shiro-web</artifactId>
            <version>1.2.5</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.slf4j/slf4j-log4j12 -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
            <version>1.7.25</version>
        </dependency>
        <!--日志支持-->
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.17</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/commons-logging/commons-logging -->
        <dependency>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
            <version>1.2</version>
        </dependency>
        <!--要单元测试，引入junit-->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
        </dependency>
        <!--Servlet支持 -->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>3.1.0</version>
        </dependency>
        <!--jstl支持 -->
        <dependency>
            <groupId>javax.servlet </groupId>
            <artifactId>jstl</artifactId>
            <version>1.2</version>
        </dependency>
    </dependencies>
</project>
```

web.xml配置
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">
    <display-name>ShiroWeb</display-name>
    <welcome-file-list>
        <welcome-file>index.jsp</welcome-file>
    </welcome-file-list>
    <listener>
        <listener-class>org.apache.shiro.web.env.EnvironmentLoaderListener</listener-class>
    </listener>
    <!--过滤器方式-->
    <filter>
        <filter-name>ShiroFilter</filter-name>
        <filter-class>org.apache.shiro.web.servlet.ShiroFilter</filter-class>
        <init-param>
            <!--配置ini文件-->
            <param-name>configPath</param-name>
            <param-value>/WEB-INF/shiro.ini</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>ShiroFilter</filter-name><!--对应上面的-->
        <url-pattern>/*</url-pattern>
    </filter-mapping>
    <servlet>
        <servlet-name>loginServlet</servlet-name>
        <servlet-class>cn.chenhaoxiang.servlet.LoginServlet</servlet-class>
    </servlet>
    <servlet>
        <servlet-name>adminServlet</servlet-name>
        <servlet-class>cn.chenhaoxiang.servlet.AdminServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>loginServlet</servlet-name>
        <url-pattern>/login</url-pattern>
    </servlet-mapping>
    <servlet-mapping>
        <servlet-name>adminServlet</servlet-name>
        <url-pattern>/admin</url-pattern>
    </servlet-mapping>
</web-app>
```

代码核心就这些了，其他的代码可以下载源码自己运行学习。  
进行测试的话，直接在浏览器中输入想访问的链接就行，你可以看控制台输出的运行结果，以及页面的跳转。  

官网学习文档链接:[http://shiro.apache.org/web.html#Web-Shiro1.2andlater](http://shiro.apache.org/web.html#Web-Shiro1.2andlater)  

# Url匹配方式(urls) 
```java
? 匹配一个字符，例如 /admin? 可以匹配/admin1;/admin2等等，但是不能匹配/admin12;/admin,也就是不能匹配多个字符或者多路径，而且必须匹配一个字符 
* 匹配零个或者一个或者多个字符，例如 /admin* 可以匹配 /admin;/admin1;/admin12;等等但是不能匹配/admin/a，也就是不能匹配多路径,只能在一个路径下  
** 匹配零个或者多个路径,例如 /admin/**,可以匹配/admin;/admin/a;/admin/a/b，不能匹配/admin12,因为是匹配多路径的，而不是多字符  
```
可以自己配合urls下的/admin来测试  
可以自己同时结合几个匹配方式来测试，例如/admin*/**  
注意: ```/admin**```和```/admin/**```是一样的  

# Shiro标签
结合实例来理解shiro标签  

## shiro:hasRole
注意jsp先引入:  
```java
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
```

jsp:
```java
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
</body>
</html>
```
这样，你登录之后，如果有admin角色，就会显示里面的那句话了  
登录admin角色的用户  
![](https://i.imgur.com/3Q9hzqK.png)  

## shiro:hasPermission标签  

```java
<shiro:hasPermission name="student:create">
欢迎有student:create权限的用户
</shiro:hasPermission>
```
如果拥有student:create权限，就会显示标签内文字   
![](https://i.imgur.com/CG4BfL9.png)  

## shiro:principal标签 
显示用户信息的标签   
```java
<shiro:principal></shiro:principal>
```
![](https://i.imgur.com/cZYlv5p.png)  


# Shiro会话机制
Shiro有自己的一套会话机制，不多讲，其实正常开发的话，一般都是用默认的  
```java
//获取Session  Shiro管理的Session
Session session = subject.getSession();
System.out.println("sessionId:"+session.getId());//用户会话的唯一id
System.out.println("sessionHost:"+session.getHost());//获取主机地址
System.out.println("sessionTimeOut:"+session.getTimeout());//获取超时时间,默认是半小时的,单位ms

session.setAttribute("info","session数据");
```  
要深入研究的话，可以去官网看看哦: http://shiro.apache.org/web.html#Web-sessionManagement  

# 源代码下载地址：
<blockquote cite='陈浩翔'>
GITHUB源码下载地址:<strong>【<a href='https://github.com/chenhaoxiang/Shiro/tree/master/20171001/code/ShiroWeb' target='_blank'>点我进行下载</a>】</strong>
</blockquote>



本文章由<a href="http://chenhaoxiang.cn/">[谙忆]</a>编写， 所有权利保留。 
欢迎转载，分享是进步的源泉。
<blockquote cite='陈浩翔'>
<p background-color='#D3D3D3'>转载请注明出处：<a href='http://chenhaoxiang.cn'><font color="green">http://chenhaoxiang.cn</font></a><br><br>
本文源自<strong>【<a href='http://chenhaoxiang.cn' target='_blank'>人生之旅_谙忆的博客</a>】</strong></p>
</blockquote>