package com.sch.data;

import java.util.Calendar;
import java.util.Date;

import com.sch.manager.ScheduleManager;

/**
 * The Schedule JTable header columns are not simple strings, instead it is the reference to the DateColumn object. This is because, 
 * anytime we edit the cell, and change/or add the person name, we want to know the date that belongs to that cell. This will be helpful for month 
 * view GUI and Weekly view GUI, as the JTable will hold for 5 different dates (weekly view).
 * 
 * The displayStr holds the String text to display the date column.
 * DateValue is the Date object.
 * 
 * @author
 *
 */
public class DateColumn {
	
	private Date dateValue = new Date(); 
	private String displayStr = "currentDate";// This will be changed to display the date like "Wed", or "Fri" .
	
	public DateColumn() {
		// default constructor.
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		int dayOfTheWeek = cal.get(Calendar.DAY_OF_WEEK);

	    displayStr = ScheduleManager.dayNames[dayOfTheWeek-1];
	}
	
	public DateColumn(Calendar cal) {
		this.dateValue = cal.getTime();
	    int dayOfTheWeek = cal.get(Calendar.DAY_OF_WEEK);

	    displayStr = ScheduleManager.dayNames[dayOfTheWeek-1];

	}
	
	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	public String getDisplayStr() {
		return displayStr;
	}

	public void setDisplayStr(String displayStr) {
		this.displayStr = displayStr;
	}

	public String toString() {
		return displayStr;
	}
	
	

}
