package com.gisquest.webgis.modules.sys.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gisquest.webgis.modules.sys.entity.ResourceSolution;
import com.gisquest.webgis.modules.sys.services.ResourceSolutionService;
import com.gisquest.webgis.util.Util;
import com.google.gson.GsonBuilder;

/**
 * 资源方案Controller
 * 
 * @author Jisj1
 *
 */
@Controller
public class ReSolController {
    /** 请求体 */
    @Autowired
    private HttpServletRequest request;

    /**
     * @return the request
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * @param request
     *            the request to set
     */
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * 根据方案ID获取单个资源方案
     * 
     * @param id
     *            方案ID
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/reSolutions/{id}")
    @ResponseBody
    public String getReSolutionById(@PathVariable String id) {
        String reSolutionJson = getReSolution();
        List<Map<String, Object>> reSolutions = new GsonBuilder().create().fromJson(reSolutionJson,
                List.class);// 把reSolutions.json转为list
        for (Map<String, Object> reSolution : reSolutions) {
            if (id.equals(reSolution.get("id"))) {
                return new GsonBuilder().setPrettyPrinting().create().toJson(reSolution);
            }
        }
        return "找不到id为" + id + "的资源方案！";
    }

    /**
     * 根据ID或名称获取一个或多个资源方案
     * 
     * @param ids
     *            ID列表，用逗号分隔
     * @param names
     *            名称列表，用逗号分隔
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/reSolutions/")
    @ResponseBody
    public String getResolutions(String[] ids, String[] names) {
        String reSolutionJson = getReSolution();
        List<Map<String, Object>> reSolutionList = new GsonBuilder().create()
                .fromJson(reSolutionJson, List.class);// 把reSolutions.json转为list
        List<Map<String, Object>> tempList = reSolutionList;
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (ids != null) {
            // 遍历ids
            for (String id : ids) {
                for (Map<String, Object> reSolution : tempList) {
                    if (id.equals(reSolution.get("id"))) {
                        if (!resultList.contains(reSolution)) {
                            resultList.add(reSolution);
                        }
                    }
                }
            }
            tempList.clear();
            tempList.addAll(resultList);
            resultList.clear();
        }
        if (names != null) {
            // 遍历names
            for (String name : names) {
                for (Map<String, Object> reSolution : tempList) {
                    if (name.equals(reSolution.get("name"))) {
                        if (!resultList.contains(reSolution)) {
                            resultList.add(reSolution);
                        }
                    }
                }
            }
            tempList.clear();
            tempList.addAll(resultList);
            resultList.clear();
        }
        return new GsonBuilder().setPrettyPrinting().create().toJson(tempList);
    }

    /**
     * 获取资源方案配置reSolutions.json
     * 
     * @return
     */
    @RequestMapping("/admin/resource/solution")
    @ResponseBody
    public String getReSolution() {
        Util.ROOT_PATH = request.getSession().getServletContext().getRealPath("/");
        return new ResourceSolutionService().getReSolution();
    }

    /**
     * 保存资源方案(包括编辑和添加)
     * 
     * @param solution
     *            资源方案对象
     * @return
     */
    @RequestMapping("/admin/resource/saveSolution")
    @ResponseBody
    public void saveReSolution(@RequestBody ResourceSolution solution) {
        Util.ROOT_PATH = request.getSession().getServletContext().getRealPath("/");
        new ResourceSolutionService().saveReSolution(solution);
    }

    /**
     * 删除资源方案
     * 
     * @param solution
     *            资源方案对象
     * @return
     */
    @RequestMapping("/admin/resource/deleteSolution")
    @ResponseBody
    public void deleteReSolution(@RequestBody ResourceSolution solution) {
        Util.ROOT_PATH = request.getSession().getServletContext().getRealPath("/");
        new ResourceSolutionService().deleteReSolution(solution);
    }
}
