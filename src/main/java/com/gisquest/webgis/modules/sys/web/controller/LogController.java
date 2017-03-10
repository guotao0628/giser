package com.gisquest.webgis.modules.sys.web.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gisquest.platform.common.utils.FileUtils;
import com.gisquest.webgis.modules.sys.entity.Log;
import com.gisquest.webgis.modules.sys.entity.ServiceInvocationError;
import com.gisquest.webgis.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * 控制与日志相关的操作
 * 
 * @author Jisj1
 *
 */
@Controller
public class LogController {
    /** 请求体 */
    @Autowired
    private HttpServletRequest request;
    /** 定义每页的日志条数 */
    private static final Integer ITEMCOUNT_EACHPAGE = 10;
    /** 日期字符串 */
    private static String DATE_STR;
    /** 本类局部线程变量 */
    private static Thread THREAD;
    /** 任务队列 */
    private static LinkedList<Runnable> QUEUE = new LinkedList<>();
    /** 日期格式 */
    private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 添加任务
     * 
     * @param runnable
     *            任务，本身是一个线程
     */
    private void addTask(Runnable runnable) {
        /**
         * 同步代码块，同一时间只能有一个线程进入，保证了同步性
         */
        synchronized (QUEUE) {
            /* System.out.println("要添加任务了"); */
            /* 在任务队列中添加任务 */
            QUEUE.add(runnable);
        }
        if (THREAD == null) {
            /* 如果线程为空，则创建新的线程，去执行队列中的任务 */
            THREAD = new Thread(new Runnable() {
                /**
                 * 线程启动方法
                 */
                @Override
                public void run() {
                    synchronized (QUEUE) {
                        while (!QUEUE.isEmpty()) {
                            /* System.out.println("要做任务了"); */
                            /* 执行队列中的第一个任务，并删除第一个任务 */
                            QUEUE.poll().run();
                        }
                        /* 若队列中没有任务，则清理线程 */
                        THREAD = null;
                    }
                }
            });
            /* 启动线程 */
            THREAD.start();
        }
    }

    /**
     * 添加日志
     * 
     * @param log
     *            日志对象
     */
    @RequestMapping("/addLog")
    @ResponseBody
    public void addLog(final Log log, final HttpServletRequest request) {
        // 日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 将当天日期格式化成字符串
        DATE_STR = sdf.format(new Date());
        /* 获取当天的日志文件路径 */
        final String path = request.getSession().getServletContext()
                .getRealPath("/log/" + DATE_STR + ".json");
        addTask(new Runnable() {
            /**
             * 任务具体做的事情，即增加一条日志
             */
            @Override
            public void run() {
                File logFile = new File(path);
                /* 内容字符串初始化 */
                String logJson = "";
                try {
                    // 如果文件不存在，则创建文件
                    if (!logFile.exists()) {
                        logFile.getParentFile().mkdirs();
                        logFile.createNewFile();
                    }
                    // 读取文件内容
                    logJson = Util.readFile(path);
                } catch (IOException e) {
                    throw new RuntimeException("文件读取异常");
                }
                /* 如果日志文件为空，则格式化日志内容字符串 */
                if ("".equals(logJson)) {
                    logJson = Util.addCorrectFormat(logJson);
                }
                /* 把日志内容字符串转为List<Log>类型 */
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Type listType = new TypeToken<List<Log>>() {
                }.getType();
                List<Log> logJsonList = gson.fromJson(logJson, listType);
                /* 将log对象添加到List<Log>中 */
                logJsonList.add(log);
                /* 保存日志 */
                try {
                    FileUtils.writeStringToFile(logFile, gson.toJson(logJsonList), "UTF-8", false);
                } catch (IOException e) {
                    throw new RuntimeException("文件读取异常");
                }
            }
        });
    }

    /**
     * 获取所有日志
     * 
     * @return 以列表形式返回所有日志对象
     */
    @RequestMapping("/admin/getLog")
    @ResponseBody
    public List<Log> getAllLogs() {
        /* 日志文件目录路径 */
        String path = request.getSession().getServletContext().getRealPath("/log");
        /* 日志文件夹目录 */
        File logFile = new File(path);
        /* 日志列表初始化 */
        List<Log> logList = new ArrayList<>();
        /* 获取文件夹下所有日志文件 */
        File[] logJsonFiles = logFile.listFiles();
        Type listType = new TypeToken<List<Log>>() {
        }.getType();
        if (logJsonFiles != null) {
            for (File file : logJsonFiles) {
                /* 获取当前遍历文件的日志内容 */
                String logJsonStr = Util.readFile(file.getPath());
                List<Log> list = new GsonBuilder().create().fromJson(logJsonStr, listType);
                logList.addAll(list);
            }
        }
        return logList;
    }

    /**
     * 获取指定页数的日志
     * 
     * @return 以列表形式返回第N页的日志对象
     */
    // @RequestMapping("/admin/logs/{page}")
    @ResponseBody
    public List<Log> getLog(@PathVariable Integer page) {
        int fromIndex = (page - 1) * ITEMCOUNT_EACHPAGE;
        int toIndex = page * ITEMCOUNT_EACHPAGE;
        int logCount = getAllLogs().size();
        /** 如果日志条数为零，则返回空的列表 */
        if (logCount == 0) {
            return new ArrayList<>();
        }
        /** 如果不足10个，则取到最后一个，否则取足10个 */
        if (logCount < page * ITEMCOUNT_EACHPAGE) {
            return getAllLogs().subList(fromIndex, logCount);
        } else {
            return getAllLogs().subList(fromIndex, toIndex);
        }
    }

