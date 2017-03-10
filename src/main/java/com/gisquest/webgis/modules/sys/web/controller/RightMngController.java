package com.gisquest.webgis.modules.sys.web.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gisquest.webgis.modules.sys.entity.FuncItem;
import com.gisquest.webgis.modules.sys.entity.Role;
import com.gisquest.webgis.modules.sys.services.RightMngService;
import com.gisquest.webgis.modules.sys.services.RoleJsonService;
import com.gisquest.webgis.util.Util;

/**
 * 控制权限管理
 * 
 * @author Jisj1
 */
@Controller
public class RightMngController {
    /** 请求体 */
    @Autowired
    private HttpServletRequest request;
    /** 基础配置service对象 */
    private RightMngService mngService;

    /**
     * 构造函数
     */
    public RightMngController() {
        mngService = new RightMngService();
        
    }

    /**
     * 读取role.json
     */
    @RequestMapping("/admin/readRoleJson")
    @ResponseBody
    public String readRoleJson() {
        Util.G_REQUEST = request;
        return Util.getRoleJson();
    }

    /**
     * 根据数据库更新role.json，删除无效角色和权限
     */
    @RequestMapping("/admin/updateRoleJson")
    @ResponseBody
    public void updateRoleJson() {
        Util.G_REQUEST = request;
        RoleJsonService service = new RoleJsonService();
        try {
            service.removeInvalidInfo();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取所有子系统
     * 
     * @return 返回子系统json串
     */
    @RequestMapping("/admin/rolemgr/subsystems")
    @ResponseBody
    public String getSubsystems() {
        return mngService.getSubsystems();
    }

    /**
     * 获取指定子系统下所有的角色组
     * 
     * @param id
     *            子系统id
     * @return 返回指定子系统下所有的角色组json串
     */
    @RequestMapping("/admin/rolemgr/rolegroups")
    @ResponseBody
    public String getRolegroups(String id) {
        return mngService.getRolegroups(id);
    }

    /**
     * 获取指定角色组下所有的角色
     * 
     * @param id
     *            角色组id
     * @return 返回指定角色组下所有的角色json串
     */
    @RequestMapping("/admin/rolemgr/roles")
    @ResponseBody
    public String getRoles(String id) {
        return mngService.getRoles(id);
    }

    /**
     * 根据roleId获取该自定义权限
     * 
     * @param roleId
     *            角色id
     * @return 返回指定角色的权限json串
     */
    @RequestMapping("/admin/rightsmgr/role-userdefine-pri-grid")
    @ResponseBody
    public String getPriv(String roleId) {
        return mngService.getPrivilegs(roleId);
    }

    /**
     * 在.net平台数据库中，获取所有角色组
     * 
     * @return
     */
    @RequestMapping("/admin/rolemgr/rolegroups_dotnet")
    @ResponseBody
    public List<Role> getRolegroups() {
        List<Role> rolegroups = new ArrayList<>();
        try {
            rolegroups = mngService.getRolegroups();
        } catch (SQLException e) {
            throw new RuntimeException("数据库访问失败");
        }
        return rolegroups;
    }

    /**
     * 在.net平台数据库中，获取指定角色组下的所有角色
     * 
     * @param id
     *            角色组id
     * @return 返回角色集合
     */
    @RequestMapping("/admin/rolemgr/roles_dotnet")
    @ResponseBody
    public List<Role> getDotnetRoles(String id) {
        List<Role> roles = new ArrayList<>();
        try {
            roles = mngService.getDotnetRoles(id);
        } catch (SQLException e) {
            throw new RuntimeException("数据库访问失败");
        }
        return roles;
    }

    /**
     * 在.net平台数据库中，获取指定角色的权限
     * 
     * @param roleId
     *            角色id
     * @return 返回指定角色的权限
     */
    @RequestMapping("/admin/rightsmgr/role-userdefine-pri-grid/.net/")
    @ResponseBody
    public List<FuncItem> getDotnetPriv(String roleId) {
        List<FuncItem> privs = new ArrayList<>();
        try {
            privs = mngService.getDotnetPriv(roleId);
        } catch (SQLException e) {
            throw new RuntimeException("数据库访问失败");
        }
        return privs;
    }
}
