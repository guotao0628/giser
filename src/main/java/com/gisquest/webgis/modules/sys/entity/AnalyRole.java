/**
 * 
 */
package com.gisquest.webgis.modules.sys.entity;

import java.io.Serializable;

/**
 * 
 * @author Administrator
 *
 */
public class AnalyRole implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1953336102141322276L;
	private String roleId;
	private String privilegId;
	private String analyIds[];

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

	public String[] getAnalyIds() {
		return analyIds;
	}

	public void setAnalyIds(String analyIds[]) {
		this.analyIds = analyIds;
	}


}
