package com.lg.personalization.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StatisticalUtils {
	private static final SimpleDateFormat hf = new SimpleDateFormat("HH");
	private static final SimpleDateFormat mf = new SimpleDateFormat("mm");
	public static double getHoursStdDeviation(int[] hours)
	{
		double mean = getMean(hours);
		double temp = 0.0;
		for(int h : hours)
			temp += (mean - h) *(mean - h);
		return Math.sqrt(temp/hours.length);
	}
	
	
	public static double getDaysVariance(List<Integer> days)
	{
		return 0.0;
	}
	
	public static double getMean(int[] values)
	{
		int sum = 0;
        for(int a : values)
            sum += a;
            return (double) sum/values.length;
	}
	
	public static int converTimeToInt(Date time)
	{
		int hours = Integer.parseInt(hf.format(time));
		int minutes = Integer.parseInt(mf.format(time));
		return hours * 60 + minutes;
	}
	
	public static String convertIntToTime(int time)
	{
		int hours = time/60;
		int minutes = time%60;
		return String.format("%02d:%02d", hours,minutes);
	}
	
	public static String convertSecondsToString(long seconds)
	{
		long hours = seconds/3600;
		long minutes = (seconds/60)%60;
		long secondsAb = (seconds%3600)%60;
		return String.format("%02d:%02d:%02d", hours,minutes, secondsAb);
	}
}
