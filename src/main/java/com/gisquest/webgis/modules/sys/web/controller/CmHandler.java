package com.gisquest.webgis.modules.sys.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gisquest.webgis.modules.sys.appcode.AnalyManager;
import com.gisquest.webgis.modules.sys.appcode.AnalyRoleManager;
import com.gisquest.webgis.modules.sys.appcode.ExpImpTypeManager;
import com.gisquest.webgis.modules.sys.appcode.GeneralManager;
import com.gisquest.webgis.modules.sys.appcode.IpManager;
import com.gisquest.webgis.modules.sys.appcode.TocRoleManager;
import com.gisquest.webgis.modules.sys.appcode.WidgetRoleManager;
import com.gisquest.webgis.modules.sys.entity.Analy;
import com.gisquest.webgis.modules.sys.entity.AnalyRole;
import com.gisquest.webgis.modules.sys.entity.ExpImpType;
import com.gisquest.webgis.modules.sys.entity.Ip;
import com.gisquest.webgis.modules.sys.entity.TocRole;
import com.gisquest.webgis.modules.sys.entity.WidgetRole;
import com.gisquest.webgis.util.Util;

/**
 * 
 * @author yedy
 *
 */
@Controller
public class CmHandler {
  /** 请求体 */
  @Autowired
  HttpServletRequest requestContent;
  /** 响应体 */
  @Autowired
  HttpServletResponse response;

  /**
   * 处理网页中POST请求
   * 
   * @return
   */
  @RequestMapping(value = "/admin/CmHandler/", method = RequestMethod.POST)
  @ResponseBody
  public void processRequset() {
    Util.G_REQUEST = requestContent;
    response.setContentType("text/plain");
    // 获取网页中请求参数
    String cmType = requestContent.getParameter("cmType");
    // 根据请求体的不同，给与不同处理方式
    switch (cmType) {
    case "analy":
      analyM(requestContent);
      break;
    case "imp":
      fileTypeM(requestContent);
      break;
    case "exp":
      fileTypeM(requestContent);
      break;
    case "ipc":
      ipConfigM(requestContent);
      break;
    case "saveCore":
      saveCoreConfig(requestContent);
      break;
    case "saveTocRole":
      saveTocRole(requestContent);
      break;
    case "saveWidRole":
      saveWidgetRole(requestContent);
      break;
    case "saveAnalyRole":
      saveAnalyRole(requestContent);
      break;
    default:
    }
  }

  /**
   * 设置基础服务
   * 
   * @param request
   */
  public static String saveCoreConfig(HttpServletRequest request) {
    String coreUrl = request.getParameter("coreUrl");
    String geoUrl = request.getParameter("geoUrl");
    GeneralManager generalManager = new GeneralManager();
    generalManager.saveConfig(coreUrl, "coreService", false);
    generalManager.saveConfig(geoUrl, "geometryService", false);
    return "success";
  }

