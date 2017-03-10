
package com.gisquest.webgis.modules.sys.entity;

import java.io.Serializable;

/**
 * @author Administrator
 *
 */
public class General implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2974324956754372415L;
	private String service;
	private String typeKey;
	private boolean dic;
	private String saveUrl;
	private String deleteUrl;
	private String sde;
	private String url;
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getTypeKey() {
		return typeKey;
	}
	public void setTypeKey(String typeKey) {
		this.typeKey = typeKey;
	}
	public boolean isDic() {
		return dic;
	}
	public void setDic(boolean dic) {
		this.dic = dic;
	}
	public String getSaveUrl() {
		return saveUrl;
	}
	public void setSaveUrl(String saveUrl) {
		this.saveUrl = saveUrl;
	}
	public String getDeleteUrl() {
		return deleteUrl;
	}
	public void setDeleteUrl(String deleteUrl) {
		this.deleteUrl = deleteUrl;
	}
	public String getSde() {
		return sde;
	}
	public void setSde(String sde) {
		this.sde = sde;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

}

