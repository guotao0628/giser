package com.gisquest.webgis.modules.sys.web.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gisquest.webgis.modules.sys.entity.Role;
import com.gisquest.webgis.modules.sys.entity.ServiceInvocationError;
import com.gisquest.webgis.modules.sys.services.ResourceSolutionService;
import com.gisquest.webgis.modules.sys.services.RightMngService;
import com.gisquest.webgis.util.Util;
import com.google.gson.GsonBuilder;

/**
 * 生成动态Config.json
 * 
 * @author Jisj1
 *
 */
@Controller
public class Config {
  /** 请求体 */
  @Autowired
  private HttpServletRequest request;
  /** 响应体 */
  @Autowired
  private HttpServletResponse response;
  /** 分析id */
  private List<String> aIds = null;
  /** 图层id */
  private Map<String, Object> tocIds = null;
  /** 初始加载图层id */
  private Map<String, Object> initalTocs = null;
  /** 预加载的模块id */
  private List<String> preWids = null;
  /** 可选的模块id */
  private List<String> poolWids = null;
  /** 图层方案id */
  private List<String> tocSolutionIds = null;

  /**
   * 获取动态的config.json
   * 
   * @return
   */
  @RequestMapping("/admin/Config")
  @ResponseBody
  public String getDynamicConfig() {
    /*
     * 设置响应编码和格式
     */
    response.setCharacterEncoding("UTF-8");
    response.setContentType("applicaton/json");
    /* 初始化privType */
    String privType = "";
    /* 设置Util的请求体 */
    Util.G_REQUEST = request;
    Util.ROOT_PATH = request.getSession().getServletContext().getRealPath("/");
    /* 获取reSolutionId */
    String reSolutionId = request.getParameter("reSolutionId");
    /* 获取服务器的IP */
    String localAddr = request.getLocalAddr();
    /* 获取参数p */
    String p = request.getParameter("p");
    if (p != null) {
      privType = p;
    }
    /* 如果p=root或builder */
    if ((privType.toLowerCase().trim().equals("root"))
        || privType.toLowerCase().trim().equals("builder")) {
      return createRootJson(reSolutionId, localAddr);
    } else if (privType.toLowerCase().trim().equals("user")) {
      // 如果p=user，获取参数userid
      String userid = request.getParameter("userid");
      if (userid != null) {
        getUserResource(userid);
        return createRoleJson(reSolutionId, localAddr);
      } else {
        return new ServiceInvocationError("未指定userid,无法获取数据。").toString();
      }
    } else {
      return new ServiceInvocationError("参数错误，无法获取数据。").toString();
    }
  }

  /**
   * 获取指定userid和资源方案id的图层名称列表
   * 
   * @return
   */
  @RequestMapping("Config/tocs/")
  @ResponseBody
  @SuppressWarnings("unchecked")
  public String getDynamicTocs() {
    String tag = request.getParameter("tag");
    String cfgJson = getDynamicConfig();
    Map<String, Object> cfgJsonMap = (Map<String, Object>) new GsonBuilder().create()
        .fromJson(cfgJson, Map.class);// 把config.json转为list
    if (cfgJsonMap != null) {
      if (cfgJsonMap.containsKey("toc")) {
        List<Map<String, Object>> tocList = (List<Map<String, Object>>) cfgJsonMap.get("toc");
        List<Map<String, Object>> tocWithDynamic = new ArrayList<>();
        // 判断是否含有type参数，如果type为dynamic，则返回toc中type为dynamic和feature的图层
        if (tag != null && "1".equals(tag)) {
          for (Map<String, Object> toc : tocList) {
            // 如果
            if ("dynamic".equals(toc.get("type")) || "feature".equals(toc.get("type"))) {
              tocWithDynamic.add(toc);
            }
          }
        }else{
          tocWithDynamic = tocList;
        }
        List<Map<String, Object>> tocNameWithoutFolder = new ArrayList<>();
        for (Map<String, Object> toc : tocWithDynamic) {
          if (toc.containsKey("layerTag") && !("maskLayer".equals(toc.get("layerTag")))) {
            Map<String, Object> tocMap = new LinkedHashMap<>();
            tocMap.put("label", toc.get("label"));
            tocMap.put("layerTag", toc.get("layerTag"));
            tocNameWithoutFolder.add(tocMap);
          }
        }
        List<Map<String, Object>> tempList = tocNameWithoutFolder;
        String tocJson = new GsonBuilder().create().toJson(tempList);
        /* 对最终的resultJson做处理，将其中用{}包含的变量替换成参数池中的参数 */
        // 获取参数池中的参数
        List<Map<String, Object>> ipPool = (List<Map<String, Object>>) cfgJsonMap.get("IpPool");
        for (Map<String, Object> ip : ipPool) {
          String ipName = (String) ip.get("name");
          String ipValue = (String) ip.get("ip");
          tocJson = tocJson.replace("{" + ipName + "}", ipValue);
        }
        return tocJson;
      }
    }
    return cfgJson;
  }