  /**
   * 维护分析目录
   * 
   * @param context
   */
  public static void analyM(HttpServletRequest request) {
    String mTag = request.getParameter("MTag");
    String analyId = "";
    String analyType = "";
    String analyYear = "";
    String analyReport = "";
    String analyParam = "";
    String analyLable = "";
    String showIndex = "";
    String aIds = "";
    String aShowIndexs = "";
    Boolean isAutoGetXZQ = false;
    String xzqCode = "";
    Boolean isUseGZX = false;
    Boolean isUseYTQ = false;
    Boolean isUseGHYT = false;
    Boolean isUseGHJQ = false;

    String[] analyContent = request.getParameterValues("analyContent");
    if (analyContent != null) {
      for (String string : analyContent) {
        switch (string) {
        case "建设用地管制区":
          isUseGZX = true;
          break;
        case "土地用途区":
          isUseYTQ = true;
          break;
        case "规划用途":
          isUseGHYT = true;
          break;
        case "规划基期":
          isUseGHJQ = true;
          break;
        default:
          break;
        }
      }
    }
    if (request.getParameter("xzqCode") != null) {
      xzqCode = request.getParameter("xzqCode");
    }
    if (request.getParameter("achieveXZQ") != null) {
      isAutoGetXZQ = true;
    }
    if (request.getParameter("AnalyID") != null) {
      analyId = request.getParameter("AnalyID");
    }
    if (request.getParameter("AnalyIDs[]") != null) {
      aIds = request.getParameter("AnalyIDS[]");
    }
    if (request.getParameter("AnalyShowIndexs[]") != null) {
      aShowIndexs = request.getParameter("AnalyShowIndexs[]");
    }
    if (request.getParameter("AnalyType") != null) {
      analyType = request.getParameter("AnalyType");
    }
    if (request.getParameter("AnalyLabel") != null) {
      analyLable = request.getParameter("AnalyLabel");
    }
    if (request.getParameter("AnalyYear") != null) {
      analyYear = request.getParameter("AnalyYear");
    }
    if (request.getParameter("AnalyReport") != null) {
      analyReport = request.getParameter("AnalyReport");
    }
    if (request.getParameter("AnalyParam") != null) {
      analyParam = request.getParameter("AnalyParam");
    }
    String analyParent = "";
    if (request.getParameter("AnalyParent") != null) {
      analyParent = request.getParameter("AnalyParent");
    }
    String analyRel = "";
    if (request.getParameter("AnalyRel") != null) {
      analyRel = request.getParameter("AnalyRel");
    }
    String analyChecked = "";

    if (request.getParameter("AnalyChecked") != null) {
      analyChecked = "true";
    } else {
      analyChecked = "false";
    }
    if (request.getParameter("AnalyShowIndex") != null) {
      showIndex = request.getParameter("AnalyShowIndex");
    }

    AnalyManager analyManager = new AnalyManager();
    Analy analy = new Analy();
    analy.setmTag(mTag);
    analy.setAnalyId(analyId);
    analy.setAnalyLable(analyLable);
    analy.setAnalyType(analyType);
    analy.setAnalyReport(analyReport);
    analy.setAnalyYear(analyYear);
    analy.setAnalyParam(analyParam);
    analy.setAnalyParent(analyParent);
    analy.setAnalyChecked(analyChecked);
    analy.setShowIndex(aShowIndexs);
    analy.setTocIds(aIds);
    analy.setShowIndex(showIndex);
    analy.setAnalyRel(analyRel);
    analy.setIsAutoGetXZQ(isAutoGetXZQ);
    analy.setXzqCode(xzqCode);
    analy.setIsUseGZX(isUseGZX);
    analy.setIsUseYTQ(isUseYTQ);
    analy.setIsUseGHYT(isUseGHYT);
    analy.setIsUseGHJQ(isUseGHJQ);
    analyManager.processAnaly(analy);
  }

  /**
   * 维护ip设置
   * 
   * @param request
   */
  public String ipConfigM(HttpServletRequest request) {
    String opt = "";
    if (request.getParameter("Opt") != null) {
      opt = request.getParameter("Opt");
    }
    String id = "";
    if (request.getParameter("ID") != null) {
      id = request.getParameter("ID");
    }
    String ip = "";
    if (request.getParameter("IP") != null) {
      ip = request.getParameter("IP");
    }
    String name = "";
    if (request.getParameter("Name") != null) {
      name = request.getParameter("Name");
    }
    IpManager ipManager = new IpManager();
    Ip ips = new Ip();
    ips.setOpt(opt);
    ips.setOpt(id);
    ips.setIp(ip);
    ips.setName(name);
    ipManager.processIp(opt, id, ip, name);
    return "success";
  }

