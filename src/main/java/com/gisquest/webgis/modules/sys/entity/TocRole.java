/**
 * 
 */
package com.gisquest.webgis.modules.sys.entity;

import java.io.Serializable;

/**
 * @author Administrator
 *
 */
public class TocRole implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2097505743367605179L;
	private String roleId;
	private String privilegId;
	private String tocIds[];
	private String tocPrivis[];
	private String tocInitals[];
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getPrivilegId() {
		return privilegId;
	}
	public void setPrivilegId(String privilegId) {
		this.privilegId = privilegId;
	}
	public String[] getTocIds() {
		return tocIds;
	}
	public void setTocIds(String[] tocIds) {
		this.tocIds = tocIds;
	}
	public String[] getTocPrivis() {
		return tocPrivis;
	}
	public void setTocPrivis(String[] tocPrivis) {
		this.tocPrivis = tocPrivis;
	}
	public String[] getTocInitals() {
		return tocInitals;
	}
	public void setTocInitals(String[] tocInitals) {
		this.tocInitals = tocInitals;
	}

}