  /**
   * 动态生成root权限的config.json
   * 
   * @param reSolutionId
   *          资源方案id
   * @param localAddr
   *          服务器的IP
   * @return
   */
  @SuppressWarnings("unchecked")
  private String createRootJson(String reSolutionId, String localAddr) {
    /* 获取config.json并解析list */
    String json = Util.addCorrectFormat(Util.getConfigJson());
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(json, List.class);
    // 未选择的图层id
    List<Map<String, Object>> unselectedLayers = new ArrayList<>();
    // 未选择的分析项id
    List<Map<String, Object>> unselectedAnlyss = new ArrayList<>();
    // 未选择的预加载模块id
    List<Map<String, Object>> unselectedPrews = new ArrayList<>();
    // 未选择的模块组件id
    List<Map<String, Object>> unselectedPoolws = new ArrayList<>();
    // 未选择的图层方案id
    List<Map<String, Object>> unselectedSols = new ArrayList<>();
    if (cfgJsonList.size() > 0) {
      Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
      if (reSolutionId != null && !reSolutionId.equals("undefined")) {
        /* 获取reSolution */
        String reSolutionJson = new ResourceSolutionService().getReSolution();
        List<Map<String, Object>> reSolutions = new GsonBuilder().create().fromJson(reSolutionJson,
            List.class);
        /* 设置资源方案是否找到，初始状态为未找到 */
        boolean isReSolutionFound = false;
        // 若reSolutions中有元素，则遍历reSolutions list
        for (Map<String, Object> reSolutionMap : reSolutions) {
          // 如果找到平台传来的资源方案id，则据资源方案获取资源
          if (reSolutionId.equals(reSolutionMap.get("id"))) {
            // 该资源方案中的图层
            List<Map<String, Object>> layersList = (List<Map<String, Object>>) reSolutionMap
                .get("layers");
            /* 该资源方案中的预加载模块和模块组件 */
            Map<String, Object> widgetsMap = (Map<String, Object>) reSolutionMap.get("widgets");
            List<Map<String, Object>> preWidgetsList = (List<Map<String, Object>>) widgetsMap
                .get("preWidgets");
            List<Map<String, Object>> poolWidgetsList = (List<Map<String, Object>>) widgetsMap
                .get("poolWidgets");
            // 该资源方案中的分析项
            List<Map<String, Object>> analysList = (List<Map<String, Object>>) reSolutionMap
                .get("analys");
            // 该资源方案中的图层方案
            List<String> tocSolutionList = (List<String>) reSolutionMap.get("tocSolutionId");
            /*
             * 遍历并判断config. json中toc项的每一个图层项id是否出现在layersList，如果没出现， 则将该项从toc中删除
             */
            if (cfgJsonMap.containsKey("toc")) {
              // 获取config.json中的toc项并遍历toc项
              List<Map<String, Object>> tocList = (List<Map<String, Object>>) cfgJsonMap.get("toc");
              for (Map<String, Object> toc : tocList) {
                // 设置一个flag，是否在layersList中找到当前遍历到的toc项
                boolean isFound = false;
                // 遍历layersList中的每一个子项
                for (Map<String, Object> layer : layersList) {
                  // 如果在layersList中找到对应的toc ID
                  if (toc.get("id").equals(layer.get("id"))) {
                    // 先判断当前toc项的初始加载状态是否为空
                    if (toc.get("initChecked") != null) {
                      // 如果不为空，则将资源方案中配置的初始加载状态赋给当前toc项
                      toc.put("initChecked", (Boolean) layer.get("inital"));
                    }
                    // 再把资源方案中配置的权限赋给当前toc项
                    toc.put("privi", layer.get("privi"));
                    /* 找到当前toc项，将flag设为true并跳出循环 */
                    isFound = true;
                    break;
                  }
                }
                // 如果在资源方案中没找到当前的toc
                if (!isFound) {
                  // 先判断当前toc项是否为掩膜图层（掩膜图层始终加载，不受资源方案影响）
                  if (!toc.get("id").equals("MaskLayer")) {
                    // 如果不是，则将该toc项添加到未被选择的图层列表中
                    unselectedLayers.add(toc);
                  }
                }
              }
              /* 遍历结束后，将未被选择的图层从tocs中删除 */
              if (unselectedLayers.size() > 0) {
                // 遍历未被选择的图层
                for (int i = unselectedLayers.size() - 1; i >= 0; i--) {
                  // 获取当前遍历到的图层在tocList的索引
                  int index = tocList.indexOf(unselectedLayers.get(i));
                  // 从tocList中删除当前遍历到图层
                  tocList.remove(index);
                }
              }
            }
            /*
             * 遍历并判断analyze中的每一个id是否出现在资源方案中，如果没出现， 则将该id从analyze中删除
             */
            if (cfgJsonMap.containsKey("analyze")) {
              // 获取分析项
              List<Map<String, Object>> analyzeList = (List<Map<String, Object>>) cfgJsonMap
                  .get("analyze");
              // 遍历分析项
              for (Map<String, Object> analyze : analyzeList) {
                // 设置flag，是否在所有分析项中找到资源方案中配置的分析项，初始为未找到
                boolean isFound = false;
                // 遍历资源方案中配置的分析项
                for (Map<String, Object> analys : analysList) {
                  // 如果找到，则将flag设为true,并跳出循环
                  if (analyze.get("id").equals(analys.get("id"))) {
                    isFound = true;
                    break;
                  }
                }
                // 如果遍历完资源方案中配置的分析项，还未找到当前遍历的分析项
                if (!isFound) {
                  // 则在未选择的分析项中添加当前遍历的分析项
                  unselectedAnlyss.add(analyze);
                }
              }
              /* 从所有分析项中删除未被选中的分析项 */
              if (unselectedAnlyss.size() > 0) {
                for (int i = unselectedAnlyss.size() - 1; i >= 0; i--) {
                  int index = analyzeList.indexOf(unselectedAnlyss.get(i));
                  analyzeList.remove(index);
                }
              }
            }

            /*
             * 遍历并判断preloadWidgets中的每一个id是否出现在资源方案中， 如果没出现，
             * 则将该id从preloadWidgets中删除
             */
            if (cfgJsonMap.containsKey("preloadWidgets")) {
              /* 获取preloadWidgets中的模块列表 */
              Map<String, Object> preloadWidgetList = (Map<String, Object>) cfgJsonMap
                  .get("preloadWidgets");
              List<Map<String, Object>> preWidgetList = (List<Map<String, Object>>) preloadWidgetList
                  .get("widgets");
              /* 遍历preloadWidgets中的模块 */
              for (Map<String, Object> preWidget : preWidgetList) {
                boolean isFound = false;
                for (Map<String, Object> preWidgets : preWidgetsList) {
                  if (preWidget.get("id").equals(preWidgets.get("id"))) {
                    isFound = true;
                    break;
                  }
                }
                if (!isFound) {
                  unselectedPrews.add(preWidget);
                }
              }
              /* 删除未选中的preloadWidgets模块 */
              if (unselectedPrews.size() > 0) {
                for (int i = unselectedPrews.size() - 1; i >= 0; i--) {
                  int index = preWidgetList.indexOf(unselectedPrews.get(i));
                  preWidgetList.remove(index);
                }
              }
            }

            /*
             * 遍历并判断poolWidgets中的每一个id是否出现在资源方案中， 如果没出现， 则将该id从poolWidgets中删除
             */
            if (cfgJsonMap.containsKey("widgetPool")) {
              /* 获取poolWidgets列表 */
              Map<String, Object> widgetPoolList = (Map<String, Object>) cfgJsonMap
                  .get("widgetPool");
              List<Map<String, Object>> widgetsList = (List<Map<String, Object>>) widgetPoolList
                  .get("widgets");
              /* 遍历poolWidgets */
              for (Map<String, Object> widget : widgetsList) {
                boolean isFound = false;
                for (Map<String, Object> poolWidget : poolWidgetsList) {
                  if (widget.get("id").equals(poolWidget.get("id"))) {
                    isFound = true;
                    break;
                  }
                }
                if (!isFound) {
                  unselectedPoolws.add(widget);
                } else {
                  /*
                   * 如果找到了，则判断当前widget是否有子节点，如果有子节点
                   */
                  if (widget.containsKey("widgets")) {
                    /* 则获取其子节点 */
                    List<Map<String, Object>> innerWidgets = (List<Map<String, Object>>) widget
                        .get("widgets");
                    /* 并遍历子节点 */
                    for (Map<String, Object> map : innerWidgets) {
                      /* 设置flag为false */
                      isFound = false;
                      /* 遍历资源方案中的poolWidgets */
                      for (Map<String, Object> poolWidget : poolWidgetsList) {
                        /* 如果在资源方案中找到对应的id */
                        if (map.get("id").equals(poolWidget.get("id"))) {
                          /* 设置flag为true，并跳出循环 */
                          isFound = true;
                          break;
                        }
                      }
                      /* 如果遍历完资源方案仍没找到 */
                      if (!isFound) {
                        /* 将该子节点也加入未选中的列表中 */
                        unselectedPoolws.add(map);
                      }
                    }
                  }
                }
              }
              /* 删除未选择的poolWidgets模块 */
              if (unselectedPoolws.size() > 0) {
                /* 遍历未选中列表 */
                for (int i = unselectedPoolws.size() - 1; i >= 0; i--) {
                  /* 获取未选中项在所有外层模块列表的索引 */
                  int index = widgetsList.indexOf(unselectedPoolws.get(i));
                  /* 如果在外层模块列表中找到该项 */
                  if (index != -1) {
                    /* 则将该项从外层模块列表中删除 */
                    widgetsList.remove(index);
                  } else {
                    /* 否则，遍历内层模块 */
                    /* 先遍历外层模块 */
                    for (Map<String, Object> map : widgetsList) {
                      /* 判断是否外层模块是否有内层模块 */
                      if (map.containsKey("widgets")) {
                        /* 如果有，则获取内层模块 */
                        List<Map<String, Object>> innerWidgets = (List<Map<String, Object>>) map
                            .get("widgets");
                        index = innerWidgets.indexOf(unselectedPoolws.get(i));
                        /* 如果在内层模块列表中找到该项 */
                        if (index != -1) {
                          /* 则将该项从该内层模块列表中删除 */
                          innerWidgets.remove(index);
                        }
                      }
                    }
                  }
                }
              }
            }
            /*
             * 遍历并判断solution中的每一个id是否出现在资源方案中， 如果没出现， 则将该id从solution中删除
             */
            if (cfgJsonMap.containsKey("solution")) {
              // 获取图层方案列表
              List<Map<String, Object>> solutionList = (List<Map<String, Object>>) cfgJsonMap
                  .get("solution");
              /* 遍历图层方案 */
              for (Map<String, Object> solution : solutionList) {
                boolean isFound = false;
                for (String tocSolution : tocSolutionList) {
                  if (solution.get("id").equals(tocSolution)) {
                    isFound = true;
                    break;
                  }
                }
                if (!isFound) {
                  unselectedSols.add(solution);
                }
              }
              /* 删除未选中的图层方案 */
              if (unselectedSols.size() > 0) {
                for (int i = unselectedSols.size() - 1; i >= 0; i--) {
                  int index = solutionList.indexOf(unselectedSols.get(i));
                  solutionList.remove(index);
                }
              }
            }
            isReSolutionFound = true;
            break;
          }
        }
        /* 如果遍历完资源方案仍未找到，说明id不存在，则返回错误信息 */
        if (!isReSolutionFound) {
          return new ServiceInvocationError("资源方案id不存在，无法获取信息。").toString();
        }
      }
    }
    /* 把获取到的修改后的数据转化为json */
    String dataJson = new GsonBuilder().create().toJson(cfgJsonList);
    String lastJson = dataJson.substring(1, (dataJson.toString().length() - 1));
    /* 将所有{dynhost}替换成服务器IP */
    lastJson = lastJson.replace("[dynhost]", localAddr);
    /* 返回动态生成的config.json */
    return lastJson;
  }

