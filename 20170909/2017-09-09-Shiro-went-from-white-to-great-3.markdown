---
layout: post
title: "【Shiro】Shiro从小白到大神(三)-权限认证(授权)"
date: 2017-09-09 18:27:54 +0800
comments: true
categories: shiro
tags: [java, shiro]
keyword: 陈浩翔, 谙忆, java, Shiro简介
description: Subject认证主体包含两个信息 Principals 身份，可以是用户名，邮件，手机号码等等，只要能用来标识一个登陆主体身份的东西都可以 Credentials 凭证(比如你说你叫张三，你凭什么说叫张三，你这个时候会拿出身份证说你就是叫张三，这个凭证和身份证差不多)，常见有密码，数字证书等等
---

本节讲权限认证，也就是授权  
基于角色的访问控制和基于权限的访问控制的小实例  
以及注解式授权和JSP标签授权详解

<!-- more -->
----------
#权限认证
##权限认证核心要素

权限认证，也就是访问控制，即在应用中控制谁能访问哪些资源  
在权限认证中，最核心的三个要素是：权限，角色和用户 (资源也算一个要素，但不是最核心的)  
权限，即操作资源的	权限，比如访问某个页面，以及对某个模块的数据的添加，修改，删除，查看的权利(整合以后，其实就是一些对URL请求的权限)  
角色，是权限的集合，一种角色可以包含多种权限(将权限赋给角色)    
用户，在Shiro中，代表访问系统的用户，即Subject(将角色赋给用户)  
英文好的，可以去看官方文档介绍: [http://shiro.apache.org/authorization.html](http://shiro.apache.org/authorization.html)  


##授权  
![](https://i.imgur.com/UNEeLs2.png)  

###编程式授权(Programmatic Authorization)  

####----基于角色的访问控制  
首先配置ini文件:
```
[users]
;基于角色的访问控制的配置文件
chx=123456,role1,role2
;加角色，密码后面是拥有的角色
jack=123456,role1
```

测试类
```java
package cn.chenhaoxiang.shiro;

import cn.chenhaoxiang.common.ShiroUtils;
import org.apache.shiro.subject.Subject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * 基于角色的访问控制
 */
public class RoleTest {
    private static Logger logger = LoggerFactory.getLogger(RoleTest.class);
    /**
     * hasRole(String roleName)  Returns true if the Subject is assigned the specified role, false otherwise.
     * hasRoles(List<String> roleNames) Returns a array of hasRole results corresponding to the indices in the method argument. Useful as a performance enhancement if many role checks need to be performed (e.g. when customizing a complex view)
     * hasAllRoles(Collection<String> roleNames) Returns true if the Subject is assigned all of the specified roles, false otherwise.
     * hasRole 判断是否拥有某个角色
     *
     * hasRoles 判断拥有某个角色  返回的是boolean[]  用来高效判断对应角色拥有
     * hasAllRoles 判断拥有所有角色  拥有传入的全部角色的话，才返回true
     *
     */
    @Test
    public void testHasRole(){
        Subject subject = ShiroUtils.login("classpath:shiro_role.ini","chx","123456");
        logger.info(subject.hasRole("role1")?"有role1这个角色":"没有role1这个角色");
        logger.info(subject.hasRole("role2")?"有role2这个角色":"没有role2这个角色");

        Subject subject2 = ShiroUtils.login("classpath:shiro_role.ini","jack","123456");
        logger.info(subject2.hasRole("role1")?"有role1这个角色":"没有role1这个角色");
        logger.info(subject2.hasRole("role2")?"有role2这个角色":"没有role2这个角色");

        // hasRoles 判断拥有某个角色  返回的是boolean[]  用来高效判断对应角色拥有
        boolean[] results = subject.hasRoles(Arrays.asList("role1","role2","role3"));
        logger.info(results[0]?"有role1这个角色":"没有role1这个角色");
        logger.info(results[1]?"有role2这个角色":"没有role2这个角色");
        logger.info(results[2]?"有role2这个角色":"没有role3这个角色");

        //hasAllRoles 判断拥有所有角色  拥有传入的全部角色的话，才返回true
        logger.info(subject.hasAllRoles(Arrays.asList("role1","role2"))?"有role1和role2这两个个角色":"role1，role2这两个角色不全部有");

        subject.logout();//退出
    }


    /**
     * CheckRole
     */
    @Test
    public void testCheckRole(){
        Subject subject = ShiroUtils.login("classpath:shiro_role.ini","chx","123456");
        subject.checkRole("role1");//没有返回值
        //subject.checkRole("role3");//没有这个角色会抛出异常 //org.apache.shiro.authz.UnauthorizedException: Subject does not have role [role211]

        //checkRoles(Collection<String> roleNames)
        subject.checkRoles(Arrays.asList("role1","role2"));
        //subject.checkRoles(Arrays.asList("role1","role2","role3"));//没有全部角色会抛出异常 //org.apache.shiro.authz.UnauthorizedException: Subject does not have role [role3]

        //checkRoles(String... roleNames)和checkRoles(Collection<String> roleNames)意思一样，传入的参数类型不同
        subject.checkRoles("role1","role2");

        subject.logout();//退出
    }
}
```

演示结果自己跑一遍就出来啦

####----基于权限的访问控制  

配置ini文件:
```
[users]
;基于权限的访问控制的配置文件
chx=123456,role1,role2
;加角色，密码后面是拥有的角色
jack=123456,role1
[roles]
;不判断角色，直接判断权限
role1=user:select
;role1拥有select权限 这里的user:select权限名字是自己定义的
role2=user:add,user:update,user:delete
;权限无非就是增删改查
```

测试类:
```java
package cn.chenhaoxiang.shiro;

import cn.chenhaoxiang.common.ShiroUtils;
import org.apache.shiro.subject.Subject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于权限的访问控制
 */
public class PermissionTest {

    private static Logger logger = LoggerFactory.getLogger(PermissionTest.class);

    /**
     * isPermitted(Permission p)  isPermitted(String str)  如果是拥有访问某个资源的权限,返回true  单个权限判断
     *
     * isPermitted(String... var1);  分别判断多个权限  返回boolean[]
     *
     * isPermittedAll(String... var1);  拥有所有权限才返回true
     */

    @Test
    public void testIsPermitted(){
        //Subject subject = ShiroUtils.login("classpath:shiro_permission.ini","chx","123456");
        Subject subject = ShiroUtils.login("classpath:shiro_permission.ini","jack","123456");
        logger.info(subject.isPermitted("user:select")?"有user:select权限":"没有user:select权限");
        logger.info(subject.isPermitted("user:update")?"有user:update权限":"没有user:update权限");

        boolean results[] = subject.isPermitted("user:select","user:update","user:delete");
        logger.info(results[0]?"有user:select权限":"没有user:select权限");
        logger.info(results[1]?"有user:update权限":"没有user:update权限");
        logger.info(results[2]?"有user:delete权限":"没有user:delete权限");

        logger.info(subject.isPermittedAll("user:select","user:update")?"有user:select和user:update权限":"user:select和user:update权限不全有");

        subject.logout();//退出
    }


    /**
     *
     * checkPermission(String var1)  没有这一个权限就抛出异常
     * checkPermissions(String... var1)  没有这些权限就抛出异常
     */
    @Test
    public void testCheckPermitted(){
        //Subject subject = ShiroUtils.login("classpath:shiro_permission.ini","chx","123456");
        Subject subject = ShiroUtils.login("classpath:shiro_permission.ini","jack","123456");

        subject.checkPermission("user:select");//检查是否有某个权限  没有权限则抛出异常
        //subject.checkPermission("user:delete");//org.apache.shiro.authz.UnauthorizedException: Subject does not have permission [user:delete]

        subject.checkPermissions("user:select","user:update");

        subject.logout();//退出
    }
}

```

讲了几个org.apache.shiro.subject.Subject的函数。  
其实官方文档都有介绍的  

###注解式授权  
更加详细的介绍可以去官网查看： http://shiro.apache.org/authorization.html  

首先你的Java版本5+才能集成shiro的注解  
####RequiresAuthentication注解 
RequiresAuthentication注解需要在当前会话中对当前的Subject进行身份验证，以便访问或调用该注解的类/实例/方法。 
也就是要求当前Subject已经在当前的Session中被验证通过才能被访问或调用  
比如:
```Java
@RequiresAuthentication //判断验证有没有通过
public void updateAccount(Account userAccount) {
    //this method will only be invoked by a
    //Subject that is guaranteed authenticated
    ...
}
```
基本等同于下面的代码:
```java
public void updateAccount(Account userAccount) {
    if (!SecurityUtils.getSubject().isAuthenticated()) {
        throw new AuthorizationException(...);
    }

    //Subject is guaranteed authenticated here
    ...
}
```
####RequiresGuest注解
要求当前的Subject是一个'guest'(游客),也就是说，必须是在之前的session中没有被验证或被记住才能被访问和调用  
例如:  
```java
@RequiresGuest
public void signUp(User newUser) {
    //this method will only be invoked by a
    //Subject that is unknown/anonymous
    ...
}
```
基本等价于下面的代码:  
```java
public void signUp(User newUser) {
    Subject currentUser = SecurityUtils.getSubject();
    PrincipalCollection principals = currentUser.getPrincipals();
    if (principals != null && !principals.isEmpty()) {
        //known identity - not a guest:
        throw new AuthorizationException(...);
    }

    //Subject is guaranteed to be a 'guest' here
    ...
}
```

####RequiresPermissions注解

RequiresPermissions注解要求当前Subject允许一个或多个权限来执行带注释的方法。  
也就是说，必须有这个权限才能访问  
例如:  
```java
@RequiresPermissions("account:create") //必须有account:create权限,多个权限之间用逗号隔开
public void createAccount(Account account) {
    //this method will only be invoked by a Subject
    //that is permitted to create an account
    ...
}
```
基本等价于：  
```java
public void createAccount(Account account) {
    Subject currentUser = SecurityUtils.getSubject();
    if (!subject.isPermitted("account:create")) {
        throw new AuthorizationException(...);
    }

    //Subject is guaranteed to be permitted here
    ...
}
```
####RequiresRoles注解

RequiresRoles注解要求当前Subject拥有所有指定的角色。如果它们没有所有的角色，则不会执行该方法，并抛出AuthorizationException  

例如:  
```java
@RequiresRoles("administrator")
public void deleteUser(User user) {
    //this method will only be invoked by an administrator
    ...
}
```
基本等同于以下代码:  
```java
public void deleteUser(User user) {
    Subject currentUser = SecurityUtils.getSubject();
    if (!subject.hasRole("administrator")) {
        throw new AuthorizationException(...);
    }

    //Subject is guaranteed to be an 'administrator' here
    ...
}
```
      
####RequiresUser注解
RequiresUser注解 需要当前的Subject是一个应用程序的用户 才能被所注解的类/实例/方法访问或者调用。  
一个"应用程序用户"被定义一个拥有已知身份，或在当前session中通过验证被确认，或者在之前的session中的"RememberMe"服务被记住  
也就是说，必须是某个用户  

例如:  
```java
@RequiresUser
public void updateAccount(Account account) {
    //this method will only be invoked by a 'user'
    //i.e. a Subject with a known identity
    ...
}
```  
基本等同于下面代码:  
```java
public void updateAccount(Account account) {
    Subject currentUser = SecurityUtils.getSubject();
    PrincipalCollection principals = currentUser.getPrincipals();
    if (principals == null || principals.isEmpty()) {
        //no identity - they're anonymous, not allowed:
        throw new AuthorizationException(...);
    }

    //Subject is guaranteed to have a known identity here
    ...
}
```

###JSP标签授权  
必须添加shiro-web.jar  

在jsp页面中引入:
```jsp
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
```  

####guest标签
用户没有身份验证时显示相应信息，即游客访问信息  
例如:  
```jsp
<shiro:guest>
    Hi there!  Please <a href="login.jsp">Login</a> or <a href="signup.jsp">Signup</a> today!
</shiro:guest>
```
在这里<shiro:guest>标签内的文字，如果用户没有登录才会显示出来,也就是游客  

####user标签
只有在当前Subject被认为是“用户”时，用户标记才会显示其包装内容。  
在这个上下文中，“用户”被定义为一个具有已知身份的主题，要么是成功的身份验证，要么是来自“记住我”的服务。    
注意，这个标记与经过身份验证的标记有语义上的不同，它比这个标记更加严格。  
例如:  
```jsp
<shiro:user>
    Welcome back John!  Not John? Click <a href="login.jsp">here<a> to login.
</shiro:user>
```
user标签和guest标签逻辑相反  

####authenticated标签
仅当当前用户在当前会话中成功验证时才显示正文内容。  
它比“用户”标签更具限制性。它在逻辑上与“notAuthenticated”标记相反。  
只有在当前Subject在当前会话中成功验证的情况下，经过身份验证的标记才会显示其包装内容。  
它是一个比用户更严格的标记，用来保证敏感工作流中的标识。也就是说，通过记住我登录的无法访问到！！！  
例如:  
```jsp
<shiro:authenticated>
    <a href="updateAccount.jsp">Update your contact information</a>.
</shiro:authenticated>
```

####notAuthenticated标签
如果当前Subject在当前会话中尚未成功验证，则未验证标记将显示其包装内容。  
也就是用户没有身份验证通过，即没有调用Subject.login进行登录，包括记住我自动登录的也属于未进行身份验证这个notAuthenticated标签！  

例如:  
```jsp
<shiro:notAuthenticated>
    Please <a href="login.jsp">login</a> in order to update your credit card information.
</shiro:notAuthenticated>
```

####principal标签
输出用户信息，会调用toString()方法  
例如:  
```jsp
Hello, <shiro:principal/>, how are you today?
```

相当于调用以下代码:  
```jsp
Hello, <%= SecurityUtils.getSubject().getPrincipal().toString() %>, how are you today?
```
#####通过类型
如果你不想获取所有的，比如在用户名和用户id之间，我想获取用户id，可以通过下面这种方式:  
```jsp
User ID: <principal type="java.lang.Integer"/>
```
等同于:  
```jsp
User ID: <%= SecurityUtils.getSubject().getPrincipals().oneByType(Integer.class).toString() %>
```

#####通过属性名
但是，当遇到复杂的情况时，上面的就不行了，毕竟可能不止一个Integer，这个时候就可以通过属性名了。  
通过getter方法获取的  
例如:  
```jsp
Hello, <shiro:principal property="firstName"/>, how are you today?
```
相当于下面的代码:  
```jsp
Hello, <%= SecurityUtils.getSubject().getPrincipal().getFirstName().toString() %>, how are you today?
```
或者说，可以结合type属性：  
```jsp
Hello, <shiro:principal type="com.foo.User" property="firstName"/>, how are you today?
```
也就是如下代码的逻辑:  
```jsp
Hello, <%= SecurityUtils.getSubject().getPrincipals().oneByType(com.foo.User.class).getFirstName().toString() %>, how are you today?
```

####hasRole标签
只有当当前Subject被分配指定角色时，hasRole标记才会显示其包装内容  

例如:  
```jsp
<shiro:hasRole name="administrator">
    <a href="admin.jsp">Administer the system</a>
</shiro:hasRole>
```

####lacksRole标签
如果当前Subject没有分配指定的角色，则将显示其包装内容。  
```jsp
<shiro:lacksRole name="administrator">
    Sorry, you are not allowed to administer the system.
</shiro:lacksRole>
```
lacksRole标签与hasRole标签的逻辑相反。  

####hasAnyRole标签  
如果当前Subject从一个由逗号分隔的角色名称列表中具有了任一指定的角色，那么hasAnyRole标记将显示其包装内容。  
```jsp
<shiro:hasAnyRoles name="developer, project manager, administrator">
    You are either a developer, project manager, or administrator.
</shiro:hasAnyRoles>
```
只要有其中一个角色，即显示主体内容  

####hasPermission标签  
如果当前Subject有权限则显示其包装的内容  
```jsp
<shiro:hasPermission name="user:create">
    <a href="createUser.jsp">Create a new User</a>
</shiro:hasPermission>
```

####lacksPermission标签
如果当前Subject没有该权限则显示其包装的内容  
```jsp
<shiro:lacksPermission name="user:delete">
    Sorry, you are not allowed to delete user accounts.
</shiro:lacksPermission>
```
lacksPermission标签与hasPermission标签的逻辑相反  

##深入理解Apache Shiro的Permissions

###通配符的权限
####单个权限: 直接起一个字符串名即可  
例如: queryPrinter权限-查询权限  
```java
subject.isPermitted("queryPrinter")
```
基本等同于:  
```java
subject.isPermitted( new WildcardPermission("queryPrinter") )
```
第二种方式基本不用，用第一种方式即可  

####多个权限: 通配符权限支持多个级别或部分的概念。  
下面使用":"用于分隔权限字符串下一部分的特殊字符。  
```java
printer:query
printer:print
printer:manage
```
即可配置多个权限  

也可以用多值来配置：  
```java
printer:print,query
```
验证查询权限:  
```java
subject.isPermitted("printer:query")
```

####单个资源的所有权限  
比如我们有这些权限:  
```java
printer:query,print,manage
```
相当于:  
```java
printer:*
```
使用第二种方法使用通配符比显式地列出动作要更好，因为如果以后向应用程序添加了一个新操作，则不需要更新在该部分中使用通配符的权限。  

####所有资源的某个权限
还可以在通配符权限字符串的任何部分使用通配符令牌  
```
*:view
```
所有资源的view权限  
也就是说对“foo:view”(或其他的:view)的任何权限检查将返回true  

####实例级别的权限控制 

通配符权限的另一个常见用法是建立实例级访问控制列表。  
在这个权限中，您将使用三个部分——第一个是域，第二个是动作，第三个是被执行的实例(标识)。  

####单个实例的单个权限  

```java
printer:query:lp7200
printer:print:epsoncolor
```
比如你拥有printer的query权限，打印机的id为lp7200，也就是拥有这类printer的query权限

如果您将这些权限授予用户，那么它们就可以在特定的实例上执行特定的行为。然后你可以在代码中做一个检查:  
```java
if ( SecurityUtils.getSubject().isPermitted("printer:query:lp7200") {
    // Return the current jobs on printer lp7200 }
}
```

####所有实例的单个权限  
```java
printer:print:*
```
也就是说，具有所有printer的print权限，相当于前面的单个资源的多个权限  

####所有实例的所有权限  
```java
printer:*:*
```

####单个实例的所有权限
```java
printer:*:lp7200
```

####单个实例的多个权限
```java
printer:query,print:lp7200
```
query和print之间用逗号隔开  
在实际开发中，基本上用不到实例级别的权限控制  

关于权限分配的最后一件事是:末尾丢失的部分意味着用户可以访问与该部分对应的所有值。换句话说,
```
printer:print
就相当于:
printer:print:*
```
```
printer 
单个权限相当于
printer:*:*
```

但是注意！
```
printer:lp7200
和
printer:*:lp7200
是不同的！！！
```
因为这不是末尾的*  

###检查权限
虽然权限分配使用通配符构造相当多(“printer:*”=打印到任何printer)，但在运行时的权限检查应该始终基于可能的最特定的权限字符串。  
比如:如果用户有一个用户界面，他们想要打印一个文档到lp7200打印机，你应该检查用户是否允许执行这个代码  
```java
if ( SecurityUtils.getSubject().isPermitted("printer:print:lp7200") ) {
    //print the document to the lp7200 printer }
}
```
这个检查非常具体，并且明确地反映了用户在那个时候正在尝试做什么。  
但是，如下代码是不对的:  
```java
if ( SecurityUtils.getSubject().isPermitted("printer:print") ) {
    //print the document }
}
```
因为第二个示例说“您必须能够打印到任何打印机，以便执行以下代码块”。但请记住，“printer:print”等同于“printer:print:*”!  

因此，这是一个不正确的检查。  
如果当前用户没有能力打印到任何打印机，但他们确实有打印的能力，比如lp7200和epsoncolor打印机。  
然而，上面的第二个例子永远不会允许他们打印到lp7200打印机，即使他们已经获得了这种能力!  

因此，经验法则是在执行权限检查时使用最特殊的权限字符串。  
当然，如果您真的只想执行代码块，如果用户被允许打印到任何打印机(可能)，那么第二个方法可能是应用程序中的另一个有效的检查。  
您的应用程序将决定什么检查是有意义的，但是一般来说，越具体越好。  


为什么运行时权限检查应该尽可能具体，但是权限分配可以更通用一些呢?  
这是因为权限检查是由隐含逻辑计算的，而不是平等检查。  

也就是说，如果用户被分配给"user:*"权限，这意味着用户可以执行"user:view"操作。字符串"user:*"显然不等于"user:view"，但前者暗示后者。"user:*"描述了由"user:view"定义的功能的超集。  

为了支持隐含规则，所有权限都被翻译到实现org.apache.shiro.authz的对象实例的权限接口中。  
这就是说，隐含逻辑可以在运行时执行，而且隐含逻辑通常比简单的字符串等式检查更复杂。  
本文档中描述的所有通配符行为实际上都是由org.apache.shiro.authz.permission.WildcardPermission类实现  

下面是一些通配符的权限字符串，它显示了访问的含义:  
```
user:*
```
暗指还能删除用户的能力:  
```
user:delete
```

但是:  
```
user:*:12345
```
也就是说，还可以使用实例12345更新用户帐户:  
```
user:update:12345
```
```
printer
暗示了打印机的任何功能，比如:
printer:print
```

##授权流程
![](https://i.imgur.com/Pm5Z7DE.png)  

授权其实就是查看有没有权限，有就授权给它  

授权步骤:
```
Step 1: Application or framework code invokes any of the Subject hasRole*, checkRole*, isPermitted*, or checkPermission* method variants, passing in whatever permission or role representation is required.

Step 2: The Subject instance, typically a DelegatingSubject (or a subclass) delegates to the application’s SecurityManager by calling the securityManager’s nearly identical respective hasRole*, checkRole*, isPermitted*, or checkPermission* method variants (the securityManager implements the org.apache.shiro.authz.Authorizer interface, which defines all Subject-specific authorization methods).

Step 3: The SecurityManager, being a basic ‘umbrella’ component, relays/delegates to its internal org.apache.shiro.authz.Authorizer instance by calling the authorizer’s respective hasRole*, checkRole*, isPermitted*, or checkPermission* method. The authorizer instance is by default a ModularRealmAuthorizer instance, which supports coordinating one or more Realm instances during any authorization operation.

Step 4: Each configured Realm is checked to see if it implements the same Authorizer interface. If so, the Realm’s own respective hasRole*, checkRole*, isPermitted*, or checkPermission* method is called.
```
有兴趣的可以去官网看看:http://shiro.apache.org/authorization.html  


#源代码下载地址：
<blockquote cite='陈浩翔'>
GITHUB源码下载地址:<strong>【<a href='https://github.com/chenhaoxiang/Shiro/tree/master/20170909/code/Shiro3' target='_blank'>点我进行下载</a>】</strong>
</blockquote>


本文章由<a href="http://chenhaoxiang.cn/">[谙忆]</a>编写， 所有权利保留。 
欢迎转载，分享是进步的源泉。
<blockquote cite='陈浩翔'>
<p background-color='#D3D3D3'>转载请注明出处：<a href='http://chenhaoxiang.cn'><font color="green">http://chenhaoxiang.cn</font></a><br><br>
本文源自<strong>【<a href='http://chenhaoxiang.cn' target='_blank'>人生之旅_谙忆的博客</a>】</strong></p>
</blockquote>