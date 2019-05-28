package com.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

public class EncryptTool {

	private static Logger logger = Logger.getLogger(EncryptTool.class);

	static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static String byteToHexString(byte[] tmp) {
		String s;
		char str[] = new char[16 * 2];
		int k = 0;
		for (int i = 0; i < 16; i++) {
			byte byte0 = tmp[i];
			str[k++] = hexDigits[byte0 >>> 4 & 0xf];
			str[k++] = hexDigits[byte0 & 0xf];
		}
		s = new String(str);
		return s;
	}

	public static String md5(String sourc) {
		if (sourc == null) {
			return sourc;
		}
		MessageDigest md = null;
		byte[] b = null;
		try {
			md = MessageDigest.getInstance("MD5");
			try {
				b = md.digest(sourc.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				logger.error(e);
			}
		} catch (NoSuchAlgorithmException e) {
			logger.error(e);
		}
		return byteToHexString(b);

	}
    
    public static void main(String[] args)
    {
        System.out.println(md5("887654677"));
    }

}
