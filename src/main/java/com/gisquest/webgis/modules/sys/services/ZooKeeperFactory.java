package com.gisquest.webgis.modules.sys.services;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

/**
 * ZooKeeper单例模式
 * 
 * @author Jisj1
 *
 */
public class ZooKeeperFactory {
    /**
     * ZooKeeper对象
     */
    private static ZooKeeper ZOOKEEPER = null;

    /**
     * 获取zookeeper实例
     * 
     * @return
     */
    public static ZooKeeper getInstance() {
        if (ZOOKEEPER == null) {
            new ZooKeeperFactory();
            return ZOOKEEPER;
        }
        return ZOOKEEPER;
    }

    private ZooKeeperFactory() {
        try {
            ZOOKEEPER = new ZooKeeper("127.0.0.1:2181", 500000, new ZooKeeperWatcher());
            /* 监听newImgNode节点 */
            ZOOKEEPER.exists("/newImgNode", true);
            List<String> children = ZOOKEEPER.getChildren("/UploadImgs", true);
            for (String child : children) {
                String zNode = "/UploadImgs/" + child;
                ZOOKEEPER.exists(zNode, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
