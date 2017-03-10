
package com.gisquest.webgis.modules.sys.entity;

import java.util.List;

/**
 * @author Jisj1
 *
 */
public class TSolution {

    /** 方案id */
    private String id;
    /** 可见图层id列表 */
    private List<String> layerIds;
    /** 方案名称 */
    private String name;
    /** 方案缩略图url */
    private String thumbnailUrl;
    /** 资源图层id列表 */
    private List<String> resourceLayerIds;

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
     * @return the thumbnailUrl
     */
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    /**
     * @param thumbnailUrl
     *            the thumbnailUrl to set
     */
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    /**
     * @return the layerIds
     */
    public List<String> getLayerIds() {
        return layerIds;
    }

    /**
     * @param layerIds
     *            the layerIds to set
     */
    public void setLayerIds(List<String> layerIds) {
        this.layerIds = layerIds;
    }

    /**
     * @return the resourceLayerIds
     */
    public List<String> getResourceLayerIds() {
        return resourceLayerIds;
    }

    /**
     * @param resourceLayerIds
     *            the resourceLayerIds to set
     */
    public void setResourceLayerIds(List<String> resourceLayerIds) {
        this.resourceLayerIds = resourceLayerIds;
    }

}