  /**
   * 维护导入导出文件类型
   * 
   * @param request
   */
  public String fileTypeM(HttpServletRequest request) {

    String yType = "";
    if (request.getParameter("YTpye") != null) {
      yType = request.getParameter("YTpye");
    }
    String oType = "";
    if (request.getParameter("OType") != null) {
      oType = request.getParameter("OType");
    }
    String id = "";
    if (request.getParameter("ID") != null) {
      id = request.getParameter("ID");
    }
    String stat = "";
    if (request.getParameter("STAT") != null) {
      stat = request.getParameter("STAT");
    }
    String label = "";
    if (request.getParameter("Label") != null) {
      label = request.getParameter("Label");
    }
    String ext = "";
    if (request.getParameter("Ext") != null) {
      ext = request.getParameter("Ext");
    }
    ExpImpTypeManager expImpManager = new ExpImpTypeManager();
    ExpImpType expImpType = new ExpImpType();
    expImpType.setId(id);
    expImpType.setyType(yType);
    expImpType.setoType(oType);
    expImpType.setLabel(label);
    expImpType.setExt(ext);
    expImpType.setStat(stat);
    expImpManager.processExpImp(expImpType);
    return "success";
  }

  /**
   * 权限分析中的图层目录
   * 
   * @param request
   * @return
   */
  public static String saveTocRole(HttpServletRequest request) {
    String roleId = request.getParameter("role");
    String privilegId = request.getParameter("privileg");
    String[] tocIds = {""};
    if (request.getParameterValues("TocIDs[]") != null) {
      tocIds = request.getParameterValues("TocIDs[]");
    }
    String[] tocPrivilegs = {""};
    if (request.getParameterValues("TocPrivis[]") != null) {
      tocPrivilegs = request.getParameterValues("TocPrivis[]");
    }
    String[] tocInitals = {""};
    if (request.getParameterValues("TocInitals[]") != null) {
      tocInitals = request.getParameterValues("TocInitals[]");
    }
    TocRoleManager tcRoleManager = new TocRoleManager();
    TocRole tocRole = new TocRole();
    tocRole.setRoleId(roleId);
    tocRole.setTocIds(tocIds);
    tocRole.setPrivilegId(privilegId);
    tocRole.setTocInitals(tocInitals);
    tocRole.setTocPrivis(tocPrivilegs);
    tcRoleManager.saveTocRole(tocRole);
    return "success";
  }

  /**
   * 权限管理中的插件设定
   * 
   * @param request
   * @return
   */
  public static String saveWidgetRole(HttpServletRequest request) {
    String roleId = request.getParameter("role");
    String privilegId = request.getParameter("privileg");
    String[] preWidgetIds = {""};
    if (request.getParameterValues("PreWidgets[]") != null) {
      preWidgetIds = request.getParameterValues("PreWidgets[]");
    }
    String[] widgetPoolIds = {""};
    if (request.getParameterValues("PoolWidgets[]") != null) {
      widgetPoolIds = request.getParameterValues("PoolWidgets[]");
    }
    WidgetRoleManager widgetRoleManager = new WidgetRoleManager();
    WidgetRole widgetRole = new WidgetRole();
    widgetRole.setRoleId(roleId);
    widgetRole.setPrivilegId(privilegId);
    widgetRole.setPreWidgetIds(preWidgetIds);
    widgetRole.setWidgetPoolIds(widgetPoolIds);
    widgetRoleManager.saveWidgetRole(widgetRole);
    return "success";
  }

  /**
   * 权限管理中的分析设定
   * 
   * @param request
   * @return
   */
  public static String saveAnalyRole(HttpServletRequest request) {
    String roleId = request.getParameter("role");
    String privilegId = request.getParameter("privileg");
    String[] analyIds = {""};
    if (request.getParameterValues("Analys[]") != null) {
      analyIds = request.getParameterValues("Analys[]");
    }
    AnalyRoleManager aRoleManager = new AnalyRoleManager();
    AnalyRole analyRole = new AnalyRole();
    analyRole.setRoleId(roleId);
    analyRole.setAnalyIds(analyIds);
    analyRole.setPrivilegId(privilegId);
    aRoleManager.saveAnalyRole(analyRole);
    return "success";
  }

  /**
   * 去掉字符串前面的空白
   */
  public static String trimStart(String s) {
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) == ' ') {
        s = s.substring(i, s.length());
        break;
      }
    }
    return s;
  }

}
