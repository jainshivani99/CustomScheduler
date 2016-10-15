package com.sch.display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.sch.data.DateColumn;
import com.sch.data.ScheduleData;
import com.sch.helper.UtilHelper;
import com.sch.manager.ScheduleManager;

/**
 * 
 * Displays the Schedule JTable, along with Save/Cancel buttons.
 * The JTable Cell editing is restricted for the past dates, and the period column (Column Index = 0), is also restricted to edit.
 * 
 * @author
 *
 */
public class RightScheduleTablePane extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = -6704225989652446545L;

	private String [] periods = {"Period 1","Period 2","Period 3","Period 4","Period 5","Period 6","Period 7","Period 8"};
    
    private Object [] columns = {"Period",new DateColumn()};

    private Object [][] data = null;
    
    private JLabel schDateLabel = new JLabel("Date:");
    private TableModel schTableModel = new DefaultTableModel();
    private JTable scheduleTable  = new JTable(schTableModel);
    
    private JButton saveButton = new JButton("Save");
        
	private ScheduleManager scheduleHelper = ScheduleManager.getInstance();
	
	private MainDisplayFrame containerFrame = null; // The Frame which contains this "RightSide Scheduler Table".
	
	public RightScheduleTablePane(MainDisplayFrame containerFrame, Calendar currentCal) {
		this.containerFrame = containerFrame;
	    setupScheduleTablePane();

	}
	
	/**
	 * Sets up the Initial Schedule JTable Panel.
	 */
	public void setupScheduleTablePane() {
	    updateTableData(containerFrame.getCurrentSelectedDate());
	    
	    
	    scheduleTable.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	    scheduleTable.setPreferredSize(new Dimension(400,150));
	    JScrollPane tablePane = new JScrollPane(scheduleTable);
	    
	    JPanel buttonPanel = new JPanel();
	    buttonPanel.setLayout(new BorderLayout());
	    
	    saveButton.addActionListener(this);
	    
	    JButton cancelButton = new JButton("Cancel");
	    cancelButton.addActionListener(this);
	    
	    buttonPanel.add(cancelButton, BorderLayout.WEST);
	    buttonPanel.add(saveButton,  BorderLayout.EAST);
	    
	    this.setLayout(new BorderLayout());
	    this.add(schDateLabel,BorderLayout.NORTH);
	    this.add(tablePane,BorderLayout.CENTER);
	    this.add(buttonPanel, BorderLayout.SOUTH);

	}



	/**
	 * Updates the Table Model Schedule Data for the given date.
	 * @param currentCal
	 */
    @SuppressWarnings("serial")
	public void updateTableData(Calendar currentCal) {
		
		containerFrame.setCurrentSelectedDate(currentCal);
		
		Date currentDate = currentCal.getTime();
	    String selectedDateStr = UtilHelper.getDateStr(currentDate, "MM/dd/yyyy");
	    
	    DateColumn dateColumn = new DateColumn(currentCal);

		columns = new Object[]{"Period", dateColumn};
		
		schDateLabel.setText("Date:" + selectedDateStr);
	    schDateLabel.setPreferredSize(new Dimension(400,51));

	    List<ScheduleData> list = ScheduleManager.getScheduleDataForDate(currentCal);
	    data = new Object[periods.length][columns.length]; 
	    for (int i=0;i<periods.length;i++) {
	    		data[i][0] = periods[i];
	    }
	    for (ScheduleData schData: list) {
	    	String periodName = schData.getPeriodName();
	    	int periodId = schData.getPeriodId();
	    	
	    	data[periodId][0]= periodName;
	    	data[periodId][1]= schData;
	    	
	    }
	    
		schTableModel = new DefaultTableModel(data, columns) {
			 @Override
			    public boolean isCellEditable(int cellRow, int cellCol) {
				 	if (cellCol == 0) {
			        	return false;
			        } else {
			        	if (currentDate.before(UtilHelper.getCurrentDate().getTime())) {
					 		return false;
					 	} else {
					 		return true;
					 	}
			        }
			    }
		};
	    if (scheduleTable != null) {
	    	scheduleTable.setModel(schTableModel);
	    } else {
	    	scheduleTable = new JTable(schTableModel);
	    }
	    scheduleTable.setBorder(new EtchedBorder(EtchedBorder.RAISED));
	    scheduleTable.setShowGrid(true);
	    scheduleTable.setCellSelectionEnabled(true);
	    
	    scheduleTable.getColumnModel().getColumn(0).setPreferredWidth(10);
	    // If the selected date is past, then disable save button.
	    if (currentDate.before(UtilHelper.getCurrentDate().getTime())) {
	    	saveButton.setEnabled(false);
	    } else {
	    	saveButton.setEnabled(true);
	    }
	}
    
    
    /**
     * Save/Cancel Click event listener implementation.
     */
    public void actionPerformed(ActionEvent e) {
		// display/center the jdialog when the button is pressed
		String action = e.getActionCommand();
		if ("Save".equals(action)) {
			JOptionPane.showMessageDialog(containerFrame, "Successfully saved the schedule.", "Saved", JOptionPane.INFORMATION_MESSAGE);
			// Loop through each entry in the jtable, and save.
			for (int row=0;row<periods.length;row++) {
				for (int col=1; col<columns.length;col++) {
					//System.out.println("Value at cell "+row+","+col+":"+scheduleTable.getValueAt(row, col));
					// change the data in the map.
					Object cellValueObj = scheduleTable.getValueAt(row,  col);
					if (cellValueObj != null && cellValueObj instanceof String) {
						// change has been made.
						String newName = (String)cellValueObj;

						if (data[row][1] != null) {
							ScheduleData schData = (ScheduleData) data[row][1];
							String oldName = schData.getPersonName();
							if (newName == null || "".equals(newName)) {
								scheduleHelper.removeSchedule(oldName, schData.getPeriodName(), schData.getSchDate());
								schData = null;
							} else {
								schData.setPersonName(newName);
							}
							
							data[row][1] = schData;
							scheduleTable.setValueAt(schData, row, col);
						} else {
							// Old Data is empty.
							if (newName == null || "".equals(newName)) {
								// Nothing to do here.
							} else {
								// create new record.
								ScheduleData schData = new ScheduleData();
								schData.setPersonName(newName);
								schData.setPeriodId(row);
								schData.setPeriodName(periods[row]);
								
								DateColumn dateColumn = (DateColumn)columns[col];
								schData.setSchDate(dateColumn.getDateValue());
								schData.setPersonName(newName);
								
								scheduleHelper.addSchedule(newName, row, periods[row], dateColumn.getDateValue());
								data[row][1] = schData;
								scheduleTable.setValueAt(schData, row, col);

							}
							
						}
					}
				}
			}
		} else if ("Cancel".equals(e.getActionCommand())) {
			// Cancel is clicked.
			int ans = JOptionPane.showConfirmDialog(containerFrame, "You may loose the changes you have last typed. Do you want to cancel ?", "Cancel", JOptionPane.YES_NO_OPTION);
			if (ans == JOptionPane.YES_OPTION) {
				updateTableData(containerFrame.getCurrentSelectedDate());
			}
		}
        				
	}
    

}
