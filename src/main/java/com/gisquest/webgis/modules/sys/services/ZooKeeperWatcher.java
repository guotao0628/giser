package com.gisquest.webgis.modules.sys.services;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;

import com.gisquest.platform.common.utils.FileUtils;
import com.gisquest.webgis.util.Util;

/**
 * zooKeeper监听器
 * 
 * @author Administrator
 *
 */
public class ZooKeeperWatcher implements Watcher {
    /** zookeeper对象 */
    private ZooKeeper zk = null;

    @Override
    public void process(WatchedEvent event) {
        try {
            zk = ZooKeeperFactory.getInstance();

            /* 如果UploadImgs删除了节点 */
            if (event.getType() == EventType.NodeDeleted) {
                System.out.println(event);
                if (event.getPath().startsWith("/UploadImgs")) {
                    String rootPath = Util.ROOT_PATH;
                    FileUtils.deleteFile(rootPath + "/admin" + event.getPath());
                }
            }
            /* 如果newImgNode值发生了改变，则监听值表示的节点 */
            if (event.getType() == EventType.NodeDataChanged
                    && event.getPath().equals("/newImgNode")) {
                String nodeName = new String(
                        zk.getData("/newImgNode", true, zk.exists("/newImgNode", true)), "utf-8");
                zk.exists("/UploadImgs/" + nodeName, true);
            }

            /* 如果/UploadImgs下新建了一个节点 */
            if (event.getType() == EventType.NodeCreated
                    && event.getPath().startsWith("/UploadImgs")) {
                String rootPath = Util.ROOT_PATH;
                String zNode = event.getPath();
                byte[] data = zk.getData(zNode, this, zk.exists(zNode, true));
                InputStream is = new ByteArrayInputStream(data);
                File destination = new File(rootPath, "admin" + zNode);
                FileUtils.copyInputStreamToFile(is, destination);
            }
            /* 如果/UploadImgs下更新了一个节点 */
            if (event.getType() == EventType.NodeDataChanged
                    && event.getPath().startsWith("/UploadImgs")) {
                String rootPath = Util.ROOT_PATH;
                String zNode = event.getPath();
                byte[] data = zk.getData(zNode, this, zk.exists(zNode, true));
                InputStream is = new ByteArrayInputStream(data);
                File destination = new File(rootPath, "admin" + zNode);
                FileUtils.copyInputStreamToFile(is, destination);
            }
        } catch (KeeperException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
