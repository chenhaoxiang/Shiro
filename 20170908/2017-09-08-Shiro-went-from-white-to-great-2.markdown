---
layout: post
title: "【Shiro】Shiro从小白到大神(二)-Subject认证结合MySQL"
date: 2017-09-08 11:38:54 +0800
comments: true
categories: shiro
tags: [java, shiro]
keyword: 陈浩翔, 谙忆, java, Shiro简介
description: Subject认证主体包含两个信息 Principals 身份，可以是用户名，邮件，手机号码等等，只要能用来标识一个登陆主体身份的东西都可以 Credentials 凭证(比如你说你叫张三，你凭什么说叫张三，你这个时候会拿出身份证说你就是叫张三，这个凭证和身份证差不多)，常见有密码，数字证书等等
---

上一节博客讲的文本数据验证，基本不会在项目中用到，只是方便用来学习和测试  
在本节，进行简单的数据库安全验证实例  

<!-- more -->
----------

#Subject认证主体
Subject认证主体包含两个信息:  
Principals: 身份，可以是用户名，邮件，手机号码等等，只要能用来标识一个登陆主体身份的东西都可以   
Credentials: 凭证(比如你说你叫张三，你凭什么说叫张三，你这个时候会拿出身份证说你就是叫张三，这个凭证和身份证差不多)，常见有密码，数字证书等等  

#认证流程
![](https://i.imgur.com/uzTsCic.png)  
细节可以自己去官网链接查看: http://shiro.apache.org/authentication.html  
1.身份凭证登录:.login(token)  
2.SecurityManager - 管理者  
3.4.涉及安全数据。在这里涉及到了Realm(意思是域)，Shiro从Realm中获取验证数据(或者叫安全数据)；  
Realm有很多种类，例如常见的jdbc realm,jndi realm,text realm(上节的博客就是text Realm).  
我们可以去Shiro的源码查看:  
![](https://i.imgur.com/BijTwuc.png)  
可以看到还是有比较多的。  
本节讲解jdbc Realm。  

#实例
既然是数据库操作，首先当然是去建数据库和表啦  
```sql sql语句
/*
SQLyog Ultimate v12.3.1 (64 bit)
MySQL - 5.7.19-log : Database - db_shiro
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`db_shiro` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `db_shiro`;

/*Table structure for table `users` */

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) DEFAULT NULL,
  `password` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*Data for the table `users` */

insert  into `users`(`id`,`username`,`password`) values 

(1,'chx','123456'),

(2,'jack','12345');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
```
直接拷贝进数据库运行即可(数据库文件在项目src\sql目录下)  
**注意事项：**  
**表名一定要是: users**  
**用户名列名必须是: userName(大小写不区分)**  **

jdbc_realm.ini配置文件
```ini jdbc_realm.ini配置文件
[main]
jdbcRealm=org.apache.shiro.realm.jdbc.JdbcRealm
;定义JdbcRealm实例-固定语法
dataSource=com.mchange.v2.c3p0.ComboPooledDataSource
;数据库链接池-查看JdbcRealm类的源码可以看到需要dataSource数据源 在om.xml导入c3p0的jar包
dataSource.driverClass=com.mysql.jdbc.Driver
;设置dataSource的jdbc驱动包 - 相当于dataSource调用了setriverClass
dataSource.jdbcUrl=jdbc:mysql://localhost:3306/db_shiroD
;数据库连接地址
dataSource.user=root
;数据库账号
dataSource.password=123456
;数据库密码
jdbcRealm.dataSource=$dataSource
;为jdbcRealm赋值dataSource,jdbcRealm调用set
securityManager.realms=$jdbcRealm
;这个realms可以有多个，多个之间用英文逗号隔开
```
";"为ini文件的注释  

测试类: 
```java JdbcRealmTest.java
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

public class JdbcRealmTest {
    private static Logger logger = LoggerFactory.getLogger(JdbcRealmTest.class);
    public static void main(String[] args) {
        //IniSecurityManagerFactory方法在1.4.0中被注解标志为不建议使用
        //读取配置文件，初始化SecurityManager工厂
        Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:jdbc_realm.ini");
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
            logger.info("登录失败！");
            e.printStackTrace();
        }
        //登出
        subject.logout();
    }

}
```
![](https://i.imgur.com/kFIvVI7.png)  
如果验证失败会报异常，可以自己测试  

#源代码下载地址：
<blockquote cite='陈浩翔'>
GITHUB源码下载地址:<strong>【<a href='https://github.com/chenhaoxiang/Shiro/tree/master/20170908/code/Shiro2' target='_blank'>点我进行下载</a>】</strong>
</blockquote>


本文章由<a href="http://chenhaoxiang.cn/">[谙忆]</a>编写， 所有权利保留。 
欢迎转载，分享是进步的源泉。
<blockquote cite='陈浩翔'>
<p background-color='#D3D3D3'>转载请注明出处：<a href='http://chenhaoxiang.cn'><font color="green">http://chenhaoxiang.cn</font></a><br><br>
本文源自<strong>【<a href='http://chenhaoxiang.cn' target='_blank'>人生之旅_谙忆的博客</a>】</strong></p>
</blockquote>