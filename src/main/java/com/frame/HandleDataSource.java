package com.frame;

/**
 * 利用ThreadLocal解决线程安全问题
 * 
 * @author bill
 *
 */
public class HandleDataSource {
	public static final ThreadLocal<String> holder = new ThreadLocal<String>();

	public static void putDataSource(String datasource) {
		holder.set(datasource);
	}

	public static String getDataSource() {
		return holder.get();
	}
}
