package com.gisquest.webgis.modules.sys.entity;

/**
 * 查询字段实体类
 * 
 * @author Jisj1
 *
 */
public class IdentifyField {
    /** 图层 */
    private String lyr;
    /** 字段 */
    private String search;
    /** 显示字段 */
    private String display;

    /**
     * @return the lyr
     */
    public String getLyr() {
        return lyr;
    }

    /**
     * @param lyr
     *            the lyr to set
     */
    public void setLyr(String lyr) {
        this.lyr = lyr;
    }

    /**
     * @return the search
     */
    public String getSearch() {
        return search;
    }

    /**
     * @param search
     *            the search to set
     */
    public void setSearch(String search) {
        this.search = search;
    }

    /**
     * @return the display
     */
    public String getDisplay() {
        return display;
    }

    /**
     * @param display
     *            the display to set
     */
    public void setDisplay(String display) {
        this.display = display;
    }

}
