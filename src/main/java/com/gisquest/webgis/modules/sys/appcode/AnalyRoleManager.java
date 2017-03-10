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

import com.gisquest.webgis.modules.sys.entity.AnalyRole;
import com.gisquest.webgis.util.Util;
import com.google.gson.GsonBuilder;

/**
 * 作用:处理权限管理下的角色自定义权限中的分析设定的权限
 * 
 * @author yedy
 *
 */
public class AnalyRoleManager {
    public AnalyRoleManager() {
        // 默认构造函数
    }

    /**
     * 处理analyRole
     * 
     * @param analyRole
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void saveAnalyRole(AnalyRole analyRole) {

        String json = Util.getRoleJson();// 获取json字符串
        json = Util.addCorrectFormatRole(json);// 格式化json为json形式
        List<String> anaIds = Arrays.asList(analyRole.getAnalyIds());// 获取analysIds，并转化为list
        // 把json数据转化为list
        List<Map<String, Object>> arrList = new GsonBuilder().create().fromJson(json, List.class);
        if (arrList.size() > 0) {// 判断arrList是否存在
            for (Map<String, Object> arr : arrList) {
                for (String key : arr.keySet()) {
                    if (key.equals("roles")) {// 判断arrlist的key是否roles
                        // 获取arr中的每一项，并转化为list集合
                        List<Map<String, Object>> roles = (List<Map<String, Object>>) arr.get(key);
                        boolean findIt = false;// 是否存在roles这个key
                        for (int i = 0; i < roles.size(); i++) {// 遍历roles
                            // 获取roles中的每项，并转化为map
                            Map<String, Object> roleArr = roles.get(i);
                            int index = roles.indexOf(roleArr);// 获取roleArr中的下标
                            List<Map<String, Object>> selectRoleArr = new LinkedList<Map<String, Object>>();
                            for (Map.Entry<String, Object> entryRoleArr : roleArr.entrySet()) {
                                if (entryRoleArr.getKey().equals("id") && entryRoleArr.getValue()
                                        .equals((analyRole.getRoleId()))) {
                                    selectRoleArr.add(roleArr);// 获取选中的roleArr,添加到selectRoleArr中
                                    break;
                                }
                            }
                            if (selectRoleArr.size() > 0) {// 如果选中role
                                findIt = true;// 说明role存在
                                roleArr.put("id", analyRole.getRoleId());// 把获取roleId放入到roleArr中
                                // 获取roleArr中的privileges，并转化为list
                                List<Map<String, Object>> privilegs = (List<Map<String, Object>>) roleArr
                                        .get("privilegs");
                                boolean findPrivi = false;// 是否有权限
                                for (int j = 0; j < privilegs.size(); j++) {// 遍历权限
                                    // 获取权限中的每项
                                    Map<String, Object> priDic = privilegs.get(j);
                                    for (String pkey : priDic.keySet()) {// 遍历权限
                                        // 如果权限中的id=获取到的权限的id
                                        if (pkey.equals("id") && priDic.get(pkey)
                                                .equals(analyRole.getPrivilegId())) {
                                            findPrivi = true;// 权限存在
                                            // 获取权限中的resource
                                            Map<String, Object> resource = (Map<String, Object>) priDic
                                                    .get("resource");
                                            if (resource.containsKey("analys")) {// 如果resource中包含analys
                                                // 获取analys并转化为集合
                                                List<Map<String, Object>> analys = (List<Map<String, Object>>) resource
                                                        .get("analys");
                                                analys = new ArrayList<Map<String, Object>>();// 清空已存在的analy
                                                for (int ni = 0; ni < anaIds.size(); ni++) {
                                                    Map<String, Object> sAnaly = new LinkedHashMap<String, Object>();
                                                    sAnaly.put("id", anaIds.get(ni));
                                                    analys.add(sAnaly);// 把获取到的数据添加到analys
                                                }
                                                resource.put("analys", analys);// 把更新的analy添加到resource中
                                            } else {// 如果analys不存在，创建一个新的anal用
                                                List<Map<String, Object>> analys = new ArrayList<Map<String, Object>>();
                                                for (int ni = 0; ni < anaIds.size(); ni++) {
                                                    Map<String, Object> sAnaly = new LinkedHashMap<String, Object>();
                                                    sAnaly.put("id", anaIds.get(ni));
                                                    analys.add(sAnaly);// 把获取到的analy添加到analy中
                                                }
                                                resource.put("analys", analys);// 把analys添加到resource
                                            }
                                            break;
                                        }
                                    }
                                }
                                if (!findPrivi) {// 如果没有发现权限，把获取到的权限添加到resource中
                                    Map<String, Object> newPrivilegs = new LinkedHashMap<String, Object>();
                                    newPrivilegs.put("id", analyRole.getPrivilegId());
                                    Map<String, Object> resources = new LinkedHashMap<String, Object>();
                                    List analys = new ArrayList();
                                    for (int ni = 0; ni < anaIds.size(); ni++) {
                                        Map<String, Object> sAnaly = new LinkedHashMap<String, Object>();
                                        sAnaly.put("id", anaIds.get(ni));
                                        analys.add(sAnaly);
                                    }
                                    resources.put("analys", analys);
                                    newPrivilegs.put("resource", resources);
                                    privilegs.add(newPrivilegs);
                                }
                                roles.set(index, roleArr);
                                arr.put(key, roles);
                                break;
                            }
                        }
                        // 如果没有发现这个角色的自定义权限，则创建新的roleId，并把获取到的analy放入到resource中
                        if (findIt == false) {
                            Map<String, Object> newRole = new LinkedHashMap<String, Object>();
                            newRole.put("id", analyRole.getRoleId());
                            List privilegsList = new ArrayList();
                            Map<String, Object> privilegs = new LinkedHashMap<String, Object>();
                            privilegs.put("id", analyRole.getPrivilegId());
                            Map<String, Object> resources = new LinkedHashMap<String, Object>();
                            List analys = new ArrayList();
                            for (int ni = 0; ni < anaIds.size(); ni++) {
                                Map<String, Object> sAnaly = new LinkedHashMap<String, Object>();
                                sAnaly.put("id", anaIds.get(ni));
                                analys.add(sAnaly);
                            }
                            resources.put("analys", analys);
                            privilegs.put("resource", resources);
                            privilegsList.add(privilegs);
                            newRole.put("privilegs", privilegsList);
                            roles.add(newRole);
                        }
                        break;
                    }
                }
            }
        }
        String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(arrList);
        // 截取dataJson中的首尾，并转为string
        String data = dataJson.toString().substring(1, (dataJson.toString().length() - 1));
        Util.writeRoleJson(data);// 写入到role.json中
    }

}
