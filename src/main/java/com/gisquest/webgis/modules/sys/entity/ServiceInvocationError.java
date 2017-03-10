package com.gisquest.webgis.modules.sys.entity;

/**
 * 错误类
 * 
 * @author Jisj1
 *
 */
@SuppressWarnings("serial")
public class ServiceInvocationError extends RuntimeException {
    /** 错误信息 */
    private String errorMsg;

    public ServiceInvocationError(String errorMsg) {
        super();
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{\"errorMsg\" : \"" + errorMsg + "\"}";
    }

}
