package com.gisquest.webgis.modules.sys.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;

import com.gisquest.platform.common.config.Global;
import com.gisquest.platform.common.utils.PropertiesLoader;
import com.gisquest.platform.common.utils.RestUtils;
import com.gisquest.webgis.modules.sys.entity.FuncItem;
import com.gisquest.webgis.modules.sys.entity.Privilege;
import com.gisquest.webgis.modules.sys.entity.Role;
import com.gisquest.webgis.modules.sys.entity.RoleGroup;
import com.gisquest.webgis.util.JdbcUtil;
import com.google.gson.GsonBuilder;

/**
 * 权限管理服务层
 * 
 * @author Jisj1
 *
 */
public class RightMngService {
    /** 政务平台服务地址入口 */
    private String baseurl;
    /** 服务登陆用户名 */
    private String username;
    /** 服务登陆密码 */
    private String password;
    /** queryRunner */
    private QueryRunner queryRunner;

    /**
     * 构造方法，用于初始化
     */
    public RightMngService() {
        PropertiesLoader loader = new PropertiesLoader("config.properties");
        baseurl = loader.getProperty("platform.rest.baseurl");
        username = loader.getProperty("platform.rest.username");
        password = loader.getProperty("platform.rest.password");
        queryRunner = new QueryRunner(JdbcUtil.getDataSource());
    }

    /**
     * 获取所有角色
     * 
     * @return 返回角色集合json串
     */
    @SuppressWarnings("unchecked")
    public String getAllRoles() {
        String allRoles = (RestUtils.get(baseurl + "rolemgr/roles-all", username, password)
                .split(Global.REST_RESPONSE_SPLIT))[1];
        List<Map<String, Object>> allRolesList = new GsonBuilder().create().fromJson(allRoles, List.class);
        String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(allRolesList);
        allRoles = "[" + dataJson.substring(1, (dataJson.toString().length() - 1)) + "]";
        return allRoles;
    }

    /**
     * 获取所有子系统
     */
    @SuppressWarnings("unchecked")
    public String getSubsystems() {
        String subSystems = (RestUtils
                .get(baseurl + "rolemgr/sync-roles-group-tree/null/null", username, password)
                .split(Global.REST_RESPONSE_SPLIT))[1];
        List<Map<String, Object>> subSystemsList = new GsonBuilder().create().fromJson(subSystems, List.class);
        String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(subSystemsList);
        String subsystemAll = "[" + dataJson.substring(1, (dataJson.toString().length() - 1)) + "]";
        return subsystemAll;
    }

    /**
     * 获取指定子系统下所有的角色组
     * 
     * @param id
     *            子系统id
     */
    @SuppressWarnings("unchecked")
    public String getRolegroups(String id) {
        // 1. 先得到子系统下的所有角色组id
        String roleGroupIds = (RestUtils
                .get(baseurl + "rolemgr/get-allrolegroupidlist/" + id, username, password)
                .split(Global.REST_RESPONSE_SPLIT))[1];
        // 2. 然后根据id获取对应的角色组名称
        String[] roleGroupIdsArray = roleGroupIds.substring(1, roleGroupIds.length() - 1)
                .split(",");
        // 3. 遍历角色组，获取每个角色组的角色成员roles的id
        List<Map<String, Object>> roleGroups = new ArrayList<>();
        for (String roleGroupId : roleGroupIdsArray) {
            if (!"".equals(roleGroupId)) {
                // 4. 通过roleGroupId获取roleGroupInfo
                String roleGroupInfoString = (RestUtils.get(
                        baseurl + "rolemgr/roles-group-info/"
                                + roleGroupId.substring(1, roleGroupId.length() - 1),
                        username, password)).split(Global.REST_RESPONSE_SPLIT)[1];
                Map<String, Object> roleGroupInfo = new GsonBuilder().create().fromJson(roleGroupInfoString, Map.class);
                roleGroups.add(roleGroupInfo);
            }
        }
        String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(roleGroups);
        String roleGroupsAll = "[" + dataJson.substring(1, (dataJson.toString().length() - 1))
                + "]";
        return roleGroupsAll;
    }

