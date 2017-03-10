package com.gisquest.webgis.modules.sys.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gisquest.webgis.modules.sys.services.LocalConfigService;

/**
 * 本地配置控制器
 * 
 * @author Jisj1
 *
 */
@Controller
public class LocalConfigController {
    /** 请求体 */
    @Autowired
    private HttpServletRequest request;

    /**
     * 设置是否使用zookeeper
     * 
     * @param useZK
     *            true为使用，false为不使用
     * @return 返回localConfig.json串
     */
    @RequestMapping(value = "/admin/localConfig", method = RequestMethod.POST)
    @ResponseBody
    public String setZKState(boolean useZK) {
        /* 项目根目录 */
        String rootPath = request.getSession().getServletContext().getRealPath("/");
        LocalConfigService lcService = new LocalConfigService();
        return lcService.setZKState(rootPath, useZK);
    }

    /**
     * 获取zookeeper使用状态
     * 
     * @return true为使用，false为不使用
     */
    @RequestMapping("/admin/localConfig/useZK")
    @ResponseBody
    public Boolean getZKState() {
        /* 项目根目录 */
        String rootPath = request.getSession().getServletContext().getRealPath("/");
        LocalConfigService lcService = new LocalConfigService();
        return lcService.getZKState(rootPath);
    }
}
