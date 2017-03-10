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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gisquest.webgis.modules.sys.dto.AjaxResult;
import com.gisquest.webgis.modules.sys.entity.PrintTemplate;
import com.gisquest.webgis.util.Util;
import com.google.gson.GsonBuilder;

/**
 * 自定义制图控制器
 * 
 * @author Jisj1
 *
 */
@Controller
@RequestMapping("/admin")
public class CustomPrintController {
  /** 请求体 */
  @Autowired
  private HttpServletRequest request;

  /**
   * 保存自定义制图服务
   * 
   * @param url
   */
  @SuppressWarnings("unchecked")
  @RequestMapping(value = "/CustomMapService/", method = RequestMethod.POST)
  @ResponseBody
  public String saveCustomPrintService(String url) {
    Util.G_REQUEST = request;
    String cfgJson = Util.addCorrectFormat(Util.getConfigJson());
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);
    if (cfgJsonList.size() > 0) {
      Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
      cfgJsonMap.put("CustomMapService", url);
    }
    // 保存config.json
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    String data = dataJson.toString().substring(1, (dataJson.toString().length() - 1));
    Util.writeJson(data);
    return "保存自定义制图服务" + url + "成功!";
  }

  /**
   * 提交制图模板
   * 
   * @param template
   *          模板对象
   */
  @SuppressWarnings("unchecked")
  @RequestMapping(value = "/printTemplates", method = RequestMethod.POST)
  @ResponseBody
  public void postTemplate(@RequestBody PrintTemplate template) {
    Util.G_REQUEST = request;
    String cfgJson = Util.addCorrectFormat(Util.getConfigJson());
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);
    String id = template.getId();
    if (cfgJsonList.size() > 0) {
      Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
      if (cfgJsonMap.containsKey("CustomMapTemplate")) {
        List<Map<String, Object>> templates = (List<Map<String, Object>>) cfgJsonMap
            .get("CustomMapTemplate");
        boolean isFound = false;
        for (Map<String, Object> item : templates) {
          if (item.get("id").equals(id)) {
            item.put("templateName", template.getTemplateName());
            item.put("mapUnit", template.getMapUnit());
            item.put("elements", template.getElements());
            item.put("pageOri", template.getPageOri());
            item.put("pageSize", template.getPageSize());
            isFound = true;
            break;
          }
        }
        if (!isFound) {
          // 如果没有找到，则操作为新增
          template.setStat("enable");
          templates.add(Util.beanToMap(template));
        }
      } else {
        // 如果不存在CustomMapTemplate，则操作为新增
        List<Map<String, Object>> templates = new ArrayList<>();
        template.setStat("enable");
        templates.add(Util.beanToMap(template));
        cfgJsonMap.put("CustomMapTemplate", templates);
      }
    }
    // 保存config.json
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    String data = dataJson.toString().substring(1, (dataJson.toString().length() - 1));
    Util.writeJson(data);
  }

  /**
   * 删除制图模板
   * 
   * @param id
   *          模板id
   */
  @SuppressWarnings("unchecked")
  @RequestMapping(value = "/printTemplates", method = RequestMethod.DELETE)
  @ResponseBody
  public void deleteTemplate(@RequestBody String id) {
    Util.G_REQUEST = request;
    String cfgJson = Util.addCorrectFormat(Util.getConfigJson());
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);
    if (cfgJsonList.size() > 0) {
      Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
      if (cfgJsonMap.containsKey("CustomMapTemplate")) {
        List<Map<String, Object>> templates = (List<Map<String, Object>>) cfgJsonMap
            .get("CustomMapTemplate");
        for (Map<String, Object> item : templates) {
          if (item.get("id").equals(id)) {
            templates.remove(templates.indexOf(item));
            break;
          }
        }
      }
    }
    // 保存config.json
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    String data = dataJson.toString().substring(1, (dataJson.toString().length() - 1));
    Util.writeJson(data);
  }

  /**
   * 清除所有制图模板
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  @RequestMapping(value = "/printTemplates/clear", method = RequestMethod.DELETE)
  @ResponseBody
  public AjaxResult<List<PrintTemplate>> clearTemplates() {
    AjaxResult<List<PrintTemplate>> ajaxResult = new AjaxResult<>();
    Util.G_REQUEST = request;
    String cfgJson = Util.addCorrectFormat(Util.getConfigJson());
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);
    if (cfgJsonList.size() > 0) {
      Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
      if (cfgJsonMap.containsKey("CustomMapTemplate")) {
        List<Map<String, Object>> templates = (List<Map<String, Object>>) cfgJsonMap
            .get("CustomMapTemplate");
        templates.clear();
        ajaxResult.setSuccess(true);
      }
    }
    // 保存config.json
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    String data = dataJson.toString().substring(1, (dataJson.toString().length() - 1));
    Util.writeJson(data);
    return ajaxResult;
  }

  /**
   * 设置制图模板启用状态
   * 
   * @param id
   *          模板id
   * @param stat
   *          启用状态
   */
  @SuppressWarnings("unchecked")
  @RequestMapping(value = "/printTemplates/{id}", method = RequestMethod.POST)
  @ResponseBody
  public void setState(@PathVariable String id, String stat) {
    Util.G_REQUEST = request;
    String cfgJson = Util.addCorrectFormat(Util.getConfigJson());
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);
    if (cfgJsonList.size() > 0) {
      Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
      if (cfgJsonMap.containsKey("CustomMapTemplate")) {
        List<Map<String, Object>> templates = (List<Map<String, Object>>) cfgJsonMap
            .get("CustomMapTemplate");
        for (Map<String, Object> item : templates) {
          if (item.get("id").equals(id)) {
            item.put("stat", stat);
            break;
          }
        }
      }
    }
    // 保存config.json
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    String data = dataJson.toString().substring(1, (dataJson.toString().length() - 1));
    Util.writeJson(data);
  }
}
