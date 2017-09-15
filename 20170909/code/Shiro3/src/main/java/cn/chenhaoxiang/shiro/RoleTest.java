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
