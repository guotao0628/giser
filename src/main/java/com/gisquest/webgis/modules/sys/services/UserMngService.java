package com.gisquest.webgis.modules.sys.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Properties;

import org.apache.zookeeper.KeeperException;

import com.gisquest.platform.common.utils.FileUtils;
import com.gisquest.webgis.util.Util;

/**
 * 用户管理服务
 * 
 * @author Jisj1
 *
 */
public class UserMngService {

  /** 配置文件加载器 */
  private Properties prop;
  private static final String ADMIN_PROP = "/admin.properties";

  /**
   * 构造函数用于初始化
   */
  public UserMngService() {
    /**
     * 读取admin.properties配置文件
     */
    prop = new Properties();
    InputStream is = this.getClass().getResourceAsStream(ADMIN_PROP);
    LocalConfigService lcService = new LocalConfigService();
    Boolean useZK = lcService.getZKState(Util.ROOT_PATH);
    try {
      if (useZK) {
        byte[] data = Util.getDataFromZK(ADMIN_PROP);
        if (data != null) {
          is = new ByteArrayInputStream(data);
        }
      }
    } catch (KeeperException e) {

    } finally {
      prop = new Properties();
      try {
        prop.load(is);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * 获取配置文件中的用户名
   * 
   * @return
   */
  public String getUsername() {
    return prop.getProperty("username");
  }

  /**
   * 获取配置文件中的密码
   * 
   * @return 解密后的密码
   */
  public String getCurrentPwd() {
    return Util.decrypt(prop.getProperty("password"));
  }

  /**
   * 修改密码服务
   * 
   * @param oldPwd
   *          原先的密码
   * @param newPwd
   *          要修改的密码
   * @return
   */
  public String changePwd(String oldPwd, String newPwd) {
    // 1. 判断旧密码是否正确
    OutputStream fos = null;
    String filepath = null;
    try {
      if (!oldPwd.equals(getCurrentPwd())) {
        // 2. 若不正确，则返回“error”
        return "error";
      } else {
        // 3. 若正确，则修改密码，并返回"success"
        prop.setProperty("password", Util.encrypt(newPwd));
        filepath = URLDecoder.decode(this.getClass().getResource(ADMIN_PROP).getFile(), "utf-8");
        fos = new FileOutputStream(filepath);
        prop.store(fos, "");
        return "success";
      }
    } catch (IOException e) {
      throw new RuntimeException(e.toString() + "admin.properties配置文件读取失败,路径为：" + filepath);
    } finally {
      if (fos != null) {
        try {
          fos.flush();
          fos.close();
          LocalConfigService lcService = new LocalConfigService();
          Boolean useZK = lcService.getZKState(Util.ROOT_PATH);
          if (useZK) {
            filepath = URLDecoder.decode(this.getClass().getResource(ADMIN_PROP).getFile(),
                "utf-8");
            /* 如果使用zk，则存入zk */
            File file = new File(filepath);
            byte[] data = FileUtils.readFileToByteArray(file);
            Util.setDataToZK(data, ADMIN_PROP);
          }
        } catch (IOException e) {
          throw new RuntimeException(e.toString() + "admin.properties配置文件读取失败,路径为：" + filepath);
        }
      }
    }
  }
}
