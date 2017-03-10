package com.gisquest.webgis.modules.sys.entity;

import java.util.List;

/**
 * 资源方案功能实体类
 * @author Jisj1
 *
 */
public class ResourceSolutionWidgets {
	List<ResourceSolutionPreWidget> preWidgets;
	List<ResourceSolutionPoolWidget> poolWidgets;
	
	public List<ResourceSolutionPreWidget> getPreWidgets() {
		return preWidgets;
	}
	public void setPreWidgets(List<ResourceSolutionPreWidget> preWidgets) {
		this.preWidgets = preWidgets;
	}
	public List<ResourceSolutionPoolWidget> getPoolWidgets() {
		return poolWidgets;
	}
	public void setPoolWidgets(List<ResourceSolutionPoolWidget> poolWidgets) {
		this.poolWidgets = poolWidgets;
	}
	
}
