package cn.chenhaoxiang.dao;

import cn.chenhaoxiang.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Dao
 */
public class UserDao {

    /**
     * 通过用户名获取用户信息
     * @param con
     * @param userName
     * @return
     * @throws Exception
     */
    public User getByUserName(Connection con, String userName)throws  Exception{
        User resultUser =null;//实际查询出来的用户
        String sql = "select * from t_user where userName=?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1,userName);
        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()){//能查询到
            resultUser = new User();
            resultUser.setId(resultSet.getInt("id"));
            resultUser.setUserName(resultSet.getString("userName"));
            resultUser.setPassword(resultSet.getString("password"));
        }
        return resultUser;
    }

    /**
     * 通过用户名查询出角色
     * @param con
     * @param userName
     * @return
     */
    public Set<String> getRoles(Connection con, String userName) throws SQLException {
        Set<String> roles = new HashSet<String>();
        String sql = "select * from t_user u,t_role r where u.roleId = r.id and u.userName=?";//没有使用级联
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1,userName);
        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()){//能查询到
            roles.add(resultSet.getString("roleName"));
        }
        return roles;
    }

    /**
     * 通过用户名获取权限
     * @param con
     * @param userName
     * @return
     * @throws SQLException
     */
    public Set<String> getPermissions(Connection con, String userName) throws SQLException {
        Set<String> permission = new HashSet<String>();
        String sql = "select from t_user u,t_role r,t_permission p where u.roleId=r.id and p.roleId=r.id and u.userName=?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1,userName);
        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()){//能查询到
            permission.add(resultSet.getString("permissionName"));
        }
        return permission;
    }
}
