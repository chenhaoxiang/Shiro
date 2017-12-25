package cn.chenhaoxiang;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Value("${info}")
    private String info;

    @Autowired
    private People people;

    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    public String say() {
        return "Hello Spring Boot!";
    }

    @RequestMapping(value = "/info",method = RequestMethod.GET)
    public String info() {
        return name+","+age+",info:"+info;
    }

    @RequestMapping(value = "/people",method = RequestMethod.GET)
    public People people() {//返回的是对象的JSON字符串
        return people;
    }

}
