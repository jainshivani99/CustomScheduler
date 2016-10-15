package com.sch.manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.sch.data.ScheduleData;
import com.sch.helper.UtilHelper;

/**
 * @author 
 * 
 * ScheduleManager implements the {@link DataHandler} interface, aims to add/update/remove schedule,
 *  and loadData (converting the file into map in the memory), saveData (storing back into the filesystem).
 *
 * getInstance method makes sure at anypoint of time, only one instance will be given to the caller, to avoid the data corruption inside the map.
 * 
 */
public class ScheduleManager implements DataHandler {

	private static final String NAME_COLUMN = "name";
	public static String FILE_NAME = "my_schedule.csv";
	public static String BASE_PATH = "/usr/local/research/CustomScheduler/report/";

	// The Master Schedule Data File - will be serialized into a CSV file later when saveData() is called.
	private static Map<String, List<ScheduleData>> scheduleMasterData = new HashMap<String, List<ScheduleData>>();

    public static String[] dayNames = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

	public static Map<String, List<ScheduleData>> getScheduleMasterDataMap() {
		synchronized (scheduleMasterData) {
			return scheduleMasterData;
		}
	}

	private ScheduleManager() {
		// default constructor, no one can create this instance, except via
		// getInstance();
	}

	private static ScheduleManager scheduleMgrObj = new ScheduleManager();

	/**
	 * @return
	 */
	public static ScheduleManager getInstance() {
		if (scheduleMgrObj == null) { // safer side check for null
			scheduleMgrObj = new ScheduleManager();
		}

		return scheduleMgrObj;
	}

	 /*  (non-Javadoc)
	 * @see com.sch.manager.DataHandler#addSchedule(java.lang.String, int, java.lang.String, java.util.Date)
	 * 
	 * Creates a new ScheduleData object, and stores into the map in the memory.
	 */
	@Override
	public void addSchedule(String personName, int periodId, String periodName, Date schDate) {
		ScheduleData newSchedule = new ScheduleData();
		newSchedule.setPersonName(personName);
		newSchedule.setPeriodId(periodId);
		newSchedule.setPeriodName(periodName);
		newSchedule.setSchDate(schDate);

		Calendar schCal = Calendar.getInstance();
		schCal.setTime(schDate);

		String dataKey = (schCal.get(Calendar.MONTH) + 1) + "-" + schCal.get(Calendar.YEAR);

		synchronized (scheduleMasterData) {
			List<ScheduleData> scheduleList = new ArrayList<>();
			if (scheduleMasterData.containsKey(dataKey)) {
				scheduleList = scheduleMasterData.get(dataKey);
			}
			scheduleList.add(newSchedule);
			scheduleMasterData.put(dataKey, scheduleList);
		}

	}

	/* (non-Javadoc)
	 * @see com.sch.manager.DataHandler#updateSchedule(java.lang.String, java.lang.String, int, java.lang.String, java.util.Date)
	 * 
	 * Updates an existing ScheduleData Object by removing/and adding the new ScheduleData for the same period/date.
	 */
	@Override
	public void updateSchedule(String prevPersonName, String newPersonName, int periodId, String periodName,
			Date schDate) {
		// First remove schedule
		removeSchedule(prevPersonName, periodName, schDate);
		// add new schedule
		addSchedule(newPersonName, periodId, periodName, schDate);

	}

