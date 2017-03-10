package com.gisquest.webgis.modules.sys.entity;

/**
 * 地图参考系对象
 * 
 * @author Administrator
 *
 */
public class SpatialReference {
    private Integer wkid;
    private Integer latestWkid;

    /**
     * @return the wkid
     */
    public Integer getWkid() {
        return wkid;
    }

    /**
     * @param wkid
     *            the wkid to set
     */
    public void setWkid(Integer wkid) {
        this.wkid = wkid;
    }

    /**
     * @return the latestWkid
     */
    public Integer getLatestWkid() {
        return latestWkid;
    }

    /**
     * @param latestWkid
     *            the latestWkid to set
     */
    public void setLatestWkid(Integer latestWkid) {
        this.latestWkid = latestWkid;
    }

}
