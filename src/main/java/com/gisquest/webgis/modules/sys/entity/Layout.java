package com.gisquest.webgis.modules.sys.entity;

import java.util.List;
import java.util.Map;

/**
 * 布局类
 * 
 * @author Jisj1
 *
 */
public class Layout {
    /** 布局ID */
    private String id;
    /** 布局名称 */
    private String name;
    /** 布局图片URL */
    private String url;
    /** 布局内组件 */
    private List<Map<String, Object>> preWidgets;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the preWidgets
     */
    public List<Map<String, Object>> getPreWidgets() {
        return preWidgets;
    }

    /**
     * @param preWidgets
     *            the preWidgets to set
     */
    public void setPreWidgets(List<Map<String, Object>> preWidgets) {
        this.preWidgets = preWidgets;
    }

}
