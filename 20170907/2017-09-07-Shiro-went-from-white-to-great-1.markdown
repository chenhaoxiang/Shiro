---
layout: post
title: "【Shiro】Shiro从小白到大神(一)-Shiro入门"
date: 2017-09-07 19:38:54 +0800
comments: true
categories: shiro
tags: [java, shiro]
keyword: 陈浩翔, 谙忆, java, Shiro简介
description: Apache Shiro（日语堡垒（Castle）的意思）是一个强大易用的Java安全框架，提供了认证、授权、加密和会话管理功能，可为任何应用提供安全保障 - 从命令行应用、移动应用到大型网络及企业应用。 
---

本系列是我在学习Shiro的路上的笔记，第一篇是属于非常入门级别的。  
首先是介绍了下shiro，然后进行了一个小例子进行实际的操作  
本节操作不涉及数据库，只是文本字符操作认证  

<!-- more -->
----------

#Shiro简介:
百度百科上的介绍:  
Apache Shiro（日语“堡垒（Castle）”的意思）是一个强大易用的Java安全框架，提供了认证、授权、加密和会话管理功能，可为任何应用提供安全保障 - 从命令行应用、移动应用到大型网络及企业应用。  
Shiro为解决下列问题（我喜欢称它们为应用安全的四要素）提供了保护应用的API：  
认证 - 用户身份识别，常被称为用户“登录”；  
授权 - 访问控制；  
密码加密 - 保护或隐藏数据防止被偷窥；  
会话管理 - 每用户相关的时间敏感的状态。  
Shiro还支持一些辅助特性，如Web应用安全、单元测试和多线程，它们的存在强化了上面提到的四个要素。  

Apache Shiro官网的介绍链接:http://shiro.apache.org/introduction.html  

Shiro targets what the Shiro development team calls “the four cornerstones of application security” - Authentication(身份认证), Authorization(权限控制), Session Management(Session管理), and Cryptography(加密):  

Authentication: Sometimes referred to as ‘login’, this is the act of proving a user is who they say they are.  
Authorization: The process of access control, i.e. determining ‘who’ has access to ‘what’.  
Session Management: Managing user-specific sessions, even in non-web or EJB applications.  
Cryptography: Keeping data secure using cryptographic algorithms while still being easy to use.  
前面四个是核心的。  
还具有Web支持，缓存，并发，伪装，"记住我"等  

本节实例使用Maven，如不熟悉Maven的，建议先去学习Maven  

#Shiro实例-模拟最简单的HelloWord
引入Shiro的Jar包:  
```xml pom.xml
 <dependency>
       <groupId>org.apache.shiro</groupId>
       <artifactId>shiro-core</artifactId>
       <version>1.3.2</version>
</dependency>
```
配置配置文件(放在resource文件下):  
最简单的账户密码形式
```ini shiro.ini
[users]
chx=123456
jack=12345
```

Java-HelloWord类

```java HelloWord类
package cn.chenhaoxiang;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWord {
    private static Logger logger = LoggerFactory.getLogger(HelloWord.class);
    public static void main(String[] args) {
        //IniSecurityManagerFactory方法在1.4.0中被注解标志为不建议使用
        //读取配置文件，初始化SecurityManager工厂
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        //获取securityManager 实例
        SecurityManager securityManager=factory.getInstance();
        //把securityManager实例绑定到SecurityUtils
        SecurityUtils.setSecurityManager(securityManager);
        //得到当前执行的用户
        Subject subject =  SecurityUtils.getSubject();//认证实体，当前进来的用户
        //创建token令牌，用户名/密码
        UsernamePasswordToken token = new UsernamePasswordToken("jack","12345");
        //身份认证
        try {
            subject.login(token);
            logger.info("登录成功！");
        } catch (AuthenticationException e) {
//login的接口函数  void login(AuthenticationToken var1) throws AuthenticationException;所以直接抓AuthenticationException异常即可
//身份认证失败即抛出此异常
            logger.info("登录失败！");
            e.printStackTrace();
        }
        //登出
        subject.logout();
    }
}
```

#源代码下载地址：
<blockquote cite='陈浩翔'>
GITHUB源码下载地址:<strong>【<a href='https://github.com/chenhaoxiang/Shiro/tree/master/20170907/code/Shiro01' target='_blank'>点我进行下载</a>】</strong>
</blockquote>


本文章由<a href="http://chenhaoxiang.cn/">[谙忆]</a>编写， 所有权利保留。 
欢迎转载，分享是进步的源泉。
<blockquote cite='陈浩翔'>
<p background-color='#D3D3D3'>转载请注明出处：<a href='http://chenhaoxiang.cn'><font color="green">http://chenhaoxiang.cn</font></a><br><br>
本文源自<strong>【<a href='http://chenhaoxiang.cn' target='_blank'>人生之旅_谙忆的博客</a>】</strong></p>
</blockquote>