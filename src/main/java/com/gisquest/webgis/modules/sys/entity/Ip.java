/**
 * 
 */
package com.gisquest.webgis.modules.sys.entity;

import java.io.Serializable;

/**
 * @author Administrator
 *
 */
public class Ip implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -916092903859512939L;
	
	private String opt;
	
	private String id;
	private String ip;
	private String name;
	public String getOpt() {
		System.out.println(getOpt());
		return opt;
	}
	public void setOpt(String opt) {
		this.opt = opt;
	}
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
//	public UUID getId() {
//		
//		return id;
//	}
//	public void setId(UUID id) {
//		this.id = id;
//	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
