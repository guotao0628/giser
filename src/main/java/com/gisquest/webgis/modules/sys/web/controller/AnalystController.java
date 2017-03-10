package com.gisquest.webgis.modules.sys.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gisquest.webgis.util.Util;
import com.google.gson.GsonBuilder;

/**
 * 控制分析配置
 * 
 * @author Administrator
 *
 */
@Controller
public class AnalystController {
  /** 请求体 */
  @Autowired
  private HttpServletRequest request;

  /**
   * 根据分析项名称获取单个分析项
   * 
   * @param label
   *          分析项名称
   * @return
   */
  @SuppressWarnings("unchecked")
  @RequestMapping("/analyzes/{label}")
  @ResponseBody
  public String getAnalyzeByName(@PathVariable String label) {
    Util.G_REQUEST = request;
    String cfgJson = Util.addCorrectFormat(Util.getConfigJson()); // 获取并格式化config.json
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);// 把config.json转为list
    if (!cfgJsonList.isEmpty()) {
      Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
      if (cfgJsonMap.containsKey("analyze")) {
        List<Map<String, Object>> analyzeList = (List<Map<String, Object>>) cfgJsonMap
            .get("analyze");
        for (Map<String, Object> analyze : analyzeList) {
          if (label.equals(analyze.get("label")) && (!"".equals(analyze.get("fxType")))) {
            return new GsonBuilder().setPrettyPrinting().create().toJson(analyze);
          }
        }
        return "Error: 未找到名称为" + label + "的分析项！";
      }
    }
    return "Error: 在解析config.json时出现错误！";
  }

  /**
   * 根据分析项ID，分析项名称，分析项启用状态，分析年份来获取一个或多个分析项
   * 
   * @param ids
   *          分析项ID
   * @param labels
   *          分析项名称
   * @param state
   *          分析项启用状态
   * @param years
   *          分析年份
   * @return
   */
  @SuppressWarnings("unchecked")
  @RequestMapping("/analyzes/")
  @ResponseBody
  public String getAnalyzes(String[] ids, String[] labels, Boolean state, String[] years) {
    Util.G_REQUEST = request;
    String cfgJson = Util.addCorrectFormat(Util.getConfigJson()); // 获取并格式化config.json
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);// 把config.json转为list
    if (!cfgJsonList.isEmpty()) {
      Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
      if (cfgJsonMap.containsKey("analyze")) {
        List<Map<String, Object>> analyzeList = (List<Map<String, Object>>) cfgJsonMap
            .get("analyze");
        List<Map<String, Object>> analyzeWithoutFolder = new ArrayList<>();
        for (Map<String, Object> analyze : analyzeList) {
          if (!"".equals(analyze.get("fxType"))) {
            analyzeWithoutFolder.add(analyze);
          }
        }
        List<Map<String, Object>> tempList = analyzeWithoutFolder;
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (ids != null) {
          // 遍历ids
          for (String id : ids) {
            for (Map<String, Object> item : tempList) {
              if (id.equals(item.get("id"))) {
                if (!resultList.contains(item)) {
                  resultList.add(item);
                }
              }
            }
          }
          tempList.clear();
          tempList.addAll(resultList);
          resultList.clear();
        }
        if (labels != null) {
          // 遍历labels
          for (String label : labels) {
            for (Map<String, Object> item : tempList) {
              if (label.equals(item.get("label"))) {
                if (!resultList.contains(item)) {
                  resultList.add(item);
                }
              }
            }
          }
          tempList.clear();
          tempList.addAll(resultList);
          resultList.clear();
        }
        if (state != null) {
          for (Map<String, Object> item : tempList) {
            if (state == item.get("stat")) {
              if (!resultList.contains(item)) {
                resultList.add(item);
              }
            }
          }
          tempList.clear();
          tempList.addAll(resultList);
          resultList.clear();
        }
        if (years != null) {
          // 遍历years
          for (String year : years) {
            for (Map<String, Object> item : tempList) {
              if (year.equals(item.get("year"))) {
                if (!resultList.contains(item)) {
                  resultList.add(item);
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
   * 拖动节点
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  @RequestMapping("/admin/analyst/dragNode")
  @ResponseBody
  public String dragNode() {
    String[] nodeIds = request.getParameterValues("AnalyIDs[]");
    String[] nodeInds = request.getParameterValues("AnalyShowIndexs[]");
    String targetNodeId = request.getParameter("AnalyID");
    String targetParentId = request.getParameter("AnalyParent");
    /* 获取并解析config.json */
    Util.G_REQUEST = request;
    String cfgJson = Util.getConfigJson();
    cfgJson = Util.addCorrectFormat(cfgJson);
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);
    if (cfgJsonList.size() > 0) {
      /* 获取cfgJsonMap */
      Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
      if (cfgJsonMap.containsKey("analyze")) {
        /* 获取分析项列表 */
        List<Map<String, Object>> analyzeList = (List<Map<String, Object>>) cfgJsonMap
            .get("analyze");
        /* 遍历前端传来的nodeIds */
        for (int i = 0; i < nodeIds.length; i++) {
          /* 获取第i个Id */
          String nodeId = nodeIds[i];
          /* 遍历分析项列表 */
          for (Map<String, Object> map : analyzeList) {
            /* 在分析项列表中找到第i个Id */
            if (map.get("id").equals(nodeId)) {
              /* 改变节点的showIndex */
              map.put("showIndex", Integer.parseInt(nodeInds[i]));
              break;
            }
          }
        }
        /* 遍历分析项列表，找目标节点 */
        for (Map<String, Object> map : analyzeList) {
          /* 在分析项列表中找到第i个Id */
          if (map.get("id").equals(targetNodeId)) {
            /* 记录当前节点状态 */
            Boolean state = (Boolean) map.get("stat");
            /* 移动节点前，先将当前节点的状态设为false */
            map.put("stat", false);
            analyzeList = setParentNodeChecked((String) map.get("parent"), analyzeList, false);
            /* 改变目标节点的parent */
            map.put("parent", targetParentId);
            /* 改变节点状态为原来的状态 */
            map.put("stat", state);
            analyzeList = setParentNodeChecked((String) map.get("parent"), analyzeList, state);
            break;
          }
        }
      }
    }
    /* 保存config.json */
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    String data = dataJson.substring(1, (dataJson.length() - 1));
    return Util.writeJson(data);
  }

  /**
   * 设置分析项的启用状态
   * 
   * @param id
   *          分析项id
   * @param stat
   *          启用状态
   * @return
   */
  @SuppressWarnings("unchecked")
  @RequestMapping("/admin/analyst/setNodeChecked")
  @ResponseBody
  public String setNodeChecked(String id, boolean stat) {
    /* 获取并解析config.json */
    Util.G_REQUEST = request;
    String cfgJson = Util.getConfigJson();
    cfgJson = Util.addCorrectFormat(cfgJson);
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);
    if (cfgJsonList.size() > 0) {
      /* 获取cfgJsonMap */
      Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
      if (cfgJsonMap.containsKey("analyze")) {
        /* 获取分析项列表 */
        List<Map<String, Object>> analyzeList = (List<Map<String, Object>>) cfgJsonMap
            .get("analyze");
        /* 遍历分析项列表 */
        for (Map<String, Object> map : analyzeList) {
          /* 获取当前map的id，如果是传来的id */
          if (map.get("id") != null && map.get("id").equals(id)) {
            /* 改变当前map的状态 */
            map.put("stat", stat);
            /* 改变其子节点的状态 */
            analyzeList = setChildNodeChecked(id, analyzeList, stat);
            /* 改变其父节点的状态 */
            String parentId = (String) map.get("parent");
            analyzeList = setParentNodeChecked(parentId, analyzeList, stat);
          }
        }
      }
    }
    /* 保存config.json */
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    String data = dataJson.substring(1, (dataJson.toString().length() - 1));
    return Util.writeJson(data);
  }

  /**
   * 递归，根据节点启用状态，设置其所有子节点启用状态
   * 
   * @param analyId
   *          指定节点id
   * @param analys
   *          分析项列表
   * @param checked
   *          指定节点启用状态
   * @return
   */
  private List<Map<String, Object>> setChildNodeChecked(String analyId,
      List<Map<String, Object>> analys, boolean checked) {
    /* 遍历分析项列表 */
    for (Map<String, Object> map : analys) {
      /* 定位指定节点的子节点 */
      if (map.get("parent") != null && map.get("parent").equals(analyId)) {
        /* 设置子节点的启用状态 */
        map.put("stat", checked);
        /* 递归 */
        analys = setChildNodeChecked((String) (map.get("id")), analys, checked);
      }
    }
    return analys;
  }

  /**
   * 递归，根据指定节点的启用状态，设置其所有父节点的启用状态
   * 
   * @param parentId
   *          指定节点的父节点id
   * @param analys
   *          分析项列表
   * @param checked
   *          指定节点的启用状态
   * @return
   */
  private List<Map<String, Object>> setParentNodeChecked(String parentId,
      List<Map<String, Object>> analys, boolean checked) {
    if (!checked) {
      /* 设置flag，其父节点的checked默认为false */
      Boolean isParentChecked = false;
      /* 遍历分析项列表 */
      for (Map<String, Object> map : analys) {
        /* 找到父节点为指定id的子节点 */
        if (map.get("parent") != null && map.get("parent").equals(parentId)) {
          /* 获取该子节点的stat */
          Boolean stat = (Boolean) map.get("stat");
          if (stat) {
            /* 只要有一个子节点为true，则父节点的checked为true */
            isParentChecked = true;
            break;
          }
        }
      }

      /* 遍历分析项 */
      for (Map<String, Object> map : analys) {
        /* 找到父节点 */
        if (map.get("id") != null && map.get("id").equals(parentId)) {
          /* 设置父节点的状态 */
          map.put("stat", isParentChecked);
          if (!isParentChecked) {
            /* 递归，设置map父节点的状态 */
            analys = setParentNodeChecked((String) (map.get("parent")), analys, false);
          }
        }
      }
    } else if (checked == true) {
      /*
       * 如果该节点状态为true,则其所有父节点状态设为true
       */
      for (Map<String, Object> map : analys) {
        /* 如果找到其父节点 */
        if (map.get("id") != null && map.get("id").equals(parentId)) {
          /* 将其父节点的stat设为true */
          map.put("stat", true);
          /* 递归，设置map的父节点为true */
          analys = setParentNodeChecked((String) (map.get("parent")), analys, true);
          break;
        }
      }
    }
    return analys;
  }
}
