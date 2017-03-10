package com.gisquest.webgis.modules.sys.entity;

import java.util.List;

/**
 * 权限功能项
 * @author Jisj1
 *
 */
public class FuncItem {
    private int funcItemId;
    private String funcItemName;
    private int sequence;
    private int funcGroupId;
    private String funcCode;
    private List<Privilege> privileges;
    public int getFuncItemId() {
        return funcItemId;
    }
    public void setFuncItemId(int funcItemId) {
        this.funcItemId = funcItemId;
    }
    public String getFuncItemName() {
        return funcItemName;
    }
    public void setFuncItemName(String funcItemName) {
        this.funcItemName = funcItemName;
    }
    public int getSequence() {
        return sequence;
    }
    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
    public int getFuncGroupId() {
        return funcGroupId;
    }
    public void setFuncGroupId(int funcGroupId) {
        this.funcGroupId = funcGroupId;
    }
    public String getFuncCode() {
        return funcCode;
    }
    public void setFuncCode(String funcCode) {
        this.funcCode = funcCode;
    }
    public List<Privilege> getPrivileges() {
        return privileges;
    }
    public void setPrivileges(List<Privilege> privileges) {
        this.privileges = privileges;
    }
    
    
}
