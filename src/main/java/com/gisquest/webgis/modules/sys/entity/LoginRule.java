package com.gisquest.webgis.modules.sys.entity;

/**
 * 登录规则实体类
 * 
 * @author Jisj1
 *
 */
public class LoginRule {
	private String id; // 登录规则id
	private String name; // 登录规则名称
	private String ip; // 登录规则ip

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

}