  /**
   * 动态生成user权限的config.json
   * 
   * @param reSolutionId
   *          资源方案id
   * @param localAddr
   *          服务器IP
   * @return
   */
  @SuppressWarnings({"unchecked"})
  private String createRoleJson(String reSolutionId, String localAddr) {
    /* 获取Config.json并解析成List */
    String cfgJson = Util.addCorrectFormat(Util.getConfigJson());
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);
    /* 如果有reSolutionId */
    if (cfgJsonList.size() > 0) {
      Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
      if (reSolutionId != null && !reSolutionId.equals("undefined")) {
        /* 获取reSolution */
        String reSolutionJson = new ResourceSolutionService().getReSolution();
        List<Map<String, Object>> reSolutions = new GsonBuilder().create().fromJson(reSolutionJson,
            List.class);
        /* 若reSolutions中有元素，则遍历reSolutions list */
        if (reSolutions.size() > 0) {
          /* 设置资源方案是否找到，初始状态为未找到 */
          boolean isReSolutionFound = false;
          for (Map<String, Object> reSolutionMap : reSolutions) {
            /* 如果找到平台传来的资源方案id，则据资源方案获取资源 */
            if (reSolutionId.equals(reSolutionMap.get("id"))) {
              /* 获取资源方案中的图层项 */
              List<Map<String, Object>> layersList = (List<Map<String, Object>>) reSolutionMap
                  .get("layers");
              /* 获取资源方案中的模块项 */
              Map<String, Object> widgetsMap = (Map<String, Object>) reSolutionMap.get("widgets");
              /* 获取资源方案中的分析项 */
              List<Map<String, Object>> analysList = (List<Map<String, Object>>) reSolutionMap
                  .get("analys");
              /* 获取资源方案中的图层方案项 */
              List<String> tSolutionIds = (List<String>) reSolutionMap.get("tocSolutionId");
              /*
               * 遍历并判断tocIds中的每一个id是否出现在资源方案，如果没出现， 则将该id从tocIds中删除
               */
              List<String> unselectedTocIds = new ArrayList<>();
              Iterator<String> iterator = tocIds.keySet().iterator();
              while (iterator.hasNext()) {
                String id = (String) iterator.next();
                boolean isFound = false;
                for (Map<String, Object> layersMap : layersList) {
                  if (id.equals(layersMap.get("id"))) {
                    String priviRight = (String) tocIds.get(id);
                    String priviSolu = (String) layersMap.get("privi");
                    if (!priviRight.equals(priviSolu)) {
                      // 如果两个相同，则toc的privi为它们的值,不变;如果两个不同，则分情况
                      if (priviRight.equals("") || priviSolu.equals("")) {
                        // 如果两个有一个为空，则toc的privi为空
                        tocIds.put(id, "");
                      } else if (priviRight.equals("save")) {
                        if (priviSolu.equals("del")) {
                          tocIds.put(id, "");
                        } else if (priviSolu.equals("save_del")) {
                          tocIds.put(id, "save");
                        }
                      } else if (priviRight.equals("del")) {
                        if (priviSolu.equals("save")) {
                          tocIds.put(id, "");
                        } else if (priviSolu.equals("save_del")) {
                          tocIds.put(id, "del");
                        }
                      } else if (priviRight.equals("save_del")) {
                        if (priviSolu.equals("save")) {
                          tocIds.put(id, "save");
                        } else if (priviSolu.equals("del")) {
                          tocIds.put(id, "del");
                        }
                      }
                    }
                    isFound = true;
                    break;
                  }
                }
                if (!isFound) {
                  unselectedTocIds.add(id);
                }
              }
              if (unselectedTocIds.size() > 0) {
                for (int i = unselectedTocIds.size() - 1; i >= 0; i--) {
                  tocIds.remove(unselectedTocIds.get(i));
                }
              }
              // initalTocs
              for (String tocId : initalTocs.keySet()) {
                boolean isFound = false;
                for (Map<String, Object> layersMap : layersList) {
                  if (tocId != null && tocId.equals(layersMap.get("id"))) {
                    if ((Boolean) initalTocs.get(tocId) == (Boolean) layersMap.get("inital")) {
                      if ((Boolean) (initalTocs.get(tocId))) {
                        isFound = true;
                      }
                      break;
                    }
                  }
                }
                if (!isFound) {
                  initalTocs.put(tocId, false);
                } else {
                  initalTocs.put(tocId, true);
                }
              }
              // aIds中的第一个id是否出现在资源方案中，如果没出现，则将该id从aIds中删除
              for (int i = aIds.size() - 1; i >= 0; i--) {
                boolean isFound = false;
                for (Map<String, Object> analysMap : analysList) {
                  if (aIds.get(i).equals(analysMap.get("id"))) {
                    isFound = true;
                    break;
                  }
                }
                if (!isFound) {
                  aIds.remove(aIds.get(i));

                }
              }
              // preWids
              List<Map<String, Object>> preWidgets = (List<Map<String, Object>>) widgetsMap
                  .get("preWidgets");
              for (int i = preWids.size() - 1; i >= 0; i--) {
                boolean isFound = false;
                for (Map<String, Object> preWidgetMap : preWidgets) {
                  if (preWids.get(i).equals(preWidgetMap.get("id"))) {
                    isFound = true;
                    break;
                  }
                }
                if (!isFound) {
                  preWids.remove(preWids.get(i));
                }
              }
              /* 获取模块组件列表 */
              List<Map<String, Object>> poolWidgets = (List<Map<String, Object>>) widgetsMap
                  .get("poolWidgets");
              /* 遍历poolWids */
              for (int i = poolWids.size() - 1; i >= 0; i--) {
                boolean isFound = false;
                /* 遍历模块组件列表 */
                for (Map<String, Object> poolWidgetMap : poolWidgets) {
                  if (poolWids.get(i).equals(poolWidgetMap.get("id"))) {
                    isFound = true;
                    break;
                  }
                }
                if (!isFound) {
                  poolWids.remove(poolWids.get(i));
                }
              }
              // 图层方案
              for (int i = tocSolutionIds.size() - 1; i >= 0; i--) {
                boolean isFound = false;
                for (String id : tSolutionIds) {
                  if (tocSolutionIds.get(i).equals(id)) {
                    isFound = true;
                    break;
                  }
                }
                if (!isFound) {
                  tocSolutionIds.remove(tocSolutionIds.get(i));
                }
              }
              isReSolutionFound = true;
              break;
            }
          }
          /* 如果遍历完资源方案仍没找到，说明指定的资源方案id不存在，则返回错误信息 */
          if (!isReSolutionFound) {
            return new ServiceInvocationError("资源方案id不存在，无法获取信息。").toString();
          }
        }
      }
      // 添加掩模图层
      tocIds.put("MaskLayer", "");
      initalTocs.put("MaskLayer", false);
      /* 删除没有权限的图层 */
      if (cfgJsonMap.containsKey("toc")) {
        List<Map<String, Object>> toc = (List<Map<String, Object>>) cfgJsonMap.get("toc");
        List<Map<String, Object>> selectLayers = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> item : toc) {
          if (tocIds.containsKey(item.get("id"))) {
            selectLayers.add(item);
          }
        }
        if (selectLayers.size() > 0) {
          for (Map<String, Object> selectLayer : selectLayers) {
            selectLayer.put("privi", tocIds.get(selectLayer.get("id")));
            if (selectLayer.containsKey("initChecked")) {
              selectLayer.put("initChecked", (Boolean) initalTocs.get(selectLayer.get("id")));
            }
          }
        }
        List<Map<String, Object>> unSelectLayers = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> item : toc) {
          if (!tocIds.containsKey(item.get("id").toString())) {
            unSelectLayers.add(item);
          }
        }
        if (unSelectLayers.size() > 0) {
          for (int i = unSelectLayers.size() - 1; i >= 0; i--) {
            int index = toc.indexOf(unSelectLayers.get(i));
            toc.remove(index);
          }
        }
      }
      /* 删除没有权限的分析 */
      if (cfgJsonMap.containsKey("analyze")) {
        List<Map<String, Object>> analys = (List<Map<String, Object>>) cfgJsonMap.get("analyze");
        List<Map<String, Object>> unSelectAnalys = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> analysEntry : analys) {
          if (!aIds.contains(analysEntry.get("id"))) {
            unSelectAnalys.add(analysEntry);
          }
        }
        if (unSelectAnalys.size() > 0) {
          for (int i = unSelectAnalys.size() - 1; i >= 0; i--) {
            int index = analys.indexOf(unSelectAnalys.get(i));
            analys.remove(index);
          }
        }
      }
      /* 删除没有配置的图层方案 */
      if (cfgJsonMap.containsKey("solution")) {
        List<Map<String, Object>> solutionList = (List<Map<String, Object>>) cfgJsonMap
            .get("solution");
        for (int i = solutionList.size() - 1; i >= 0; i--) {
          if (!tocSolutionIds.contains(solutionList.get(i).get("id"))) {
            solutionList.remove(i);
          }
        }
      }
      /* 删除没有权限的预加载模块 */
      if (cfgJsonMap.containsKey("preloadWidgets")) {
        Map<String, Object> preLoadWidgetsMap = (Map<String, Object>) cfgJsonMap
            .get("preloadWidgets");
        List<Map<String, Object>> preLoadWidgets = (List<Map<String, Object>>) preLoadWidgetsMap
            .get("widgets");
        List<Map<String, Object>> unSelectWidgets = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> entry : preLoadWidgets) {
          if (!preWids.contains(entry.get("id"))) {
            unSelectWidgets.add(entry);
          }
        }
        if (unSelectWidgets.size() > 0) {
          for (int i = unSelectWidgets.size() - 1; i >= 0; i--) {
            int index = preLoadWidgets.indexOf(unSelectWidgets.get(i));
            preLoadWidgets.remove(index);
          }
        }
      }
      /* 删除没有权限的功能模块 */
      if (cfgJsonMap.containsKey("widgetPool")) {
        Map<String, Object> poolWidgetsMap = (Map<String, Object>) cfgJsonMap.get("widgetPool");
        List<Map<String, Object>> poolWidgets = (List<Map<String, Object>>) poolWidgetsMap
            .get("widgets");
        List<Map<String, Object>> unSelectWidgets = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> poolWidgetsEntry : poolWidgets) {
          // 判断是否存在widgets键 ，即是否为模块组
          if (poolWidgetsEntry.containsKey("widgets")) {
            // 若存在widgets键，即模块为模型组，则遍历模型组中的模块
            List<Map<String, Object>> unSelectInnerWidgets = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> innnerWidgets = (List<Map<String, Object>>) poolWidgetsEntry
                .get("widgets");
            for (Map<String, Object> innnerWidgetMap : innnerWidgets) {
              if (!poolWids.contains(innnerWidgetMap.get("id"))) {
                unSelectInnerWidgets.add(innnerWidgetMap);
              }
            }
            if (unSelectInnerWidgets.size() > 0) {
              for (int i = unSelectInnerWidgets.size() - 1; i >= 0; i--) {
                int index = innnerWidgets.indexOf(unSelectInnerWidgets.get(i));
                innnerWidgets.remove(index);
              }
            }
          }
          if (!poolWids.contains(poolWidgetsEntry.get("id"))) {
            unSelectWidgets.add(poolWidgetsEntry);
          }
        }
        if (unSelectWidgets.size() > 0) {
          for (int i = unSelectWidgets.size() - 1; i >= 0; i--) {
            int index = poolWidgets.indexOf(unSelectWidgets.get(i));
            poolWidgets.remove(index);
          }
        }
      }
    }
    // 把获取到的修改后的数据转化为json
    String dataJson = new GsonBuilder().create().toJson(cfgJsonList);
    String lastJson = dataJson.substring(1, (dataJson.toString().length() - 1));
    /* 将所有{dynhost}替换成服务器IP */
    return lastJson.replace("[dynhost]", localAddr);
  }

  /**
   * 根据用户id获取用户拥有的资源
   * 
   * @param userid
   */
  @SuppressWarnings("unchecked")
  private void getUserResource(String userid) {
    // 初始化选中分析项id列表
    aIds = new ArrayList<String>();
    // 初始化选中图层项id列表
    tocIds = new LinkedHashMap<String, Object>();
    // 初始化选中初始加载图层列表
    initalTocs = new LinkedHashMap<String, Object>();
    // 初始化预加载模块id列表
    preWids = new ArrayList<String>();
    // 初始化模块组件id列表
    poolWids = new ArrayList<String>();
    // 初始化图层方案id列表
    tocSolutionIds = new ArrayList<String>();
    // 初始化电子政务版本
    String dzzwVersion = null;
    // 初始化用户角色列表
    List<Role> userRoles = new ArrayList<>();
    /*
     * 获取并解析config.json
     */
    String cfgJson = Util.getConfigJson();
    cfgJson = Util.addCorrectFormat(cfgJson);
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);
    if (cfgJsonList.size() > 0) {
      Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
      // 获取电子政务版本
      dzzwVersion = (String) cfgJsonMap.get("dzzw");
      // 从config.json中获取图层方案
      List<Map<String, Object>> solutionList = (List<Map<String, Object>>) cfgJsonMap
          .get("solution");
      /*
       * 遍历图层方案，并将每个方案id添加到tocSolutionIds中
       */
      for (Map<String, Object> map : solutionList) {
        tocSolutionIds.add((String) map.get("id"));
      }
    }

    /*
     * 判断电子政务的版本，调用不同的服务获取用户拥有的角色
     */
    RightMngService roleService = new RightMngService();
    if (dzzwVersion != null && dzzwVersion.equalsIgnoreCase(".net")) {
      try {
        userRoles = roleService.getDotnetUserRoles(userid);
      } catch (SQLException e) {
        throw new RuntimeException("数据库访问异常");
      }
    } else if (dzzwVersion != null && dzzwVersion.equalsIgnoreCase("java")) {
      userRoles = roleService.getUserRoles(userid);
    }

    /*
     * 获取并解析角色权限JSON
     */
    String roleJson = Util.getRoleJson();
    roleJson = Util.addCorrectFormatRole(roleJson);
    List<Map<String, Object>> roleJsonList = new GsonBuilder().create().fromJson(roleJson,
        List.class);
    if (roleJsonList.size() > 0) {
      Map<String, Object> roleJsonMap = roleJsonList.get(0);
      /*
       * 如果roleJson中包含roles键
       */
      if (roleJsonMap.containsKey("roles")) {
        /* 获取roles键的值，即角色信息列表 */
        List<Map<String, Object>> roles = (List<Map<String, Object>>) roleJsonMap.get("roles");
        /* 遍历用户角色 */
        for (Role userRole : userRoles) {
          /*
           * 遍历role.json中的所有角色Map
           */
          for (Map<String, Object> roleMap : roles) {
            if (roleMap.containsKey("id")
                && roleMap.get("id").equals(String.valueOf(userRole.getId()))) {
              /* 获取用户权限 */
              List<Map<String, Object>> privileges = (List<Map<String, Object>>) roleMap
                  .get("privilegs");
              /*
               * 遍历用户权限
               */
              for (Map<String, Object> privilege : privileges) {
                /* 获取权限中的资源 */
                Map<String, Object> resource = (Map<String, Object>) privilege.get("resource");
                /*
                 * 获取分析权限
                 */
                if (resource.containsKey("analys")) {
                  List<Map<String, Object>> analys = (List<Map<String, Object>>) resource
                      .get("analys");
                  for (Map<String, Object> analy : analys) {
                    if (!analy.get("id").equals("")) {
                      if (!aIds.contains(analy.get("id"))) {
                        aIds.add((String) analy.get("id"));
                      }
                    }

                  }
                }

                /*
                 * 获取图层权限
                 */
                if (resource.containsKey("layers")) {
                  List<Map<String, Object>> layers = (List<Map<String, Object>>) resource
                      .get("layers");
                  for (Map<String, Object> layer : layers) {
                    if (!tocIds.containsKey(layer.get("id"))) {
                      tocIds.put((String) layer.get("id"), layer.get("privi"));
                    }
                    if (layer.get("inital") != null) {
                      initalTocs.put((String) layer.get("id"), layer.get("inital"));
                    }
                  }
                }

                /*
                 * 获取模块权限
                 */
                if (resource.containsKey("widgets")) {
                  Map<String, Object> widgets = (Map<String, Object>) resource.get("widgets");
                  List<Map<String, Object>> preWidgets = (List<Map<String, Object>>) widgets
                      .get("preWidgets");
                  List<Map<String, Object>> poolWidgets = (List<Map<String, Object>>) widgets
                      .get("poolWidgets");
                  for (Map<String, Object> preWidget : preWidgets) {
                    if (!preWids.contains(preWidget.get("id"))) {
                      preWids.add((String) preWidget.get("id"));
                    }
                  }
                  for (Map<String, Object> poolWidget : poolWidgets) {
                    if (!poolWids.contains(poolWidget.get("id"))) {
                      poolWids.add((String) poolWidget.get("id"));
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
