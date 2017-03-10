package com.gisquest.webgis.modules.sys.entity;

import java.util.List;
import java.util.Map;

/**
 * 制图模板
 * 
 * @author Jisj1
 *
 */
public class PrintTemplate {
  /** 模板ID */
  private String id;
  /** 模板名称 */
  private String templateName;
  /** 模板启用状态 */
  private String stat;
  /** 地图单位 */
  private String mapUnit;
  /** 模板元素集合 */
  private List<Map<String, String>> elements;
  private String pageOri;
  private String pageSize;

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return the templateName
   */
  public String getTemplateName() {
    return templateName;
  }

  /**
   * @param templateName
   *          the templateName to set
   */
  public void setTemplateName(String templateName) {
    this.templateName = templateName;
  }

  /**
   * @return the stat
   */
  public String getStat() {
    return stat;
  }

  /**
   * @param stat
   *          the stat to set
   */
  public void setStat(String stat) {
    this.stat = stat;
  }

  /**
   * @return the mapUnit
   */
  public String getMapUnit() {
    return mapUnit;
  }

  /**
   * @param mapUnit
   *          the mapUnit to set
   */
  public void setMapUnit(String mapUnit) {
    this.mapUnit = mapUnit;
  }

  /**
   * @return the elements
   */
  public List<Map<String, String>> getElements() {
    return elements;
  }

  /**
   * @param elements
   *          the elements to set
   */
  public void setElements(List<Map<String, String>> elements) {
    this.elements = elements;
  }

  /**
   * @return the pageOri
   */
  public String getPageOri() {
    return pageOri;
  }

  /**
   * @param pageOri
   *          the pageOri to set
   */
  public void setPageOri(String pageOri) {
    this.pageOri = pageOri;
  }

  /**
   * @return the pageSize
   */
  public String getPageSize() {
    return pageSize;
  }

  /**
   * @param pageSize
   *          the pageSize to set
   */
  public void setPageSize(String pageSize) {
    this.pageSize = pageSize;
  }
}
