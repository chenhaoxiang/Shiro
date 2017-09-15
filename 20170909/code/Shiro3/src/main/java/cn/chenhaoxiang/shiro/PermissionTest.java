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
