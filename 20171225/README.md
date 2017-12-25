---
layout: post
title: "【SpringBoot】项目属性配置"
date: 2017-12-24 13:38:54 +0800
comments: true
categories: SpringBoot
tags: [SpringBoot]
keyword: 陈浩翔, 谙忆, SpringBoot
description:  项目属性配置
---


简单的介绍一下SpringBoot的属性配置。

继续上节的博客喔、  

配置application.properties文件:  
```xml
#第一种配置方式
#这种配置方式每个属性名都必须写完整
server.port=8081
#配置端口
server.context-path=/hello
#配置项目路径
```

可以跑一下看看，这个时候访问项目就路径需要增加/hello项目名了。  
而且端口是8081，默认的是8080  

进行第二种配置方式之前，先删除application.properties文件  
我就不删除了，我重命名为application.txt文件了，建议你删除，选择第二种配置方式即可  
#第二种配置方式-推荐

在resource目录下新建文件:application.yml  
```
server:
  port: 8081
#  :号后面必须有空格
  context-path: /hello
```
这个配置就方便很多，不用全名了。  
有个注意事项，请看代码中的注释  

运行结果和第一种配置方式是一样的  

#进行自定义的配置变量
例如增加:
```
server:
  port: 8082
#  :号后面必须有空格
  context-path: /hello
name: 陈浩翔
age: 20
```
我们不需要在这里配置变量类型，只要在注入的时候写好属性类型即可  
我们使用的是 @Value注入  

在代码中读取配置:  
```java
package cn.chenhaoxiang;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * User: 陈浩翔.
 * Date: 2017/12/25.
 * Time: 下午 9:44.
 * Explain:
 */
@RestController
public class HelloController {

    @Value("${name}")//这个变量读取写法有点像jsp读取session的
    private String name;

    @Value("${age}")
    private Integer age;

    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    public String say() {
        return "Hello Spring Boot!";
    }

    @RequestMapping(value = "/info",method = RequestMethod.GET)
    public String info() {
        return name+","+age;
    }
}

```
运行之后，到浏览器输入地址看运行结果  
![](https://i.imgur.com/UFB9qtj.png)    

还可以在配置中使用配置，我们可以在配置文件中这么写:  
```
info: "name:${name},age:${age}"
```
这样就可以在配置中引用name的值和age的值  

有没有发现上面的配置方式有点麻烦，如果我有很多属性，岂不是要写很多读取和写嘛  
放心，肯定有简便方式的，这个时候我们可以选择用类来封装  

我们定义一个People类。  
有年龄，姓名，地址属性  
接下来看代码吧，代码说明一切  
```java
package cn.chenhaoxiang;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: 陈浩翔.
 * Date: 2017/12/25.
 * Time: 下午 9:58.
 * Explain:
 */
@Component //注入Bean需要
@ConfigurationProperties(prefix = "people")//获取前缀是people的配置
public class People {

    private String name;

    private Integer age;

    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "People{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", address='" + address + '\'' +
                '}';
    }
}

```

到配置中配置People的值:
```
people:
  name: chx
  age: 20
  address: 长沙
```

HelloController.java
我们可以这样注入People的Bean：
```java
@Autowired
    private People people;

@RequestMapping(value = "/people",method = RequestMethod.GET)
public People people() {
    return people;
}//返回的是对象的JSON字符串
```

我们看输出:  
![](https://i.imgur.com/6qGz9zD.png)  


#动态配置
比如我们开发的时候和发布的时候使用的数据库地址不同，我们可以这样配置  

新建两个配置文件，分别为:
application-dev.yml  开发使用
application-prod.yml  发布使用  

默认的配置文件中的内容可以删除了。写上:  
application.yml  
```
spring:
  profiles:
    active: dev
```
修改application-dev.yml中的值，和application-prod.yml不同即可，这个时候可以运行项目，打开链接，可以看到people的值是dev文件的内容  
你可以将dev改成prod，配置内容即是prod文件中的内容  

但是这样还不是动态，因为我们需要每次改变application.yml中的值。  
所以我们可以这样做，用上篇博客的启动方式  
也就是java -jar的启动方式  

首先编译一下：
```
mvn install
```
然后运行:
```
java -jar target/hello-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```
后面带上动态参数即可  

# 源代码下载地址：
<blockquote cite='陈浩翔'>
GITHUB源码下载地址:<strong>【<a href='https://github.com/chenhaoxiang/SpringBoot/tree/master/20171224/code/hello' target='_blank'>点我进行下载</a>】</strong>
</blockquote>