	/* (non-Javadoc)
	 * @see com.sch.manager.DataHandler#removeSchedule(java.lang.String, java.lang.String, java.util.Date)
	 * 
	 * Removes/Deletes a ScheduleData Object from the map in the memory.
	 */
	@Override
	public void removeSchedule(String studentName, String periodName, Date schDate) {
		Calendar schCal = Calendar.getInstance();
		schCal.setTime(schDate);

		int month = schCal.get(Calendar.MONTH) + 1;
		int year = schCal.get(Calendar.YEAR);

		String dataKey = month + "-" + year;
		synchronized (scheduleMasterData) {
			if (scheduleMasterData.containsKey(dataKey)) {
				List<ScheduleData> scheduleList = scheduleMasterData.get(dataKey);

				ScheduleData matchDataItem = null;
				// Locate the period and studentName for this month.
				for (ScheduleData schData : scheduleList) {
					if (schData.getPersonName().equalsIgnoreCase(studentName)
							&& schData.getPeriodName().equalsIgnoreCase(periodName)) {
						// Found the record.
						matchDataItem = schData;
						break;
					}
				}

				if (matchDataItem != null) {
					scheduleList.remove(matchDataItem);
				}

				scheduleMasterData.put(dataKey, scheduleList);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.sch.manager.DataHandler#loadData()
	 * 
	 * Implements the interface method loadData, aims to convert the data from the FileSystem, into the map in the memory. 
	 * It inturn calls convertFileToMap() method.
	 */
	@Override
	public void loadData() {
		convertFileToMap();
	}

	/* (non-Javadoc)
	 * @see com.sch.manager.DataHandler#saveData()
	 * 
	 * Implements the interface method saveData, aims to convert back the data from the map, into the FileSystem.
	 */
	@Override
	public void saveData() {
		convertMapToFile();
	}

	/**
	 *  Opens the csv file, creates if not existing, adds all the entries from the map into the csv file.
	 */
	private void convertMapToFile() {
		UtilHelper.clearDataInFile(BASE_PATH, FILE_NAME,true);

		synchronized (scheduleMasterData) {
			Collection<List<ScheduleData>> valueList = scheduleMasterData.values();
			Iterator<List<ScheduleData>> valueListItr = valueList.iterator();
			while (valueListItr.hasNext()) {
				List<ScheduleData> scheduleList = valueListItr.next();
				Iterator<ScheduleData> scheduleListItr = scheduleList.iterator();
				while (scheduleListItr.hasNext()) {
					ScheduleData schData = scheduleListItr.next();
					writeDataToFile(schData);
				}
			}

		}
	}

	

	/**
	 * Writes a single record entry into the csv file.
	 * @param schData a ScheduleData Object.
	 */
	private void writeDataToFile(ScheduleData schData) {
		try {
			File file = new File(BASE_PATH, FILE_NAME);
			boolean created = false;
			if (!file.exists()) {
				file.createNewFile();
				created = true;
			}

			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

			if (created) {
				// Write header first.
				writer.append("name,periodId,periodName,date\r\n");
			}

			writer.append(schData.getPersonName()).append(",")
			.append(String.valueOf(schData.getPeriodId())).append(",")
			.append(schData.getPeriodName()).append(",")
			.append(UtilHelper.getDateStr(schData.getSchDate(), "MM/dd/yyyy")).append("\r\n");

			writer.flush();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Converts the data from the csv file, into the map in the memory.
	 */
	private void convertFileToMap() {
		try {

			File file = new File(BASE_PATH, FILE_NAME);
			if (file.exists()) {
				
				BufferedReader reader = new BufferedReader(new FileReader(file));
				while (reader.ready()) {
					String entry = reader.readLine();
					if (entry.startsWith(NAME_COLUMN)) {
						// skip.
						continue;
					} else {
						StringTokenizer st = new StringTokenizer(entry, ",");
						String studentName = st.nextToken();
						int periodId = Integer.parseInt(st.nextToken());
						String periodName = st.nextToken();
						String schDateStr = st.nextToken();
	
						ScheduleData schData = new ScheduleData();
						schData.setPersonName(studentName);
						schData.setPeriodId(periodId);
						schData.setPeriodName(periodName);
						Date schDate = UtilHelper.formatDate(schDateStr);
						schData.setSchDate(schDate);
						// add to map.
						addSchedule(studentName, periodId, periodName, schDate);
					}
				}
				reader.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Helper method for the GUI, to get the List of <ScheduleData> objects from the Map.
	 * 
	 * This method is not used for now, but later will be used for MonthlyView GUI.
	 * @param monthYear
	 * @return
	 */
	public static List<ScheduleData> getScheduleDataForMonthYear(String monthYear) {
		List<ScheduleData> list = new ArrayList<ScheduleData>();
		if (scheduleMasterData != null && scheduleMasterData.containsKey(monthYear)) {
			list = (List<ScheduleData>) scheduleMasterData.get(monthYear);
		}
		return list;
	}

	/**
	 * Helper Method  for the GUI, to get the List of <ScheduleData> objects from the Map, for given CalendarDate.
	 * 
	 * This method is used for the DailyView GUI.
	 * @param calendarDate
	 * @return
	 */
	public static List<ScheduleData> getScheduleDataForDate(Calendar calendarDate) {
		List<ScheduleData> returnList = new ArrayList<ScheduleData>();
		int month = calendarDate.get(Calendar.MONTH) + 1;
		int year = calendarDate.get(Calendar.YEAR);
		String monthYear = String.valueOf(month) + "-" + String.valueOf(year);
		if (scheduleMasterData != null && scheduleMasterData.containsKey(monthYear)) {
			List<ScheduleData> list = (List<ScheduleData>) scheduleMasterData.get(monthYear);
			if (list != null) {
				for (ScheduleData schData : list) {
					if (schData.getSchDate().equals(calendarDate.getTime())) {
						returnList.add(schData);
					}
				}
			}
		}
		return returnList;
	}

	
}
