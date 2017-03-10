package com.gisquest.webgis.modules.sys.web.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gisquest.platform.common.utils.FileUtils;
import com.gisquest.webgis.modules.sys.entity.TSolution;
import com.gisquest.webgis.modules.sys.services.LocalConfigService;
import com.gisquest.webgis.util.Util;
import com.google.gson.GsonBuilder;

/**
 * 图层方案控制器
 * 
 * @author Jisj1
 *
 */
@Controller
public class SolController {
    /** 请求体 */
    @Autowired
    private HttpServletRequest request;

    /**
     * 根据方案ID获取单个图层方案
     * 
     * @param id
     *            图层方案ID
     * @return 图层方案JSON串
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/solutions/{id}")
    @ResponseBody
    public String getSolutionById(@PathVariable String id) {
        Util.G_REQUEST = request;
        /* 获取并格式化config.json */
        String cfgJson = Util.addCorrectFormat(Util.getConfigJson());
        List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
                List.class);
        if (cfgJsonList.size() > 0) {
            Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
            if (cfgJsonMap.containsKey("solution")) {
                List<Map<String, Object>> solutions = (List<Map<String, Object>>) cfgJsonMap
                        .get("solution");
                for (Map<String, Object> solution : solutions) {
                    if (id.equals(solution.get("id"))) {
                        return new GsonBuilder().setPrettyPrinting().create().toJson(solution);
                    }
                }
                return "Error: 未找到id为" + id + "的图层方案！";
            }
        }
        return "Error: 在解析config.json时出现错误！";
    }

    /**
     * 根据ID或名称获取一个或多个图层方案
     * @param ids 图层ID列表，用逗号分隔
     * @param names 图层名称列表，用逗号分隔
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/solutions/")
    @ResponseBody
    public String getSolutions(String[] ids, String[] names) {
        Util.G_REQUEST = request;
        /* 获取并格式化config.json */
        String cfgJson = Util.addCorrectFormat(Util.getConfigJson());
        List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
                List.class);
        if (cfgJsonList.size() > 0) {
            Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
            if (cfgJsonMap.containsKey("solution")) {
                List<Map<String, Object>> solutionList = (List<Map<String, Object>>) cfgJsonMap
                        .get("solution");
                List<Map<String, Object>> tempList = solutionList;
                List<Map<String, Object>> resultList = new ArrayList<>();
                if (ids != null) {
                    // 遍历ids
                    for (String id : ids) {
                        for (Map<String, Object> solution : tempList) {
                            if (id.equals(solution.get("id"))) {
                                if (!resultList.contains(solution)) {
                                    resultList.add(solution);
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
                        for (Map<String, Object> solution : tempList) {
                            if (name.equals(solution.get("name"))) {
                                if (!resultList.contains(solution)) {
                                    resultList.add(solution);
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
        }
        return "Error: 在解析config.json时出现错误！";
    }

    /**
     * 保存图层方案
     * 
     * @param tSolution
     *            图层方案
     * @param file
     *            上传的方案缩略图文件
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/admin/solution", method = RequestMethod.POST)
    @ResponseBody
    public String saveSolution(@RequestBody TSolution tSolution) {
        Util.G_REQUEST = request;
        /* 获取方案信息 */
        String id = tSolution.getId();
        String name = tSolution.getName();
        String thumbnailUrl = tSolution.getThumbnailUrl();
        List<String> layerIds = tSolution.getLayerIds();
        List<String> resourceLayerIds = tSolution.getResourceLayerIds();
        /* 获取并格式化config.json */
        String cfgJson = Util.addCorrectFormat(Util.getConfigJson());
        List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
                List.class);
        if (cfgJsonList.size() > 0) {
            Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
            if (cfgJsonMap.containsKey("solution")) {
                List<Map<String, Object>> solutions = (List<Map<String, Object>>) cfgJsonMap
                        .get("solution");
                Boolean isSolutionFound = false;
                for (Map<String, Object> solution : solutions) {
                    if (solution.get("id").equals(id)) {
                        solution.put("name", name);
                        solution.put("layersId", layerIds);
                        solution.put("resourceLayersId", resourceLayerIds);
                        /* 如果没上传缩略图，则不修改原有缩略图 */
                        if (!thumbnailUrl.equals("")) {
                            /* 否则若原缩略图不为默认图，则删除原有缩略图 */
                            String originUrl = (String) solution.get("thumbnailUrl");
                            String rootPath = request.getSession().getServletContext()
                                    .getRealPath("/");
                            if (!originUrl.equals("admin/UploadImgs/th_default.png")) {
                                if (originUrl.startsWith("admin/")) {
                                    FileUtils.deleteFile(rootPath + File.separatorChar + originUrl);
                                } else if (originUrl.startsWith("UploadImgs/")) {
                                    FileUtils.deleteFile(rootPath + File.separatorChar + "admin"
                                            + File.separatorChar + originUrl);
                                }
                                LocalConfigService lcService = new LocalConfigService();
                                Boolean useZK = lcService.getZKState(rootPath);
                                if (useZK) {
                                    Util.deleteDataFromZK("/UploadImgs"
                                            + originUrl.substring(originUrl.lastIndexOf("/")));
                                }
                            }
                            /* 并修改缩略图路径 */
                            solution.put("thumbnailUrl", thumbnailUrl);
                        }
                        isSolutionFound = true;
                        break;
                    }
                }
                /* 若遍历完solution仍没找到，则新增方案 */
                if (!isSolutionFound) {
                    Map<String, Object> newSolution = new LinkedHashMap<>();
                    newSolution.put("id", id);
                    newSolution.put("name", name);
                    newSolution.put("layersId", layerIds);
                    newSolution.put("resourceLayersId", resourceLayerIds);
                    newSolution.put("thumbnailUrl", thumbnailUrl);
                    solutions.add(newSolution);
                }
            }
        }
        String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
        String data = dataJson.substring(1, (dataJson.toString().length() - 1));
        return Util.writeJson(data);
    }

    /**
     * 删除资源方案
     * 
     * @param id
     *            资源方案ID
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/admin/solution/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public String deleteSolution(@PathVariable String id) {
        Util.G_REQUEST = request;
        /* 获取并格式化config.json */
        String cfgJson = Util.addCorrectFormat(Util.getConfigJson());
        List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
                List.class);
        if (cfgJsonList.size() > 0) {
            Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
            if (cfgJsonMap.containsKey("solution")) {
                List<Map<String, Object>> solutions = (List<Map<String, Object>>) cfgJsonMap
                        .get("solution");
                for (Map<String, Object> solution : solutions) {
                    if (solution.get("id").equals(id)) {
                        /* 否则若原缩略图不为默认图，则删除原有缩略图 */
                        String originUrl = (String) solution.get("thumbnailUrl");
                        String rootPath = request.getSession().getServletContext().getRealPath("/");
                        if (!originUrl.equals("admin/UploadImgs/th_default.png")
                                && !originUrl.equals("")) {
                            if (originUrl.startsWith("admin/")) {
                                FileUtils.deleteFile(rootPath + File.separatorChar + originUrl);
                            } else if (originUrl.startsWith("UploadImgs/")) {
                                FileUtils.deleteFile(rootPath + File.separatorChar + "admin"
                                        + File.separatorChar + originUrl);
                            }
                            LocalConfigService lcService = new LocalConfigService();
                            Boolean useZK = lcService.getZKState(rootPath);
                            if (useZK) {
                                Util.deleteDataFromZK("/UploadImgs"
                                        + originUrl.substring(originUrl.lastIndexOf("/")));
                            }
                        }
                        solutions.remove(solution);
                        break;
                    }
                }
            }
        }
        String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
        String data = dataJson.substring(1, (dataJson.toString().length() - 1));
        return Util.writeJson(data);
    }
}
