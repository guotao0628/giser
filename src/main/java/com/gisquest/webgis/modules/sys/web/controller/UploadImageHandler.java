/**
 * 
 */
package com.gisquest.webgis.modules.sys.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.gisquest.webgis.modules.sys.services.LocalConfigService;
import com.gisquest.webgis.util.Util;

/**
 * 该类作用为：实现图片上传上传功能
 * 
 * @author Jisj1
 *
 */

@Controller
public class UploadImageHandler {

    /**
     * 实现图片上传
     * 
     * @param myImage获取网页中的图片
     * @param request请求体
     * @param response相应体
     * @return realPath 上传图片的路径
     * 
     */
    @RequestMapping(value = "/admin/uploadImgs", method = RequestMethod.POST)
    @ResponseBody
    public String processRequest(@RequestParam(required = false, value = "file") MultipartFile file,
            HttpServletRequest request, HttpServletResponse response) {
        String imgPath = new String();
        if (file != null) {
            try {
                request.setCharacterEncoding("utf-8");
                response.setCharacterEncoding("utf-8");
                if (!file.isEmpty()) {
                    String mapPath = request.getSession().getServletContext()
                            .getRealPath("/admin/UploadImgs/");
                    String originalName = file.getOriginalFilename();
                    /* 获取文件后缀 */
                    String suffix = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
                    /* 指定上传文件的名称 */
                    String fileName = UUID.randomUUID().toString() + suffix;
                    /* 创建要上传的文件 */
                    File targetFile = new File(mapPath, fileName);
                    /* 上传图片 */
                    FileUtils.copyInputStreamToFile(file.getInputStream(), targetFile);
                    /* 如果使用zookeeper，则将图片放入zookeeper */
                    LocalConfigService lcService = new LocalConfigService();
                    String rootPath = request.getSession().getServletContext().getRealPath("/");
                    Boolean useZK = lcService.getZKState(rootPath);
                    if (useZK) {
                        Util.setDataToZK(fileName.getBytes("utf-8"), "/newImgNode");
                        Util.createDataToZK(file.getBytes(), "/UploadImgs/" + fileName);
                    }
                    imgPath = "UploadImgs/" + fileName;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return imgPath;
    }
}
