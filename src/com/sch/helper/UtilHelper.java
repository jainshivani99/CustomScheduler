package com.sch.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UtilHelper {

	public static Calendar getCurrentDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}
	
	public static Calendar getCalendarDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	public static String getDateStr(Date dateObj, String formatStr) {
		SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
		return sdf.format(dateObj);
	}

	public static Date formatDate(String dateStr) {
		Calendar cal = Calendar.getInstance();
		Date returnDate = cal.getTime();
		try {
	
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	
			returnDate = sdf.parse(dateStr);
		} catch (ParseException parseEx) {
			System.out.println("Could not parse the date:" + dateStr + " for the pattern MM/dd/yyyy");
		}
	
		return returnDate;
	}
	
	public static void clearDataInFile(String dirName, String fileName, boolean writeHeaderFlag) {
		try {
			File file = new File(dirName, fileName);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
			if (writeHeaderFlag) {
				writer.write("name,periodId,periodName,date\r\n");
			} else {
				writer.write("\r\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
