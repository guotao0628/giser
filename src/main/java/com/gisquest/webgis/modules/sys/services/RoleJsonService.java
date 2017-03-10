package com.gisquest.webgis.modules.sys.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gisquest.webgis.modules.sys.entity.Privilege;
import com.gisquest.webgis.modules.sys.entity.Role;
import com.gisquest.webgis.util.Util;
import com.google.gson.GsonBuilder;

/**
 * role.json配置服务
 * 
 * @author Jisj1
 *
 */
public class RoleJsonService {

    /**
     * 获取role.json下role的集合
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getRoleList() {
        // 1. 读取role.json
        String roleJson = Util.getRoleJson();
        roleJson = Util.addCorrectFormat(roleJson); // 格式化config.json
        List<Map<String, Object>> roleJsonList = new GsonBuilder().create().fromJson(roleJson, List.class);// 把config.json转为list
        return roleJsonList;
    }

    /**
     * 根据数据库删除多余的角色和权限
     * 
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    public void removeInvalidInfo() throws SQLException {
        // 1. 获取role集合
        List<Map<String, Object>> roleJsonList = getRoleList();
        List<Map<String, Object>> roleList = new ArrayList<>();
        if (roleJsonList.size() > 0) {
            Map<String, Object> roleJsonMap = roleJsonList.get(0);
            roleList = (List<Map<String, Object>>) roleJsonMap.get("roles");
        }
        // 2. 根据政务平台，获取所有角色
        BasicConfigService service = new BasicConfigService();
        String dzzwVersion = service.getCurrentDzzwVersion();
        RightMngService rightMngService = new RightMngService();
        switch (dzzwVersion) {
        case "java":
            String allRoles = rightMngService.getAllRoles();
            List<Map<String, Object>> allRolesList = new GsonBuilder().create().fromJson(allRoles, List.class);
            for (int i = roleList.size() - 1; i >= 0; i--) {
                boolean isRoleFound = false;
                for (Map<String, Object> roleInDB : allRolesList) {
                    // role.json中的id
                    String roleId = (String) roleList.get(i).get("id");
                    List<Map<String, Object>> privilegsList = (List<Map<String, Object>>) roleList
                            .get(i).get("privilegs");
                    // 判断role.json中的id是否存在于roleInDB中
                    if (roleInDB.get("id").equals(roleId)) {
                        // 若找到了,获取指定id的所有权限
                        String privis = rightMngService.getPrivilegs(roleId);
                        Map<String, Object> privisInfoMap = new GsonBuilder().create().fromJson(privis, Map.class);
                        if (privisInfoMap.size() > 0) {
                            List<Map<String, Object>> privisList = (List<Map<String, Object>>) privisInfoMap
                                    .get("value");
                            for (int j = privilegsList.size() - 1; j >= 0; j--) {
                                boolean isPriviFound = false;
                                for (Map<String, Object> privisInDB : privisList) {
                                    // 判断role.json中权限的id是否存在于privisInDB中
                                    if (privisInDB.get("id")
                                            .equals(privilegsList.get(j).get("id"))) {
                                        // 若找到了,跳出遍历
                                        isPriviFound = true;
                                        break;
                                    }
                                }
                                // 删除role.json无效权限
                                if (!isPriviFound) {
                                    // 遍历完privisInDB仍没找到，则删除priviInJson中的privi
                                    privilegsList.remove(j);
                                }
                            }
                        }
                        isRoleFound = true;
                        break;
                    }
                }
                // 删除role.json无效角色
                if (!isRoleFound) {
                    // 遍历完roleInDB仍没找到，则删除roleInJson中的role
                    roleList.remove(i);
                }
            }
            break;
        case ".net":
            List<Role> dotnetRoles = rightMngService.getDotnetAllRoles();
            for (int i = roleList.size() - 1; i >= 0; i--) {
                boolean isRoleFound = false;
                for (Role roleInDB : dotnetRoles) {
                    // role.json中的id
                    String roleId = (String) roleList.get(i).get("id");
                    List<Map<String, Object>> privilegsList = (List<Map<String, Object>>) roleList
                            .get(i).get("privilegs");
                    // 判断role.json中的id是否存在于roleInDB中
                    if (roleInDB.getId().equals(roleId)) {
                        // 若找到了,获取指定id的所有权限
                        List<Privilege> privileges = roleInDB.getPrivileges();
                        for (int j = privilegsList.size() - 1; j >= 0; j--) {
                            boolean isPriviFound = false;
                            for (Privilege privisInDB : privileges) {
                                // 判断role.json中权限的id是否存在于privisInDB中
                                if (privisInDB.getPrivilegeId()
                                        .equals(privilegsList.get(j).get("id"))) {
                                    // 若找到了,跳出遍历
                                    isPriviFound = true;
                                    break;
                                }
                            }
                            // 删除role.json无效权限
                            if (!isPriviFound) {
                                // 遍历完privisInDB仍没找到，则删除priviInJson中的privi
                                privilegsList.remove(j);
                            }
                        }
                        isRoleFound = true;
                        break;
                    }
                }
                // 删除role.json无效角色
                if (!isRoleFound) {
                    // 遍历完roleInDB仍没找到，则删除roleInJson中的role
                    roleList.remove(i);
                }
            }
            break;
        default:
            break;
        }
        // 保存role.json
        String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(roleJsonList);
        String data = dataJson.substring(1, (dataJson.toString().length() - 1));
        Util.writeRoleJson(data);
    }

    /**
     * 删除role.json中的指定id的图层
     * 
     * @param id
     *            图层id
     */
    @SuppressWarnings("unchecked")
    public void deleteTocNode(String id) {
        // 1. 获取role集合
        List<Map<String, Object>> roleJsonList = getRoleList();
        List<Map<String, Object>> roleList = new ArrayList<>();
        if (roleJsonList.size() > 0) {
            Map<String, Object> roleJsonMap = roleJsonList.get(0);
            roleList = (List<Map<String, Object>>) roleJsonMap.get("roles");
        }
        // 遍历role集合
        for (Map<String, Object> role : roleList) {
            List<Map<String, Object>> privileges = (List<Map<String, Object>>) role
                    .get("privilegs");
            for (Map<String, Object> privilege : privileges) {
                Map<String, Object> resourceMap = (Map<String, Object>) privilege.get("resource");
                List<Map<String, Object>> layers = (List<Map<String, Object>>) resourceMap
                        .get("layers");
                if (layers != null) {
                    for (int i = layers.size() - 1; i >= 0; i--) {
                        if (layers.get(i).get("id").toString().contains(id)) {
                            layers.remove(i);
                        }
                    }
                }
            }
        }
        // 保存role.json
        String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(roleJsonList);
        String data = dataJson.substring(1, (dataJson.toString().length() - 1));
        Util.writeRoleJson(data);
    }

