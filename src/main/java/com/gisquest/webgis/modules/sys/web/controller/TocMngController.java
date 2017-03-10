package com.gisquest.webgis.modules.sys.web.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.gisquest.webgis.modules.sys.entity.IdentifyFieldObj;
import com.gisquest.webgis.modules.sys.entity.SaveFieldObj;
import com.gisquest.webgis.modules.sys.entity.TocFormCfg;
import com.gisquest.webgis.modules.sys.entity.TocStdCfg;
import com.gisquest.webgis.modules.sys.services.LocalConfigService;
import com.gisquest.webgis.modules.sys.services.ResourceSolutionService;
import com.gisquest.webgis.modules.sys.services.RoleJsonService;
import com.gisquest.webgis.modules.sys.services.TocMngService;
import com.gisquest.webgis.util.Util;
import com.google.gson.GsonBuilder;

/**
 * 图层管理控制器
 * 
 * @author Jisj1
 *
 */

@Controller
public class TocMngController {
  /** 请求体 */
  @Autowired
  private HttpServletRequest request;
  /** 响应体 */
  @Autowired
  private HttpServletResponse response;

  /** 图层管理service对象 */
  private TocMngService mngService;

  /**
   * 构造函数
   */
  public TocMngController() {
    mngService = new TocMngService();
  }