    /**
     * 获取指定角色组下所有的角色成员
     * 
     * @param id
     *            角色组id
     * @return 返回指定角色组下所有的角色成员json串
     */
    @SuppressWarnings("unchecked")
    public String getRoles(String id) {
        // 1. 先获取指定角色组下所有角色的id
        String roleIds = (RestUtils.post(baseurl + "rolemgr/get-allroleidlist-bygroupid", username,
                password, "[\"" + id + "\"]").split(Global.REST_RESPONSE_SPLIT))[1];
        // 2. 然后根据角色的id获取对应的角色信息
        String[] roleIdsArray = roleIds.substring(1, roleIds.length() - 1).split(",");
        List<Map<String, Object>> roleList = new ArrayList<>();
        for (String roleId : roleIdsArray) {
            if (!"".equals(roleId)) {
                String roleInfo = (RestUtils
                        .get(baseurl + "rolemgr/roles/" + roleId.substring(1, roleId.length() - 1),
                                username, password)
                        .split(Global.REST_RESPONSE_SPLIT))[1];
                // 7. 把roleInfo存入roleGroupInfo
                Map<String, Object> roleMap = new GsonBuilder().create().fromJson(roleInfo, Map.class);
                roleList.add(roleMap);
            }
        }
        String dataJson = new GsonBuilder().setPrettyPrinting().create().toJson(roleList);
        String rolesAll = "[" + dataJson.substring(1, (dataJson.toString().length() - 1)) + "]";
        return rolesAll;
    }

    /**
     * 根据角色id获取权限
     * 
     * @param roleId
     * @return 返回角色拥有的权限
     */
    public String getPrivilegs(String roleId) {
        String rolePrid = "{\"" + "sort" + "\":\"" + "" + "\",\"" + "order" + "\":\"" + "" + "\",\""
                + "start" + "\":\"" + "0" + "\",\"" + "pageSize" + "\":\"" + "15" + "\",\"" + ""
                + "pageNum" + "\":\"0" + "" + "\",\"" + "roleId" + "\":\"" + roleId + "\"}";
        String serviceUrl = baseurl + "rightsmgr/role-userdefine-pri-grid";
        String result = RestUtils.post(serviceUrl, username, password, rolePrid);
        String[] array = result.split(Global.REST_RESPONSE_SPLIT);
        String rolesRight = array[1];
        return rolesRight;
    }

    /**
     * 在.net版本数据库中，获取所有角色组
     * 
     * @return 返回所有角色组集合
     * @throws SQLException
     */
    public List<Role> getRolegroups() throws SQLException {
        List<Role> roleList = new ArrayList<>();
        String sql = "SELECT ROLE_GROUP_ID id, NAME, SEQUENCE sequence FROM bt_role_group";
        List<RoleGroup> roleGroups = queryRunner.query(sql, new BeanListHandler<>(RoleGroup.class));
        sql = "SELECT ROLE_ID id,NAME name, ROLE_GROUP_ID roleGroupId, SEQUENCE sequence FROM bt_role where role_group_id=0";
        List<Role> roles = queryRunner.query(sql, new BeanListHandler<>(Role.class));
        roleList.addAll(roleGroups);
        roleList.addAll(roles);
        return roleList;
    }

    /**
     * 在.net版本数据库中，获取指定角色组下的所有角色
     * 
     * @return 返回指定角色组下的角色集合
     * @throws SQLException
     */
    public List<Role> getDotnetRoles(String id) throws SQLException {
        String sql = "SELECT ROLE_ID id,NAME,SEQUENCE sequence FROM bt_role where role_group_id=?";
        List<Role> roles = queryRunner.query(sql, new BeanListHandler<>(Role.class), id);
        for (Role role : roles) {
            List<FuncItem> funcItems = getDotnetPriv(role.getId());
            List<Privilege> privilegs = new ArrayList<>();
            for (FuncItem funcItem : funcItems) {
                privilegs.addAll(funcItem.getPrivileges());
            }
            role.setPrivileges(privilegs);
        }
        return roles;
    }

    /**
     * 在.net版本数据库中，获取所有角色
     * 
     * @return
     * @throws SQLException
     */
    public List<Role> getDotnetAllRoles() throws SQLException {
        List<Role> roles = new ArrayList<>();
        List<Role> rolegroups = getRolegroups();
        for (Role role : rolegroups) {
            if (role.getRoleGroupId() == null) {
                roles.addAll(getDotnetRoles(role.getId()));
            } else if (role.getRoleGroupId().equals("0")) {
                List<FuncItem> funcItems = getDotnetPriv(role.getId());
                List<Privilege> privilegs = new ArrayList<>();
                for (FuncItem funcItem : funcItems) {
                    privilegs.addAll(funcItem.getPrivileges());
                }
                role.setPrivileges(privilegs);
                roles.add(role);
            }
        }
        return roles;
    }

    /**
     * 根据用户id获取所有用户角色
     * 
     * @param userId
     *            用户id
     * @return
     * @throws SQLException
     */
    public List<Role> getDotnetUserRoles(String userId) throws SQLException {
        List<Role> allRoles = new ArrayList<Role>();
        String sql = "SELECT ROLE_ID FROM BT_USER_ROLE where USER_ID= ?";
        List<Object> roleIds = queryRunner.query(sql, new ColumnListHandler<>("ROLE_ID"), userId);
        sql = "SELECT ROLE_ID id,NAME FROM bt_role where role_id= ? ";
        for (Object roleId : roleIds) {
            List<Role> roles = queryRunner.query(sql, new BeanListHandler<Role>(Role.class),
                    roleId);
            allRoles.addAll(roles);
        }
        return allRoles;
    }

