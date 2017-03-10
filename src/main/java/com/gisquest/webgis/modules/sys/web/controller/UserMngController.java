package com.gisquest.webgis.modules.sys.web.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gisquest.webgis.config.Global;
import com.gisquest.webgis.modules.sys.services.UserMngService;
import com.gisquest.webgis.util.Util;

/**
 * 控制用户管理操作
 * 
 * @author Jisj1
 *
 */
@Controller
public class UserMngController {
    /** 响应体 */
    @Autowired
    private HttpServletResponse response;

    /**
     * 用户登录
     * 
     * @param username
     *            用户名
     * @param password
     *            密码
     * @param session
     *            session域
     * @return 检测用户名和密码，成功返回"success",否则返回"error"
     */
    @RequestMapping("/admin/_login")
    @ResponseBody
    public String login(String username, String password, String remember, HttpSession session) {
        Util.ROOT_PATH = session.getServletContext().getRealPath("/");
        UserMngService mngService = new UserMngService();
        if (username.equals(mngService.getUsername())
                && password.equals(mngService.getCurrentPwd())) {
            session.setAttribute("username", Global.SESSION_ADMIN);
            // 如果记住密码被选中，则将用户名和密码存入cookie中
            if (remember != null) {
                Cookie unameCookie = new Cookie("username", username);
                Cookie pwdCookie = new Cookie("password", password);
                response.addCookie(unameCookie);
                response.addCookie(pwdCookie);
            } else {
                // 删除cookie
                Cookie unameCookie = new Cookie("username", null);
                Cookie pwdCookie = new Cookie("password", null);
                unameCookie.setMaxAge(0);
                unameCookie.setPath("/webgis/admin");
                response.addCookie(unameCookie);
                pwdCookie.setMaxAge(0);
                pwdCookie.setPath("/webgis/admin");
                response.addCookie(pwdCookie);
            }
            return "success";
        } else {
            return "error";
        }
    }

    /**
     * 用户修改密码
     * 
     * @param oldPwd
     *            旧密码
     * @param newPwd
     *            新密码
     * @return 若旧密码检验成功，返回"success",否则"error"
     */
    @RequestMapping("/admin/changePwd")
    @ResponseBody
    public String changePwd(String oldPwd, String newPwd) {
        UserMngService mngService = new UserMngService();
        return mngService.changePwd(oldPwd, newPwd);
    }

    /**
     * 解锁服务
     * 
     * @param pwd
     *            解锁密码
     * @return 验证成功返回"success",否则返回"error"
     */
    @RequestMapping("/admin/_unlock")
    @ResponseBody
    public String unlock(String pwd, HttpSession session) {
        UserMngService mngService = new UserMngService();
        if (pwd.equals(mngService.getCurrentPwd())) {
            skipLogin(session);
            return "success";
        } else {
            return "error";
        }
    }

    /**
     * 注销服务
     * 
     * @param session
     *            session域
     * @return "success"
     */
    @RequestMapping("/admin/_logout")
    @ResponseBody
    public String logout(HttpSession session) {
        // 清空session
        session.removeAttribute("username");
        session.invalidate();
        return "success";
    }

    /**
     * 跳过登录
     * 
     * @param session
     *            session域
     * @return "success"
     */
    public String skipLogin(HttpSession session) {
        session.setAttribute("username", Global.SESSION_ADMIN);
        return "success";
    }

    /**
     * 重置密码
     * 
     * @return "success"
     */
    @RequestMapping("/admin/resetPwd")
    @ResponseBody
    public String resetPwd() {
        UserMngService mngService = new UserMngService();
        changePwd(mngService.getCurrentPwd(), mngService.getUsername());
        return "success";
    }
}
