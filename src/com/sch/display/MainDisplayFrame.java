package com.sch.display;

import java.awt.BorderLayout;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.sch.helper.UtilHelper;
import com.sch.manager.ScheduleManager;

/**
 * @author 
 * 
 * Scheduler's Main display frame - a Swing JFrame, displays two JPanels (Calendar Panel, and Scheduler Panel). 
 *
 */

public class MainDisplayFrame extends JFrame {
	
	private static final long serialVersionUID = -5344647873713598073L;

	private static final int FRAME_WIDTH = 600;
	private static final int FRAME_HEIGHT = 400;
	
	private LeftCalendarTablePane leftCalendarTablePane  = null;
    private RightScheduleTablePane rightScheduleTablePane = null;
    
    private Calendar currentSelectedDate = null;

	private ScheduleManager scheduleHelper = ScheduleManager.getInstance();
	
	public MainDisplayFrame(Calendar selectedDateCal) {
		scheduleHelper.loadData();
		displayFrame(selectedDateCal);
	}

	
	/**
	 * Any time when the date is changed in the LeftCalendarTable, this method is triggered, so that the Schedule for the selected date can be refreshed on right side.
	 * @param newSelectedData New Selected Date.
	 */
	public void fireDateChangedEvent(Calendar newSelectedDate) {
		setCurrentSelectedDate(newSelectedDate);
		rightScheduleTablePane.updateTableData(newSelectedDate);
	}
	
	
	/**
	 * Displays a swing JFrame, with two JTable (one for main calendar, and one for the Scheduler). The windowClosing event is captured for the close button, so we can save the data into filesystem.
	 * @param selectedDateCal a date for which the Month view, and Scheduler table is to be displayed.
	 */
	private void displayFrame(Calendar selectedDateCal) {
		
	    this.setLayout(new BorderLayout());

		setCurrentSelectedDate(selectedDateCal);
		
		leftCalendarTablePane = new LeftCalendarTablePane(this, selectedDateCal);
		rightScheduleTablePane = new RightScheduleTablePane(this, selectedDateCal);
		
	    this.add(leftCalendarTablePane, BorderLayout.WEST);
	    this.add(rightScheduleTablePane, BorderLayout.CENTER);
	
	    
	    this.setTitle("Laboratory Calandar");
	    this.setVisible(true);
	    this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.validate();
	    
	    this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
            	closeFrame();
                e.getWindow().dispose();
            }
        });
		
	}
	
	private void closeFrame() {
		// save the map to file.
    	scheduleHelper.saveData();
        System.out.println("Closed");
	}
	
	/**
	 * Helper method
	 * @return
	 */
	public Calendar getCurrentSelectedDate() {
		return currentSelectedDate;
	}

	public void setCurrentSelectedDate(Calendar currentSelectedDate) {
		this.currentSelectedDate = currentSelectedDate;
	}
	
	/**
	 * Starts the MainDisplayFrame for the current date to start with.
	 * @param args
	 */
	public static void main(String []args) {
			
		 SwingUtilities.invokeLater(new Runnable() {
	            @SuppressWarnings("unused")
				@Override
	            public void run() {
	            	Calendar currentCal = UtilHelper.getCurrentDate();
	            	MainDisplayFrame mdFrame = new MainDisplayFrame(currentCal);	        		
	            }
	        });

	}
}
