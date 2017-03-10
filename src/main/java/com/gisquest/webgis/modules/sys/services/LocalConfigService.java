package com.gisquest.webgis.modules.sys.services;

import java.io.File;
import java.io.IOException;

import com.gisquest.platform.common.utils.FileUtils;
import com.gisquest.webgis.modules.sys.entity.LocalConfig;
import com.gisquest.webgis.util.Util;
import com.google.gson.GsonBuilder;

/**
 * 本地配置服务
 * 
 * @author Administrator
 *
 */
public class LocalConfigService {
    /** 本地配置文件名 */
    public static final String LC_FILENAME = "localConfig.json";

    /**
     * 读取本地配置
     * 
     * @param from
     *            路径
     * @return 本地配置文件内容
     */
    private String read(String rootPath) {
        String lcString = new String();
        lcString = Util.readFile(rootPath + File.separatorChar + LC_FILENAME);
        return lcString;
    }

    /**
     * 写出配置到本地配置文件
     * 
     * @param rootPath
     *            项目根目录
     * @param lcString
     *            配置内容
     * @return 本地配置文件内容
     */
    private String write(String rootPath, String lcString) {
        File lcFile = new File(rootPath, LC_FILENAME);
        try {
            FileUtils.writeStringToFile(lcFile, lcString, "UTF-8", false);
        } catch (IOException e) {
            throw new RuntimeException("文件读取失败！");
        }
        return lcString;
    }

    /**
     * 设置本地配置中的useZK项
     * 
     * @param rootPath
     *            项目根目录
     * @param useZK
     *            useZK项的值
     * @return 本地配置文件内容
     */
    public String setZKState(String rootPath, boolean useZK) {
        LocalConfig lc = new LocalConfig();
        lc.setUseZK(useZK);
        String lcString = new GsonBuilder().setPrettyPrinting().create().toJson(lc);
        return write(rootPath, lcString);
    }

    /**
     * 获取本地配置中的useZK项
     * 
     * @param rootPath
     *            项目根目录
     * @return useZK项的值
     */
    public Boolean getZKState(String rootPath) {
        String lcString = read(rootPath);
        LocalConfig lc = new LocalConfig();
        if (!lcString.trim().equals("")) {
            lc = new GsonBuilder().create().fromJson(lcString, LocalConfig.class);
        }
        return lc.isUseZK();
    }
}
