package com.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class TokenUtil {
	
	public static int UniqueSn_DefaultOffsetSep = (int) (System
			.currentTimeMillis() % 10L);

	private static Random randGenerator = new Random();

	private static int curRandNumOffset = 0;
	
	public static String getStdTimeString(Date cTime, boolean noSeperator) {
		if (cTime == null)
			return "";
		DateFormat format;
		if (!(noSeperator))
			format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		else {
			format = new SimpleDateFormat("yyyyMMddHHmmss");
		}

		return format.format(cTime);
	}
	
	public static String getStdTimeString(Date cTime, String dateFormat) {
		if (cTime == null)
			return "";

		DateFormat format = new SimpleDateFormat(dateFormat);
		return format.format(cTime);
	}
	
	public static long convertStringToLong(String longString,
			long defaultExceptionValue) {
		long num = 0L;
		if (longString != null) {
			try {
				num = Long.parseLong(longString.trim());
			} catch (Exception ex) {
				num = defaultExceptionValue;
			}

		}

		return num;
	}
	
	public static synchronized int getNewRandOffset() {
		curRandNumOffset = (curRandNumOffset + 1) % 10000;

		return curRandNumOffset;
	}
	
	private static synchronized long getUniqueSnByTimeAndSepNo(Date time,
			int randOffsetSepNum) {
		Date curTime = time;
		if (time == null)
			curTime = new Date(System.currentTimeMillis());

		int curRandOff = randOffsetSepNum;
		if ((curRandOff < 1) || (curRandOff > 9)) {
			curRandOff = randGenerator.nextInt(10);
		}

		String timeStr = getStdTimeString(curTime, "yyMMddHHmmss");

		long randSn = convertStringToLong(timeStr, System.currentTimeMillis());

		int curOffset = getNewRandOffset();
		randSn = (randSn * 10L + curRandOff) * 10000L + curOffset;

		return randSn;
	}

	public static synchronized long getUniqueSn(int randOffsetSepNum) {
		Date curTime = new Date(System.currentTimeMillis());

		long sn = getUniqueSnByTimeAndSepNo(curTime, randOffsetSepNum);

		return sn;
	}

	public static synchronized long getUniqueSn() {
		long sn = getUniqueSn(UniqueSn_DefaultOffsetSep % 10);

		return sn;
	}
    
    public static void main(String[] args)
    {
        System.out.println("getUniqueSn()==" + getUniqueSn());
    }
}
