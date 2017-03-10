package com.gisquest.webgis.modules.sys.entity;

/**
 * 预加载模块
 * 
 * @author Jisj1
 *
 */
public class PreloadWidget {
  /** 模块ID */
  private String id;
  /** 模块标签 */
  private String label;
  /** 模块启用状态 */
  private Boolean stat;
  /** 掩膜服务 */
  private String maskUrl;
  /** 行政区服务 */
  private String xzqUrl;
  private String blockShow;

  /**
   * @return the blockShow
   */
  public String getBlockShow() {
    return blockShow;
  }

  /**
   * @param blockShow
   *          the blockShow to set
   */
  public void setBlockShow(String blockShow) {
    this.blockShow = blockShow;
  }

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
   * @return the label
   */
  public String getLabel() {
    return label;
  }

  /**
   * @param label
   *          the label to set
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * @return the stat
   */
  public Boolean getStat() {
    return stat;
  }

  /**
   * @param stat
   *          the stat to set
   */
  public void setStat(Boolean stat) {
    this.stat = stat;
  }

  /**
   * @return the maskUrl
   */
  public String getMaskUrl() {
    return maskUrl;
  }

  /**
   * @param maskUrl
   *          the maskUrl to set
   */
  public void setMaskUrl(String maskUrl) {
    this.maskUrl = maskUrl;
  }

  /**
   * @return the xzqUrl
   */
  public String getXzqUrl() {
    return xzqUrl;
  }

  /**
   * @param xzqUrl
   *          the xzqUrl to set
   */
  public void setXzqUrl(String xzqUrl) {
    this.xzqUrl = xzqUrl;
  }

}
