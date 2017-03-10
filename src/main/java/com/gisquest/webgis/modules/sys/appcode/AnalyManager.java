package com.gisquest.webgis.modules.sys.appcode;

import static com.gisquest.webgis.util.Util.writeJson;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.gisquest.webgis.modules.sys.entity.Analy;
import com.gisquest.webgis.util.Util;
import com.google.common.base.Objects;
import com.google.gson.GsonBuilder;

/**
 * 对应webgis中分析设置
 * 
 * @author yedy
 *
 */
public class AnalyManager {

  /**
   * 处理分析请求
   * 
   * @param analy
   * @return
   */
  @SuppressWarnings({"unchecked"})
  public String processAnaly(Analy analy) {
    String json = Util.getConfigJson();// 获取json字符串
    json = Util.addCorrectFormat(json);// 格式化成json对象
    Map<String, Integer> showIndexMap = new LinkedHashMap<String, Integer>();
    if (!analy.getTocIds().equals("") && !analy.getTocShowIndexs().equals("")) {
      // 把获取到的TOCIds数组转化为list
      List<String> tIds = Arrays.asList(analy.getTocIds().split(","));
      // 把获取到的getTocShowIndexs转化为list
      List<String> tShowIndexs = Arrays.asList(analy.getTocShowIndexs().split(","));
      for (int i = 0; i < tIds.size(); i++) {
        // 把获取到的数据以key-value的形式存在在map中
        showIndexMap.put(tIds.get(i), Integer.parseInt(tShowIndexs.get(i)));//
      }
    }
    // 把json转为list集合
    List<Map<String, Object>> arrList = new GsonBuilder().create().fromJson(json, List.class);
    if (arrList.size() > 0) {// 判断list是否为空，如果不为空的话，进行遍历
      for (Map<String, Object> arr : arrList) {
        for (String key : arr.keySet()) {
          if (key.equals("analyze")) {// 如果集合中内容为analyze,
            // 定义集合，把key添加到集合中
            List<Map<String, Object>> analys = (List<Map<String, Object>>) arr.get(key);
            boolean findIt = false;// 在json中是否包含analyze
            for (int i = 0; i < analys.size(); i++) {
              Map<String, Object> analyArr = (Map<String, Object>) analys.get(i);// 获取analys中的每项，并以key-value的形式存放在analyArr中
              int index = analys.indexOf(analyArr);// 设置analys的下标
              if (analy.getmTag().equals("ChangI")) {// 如果获取到的标签包含“ChangI”
                String tId = analyArr.get("id").toString();
                if (showIndexMap.containsKey(tId)) {// 如果获取到的showIndexMap包含id
                  analyArr.put("showIndex", showIndexMap.get(tId));
                  analys.set(index, analyArr);// 把获取到的数据替换掉原来存在的数据
                }
                arr.put(key, analys);// 把修改后的数据，放入analys中
              } else {
                List<Map<String, Object>> selectAnalyArr = new LinkedList<Map<String, Object>>();
                for (Map.Entry<String, Object> entryAnalyArr : analyArr.entrySet()) {
                  // 如果获取analyArr中的key中有“id”并且value等于获取到的analyId，把这条数据放入selectAnalyArr中
                  if (entryAnalyArr.getKey().equals("id")
                      && entryAnalyArr.getValue().equals(analy.getAnalyId())) {
                    selectAnalyArr.add(analyArr);
                    break;
                  }
                }
                if (selectAnalyArr.size() > 0) {// 如果selectAnalyArr存在
                  findIt = true;// 说明json中包含analyze
                  if (analy.getmTag().equals("delete")) {// 如果获取到的mTag等于delete
                    /* 删除前将启用状态设为false */
                    analys.remove(index);// 移除当前analyArr
                    analys = removeChild(analys, analy.getAnalyId());// 移除当前analy
                  } else if (analy.getmTag().equals("edit")) {// 如果获取到的mTag等于edit
                    if (analy.getAnalyRel().equals("root")) {// 如果获取到的AnalyRel等于root
                      analyArr.put("label", analy.getAnalyLable());
                      analyArr.put("id", analy.getAnalyId());
                      analyArr.put("fxType", analy.getAnalyType());
                      analys.set(index, analyArr);// 替换原来的数据
                    } else if (analy.getAnalyRel().equals("folder")) {// 如果获取到的AnalyRel等于folder
                      analyArr.put("label", analy.getAnalyLable());
                      analyArr.put("parent", analy.getAnalyParent());
                      analyArr.put("id", analy.getAnalyId());
                      analyArr.put("fxType", analy.getAnalyType());
                      analys.set(index, analyArr);// 替换原来的数据
                    } else {
                      analyArr.put("label", analy.getAnalyLable());
                      analyArr.put("parent", analy.getAnalyParent());
                      analyArr.put("id", analy.getAnalyId());
                      analyArr.put("fxType", analy.getAnalyType());
                      analyArr.put("customUrl", analy.getAnalyReport());
                      analyArr.put("year", analy.getAnalyYear());
                      analyArr.put("tableName", analy.getAnalyParam());
                      analyArr.put("checked", false);
                      analyArr.put("isAutoGetXZQ", analy.getIsAutoGetXZQ());
                      analyArr.put("xzqCode", analy.getXzqCode());
                      if (Objects.equal(analy.getAnalyType(), "1")) { // 如果是规划分析
                        analyArr.put("isUseGZQ", analy.getIsUseGZX());
                        analyArr.put("isUseYTQ", analy.getIsUseYTQ());
                        analyArr.put("isUseGHYT", analy.getIsUseGHYT());
                        analyArr.put("isUseGHJQ", analy.getIsUseGHJQ());
                      }
                      analys.set(index, analyArr);// 替换原来的数据
                    }

                  } else if (analy.getmTag().equals("changP")) {// 如果mTag等于长changP
                    analyArr.put("parent", analy.getAnalyParent());// 把获取到的数据放入analyArr中
                  }
                  arr.put(key, analys);// 更新的数据放入到arrList中
                  break;
                }
              }
            }
            // 如果没有发现analyze并且mTag不等于delete和ChangP
            if (findIt == false && !analy.getmTag().equals("delete")
                && !analy.getmTag().equals("ChangeI")) {
              if (analy.getAnalyRel().equals("folder")) {// 如果是文件夹
                Map<String, Object> newAnaly = new LinkedHashMap<String, Object>();
                newAnaly.put("label", analy.getAnalyLable());
                newAnaly.put("id", analy.getAnalyId());
                newAnaly.put("parent", analy.getAnalyParent());
                newAnaly.put("isgroup", true);
                newAnaly.put("showIndex",
                    analy.getShowIndex().equals("") ? 88 : Integer.parseInt(analy.getShowIndex()));
                newAnaly.put("stat", Boolean.parseBoolean(analy.getAnalyChecked()));
                newAnaly.put("fxType", analy.getAnalyType());
                analys.add(newAnaly);// 增加一个新的文件夹
              } else {
                Map<String, Object> newAnalysis = new LinkedHashMap<String, Object>();
                newAnalysis.put("label", analy.getAnalyLable());
                newAnalysis.put("id", analy.getAnalyId());
                newAnalysis.put("parent", analy.getAnalyParent());
                newAnalysis.put("checked", false);
                newAnalysis.put("stat", true);
                newAnalysis.put("fxType", analy.getAnalyType());
                newAnalysis.put("customUrl", analy.getAnalyReport());
                newAnalysis.put("year", analy.getAnalyYear());
                newAnalysis.put("showIndex", (analy.getShowIndex()).equals("") ? 11
                    : Integer.parseInt(analy.getShowIndex()));
                newAnalysis.put("tableName", analy.getAnalyParam());
                newAnalysis.put("isAutoGetXZQ", analy.getIsAutoGetXZQ());
                newAnalysis.put("xzqCode", analy.getXzqCode());
                if (Objects.equal(analy.getAnalyType(), "1")) { // 如果是规划分析
                  newAnalysis.put("isUseGZQ", analy.getIsUseGZX());
                  newAnalysis.put("isUseYTQ", analy.getIsUseYTQ());
                  newAnalysis.put("isUseGHYT", analy.getIsUseGHYT());
                  newAnalysis.put("isUseGHJQ", analy.getIsUseGHJQ());
                }
                analys.add(newAnalysis);// 增加一个新的分析项
              }
            }
            break;
          }
        }
      }
    }
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(arrList);
    String data = dataJson.substring(1, // 截取datajson的首尾
        (dataJson.length() - 1));
    return writeJson(data);// 把修改后的数据写入到config.json中
  }

  /**
   * 移除list中parent等于pId的元素
   * 
   * @param analys
   *          分析项列表
   * @param pId
   *          父节点Id
   * @return
   */
  public static List<Map<String, Object>> removeChild(List<Map<String, Object>> analys,
      String pId) {
    for (int i = analys.size() - 1; i >= 0; i--) {
      Map<String, Object> analyArr = analys.get(i);
      if (analyArr.containsKey("parent")) {
        if (analyArr.get("parent").equals(pId)) {
          analys.remove(i);
        }
      }
    }
    return analys;
  }

}
