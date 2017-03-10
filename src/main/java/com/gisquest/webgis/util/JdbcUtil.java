package com.gisquest.webgis.util;

import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * JDBC工具类
 * 
 * @author Jisj1
 *
 */
public final class JdbcUtil {

	/**
	 * 数据库连接池
	 */
	private static ComboPooledDataSource DATA_SOURCE;
	static {
		DATA_SOURCE = new ComboPooledDataSource();
	}

	/**
	 * 将构造函数私有，不让外界创建实例对象
	 */
	private JdbcUtil() {

	}

	/**
	 * 获取数据库连接池
	 */
	public static ComboPooledDataSource getDataSource() {
		return DATA_SOURCE;
	}

	/**
	 * 获取数据库连接池的连接
	 */
	public static Connection getOraConnection() {
		try {
			return DATA_SOURCE.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException("数据库连接失败，请检查c3p0-config.xml配置文件");
		}
	}

}
