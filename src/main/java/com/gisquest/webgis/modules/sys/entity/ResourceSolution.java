package com.gisquest.webgis.modules.sys.entity;

import java.util.List;

/**
 * 资源方案实体类
 * @author Jisj1
 *
 */
public class ResourceSolution {
	String id;
	String name;
	String thumb;
	List<ResourceSolutionLayer> layers;
	List<ResourceSolutionAnaly> analys;
	ResourceSolutionWidgets widgets;
	List<String> tocSolutionId;
	
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
	public String getThumb() {
		return thumb;
	}
	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
	public List<ResourceSolutionLayer> getLayers() {
		return layers;
	}
	public void setLayers(List<ResourceSolutionLayer> layers) {
		this.layers = layers;
	}
	public List<ResourceSolutionAnaly> getAnalys() {
		return analys;
	}
	public void setAnalys(List<ResourceSolutionAnaly> analys) {
		this.analys = analys;
	}
	public ResourceSolutionWidgets getWidgets() {
		return widgets;
	}
	public void setWidgets(ResourceSolutionWidgets widgets) {
		this.widgets = widgets;
	}
	public List<String> getTocSolutionId() {
		return tocSolutionId;
	}
	public void setTocSolutionId(List<String> tocSolutionId) {
		this.tocSolutionId = tocSolutionId;
	}
	@Override
	public String toString() {
		return "ResourceSolution [id=" + id + ", name=" + name + ", thumb=" + thumb + ", layers=" + layers + ", analys="
				+ analys + ", widgets=" + widgets + ", tocSolutionId=" + tocSolutionId + "]";
	}
	
}
