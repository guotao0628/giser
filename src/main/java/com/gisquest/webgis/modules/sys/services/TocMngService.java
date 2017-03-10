package com.gisquest.webgis.modules.sys.services;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.gisquest.webgis.modules.sys.entity.IdentifyFieldObj;
import com.gisquest.webgis.modules.sys.entity.SaveFieldObj;
import com.gisquest.webgis.modules.sys.entity.TocFormCfg;
import com.gisquest.webgis.modules.sys.entity.TocStdCfg;
import com.gisquest.webgis.util.Util;
import com.google.gson.GsonBuilder;

/**
 * 图层管理服务
 * 
 * @author Jisj1
 *
 */
public class TocMngService {
  /**
   * 修改图层标准配置
   * 
   * @param tocStdConfig
   *          图层标准配置对象
   */
  @SuppressWarnings("unchecked")
  public void modifyTocCfg(TocStdCfg tocStdConfig) {
    String cfgJson = Util.getConfigJson(); // 获取config.json
    cfgJson = Util.addCorrectFormat(cfgJson); // 格式化config.json
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);// 把config.json转为list
    boolean isTocFound = false;
    if (cfgJsonList.size() > 0) {
      // 1.遍历cfgJsonList中的每一个Map
      for (Map<String, Object> cfgJsonMap : cfgJsonList) {
        // 2.遍历cfgJsonMap中的每一个key
        for (String key : cfgJsonMap.keySet()) {
          if (key.equals("toc")) {
            // 3.若key的名称为toc，则获取toc的value并存放在集合tocs中
            List<Map<String, Object>> tocs = (List<Map<String, Object>>) cfgJsonMap.get(key);
            // 4.遍历tocs中的每一个toc
            for (Map<String, Object> toc : tocs) {
              if (toc.get("id").equals(tocStdConfig.getId())) {
                isTocFound = true; // 找到了操作的图层id
                // 5. 若toc的id为操作的图层id，则向config.json存入信息
                toc.put("label", tocStdConfig.getLabel());
                toc.put("layerTag", tocStdConfig.getTag());
                toc.put("type", tocStdConfig.getType());
                toc.put("geometryType", tocStdConfig.getGeotype());
                toc.put("topologyCheck", tocStdConfig.getTopology());
                toc.put("url", tocStdConfig.getUrl());
                toc.put("visibleLayers", tocStdConfig.getVisIds());
                toc.put("mapIndex", tocStdConfig.getMapIndex());
                toc.put("isSwitch", tocStdConfig.isEnableSwitch());
                toc.put("isShortcut", tocStdConfig.isShortcut());
                toc.put("layerTable", tocStdConfig.getLayerTable());
                toc.put("group", tocStdConfig.getGroup());
                toc.put("thumbUrl", tocStdConfig.getDisThumb());
                toc.put("mobile", tocStdConfig.isMobileAvailable());
                toc.put("showInfoWindow", tocStdConfig.isShowInfoWindow());
                toc.put("isFilter", tocStdConfig.isFilter());
                toc.put("selectLayer", tocStdConfig.getSelectLayer());
                break;
              }
            }
          }
        }
        // 6.若toc已找到并存储完成，则跳出循环
        if (isTocFound) {
          break;
        }
      }
    }
    // 7.再jsonList转成String, 保存到role.json
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    String data = dataJson.substring(1, (dataJson.toString().length() - 1));
    Util.writeJson(data);

  }

  /**
   * 设置子节点状态时，递归设置其所有父节点的状态
   * 
   * @param parentId
   *          父节点的id
   * @param tocs
   *          图层目录列表
   * @param Checked
   *          子节点的状态
   * @return
   */
  private List<Map<String, Object>> setChildNodeChecked(String parentId,
      List<Map<String, Object>> tocs, boolean checked) {
    /* 如果为初始加载，则将其所有父节点也设为初始加载 */
    if (checked) {
      /* 遍历toc项 */
      for (Map<String, Object> map : tocs) {
        if (map.get("id").equals(parentId)) {
          map.put("initChecked", checked);
          /* 如果当前节点有父节点 */
          if (map.containsKey("parent")) {
            /* 递归 */
            tocs = setChildNodeChecked((String) map.get("parent"), tocs, checked);
          }
        }
      }
    } else {
      boolean isAllFalse = true;
      /* 遍历toc项 */
      for (Map<String, Object> map : tocs) {
        /* 节点存在父节点，并且父节点id为指定的id，同时节点存在初始加载字段，并且初始加载为true */
        if (map.get("parent") != null && map.get("parent").equals(parentId)
            && map.get("initChecked") != null && (boolean) map.get("initChecked") == true) {
          isAllFalse = false;
          break;
        }
      }
      if (isAllFalse) {
        /* 遍历toc项 */
        for (Map<String, Object> map : tocs) {
          if (map.get("id").equals(parentId)) {
            map.put("initChecked", checked);
            if (map.containsKey("parent")) {
              /* 递归 */
              tocs = setChildNodeChecked((String) map.get("parent"), tocs, checked);
            }
            break;
          }
        }
      }
    }
    return tocs;
  }

  /**
   * 修改图层表单配置
   * 
   * @param tocFormCfg
   *          图层表单配置对象
   */
  @SuppressWarnings("unchecked")
  public void modifyTocCfg(TocFormCfg tocFormCfg) {
    String cfgJson = Util.getConfigJson(); // 获取config.json
    cfgJson = Util.addCorrectFormat(cfgJson); // 格式化config.json
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);// 把config.json转为list
    boolean isTocFound = false;
    if (cfgJsonList.size() > 0) {
      // 1.遍历cfgJsonList中的每一个Map
      for (Map<String, Object> cfgJsonMap : cfgJsonList) {
        // 2.遍历cfgJsonMap中的每一个key
        for (String key : cfgJsonMap.keySet()) {
          if (key.equals("toc")) {
            // 3.若key的名称为toc，则获取toc的value并存放在集合tocs中
            List<Map<String, Object>> tocs = (List<Map<String, Object>>) cfgJsonMap.get(key);
            // 4.遍历tocs中的每一个toc
            for (Map<String, Object> toc : tocs) {
              if (toc.get("id").equals(tocFormCfg.getId())) {
                isTocFound = true; // 找到了操作的图层id
                // 5. 若toc的id为操作的图层id，则向config.json存入信息
                toc.put("keyWord", tocFormCfg.getKeywords());
                toc.put("keyMap", tocFormCfg.getKeyMaps());
                toc.put("customUrl", tocFormCfg.getReportUrl());
                break;
              }
            }
            break;
          }
        }
        // 若toc已找到并存储完成，则跳出循环
        if (isTocFound) {
          break;
        }
      }
    }
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    String data = dataJson.substring(1, (dataJson.toString().length() - 1));
    Util.writeJson(data);
  }

  /**
   * 修改图层目录的名称
   * 
   * @param id
   *          图层目录的id
   * @param label
   *          修改后的图层目录名
   */
  @SuppressWarnings("unchecked")
  public void modifyTocCtn(String id, String label) {
    String cfgJson = Util.getConfigJson(); // 获取config.json
    cfgJson = Util.addCorrectFormat(cfgJson); // 格式化config.json
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);// 把config.json转为list
    boolean isTocFound = false;
    if (cfgJsonList.size() > 0) {
      // 1.遍历cfgJsonList中的每一个Map
      for (Map<String, Object> cfgJsonMap : cfgJsonList) {
        // 2.遍历cfgJsonMap中的每一个key
        for (String key : cfgJsonMap.keySet()) {
          if (key.equals("toc")) {
            // 3.若key的名称为toc，则获取toc的value并存放在集合tocs中
            List<Map<String, Object>> tocs = (List<Map<String, Object>>) cfgJsonMap.get(key);
            // 4.遍历tocs中的每一个toc
            for (Map<String, Object> toc : tocs) {
              if (toc.get("id").equals(id)) {
                isTocFound = true; // 找到了操作的图层id
                // 5. 若toc的id为操作的图层id，则向config.json存入label信息
                toc.put("label", label);
                break;
              }
            }
            break;
          }
        }
        // 6.若toc已找到并存储完成，则跳出循环
        if (isTocFound) {
          break;
        }
      }
    }
    // 7.再jsonList转成String, 保存到role.json
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    String data = dataJson.substring(1, (dataJson.toString().length() - 1));
    Util.writeJson(data);
  }

  /**
   * 递归设置某一节点下所有子节点的初始加载状态
   * 
   * @param id
   *          某一节点的id
   * @param initLoad
   *          初始加载状态
   * @param tocs
   *          图层集合
   * @return
   */
  private List<Map<String, Object>> setParentNodeChecked(String id, boolean initLoad,
      List<Map<String, Object>> tocs) {
    /* 如果父节点改变了状态，则其子节点全部改为其父节点的状态 */
    for (Map<String, Object> map : tocs) {
      /* 遍历tocs，查看各toc项的parent是否为改父节点id */
      if (id != null && id.equals(map.get("parent"))) {
        map.put("initChecked", initLoad);
        tocs = setParentNodeChecked((String) (map.get("id")), initLoad, tocs);
      }
    }
    return tocs;
  }

  /**
   * 删除指定的图层
   * 
   * @param id
   *          要删除的图层id
   */
  @SuppressWarnings("unchecked")
  public void deleteTocNode(String id) {
    /* 删除节点前，将图层的初始加载状态设为false */
    setInitChecked(id, false);
    String cfgJson = Util.getConfigJson(); // 获取config.json
    cfgJson = Util.addCorrectFormat(cfgJson); // 格式化config.json
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);// 把config.json转为list
    boolean isTocFound = false;
    if (cfgJsonList.size() > 0) {
      // 1.遍历cfgJsonList中的每一个Map
      for (Map<String, Object> cfgJsonMap : cfgJsonList) {
        // 2.遍历cfgJsonMap中的每一个key
        for (String key : cfgJsonMap.keySet()) {
          if (key.equals("toc")) {
            // 3.若key的名称为toc，则获取toc的value并存放在集合tocs中
            List<Map<String, Object>> tocs = (List<Map<String, Object>>) cfgJsonMap.get(key);
            // 4.遍历tocs中的每一个toc
            for (Map<String, Object> toc : tocs) {
              if (toc.get("id").equals(id)) {
                isTocFound = true; // 找到了操作的图层id
                // 5. 若toc的id为操作的图层id，则从tocs中删除该toc
                tocs.remove(toc);
                // 6. 若toc存在子节点，则删除其所有子节点
                removeChild(tocs, id);
                // 7. 删除缩略图
                String nowPath = Util.G_REQUEST.getSession().getServletContext().getRealPath("/");// 获取请求体的资源路径
                File imgFile = new File(nowPath + "/admin/" + toc.get("thumbUrl"));
                if (imgFile.exists()) {
                  if (!imgFile.getName().equals("toc_default.png")) {
                    imgFile.delete();
                    /* 如果使用zookeeper，则删除zookeeper中的图片结点 */
                    LocalConfigService lcService = new LocalConfigService();
                    Boolean useZK = lcService.getZKState(nowPath);
                    if (useZK) {
                      Util.deleteDataFromZK("/UploadImgs/" + imgFile.getName());
                    }
                  }
                }
                break;
              }
            }
          }
          if (key.equals("reSolution")) {
            List<Map<String, Object>> reSolutions = (List<Map<String, Object>>) cfgJsonMap.get(key);
            for (Map<String, Object> reSolution : reSolutions) {
              List<Map<String, Object>> layers = (List<Map<String, Object>>) reSolution
                  .get("layers");
              for (int i = layers.size() - 1; i >= 0; i--) {
                String layerId = (String) (layers.get(i).get("id"));
                if (layerId.contains(id)) {
                  // 若含有指定id，则删除
                  layers.remove(i);
                }
              }
            }
          }
        }
        // 7.若toc已找到并存储完成，则跳出循环
        if (isTocFound) {
          break;
        }
      }
    }
    // 7.再jsonList转成String, 保存到role.json
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    String data = dataJson.substring(1, (dataJson.toString().length() - 1));
    Util.writeJson(data);
    // 删除role.json多余的toc（若存在）
    new RoleJsonService().deleteTocNode(id);
  }

  /**
   * 添加Toc目录节点
   * 
   * @param parentId
   *          父结点ID
   * @param id
   *          目录节点id
   * @param label
   *          目录标签
   * @param showIndex
   *          显示索引
   */
  @SuppressWarnings("unchecked")
  public void addTocCtnNode(String parentId, String id, String label, String showIndex,
      boolean initLoad) {
    String cfgJson = Util.getConfigJson(); // 获取config.json
    cfgJson = Util.addCorrectFormat(cfgJson); // 格式化config.json
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);// 把config.json转为list
    if (cfgJsonList.size() > 0) {
      // 1.遍历cfgJsonList中的每一个Map
      for (Map<String, Object> cfgJsonMap : cfgJsonList) {
        // 2.遍历cfgJsonMap中的每一个key
        for (String key : cfgJsonMap.keySet()) {
          if (key.equals("toc")) {
            // 3.若key的名称为toc，则获取toc的value并存放在集合tocs中
            List<Map<String, Object>> tocs = (List<Map<String, Object>>) cfgJsonMap.get(key);
            // 4.在tocs中添加新的toc
            Map<String, Object> newTocCtn = new LinkedHashMap<>();
            newTocCtn.put("id", id);
            newTocCtn.put("label", label);
            newTocCtn.put("parent", parentId);
            newTocCtn.put("showIndex", showIndex);
            newTocCtn.put("initChecked", initLoad);
            tocs.add(newTocCtn);
            break;
          }
        }
      }
    }
    // 5.再jsonList转成String, 保存到role.json
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    String data = dataJson.substring(1, (dataJson.toString().length() - 1));
    Util.writeJson(data);
  }

  /**
   * 新建图层
   * 
   * @param tocStdConfig
   *          新建图层标准配置
   */
  @SuppressWarnings("unchecked")
  public void addTocNode(TocStdCfg tocStdConfig) {
    String cfgJson = Util.getConfigJson();
    cfgJson = Util.addCorrectFormat(cfgJson);
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);
    if (cfgJsonList.size() > 0) {
      // 1.遍历cfgJsonList中的每一个Map
      for (Map<String, Object> cfgJsonMap : cfgJsonList) {
        // 2.遍历cfgJsonMap中的每一个key
        for (String key : cfgJsonMap.keySet()) {
          if (key.equals("toc")) {
            // 3.若key的名称为toc，则获取toc的value并存放在集合tocs中
            List<Map<String, Object>> tocs = (List<Map<String, Object>>) cfgJsonMap.get(key);
            Map<String, Object> newToc = new LinkedHashMap<>();
            newToc.put("id", tocStdConfig.getId());
            newToc.put("showIndex", tocStdConfig.getShowIndex());
            newToc.put("parent", tocStdConfig.getParentId());
            newToc.put("label", tocStdConfig.getLabel());
            newToc.put("layerTag", tocStdConfig.getTag());
            newToc.put("type", tocStdConfig.getType());
            newToc.put("geometryType", tocStdConfig.getGeotype());
            newToc.put("topologyCheck", tocStdConfig.getTopology());
            newToc.put("url", tocStdConfig.getUrl());
            newToc.put("visibleLayers", tocStdConfig.getVisIds());
            newToc.put("mapIndex", tocStdConfig.getMapIndex());
            newToc.put("initChecked", tocStdConfig.isInitLoad());
            newToc.put("isShortcut", tocStdConfig.isShortcut());
            newToc.put("isSwitch", tocStdConfig.isEnableSwitch());
            newToc.put("layerTable", tocStdConfig.getLayerTable());
            newToc.put("group", tocStdConfig.getGroup());
            newToc.put("thumbUrl", tocStdConfig.getDisThumb());
            newToc.put("mobile", tocStdConfig.isMobileAvailable());
            newToc.put("showInfoWindow", tocStdConfig.isShowInfoWindow());
            newToc.put("isFilter", tocStdConfig.isFilter());
            newToc.put("selectLayer", tocStdConfig.getSelectLayer());
            tocs.add(newToc);
          }
        }
      }
    }
    /* 再jsonList转成String, 保存到role.json */
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    String data = dataJson.substring(1, (dataJson.toString().length() - 1));
    Util.writeJson(data);
    /* 设置图层初始加载状态 */
    setInitChecked(tocStdConfig.getId(), tocStdConfig.isInitLoad());
  }

  /**
   * 改变所属父级
   * 
   * @param id
   *          要更换的子节点ID
   * @param parentId
   *          更换后的父节点ID
   * 
   */
  @SuppressWarnings("unchecked")
  public void changeParentNode(String id, String parentId) {
    String cfgJson = Util.getConfigJson(); // 获取config.json
    cfgJson = Util.addCorrectFormat(cfgJson); // 格式化config.json
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);// 把config.json转为list
    boolean isTocFound = false;
    boolean initChecked = false;
    if (cfgJsonList.size() > 0) {
      // 1.遍历cfgJsonList中的每一个Map
      for (Map<String, Object> cfgJsonMap : cfgJsonList) {
        // 2.遍历cfgJsonMap中的每一个key
        for (String key : cfgJsonMap.keySet()) {
          if (key.equals("toc")) {
            // 3.若key的名称为toc，则获取toc的value并存放在集合tocs中
            List<Map<String, Object>> tocs = (List<Map<String, Object>>) cfgJsonMap.get(key);
            // 4.遍历tocs中的每一个toc
            for (Map<String, Object> toc : tocs) {
              if (toc.get("id").equals(id)) {
                isTocFound = true; // 找到了操作的图层id
                /* 获取图层节点的初始加载状态 */
                initChecked = (boolean) toc.get("initChecked");
                /* 移动节点之前，将当前节点的初始加载状态设为false */
                toc.put("initChecked", false);
                tocs = setChildNodeChecked((String) toc.get("parent"), tocs, false);
                /* 若toc的id为操作的图层id，则改变parent值 */
                toc.put("parent", parentId);
                /* 将该节点的初始加载状态还原 */
                toc.put("initChecked", initChecked);
                tocs = setChildNodeChecked(parentId, tocs, initChecked);
                break;
              }
            }
            break;
          }
        }
        // 6.若toc已找到并存储完成，则跳出循环
        if (isTocFound) {
          break;
        }
      }
    }
    // 7.再jsonList转成String, 保存到role.json
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    String data = dataJson.substring(1, (dataJson.toString().length() - 1));
    Util.writeJson(data);
  }

  /**
   * 改变树节点showIndex
   * 
   * @param ids
   *          树各节点的id
   * @param showIndexs
   *          树各节点新的showIndex
   */
  @SuppressWarnings("unchecked")
  public void changeShowIndex(String[] ids, String[] showIndexs) {
    String cfgJson = Util.getConfigJson(); // 获取config.json
    cfgJson = Util.addCorrectFormat(cfgJson); // 格式化config.json
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);// 把config.json转为list
    if (cfgJsonList.size() > 0) {
      // 1.遍历cfgJsonList中的每一个Map
      for (Map<String, Object> cfgJsonMap : cfgJsonList) {
        // 2.遍历cfgJsonMap中的每一个key
        for (String key : cfgJsonMap.keySet()) {
          if (key.equals("toc")) {
            // 3.若key的名称为toc，则获取toc的value并存放在集合tocs中
            List<Map<String, Object>> tocs = (List<Map<String, Object>>) cfgJsonMap.get(key);
            // 4.遍历tocs中的每一个toc
            for (int i = 0; i < ids.length; i++) {
              for (Map<String, Object> toc : tocs) {
                // 若该id与遍历到的toc的id一致，则修改当前toc的showIndex
                if (ids[i].equals(toc.get("id"))) {
                  toc.put("showIndex", Integer.parseInt(showIndexs[i]));
                  break;
                }
              }
            }
            break;
          }
        }
      }
    }
    // 7.再jsonList转成String, 保存到role.json
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    String data = dataJson.substring(1, (dataJson.toString().length() - 1));
    Util.writeJson(data);
  }

  /**
   * 改变树节点mapIndex
   * 
   * @param ids
   *          树各节点的id
   * @param mapIndexs
   *          树各节点新的mapIndex
   */
  @SuppressWarnings("unchecked")
  public void changeMapIndex(String[] ids, String[] mapIndexs) {
    String cfgJson = Util.getConfigJson(); // 获取config.json
    cfgJson = Util.addCorrectFormat(cfgJson); // 格式化config.json
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);// 把config.json转为list
    if (cfgJsonList.size() > 0) {
      // 1.遍历cfgJsonList中的每一个Map
      for (Map<String, Object> cfgJsonMap : cfgJsonList) {
        // 2.遍历cfgJsonMap中的每一个key
        for (String key : cfgJsonMap.keySet()) {
          if (key.equals("toc")) {
            // 3.若key的名称为toc，则获取toc的value并存放在集合tocs中
            List<Map<String, Object>> tocs = (List<Map<String, Object>>) cfgJsonMap.get(key);
            // 4.遍历tocs中的每一个toc
            for (int i = 0; i < ids.length; i++) {
              for (Map<String, Object> toc : tocs) {
                // 若该id与遍历到的toc的id一致，则修改当前toc的mapIndexs
                if (ids[i].equals(toc.get("id"))) {
                  toc.put("mapIndex", Integer.parseInt(mapIndexs[i]));
                  break;
                }
              }
            }
            break;
          }
        }
      }
    }
    // 7.再jsonList转成String, 保存到role.json
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    String data = dataJson.substring(1, (dataJson.toString().length() - 1));
    Util.writeJson(data);
  }

  /**
   * 移除包含parent的元素
   * 
   * @param toc
   *          图层列表
   * @param pId
   *          图层id
   * @return
   */
  private static List<Map<String, Object>> removeChild(List<Map<String, Object>> toc, String pId) {
    List<Map<String, Object>> tocsToDelete = new ArrayList<>();
    /* 遍历toc */
    for (Map<String, Object> map : toc) {
      /* 获取parent值 */
      String parentId = (String) map.get("parent");
      if (pId.equals(parentId)) {
        tocsToDelete.add(map);
      }
    }
    for (Map<String, Object> map : tocsToDelete) {
      toc.remove(map);
      removeChild(toc, (String) map.get("id"));
    }
    return toc;
  }

  /**
   * 设置初始加载
   * 
   * @param id
   *          图层id
   * @param initLoad
   *          初始加载状态
   */
  @SuppressWarnings("unchecked")
  public void setInitChecked(String id, boolean initLoad) {
    /* 获取并格式化config.json */
    String cfgJson = Util.getConfigJson();
    cfgJson = Util.addCorrectFormat(cfgJson);
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);
    if (cfgJsonList.size() > 0) {
      /* 获取cfgJsonMap */
      Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
      if (cfgJsonMap.containsKey("toc")) {
        /* 定位到toc节点 */
        List<Map<String, Object>> tocList = (List<Map<String, Object>>) cfgJsonMap.get("toc");
        /* 遍历toc */
        for (Map<String, Object> map : tocList) {
          if (map.get("id").equals(id)) {
            /* 设置图层的初始加载状态 */
            map.put("initChecked", initLoad);
            /* 设置其所有子节点的初始加载状态 */
            tocList = setParentNodeChecked(id, initLoad, tocList);
            /* 如果当前操作节点存在父节点 */
            if (map.containsKey("parent")) {
              /* 设置其所有父节点的初始加载状态 */
              String parentId = (String) map.get("parent");
              tocList = setChildNodeChecked(parentId, tocList, initLoad);
            }
            break;
          }
        }
      }
    }
    /* 再jsonList转成String, 保存到config.json */
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    String data = dataJson.substring(1, (dataJson.toString().length() - 1));
    Util.writeJson(data);
  }

  /**
   * 设置查询字段
   * 
   * @param identifyField
   */
  @SuppressWarnings("unchecked")
  public void setIdentifyField(IdentifyFieldObj identifyObject) {
    /* 获取并格式化config.json */
    String cfgJson = Util.getConfigJson();
    cfgJson = Util.addCorrectFormat(cfgJson);
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);
    if (cfgJsonList.size() > 0) {
      /* 获取cfgJsonMap */
      Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
      if (cfgJsonMap.containsKey("toc")) {
        /* 定位到toc节点 */
        List<Map<String, Object>> tocList = (List<Map<String, Object>>) cfgJsonMap.get("toc");
        /* 遍历toc */
        for (Map<String, Object> map : tocList) {
          if (map.get("id").equals(identifyObject.getId())) {
            map.put("identifyField", identifyObject.getIdentifyField());
            break;
          }
        }
      }
    }
    /* 再jsonList转成String, 保存到config.json */
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    String data = dataJson.substring(1, (dataJson.toString().length() - 1));
    Util.writeJson(data);
  }

  @SuppressWarnings("unchecked")
  public void setSaveField(SaveFieldObj saveFieldObj) {
    /* 获取并格式化config.json */
    String cfgJson = Util.getConfigJson();
    cfgJson = Util.addCorrectFormat(cfgJson);
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);
    if (cfgJsonList.size() > 0) {
      /* 获取cfgJsonMap */
      Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
      if (cfgJsonMap.containsKey("toc")) {
        /* 定位到toc节点 */
        List<Map<String, Object>> tocList = (List<Map<String, Object>>) cfgJsonMap.get("toc");
        /* 遍历toc */
        for (Map<String, Object> map : tocList) {
          if (map.get("id").equals(saveFieldObj.getId())) {
            if ("dynamic".equals(map.get("type")) || "feature".equals(map.get("type"))) {
              map.put("saveField", saveFieldObj.getSaveFields());
              break;
            }
          }
        }
      }
    }
    /* 再jsonList转成String, 保存到config.json */
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(cfgJsonList);
    String data = dataJson.substring(1, (dataJson.toString().length() - 1));
    Util.writeJson(data);
  }
}
