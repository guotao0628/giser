package com.gisquest.webgis.modules.sys.web.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.gisquest.webgis.config.Global;
import com.gisquest.webgis.modules.sys.entity.LoginRule;
import com.gisquest.webgis.modules.sys.services.BasicConfigService;
import com.gisquest.webgis.modules.sys.web.controller.UserMngController;
import com.gisquest.webgis.util.Util;

/**
 * 用户登陆拦截器
 * 
 * @author Jisj1
 *
 */
public class UserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {

        String username = (String) request.getSession().getAttribute("username");
        if (username != null && username.equals(Global.SESSION_ADMIN)) {
            return true;
        } else {
            // 获取请求来源IP
            String localAddr = request.getHeader("REFERER");
            // 获取登录规则池中的规则
            Util.G_REQUEST = request;
            BasicConfigService configService = new BasicConfigService();
            List<LoginRule> loginRules = configService.getLoginRules();
            // 遍历登录规则池中的IP
            boolean isFound = false;
            for (LoginRule loginRule : loginRules) {
                // 判断请求来源IP是否存在于规则池中,若存在跳过登录
                if (localAddr != null && localAddr.contains(loginRule.getIp())) {
                    new UserMngController().skipLogin(request.getSession());
                    isFound = true;
                    break;
                }
            }
            // 如果没找到,则跳转到登录页面
            if (!isFound) {
                // 添加redirect参数，参数代表登陆成功后跳转的页面
                String uri = request.getRequestURI() + "?" + request.getQueryString();
                String prefix = "admin/";
                int index = uri.indexOf(prefix);
                String redirectPage = "";
                if (index != -1) {
                    redirectPage = uri.substring(index + prefix.length());
                }
                String requestType = request.getHeader("X-Requested-With");
                if (requestType != null && requestType.equals("XMLHttpRequest")) {
                    response.setStatus(404);
                } else {
                    response.sendRedirect(
                            request.getContextPath() + "/admin/login.html?t=" + redirectPage);
                }
                return false;
            }
            return true;
        }

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) throws Exception {
    }

}
