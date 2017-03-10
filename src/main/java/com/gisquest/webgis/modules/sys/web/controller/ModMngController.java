package com.gisquest.webgis.modules.sys.web.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gisquest.webgis.modules.sys.entity.Module;
import com.gisquest.webgis.modules.sys.entity.ModuleGroup;
import com.gisquest.webgis.modules.sys.entity.PreloadWidget;
import com.gisquest.webgis.modules.sys.services.ModMngService;
import com.gisquest.webgis.util.Util;
import com.google.gson.GsonBuilder;

/**
 * 模块管理控制器
 * 
 * @author Jisj1
 *
 */
@Controller
@SuppressWarnings("unchecked")
public class ModMngController {
    /** 请求体 */
    @Autowired
    private HttpServletRequest request;
    /** 模块管理服务 */
    private ModMngService mngService;

    /**
     * 构造函数，初始化
     */
    public ModMngController() {
        mngService = new ModMngService();
    }

    /**
     * 根据功能项ID获取单个功能项
     * 
     * @param id
     *            功能项ID
     * @return
     */
    @RequestMapping("/widgets/{id}")
    @ResponseBody
    public String getWidgetById(@PathVariable String id) {
        Util.G_REQUEST = request;
        String cfgJson = Util.addCorrectFormat(Util.getConfigJson()); // 获取并格式化config.json
        List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
                List.class);// 把config.json转为list
        if (cfgJsonList.size() > 0) {
            Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
            if (cfgJsonMap.containsKey("widgetPool")) {
                // 预置组件
                Map<String, Object> preWidgetPool = (Map<String, Object>) cfgJsonMap
                        .get("preloadWidgets");
                List<Map<String, Object>> preWidgets = (List<Map<String, Object>>) preWidgetPool
                        .get("widgets");
                // 功能组件
                Map<String, Object> widgetPool = (Map<String, Object>) cfgJsonMap.get("widgetPool");
                List<Map<String, Object>> widgets = (List<Map<String, Object>>) widgetPool
                        .get("widgets");
                // 合并
                List<Map<String, Object>> allWidgets = new ArrayList<>();
                allWidgets.addAll(preWidgets);
                allWidgets.addAll(widgets);
                for (Map<String, Object> widget : allWidgets) {
                    if (("customDijit/" + id).equals(widget.get("id"))
                            || id.equals(widget.get("id"))) {
                        // 去除组件的子类
                        widget.remove("widgets");
                        return new GsonBuilder().setPrettyPrinting().create().toJson(widget);
                    } else if (widget.containsKey("widgets")) {
                        List<Map<String, Object>> innerWidgetList = (List<Map<String, Object>>) widget
                                .get("widgets");
                        for (Map<String, Object> innerWidget : innerWidgetList) {
                            if (("customDijit/" + id).equals(innerWidget.get("id"))
                                    || id.equals(innerWidget.get("id"))) {
                                return new GsonBuilder().setPrettyPrinting().create()
                                        .toJson(innerWidget);
                            }
                        }
                    }
                }
                return "Error: 未找到ID为" + id + "的功能项！";
            }
        }
        return "Error: 在解析config.json时出现错误！";
    }

    /**
     * 根据功能项名称，功能项启用状态获取一个或多个功能项
     * 
     * @param labels
     *            功能项名称
     * @param state
     *            功能项启用状态
     * @return
     */

    @RequestMapping("/widgets/")
    @ResponseBody
    public String getWidgets(String[] ids, Boolean state) {
        Util.G_REQUEST = request;
        String cfgJson = Util.addCorrectFormat(Util.getConfigJson()); // 获取并格式化config.json
        List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
                List.class);// 把config.json转为list
        if (cfgJsonList.size() > 0) {
            Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
            // 获取预置组件和功能组件
            if (cfgJsonMap.containsKey("preloadWidgets") && cfgJsonMap.containsKey("widgetPool")) {
                // 预置组件
                Map<String, Object> preWidgetPool = (Map<String, Object>) cfgJsonMap
                        .get("preloadWidgets");
                List<Map<String, Object>> preWidgets = (List<Map<String, Object>>) preWidgetPool
                        .get("widgets");
                // 功能组件
                Map<String, Object> widgetPool = (Map<String, Object>) cfgJsonMap.get("widgetPool");
                List<Map<String, Object>> widgets = (List<Map<String, Object>>) widgetPool
                        .get("widgets");
                // 合并
                List<Map<String, Object>> allWidgets = new ArrayList<>();
                allWidgets.addAll(preWidgets);
                allWidgets.addAll(widgets);
                // 将子类所加入allWidgets
                for (Map<String, Object> map : widgets) {
                    if (map.containsKey("widgets")) {
                        allWidgets.addAll((List<Map<String, Object>>) map.get("widgets"));
                        // 去除组件的子类
                        map.remove("widgets");
                    }
                }
                List<Map<String, Object>> tempList = allWidgets;
                // HashSet元素唯一
                Set<Map<String, Object>> resultSet = new HashSet<>();
                if (ids != null) {
                    // 遍历ids
                    for (String id : ids) {
                        for (Map<String, Object> item : tempList) {
                            if (("customDijit/" + id).equals(item.get("id"))
                                    || id.equals(item.get("id"))) {
                                resultSet.add(item);
                            } else if (item.containsKey("widgets")) {
                                List<Map<String, Object>> innerWidgets = (List<Map<String, Object>>) item
                                        .get("widgets");
                                for (Map<String, Object> innerWidget : innerWidgets) {
                                    if (("customDijit/" + id).equals(innerWidget.get("id"))
                                            || id.equals(innerWidget.get("id"))) {
                                        resultSet.add(innerWidget);
                                    }
                                }
                            }
                        }
                    }
                    tempList.clear();
                    tempList.addAll(resultSet);
                    resultSet.clear();
                }
                if (state != null) {
                    for (Map<String, Object> item : tempList) {
                        Boolean itemStat = "enable".equals(item.get("stat"));
                        if (state == itemStat) {
                            resultSet.add(item);
                        }
                    }
                    tempList.clear();
                    tempList.addAll(resultSet);
                    resultSet.clear();
                }
                return new GsonBuilder().setPrettyPrinting().create().toJson(tempList);
            }
        }
        return "Error: 在解析config.json时出现错误！";

    }

    /**
     * 重置组件
     */
    @RequestMapping("/admin/resetMod")
    @ResponseBody
    public void resetMod() {
        Util.G_REQUEST = request;
        mngService.resetMod();
    }

    /**
     * 编辑模块
     * 
     * @param module
     *            模块对象
     * @param file
     *            上传文件
     * @return
     */
    @RequestMapping("/admin/editModule")
    @ResponseBody
    public String editModule(Module module) {
        return mngService.editModule(module);
    }

    /**
     * 新增模块组
     * 
     * @param moduleGroup
     *            模块组
     * @param file
     *            上传文件
     * @return
     */
    @RequestMapping(value = "/admin/addModGroup", method = RequestMethod.POST)
    @ResponseBody
    public String addModGroup(ModuleGroup moduleGroup) {
        return mngService.addModGroup(moduleGroup);
    }

    /**
     * 互换导入导出表格中列的顺序
     * 
     * @param impOrExp
     *            导入或导出
     * @param id
     *            列id
     * @param upOrDown
     *            向上或向下
     */
    @RequestMapping("/admin/swapOrder")
    @ResponseBody
    public void swapOrder(String impOrExp, String id, String upOrDown) {
        Util.G_REQUEST = request;
        mngService.swapOrder(impOrExp, id, upOrDown);
    }

    /**
     * 保存预加载模块配置
     * 
     * @param preWidget
     *            预加载模块对象
     */
    @RequestMapping("/admin/preWidget")
    @ResponseBody
    public void savePreWidget(PreloadWidget preWidget) {
        /* 获取要编辑模块的ID */
        Util.G_REQUEST = request;
        String cfgJson = Util.addCorrectFormat(Util.getConfigJson());
        List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
                List.class);
        if (cfgJsonList.size() > 0) {
            /* 定位到config.json根节点 */
            Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
            /* 如果模块是行政区导航，则设置掩膜服务和行政区服务 */
            if (preWidget.getId().startsWith("customDijit/Navigator")) {
                /* 定位到toc */
                List<Map<String, Object>> tocList = (List<Map<String, Object>>) cfgJsonMap
                        .get("toc");
                for (Map<String, Object> toc : tocList) {
                    if (toc.get("id").equals("MaskLayer")) {
                        toc.put("url", preWidget.getMaskUrl());
                        break;
                    }
                }
                /* 设置xzqService */
                cfgJsonMap.put("xzqService", preWidget.getXzqUrl());
            }
            /* 如果模块是地块列表模块，则设置blockShow字段*/
            if(preWidget.getId().startsWith("customDijit/BlockList")){
              //定位到preloadWidgets
              Map<String, Object> preloadWidgets = (Map<String, Object>) cfgJsonMap
                  .get("preloadWidgets");
              List<Map<String, Object>> widgets = (List<Map<String, Object>>) preloadWidgets.get("widgets");
              for (Map<String, Object> widget : widgets) {
                if(((String)widget.get("id")).startsWith("customDijit/BlockList")){
                  widget.put("blockShow",preWidget.getBlockShow());
                  break;
                }
              }
            }
        }
        /* 保存Config.json */
        String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
        String data = dataJson.substring(1, (dataJson.toString().length() - 1));
        Util.writeJson(data);
    }

    /**
     * 设置模块项的启用状态
     * 
     * @param id
     *            模块项id
     * @param stat
     *            启用状态
     */
    @RequestMapping(value = "/admin/module/modState", method = RequestMethod.POST)
    @ResponseBody
    public void setModState(String id, String stat) {
        /* 获取并解析config.json */
        Util.G_REQUEST = request;
        String cfgJson = Util.getConfigJson();
        cfgJson = Util.addCorrectFormat(cfgJson);
        List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
                List.class);
        if (cfgJsonList.size() > 0) {
            /* 获取cfgJsonMap */
            Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
            if (cfgJsonMap.containsKey("widgetPool")) {
                /* 获取widgetPool */
                Map<String, Object> widgetPoolMap = (Map<String, Object>) cfgJsonMap
                        .get("widgetPool");
                if (widgetPoolMap.containsKey("widgets")) {
                    /* 获取widgetPool中的widgets列表 */
                    List<Map<String, Object>> widgetsList = (List<Map<String, Object>>) widgetPoolMap
                            .get("widgets");
                    /* 设置根节点的初始状态，默认为false */
                    String isRootChecked = "unable";
                    /* 遍历widgets列表 */
                    for (Map<String, Object> widget : widgetsList) {
                        /* 获取当前widget的id，如果是传来的id */
                        if (widget.get("id") != null && widget.get("id").equals(id)) {
                            /* 改变当前widget的状态 */
                            widget.put("stat", stat);
                            /* 如果有子节点，改变其子节点的状态 */
                            if (widget.containsKey("widgets")) {
                                List<Map<String, Object>> innerWidgets = (List<Map<String, Object>>) widget
                                        .get("widgets");
                                for (Map<String, Object> map : innerWidgets) {
                                    map.put("stat", stat);
                                }
                            }
                        } else if (widget.containsKey("widgets")) {
                            /* 如果当前widget有子节点widgets,则定位到该子节点 */
                            List<Map<String, Object>> innerWidgetsList = (List<Map<String, Object>>) widget
                                    .get("widgets");
                            /* 设置根节点的初始状态，默认为false */
                            String isFolderChecked = "unable";
                            /* 遍历子节点widgets列表 */
                            for (Map<String, Object> innerWidget : innerWidgetsList) {
                                /* 获取当前innerWidget的id，如果是传来的id */
                                if (innerWidget.get("id") != null
                                        && innerWidget.get("id").equals(id)) {
                                    /* 改变当前widget的状态 */
                                    innerWidget.put("stat", stat);
                                    /* 如果是启用，则启用其父节点 */
                                    if (stat.equals("enable")) {
                                        widget.put("stat", stat);
                                    }
                                }
                                /* 如果有一个子节点为enable，则为enable */
                                if (innerWidget.get("stat") != null
                                        && innerWidget.get("stat").equals("enable")) {
                                    isFolderChecked = "enable";
                                }
                            }
                            /* 改变目录节点的状态 */
                            widget.put("stat", isFolderChecked);
                        }
                        /* 如果有一个子节点为enable，则为enable */
                        if (widget.get("stat") != null && widget.get("stat").equals("enable")) {
                            isRootChecked = "enable";
                        }
                    }
                    /* 改变根节点的状态 */
                    if (cfgJsonMap.containsKey("preloadWidgets")) {
                        Map<String, Object> preloadWidgets = (Map<String, Object>) cfgJsonMap
                                .get("preloadWidgets");
                        List<Map<String, Object>> widgets = (List<Map<String, Object>>) preloadWidgets
                                .get("widgets");
                        for (Map<String, Object> map : widgets) {
                            if (map.get("id").equals(
                                    "customDijit/HeaderController_Widget_HC3CF32A7218794753B116F8E136C6C411")) {
                                map.put("stat", isRootChecked);
                            }
                        }
                    }
                }
            }
        }
        /* 保存config.json */
        String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
        String data = dataJson.substring(1, (dataJson.toString().length() - 1));
        Util.writeJson(data);
    }

    /**
     * 设置预加载模块项的启用状态
     * 
     * @param id
     *            模块项id
     * @param stat
     *            启用状态
     */
    @RequestMapping(value = "/admin/module/preWidState", method = RequestMethod.POST)
    @ResponseBody
    public void setPreWidState(String id, String stat) {
        /* 获取并解析config.json */
        Util.G_REQUEST = request;
        String cfgJson = Util.getConfigJson();
        cfgJson = Util.addCorrectFormat(cfgJson);
        List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
                List.class);
        if (cfgJsonList.size() > 0) {
            /* 获取cfgJsonMap */
            Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
            Map<String, Object> preloadWidgets = (Map<String, Object>) cfgJsonMap
                    .get("preloadWidgets");
            List<Map<String, Object>> widgets = (List<Map<String, Object>>) preloadWidgets
                    .get("widgets");
            for (Map<String, Object> map : widgets) {
                if (map.get("id").equals(id)) {
                    map.put("stat", stat);
                    if (id.equals(
                            "customDijit/HeaderController_Widget_HC3CF32A7218794753B116F8E136C6C411")) {
                        Map<String, Object> widgetPool = (Map<String, Object>) cfgJsonMap
                                .get("widgetPool");
                        List<Map<String, Object>> poolWidgets = (List<Map<String, Object>>) widgetPool
                                .get("widgets");
                        for (Map<String, Object> poolWidget : poolWidgets) {
                            poolWidget.put("stat", stat);
                            if (poolWidget.containsKey("widgets")) {
                                List<Map<Object, String>> innerWidgets = (List<Map<Object, String>>) poolWidget
                                        .get("widgets");
                                for (Map<Object, String> innerWidget : innerWidgets) {
                                    innerWidget.put("stat", stat);
                                }
                            }
                        }
                    }
                }
            }
        }
        /* 保存config.json */
        String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
        String data = dataJson.substring(1, (dataJson.toString().length() - 1));
        Util.writeJson(data);
    }

    /**
     * 设置根节点的启用状态
     * 
     * @param id
     *            模块项id
     * @param stat
     *            启用状态
     */
    @RequestMapping(value = "/admin/module/rootState", method = RequestMethod.POST)
    @ResponseBody
    public void setRootState(String stat) {
        /* 获取并解析config.json */
        Util.G_REQUEST = request;
        String cfgJson = Util.getConfigJson();
        cfgJson = Util.addCorrectFormat(cfgJson);
        List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
                List.class);
        if (cfgJsonList.size() > 0) {
            /* 获取cfgJsonMap */
            Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
            Map<String, Object> preloadWidgets = (Map<String, Object>) cfgJsonMap
                    .get("preloadWidgets");
            List<Map<String, Object>> widgets = (List<Map<String, Object>>) preloadWidgets
                    .get("widgets");
            for (Map<String, Object> map : widgets) {
                /* 如果是图层目录节点，不对其进行修改 */
                if (!map.get("id")
                        .equals("customDijit/Toc_Widget_TOC3CF32A7218794753B116F8E136C6C411")) {
                    map.put("stat", stat);
                    if (map.get("id").equals(
                            "customDijit/HeaderController_Widget_HC3CF32A7218794753B116F8E136C6C411")) {
                        Map<String, Object> widgetPool = (Map<String, Object>) cfgJsonMap
                                .get("widgetPool");
                        List<Map<String, Object>> poolWidgets = (List<Map<String, Object>>) widgetPool
                                .get("widgets");
                        for (Map<String, Object> poolWidget : poolWidgets) {
                            poolWidget.put("stat", stat);
                            if (poolWidget.containsKey("widgets")) {
                                List<Map<Object, String>> innerWidgets = (List<Map<Object, String>>) poolWidget
                                        .get("widgets");
                                for (Map<Object, String> innerWidget : innerWidgets) {
                                    innerWidget.put("stat", stat);
                                }
                            }
                        }
                    }
                }
            }
        }
        /* 保存config.json */
        String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
        String data = dataJson.substring(1, (dataJson.toString().length() - 1));
        Util.writeJson(data);
    }

    /**
     * 用于拖拽模块更换模块所属目录
     * 
     * @param id
     *            目标模块的Id
     * @param parentId
     *            要拖往模块的Id
     */
    @RequestMapping(value = "/admin/CmHandler/doesDrop/", method = RequestMethod.POST)
    @ResponseBody
    public void changeModParent(@RequestBody Module module) {
        String id = module.getId();
        String parentId = module.getParentId();
        /* 获取并解析config.json */
        Util.G_REQUEST = request;
        String cfgJson = Util.getConfigJson();
        Map<String, Object> targetWidget = new LinkedHashMap<>();
        cfgJson = Util.addCorrectFormat(cfgJson);
        List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
                List.class);
        if (cfgJsonList.size() > 0) {
            /* 获取cfgJsonMap */
            Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
            Map<String, Object> widgetPool = (Map<String, Object>) cfgJsonMap.get("widgetPool");
            List<Map<String, Object>> widgets = (List<Map<String, Object>>) widgetPool
                    .get("widgets");
            boolean isWidgetFound = false;
            for (Map<String, Object> widget : widgets) {
                /* 获取当前widget的id，如果是传来的id */
                if (widget.get("id") != null && widget.get("id").equals(id)) {
                    targetWidget = widget;
                    widgets.remove(widget);
                    isWidgetFound = true;
                } else if (widget.containsKey("widgets")) {
                    /* 如果当前widget有子节点widgets,则定位到该子节点 */
                    List<Map<String, Object>> innerWidgetsList = (List<Map<String, Object>>) widget
                            .get("widgets");
                    /* 遍历子节点widgets列表 */
                    for (Map<String, Object> innerWidget : innerWidgetsList) {
                        /* 获取当前innerWidget的id，如果是传来的id */
                        if (innerWidget.get("id") != null && innerWidget.get("id").equals(id)) {
                            targetWidget = innerWidget;
                            innerWidgetsList.remove(innerWidget);
                            isWidgetFound = true;
                            break;
                        }
                    }
                }
                if (isWidgetFound) {
                    break;
                }
            }
            /* 如果要移往的Id是头部控件 */
            if (parentId.equals(
                    "customDijit/HeaderController_Widget_HC3CF32A7218794753B116F8E136C6C411")) {
                widgets.add(targetWidget);
            } else {
                for (Map<String, Object> widget : widgets) {
                    /* 获取当前widget的id，如果是传来的id */
                    if (widget.get("id") != null && widget.get("id").equals(parentId)) {
                        List<Map<String, Object>> innerWidgets = (List<Map<String, Object>>) widget
                                .get("widgets");
                        innerWidgets.add(targetWidget);
                        break;
                    }
                }
            }
        }
        /* 保存config.json */
        String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
        String data = dataJson.substring(1, (dataJson.toString().length() - 1));
        Util.writeJson(data);
    }

    /**
     * 用于拖拽模块改变模块顺序
     * 
     * @param modules
     * @return
     */
    @RequestMapping(value = "/admin/CmHandler/doDrop/", method = RequestMethod.POST)
    @ResponseBody
    public void dragModule(@RequestBody Module[] modules) {
        Util.G_REQUEST = request;
        mngService.dragModule(modules);
    }

    /**
     * 用于删除模块组
     * 
     * @return
     */
    @RequestMapping(value = "/admin/CmHandler/deleteGroup/", method = RequestMethod.POST)
    @ResponseBody
    public void deleteGroup(@RequestBody ModuleGroup moduleGroup) {
        Util.G_REQUEST = request;
        mngService.deleteGroup(moduleGroup);
    }

}
