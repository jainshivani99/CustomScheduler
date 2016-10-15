package com.sch.manager;

import java.util.Date;

/**
 * 
 * @author 
 * Simple DataHandler Interface - helps implementor to implement the basic Create/Update/Remove Schedule API methods. 
 * Also Implementor must write load, and save methods - these are the important methods to read data from the <datastoragesystem>, and 
 * write data into the <datastoragesystem>. The <datastoragesystem> can be anything the implementor choose. Example: File System, or Database.
 *
 */
public interface DataHandler {
	/**
	 * Creates a new Schedule Record.
	 * @param personName Name of the Person for whom the period/date is being reserved.
	 * @param periodId - unique period id for the period name.
	 * @param periodName - Name of the period.
	 * @param schDate - a java.util.Date object for the Schedule.
	 */
	public void addSchedule(String personName, int periodId, String periodName, Date schDate);
	
	/**
	 * Updates an existing schedule record.
	 * @param prevPersonName - an existing person name.
	 * @param newPersonName - new person name.
	 * @param periodId - unique period id.
	 * @param periodName - Name of the period.
	 * @param schDate - a java.util.Date object for the Schedule.
	 */
	public void updateSchedule(String prevPersonName, String newPersonName, int periodId, String periodName,
			Date schDate);
	
	/**
	 * Removes/Deletes an existing schedule record.
	 * @param studentName
	 * @param periodName
	 * @param schDate
	 */
	public void removeSchedule(String studentName, String periodName, Date schDate);
	
	/**
	 * Loads the data from the underlying database (either file system, or database), into the map in the memory.
	 */
	public void loadData();
	
	/**
	 * Saves the data from the map in the memory, into the underlying database (either file system or database).
	 */
	public void saveData();

}
