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
