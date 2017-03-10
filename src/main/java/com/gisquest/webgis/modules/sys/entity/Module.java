package com.gisquest.webgis.modules.sys.entity;

/**
 * 模块实体类
 * 
 * @author Jisj1
 *
 */
public class Module {
  /** 模块名称 */
  private String name;
  /** 模块标签 */
  private String label;
  /** 模块uri */
  private String uri;
  /** 模块序号 */
  private int index;
  /** 模块id */
  private String id;
  /** 模块图标url */
  private String icon;
  /** 模块启用状态 */
  private String stat;
  /** 模块panelType */
  private String panelType;
  /** isPanelMutex */
  private boolean isPanelMutex = false;
  /** 是否为切换按钮 */
  private boolean noTogglen = true;
  /** 位置 */
  private String regPosition;
  /** 父节点ID */
  private String parentId;
  /** 制图服务地址 */
  private String printUrl;
  /** 分析结果显示方案 */
  private String analyShow;
  private String selectShow;
  private String selectInfoUrl;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getStat() {
    return stat;
  }

  public void setStat(String stat) {
    this.stat = stat;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public String getPanelType() {
    return panelType;
  }

  public void setPanelType(String panelType) {
    this.panelType = panelType;
  }

  public boolean isPanelMutex() {
    return isPanelMutex;
  }

  public void setPanelMutex(boolean isPanelMutex) {
    this.isPanelMutex = isPanelMutex;
  }

  public boolean isNoTogglen() {
    return noTogglen;
  }

  public void setNoTogglen(boolean noTogglen) {
    this.noTogglen = noTogglen;
  }

  public String getRegPosition() {
    return regPosition;
  }

  public void setRegPosition(String regPosition) {
    this.regPosition = regPosition;
  }

  /**
   * @return the printUrl
   */
  public String getPrintUrl() {
    return printUrl;
  }

  /**
   * @param printUrl
   *          the printUrl to set
   */
  public void setPrintUrl(String printUrl) {
    this.printUrl = printUrl;
  }

  /**
   * @return the analyShow
   */
  public String getAnalyShow() {
    return analyShow;
  }

  /**
   * @param analyShow
   *          the analyShow to set
   */
  public void setAnalyShow(String analyShow) {
    this.analyShow = analyShow;
  }

  /**
   * @return the selectShow
   */
  public String getSelectShow() {
    return selectShow;
  }

  /**
   * @param selectShow
   *          the selectShow to set
   */
  public void setSelectShow(String selectShow) {
    this.selectShow = selectShow;
  }

  /**
   * @return the selectInfoUrl
   */
  public String getSelectInfoUrl() {
    return selectInfoUrl;
  }

  /**
   * @param selectInfoUrl
   *          the selectInfoUrl to set
   */
  public void setSelectInfoUrl(String selectInfoUrl) {
    this.selectInfoUrl = selectInfoUrl;
  }
}
