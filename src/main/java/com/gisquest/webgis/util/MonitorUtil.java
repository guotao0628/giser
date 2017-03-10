package com.gisquest.webgis.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 * 获取系统信息工具类
 * 
 * @author liul 2015年12月28日下午3:12:16
 * @
 */
public class MonitorUtil {
    /**
     * 获取电脑信息
     * 
     * @return
     */
    public static Map<String, String> getComputerGeneral() {
        Map<String, String> result = null;
        try {
            result = new HashMap<String, String>();
            // ip地址
            InetAddress address = InetAddress.getLocalHost();
            String ip = address.getHostAddress();
            // 计算机名称
            Map<String, String> map = System.getenv();
            String computerName = map.get("COMPUTERNAME");
            // 内存总量
            Sigar sigar = new Sigar();
            Mem mem = sigar.getMem();
            double memoryTotal = longConversionDouble(mem.getTotal());
            memoryTotal = memoryTotal / 1024 / 1024 / 1024;
            String memoryString = String.format("%.2f", memoryTotal) + "GB";
            double memUsed = longConversionDouble(mem.getUsed());
            memUsed = memUsed / 1024 / 1024 / 1024;
            String memUsedString = String.format("%.2f", memUsed) + "GB";
            // 系统名称
            Properties properties = System.getProperties();
            String systemName = properties.getProperty("os.name");
            String systemArch = properties.getProperty("os.arch");
            String systemVersion = properties.getProperty("os.version");
            result.put("ip", ip);
            result.put("computerName", computerName);
            result.put("memoryString", memoryString);
            result.put("systemName", systemName);
            result.put("systemArch", systemArch);
            result.put("systemVersion", systemVersion);
            result.put("memUsedString", memUsedString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取jvm虚拟机信息
     * 
     * @return
     */
    public static Map<String, String> getJVMInfo() {
        Runtime runtime = Runtime.getRuntime();
        Properties properties = System.getProperties();
        Map<String, String> map = new HashMap<String, String>();
        double jvmTotalMemoryDouble = longConversionDouble(runtime.totalMemory());
        jvmTotalMemoryDouble = jvmTotalMemoryDouble / 1024 / 1024 / 1024;
        double jvmFreeMemoryDouble = longConversionDouble(runtime.freeMemory());
        jvmFreeMemoryDouble = jvmFreeMemoryDouble / 1024 / 1024 / 1024;
        map.put("jvmTotalMemory", String.format("%.2f", jvmTotalMemoryDouble) + "GB");
        map.put("jvmFreeMemory", String.format("%.2f", jvmFreeMemoryDouble) + "GB");
        map.put("processor", runtime.availableProcessors() + "");
        map.put("vmSpeciName", properties.getProperty("java.vm.specification.name"));
        map.put("vmSpeciVersion", properties.getProperty("java.vm.specification.version"));
        map.put("vmSpeciVendor", properties.getProperty("java.vm.specification.vendor"));
        return map;
    }

    /**
     * 获取cpu信息
     * 
     * @return
     */
    public static Map<String, String> getCpuInfo() {
        Sigar sigar = new Sigar();
        Map<String, String> map = new HashMap<String, String>();
        CpuInfo[] infos = null;
        CpuPerc cpuPerc = null;
        try {
            infos = sigar.getCpuInfoList();
            cpuPerc = sigar.getCpuPerc();
        } catch (SigarException e) {
            e.printStackTrace();
        }
        if (infos.length > 0) {
            map.put("cpuTotal", infos[0].getMhz() + "MHz");
            map.put("cpuVendor", infos[0].getVendor());
            map.put("cpuModel", infos[0].getModel());
            map.put("cpuUser", String.valueOf(cpuPerc.getUser()));
            map.put("cpuSystem", CpuPerc.format(cpuPerc.getSys()));
            map.put("cpuCompined", CpuPerc.format(cpuPerc.getCombined()));
        } else {
            throw new RuntimeException("无cpu");
        }
        return map;
    }

    /**
     * 获取硬盘信息
     * 
     * @return
     */
    public static List<Map<String, String>> getHardDisk() {
        Sigar sigar = new Sigar();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            FileSystem[] fileSystems = sigar.getFileSystemList();
            for (int i = 0; i < fileSystems.length; i++) {
                FileSystem fileSystem = fileSystems[i];
                if (fileSystem.getType() == 2) {
                    Map<String, String> map = new HashMap<String, String>();
                    FileSystemUsage usage = sigar.getFileSystemUsage(fileSystem.getDirName());
                    String diskName = fileSystem.getDevName();
                    diskName = diskName.substring(0, diskName.indexOf(":"));
                    map.put("diskName", diskName);
                    map.put("usageTotal", String.format("%.0f",
                            longConversionDouble(usage.getTotal()) / 1024 / 1024) + "GB");
                    map.put("usageFree", String.format("%.1f",
                            longConversionDouble(usage.getFree()) / 1024 / 1024) + "GB");
                    map.put("usageUsed", String.format("%.1f",
                            longConversionDouble(usage.getUsed()) / 1024 / 1024) + "GB");
                    list.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 实时获得电脑信息
     * 
     * @return
     */
    public static Map<String, String> getComputerInfoRealTime() {
        Map<String, String> map = new HashMap<String, String>();
        Sigar sigar = new Sigar();
        // memory
        Mem mem = null;
        try {
            mem = sigar.getMem();
        } catch (SigarException e) {
            e.printStackTrace();
        }
        double memUsed = longConversionDouble(mem.getUsed());
        memUsed = memUsed / 1024 / 1024 / 1024;
        String memUsedFormat = String.format("%.2f", memUsed);
        String memUsedString = memUsedFormat + "GB";
        double memoryTotal = longConversionDouble(mem.getTotal());
        memoryTotal = memoryTotal / 1024 / 1024 / 1024;
        double memUtilization = memUsed / memoryTotal * 100;
        String memUtilizationFormat = String.format("%.2f", memUtilization);
        map.put("memUsed", memUsedString);
        map.put("memUtilization", memUtilizationFormat);
        // cpu
        CpuPerc cpuPerc = null;
        try {
            cpuPerc = sigar.getCpuPerc();
        } catch (SigarException e) {
            e.printStackTrace();
        }
        map.put("cpuUser", String.valueOf(cpuPerc.getUser()));
        map.put("cpuSystem", CpuPerc.format(cpuPerc.getSys()));
        map.put("cpuCompined", CpuPerc.format(cpuPerc.getCombined()));
        // jvm
        Runtime runtime = Runtime.getRuntime();
        double jvmFreeMemoryDouble = longConversionDouble(runtime.freeMemory());
        jvmFreeMemoryDouble = jvmFreeMemoryDouble / 1024 / 1024 / 1024;
        map.put("jvmFreeMemory", String.format("%.2f", jvmFreeMemoryDouble) + "GB");
        // fileDisk
        map.put("storageUtilization", getStorageUtilization());
        return map;
    }

    /**
     * 存储利用率
     * 
     * @return
     */
    public static String getStorageUtilization() {
        double fileTotal = 0.0;
        double fileUsage = 0.0;
        try {
            Sigar sigar = new Sigar();
            FileSystem[] fileSystems = sigar.getFileSystemList();
            for (int i = 0; i < fileSystems.length; i++) {
                FileSystem fileSystem = fileSystems[i];
                if (fileSystem.getType() == 2) {
                    FileSystemUsage usage = sigar.getFileSystemUsage(fileSystem.getDirName());
                    fileTotal += longConversionDouble(usage.getTotal()) / 1024 / 1024;
                    fileUsage += longConversionDouble(usage.getUsed()) / 1024 / 1024;
                }
            }
        } catch (Exception e) {
        }
        return String.format("%.2f", (fileUsage / fileTotal) * 100);
    }

    /**
     * long类型转double
     * 
     * @param variable
     * @return
     */
    private static double longConversionDouble(long variable) {
        String temp = variable + "";
        double result = Double.parseDouble(temp);
        return result;
    }

    /**
     * 
     * 获取系统进程
     * 
     * @author yedy
     * @return progressNumber 进程总数
     */
    public static int systemProcess() {

        Process process = null;// 创建一个空的进程
        int progressNumber = 0;// 初始化进程数
        try {
            process = Runtime.getRuntime().exec("tasklist");// 获取计算机中所有的进程
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "utf-8"));
            while (br.readLine() != null) {
                progressNumber++; // 每读取一行进程，进程数加1
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return progressNumber;
    }

}
