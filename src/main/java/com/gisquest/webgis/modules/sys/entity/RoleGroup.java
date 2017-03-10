package com.gisquest.webgis.modules.sys.entity;

import java.util.List;

/**
 * 角色组类
 * 
 * @author Jisj1
 *
 */
public class RoleGroup extends Role {

	/**
	 * 角色组中的角色
	 */
	private List<Role> roles;

	/**
	 * 获取角色组中的角色
	 * @return 角色
	 */
	public List<Role> getRoles() {
		return roles;
	}

	/**
	 * 设置角色组中的角色
	 * @param roles 角色
	 */
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
}
