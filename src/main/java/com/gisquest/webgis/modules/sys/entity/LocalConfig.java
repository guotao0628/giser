package com.gisquest.webgis.modules.sys.entity;

/**
 * 本地配置文件
 * 
 * @author Jisj1
 *
 */
public class LocalConfig {
    /** 是否使用zookeeper */
    private boolean useZK = false;

    /**
     * @return the useZK
     */
    public boolean isUseZK() {
        return useZK;
    }

    /**
     * @param useZK
     *            the useZK to set
     */
    public void setUseZK(boolean useZK) {
        this.useZK = useZK;
    }

}
