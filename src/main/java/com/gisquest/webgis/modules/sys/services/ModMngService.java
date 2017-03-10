package com.gisquest.webgis.modules.sys.services;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.gisquest.platform.common.utils.FileUtils;
import com.gisquest.webgis.modules.sys.entity.Module;
import com.gisquest.webgis.modules.sys.entity.ModuleGroup;
import com.gisquest.webgis.util.Util;
import com.google.gson.GsonBuilder;

/**
 * 模块管理服务
 * 
 * @author Jisj1
 *
 */
public class ModMngService {
  /**
   * 编辑模块方法
   * 
   * @param module
   *          模块对象
   */
  @SuppressWarnings("unchecked")
  public String editModule(Module module) {
    String id = module.getId();
    String label = module.getLabel();
    String name = module.getName();
    String icon = module.getIcon();
    String analyShow = module.getAnalyShow();
    boolean isFound = false;
    String cfgJson = Util.addCorrectFormat(Util.getConfigJson());// 格式化为Json
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);
    if (cfgJsonList.size() > 0) {
      // 遍历整个jsonList的List<Map>
      for (Map<String, Object> cfgJsonMap : cfgJsonList) {
        // 遍历整个Map的key
        for (String cfgJsonMapKey : cfgJsonMap.keySet()) {
          /* 如果模块为制图模块,则设置制图服务 */
          if (id.startsWith("customDijit/ExportMap")) {
            if (cfgJsonMapKey.equals("PrintMapService")) {
              cfgJsonMap.put(cfgJsonMapKey, module.getPrintUrl());
            }
          }
          // 如果集合中的key等于widgetPool
          if (cfgJsonMapKey.equals("widgetPool")) {
            // 获取widgetPool的value并存放在集合widgetPoolVal中
            Map<String, Object> widgetPoolVal = (Map<String, Object>) cfgJsonMap.get(cfgJsonMapKey);
            // 遍历整个widgetPoolVal的Map
            for (String poolValItemKey : widgetPoolVal.keySet()) {
              // 如果集合中的key等于widgets
              if (poolValItemKey.equals("widgets")) {
                List<Map<String, Object>> widgets = (List<Map<String, Object>>) widgetPoolVal
                    .get(poolValItemKey);
                /**
                 * 1.遍历widgets，找到要编辑widget的id 2.修改参数
                 */
                for (Map<String, Object> widget : widgets) {
                  if (widget.get("id").equals(id)) {
                    widget.put("label", label);
                    widget.put("name", name);
                    if (id.startsWith("customDijit/SelectTool")) { // 如果是选择组件，则保存selectShow和selectInfoUrl
                      widget.put("selectShow", module.getSelectShow());
                      widget.put("selectInfoUrl", module.getSelectInfoUrl());
                    }
                    if (id.startsWith(
                        "customDijit/Analyze")) {/* 如果模块为分析模块,则设置分析结果显示方案 */
                      widget.put("analyShow", analyShow);
                    }
                    if (icon != null && !icon.equals("")) {
                      String originIcon = (String) widget.get("icon");
                      /* 删除原来的图片 */
                      if (originIcon.startsWith("UploadImgs")) {
                        FileUtils.deleteFile(Util.ROOT_PATH + File.separatorChar + originIcon);
                        /* 如果使用zk,则删除zk相应节点 */
                        LocalConfigService lcService = new LocalConfigService();
                        Boolean useZK = lcService.getZKState(Util.ROOT_PATH);
                        if (useZK) {
                          Util.deleteDataFromZK(
                              "/UploadImgs" + originIcon.substring(originIcon.lastIndexOf("/")));
                        }
                      }
                      widget.put("icon", icon);
                    }
                    break;
                  }
                  if (widget.containsKey("widgets")) {
                    List<Map<String, Object>> widgetList = (List<Map<String, Object>>) widget
                        .get("widgets");
                    for (Map<String, Object> widgetMap : widgetList) {
                      if (widgetMap.get("id").equals(id)) {
                        widgetMap.put("label", label);
                        widgetMap.put("name", name);
                        if (id.startsWith("customDijit/SelectTool")) { // 如果是选择组件，则保存selectShow和selectInfoUrl
                          widgetMap.put("selectShow", module.getSelectShow());
                          widgetMap.put("selectInfoUrl", module.getSelectInfoUrl());
                        }
                        if (id.startsWith(
                            "customDijit/Analyze")) {/* 如果模块为分析模块,则设置分析结果显示方案 */
                          widgetMap.put("analyShow", analyShow);
                        }
                        if (icon != null && !icon.equals("")) {
                          String originIcon = (String) widget.get("icon");
                          /* 删除原来的图片 */
                          if (originIcon.startsWith("UploadImgs")) {
                            FileUtils.deleteFile(Util.ROOT_PATH + File.separatorChar + originIcon);
                            /* 如果使用zk,则删除zk相应节点 */
                            LocalConfigService lcService = new LocalConfigService();
                            Boolean useZK = lcService.getZKState(Util.ROOT_PATH);
                            if (useZK) {
                              Util.deleteDataFromZK("/UploadImgs"
                                  + originIcon.substring(originIcon.lastIndexOf("/")));
                            }
                          }
                          widgetMap.put("icon", icon);
                        }
                        isFound = true;
                        break;
                      }
                    }
                  }
                  if (isFound) {
                    break;
                  }
                }
              }
            }
          }
        }
      }
    }
    // 把获取到的修改后的数据转化为json
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    // 截取dataJson中的首尾，并转为string
    String data = dataJson.toString().substring(1, (dataJson.toString().length() - 1));
    return Util.writeJson(data);
  }

  /**
   * 新增模块组
   * 
   * @param moduleGroup
   *          模块组
   * @return
   */
  @SuppressWarnings("unchecked")
  public String addModGroup(ModuleGroup moduleGroup) {
    String id = moduleGroup.getId();
    String label = moduleGroup.getLabel();
    String name = moduleGroup.getName();
    String icon = moduleGroup.getIcon();
    String stat = moduleGroup.getStat() != null ? "enable" : "unable";
    String cfgJson = Util.addCorrectFormat(Util.getConfigJson());// 格式化为Json
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);
    if (cfgJsonList.size() > 0) {
      // 遍历整个jsonList的List<Map>
      for (Map<String, Object> cfgJsonMap : cfgJsonList) {
        // 遍历整个Map的key
        for (String cfgJsonMapKey : cfgJsonMap.keySet()) {
          // 如果集合中的key等于widgetPool
          if (cfgJsonMapKey.equals("widgetPool")) {
            // 获取widgetPool的value并存放在集合widgetPoolVal中
            Map<String, Object> widgetPoolVal = (Map<String, Object>) cfgJsonMap.get(cfgJsonMapKey);
            // 遍历整个widgetPoolVal的Map
            for (String poolValItemKey : widgetPoolVal.keySet()) {
              // 如果集合中的key等于widgets
              if (poolValItemKey.equals("widgets")) {
                List<Map<String, Object>> widgets = (List<Map<String, Object>>) widgetPoolVal
                    .get(poolValItemKey);
                // 在widgets下添加模块Map
                Map<String, Object> newWidget = new LinkedHashMap<>();
                newWidget.put("id", id);
                newWidget.put("label", label);
                newWidget.put("name", name);
                if (icon != null) {
                  // 若icon不为空，则存入icon的值
                  newWidget.put("icon", icon);
                } else {
                  // 若icon为空，则将
                  newWidget.put("icon", "admin/UploadImgs/mode_default.png");
                }
                newWidget.put("stat", stat);
                newWidget.put("widgets", new ArrayList<Map<String, Object>>());
                widgets.add(newWidget);
              }
            }
          }
        }
      }
    }
    // 把获取到的修改后的数据转化为json
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    // 截取dataJson中的首尾，并转为string
    String data = dataJson.toString().substring(1, (dataJson.toString().length() - 1));
    return Util.writeJson(data);
  }

  /**
   * 互换导入导出集合中的元素顺序
   * 
   * @param impOrExp
   *          导入或导出
   * @param id
   *          元素id
   * @param upOrDown
   *          向上或向下
   * @return
   * 
   */
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> swapOrder(String impOrExp, String id, String upOrDown) {
    String cfgJson = Util.addCorrectFormat(Util.getConfigJson());// 格式化为Json
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);
    // 类型：导入或导出
    String type = impOrExp.equals("imp") ? "ImportFileTypeTable" : "ExportFileTypeTable";
    if (cfgJsonList.size() > 0) {
      Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
      List<Map<String, Object>> table = (List<Map<String, Object>>) cfgJsonMap.get(type);
      int index = 0;
      for (Map<String, Object> row : table) {
        if (row.get("id").equals(id)) {
          switch (upOrDown) {
          case "up":
            // 若选择与上方的互换位置，则先判断当前的index是否为0,为0不做改变
            if (index != 0) {
              // 否则与上方元素互换，即先获取上方的元素
              Map<String, Object> preRow = table.get(index - 1);
              table.set(index - 1, row);
              table.set(index, preRow);
            }
            break;
          case "down":
            // 若选择与下方的互换位置，则先判断当前元素是否是最后一个,若为最后一个，说明下方有元素，不做改变
            if (index != table.size() - 1) {
              // 否则与下方元素互换，即先获取下方的元素
              Map<String, Object> nextRow = table.get(index + 1);
              table.set(index + 1, row);
              table.set(index, nextRow);
            }
            break;
          default:
            break;
          }
          break;
        }
        index++;
      }
    }
    // 把获取到的修改后的数据转化为json
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    // 截取dataJson中的首尾，并转为string
    String data = dataJson.toString().substring(1, (dataJson.toString().length() - 1));
    Util.writeJson(data);
    return cfgJsonList;
  }

  /**
   * 重置组件
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  public List<Map<String, Object>> resetMod() {
    // 读取config.json
    String cfgJson = Util.addCorrectFormat(Util.getConfigJson());// 格式化为Json
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);
    Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
    Map<String, Object> widgetPool = (Map<String, Object>) cfgJsonMap.get("widgetPool");
    // 读取config_update.json
    String cfgJsonUpdate = Util.addCorrectFormat(Util.getConfigJson("config_update.json"));
    List<Map<String, Object>> cfgJsonUpdateList = new GsonBuilder().create().fromJson(cfgJsonUpdate,
        List.class);
    Map<String, Object> cfgJsonUpdateMap = cfgJsonUpdateList.get(0);
    Map<String, Object> widgetPoolUpdate = (Map<String, Object>) cfgJsonUpdateMap.get("widgetPool");
    widgetPool.put("widgets", widgetPoolUpdate.get("widgets"));
    // 把获取到的修改后的数据转化为json
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    // 截取dataJson中的首尾，并转为string
    String data = dataJson.toString().substring(1, (dataJson.toString().length() - 1));
    Util.writeJson(data);
    return cfgJsonList;
  }

  /**
   * 删除模块组
   * 
   * @param moduleGroup
   *          模块组
   */
  @SuppressWarnings("unchecked")
  public void deleteGroup(ModuleGroup moduleGroup) {
    String json = Util.getConfigJson();// 获取json数据
    json = Util.addCorrectFormat(json);// 格式化为json
    List<Map<String, Object>> jsonList = new GsonBuilder().create().fromJson(json, List.class);
    if (jsonList.size() > 0) {
      // 遍历整个jsonList的List<Map>
      for (Map<String, Object> item : jsonList) {
        // 遍历整个Map的key
        for (String itemKey : item.keySet()) {
          // 如果集合中的key等于widgetPool
          if (itemKey.equals("widgetPool")) {
            // 获取widgetPool的value并存放在集合widgetPoolVal中
            Map<String, Object> widgetPoolVal = (Map<String, Object>) item.get(itemKey);
            // 遍历整个widgetPoolVal的Map
            for (String poolValItemKey : widgetPoolVal.keySet()) {
              // 如果集合中的key等于widgets
              if (poolValItemKey.equals("widgets")) {
                List<Map<String, Object>> widgets = (List<Map<String, Object>>) widgetPoolVal
                    .get(poolValItemKey);
                /**
                 * 1. 先把指定id的模块组删除 遍历widgets List，找到指定id的Map，然后把该Map从List中remove
                 * 2. 再把模块组中的模块添加到widgets节点下
                 * 找到指定id的Map下的widgets键对应的值，然后把值添加到widgets List中
                 */
                for (Map<String, Object> widget : widgets) {
                  if (widget.get("id").equals(moduleGroup.getId())) {
                    Module[] modules = moduleGroup.getWidgets();
                    if (modules != null) {
                      for (Module module : modules) {
                        Map<String, Object> widgetMap = new LinkedHashMap<String, Object>();
                        widgetMap.put("name", module.getName());
                        widgetMap.put("label", module.getLabel());
                        widgetMap.put("uri", module.getUri());
                        widgetMap.put("index", module.getIndex());
                        widgetMap.put("id", module.getId());
                        widgetMap.put("icon", module.getIcon());
                        widgetMap.put("stat", module.getStat());
                        widgetMap.put("panelType", module.getPanelType());
                        widgetMap.put("noTogglen", module.isNoTogglen());
                        widgetMap.put("isPanelMutex", module.isPanelMutex());
                        widgetMap.put("regPosition", module.getRegPosition());
                        widgets.add(widgetMap);
                      }
                    }
                    widgets.remove(widget);
                    break;
                  }
                }
              }
            }
          }
        }
      }
    }
    // 把获取到的修改后的数据转化为json
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(jsonList);
    // 截取dataJson中的首尾，并转为string
    String data = dataJson.toString().substring(1, (dataJson.toString().length() - 1));
    Util.writeJson(data);// 写入到config.json中

  }

  /**
   * 拖动模块
   * 
   * @param modules
   *          拖动后的模块数组
   */
  @SuppressWarnings("unchecked")
  public void dragModule(Module[] modules) {
    String json = Util.getConfigJson();// 获取json数据
    json = Util.addCorrectFormat(json);// 格式化为json
    List<Map<String, Object>> jsonList = new GsonBuilder().create().fromJson(json, List.class);
    if (jsonList.size() > 0) {
      // 遍历整个jsonList的List<Map>
      for (Map<String, Object> item : jsonList) {
        // 遍历整个Map的key
        for (String itemKey : item.keySet()) {
          // 如果集合中的key等于widgetPool
          if (itemKey.equals("widgetPool")) {
            // 获取widgetPool的value并存放在集合widgetPoolVal中
            Map<String, Object> widgetPoolVal = (Map<String, Object>) item.get(itemKey);
            // 遍历整个widgetPoolVal的Map
            for (String poolValItemKey : widgetPoolVal.keySet()) {
              // 如果集合中的key等于widgets
              if (poolValItemKey.equals("widgets")) {
                List<Map<String, Object>> widgets = (List<Map<String, Object>>) widgetPoolVal
                    .get(poolValItemKey);
                /**
                 * 1.遍历module[] 2.获取每个模块的id 3.遍历widgets，如果有个模块的id为当前的module的id
                 * ，那么当前的widget的index改为当前module的index
                 */
                List<String> ids = new ArrayList<String>();
                List<Integer> indices = new ArrayList<Integer>();
                for (Module module : modules) {
                  ids.add(module.getId());
                  indices.add(module.getIndex());
                }
                for (int i = 0; i < ids.size(); i++) {
                  for (Map<String, Object> widget : widgets) {
                    if (widget.get("id").equals(ids.get(i))) {
                      widget.put("index", indices.get(i));
                      break;
                    }
                    if (widget.containsKey("widgets")) {
                      List<Map<String, Object>> innerWidgets = (List<Map<String, Object>>) widget
                          .get("widgets");
                      for (Map<String, Object> innerWidget : innerWidgets) {
                        if (innerWidget.get("id").equals(ids.get(i))) {
                          innerWidget.put("index", indices.get(i));
                          break;
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    // 把获取到的修改后的数据转化为json
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(jsonList);
    // 截取dataJson中的首尾，并转为string
    String data = dataJson.toString().substring(1, (dataJson.toString().length() - 1));
    Util.writeJson(data);// 写入到config.json中
  }
}
