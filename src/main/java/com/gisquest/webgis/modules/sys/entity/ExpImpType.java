
/**
 * 
 */
package com.gisquest.webgis.modules.sys.entity;

import java.io.Serializable;

/**
 * @author Administrator
 *
 */
public class ExpImpType implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3285923178494126588L;
	private String id;
	private String yType;
	private String oType;
	private String label;
	private String ext;
	private String stat;
	private String Service;
	private String typeKey;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}



	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public String getService() {
		return Service;
	}

	public void setService(String service) {
		Service = service;
	}

	public String getTypeKey() {
		return typeKey;
	}

	public void setTypeKey(String typeKey) {
		this.typeKey = typeKey;
	}

	public String getyType() {
		return yType;
	}

	public void setyType(String yType) {
		this.yType = yType;
	}

	public String getoType() {
		return oType;
	}

	public void setoType(String oType) {
		this.oType = oType;
	}
}
