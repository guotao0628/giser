package com.gisquest.webgis.modules.sys.entity;

public class SaveFieldObj {
  private String id;
  private SaveField[] saveFields;

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
   * @return the saveFields
   */
  public SaveField[] getSaveFields() {
    return saveFields;
  }

  /**
   * @param saveFields
   *          the saveFields to set
   */
  public void setSaveFields(SaveField[] saveFields) {
    this.saveFields = saveFields;
  }

}
