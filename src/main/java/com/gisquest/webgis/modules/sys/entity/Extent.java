package com.gisquest.webgis.modules.sys.entity;

/**
 * 地图范围对象
 * 
 * @author Jisj1
 *
 */
public class Extent {
    private Double xmax;
    private Double xmin;
    private Double ymax;
    private Double ymin;
    private SpatialReference spatialReference;

    /**
     * @return the xmax
     */
    public Double getXmax() {
        return xmax;
    }

    /**
     * @param xmax
     *            the xmax to set
     */
    public void setXmax(Double xmax) {
        this.xmax = xmax;
    }

    /**
     * @return the xmin
     */
    public Double getXmin() {
        return xmin;
    }

    /**
     * @param xmin
     *            the xmin to set
     */
    public void setXmin(Double xmin) {
        this.xmin = xmin;
    }

    /**
     * @return the ymax
     */
    public Double getYmax() {
        return ymax;
    }

    /**
     * @param ymax
     *            the ymax to set
     */
    public void setYmax(Double ymax) {
        this.ymax = ymax;
    }

    /**
     * @return the ymin
     */
    public Double getYmin() {
        return ymin;
    }

    /**
     * @param ymin
     *            the ymin to set
     */
    public void setYmin(Double ymin) {
        this.ymin = ymin;
    }

    /**
     * @return the spatialReference
     */
    public SpatialReference getSpatialReference() {
        return spatialReference;
    }

    /**
     * @param spatialReference
     *            the spatialReference to set
     */
    public void setSpatialReference(SpatialReference spatialReference) {
        this.spatialReference = spatialReference;
    }

}