    /**
     * 修改role.json中相应的图层类型
     * 
     * @param id
     *            图层id
     * @param type
     *            图层类型
     */
    @SuppressWarnings("unchecked")
    public void modifyTocNode(String id, String type, String group) {
        // 如果不是矢量，则删除role.json中相应图层的可删除，可保存的图层，同时将"privi"值改为""
        if (!type.equals("dynamic") || !group.equals("2")) {
            // 1. 获取role集合
            List<Map<String, Object>> roleJsonList = getRoleList();
            List<Map<String, Object>> roleList = new ArrayList<>();
            if (roleJsonList.size() > 0) {
                Map<String, Object> roleJsonMap = roleJsonList.get(0);
                roleList = (List<Map<String, Object>>) roleJsonMap.get("roles");
            }
            // 遍历role集合
            for (Map<String, Object> role : roleList) {
                List<Map<String, Object>> privileges = (List<Map<String, Object>>) role
                        .get("privilegs");
                for (Map<String, Object> privilege : privileges) {
                    Map<String, Object> resourceMap = (Map<String, Object>) privilege
                            .get("resource");
                    List<Map<String, Object>> layers = (List<Map<String, Object>>) resourceMap
                            .get("layers");
                    if (layers != null) {
                        for (int i = layers.size() - 1; i >= 0; i--) {
                            String layerId = (String) layers.get(i).get("id");
                            if (layerId.equals(id)) {
                                layers.get(i).put("privi", "");
                            } else if (layerId.equals(id + "_del")) {
                                layers.remove(i);
                            } else if (layerId.equals(id + "_save")) {
                                layers.remove(i);
                            }
                        }
                    }
                }
            }
            // 保存role.json
            String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(roleJsonList);
            String data = dataJson.substring(1, (dataJson.toString().length() - 1));
            Util.writeRoleJson(data);
        }
    }
}
