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
