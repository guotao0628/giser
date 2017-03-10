package com.gisquest.webgis.modules.sys.web.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.gisquest.platform.common.utils.FileUtils;
import com.gisquest.platform.common.utils.PropertiesLoader;
import com.gisquest.webgis.modules.sys.entity.LoginRule;
import com.gisquest.webgis.modules.sys.services.BasicConfigService;
import com.gisquest.webgis.modules.sys.services.LocalConfigService;
import com.gisquest.webgis.modules.sys.services.ZooKeeperFactory;
import com.gisquest.webgis.util.Util;
import com.google.gson.GsonBuilder;

/**
 * 基础配置Controller
 * 
 * @author Jisj1
 *
 */
@Controller
public class BasicConfigController {
    /** 请求体 */
    @Autowired
    private HttpServletRequest request;
    /** 响应体 */
    @Autowired
    private HttpServletResponse response;
    /** 基础配置service对象 */
    @Autowired
    private BasicConfigService basicConfigService;

    /**
     * 新增数据之后，从这个服务中读取数据
     * 
     * @return data config.json中的所有数据
     */
    @RequestMapping("/admin/readconfig")
    @ResponseBody
    public String readConfig() {
        Util.G_REQUEST = request;
        return Util.getConfigJson();
    }

    /**
     * 获取config.json中的某一项
     * 
     * @param type
     *            config.json中的某一项
     * @param expression
     *            查询语句
     * @return 结果集json字符串
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/configJson")
    @ResponseBody
    public String getCfgJson(String type, String expression) {
        String resultJson = "[]";
        String cfgJson = Util.addCorrectFormat(readConfig());
        List<Map<String, Object>> cfgJsonList = new GsonBuilder().create().fromJson(cfgJson,
                List.class);// 把config.json转为list
        if (cfgJsonList.size() > 0) {
            Map<String, Object> cfgJsonMap = cfgJsonList.get(0);
            switch (type) {
            case "toc":
                List<Map<String, Object>> tocList = (List<Map<String, Object>>) cfgJsonMap
                        .get("toc");
                // 初始化要返回的数据
                List<Map<String, Object>> tempTocList = new ArrayList<>();
                for (Map<String, Object> toc : tocList) {
                    if (toc.containsKey("layerTag")) {
                        tempTocList.add(toc);
                    }
                }
                // 支持id[]，layerTag[], initChecked以及mobile四个字段的查询
                // id=1,2,3&layerTag=1,2,3&initChecked=true&mobile=true
                // 将expression以$为分隔符分隔
                if (expression != null) {
                    String[] params = expression.split("$");
                    for (String param : params) {
                        List<Map<String, Object>> results = new ArrayList<>();
                        if (param != null) {
                            // 将param以=为分隔符分隔
                            String key = param.split("=")[0];
                            String value = "";
                            if (param.split("=").length > 1) {
                                value = param.split("=")[1];
                            }
                            // 遍历tocList
                            for (Map<String, Object> toc : tempTocList) {
                                if (toc.containsKey(key)) {
                                    if (key.equals("initChecked") || key.equals("mobile")) {
                                        if (toc.get(key).toString().equals(value)) {
                                            results.add(toc);
                                        }
                                    } else if (key.equals("id") || key.equals("layerTag")) {
                                        // 如果是id或者layerTag，则将value值以,为分隔符分隔
                                        if (value != null) {
                                            String[] keyValues = value.split(",");
                                            for (String keyValue : keyValues) {
                                                if (toc.get(key).equals(keyValue)) {
                                                    results.add(toc);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        // 将要遍历的集合变为结果，即在结果中再次过滤
                        tempTocList = results;
                    }
                }
                resultJson = new GsonBuilder().setPrettyPrinting().create().toJson(tempTocList);
                break;
            case "widget":
                break;
            default:
                resultJson = "Error：type属性为空或者不支持的type类型。";
                break;
            }
            System.out.println(resultJson);
            /* 对最终的resultJson做处理，将其中用{}包含的变量替换成参数池中的参数 */
            // 获取参数池中的参数
            List<Map<String, Object>> ipPool = (List<Map<String, Object>>) cfgJsonMap.get("IpPool");
            for (Map<String, Object> ip : ipPool) {
                String ipName = (String) ip.get("name");
                String ipValue = (String) ip.get("ip");
                resultJson = resultJson.replace("{" + ipName + "}", ipValue);
            }
            System.out.println(resultJson);
        }
        return resultJson;
    }

    /**
     * 保存电子政务信息
     * 
     * @param version
     *            电子政务版本
     * @param url
     *            电子政务地址
     * @param user
     *            用户名
     * @param pwd
     *            密码
     */
    @RequestMapping("/admin/saveDzzwInfo")
    @ResponseBody
    public void saveDzzwInfo(String version, String url, String user, String pwd) {
        Util.G_REQUEST = request;
        // 保存电子政务版本
        basicConfigService.saveDzzwVersion(version);
        OutputStream fos = null;
        PropertiesLoader loader = null;
        switch (version) {
        case "java":
            // 如果是java版本，将信息存入config.properties文件
            loader = new PropertiesLoader("config.properties");
            loader.getProperties().setProperty("platform.rest.baseurl", url);
            loader.getProperties().setProperty("platform.rest.username", user);
            loader.getProperties().setProperty("platform.rest.password", pwd);
            try {
                fos = new FileOutputStream(URLDecoder.decode(
                        this.getClass().getResource("/config.properties").getFile(), "utf-8"));
                loader.getProperties().store(fos, "");
                /* 如果使用了zookeeper，保存到zooKeeper */
                LocalConfigService lcService = new LocalConfigService();
                Boolean useZK = lcService.getZKState(Util.ROOT_PATH);
                if (useZK) {
                    File f = new File(URLDecoder.decode(
                            this.getClass().getResource("/config.properties").getFile(), "utf-8"));
                    byte[] data = FileUtils.readFileToByteArray(f);
                    Util.setDataToZK(data, "/config.properties");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (fos != null) {
                        fos.flush();
                        fos.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            break;
        case ".net":
            // 再将信息存入c3p0-config.xml文件
            SAXReader reader = new SAXReader();
            Document d = null;
            try {
                d = reader.read(this.getClass().getResourceAsStream("/c3p0-config.xml"));
            } catch (DocumentException e) {
                throw new RuntimeException(e);
            }
            if (d != null) {
                d.selectSingleNode("//property[@name='jdbcUrl']").setText(url);
                d.selectSingleNode("//property[@name='user']").setText(user);
                d.selectSingleNode("//property[@name='password']").setText(pwd);
            }
            // 回写xml
            XMLWriter writer = null;
            try {
                fos = new FileOutputStream(URLDecoder.decode(
                        this.getClass().getResource("/c3p0-config.xml").getFile(), "utf-8"));
                writer = new XMLWriter(fos, OutputFormat.createPrettyPrint());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (writer != null) {
                        writer.write(d);
                        writer.close();
                    }
                    if (fos != null) {
                        fos.flush();
                        fos.close();
                        /* 如果使用了zookeeper，保存到zooKeeper */
                        LocalConfigService lcService = new LocalConfigService();
                        Boolean useZK = lcService.getZKState(Util.ROOT_PATH);
                        if (useZK) {
                            File f = new File(URLDecoder.decode(
                                    this.getClass().getResource("/c3p0-config.xml").getFile(),
                                    "utf-8"));
                            byte[] data = FileUtils.readFileToByteArray(f);
                            System.out.println(new String(data, "utf-8"));
                            Util.setDataToZK(data, "/c3p0-config.xml");
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            break;
        default:
            break;
        }
    }

    /**
     * 读取电子政务配置信息
     * 
     * @return 配置信息
     */
    @RequestMapping("/admin/readDzzwInfo")
    @ResponseBody
    public List<Map<String, Object>> readDzzwInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        // 获取类路径下config.properties文件中的字段值
        PropertiesLoader loader = new PropertiesLoader("config.properties");
        info.put("dzzwUrl_java", loader.getProperty("platform.rest.baseurl"));
        info.put("dzzwUser_java", loader.getProperty("platform.rest.username"));
        info.put("dzzwPwd_java", loader.getProperty("platform.rest.password"));
        /* 是否使用zookeeper */
        LocalConfigService lcService = new LocalConfigService();
        Boolean useZK = lcService.getZKState(Util.ROOT_PATH);
        byte[] data = null;
        if (useZK) {
            try {
                data = Util.getDataFromZK("/config.properties");
            } catch (KeeperException e) {
                e.printStackTrace();
            }
        }
        if (data != null) {
            InputStream is = new ByteArrayInputStream(data);
            Properties p = new Properties();
            try {
                p.load(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            info.put("dzzwUrl_java", p.getProperty("platform.rest.baseurl"));
            info.put("dzzwUser_java", p.getProperty("platform.rest.username"));
            info.put("dzzwPwd_java", p.getProperty("platform.rest.password"));
        }
        // 解析类路径下的c3p0-config.xml文件,并获取指定的字段值 //bbb[@id='b1'] 表示id属性为b1的bbb都可
        SAXReader reader = new SAXReader();
        Document d = null;
        try {
            d = reader.read(this.getClass().getResourceAsStream("/c3p0-config.xml"));
            if (useZK) {
                data = Util.getDataFromZK("/c3p0-config.xml");
                if (data != null) {
                    InputStream is = new ByteArrayInputStream(data);
                    d = reader.read(is);
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        if (d != null) {
            String dzzwUrl = d.selectSingleNode("//property[@name='jdbcUrl']").getText();
            String dzzwUser = d.selectSingleNode("//property[@name='user']").getText();
            String dzzwPwd = d.selectSingleNode("//property[@name='password']").getText();
            info.put("dzzwUrl_dotnet", dzzwUrl);
            info.put("dzzwUser_dotnet", dzzwUser);
            info.put("dzzwPwd_dotnet", dzzwPwd);
        }
        List<Map<String, Object>> infoList = new ArrayList<>();
        infoList.add(info);
        return infoList;
    }

    /**
     * 保存规则池中的规则
     * 
     * @param loginRule
     *            登录规则对象
     * @return "success"
     */
    @RequestMapping("/admin/saveRule")
    @ResponseBody
    public String saveLoginRule(@RequestBody LoginRule loginRule) {
        Util.G_REQUEST = request;
        response.setContentType("text/plain");
        return basicConfigService.saveLoginRule(loginRule);
    }

    /**
     * 删除规则池中的规则
     * 
     * @param loginRule
     *            登录规则对象
     * @return "success"
     */
    @RequestMapping("/admin/removeRule")
    @ResponseBody
    public String removeLoginRule(@RequestBody LoginRule loginRule) {
        Util.G_REQUEST = request;
        response.setContentType("text/plain");
        return basicConfigService.removeLoginRule(loginRule);
    }

    /**
     * 导出配置
     * 
     * @return 导出的文件(相对于项目根目录的路径)
     */
    @RequestMapping("/admin/exportConfig")
    @ResponseBody
    public String exportConfig() {
        String rootPath = request.getSession().getServletContext().getRealPath("/");
        return basicConfigService.exportConfig(rootPath);
    }

    /**
     * 导入配置文件
     * 
     * @param file
     *            配置文件
     * @return 成功返回"success"，失败返回"error"
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/admin/importConfig", method = RequestMethod.POST)
    @ResponseBody
    public String importConfig(@RequestParam(required = false, value = "file") MultipartFile file) {
        if (file != null) {
            try {
                Util.G_REQUEST = request;
                request.setCharacterEncoding("utf-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("不支持的编码类型");
            } // 设置请求体的编码格式
            response.setCharacterEncoding("utf-8");// 设置响应体的编码格式
            if (!file.isEmpty()) {
                String mapPath = request.getSession().getServletContext().getRealPath("/");// 获取项目的目录
                String fileName = "config.zsjs";// 上传文件的名称
                File targetFile = new File(mapPath, fileName);// 创建要上传的文件
                File uploadImgFile = new File(mapPath + "/admin/UploadImgs");
                // 删除uploadImgFile文件
                try {
                    FileUtils.deleteDirectory(uploadImgFile);
                    FileUtils.copyInputStreamToFile(file.getInputStream(), targetFile);
                    // 上传文件
                    ArrayList<String> filenames = Util.unzipFile(targetFile.getAbsolutePath(),
                            mapPath + "/"); // 解压文件到项目根目录
                    /* 如果使用zookeeper,写入zookeeper */
                    LocalConfigService lcService = new LocalConfigService();
                    Boolean useZK = lcService.getZKState(Util.ROOT_PATH);
                    if (useZK) {
                        clearImgs();
                        for (String filename : filenames) {
                            File f = new File(filename);
                            byte[] data = FileUtils.readFileToByteArray(f);
                            String shortName = filename.substring(filename.lastIndexOf("\\") + 1);
                            System.out.println(shortName);
                            switch (shortName) {
                            case "config.json":
                                Util.setDataToZK(data, "/configJson");
                                break;
                            case "role.json":
                                Util.setDataToZK(data, "/roleJson");
                                break;
                            case "config.properties":
                                Util.setDataToZK(data, "/config.properties");
                                break;
                            case "c3p0-config.xml":
                                Util.setDataToZK(data, "/c3p0-config.xml");
                                break;
                            case "admin.properties":
                                Util.setDataToZK(data, "/admin.properties");
                                break;
                            default:
                                break;
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //更新role.json,config.json, reSolutions.json
                basicConfigService.updateConfig();
                basicConfigService.updateRoleJson();
                basicConfigService.updateReSolutionJson();
                return "success";
            }
            return "error";
        } else {
            return "error";
        }
    }

    /**
     * 清空上传图片的节点
     */
    @RequestMapping("/admin/clearImgs")
    @ResponseBody
    public String clearImgs() {
        ZooKeeper zk = ZooKeeperFactory.getInstance();
        try {
            List<String> children = zk.getChildren("/UploadImgs", false);
            for (String child : children) {
                String path = "/UploadImgs/" + child;
                Util.deleteDataFromZK(path);
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "success";
    }
}
