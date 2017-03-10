package com.gisquest.webgis.modules.sys.entity;

/**
 * 权限项类
 * @author Jisj1
 *
 */
public class Privilege {
    
    private String privilegeId;
    private String privilegeName;
    private String funcItemName;
    private String privilegeCode;
    private int funcItemId;
    private String description;
    private String subGuid;
    public String getPrivilegeId() {
        return privilegeId;
    }
    public void setPrivilegeId(String object) {
        this.privilegeId = object;
    }
    public String getPrivilegeName() {
        return privilegeName;
    }
    public void setPrivilegeName(String privilegeName) {
        this.privilegeName = privilegeName;
    }
    public String getFuncItemName() {
        return funcItemName;
    }
    public void setFuncItemName(String funcItemName) {
        this.funcItemName = funcItemName;
    }
    public String getPrivilegeCode() {
        return privilegeCode;
    }
    public void setPrivilegeCode(String privilegeCode) {
        this.privilegeCode = privilegeCode;
    }
    public int getFuncItemId() {
        return funcItemId;
    }
    public void setFuncItemId(int funcItemId) {
        this.funcItemId = funcItemId;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getSubGuid() {
        return subGuid;
    }
    public void setSubGuid(String subGuid) {
        this.subGuid = subGuid;
    }
    
    
}
