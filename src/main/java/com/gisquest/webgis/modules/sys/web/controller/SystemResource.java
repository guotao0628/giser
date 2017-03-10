package com.gisquest.webgis.modules.sys.web.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gisquest.webgis.util.MonitorUtil;

/**
 * 该类的作用是获取系统资源
 * @author yedy
 */

@Controller
public class SystemResource {

	/**
	 * 获取系统资源
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/admin/sysInfo") // 设置获取系统资源服务地址
	public String processRequest(HttpServletResponse response) {
		response.setContentType("text/json");// 设置响应体的编码格式
		// 获取内存占用率
		String memoryPercent = (MonitorUtil.getComputerInfoRealTime().get("memUtilization"));
		String cpuInfo = MonitorUtil.getCpuInfo().get("cpuCompined");// 获取cpu占用率
		String cpu = cpuInfo.substring(0, cpuInfo.indexOf("."));// 截取获取cpu占用率小数点之前的数字
		// 截取内存占用率小数的之后1位之前的数字
		String men = String.valueOf(memoryPercent).substring(0, memoryPercent.indexOf(".") + 2);
		// 把获取到的信息拼接成一个json字符串的形式
		StringBuffer sysJson = new StringBuffer("[{\"" + "pro" + "\":\"" + MonitorUtil.systemProcess() + "\",\"" + "cpu"
				+ "\":\"" + cpu + "\",\"" + "mem" + "\":\"" + men + "\"}]");
		return sysJson.toString();
	}
}
