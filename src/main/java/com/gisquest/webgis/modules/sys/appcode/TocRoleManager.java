/**
 * 
 */
package com.gisquest.webgis.modules.sys.appcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.gisquest.webgis.modules.sys.entity.TocRole;
import com.gisquest.webgis.util.Util;
import com.google.gson.GsonBuilder;

/**
 * 该类的作用是处理webgis中权限管理中的图层角色管理
 * 
 * @author yedy
 *
 */
public class TocRoleManager {

  public TocRoleManager() {
    // 默认构造函数
  }

  /**
   * 处理请求
   * 
   * @param tocRole
   */

  @SuppressWarnings({"unchecked", "rawtypes"})
  public void saveTocRole(TocRole tocRole) {

    String json = Util.getRoleJson();// 获取json数据
    json = Util.addCorrectFormatRole(json);// 格式化为json格式
    Map<String, Object> priviMap = new LinkedHashMap<>();
    Map<String, Object> initalMap = new LinkedHashMap<>();
    if (!tocRole.getTocIds().equals("") && !tocRole.getTocPrivis().equals("")
        && !tocRole.getTocInitals().equals("")) {
      // 把获取到tocIds转为集合
      List<String> tIds = Arrays.asList(tocRole.getTocIds());
      // 把获取到的tocPrivis转为集合
      List<String> tPrivis = Arrays.asList(tocRole.getTocPrivis());
      // 把获取去到的tocInitals转为集合
      List<String> tInitals = Arrays.asList(tocRole.getTocInitals());
      for (int i = 0; i < tIds.size(); i++) {
        // 把tocIds作为key,把tPrivis作为value，放入到priviMap
        priviMap.put(tIds.get(i), tPrivis.get(i));
        // 把tocIds作为key,把tocInitals作为value，放入到initalMap
        initalMap.put(tIds.get(i), tInitals.get(i));
      }
    }
    // 把获取到的json转为list集合
    List<Map<String, Object>> arrList = new GsonBuilder().create().fromJson(json, List.class);
    if (arrList.size() > 0) {
      for (Map<String, Object> arr : arrList) {
        for (String key : arr.keySet()) {
          if (key.equals("roles")) {// 判断key是否等于roles
            // 获取roles的value，转为集合
            boolean findIt = false;// 判断是否存在
            List<Map<String, Object>> roles = (List<Map<String, Object>>) arr.get(key);

            for (int i = 0; i < roles.size(); i++) {
              // 获取roles中的每项，并把它存在map中
              Map<String, Object> roleArr = roles.get(i);
              int index = roles.indexOf(roleArr);// 获取roleArr在roleArr中的位置
              // 获取选中的role
              List<Map<String, Object>> selectRoleArr = new LinkedList<Map<String, Object>>();
              for (Map.Entry<String, Object> entryRoleArr : roleArr.entrySet()) {

                if (entryRoleArr.getKey().equals("id")
                    && entryRoleArr.getValue().equals(tocRole.getRoleId())) {
                  selectRoleArr.add(roleArr);
                  break;
                }
              }
              if (selectRoleArr.size() > 0) {// 判断有选中
                findIt = true;// 有选中的role
                roleArr.put("id", tocRole.getRoleId());
                // 获取role中privilegs并存放在了list中
                List<Map<String, Object>> privilegs = (List<Map<String, Object>>) roleArr
                    .get("privilegs");
                boolean findPrivi = false;// 设置是否有被选中的 privilegs
                for (int j = 0; j < privilegs.size(); j++) {
                  // 获取privilegs中的每一项，并存放在map中
                  Map<String, Object> priMap = privilegs.get(j);
                  for (String pkey : priMap.keySet()) {
                    // 判断privMap中是否有id并且id的值是否等于获取到的值
                    if (pkey.equals("id") && priMap.get(pkey).equals(tocRole.getPrivilegId())) {
                      findPrivi = true;// 发现被选中的权限
                      // 获取privilegs中的resource，并保存在map中
                      Map<String, Object> resource = (Map<String, Object>) priMap.get("resource");
                      if (resource.containsKey("layers")) {// 如果resource包含layers
                        List<Map<String, Object>> layers = (List<Map<String, Object>>) resource
                            .get("layers");
                        layers.remove("layers");// 移除layers
                        layers = new ArrayList();// 创建一个新的layers
                        for (int ni = 0; ni < priviMap.keySet().size(); ni++) {
                          Map<String, Object> singleLayer = new LinkedHashMap<String, Object>();
                          List priviMapKey = new ArrayList();
                          for (String priviKey : priviMap.keySet()) {
                            priviMapKey.add(priviKey);
                          }
                          // 把获取到privi放入到singleLayer中
                          singleLayer.put("id", priviMapKey.get(ni));
                          singleLayer.put("privi", priviMap.get(priviMapKey.get(ni)));
                          List initalMapKey = new ArrayList<String>();
                          for (Object object : initalMap.keySet()) {
                            initalMapKey.add(object);
                          }
                          // 判断是否initalMapKey.get(ni)是否为空，如若为空则inital为false
                          if (initalMap.get(initalMapKey.get(ni)).equals("")) {
                            singleLayer.put("inital", false);
                          } else {
                            // 否则为Boolean.parseBoolean((String)
                            // initalMap.get(initalMapKey.get(ni)))
                            singleLayer.put("inital",
                                Boolean.parseBoolean((String) initalMap.get(initalMapKey.get(ni))));
                          }
                          layers.add(singleLayer);// 把singleLayer放入到layers中
                        }
                        // 把layers添加到layers
                        resource.put("layers", layers);
                      }
                      break;
                    }
                  }
                }
                if (!findPrivi) {// 如果没有权限，则创建一个新的权限，并添加到集合privilegs
                  Map<String, Object> newPrivilegs = new LinkedHashMap<String, Object>();
                  newPrivilegs.put("id", tocRole.getPrivilegId());
                  Map<String, Object> resource = new LinkedHashMap<String, Object>();
                  List<Map<String, Object>> layers = new ArrayList<Map<String, Object>>();
                  for (int ni = 0; ni < priviMap.keySet().size(); ni++) {
                    Map<String, Object> singleLayer = new LinkedHashMap<String, Object>();
                    List priviMapKey = new ArrayList();
                    for (String priviKey : priviMap.keySet()) {
                      priviMapKey.add(priviKey);
                    }
                    singleLayer.put("id", priviMapKey.get(ni));
                    singleLayer.put("privi", priviMap.get(priviMapKey.get(ni)));
                    List initalMapKey = new ArrayList<String>();
                    for (Object object : initalMap.keySet()) {
                      initalMapKey.add(object);
                    }

                    if (initalMap.get(initalMapKey.get(ni)).equals("")) {
                      singleLayer.put("inital", false);
                    } else {
                      singleLayer.put("inital",
                          Boolean.parseBoolean((String) initalMap.get(initalMapKey.get(ni))));
                    }
                    layers.add(singleLayer);
                  }
                  resource.put("layers", layers);
                  newPrivilegs.put("resource", resource);
                  privilegs.add(newPrivilegs);
                }
                roles.set(index, roleArr);// 替换原来的roleArr
                arr.put(key, roles);// 放入到roles中
                break;
              }
            }
            if (findIt == false) {// 如果没有发现该角色拥有权限，则创建一个新的角色，并保存相应的权限
              Map<String, Object> newRole = new LinkedHashMap<String, Object>();
              newRole.put("id", tocRole.getRoleId());
              List privilegsList = new LinkedList();
              Map<String, Object> privilegs = new LinkedHashMap<String, Object>();
              privilegs.put("id", tocRole.getPrivilegId());
              Map<String, Object> resource = new LinkedHashMap<String, Object>();
              List<Map<String, Object>> layers = new ArrayList<Map<String, Object>>();
              for (int ni = 0; ni < priviMap.keySet().size(); ni++) {
                Map<String, Object> singleLayer = new LinkedHashMap<String, Object>();
                List<String> priviMapKey = new ArrayList<String>();
                for (String priviKey : priviMap.keySet()) {
                  priviMapKey.add(priviKey);
                }
                singleLayer.put("id", priviMapKey.get(ni));
                singleLayer.put("privi", priviMap.get(priviMapKey.get(ni)));
                List initalMapKey = new ArrayList<String>();
                for (Object object : initalMap.keySet()) {
                  initalMapKey.add(object);
                }
                if (initalMap.get(initalMapKey.get(ni)).equals("")) {
                  singleLayer.put("inital", false);
                } else {
                  singleLayer.put("inital",
                      Boolean.parseBoolean((String) initalMap.get(initalMapKey.get(ni))));
                }
                layers.add(singleLayer);
              }
              resource.put("layers", layers);
              privilegs.put("resource", resource);
              privilegsList.add(privilegs);
              newRole.put("privilegs", privilegsList);
              roles.add(newRole);
            }
            break;
          }
        }
      }
    }
    // 把获取到的修改后的数据转化为json
    String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(arrList);
    // 截取dataJson中的首尾，并转为string
    String data = dataJson.toString().substring(1, (dataJson.toString().length() - 1));
    Util.writeRoleJson(data);// 写入到config.json中
  }

}
