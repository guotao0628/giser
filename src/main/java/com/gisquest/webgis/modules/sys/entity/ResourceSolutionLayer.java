package com.gisquest.webgis.modules.sys.entity;

/**
 * 资源方案图层实体类
 * 
 * @author Jisj1
 *
 */
public class ResourceSolutionLayer {
    /** 图层id */
    private String id;
    /** 图层操作权限 */
    private String privi;
    /** 图层初始加载状态 */
    private Boolean inital;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrivi() {
        return privi;
    }

    public void setPrivi(String privi) {
        this.privi = privi;
    }

    public Boolean getInital() {
        return inital;
    }

    public void setInital(Boolean inital) {
        this.inital = inital;
    }

}
