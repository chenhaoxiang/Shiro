package cn.chenhaoxiang.servlet;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 */
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("login doget");
        req.getRequestDispatcher("login.jsp").forward(req,resp);//转发
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println("login dopost");
        String userName = req.getParameter("userName");
        String password = req.getParameter("password");

        Subject subject =SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken(userName,password);

        try {
            subject.login(token);
            //获取Session  Shiro管理的Session
            Session session = subject.getSession();
            System.out.println("sessionId:"+session.getId());//用户会话的唯一id
            System.out.println("sessionHost:"+session.getHost());//获取主机地址
            System.out.println("sessionTimeOut:"+session.getTimeout());//获取超时时间,默认是半小时的,单位ms

            session.setAttribute("info","session数据");

            resp.sendRedirect("success.jsp");//跳转到成功页面 重定向
        } catch (AuthenticationException e) {
            e.printStackTrace();
            req.setAttribute("errorInfo","用户名或者密码错误");
            req.getRequestDispatcher("login.jsp").forward(req,resp);//转发
        }

    }
}
