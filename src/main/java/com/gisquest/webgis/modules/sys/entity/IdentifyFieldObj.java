package com.gisquest.webgis.modules.sys.entity;

public class IdentifyFieldObj {
    private String id;
    private IdentifyField[] identifyField;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the identifyField
     */
    public IdentifyField[] getIdentifyField() {
        return identifyField;
    }

    /**
     * @param identifyField
     *            the identifyField to set
     */
    public void setIdentifyField(IdentifyField[] identifyField) {
        this.identifyField = identifyField;
    }
}
