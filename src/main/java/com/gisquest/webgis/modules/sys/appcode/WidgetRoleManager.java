
package com.gisquest.webgis.modules.sys.appcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.gisquest.webgis.modules.sys.entity.WidgetRole;
import com.gisquest.webgis.util.Util;
import com.google.gson.GsonBuilder;

/**
 * 该类的作用：处理webGis中权限管理中的插件设定功能的请求
 * 
 * @author Administrator
 *
 */
public class WidgetRoleManager {
    public WidgetRoleManager() {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void saveWidgetRole(WidgetRole widgetRole) {
        String json = Util.getRoleJson();// 获取json字符串
        json = Util.addCorrectFormatRole(json);// 格式化json数据
        // 把获取到的PreWidgetIds转化我集合
        List<String> preWids = Arrays.asList(widgetRole.getPreWidgetIds());
        // 获取到的WidgetPoolIds转化为集合
        List<String> poolIds = Arrays.asList(widgetRole.getWidgetPoolIds());
        // 把json格式转为list
        List<Map<String, Object>> arrList = new GsonBuilder().create().fromJson(json, List.class);
        if (arrList.size() > 0) {
            for (Map<String, Object> arr : arrList) {
                for (String key : arr.keySet()) {
                    if (key.equals("roles")) {// 判断key是否为roles
                        // 获取arr中value并以list形式保存
                        List<Map<String, Object>> roles = (List<Map<String, Object>>) arr.get(key);
                        boolean findIt = false;// 是否有roles
                        for (int i = 0; i < roles.size(); i++) {
                            // 获取roles中的每项并以map的形式保存
                            Map<String, Object> roleArr = roles.get(i);
                            int index = roles.indexOf(roleArr);// 活roles的下标
                            // 把选中的role保存在selectRoleArr中
                            List<Map<String, Object>> selectRoleArr = new ArrayList<>();
                            for (Map.Entry<String, Object> entry : roleArr.entrySet()) {
                                if (entry.getKey().equals("id")
                                        && entry.getValue().equals(widgetRole.getRoleId())) {
                                    selectRoleArr.add(roleArr);
                                    break;
                                }
                            }
                            if (selectRoleArr.size() > 0) {
                                findIt = true;// 证明role存在
                                roleArr.put("id", widgetRole.getRoleId());
                                // 获取roleArr中privilegs并保存在集合中
                                List<Map<String, Object>> privilegs = (List<Map<String, Object>>) roleArr
                                        .get("privilegs");
                                boolean findPrivi = false;// privilegs是否存在
                                for (int j = 0; j < privilegs.size(); j++) {
                                    // 获取key中的每一项，并保存在map中
                                    Map<String, Object> priDic = privilegs.get(j);
                                    for (String pkey : priDic.keySet()) {
                                        // 如果priDic的id等于获取的id的值
                                        if (pkey.equals("id") && priDic.get(pkey)
                                                .equals(widgetRole.getPrivilegId())) {
                                            findPrivi = true;// 说明privilegs存在
                                            // 获取privilege中的resource，并保存在map总
                                            Map<String, Object> resource = (Map<String, Object>) priDic
                                                    .get("resource");
                                            if (resource.containsKey("widgets")) {// 如果resource中包含widgets
                                                // 获取resource中的widgets，并保存在map中
                                                Map<String, Object> widgets = (Map<String, Object>) resource
                                                        .get("widgets");
                                                // 获取widgets中的preWidgets并保存在集合中
                                                List preWidgets = (ArrayList) widgets
                                                        .get("preWidgets");
                                                preWidgets.remove("preWidgets");// 移除已有的
                                                preWidgets = new ArrayList<>();
                                                List poolWidgets = (ArrayList) widgets
                                                        .get("poolWidgets");
                                                poolWidgets.remove("poolWidgets");// 移除已有的
                                                poolWidgets = new ArrayList<String>();
                                                for (int ni = 0; ni < preWids.size(); ni++) {
                                                    Map<String, Object> sWidget = new LinkedHashMap<>();
                                                    sWidget.put("id", preWids.get(ni));
                                                    // 创建一个新的preWidgets，把获取到的数据保存在里面
                                                    preWidgets.add(sWidget);
                                                }
                                                for (int ni = 0; ni < poolIds.size(); ni++) {
                                                    Map<String, Object> sWidget = new LinkedHashMap<>();
                                                    sWidget.put("id", poolIds.get(ni));
                                                    // 创建一个新的poolWidgets，把获取到的数据保存在里面
                                                    poolWidgets.add(sWidget);
                                                }
                                                widgets.put("preWidgets", preWidgets);
                                                widgets.put("poolWidgets", poolWidgets);
                                                resource.put("widgets", widgets);
                                            } else {// 如果不包含widgets，则创建一个新的widgets，把获取到的数据保存到里面
                                                Map<String, Object> widgets = new LinkedHashMap<>();
                                                ArrayList preWidgets = new ArrayList();
                                                ArrayList poolWidgets = new ArrayList();
                                                for (int ni = 0; ni < preWids.size(); ni++) {
                                                    Map<String, Object> sWidget = new LinkedHashMap<>();
                                                    sWidget.put("id", preWids.get(ni));
                                                    preWidgets.add(sWidget);

                                                }
                                                for (int ni = 0; ni < poolIds.size(); ni++) {
                                                    Map<String, Object> sWidget = new LinkedHashMap<>();
                                                    sWidget.put("id", poolIds.get(ni));
                                                    poolWidgets.add(sWidget);
                                                }
                                                widgets.put("preWidgets", preWidgets);
                                                widgets.put("poolWidgets", poolWidgets);
                                                resource.put("widgets", widgets);
                                            }
                                            break;
                                        }
                                    }
                                }
                                // 如果没有发现roleId有权限，则创建一个新的privileg，把获取到的数据保存到里面，并添加到privilegs中
                                if (!findPrivi) {
                                    Map<String, Object> newPirvilegs = new LinkedHashMap<>();
                                    newPirvilegs.put("id", widgetRole.getPrivilegId());
                                    Map<String, Object> resource = new LinkedHashMap<>();
                                    Map<String, Object> widgets = new LinkedHashMap<>();
                                    List preWidgets = new ArrayList();
                                    List poolWidgets = new ArrayList();
                                    for (int ni = 0; ni < preWids.size(); ni++) {
                                        Map<String, Object> sWidget = new LinkedHashMap<>();
                                        sWidget.put("id", preWids.get(ni));
                                        preWidgets.add(sWidget);
                                    }
                                    for (int ni = 0; ni < poolIds.size(); ni++) {
                                        Map<String, Object> sWidget = new LinkedHashMap<>();
                                        sWidget.put("id", poolIds.get(ni));
                                        poolWidgets.add(sWidget);
                                    }
                                    widgets.put("preWidgets", poolWidgets);
                                    widgets.put("poolWidgets", poolWidgets);
                                    resource.put("widgets", widgets);
                                    newPirvilegs.put("resource", resource);
                                    privilegs.add(newPirvilegs);
                                }
                                roles.set(index, roleArr);
                                arr.put(key, roles);
                                break;
                            }

                        }
                        // 如果没有发现这个roleId，则创建一个新的roleId，并把获取到的数据按照相应的形式保存起来
                        if (findIt == false) {
                            Map<String, Object> newRole = new LinkedHashMap<>();
                            newRole.put("id", widgetRole.getRoleId());
                            List privilegList = new ArrayList();
                            Map<String, Object> privilegs = new LinkedHashMap<>();
                            privilegs.put("id", widgetRole.getPrivilegId());
                            Map<String, Object> resource = new LinkedHashMap<>();
                            Map<String, Object> widgets = new LinkedHashMap<>();
                            List preWidgets = new ArrayList();
                            List poolWidgets = new ArrayList();
                            for (int ni = 0; ni < preWids.size(); ni++) {
                                Map<String, Object> sWidget = new LinkedHashMap<>();
                                sWidget.put("id", preWids.get(ni));
                                preWidgets.add(sWidget);

                            }
                            for (int ni = 0; ni < poolIds.size(); ni++) {
                                Map<String, Object> sWidget = new LinkedHashMap<>();
                                sWidget.put("id", poolIds.get(ni));
                                poolWidgets.add(sWidget);
                            }
                            widgets.put("preWidgets", preWidgets);
                            widgets.put("poolWidgets", poolWidgets);
                            resource.put("widgets", widgets);
                            privilegs.put("resource", resource);
                            privilegList.add(privilegs);
                            newRole.put("privilegs", privilegList);
                            roles.add(newRole);

                        }
                        break;
                    }
                }

            }
        }
        // 把获取到的修改后的数据转化为json
        String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(arrList);
        String data = dataJson.toString().substring(1, (dataJson.toString().length() - 1));
        Util.writeRoleJson(data);// 写入到role.json中
    }
}