    /**
     * 获取日志的页数
     * 
     * @return 以列表形式返回第N页的日志对象
     */
    @RequestMapping("/admin/log/pageCount")
    @ResponseBody
    public Integer getPageCount() {
        Integer logCount = getAllLogs().size();
        return (logCount - 1) / ITEMCOUNT_EACHPAGE + 1;
    }

    /**
     * 搜索指定的日志列表
     * 
     * @param startDateStr
     *            起始日期
     * @param endDateStr
     *            终止日期
     * @param userId
     *            用户ID
     * @param action
     *            操作行为
     * @return 过滤后的日志列表
     */
    @RequestMapping("/admin/logs/")
    @ResponseBody
    public Object getLog(String startDateStr, String endDateStr, String userId, String action) {
        try {
            /* 日期格式 */
            Date startDate = null;
            Date endDate = null;
            Integer oneDay = 1 * 24 * 60 * 60 * 1000;
            /* 获取文件夹下所有日志文件 */
            List<File> logJsonFiles = getAllLogFiles();
            /* 对日期进行过滤 */
            if (!startDateStr.trim().equals("")) {
                startDate = strToDate(startDateStr);
                startDate = new Date(startDate.getTime() - oneDay);
                logJsonFiles = getLogFilesAfterDate(logJsonFiles, startDate);
            }
            if (!endDateStr.trim().equals("")) {
                endDate = strToDate(endDateStr);
                endDate = new Date(endDate.getTime() + oneDay);
                logJsonFiles = getLogFilesBeforeDate(logJsonFiles, endDate);
            }

            /* 日志列表初始化 */
            List<Log> logList = new ArrayList<>();
            if (logJsonFiles != null) {
                for (File file : logJsonFiles) {
                    /* 获取当前遍历文件的日志内容 */
                    String logJsonStr = Util.readFile(file.getPath());
                    Type listType = new TypeToken<List<Log>>() {
                    }.getType();
                    List<Log> list = new GsonBuilder().create().fromJson(logJsonStr, listType);
                    List<Log> resultList = new ArrayList<>();
                    for (Log log : list) {
                        /* 如果userId字段和action字段都不为空，以该字段值进行筛选 */
                        if (userId != null && action != null) {
                            if (((String) log.getUserId()).contains(userId)
                                    && ((String) log.getAction()).contains(action)) {
                                resultList.add(log);
                            }
                        }
                    }
                    logList.addAll(resultList);
                }
            }
            return logList;
        } catch (ParseException e) {
            return new ServiceInvocationError("日期格式不正确，正确的格式为yyyy-MM-dd");
        }
    }

    /**
     * 获取指定日期以后的日志文件数组（包含当天）
     * 
     * @param files
     *            需要过滤的文件数组
     * @param date
     *            日期
     * @return
     * @throws ParseException
     *             日期格式异常
     */
    private List<File> getLogFilesAfterDate(List<File> files, Date date) throws ParseException {
        List<File> resultFiles = new ArrayList<>();
        for (File file : files) {
            if (file != null) {
                /* 获取不带后缀的文件名 */
                String filename = file.getName();
                filename = filename.substring(0, filename.length() - 5);
                Date fileDate = strToDate(filename);
                if (fileDate.after(date)) {
                    resultFiles.add(file);
                }
            }
        }
        return resultFiles;
    }

    /**
     * 获取指定日期以前的日志文件数组（包含当天）
     * 
     * @param files
     *            需要过滤的文件数组
     * @param date
     *            指定日期
     * @return
     * @throws ParseException
     *             日期格式异常
     */
    private List<File> getLogFilesBeforeDate(List<File> files, Date date) throws ParseException {
        List<File> resultFiles = new ArrayList<>();
        for (File file : files) {
            if (file != null) {
                /* 获取不带后缀的文件名 */
                String filename = file.getName();
                filename = filename.substring(0, filename.length() - 5);
                Date fileDate = strToDate(filename);
                if (fileDate.before(date)) {
                    resultFiles.add(file);
                }
            }
        }
        return resultFiles;
    }

    /**
     * 获取所有日志文件数组
     * 
     * @return
     */
    private List<File> getAllLogFiles() {
        /* 日志文件目录路径 */
        String path = request.getSession().getServletContext().getRealPath("/log");
        /* 日志文件夹目录 */
        File logFile = new File(path);
        /* 获取文件夹下所有日志文件 */
        File[] allFiles = logFile.listFiles();
        List<File> logJsonFiles = new ArrayList<>();
        for (File file : allFiles) {
            logJsonFiles.add(file);
        }
        return logJsonFiles;
    }

    /**
     * 日期字符串转日期
     * 
     * @param dateStr
     *            日期字符串
     * @return
     * @throws ParseException
     *             日期格式异常
     */
    private Date strToDate(String dateStr) throws ParseException {
        return DATEFORMAT.parse(dateStr);
    }
}
