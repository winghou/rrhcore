package com.util;

/**
 * 融360校验时判断非空的异常
 *
 * @author WangChun
 * @create 2017-10-25-17:30
 */
public class RongNullPointerException extends Exception {
	private static final long serialVersionUID = 1L;

	// 提供无参数的构造方法
	public RongNullPointerException() {
	}

	// 提供一个有参数的构造方法
	public RongNullPointerException(String message) {
		//把参数传递给Throwable的带String参数的构造方法
		super(message);
	}
}
