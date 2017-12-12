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
