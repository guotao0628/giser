package com.gisquest.webgis.modules.sys.entity;

public class SaveField {
  private String selectLayer;
  private String keyField;

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
   * @return the keyField
   */
  public String getKeyField() {
    return keyField;
  }

  /**
   * @param keyField
   *          the keyField to set
   */
  public void setKeyField(String keyField) {
    this.keyField = keyField;
  }

}
