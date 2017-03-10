package com.gisquest.webgis.modules.sys.web.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gisquest.webgis.modules.sys.entity.Bookmark;
import com.gisquest.webgis.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * 地图书签控制器
 * 
 * @author Jisj1
 *
 */
@Controller
public class BookmarkController {
  /** 请求体 */
  @Autowired
  private HttpServletRequest request;
  private Gson gson;
  private Type listType = new TypeToken<List<Bookmark>>() {
  }.getType();

  public BookmarkController() {
    gson = new GsonBuilder().setPrettyPrinting().create();
  }

  /**
   * 获取提定user的书签
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  @RequestMapping(value = "/bookmarks/{userId}", method = RequestMethod.GET)
  @ResponseBody
  public String getBookmarksByUserId(@PathVariable String userId) {
    Util.G_REQUEST = request;
    String bookmarkJson = Util.getBookmarks();
    List<Map<String, Object>> user_bookmarks = gson.fromJson(bookmarkJson, List.class);// 把bookmarks.json转为list
    if (user_bookmarks != null) {
      for (Map<String, Object> user_bookmark : user_bookmarks) {
        if (userId.equals(user_bookmark.get("userId"))) {
          return gson.toJson(user_bookmark.get("bookmarks"), listType);
        }
      }
    }
    // 未找到指定的user，则返回空的数组
    return "[]";
  }

  /**
   * 删除指定user的指定id的书签
   * 
   * @param userId
   * @param id
   * @return
   */
  @SuppressWarnings("unchecked")
  @RequestMapping(value = "/bookmarks/{userId}/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public String deleteBookmarkById(@PathVariable String userId, @PathVariable String id) {
    Util.G_REQUEST = request;
    String bookmarkJson = Util.getBookmarks();
    List<Map<String, Object>> user_bookmarks = gson.fromJson(bookmarkJson, List.class);// 把bookmarks.json转为list
    if (user_bookmarks != null) {
      for (Map<String, Object> user_bookmark : user_bookmarks) {
        if (userId.equals(user_bookmark.get("userId"))) {
          // 获取指定user的bookmarks
          List<Map<String, Object>> bookmarks = (List<Map<String, Object>>) user_bookmark
              .get("bookmarks");
          for (Map<String, Object> bookmark : bookmarks) {
            if (id.equals(bookmark.get("id"))) {
              bookmarks.remove(bookmark);
              Util.writeBookmarks(gson.toJson(user_bookmarks));
              break;
            }
          }
        }
      }
    }
    return "删除成功！";
  }

  /**
   * 删除指定userId的所有书签
   * 
   * @param userId
   *          用户ID
   * @return
   */
  @RequestMapping(value = "/bookmarks/{userId}", method = RequestMethod.DELETE)
  @ResponseBody
  @SuppressWarnings("unchecked")
  public String removeAllBookmarks(@PathVariable String userId) {
    Util.G_REQUEST = request;
    String bookmarkJson = Util.getBookmarks();
    List<Map<String, Object>> user_bookmarks = gson.fromJson(bookmarkJson, List.class);// 把bookmarks.json转为list
    if (user_bookmarks != null) {
      for (Map<String, Object> user_bookmark : user_bookmarks) {
        if (userId.equals(user_bookmark.get("userId"))) {
          // 获取指定user的bookmarks
          List<Map<String, Object>> bookmarks = (List<Map<String, Object>>) user_bookmark
              .get("bookmarks");
          bookmarks.clear();
          Util.writeBookmarks(gson.toJson(user_bookmarks));
        }
      }
    }
    return "删除成功！";
  }

  /**
   * 为指定user增加一个书签
   * 
   * @param userId
   *          用户ID
   * @param bookmark
   *          书签对象
   * @return
   */
  @SuppressWarnings("unchecked")
  @RequestMapping(value = "/bookmarks/{userId}", method = RequestMethod.POST)
  @ResponseBody
  public String addBookmark(@PathVariable String userId, @RequestBody Bookmark bookmark) {
    Util.G_REQUEST = request;
    String bookmarkJson = Util.getBookmarks();
    List<Map<String, Object>> user_bookmarks = gson.fromJson(bookmarkJson, List.class);// 把bookmarks.json转为list
    if (user_bookmarks == null) { // 若文件内容为空
      user_bookmarks = new ArrayList<>();
    }
    boolean isUserFound = false;
    for (Map<String, Object> user_bookmark : user_bookmarks) {
      // 如果找到了指定的user，则获取该user的bookmarks
      if (userId.equals(user_bookmark.get("userId"))) {
        List<Bookmark> bookmarks = (List<Bookmark>) user_bookmark.get("bookmarks");
        // 在bookmarks中添加一个新的bookmark
        bookmarks.add(bookmark);
        isUserFound = true;
        break;
      }
    }
    // 如果未找到指定的user，则为该user创建一个新的user_bookmark
    if (!isUserFound) {
      Map<String, Object> user_bookmark = new LinkedHashMap<>();
      user_bookmark.put("userId", userId);
      List<Bookmark> bookmarks = new ArrayList<>();
      bookmarks.add(bookmark);
      user_bookmark.put("bookmarks", bookmarks);
      user_bookmarks.add(user_bookmark);
    }
    Util.writeBookmarks(gson.toJson(user_bookmarks));
    return "增加书签成功！";
  }
}
