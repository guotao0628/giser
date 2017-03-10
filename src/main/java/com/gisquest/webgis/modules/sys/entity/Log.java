package com.gisquest.webgis.modules.sys.entity;

/**
 * 日志对象
 * 
 * @author Jisj1
 *
 */
public class Log {
    /** 操作用户Id */
    private String userId;
    /** 操作行为 */
    private String action;
    /** 操作信息 */
    private String info;
    /** 操作时间 */
    private String date;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
