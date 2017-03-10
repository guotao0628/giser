package com.gisquest.webgis.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import com.gisquest.platform.common.utils.FileUtils;
import com.gisquest.webgis.modules.sys.entity.ServiceInvocationError;
import com.gisquest.webgis.modules.sys.services.LocalConfigService;
import com.gisquest.webgis.modules.sys.services.ZooKeeperFactory;
import com.google.gson.GsonBuilder;

/**
 * 工具类主要对Json文件的操作(读写)
 * 
 * @author Jisj1
 *
 */
public class Util {
    /** 请求体 */
    public static HttpServletRequest G_REQUEST;
    /** 项目根目录 */
    public static String ROOT_PATH;

    public static String getBookmarks() {
        String rootPath = G_REQUEST.getSession().getServletContext().getRealPath("/");
        File file = new File(rootPath, "bookmarks.json");
        String bookmarks = new String();
        /* 如果文件不存在，则创建文件 */
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            bookmarks = readFile(rootPath + File.separatorChar + "bookmarks.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bookmarks;
    }

    public static void writeBookmarks(String bookmarks) {
        String rootPath = G_REQUEST.getSession().getServletContext().getRealPath("/");
        File file = new File(rootPath, "bookmarks.json");
        try {
            FileUtils.writeStringToFile(file, bookmarks, "UTF-8", false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从zookeeper中获取数据
     * 
     * @param zNode
     *            zookeeper中的节点
     * @return 返回字节型数据
     * @throws KeeperException
     *             连接zookeeper异常
     */
    public static byte[] getDataFromZK(String zNode) throws KeeperException {
        ZooKeeper zk = null;
        byte[] data = null;
        try {
            zk = ZooKeeperFactory.getInstance();
            if (zk.exists(zNode, true) != null) {
                data = zk.getData(zNode, true, zk.exists(zNode, true));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 设置zookeeper指定节点上的数据
     * 
     * @param data
     *            要设置的数据
     * @param zNode
     *            zookeeper节点
     * @throws KeeperException
     *             zookeeper连接异常
     */
    public static String setDataToZK(byte[] data, String zNode) {
        ZooKeeper zk = null;
        try {
            zk = ZooKeeperFactory.getInstance();
            if (zk.exists(zNode, false) != null) {
                zk.setData(zNode, data, -1);
            } else {
                /* 若不存在节点，则创建节点 */
                zk.create(zNode, data, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            return new String(data, "utf-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (KeeperException e) {
            return new ServiceInvocationError("连接zookeeper失败，请检查是否启动zookeeper！").toString();
        }
    }

    /**
     * 在zookeeper上新增节点
     * 
     * @param data
     *            数据
     * @param zNode
     *            节点路径
     * @return
     */
    public static String createDataToZK(byte[] data, String zNode) {
        ZooKeeper zk = ZooKeeperFactory.getInstance();
        try {
            if (zk.exists(zNode, false) == null) {
                zk.create(zNode, data, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            return new String(data, "utf-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (KeeperException e) {
            return new ServiceInvocationError("连接zookeeper失败，请检查是否启动zookeeper！").toString();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从zookeeper中删除节点
     * 
     * @param zNode
     *            节点
     * @return 返回删除的节点名
     */
    public static String deleteDataFromZK(String zNode) {
        ZooKeeper zk = null;
        try {
            zk = ZooKeeperFactory.getInstance();
            if (zk.exists(zNode, true) != null) {
                zk.delete(zNode, -1);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (KeeperException e) {
            return new ServiceInvocationError("连接zookeeper失败，请检查是否启动zookeeper！").toString();
        }
        return zNode;
    }

    /**
     * 将Java对象转成Map
     * 
     * @param obj
     *            Java对象
     * @return
     */
    public static Map<String, Object> beanToMap(Object obj) {
        /* 初始化map对象 */
        Map<String, Object> map = new LinkedHashMap<>();
        /* 获取对象的类类型 */
        Class<? extends Object> clazz = obj.getClass();
        /* 获取对象的成员方法数组 */
        Method[] methods = clazz.getDeclaredMethods();
        /* 遍历成员方法数组 */
        for (Method method : methods) {
            /* 获取方法名 */
            String methodName = method.getName();
            /* 若方法名以get开头 */
            if (methodName.startsWith("get")) {
                /* 成员变量为方法名get后面的字符串，并转换成小写 */
                String field = methodName.substring(3);
                field = field.toLowerCase().charAt(0) + field.substring(1);
                try {
                    /* 构造map对象，将其成员变量做为键 ，成员变量get方法返回的值做为值 */
                    map.put(field, method.invoke(obj));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        /* 返回map对象 */
        return map;
    }

    /**
     * 读取config.json文件
     * 
     * @return
     */
    public static String getConfigJson() {
        String configJson = new String();
        String rootPath = G_REQUEST.getSession().getServletContext().getRealPath("/");
        ROOT_PATH = rootPath;
        File file = new File(rootPath, "config.json");
        try {
            /* 如果文件不存在，则创建文件 */
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            configJson = readFile(rootPath + File.separatorChar + "config.json");
            LocalConfigService lcService = new LocalConfigService();
            boolean useZooKeeper = lcService.getZKState(rootPath);
            /* 若使用zookeeper，则使用zookeeper中的config.json */
            if (useZooKeeper) {
                byte[] data = getDataFromZK("/configJson");
                if (data != null) {
                    configJson = new String(data, "utf-8");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (KeeperException e) {
            return new ServiceInvocationError("连接zookeeper失败，请检查是否启动zookeeper！").toString();
        }
        return configJson;
    }

    /**
     * 读取配置文件
     * 
     * @param filename
     *            读取的config文件名
     * @return 配置文件内容
     */
    public static String getConfigJson(String filename) {
        String path = G_REQUEST.getSession().getServletContext().getRealPath("/" + filename);
        String configJson = new String();
        configJson = readFile(path);
        return configJson;
    }

    /**
     * 保存修改后的config.json文件
     * 
     * @param configJson
     *            配置文件内容
     * @return
     * @throws KeeperException
     * @throws IOException
     */
    public static String writeJson(String configJson) {
        /* 将/u003c转成<,/u003e转成> */
        configJson = configJson.replace("\\u003c", "<").replace("\\u003e", ">");
        String rootPath = G_REQUEST.getSession().getServletContext().getRealPath("/");
        LocalConfigService lcService = new LocalConfigService();
        boolean useZooKeeper = lcService.getZKState(rootPath);
        File cfgFile = new File(rootPath, "config.json");
        try {
            FileUtils.writeStringToFile(cfgFile, configJson, "UTF-8", false);
            /* 若使用zookeeper，则写入zookeeper中的configJson节点 */
            if (useZooKeeper) {
                setDataToZK(configJson.getBytes("utf-8"), "/configJson");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return configJson;
    }

    /**
     * 保存配置文件
     * 
     * @param configJson
     *            config.json内容
     * @param filename
     *            另存文件名
     */
    public static void writeJson(String configJson, String filename) {
        String path = G_REQUEST.getSession().getServletContext().getRealPath("/" + filename);
        File cfgFile = new File(path);
        try {
            FileUtils.writeStringToFile(cfgFile, configJson, "UTF-8", false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取role.json
     * 
     * @return 获取到的字符串
     */
    public static String getRoleJson() {
        String roleJson = new String();
        String rootPath = G_REQUEST.getSession().getServletContext().getRealPath("/");
        roleJson = readFile(rootPath + File.separatorChar + "role.json");
        LocalConfigService lcService = new LocalConfigService();
        boolean useZooKeeper = lcService.getZKState(rootPath);
        /* 若使用zookeeper，则使用zookeeper中的config.json */
        if (useZooKeeper) {
            try {
                byte[] data = getDataFromZK("/roleJson");
                if (data != null) {
                    roleJson = new String(data, "utf-8");
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            } catch (KeeperException e) {
                return new GsonBuilder().create()
                        .toJson(new ServiceInvocationError("连接zookeeper失败，请检查是否启动zookeeper！"));
            }
        }
        return roleJson;
    }

    /**
     * 保存修改后的role.json文件
     * 
     * @param roleJson
     *            roleJson数据
     * @return
     */
    public static String writeRoleJson(String roleJson) {
        String rootPath = G_REQUEST.getSession().getServletContext().getRealPath("/");
        LocalConfigService lcService = new LocalConfigService();
        boolean useZooKeeper = lcService.getZKState(rootPath);
        File roleJsonFile = new File(rootPath, "role.json");
        try {
            FileUtils.writeStringToFile(roleJsonFile, roleJson, "UTF-8", false);
            if (useZooKeeper) {
                setDataToZK(roleJson.getBytes("utf-8"), "/roleJson");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return roleJson;
    }

    /**
     * 格式化config.json数据
     * 
     * @param json
     * @return 格式化后的json
     */
    public static String addCorrectFormat(String json) {
        json = json.trim();// 去掉json中的空格
        if (json.length() > 0) {
            if (json.indexOf(0) != '[') {// 如果json不是以"["开头,则在json前加"["
                json = "[" + json;
            }
            if (json.indexOf(json.length() - 1) != ']') {// 如果json不是以"["结尾,则在json末尾"["
                json += "]";
            }
        } else {
            json = "[]";
        }
        json = json.trim();
        return json;
    }

    /**
     * 格式化role.json文件
     * 
     * @param json
     *            role.json数据
     * @return 格式化后的json数据
     */
    public static String addCorrectFormatRole(String json) {
        json = json.trim();// 去掉json中的空格
        if (json.length() > 0) {
            if (json.indexOf(0) != '[') {// 如果json不是以"["开头,则在json前加"["
                json = "[" + json;
            }
            if (json.indexOf(json.length() - 1) != ']') {// 如果json不是以"["结尾,则在json末尾"["
                json += "]";
            }
        } else {
            // json = "[{\"role"+"\":"+"[]"+"}]";
            json = "[{\"roles\":[]}]";
        }
        return json;
    }

    /**
     * 用户名解密
     * 
     * @param ssoToken
     *            字符串
     * @return String 返回加密字符串
     */
    public static String decrypt(String ssoToken) {
        String name = new String();
        StringTokenizer st = new StringTokenizer(ssoToken, "%");
        while (st.hasMoreElements()) {
            int asc = Integer.parseInt((String) st.nextElement()) - 27;
            name = name + (char) asc;
        }
        return name;
    }

    /**
     * 用户名加密
     * 
     * @param ssoToken
     *            字符串
     * @return String 返回加密字符串
     */
    public static String encrypt(String ssoToken) {
        byte[] ssoTokenBtye;
        String name = new String();
        try {
            ssoTokenBtye = ssoToken.getBytes("ISO-8859-1");
            for (int i = 0; i < ssoTokenBtye.length; i++) {
                int asc = ssoTokenBtye[i];
                ssoTokenBtye[i] = (byte) (asc + 27);
                name = name + (asc + 27) + "%";
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return name;
    }

    /**
     * 将源文件/文件夹生成指定格式的压缩文件,格式zip
     * 
     * @param resourcesPath
     *            源文件/文件夹
     * @param targetPath
     *            目的压缩文件保存路径
     * @throws Exception
     */
    public static void zipFile(String resourcesPath, String targetPath) throws Exception {
        File resourcesFile = new File(resourcesPath); // 源文件
        File targetFile = new File(targetPath); // 目的
        // 如果目的路径不存在，则新建
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        String targetName = resourcesFile.getName() + ".zsjs"; // 目的压缩文件名
        FileOutputStream outputStream = new FileOutputStream(targetPath + "\\" + targetName);
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(outputStream));
        createZipFile(out, resourcesFile, "");
        out.close();
    }

    /**
     * @desc 生成压缩文件。 如果是文件夹，则使用递归，进行文件遍历、压缩 如果是文件，直接压缩
     * @param out
     *            输出流
     * @param file
     *            目标文件
     * @throws Exception
     */
    private static void createZipFile(ZipOutputStream out, File file, String dir) throws Exception {
        // 如果当前的是文件夹，则进行进一步处理
        if (file.isDirectory()) {
            // 得到文件列表信息
            File[] files = file.listFiles();
            // 将文件夹添加到下一级打包目录
            out.putNextEntry(new ZipEntry(dir + "/"));
            dir = dir.length() == 0 ? "" : dir + "/";
            // 循环将文件夹中的文件打包
            for (int i = 0; i < files.length; i++) {
                createZipFile(out, files[i], dir + files[i].getName()); // 递归处理
            }
        } else { // 当前的是文件，打包处理
            // 文件输入流
            FileInputStream fis = new FileInputStream(file);
            out.putNextEntry(new ZipEntry(dir));
            // 进行写操作
            int j = 0;
            byte[] buffer = new byte[1024];
            while ((j = fis.read(buffer)) > 0) {
                out.write(buffer, 0, j);
            }
            // 关闭输入流
            fis.close();
        }
    }

    /**
     * 解压缩
     * 
     * @param sZipPathFile
     *            要解压的文件
     * @param sDestPath
     *            解压到某文件夹
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static ArrayList unzipFile(String sZipPathFile, String sDestPath) {
        ArrayList<String> allFileName = new ArrayList<String>();
        try {
            // 先指定压缩档的位置和档名，建立FileInputStream对象
            FileInputStream fins = new FileInputStream(sZipPathFile);
            // 将fins传入ZipInputStream中
            ZipInputStream zins = new ZipInputStream(fins);
            ZipEntry ze = null;
            byte[] ch = new byte[256];
            while ((ze = zins.getNextEntry()) != null) {
                File zfile = new File(sDestPath + ze.getName());
                File fpath = zfile.getParentFile();
                if (ze.isDirectory()) {
                    if (!zfile.exists()) {
                        zfile.mkdirs();
                    }
                    zins.closeEntry();
                } else {
                    if (fpath != null) {
                        fpath.mkdirs();
                    }
                    FileOutputStream fouts = new FileOutputStream(zfile);
                    int i;
                    allFileName.add(zfile.getAbsolutePath());
                    while ((i = zins.read(ch)) != -1) {
                        fouts.write(ch, 0, i);
                    }
                    zins.closeEntry();
                    fouts.close();
                }
            }
            fins.close();
            zins.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return allFileName;
    }

    /**
     * 读取文件(with bom)
     * 
     * @param path
     *            文件路径
     * @return 返回字符串
     * @throws IOException
     *             IO异常
     */
    public static String readFile(String path) {
        File file = new File(path);
        BufferedReader reader = null;
        StringBuffer laststr = new StringBuffer();
        try {
            if (!file.exists()) {
                /* 如果文件不存在，则创建文件 */
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
            }
            InputStream in = new FileInputStream(file);
            reader = new BufferedReader(new UnicodeReader(in, "utf-8"));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                laststr.append(tempString);
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return laststr.toString();
    }

}
