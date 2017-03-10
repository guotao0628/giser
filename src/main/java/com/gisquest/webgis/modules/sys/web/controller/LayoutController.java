package com.gisquest.webgis.modules.sys.web.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gisquest.webgis.modules.sys.entity.ServiceInvocationError;
import com.gisquest.webgis.util.Util;
import com.google.gson.GsonBuilder;

/**
 * 控制布局
 * 
 * @author Jisj1
 *
 */
@Controller
public class LayoutController {
    /** 获取请求路径 */
    @Autowired
    private HttpServletRequest request;
    /** layout.json相对路径 */
    public static final String PATHNAME = "layout.json";

    /**
     * 应用布局方案
     * 
     * @param layoutId
     *            布局方案ID
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/admin/layout/{id}")
    @ResponseBody
    public String useLayout(@PathVariable("id") String id) {
        String layoutJson = getallLayout();
        /* 把Json数据格式转为list */
        List<Map<String, Object>> layoutJsonList = new GsonBuilder().create().fromJson(layoutJson, List.class);
        for (Map<String, Object> layout : layoutJsonList) {
            List<Map<String, Object>> preWidgets = (List<Map<String, Object>>) layout
                    .get("preWidgets");
            /** 如果找到了指定的ID */
            if (id.equals(layout.get("id"))) {
                /* 设置布局到配置文件中 */
                setLayoutToConfig(preWidgets, id);
                return new GsonBuilder().setPrettyPrinting().create().toJson(layout);
            }
        }
        return new GsonBuilder().create().toJson(new ServiceInvocationError("找不到指定的布局方案ID"));
    }

    /**
     * 设置布局到配置文件中
     * 
     * @param preWidgets
     *            布局组件
     * @param id
     *            布局方案ID
     */
    @SuppressWarnings("unchecked")
    private void setLayoutToConfig(List<Map<String, Object>> preWidgets, String id) {
        /* 获取并解析config.json */
        Util.G_REQUEST = request;
        String cfgJson = Util.addCorrectFormat(Util.getConfigJson());
        List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson, List.class);
        Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
        /* 设置当前使用的方案 */
        cfgJsonMap.put("layoutId", id);
        if (cfgJsonMap.containsKey("preloadWidgets")) {
            /* 定位到preloadWidgets */
            Map<String, Object> preloadWidgets = (Map<String, Object>) cfgJsonMap
                    .get("preloadWidgets");
            if (preloadWidgets.containsKey("widgets")) {
                /* 定位到widgets */
                List<Map<String, Object>> widgets = (List<Map<String, Object>>) preloadWidgets
                        .get("widgets");
                /* 遍历widgets */
                for (Map<String, Object> map : widgets) {
                    /* 获取每一个widget的id */
                    String mapId = (String) map.get("id");
                    /* 遍历布局方案中的preWidgets */
                    for (Map<String, Object> preWidget : preWidgets) {
                        /* 获取布局方案中preWidgets的id */
                        String preWidgetId = (String) preWidget.get("id");
                        if (mapId.equals(preWidgetId)) {
                            map.put("position", preWidget.get("position"));
                            break;
                        }
                    }
                }
            }
        }
        /* 把获取到的修改后的数据转化为json */
        String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
        /* 截取dataJson中的首尾，并转为string */
        String data = dataJson.substring(1, (dataJson.toString().length() - 1));
        Util.writeJson(data);
    }

    /**
     * 获取布局方案文件
     * 
     * @return 返回JSON串
     */
    @RequestMapping("/admin/layouts")
    @ResponseBody
    public String getallLayout() {
        String filepath = request.getSession().getServletContext().getRealPath("/" + PATHNAME);
        String layoutJson = new String();
        layoutJson = Util.readFile(filepath);
        return layoutJson;
    }
}
