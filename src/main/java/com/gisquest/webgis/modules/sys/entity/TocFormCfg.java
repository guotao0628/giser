package com.gisquest.webgis.modules.sys.entity;

public class TocFormCfg {
	private String id; // 图层ID
	private String keywords; // 表单参数
	private String keyMaps; // 参数映射
	private String reportUrl; // 表单映射

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getKeyMaps() {
		return keyMaps;
	}

	public void setKeyMaps(String keyMaps) {
		this.keyMaps = keyMaps;
	}

	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}

}