  /**
   * 根据图层名称获取单个Toc
   * 
   * @param label
   *          图层名称
   * @return
   */
  @SuppressWarnings("unchecked")
  @RequestMapping("/tocs/{label}")
  @ResponseBody
  public String getTocByName(@PathVariable String label) {
    Util.G_REQUEST = request;
    String cfgJson = Util.addCorrectFormat(Util.getConfigJson()); // 获取并格式化config.json
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);// 把config.json转为list
    if (cfgJsonList.size() > 0) {
      Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
      if (cfgJsonMap.containsKey("toc")) {
        List<Map<String, Object>> tocList = (List<Map<String, Object>>) cfgJsonMap.get("toc");
        for (Map<String, Object> toc : tocList) {
          if (toc.get("label").equals(label) && toc.containsKey("layerTag")
              && !("maskLayer".equals(toc.get("layerTag")))) {
            String tocJson = new GsonBuilder().setPrettyPrinting().create().toJson(toc);
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
        return "Error: 未找到图层名称为" + label + "的图层！";
      }
    }
    return "Error: 在解析config.json时出现错误！";
  }

  /**
   * 根据图层ID，图层名称，图层标识，图层初始加载状态，图层是否移动端可用来获取一个或多个图层
   * 
   * @param ids
   *          图层ID
   * @param labels
   *          图层名称
   * @param layerTags
   *          图层标识
   * @param initChecked
   *          图层初始加载状态
   * @param mobile
   *          图层是否移动端可用
   * @return
   */
  @SuppressWarnings("unchecked")
  @RequestMapping("/tocs/")
  @ResponseBody
  public String getTocs(String[] ids, String[] labels, String[] layerTags, Boolean initChecked,
      Boolean mobile, String group) {
    Util.G_REQUEST = request;
    String cfgJson = Util.addCorrectFormat(Util.getConfigJson()); // 获取并格式化config.json
    List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
        List.class);// 把config.json转为list
    if (cfgJsonList.size() > 0) {
      Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
      if (cfgJsonMap.containsKey("toc")) {
        List<Map<String, Object>> tocList = (List<Map<String, Object>>) cfgJsonMap.get("toc");
        List<Map<String, Object>> tocWithoutFolder = new ArrayList<>();
        for (Map<String, Object> toc : tocList) {
          // 是图层并且非掩膜图层
          if (toc.containsKey("layerTag") && !("maskLayer".equals(toc.get("layerTag")))) {
            tocWithoutFolder.add(toc);
          }
        }
        List<Map<String, Object>> tempList = tocWithoutFolder;
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (ids != null) {
          // 遍历ids
          for (String id : ids) {
            for (Map<String, Object> toc : tempList) {
              if (id.equals(toc.get("id"))) {
                if (!resultList.contains(toc)) {
                  resultList.add(toc);
                }
              }
            }
          }
          tempList.clear();
          tempList.addAll(resultList);
          resultList.clear();
        }
        if (labels != null) {
          // 遍历labels
          for (String label : labels) {
            for (Map<String, Object> toc : tempList) {
              if (label.equals(toc.get("label"))) {
                if (!resultList.contains(toc)) {
                  resultList.add(toc);
                }
              }
            }
          }
          tempList.clear();
          tempList.addAll(resultList);
          resultList.clear();
        }
        if (layerTags != null) {
          // 遍历layerTags
          for (String layerTag : layerTags) {
            for (Map<String, Object> toc : tempList) {
              if (layerTag.equals(toc.get("layerTag"))) {
                if (!resultList.contains(toc)) {
                  resultList.add(toc);
                }
              }
            }
          }
          tempList.clear();
          tempList.addAll(resultList);
          resultList.clear();
        }
        if (initChecked != null) {
          for (Map<String, Object> toc : tempList) {
            if (initChecked == toc.get("initChecked")) {
              if (!resultList.contains(toc)) {
                resultList.add(toc);
              }
            }
          }
          tempList.clear();
          tempList.addAll(resultList);
          resultList.clear();
        }
        if (mobile != null) {
          for (Map<String, Object> toc : tempList) {
            if (mobile == toc.get("mobile")) {
              if (!resultList.contains(toc)) {
                resultList.add(toc);
              }
            }
          }
          tempList.clear();
          tempList.addAll(resultList);
          resultList.clear();
        }
        if (group != null) {
          for (Map<String, Object> toc : tempList) {
            if (group.equals(toc.get("group"))) {
              if (!resultList.contains(toc)) {
                resultList.add(toc);
              }
            }
          }
          tempList.clear();
          tempList.addAll(resultList);
          resultList.clear();
        }
        String tocJson = new GsonBuilder().setPrettyPrinting().create().toJson(tempList);
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
    return "Error: 在解析config.json时出现错误！";
  }

  /**
   * 提交图层配置信息
   * 
   * @param tocStdConfig
   *          图层标准配置对象
   * @param file
   *          上传文件对象
   * @return "success"
   */
  @SuppressWarnings("unchecked")
  @RequestMapping("/admin/submitTocStdCfg")
  @ResponseBody
  public String submitTocStdCfg(TocStdCfg tocStdConfig,
      @RequestParam(required = false, value = "filename") MultipartFile file) {
    Util.G_REQUEST = request;
    StringBuffer bs = new StringBuffer();
    if (file != null) {
      try {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");// 设置响应体的编码格式
        if (!file.isEmpty()) {
          /* 设置图片上传后的路径 */
          String path = "UploadImgs\\";
          /* 获取上传图片的网络路径 */
          String mapPath = request.getSession().getServletContext()
              .getRealPath("/admin/UploadImgs/");
          /* 获取上传文件名 */
          String originalName = file.getOriginalFilename();
          /* 获取文件后缀 */
          String suffix = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
          /* 指定上传文件的名称 */
          String fileName = tocStdConfig.getId() + "_123" + suffix;
          /* 创建要上传的文件 */
          File targetFile = new File(mapPath, fileName);
          /* 上传图片 */
          FileUtils.copyInputStreamToFile(file.getInputStream(), targetFile);
          /* 如果使用zookeeper，则将图片放入zookeeper */
          LocalConfigService lcService = new LocalConfigService();
          String rootPath = request.getSession().getServletContext().getRealPath("/");
          Boolean useZK = lcService.getZKState(rootPath);
          if (useZK) {
            Util.setDataToZK(fileName.getBytes("utf-8"), "/newImgNode");
            Util.setDataToZK(file.getBytes(), "/UploadImgs/" + fileName);
          }
          bs.append(path.replace("\\", "/") + fileName);
          tocStdConfig.setDisThumb(bs.toString());
        }
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    mngService.modifyTocCfg(tocStdConfig);
    if (!tocStdConfig.getType().equals("dynamic") || !tocStdConfig.getGroup().equals("2")) {
      /* 如果图层类型不为矢量，删除资源方案中多余的图层 */
      Util.ROOT_PATH = request.getSession().getServletContext().getRealPath("/");
      String reSolutionJson = new ResourceSolutionService().getReSolution();
      String id = tocStdConfig.getId();
      /* 将json转成list */
      List<Map<String, Object>> reSolutions = new GsonBuilder().create().fromJson(reSolutionJson,
          List.class);
      if (reSolutions != null && reSolutions.size() > 0) {
        for (Map<String, Object> reSolution : reSolutions) {
          List<Map<String, Object>> layers = (List<Map<String, Object>>) reSolution.get("layers");
          if (layers != null) {
            for (int i = layers.size() - 1; i >= 0; i--) {
              String layerId = (String) layers.get(i).get("id");
              if (layerId.equals(id)) {
                layers.get(i).put("privi", "");
              } else if (layerId.equals(id + "_del")) {
                layers.remove(i);
              } else if (layerId.equals(id + "_save")) {
                layers.remove(i);
              }
            }
          }
        }
      }
    }
    /* 修改role.json相应的图层类型 */
    new RoleJsonService().modifyTocNode(tocStdConfig.getId(), tocStdConfig.getType(),
        tocStdConfig.getGroup());
    /* 设置图层的初始加载状态 */
    setInitChecked(tocStdConfig.getId(), tocStdConfig.isInitLoad());
    return "success";
  }

  /**
   * 提交图层表单对象
   * 
   * @param tocFormCfg
   *          图层表单配置对象
   * @return "success"
   */
  @RequestMapping(value = "/admin/submitTocFormCfg", method = RequestMethod.POST)
  @ResponseBody
  public String submitTocFormCfg(TocFormCfg tocFormCfg) {
    Util.G_REQUEST = request;
    response.setContentType("text/plain");
    mngService.modifyTocCfg(tocFormCfg);
    return "success";
  }

  /**
   * 修改图层目录名称
   * 
   * @param id
   *          图层目录的id
   * @param label
   *          修改后的图层目录名称
   * @return "success"
   */
  @RequestMapping(value = "/admin/submitTocCtn", method = RequestMethod.POST)
  @ResponseBody
  public String submitTocCtn(String id, String label) {
    Util.G_REQUEST = request;
    response.setContentType("text/plain");
    mngService.modifyTocCtn(id, label);
    return "success";
  }

  /**
   * 删除图层节点
   * 
   * @param id
   *          要删除的图层ID
   * @return "success"
   */
  @RequestMapping("/admin/deleteToc")
  @ResponseBody
  public String deleteToc(String id) {
    Util.G_REQUEST = request;
    response.setContentType("text/plain");
    mngService.deleteTocNode(id);
    return "success";
  }

  /**
   * 添加目录结点
   * 
   * @param parentId
   *          父结点ID
   * @param id
   *          目录结点ID
   * @param label
   *          目录标签
   * @param showIndex
   *          显示索引
   * @return "success"
   */
  @RequestMapping("/admin/addTocCtn")
  @ResponseBody
  public String addTocCtn(String parentId, String id, String label, String showIndex,
      boolean initLoad) {
    Util.G_REQUEST = request;
    response.setContentType("text/plain");
    mngService.addTocCtnNode(parentId, id, label, showIndex, initLoad);
    return "success";
  }

  /**
   * 新建图层
   * 
   * @param tocStdConfig
   *          图层标准配置对象
   * @param file
   *          上传的文件对象
   * @return "success"
   */
  @RequestMapping("/admin/addToc")
  @ResponseBody
  public String addToc(TocStdCfg tocStdConfig,
      @RequestParam(required = false, value = "filename") MultipartFile file) {
    Util.G_REQUEST = request;
    StringBuffer bs = new StringBuffer();
    /* 判断是否上传文件 */
    if (file != null) {
      try {
        /* 设置请求体的编码格式 */
        request.setCharacterEncoding("utf-8");
        /* 设置响应体的编码格式 */
        response.setCharacterEncoding("utf-8");
        if (!file.isEmpty()) {
          /* 设置图片上传后的路径 */
          String path = "UploadImgs\\";
          /* 获取上传图片的网络路径 */
          String mapPath = request.getSession().getServletContext()
              .getRealPath("/admin/UploadImgs/");
          /* 获取上传文件名 */
          String originalName = file.getOriginalFilename();
          /* 获取文件后缀 */
          String suffix = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
          /* 指定上传文件的名称 */
          String fileName = tocStdConfig.getId() + "_123" + suffix;
          /* 创建要上传的文件 */
          File targetFile = new File(mapPath, fileName);
          /* 判断要上传的文件是否存在,如果不存在,则上传 */
          if (!targetFile.exists()) {
            /* 上传图片 */
            FileUtils.copyInputStreamToFile(file.getInputStream(), targetFile);
            /* 如果使用zookeeper，则将图片放入zookeeper */
            LocalConfigService lcService = new LocalConfigService();
            String rootPath = request.getSession().getServletContext().getRealPath("/");
            Boolean useZK = lcService.getZKState(rootPath);
            if (useZK) {
              Util.setDataToZK(file.getBytes(), "/UploadImgs/" + fileName);
            }
          }
          bs.append(path.replace("\\", "/") + fileName);
          tocStdConfig.setDisThumb(bs.toString());
        }
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else {
      /* 如果没有上传的文件,则复制图片 */
      String mapPath = request.getSession().getServletContext().getRealPath("/admin/");
      String uri = tocStdConfig.getDisThumb();
      if (!uri.equals("")) {
        File srcFile = new File(mapPath, uri);
        String suffix = uri.substring(uri.lastIndexOf(".")).toLowerCase();
        File destFile = new File(mapPath + "/UploadImgs/", tocStdConfig.getId() + "_123" + suffix);
        try {
          if (srcFile.exists()) {
            FileUtils.copyFile(srcFile, destFile);
          }
          /* 如果使用zookeeper，则将图片放入zookeeper */
          LocalConfigService lcService = new LocalConfigService();
          String rootPath = request.getSession().getServletContext().getRealPath("/");
          Boolean useZK = lcService.getZKState(rootPath);
          if (useZK) {
            Util.setDataToZK(FileUtils.readFileToByteArray(srcFile),
                "/UploadImgs/" + destFile.getName());
          }
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
        tocStdConfig.setDisThumb("UploadImgs/" + destFile.getName());
      } else {
        tocStdConfig.setDisThumb("");
      }
    }
    mngService.addTocNode(tocStdConfig);
    return "success";
  }

  /**
   * 改变所属父级
   * 
   * @param id
   *          要更换的子节点ID
   * @param parentId
   *          更换后的父节点ID
   * @return
   */
  @RequestMapping("/admin/changeParent")
  @ResponseBody
  public String changeParent(String id, String parentId) {
    Util.G_REQUEST = request;
    response.setContentType("text/plain");
    mngService.changeParentNode(id, parentId);
    return "success";
  }

  /**
   * 改变树节点showIndex
   * 
   * @return
   */
  @RequestMapping("/admin/changeShowIndex")
  @ResponseBody
  public String changeShowIndex() {
    Util.G_REQUEST = request;
    String[] ids = request.getParameterValues("ids[]");
    String[] showIndexs = request.getParameterValues("showIndexs[]");
    response.setContentType("text/plain");
    mngService.changeShowIndex(ids, showIndexs);
    return "success";
  }

  /**
   * 改变树节点mapIndex
   * 
   * @return
   */
  @RequestMapping("/admin/changeMapIndex")
  @ResponseBody
  public String changeMapIndex() {
    Util.G_REQUEST = request;
    String[] ids = request.getParameterValues("ids[]");
    String[] mapIndexs = request.getParameterValues("mapIndexs[]");
    response.setContentType("text/plain");
    mngService.changeMapIndex(ids, mapIndexs);
    return "success";
  }

  /**
   * 设置初始加载
   * 
   * @param id
   *          图层id
   * @param initLoad
   *          初始加载状态
   */
  @RequestMapping("/admin/setInitChecked")
  @ResponseBody
  public void setInitChecked(String id, boolean initLoad) {
    Util.G_REQUEST = request;
    mngService.setInitChecked(id, initLoad);
  }

  /**
   * 设置查询字段
   * 
   * @param identifyField
   */
  @RequestMapping("/admin/setIdentifyField")
  @ResponseBody
  public void setIdentifyField(@RequestBody IdentifyFieldObj identifyObject) {
    Util.G_REQUEST = request;
    mngService.setIdentifyField(identifyObject);
  }

  @RequestMapping("/admin/setSaveField")
  @ResponseBody
  public void setSaveField(@RequestBody SaveFieldObj saveFieldObj) {
    Util.G_REQUEST = request;
    mngService.setSaveField(saveFieldObj);
  }
}