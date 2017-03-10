package com.gisquest.webgis.modules.sys.entity;

/**
 * 地图书签对象
 * 
 * @author Jisj1
 *
 */
public class Bookmark {
    private String id;
    private String name;
    private Extent extent;

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
     * @return the extent
     */
    public Extent getExtent() {
        return extent;
    }

    /**
     * @param extent
     *            the extent to set
     */
    public void setExtent(Extent extent) {
        this.extent = extent;
    }

}
