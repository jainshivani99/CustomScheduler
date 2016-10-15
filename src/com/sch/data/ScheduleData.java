package com.sch.data;

import java.util.Date;

/**
 * A simple data object, holds the Schedule information for a period/date/name.
 * 
 * @author 
 *
 */
public class ScheduleData {
	
	/**
	 * Person Name - for whom the Period/Date is to be reserved.
	 */
	private String personName;
	
	/**
	 * Period Name - Name of the Period.
	 */
	private String periodName;
	/**
	 * Period ID - a simple identifier for the Period, so as to retrieve from the JTable.
	 */
	private int periodId;
	
	/**
	 * Schedule Date.
	 */
	private Date schDate;
	
	public String getPersonName() {
		return personName;
	}
	public void setPersonName(String personName) {
		this.personName = personName;
	}
	public String getPeriodName() {
		return periodName;
	}
	public void setPeriodName(String periodName) {
		this.periodName = periodName;
	}
	public Date getSchDate() {
		return schDate;
	}
	public void setSchDate(Date schDate) {
		this.schDate = schDate;
	}
	
	public String toString() {
		return personName;
	}
	
	public void setPeriodId(int periodId) {
		this.periodId = periodId;
	}
	public int getPeriodId() {
		return this.periodId;
	}
	
	
	

}
