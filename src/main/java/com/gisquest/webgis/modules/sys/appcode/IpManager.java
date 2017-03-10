
/**
 * 
 */
package com.gisquest.webgis.modules.sys.appcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.gisquest.webgis.util.Util;
import com.google.gson.GsonBuilder;

/**
 * 处理webgis中的 参数池管理
 * 
 * @author yedy
 *
 */
public class IpManager {
    public IpManager() {

    }

    /**
     * 处理请求
     * 
     * @param opt
     *            操作类型
     * @param id
     * @param ip
     * @param name
     */
    @SuppressWarnings("unchecked")
    public void processIp(String opt, String id, String ip, String name) {
        String json = Util.getConfigJson();// 获取json字符串
        json = Util.addCorrectFormat(json);// 格式化json
        List<Map<String, Object>> arrList = new GsonBuilder().create().fromJson(json, List.class);
        if (arrList.size() > 0) {
            for (Map<String, Object> arr : arrList) {
                for (String key : arr.keySet()) {
                    if (key.equals("IpPool")) {// 如果集合中的key等于IpPool
                        // 获取arr中key的值，并转为集合
                        List<Map<String, Object>> param = (List<Map<String, Object>>) arr.get(key);
                        boolean findIt = false;// 是否被发现
                        for (Map<String, Object> paramArr : param) {// 遍历集合param
                            int index = param.indexOf(paramArr);// 获取集合的下标
                            List<Map<String, Object>> selectIpPoolArr = new LinkedList<Map<String, Object>>();
                            for (Entry<String, Object> entryParamArr : paramArr.entrySet()) {
                                if (entryParamArr.getKey().equals("id")
                                        && entryParamArr.getValue().equals(id)) {
                                    selectIpPoolArr.add(paramArr);// 把选中的IpPool放入到selectIpPoolArr中
                                    break;
                                }
                            }
                            if (selectIpPoolArr.size() > 0) {// 判断是否有选中的IpPool
                                findIt = true;// 有
                                if (opt.equals("delete")) {// 如果opt等于delete

                                    param.remove(index);// 移除该IpPool
                                } else if (opt.equals("edit")) {// 如果opt等于编辑edit
                                    paramArr.put("name", name);
                                    paramArr.put("ip", ip);
                                    param.set(index, paramArr);// 把获取到的数据放入到集合中，并替换原来的数据

                                }
                                arr.put(key, param);
                                break;
                            }
                        }
                        if (findIt == false) {// 如果没有，则创建一个新的ip,并放入到集合中
                            Map<String, Object> newIp = new LinkedHashMap<String, Object>();
                            newIp.put("name", name);
                            newIp.put("ip", ip);
                            newIp.put("id", id);
                            param.add(newIp);
                        }
                        break;
                    }
                }
            }
        }
        // 把arrList转化为JSONArray
        String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(arrList);
        // 截取dataJson中的首尾，并转为string
        String data = dataJson.toString().substring(1, (dataJson.toString().length() - 1));
        Util.writeJson(data);// 写入到config.json中
    }

    /**
     * 该方法未使用
     * 
     * @param service
     * @param typeKey
     * @return
     */
    @SuppressWarnings("unchecked")
    public String saveConfig(String service, String typeKey) {
        try {
            String json = Util.getConfigJson();
            json = Util.addCorrectFormat(json);
            List<Map<String, Object>> arrList = new GsonBuilder().create().fromJson(json,
                    List.class);
            if (arrList.size() > 0) {
                for (int i = 0; i < arrList.size(); i++) {
                    Map<String, Object> arr = (HashMap<String, Object>) arrList.get(i);
                    List<String> keyList = new ArrayList<String>();
                    for (Map.Entry<String, Object> arrEntry : arr.entrySet()) {
                        keyList.add(arrEntry.getKey());
                    }
                    for (int j = 0; j < keyList.size(); j++) {

                        String key = keyList.get(j);
                        if (typeKey.equals(key)) {
                            arr.put(key, service);
                            break;
                        }
                    }
                }
            }
            // 把arrList转化为JSONArray
            String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(arrList);
            // 截取dataJson中的首尾，并转为string
            String data = dataJson.toString().substring(1, (dataJson.toString().length() - 1));
            Util.writeJson(data);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }
}
