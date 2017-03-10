/**
 * 
 */
package com.gisquest.webgis.modules.sys.appcode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.gisquest.webgis.util.Util;
import com.google.gson.GsonBuilder;

/**
 * 处理webgis中的基础配置
 * 
 * @author yedy
 *
 */
public class GeneralManager {
    public GeneralManager() {
        // 默认构造函数
    }

    /**
     * 设置政区导航服务
     * 
     * @param service
     *            服务地址
     * @param typeKey
     *            服务类型
     * @param dic
     *            服务是否可用
     * @return
     */
    @SuppressWarnings("unchecked")
    public String saveConfig(String service, String typeKey, boolean dic) {
        String json = Util.getConfigJson();// 获取json字符串
        json = Util.addCorrectFormat(json);// 转为json格式
        List<Map<String, Object>> arrList = new GsonBuilder().create().fromJson(json, List.class);
        if (arrList.size() > 0) {// 判断集合是否为空
            for (int i = 0; i < arrList.size(); i++) {
                Map<String, Object> arr = arrList.get(i);// 获取list中的每项并转为map
                List<String> listKey = new ArrayList<String>();
                for (Entry<String, Object> map : arr.entrySet()) {
                    listKey.add(map.getKey());// 把map中的key放入到list集合中
                }
                for (int j = 0; j < listKey.size(); j++) {
                    String key = listKey.get(j);// 获取集合中的每项
                    if (key.equals(typeKey)) {// 判断获取到的key是否等于集合中的key
                        arrList.get(i).put(key, service);
                    }
                }
            }
        }
        // 把获取到的修改后的数据转化为json
        String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(arrList);
        // 截取dataJson中的首尾，并转为string
        String data = dataJson.toString().substring(1, (dataJson.toString().length() - 1));
        Util.writeJson(data);// 写入到config.json中
        return "success";
    }
}
