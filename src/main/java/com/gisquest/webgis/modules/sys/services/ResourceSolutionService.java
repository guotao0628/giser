package com.gisquest.webgis.modules.sys.services;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.zookeeper.KeeperException;
import org.springframework.stereotype.Service;

import com.gisquest.platform.common.utils.FileUtils;
import com.gisquest.webgis.modules.sys.entity.ResourceSolution;
import com.gisquest.webgis.modules.sys.entity.ServiceInvocationError;
import com.gisquest.webgis.util.Util;
import com.google.gson.GsonBuilder;

/**
 * 资源方案服务
 * 
 * @author Jisj1
 *
 */
@Service("reSolutionService")
public class ResourceSolutionService {
    /** 资源方案配置文件名 */
    public static final String FILENAME = "reSolutions.json";

    /**
     * 将JSON串写入资源方案文件
     * 
     * @param reSolutionJson
     *            JSON串
     */
    public String writeReSolutionJson(String reSolutionJson) {
        String rootPath = Util.ROOT_PATH;
        LocalConfigService lcService = new LocalConfigService();
        boolean useZooKeeper = lcService.getZKState(rootPath);
        File file = new File(rootPath, FILENAME);
        try {
            FileUtils.writeStringToFile(file, reSolutionJson, "UTF-8", false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (useZooKeeper) {
            try {
                Util.setDataToZK(reSolutionJson.getBytes("utf-8"), "/reSolutionJson");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return reSolutionJson;
    }

    /**
     * 获取资源方案
     * 
     * @param filepath
     * @return
     */
    public String getReSolution() {
        String rootPath = Util.ROOT_PATH;
        String reSolutions = new String();
        /* 读取文件内容 */
        reSolutions = Util.readFile(rootPath + File.separatorChar + FILENAME);
        LocalConfigService lcService = new LocalConfigService();
        boolean useZooKeeper = lcService.getZKState(rootPath);
        /* 若使用zookeeper，则使用zookeeper中的config.json */
        if (useZooKeeper) {
            try {
                byte[] data = Util.getDataFromZK("/reSolutionJson");
                if (data != null) {
                    reSolutions = new String(data, "utf-8");
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            } catch (KeeperException e) {
                return new GsonBuilder().setPrettyPrinting().create()
                        .toJson(new ServiceInvocationError("连接zookeeper失败，请检查是否启动zookeeper！"));
            }
        }
        return reSolutions;
    }

    /**
     * 保存资源方案
     * 
     * @param solution
     *            资源方案
     */
    @SuppressWarnings("unchecked")
    public void saveReSolution(ResourceSolution solution) {
        Map<String, Object> solutionMap = Util.beanToMap(solution);
        /* 获取solution对象的成员 */
        String id = solution.getId();
        /* 获取reSolutions.json */
        String reSolutionJson = getReSolution();
        /* 将json转成list */
        List<Map<String, Object>> reSolutions = new GsonBuilder().create().fromJson(reSolutionJson, List.class);
        if (reSolutions == null) {
            reSolutions = new ArrayList<>();
        }
        if (reSolutions.size() > 0) {
            /* 设置flag，是否在资源方案中找到传来的id */
            boolean isFound = false;
            for (Map<String, Object> reSolution : reSolutions) {
                String mapId = (String) reSolution.get("id");
                /* 若前端传来的solution id存在，则为编辑操作 */
                if (mapId != null && mapId.equals(id)) {
                    /* 编辑资源方案 */
                    reSolution.putAll(solutionMap);
                    /* 将Flag设置为true，并跳出循环 */
                    isFound = true;
                    break;
                }
            }
            /* 如果前端传来的id不存在，则是新增操作 */
            if (!isFound) {
                /* 将solution添加到reSolution list集合中 */
                reSolutions.add(solutionMap);
            }
        } else {
            /* 将solution添加到reSolution list集合中 */
            reSolutions.add(solutionMap);
        }
        /** 保存到reSolutions.json中 */
        reSolutionJson = new GsonBuilder().setPrettyPrinting().create().toJson(reSolutions);
        writeReSolutionJson(reSolutionJson);
    }

    /**
     * 删除资源方案
     * 
     * @param solution
     *            资源方案对象
     */
    @SuppressWarnings("unchecked")
    public void deleteReSolution(ResourceSolution solution) {
        String rootPath = Util.ROOT_PATH;
        String id = solution.getId();
        /* 获取reSolutions.json */
        String reSolutionJson = getReSolution();
        /* 将json转成list */
        List<Map<String, Object>> reSolutions = new GsonBuilder().create().fromJson(reSolutionJson, List.class);
        for (Map<String, Object> reSolution : reSolutions) {
            String mapId = (String) reSolution.get("id");
            if (mapId != null && mapId.equals(id)) {
                /* 删除资源方案 */
                String originUrl = solution.getThumb();
                if (originUrl != null && !originUrl.equals("UploadImgs/th_default.png")) {
                    /* 如果不为默认图片，则删除该图片 */
                    FileUtils.deleteFile(rootPath + File.separatorChar + "admin"
                            + File.separatorChar + originUrl);
                    LocalConfigService lcService = new LocalConfigService();
                    Boolean useZK = lcService.getZKState(rootPath);
                    if (useZK) {
                        Util.deleteDataFromZK(
                                "/UploadImgs" + originUrl.substring(originUrl.lastIndexOf("/")));
                    }
                }
                reSolutions.remove(reSolution);
                break;
            }
        }
        /** 保存到reSolutions.json中 */
        reSolutionJson = new GsonBuilder().setPrettyPrinting().create().toJson(reSolutions);
        writeReSolutionJson(reSolutionJson);
    }
}
