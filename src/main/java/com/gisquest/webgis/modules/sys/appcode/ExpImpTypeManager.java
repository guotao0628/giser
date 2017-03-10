
package com.gisquest.webgis.modules.sys.appcode;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.gisquest.webgis.modules.sys.entity.ExpImpType;
import com.gisquest.webgis.util.Util;
import com.google.gson.GsonBuilder;

/**
 * 作用：是对webgis中文件管理做处理
 * 
 * @author yedy
 *
 */
public class ExpImpTypeManager {
    /**
     * 处理文件管理的导入导出
     * 
     * @param expImpType
     */
    @SuppressWarnings("unchecked")
    public void processExpImp(ExpImpType expImpType) {
        String json = Util.getConfigJson();// 获取json字符串
        json = Util.addCorrectFormat(json);// 格式化成json类型
        // 把json类型转为list集合
        List<Map<String, Object>> arrList = new GsonBuilder().create().fromJson(json, List.class);
        if (arrList.size() > 0) {
            String fKye = "";
            if (expImpType.getyType().equals("exp")) {// 判断获取到的exp是否等于exp
                fKye = "ExportFileTypeTable";// 如果等于则fKye=ExportFileTypeTable
            } else {
                fKye = "ImportFileTypeTable";// 如果不等则fKye="ImportFileTypeTable"
            }
            for (Map<String, Object> arr : arrList) {// 遍历arrList
                for (String key : arr.keySet()) {
                    if (fKye.equals(key)) {// 判断获到的fkey是否等于key，如果等于
                        // 获取集合中的key的值，并转为list集合
                        List<Map<String, Object>> toc = (List<Map<String, Object>>) arr.get(key);
                        for (Map<String, Object> tocArr : toc) {
                            int index = toc.indexOf(tocArr);// 获取tocArr的下标
                            // 创建一个集合存放已选择的toc
                            List<Map<String, Object>> selectedToc = new LinkedList<Map<String, Object>>();
                            for (Map.Entry<String, Object> entryTocArr : tocArr.entrySet()) {
                                if (entryTocArr.getKey().equals("id")
                                        && entryTocArr.getValue().equals(expImpType.getId())) {
                                    selectedToc.add(tocArr);
                                    break;
                                }
                            }
                            if (selectedToc.size() > 0) {// 如果toc选中
                                // 如果获取到的类型为edit，则表示编辑
                                if (expImpType.getoType().toLowerCase().equals("edit")) {
                                    tocArr.put("label", expImpType.getLabel());
                                    tocArr.put("ext", expImpType.getExt());
                                    toc.set(index, tocArr);// 把用编辑的替换掉之前的
                                } else if (expImpType.getoType().toLowerCase().equals("stat")) {
                                    tocArr.put("stat", expImpType.getStat());
                                    toc.set(index, tocArr);// 把编辑好的值重新替换放到toc中
                                }
                                arr.put(key, toc);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }
        String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(arrList);
        // 把dataJson转为string，并截取首尾
        String data = dataJson.toString().substring(1, (dataJson.toString().length() - 1));
        Util.writeJson(data);// 把处理好的数据写入到从config.json中
    }
}
