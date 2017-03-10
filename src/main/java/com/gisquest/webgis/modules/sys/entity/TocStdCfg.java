package com.gisquest.webgis.modules.sys.entity;

/**
 * 图层标准配置对象
 * 
 * @author Jisj1
 *
 */
public class TocStdCfg {
  /** 图层ID */
  private String id;
  /** 图层名称 */
  private String label;
  /** 图层标识 */
  private String tag;
  /** 图层类型 */
  private String type;
  /** 图形服务url */
  private String url;
  /** 可见图层 id数组 */
  private String[] visIds;
  /** 查询字段 */
  private IdentifyField[] identifyField;
  /** 加载顺序 */
  private Integer mapIndex;
  /** 初始加载 */
  private boolean initLoad;
  /** 可切换 */
  private boolean enableSwitch;
  /** 数据表 */
  private String layerTable;
  /** 图层分类 */
  private String group;
  /** 快速切换 */
  private boolean shortcut;
  /** 显示顺序 */
  private Integer showIndex;
  /** 缩略图路径 */
  private String disThumb;
  /** 父节点id */
  private String parentId;
  /** 几何类型 */
  private String geotype;
  /** 拓扑检查 */
  private Integer topology;
  /** 移动可用状态 */
  private boolean mobileAvailable;
  /** 是否弹信息框 */
  private boolean showInfoWindow;
  /** 可选图层 */
  private String selectLayer;
  private boolean filter;
  private String saveField;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * 若vidIds为null，则新建一个空的String数组
   * 
   * @return
   */
  public String[] getVisIds() {
    if (visIds == null) {
      visIds = new String[] {};
    } else {
      for (int i = 0; i < visIds.length; i++) {
        if (visIds[i].contains("(")) {
          visIds[i] = visIds[i].substring(0, visIds[i].indexOf("("));
        }
      }
    }
    return visIds;
  }

  /**
   * 若vidIds为null，则新建一个空的String数组
   * 
   * @return
   */
  public void setVisIds(String[] visIds) {
    if (visIds == null) {
      visIds = new String[] {};
      this.visIds = visIds;
    }
    this.visIds = visIds;
  }

  public Integer getMapIndex() {
    return mapIndex;
  }

  public void setMapIndex(Integer mapIndex) {
    this.mapIndex = mapIndex;
  }

  public boolean isInitLoad() {
    return initLoad;
  }

  public void setInitLoad(boolean initLoad) {
    this.initLoad = initLoad;
  }

  public boolean isEnableSwitch() {
    return enableSwitch;
  }

  public void setEnableSwitch(boolean enableSwitch) {
    this.enableSwitch = enableSwitch;
  }

  public String getLayerTable() {
    return layerTable;
  }

  public void setLayerTable(String layerTable) {
    this.layerTable = layerTable;
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public boolean isShortcut() {
    return shortcut;
  }

  public void setShortcut(boolean shortcut) {
    this.shortcut = shortcut;
  }

  public Integer getShowIndex() {
    return showIndex;
  }

  public void setShowIndex(Integer showIndex) {
    this.showIndex = showIndex;
  }

  public String getDisThumb() {
    return disThumb;
  }

  public void setDisThumb(String disThumb) {
    this.disThumb = disThumb;
  }

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public String getGeotype() {
    return geotype;
  }

  public void setGeotype(String geotype) {
    this.geotype = geotype;
  }

  public Integer getTopology() {
    return topology;
  }

  public void setTopology(Integer topology) {
    this.topology = topology;
  }

  /**
   * @return the identifyField
   */
  public IdentifyField[] getIdentifyField() {
    return identifyField;
  }

  /**
   * @param identifyField
   *          the identifyField to set
   */
  public void setIdentifyField(IdentifyField[] identifyField) {
    this.identifyField = identifyField;
  }

  /**
   * @return the mobileAvailable
   */
  public boolean isMobileAvailable() {
    return mobileAvailable;
  }

  /**
   * @param mobileAvailable
   *          the mobileAvailable to set
   */
  public void setMobileAvailable(boolean mobileAvailable) {
    this.mobileAvailable = mobileAvailable;
  }

  /**
   * @return the showInfoWindow
   */
  public boolean isShowInfoWindow() {
    return showInfoWindow;
  }

  /**
   * @param showInfoWindow
   *          the showInfoWindow to set
   */
  public void setShowInfoWindow(boolean showInfoWindow) {
    this.showInfoWindow = showInfoWindow;
  }

  /**
   * @return the selectLayer
   */
  public String getSelectLayer() {
    return selectLayer;
  }

  /**
   * @param selectLayer
   *          the selectLayer to set
   */
  public void setSelectLayer(String selectLayer) {
    this.selectLayer = selectLayer;
  }

  /**
   * @return the filter
   */
  public boolean isFilter() {
    return filter;
  }

  /**
   * @param filter
   *          the filter to set
   */
  public void setFilter(boolean filter) {
    this.filter = filter;
  }

  /**
   * @return the saveField
   */
  public String getSaveField() {
    return saveField;
  }

  /**
   * @param saveField
   *          the saveField to set
   */
  public void setSaveField(String saveField) {
    this.saveField = saveField;
  }

}
