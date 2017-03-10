/**
 * 
 */
package com.gisquest.webgis.modules.sys.entity;

import java.io.Serializable;

/**
 * @author Administrator
 *
 */
public class WidgetRole implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9195972480618611254L;
	private String roleId;
	private String privilegId;
	private String preWidgetIds[];
	private String widgetPoolIds[];

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

	public String[] getPreWidgetIds() {
		return preWidgetIds;
	}

	public void setPreWidgetIds(String[] preWidgetIds) {
		this.preWidgetIds = preWidgetIds;
	}

	public String[] getWidgetPoolIds() {
		return widgetPoolIds;
	}

	public void setWidgetPoolIds(String[] widgetPoolIds) {
		this.widgetPoolIds = widgetPoolIds;
	}


}
