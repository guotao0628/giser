package com.gisquest.webgis.modules.sys.services;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gisquest.webgis.modules.sys.entity.IdentifyField;
import com.gisquest.webgis.modules.sys.entity.LoginRule;
import com.gisquest.webgis.modules.sys.entity.SaveField;
import com.gisquest.webgis.util.Util;
import com.google.gson.GsonBuilder;

/**
 * 基本配置服务
 * 
 * @author Jisj1
 *
 */
@Service("basicConfigService")
public class BasicConfigService {
  @Autowired
  private ResourceSolutionService reSolutionService;

  /**
   * 获取当前政务平台版本
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  public String getCurrentDzzwVersion() {
    String cfgJson = Util.getConfigJson(); // 获取config.json
    cfgJson = Util.addCorrectFormat(cfgJson); // 格式化config.json
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);
    if (cfgJsonList.size() > 0) {
      Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
      return (String) cfgJsonMap.get("dzzw");
    }
    return null;
  }

  /**
   * 保存电子政务版本
   * 
   * @param version
   */
  @SuppressWarnings("unchecked")
  public void saveDzzwVersion(String version) {
    String cfgJson = Util.getConfigJson(); // 获取config.json
    cfgJson = Util.addCorrectFormat(cfgJson); // 格式化config.json
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);
    if (cfgJsonList.size() > 0) {
      Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
      cfgJsonMap.put("dzzw", version);
    }
    // 保存config.json
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    String data = dataJson.toString().substring(1, (dataJson.toString().length() - 1));
    Util.writeJson(data);
  }

  /**
   * 获取登录规则池
   * 
   * @return 返回登录规则集合
   */
  @SuppressWarnings("unchecked")
  public List<LoginRule> getLoginRules() {
    List<LoginRule> loginRules = new ArrayList<>();
    String cfgJson = Util.getConfigJson(); // 获取config.json
    cfgJson = Util.addCorrectFormat(cfgJson); // 格式化config.json
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);
    if (cfgJsonList.size() > 0) {
      Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
      List<Map<String, Object>> rules = (List<Map<String, Object>>) cfgJsonMap.get("freeLogin");
      if (rules != null) {
        for (Map<String, Object> ruleMap : rules) {
          LoginRule loginRule = new LoginRule();
          loginRule.setId((String) ruleMap.get("id"));
          loginRule.setName((String) ruleMap.get("name"));
          loginRule.setIp((String) ruleMap.get("ip"));
          loginRules.add(loginRule);
        }
      }
    }
    return loginRules;
  }

  /**
   * 将规则的id和name保存到config.json
   * 
   * @param id
   *          规则id
   * @param name
   *          规则名称
   * @return
   */
  @SuppressWarnings("unchecked")
  public String saveLoginRule(LoginRule loginRule) {
    String id = loginRule.getId();
    String name = loginRule.getName();
    String ip = loginRule.getIp();
    String cfgJson = Util.getConfigJson(); // 获取config.json
    cfgJson = Util.addCorrectFormat(cfgJson); // 格式化config.json
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);
    if (cfgJsonList.size() > 0) {
      Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
      // 判断map中是否含有freeLogin字段
      if (cfgJsonMap.containsKey("freeLogin")) {
        // 有则获取规则池
        List<Map<String, Object>> rulePool = (List<Map<String, Object>>) cfgJsonMap
            .get("freeLogin");
        // 如果rulePool中的元素不为空，遍历规则池，判断规则id是否存在
        if (rulePool.size() > 0) {
          boolean isFound = false;
          for (Map<String, Object> ruleMap : rulePool) {
            if (ruleMap.get("id").equals(id)) {
              // 存在则编辑登录规则
              ruleMap.put("name", name);
              ruleMap.put("ip", ip);
              isFound = true;
              break;
            }
          }
          // 如果没找到，则新增登录规则
          if (!isFound) {
            Map<String, Object> ruleMap = new LinkedHashMap<>();
            ruleMap.put("id", id);
            ruleMap.put("name", name);
            ruleMap.put("ip", ip);
            rulePool.add(ruleMap);
          }
        } else {
          // 如果freeLogin中没有元素
          Map<String, Object> ruleMap = new LinkedHashMap<>();
          ruleMap.put("id", id);
          ruleMap.put("name", name);
          ruleMap.put("ip", ip);
          rulePool.add(ruleMap);
        }
      } else {
        // 找不到freeLogin字段，则添加该字段
        List<LoginRule> loginRules = new ArrayList<>();
        loginRules.add(loginRule);
        cfgJsonMap.put("freeLogin", loginRules);
      }
    }
    // 保存config.json
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    String data = dataJson.toString().substring(1, (dataJson.toString().length() - 1));
    return Util.writeJson(data);
  }

  /**
   * 删除登录规则
   * 
   * @param loginRule
   *          登录规则
   * @return
   */
  @SuppressWarnings("unchecked")
  public String removeLoginRule(LoginRule loginRule) {
    String id = loginRule.getId();
    String cfgJson = Util.getConfigJson(); // 获取config.json
    cfgJson = Util.addCorrectFormat(cfgJson); // 格式化config.json
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);
    if (cfgJsonList.size() > 0) {
      Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
      List<Map<String, Object>> rulePool = (List<Map<String, Object>>) cfgJsonMap.get("freeLogin");
      for (Map<String, Object> ruleMap : rulePool) {
        if (ruleMap.get("id").equals(id)) {
          rulePool.remove(ruleMap);
          break;
        }
      }
    }
    // 保存config.json
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    String data = dataJson.toString().substring(1, (dataJson.toString().length() - 1));
    return Util.writeJson(data);
  }

  /**
   * 导出配置
   * 
   * @param rootPath
   *          根目录路径
   */
  public String exportConfig(String rootPath) {
    String filename = "/config";
    String cfgJsonPath = rootPath + "/config.json";
    String roleJsonPath = rootPath + "/role.json";
    String reSolutionJsonPath = rootPath + "/reSolutions.json";
    String bookmarksJsonPath = rootPath + "/bookmarks.json";
    String uploadImgPath = rootPath + "/admin/UploadImgs";
    String cfgPropPath = rootPath + "/WEB-INF/classes/config.properties";
    String c3p0Path = rootPath + "/WEB-INF/classes/c3p0-config.xml";
    String adminPropPath = rootPath + "/WEB-INF/classes/admin.properties";
    String zipPath = rootPath + filename;
    File zipFile = new File(zipPath);
    File classesFile = new File(zipPath + "/WEB-INF/classes");
    File adminFile = new File(zipPath + "/admin");
    // 创建webgis文件夹
    adminFile.mkdirs();
    classesFile.mkdirs();
    try {
      // 拷贝config.json,config.properties,c3p0-config.xml,admin.properties和UploadImgs文件夹到指定文件夹中
      File cfgJsonFile = new File(cfgJsonPath);
      if (cfgJsonFile.exists()) {
        FileUtils.copyFileToDirectory(cfgJsonFile, zipFile);
      }
      File roleJsonFile = new File(roleJsonPath);
      if (roleJsonFile.exists()) {
        FileUtils.copyFileToDirectory(roleJsonFile, zipFile);
      }
      File reSolutionJsonFile = new File(reSolutionJsonPath);
      if (reSolutionJsonFile.exists()) {
        FileUtils.copyFileToDirectory(reSolutionJsonFile, zipFile);
      }
      File bookmarksJsonFile = new File(bookmarksJsonPath);
      if (bookmarksJsonFile.exists()) {
        FileUtils.copyFileToDirectory(bookmarksJsonFile, zipFile);
      }
      File cfgPropFile = new File(cfgPropPath);
      if (cfgPropFile.exists()) {
        FileUtils.copyFileToDirectory(cfgPropFile, classesFile);
      }
      File c3p0File = new File(c3p0Path);
      if (c3p0File.exists()) {
        FileUtils.copyFileToDirectory(c3p0File, classesFile);
      }
      File adminPropFile = new File(adminPropPath);
      if (adminPropFile.exists()) {
        FileUtils.copyFileToDirectory(adminPropFile, classesFile);
      }
      File uploadImgFile = new File(uploadImgPath);
      if (uploadImgFile.exists()) {
        FileUtils.copyDirectoryToDirectory(uploadImgFile, adminFile);
      }
      // 压缩config.json,config.properties,c3p0-config.xml,admin.properties和admin\UploadImgs文件夹，生成压缩文件
      Util.zipFile(zipPath, rootPath);
      // 删除webgis文件
      if (zipFile.exists()) {
        FileUtils.deleteDirectory(zipFile);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return filename + ".zsjs";
  }

  /**
   * 更新配置文件
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  public String updateConfig() {
    // 互斥组件
    String[] mutexWidgetIds = {
        "customDijit/Measurement_Widget_MEAS3CF32A7218794753B116F8E136C6C411",
        "customDijit/MapLabel_Widget_f094f2ec-24cd-49a6-a9a7-920485823f6c",
        "customDijit/ClearTool_Widget_435285fde-63e3-4a99-add2-da64ad48575c",
        "customDijit/EditTool_Widget_b27dat14-5c70-4e7d-86e2-do9d96d00ab3a",
        "customDijit/Save_Widget_b27da614-5c70-4e7d-86e2-bdd96d00ab3a",
        "customDijit/DeleteTool_Widget_7ca9bfde-63e3-4a99-add2-da64ad47375c",
        "customDijit/PolygonTool_Widget_5e4d69ac-9593-4791-b4f4-22a137a79073",
        "customDijit/Identify_Widget_36fcf5eb-09f2-4669-9caf-283a6b2873d0",
        "customDijit/BufferAnalyze_Widget_694b96a1-s23f-f5g2-ad14-gf85fv021g4",
        "customDijit/SelectTool_Widget_9bf3ec53-9a0f-42b0-a203-ba9afa1b7246"};
    String cfgJson = Util.getConfigJson(); // 获取config.json
    Util.writeJson(cfgJson, "config_backup.json"); // 备份配置文件
    String cfgJsonUpdate = Util.getConfigJson("config_update.json"); // 获取config_update.json
    cfgJson = Util.addCorrectFormat(cfgJson); // 格式化config.json
    cfgJsonUpdate = Util.addCorrectFormat(cfgJsonUpdate);
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);
    List<Map<String, Object>> cfgJsonUpdateList = new GsonBuilder().create().fromJson(cfgJsonUpdate,
        List.class);
    if (cfgJsonList.size() > 0) {
      Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
      // 2.2 更新： 将根节点的analyShow字段放入分析的widget里面
      String analyShow = (String) cfgJsonMap.get("analyShow");
      cfgJsonMap.remove("analyShow");
      // 2.0更新：将widgetPool中的panel和floatPanel转成新版中对应的项
      if (cfgJsonMap.containsKey("widgetPool")) {
        Map<String, Object> widgetPoolMap = (Map<String, Object>) cfgJsonMap.get("widgetPool");
        // 2.1更新：将模块的ID中第二个“/”改成“_”，添加图例预置组件和缓冲区分析功能组件
        if (widgetPoolMap.containsKey("widgets")) {
          List<Map<String, Object>> widgets = (List<Map<String, Object>>) widgetPoolMap
              .get("widgets");
          boolean hasBufferWidget = false;
          for (Map<String, Object> widget : widgets) {
            // 2.2 更新： 将根节点的analyShow字段放入分析的widget里面
            if ("customDijit/Analyze_Widget_694b96a1-f305-4fad-8f1b-9eb35f991ada"
                .equals(widget.get("id"))) {/* 如果模块为分析模块,则设置分析结果显示方案 */
              if (!StringUtils.isEmpty(analyShow)) {
                widget.put("analyShow", analyShow);
              }
            }
            if ("customDijit/BufferAnalyze_Widget_694b96a1-s23f-f5g2-ad14-gf85fv021g4"
                .equals(widget.get("id"))) {
              hasBufferWidget = true;
            }
            // 如果不是模块组，则将模块的ID中第二个“/”改成“_”
            if (!widget.containsKey("widgets")) {
              String originId = (String) widget.get("id");
              widget.put("id", originId.replace("/Widget", "_Widget"));
              // 2.1更新，如果是清除组件，则将其ID改为customDijit/ClearTool_Widget_435285fde-63e3-4a99-add2-da64ad48575c
              if ("ClearTool".equals(widget.get("name"))) {
                widget.put("id",
                    "customDijit/ClearTool_Widget_435285fde-63e3-4a99-add2-da64ad48575c");
              }
              // 2.1更新，将部分组件互斥字段改为true
              if (ArrayUtils.contains(mutexWidgetIds, widget.get("id"))) {
                widget.put("isPanelMutex", true);
              }
            } else {
              // 如果是模块组，则遍历模块组
              List<Map<String, Object>> innerWidgets = (List<Map<String, Object>>) widget
                  .get("widgets");
              for (Map<String, Object> innerWidget : innerWidgets) {
                String originId = (String) innerWidget.get("id");
                innerWidget.put("id", originId.replace("/Widget", "_Widget"));
                // 2.1更新，如果是清除组件，则将其ID改为customDijit/ClearTool_Widget_435285fde-63e3-4a99-add2-da64ad48575c
                if ("ClearTool".equals(innerWidget.get("name"))) {
                  innerWidget.put("id",
                      "customDijit/ClearTool_Widget_435285fde-63e3-4a99-add2-da64ad48575c");
                }
                // 2.1更新，将部分组件互斥字段改为true
                if (ArrayUtils.contains(mutexWidgetIds, innerWidget.get("id"))) {
                  innerWidget.put("isPanelMutex", true);
                }
                // 2.2 更新： 将根节点的analyShow字段放入分析的widget里面
                if ("customDijit/Analyze_Widget_694b96a1-f305-4fad-8f1b-9eb35f991ada"
                    .equals(innerWidget.get("id"))) {/* 如果模块为分析模块,则设置分析结果显示方案 */
                  if (!StringUtils.isEmpty(analyShow)) {
                    innerWidget.put("analyShow", analyShow);
                  }
                }
              }
            }
          }
          // 如果不存在缓冲区查询组件
          if (!hasBufferWidget) {
            // 新增一个缓冲区查询功能组件
            Map<String, Object> bufferWidget = new LinkedHashMap<>();
            bufferWidget.put("name", "buffer");
            bufferWidget.put("label", "缓冲区查询");
            bufferWidget.put("uri", "customDijit/BufferAnalyze/Widget");
            bufferWidget.put("panelType", "FLOAT_PANEL");
            bufferWidget.put("noTogglen", false);
            bufferWidget.put("isPanelMutex", true);
            bufferWidget.put("index", 991);
            bufferWidget.put("id",
                "customDijit/BufferAnalyze_Widget_694b96a1-s23f-f5g2-ad14-gf85fv021g4");
            bufferWidget.put("icon", "./custom/dijit/BufferAnalyze/images/buffer.png");
            bufferWidget.put("regPosition", "header-top-right");
            bufferWidget.put("icon", "./custom/dijit/BufferAnalyze/images/buffer.png");
            bufferWidget.put("stat", "enable");
            widgets.add(bufferWidget);
          }
        }

        if (cfgJsonUpdateList.size() > 0) {
          Map<String, Object> cfgJsonUpdateMap = cfgJsonUpdateList.get(0);
          if (cfgJsonUpdateMap.containsKey("widgetPool")) {
            Map<String, Object> widgetPoolUpdateMap = (Map<String, Object>) cfgJsonUpdateMap
                .get("widgetPool");
            widgetPoolMap.put("panel", widgetPoolUpdateMap.get("panel"));
            widgetPoolMap.put("floatPanel", widgetPoolUpdateMap.get("floatPanel"));
          }
        }
      }
      // 2.0更新：将preloadWidgets替换成新版本中的preloadWidgets
      if (cfgJsonUpdateList.size() > 0) {
        Map<String, Object> cfgJsonUpdateMap = cfgJsonUpdateList.get(0);
        if (cfgJsonUpdateMap.containsKey("preloadWidgets")) {
          Map<String, Object> preloadWidgetsUpdateMap = (Map<String, Object>) cfgJsonUpdateMap
              .get("preloadWidgets");
          cfgJsonMap.put("preloadWidgets", preloadWidgetsUpdateMap);
        }
      }

      // 2.0更新：在导入导出中增加 简单自定义点格式(*.txt)和 简单自定义线格式(*.txt)
      Map<String, Object> point = new LinkedHashMap<>();
      point.put("id", "ZSPOINT");
      point.put("label", "简单自定义点格式(*.txt)");
      point.put("ext", "ZSPOINT|.txt");
      point.put("stat", "enable");
      Map<String, Object> line = new LinkedHashMap<>();
      point.put("id", "ZSLINE");
      point.put("label", "简单自定义线格式(*.txt)");
      point.put("ext", "ZSLINE|.txt");
      point.put("stat", "enable");
      // 导出table
      List<Map<String, Object>> exportTableList = (List<Map<String, Object>>) cfgJsonMap
          .get("ExportFileTypeTable");
      boolean isZSPointFound = false;
      boolean isZSLineFound = false;
      boolean isDXFFound = false;
      Map<String, Object> dxfMap = new LinkedHashMap<>();
      for (Map<String, Object> map : exportTableList) {
        if (map != null) {
          if (map.get("id") != null && map.get("id").equals("ZSPOINT")) {
            isZSPointFound = true;
          }
          if (map.get("id") != null && map.get("id").equals("ZSLINE")) {
            isZSLineFound = true;
          }
          if (map.get("id") != null && map.get("id").equals("dxf")) {
            // 2.2更新：去掉dxf的导出
            dxfMap = map;
            isDXFFound = true;
          }
          if (isZSPointFound && isZSLineFound && isDXFFound) {
            break;
          }
        }
      }
      if (!isZSPointFound) {
        exportTableList.add(point);
      }
      if (!isZSLineFound) {
        exportTableList.add(line);
      }
      if (isDXFFound) {
        exportTableList.remove(dxfMap);
      }
      // 导入table
      List<Map<String, Object>> importTableList = (List<Map<String, Object>>) cfgJsonMap
          .get("ImportFileTypeTable");
      isZSPointFound = false;
      isZSLineFound = false;
      for (Map<String, Object> map : importTableList) {
        if (map != null) {
          if (map.get("id") != null && map.get("id").equals("ZSPOINT")) {
            isZSPointFound = true;
          }
          if (map.get("id") != null && map.get("id").equals("ZSLINE")) {
            isZSLineFound = true;
          }
          if (isZSPointFound && isZSLineFound) {
            break;
          }
        }
      }
      if (!isZSPointFound) {
        importTableList.add(point);
      }
      if (!isZSLineFound) {
        importTableList.add(line);
      }

      // 2.0更新：在toc图层中的矢量图层添加字段geometryType,topologyCheck
      List<Map<String, Object>> toc = (List<Map<String, Object>>) cfgJsonMap.get("toc");
      for (Map<String, Object> map : toc) {
        if (map.containsKey("type")
            && (map.get("type").equals("dynamic") || map.get("type").equals("feature"))) {
          // 2.2更新：将selectLayer和keyField合并成saveField字段, 如果没有selectLayer就加上
          if (!map.containsKey("selectLayer")) {
            map.put("selectLayer", "0");
          }
          if (!map.containsKey("saveField")) {
            SaveField saveField = new SaveField();
            saveField.setSelectLayer((String) map.get("selectLayer"));
            saveField.setKeyField("");
            List<SaveField> saveFields = new ArrayList<>();
            saveFields.add(saveField);
            map.put("saveField", saveFields);
          }
          // 如果toc_map中含 有type字段，并值为dynamic，则添加字段geometryType:
          // "polygon"
          if (!map.containsKey("geometryType")) {
            // 没有这个字段才添加，并赋默认值polygon
            map.put("geometryType", "polygon");
          }
        }
        // 只有矢量才有topologyCheck字段
        if (map.containsKey("type") && map.get("type").equals("dynamic")) {
          if (!map.containsKey("topologyCheck")) {
            // 没有拓扑检查字段才添加，并赋默认值1
            map.put("topologyCheck", 1);
          }
        }
        /* 2.0版本删除了常用图层 */
        if (map.containsKey("isCommonUsed")) {
          map.remove("isCommonUsed");
        }
        /*
         * 如果toc中含有identifyUrl字段，则删除老版本中的identifyUrl,display和search，
         * 并合并为identifyField字段
         */
        if (map.containsKey("identifyUrl")) {
          String lyr = (String) map.get("identifyUrl");
          map.remove("identifyUrl");
          String search = "";
          String display = "";
          if (map.containsKey("search")) {
            search = (String) map.get("search");
            map.remove("search");
          }
          if (map.containsKey("display")) {
            display = (String) map.get("display");
            map.remove("display");
          }
          if ("".equals(lyr)) {
            if (map.get("type").equals("dynamic")) { // 如果是矢量图层
              // 获取visibleLayers
              List<String> visibleLayers = (List<String>) map.get("visibleLayers");
              if (visibleLayers.size() == 0) { // 如果可见图层为空，则设置查询地址为第0层
                List<IdentifyField> fields = new ArrayList<>();
                IdentifyField field = new IdentifyField();
                field.setLyr(map.get("url") + "/0");
                field.setSearch("");
                field.setDisplay("");
                fields.add(field);
                map.put("identifyField", fields);
              } else { // 否则遍历可见图层
                List<IdentifyField> fields = new ArrayList<>();
                for (String visibleLayer : visibleLayers) {
                  IdentifyField field = new IdentifyField();
                  field.setLyr(map.get("url") + "/" + visibleLayer);
                  field.setSearch("");
                  field.setDisplay("");
                  fields.add(field);
                }
                map.put("identifyField", fields);
              }
            } else {
              map.put("identifyField", new ArrayList<>());
            }
          } else {
            List<IdentifyField> fields = new ArrayList<>();
            IdentifyField field = new IdentifyField();
            field.setLyr(lyr);
            field.setSearch(search);
            field.setDisplay(display);
            fields.add(field);
            map.put("identifyField", fields);
          }
        }
      }

      // 2.0更新：规划分析中，如果没有"isAutoGetXZQ"，"xzqCode"，"isUseGZQ"，"isUseYTQ"，"isUseGHYT"则给予默认值
      List<Map<String, Object>> analyze = (List<Map<String, Object>>) cfgJsonMap.get("analyze");
      for (Map<String, Object> map : analyze) {
        /* 如果是规划分析 */
        if (map.get("fxType").equals("1") || map.get("fxType").equals("0")) {
          if (!map.containsKey("xzqCode")) {
            map.put("xzqCode", "");
          }
          if (!map.containsKey("isAutoGetXZQ")) {
            map.put("isAutoGetXZQ", true);
          }
        }
      }
    }
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    String data = dataJson.toString().substring(1, (dataJson.toString().length() - 1));
    return Util.writeJson(data);
  }

  /**
   * 更新role.json文件
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  public void updateRoleJson() {
    // 获取role.json
    String roleJson = Util.getRoleJson();
    Map<String, Object> roleJsonMap = new GsonBuilder().create().fromJson(roleJson, Map.class);
    if (roleJsonMap != null) {
      List<Map<String, Object>> roles = (List<Map<String, Object>>) roleJsonMap.get("roles");
      // 将id中的/Widget替换成_Widget
      for (Map<String, Object> role : roles) {
        List<Map<String, Object>> privilegs = (List<Map<String, Object>>) role.get("privilegs");
        for (Map<String, Object> privileg : privilegs) {
          Map<String, Object> resource = (Map<String, Object>) privileg.get("resource");
          Map<String, Object> widgets = (Map<String, Object>) resource.get("widgets");
          if (widgets != null) {
            List<Map<String, Object>> preWidgets = (List<Map<String, Object>>) widgets
                .get("preWidgets");
            for (Map<String, Object> preWidget : preWidgets) {
              String originId = (String) preWidget.get("id");
              preWidget.put("id", originId.replace("/Widget", "_Widget"));
            }
            List<Map<String, Object>> poolWidgets = (List<Map<String, Object>>) widgets
                .get("poolWidgets");
            for (Map<String, Object> poolWidget : poolWidgets) {
              String originId = (String) poolWidget.get("id");
              poolWidget.put("id", originId.replace("/Widget", "_Widget"));
            }
          }
        }
      }
      // 将修改后role.json保存
      String result = new GsonBuilder().setPrettyPrinting().create().toJson(roleJsonMap);
      Util.writeRoleJson(result);
    }
  }

  @SuppressWarnings("unchecked")
  public void updateReSolutionJson() {
    // 获取reSolutions.json
    String json = reSolutionService.getReSolution();
    List<Map<String, Object>> reSolutions = new GsonBuilder().create().fromJson(json, List.class);
    if (reSolutions != null) {
      for (Map<String, Object> reSolution : reSolutions) {
        Map<String, Object> widgets = (Map<String, Object>) reSolution.get("widgets");
        List<Map<String, Object>> poolWidgets = (List<Map<String, Object>>) widgets
            .get("poolWidgets");
        for (Map<String, Object> poolWidget : poolWidgets) {
          String originId = (String) poolWidget.get("id");
          poolWidget.put("id", originId.replace("/Widget", "_Widget"));
        }
        List<Map<String, Object>> preWidgets = (List<Map<String, Object>>) widgets
            .get("preWidgets");
        for (Map<String, Object> preWidget : preWidgets) {
          String originId = (String) preWidget.get("id");
          preWidget.put("id", originId.replace("/Widget", "_Widget"));
        }
      }
    }
    // 将修改后reSolutions.json保存
    String result = new GsonBuilder().setPrettyPrinting().create().toJson(reSolutions);
    reSolutionService.writeReSolutionJson(result);
  }
}
