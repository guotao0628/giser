/**
 * 
 */
package com.gisquest.webgis.modules.sys.entity;

/**
 * 作用：封装analyManager.java中的参数
 * 
 * @author yedy
 *
 */
public class Analy {
    /** 操作标签 */
    private String mTag;
    /** ID */
    private String analyId;
    /** 标签 */
    private String analyLable;
    /** 类型 */
    private String analyType;
    /** 报表地址 */
    private String analyReport;
    /** 年份 */
    private String analyYear;
    /** 参数 */
    private String analyParam;
    /** 标签 */
    private String analyParent;
    /** 引用 */
    private String analyRel;
    /** 加载状态 */
    private String analyChecked;
    /** 显示顺序 */
    private String showIndex;
    /** tocID */
    private String tocIds;
    /** tocShowIndexs */
    private String tocShowIndexs;
    /** 是否自动获取行政区 */
    private Boolean isAutoGetXZQ;
    /** 行政区代码 */
    private String xzqCode;
    /** 是否使用管制区 */
    private Boolean isUseGZX;
    /** 是否使用用途区 */
    private Boolean isUseYTQ;
    /** 是否使用规划分析 */
    private Boolean isUseGHYT;
    /** 是否使用规划基期 */
    private Boolean isUseGHJQ;

    public String getmTag() {
        return mTag;
    }

    public void setmTag(String mTag) {
        this.mTag = mTag;
    }

    public String getAnalyId() {
        return analyId;
    }

    public void setAnalyId(String analyId) {
        this.analyId = analyId;
    }

    public String getAnalyLable() {
        return analyLable;
    }

    public void setAnalyLable(String analyLable) {
        this.analyLable = analyLable;
    }

    public String getAnalyType() {
        return analyType;
    }

    public void setAnalyType(String analyType) {
        this.analyType = analyType;
    }

    public String getAnalyReport() {
        return analyReport;
    }

    public void setAnalyReport(String analyReport) {
        this.analyReport = analyReport;
    }

    public String getAnalyYear() {
        return analyYear;
    }

    public void setAnalyYear(String analyYear) {
        this.analyYear = analyYear;
    }

    public String getAnalyParam() {
        return analyParam;
    }

    public void setAnalyParam(String analyParam) {
        this.analyParam = analyParam;
    }

    public String getAnalyParent() {
        return analyParent;
    }

    public void setAnalyParent(String analyParent) {
        this.analyParent = analyParent;
    }

    public String getAnalyRel() {
        return analyRel;
    }

    public void setAnalyRel(String analyRel) {
        this.analyRel = analyRel;
    }

    public String getAnalyChecked() {
        return analyChecked;
    }

    public void setAnalyChecked(String analyChecked) {
        this.analyChecked = analyChecked;
    }

    public String getShowIndex() {
        return showIndex;
    }

    public void setShowIndex(String showIndex) {
        this.showIndex = showIndex;
    }

    public String getTocIds() {
        return tocIds;
    }

    public void setTocIds(String tocIds) {
        this.tocIds = tocIds;
    }

    public String getTocShowIndexs() {
        return tocShowIndexs;
    }

    public void setTocShowIndexs(String tocShowIndexs) {
        this.tocShowIndexs = tocShowIndexs;
    }

    /**
     * @return the isAutoGetXZQ
     */
    public Boolean getIsAutoGetXZQ() {
        return isAutoGetXZQ;
    }

    /**
     * @param isAutoGetXZQ
     *            the isAutoGetXZQ to set
     */
    public void setIsAutoGetXZQ(Boolean isAutoGetXZQ) {
        this.isAutoGetXZQ = isAutoGetXZQ;
    }

    /**
     * @return the xzqCode
     */
    public String getXzqCode() {
        return xzqCode;
    }

    /**
     * @param xzqCode
     *            the xzqCode to set
     */
    public void setXzqCode(String xzqCode) {
        this.xzqCode = xzqCode;
    }

    /**
     * @return the isUseGZX
     */
    public Boolean getIsUseGZX() {
        return isUseGZX;
    }

    /**
     * @param isUseGZX
     *            the isUseGZX to set
     */
    public void setIsUseGZX(Boolean isUseGZX) {
        this.isUseGZX = isUseGZX;
    }

    /**
     * @return the isUseYTQ
     */
    public Boolean getIsUseYTQ() {
        return isUseYTQ;
    }

    /**
     * @param isUseYTQ
     *            the isUseYTQ to set
     */
    public void setIsUseYTQ(Boolean isUseYTQ) {
        this.isUseYTQ = isUseYTQ;
    }

    /**
     * @return the isUseGHYT
     */
    public Boolean getIsUseGHYT() {
        return isUseGHYT;
    }

    /**
     * @param isUseGHYT
     *            the isUseGHYT to set
     */
    public void setIsUseGHYT(Boolean isUseGHYT) {
        this.isUseGHYT = isUseGHYT;
    }

    /**
     * @return the isUseGHJQ
     */
    public Boolean getIsUseGHJQ() {
        return isUseGHJQ;
    }

    /**
     * @param isUseGHJQ
     *            the isUseGHJQ to set
     */
    public void setIsUseGHJQ(Boolean isUseGHJQ) {
        this.isUseGHJQ = isUseGHJQ;
    }
    
}
