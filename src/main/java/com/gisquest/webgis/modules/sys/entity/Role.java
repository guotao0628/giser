package com.gisquest.webgis.modules.sys.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Role实体类
 * 
 * @author Jisj1
 *
 */
public class Role {

	private String id; // 角色id
	private String name; // 角色名
	private String roleGroupId; // 角色组id
	private String description; // 描述
	private int sequence; // 序列
	private String roleCode; // 角色编号
	private List<Privilege> privileges = new ArrayList<>(); // 角色拥有的权限

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

	public String getRoleGroupId() {
		return roleGroupId;
	}

	public void setRoleGroupId(String roleGroupId) {
		this.roleGroupId = roleGroupId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public List<Privilege> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(List<Privilege> privileges) {
		this.privileges = privileges;
	}
}