    /**
     * 根据用户id获取所有用户角色
     * 
     * @param userId
     *            用户id
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Role> getUserRoles(String userId) {
        List<Role> userRoles = new ArrayList<Role>();
        String baseurl = Global.getConfig("platform.rest.baseurl");
        String username = Global.getConfig("platform.rest.username");
        String password = Global.getConfig("platform.rest.password");
        String serviceUrl = baseurl + "/identity/users-roles/" + userId;
        String result = RestUtils.get(serviceUrl, username, password);
        String[] array = result.split(Global.REST_RESPONSE_SPLIT);
        String user = array[1];
        Map<String, Object> userMap = new GsonBuilder().create().fromJson(user,
                Map.class);
        for (String key : userMap.keySet()) {
            if (key.equals("roles")) {
                List<Map<String, Object>> roles = (List<Map<String, Object>>) userMap.get(key);
                for (Map<String, Object> roleMap : roles) {
                    Role role = new Role();
                    role.setId((String) roleMap.get("id"));
                    role.setName((String) roleMap.get("name"));
                    role.setRoleGroupId((String) roleMap.get("roleGroupId"));
                    role.setDescription((String) roleMap.get("description"));
                    role.setSequence(Integer.parseInt((String) roleMap.get("sequence")));
                    role.setRoleCode((String) roleMap.get("roleCode"));
                    serviceUrl = baseurl + "rightsmgr/role-userdefine-pri-grid";
                    String rolePrid = "{\"" + "sort" + "\":\"" + "" + "\",\"" + "order" + "\":\""
                            + "" + "\",\"" + "start" + "\":\"" + "0" + "\",\"" + "pageSize"
                            + "\":\"" + "15" + "\",\"" + "" + "pageNum" + "\":\"0" + "" + "\",\""
                            + "roleId" + "\":\"" + role.getId() + "\"}";
                    result = RestUtils.post(serviceUrl, username, password, rolePrid);
                    array = result.split(Global.REST_RESPONSE_SPLIT);
                    String rolePrivs = array[1];
                    List<Privilege> privileges = new ArrayList<>();
                    Map<String, Object> privsMap =new GsonBuilder().create().fromJson(rolePrivs, Map.class);
                    for (String privsMapKey : privsMap.keySet()) {
                        if (privsMapKey.equals("value")) {
                            List<Map<String, Object>> values = (List<Map<String, Object>>) privsMap
                                    .get(privsMapKey);
                            for (Map<String, Object> value : values) {
                                Privilege p = new Privilege();
                                p.setPrivilegeId((String) value.get("id"));
                                p.setPrivilegeCode((String) value.get("privilegeCode"));
                                p.setPrivilegeName((String) value.get("funcName"));
                                p.setFuncItemName((String) value.get("funcItem"));
                                privileges.add(p);
                            }
                            break;
                        }
                    }
                    role.setPrivileges(privileges);
                    userRoles.add(role);
                }
                break;
            }
        }
        return userRoles;
    }

    /**
     * 根据roleId获取该自定义权限（权限组->权限）
     * 
     * @param roleId
     *            角色id
     * @return 返回指定roleId的权限
     * @throws SQLException
     */
    public List<FuncItem> getDotnetPriv(String roleId) throws SQLException {
        List<FuncItem> funcItems = new ArrayList<FuncItem>();
        List<Privilege> privileges = new ArrayList<Privilege>();
        String sql = "SELECT FUNCITEM_ID funcItemId, NAME funcItemName"
                + " FROM bt_funcitem f WHERE f.funcgroup_id=0 and FUNCITEM_ID in "
                + "(SELECT FUNCITEM_ID FROM bt_privilege p WHERE p.PRIVILEGE_ID in "
                + "(SELECT PRIVILEGE_ID FROM BT_ROLE_PRIVILEGE rp WHERE rp.ROLE_ID = ? ))";
        funcItems = queryRunner.query(sql, new BeanListHandler<FuncItem>(FuncItem.class), roleId);
        sql = "SELECT PRIVILEGE_ID privilegeId,NAME privilegeName"
                + " FROM BT_PRIVILEGE where FuncItem_Id = ? and PRIVILEGE_ID in "
                + "(SELECT PRIVILEGE_ID FROM BT_ROLE_PRIVILEGE rp WHERE rp.ROLE_ID = ?)";
        for (FuncItem funcItem : funcItems) {
            privileges = queryRunner.query(sql, new BeanListHandler<Privilege>(Privilege.class),
                    funcItem.getFuncItemId(), roleId);
            funcItem.setPrivileges(privileges);
        }
        return funcItems;

    }

}
