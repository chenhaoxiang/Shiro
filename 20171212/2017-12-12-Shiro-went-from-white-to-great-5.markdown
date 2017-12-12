---
layout: post
title: "【Shiro】Shiro从小白到大神(五)-自定义Realm"
date: 2017-12-12 21:29:24 +0800
comments: true
categories: shiro
tags: [java, shiro]
keyword: 陈浩翔, 谙忆, java, Shiro简介
description: 前面讲的，用户数据，以及配置ini数据都是在文件里面配置的，实际项目中，很少这么开发的。基本上是通过读取数据库来配置的。这个时候就需要用到自定义Realm了。  

---

前面讲的，用户数据，以及配置ini数据都是在文件里面配置的，实际项目中，很少这么开发的。基本上是通过读取数据库来配置的。  
这个时候就需要用到自定义Realm了。  

<!-- more -->
----------

用数据库的话，至少会涉及到这几张表:  
用户表，角色表，权限表  

角色和用户是一对多的关系 多个用户可以拥有同一个角色  
角色和权限在这里也是一对多的关系 一个角色可以拥有很多个权限  
  
数据库表名:  
t_role 角色表  
并插入如下数据  
![](https://i.imgur.com/lfVB6MB.png)  

t_user 用户表  
roleId关联角色表
![](https://i.imgur.com/uJUYXKw.png)  

t_permission 权限表  
roleId关联角色表
![](https://i.imgur.com/48sSenK.png)  

接下来就是设置外键，设置好之后就看架构设计：  
![](https://i.imgur.com/kQXn1RD.png)  

接下来就是写代码了，首先肯定是写一个连接数据库的工具类:
DbUtils.java
```java
package cn.chenhaoxiang.common;


import java.sql.Connection;
import java.sql.DriverManager;

/**
 * 数据库工具类
 * @author chenhaoxiang
 *
 */
public class DbUtils {

    /**
     * 获取数据库连接
     * @return
     * @throws Exception
     */
    public Connection getCon() throws Exception{
        Class.forName("com.mysql.jdbc.Driver");
        Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/db_shiro", "root", "1234");
        return con;
    }

    /**
     * 关闭数据库连接
     * @param con
     * @throws Exception
     */
    public void closeCon(Connection con)throws Exception{
        if(con!=null){
            con.close();
        }
    }

    public static void main(String[] args) {
        DbUtils dbUtil=new DbUtils();
        try {
            dbUtil.getCon();
            System.out.println("数据库连接成功");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("数据库连接失败");
        }
    }
}
```
既然连接数据库，肯定就需要用到数据库的驱动包，在maven中导入mysql驱动包。  
```java
        <!--MySQL驱动-->
        <!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.40</version>
        </dependency>
```
版本不同的可以自行去Maven中央仓库去进行搜索。  

你的连接账号密码可能与我的不同，记得修改。  
完成之后测试一波，没问题的话我们继续  

接下来写一个自定义的Realm：  
```java
package cn.chenhaoxiang.realm;

import cn.chenhaoxiang.common.DbUtils;
import cn.chenhaoxiang.dao.UserDao;
import cn.chenhaoxiang.entity.User;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.sql.Connection;

/**
 * 继承一个认证的Realm
 * @author chenhaoxiang
 */
public class MyRealm extends AuthorizingRealm{

    private UserDao userDao = new UserDao();
    private DbUtils dbUtils = new DbUtils();
    /**
     * 为当前登录成功的用户授予角色和权限，从数据库读取
     * 如果登录失败，不会到这里来的
     * @param principalCollection
     * @return
     */
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //登录成功之后
        String userName = (String) principalCollection.getPrimaryPrincipal();//获取用户信息
        //认证信息
        SimpleAuthorizationInfo simpleAuthenticationInfo = new SimpleAuthorizationInfo();
        Connection con = null;
        try{
            con = dbUtils.getCon();
            simpleAuthenticationInfo.setRoles(userDao.getRoles(con,userName));//设置角色
            simpleAuthenticationInfo.setStringPermissions(userDao.getPermissions(con,userName));//获取权限
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                dbUtils.closeCon(con);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return simpleAuthenticationInfo;
    }

    /**
     * 获取认证信息  验证当前登录的用户
     * @param token
     * @return 登录失败返回null
     * @throws AuthenticationException
     */
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String userName = (String) token.getPrincipal();//获取用户名
        //通过用户名去数据库查找信息 然后和提交过来的信息比对
        Connection con = null;
        try{
            con = dbUtils.getCon();
            User user = userDao.getByUserName(con,userName);
            if(user!=null){
                AuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user.getUserName(),user.getPassword(),"aa");//第二个参数密码是数据库的密码，第三个参数realmName，我们在这里随便写一个
                return authenticationInfo;//shiro内部会帮我们比对的
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                dbUtils.closeCon(con);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}

```

其他还有一些类，大家直接来我们的github下载完整示例吧。  
数据库的脚本在项目的WEB-INF的sql文件夹内  

演示的话，大家可以先进入登录页面，然后登录来进行测试、  
可以在登录验证的MyRealm类中设置断点，这样会让你记忆深一些，也会容易理解一些，理一下验证的顺序。  

#源代码下载地址：
<blockquote cite='陈浩翔'>
GITHUB源码下载地址:<strong>【<a href='https://github.com/chenhaoxiang/Shiro/tree/master/20171212/code/ShiroWeb' target='_blank'>点我进行下载</a>】</strong>
</blockquote>


本文章由<a href="http://chenhaoxiang.cn/">[谙忆]</a>编写， 所有权利保留。 
欢迎转载，分享是进步的源泉。
<blockquote cite='陈浩翔'>
<p background-color='#D3D3D3'>转载请注明出处：<a href='http://chenhaoxiang.cn'><font color="green">http://chenhaoxiang.cn</font></a><br><br>
本文源自<strong>【<a href='http://chenhaoxiang.cn' target='_blank'>人生之旅_谙忆的博客</a>】</strong></p>
 </blockquote>