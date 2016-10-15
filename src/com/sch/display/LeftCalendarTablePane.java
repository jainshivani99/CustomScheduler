package com.sch.display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 * A Month View Calendar Panel - consists of a JTable, JButton, and JLabel.
 * 
 * When the data is selected, the schedule for the selected date will be read using ScheduleManager methods, and refresh the right side Schedule JTable.
 * @author
 *
 */
public class LeftCalendarTablePane extends JPanel {

	/**
	* 
	*/
	private static final long serialVersionUID = -2081502697843001533L;
	
	private String[] columns = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

	private Calendar cal = new GregorianCalendar();

	private int currentMonth = 1;
	private int currentDay = 1;
	private int currentYear = 2016;
	private int currentRow = 0;
	private int currentCol = 0;


	public LeftCalendarTablePane(MainDisplayFrame parent, Calendar selectedDateCal) {
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		this.setPreferredSize(new Dimension(250, 100));
		
		setCalendarTablePane(parent, selectedDateCal);
	}

	/**
	 * Sets up the initial display of the Month View JTable for the given date.
	 * 
	 * @param parent - a container frame, reference to the MainDisplayFrame object.
	 * @param selectedDateCal - the date selected for the Schedule.
	 */
	public void setCalendarTablePane(MainDisplayFrame parent, Calendar selectedDateCal) {

		int daySelected = selectedDateCal.get(Calendar.DAY_OF_MONTH);

		JLabel monthTextLabel = new JLabel();
		monthTextLabel.setHorizontalAlignment(SwingConstants.CENTER);

		DefaultTableModel tableModel = new DefaultTableModel(null, columns);
		
		JButton leftArrowBtn = new JButton("<-");
		leftArrowBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent ae) {
				cal.add(Calendar.MONTH, -1);
				updateMonth(daySelected,tableModel,monthTextLabel);
			}
		});

		JButton rightArrowBtn = new JButton("->");
		rightArrowBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				cal.add(Calendar.MONTH, +1);
				updateMonth(daySelected,tableModel,monthTextLabel);
			}
		});

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(leftArrowBtn, BorderLayout.WEST);
		panel.add(monthTextLabel, BorderLayout.CENTER);
		panel.add(rightArrowBtn, BorderLayout.EAST);
		
		
		JTable monthCalendarJTable = new JTable(tableModel);
		monthCalendarJTable.setPreferredSize(new Dimension(200, 100));

		JScrollPane pane = new JScrollPane(monthCalendarJTable);
		pane.setPreferredSize(new Dimension(200, 100));

		monthCalendarJTable.setPreferredScrollableViewportSize(monthCalendarJTable.getPreferredSize());
		monthCalendarJTable.setFillsViewportHeight(true);

		this.add(panel, BorderLayout.NORTH);
		this.add(pane, BorderLayout.CENTER);

		updateMonth(daySelected,tableModel,monthTextLabel);

		monthCalendarJTable.setCellSelectionEnabled(true);
		monthCalendarJTable.changeSelection(currentRow, currentCol, false, false);

		monthCalendarJTable.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int row = monthCalendarJTable.rowAtPoint(evt.getPoint());
				int col = monthCalendarJTable.columnAtPoint(evt.getPoint());
				if (row >= 0 && col >= 0) {
					String value = String.valueOf(monthCalendarJTable.getValueAt(row, col));
					currentDay = Integer.parseInt(value);
					Calendar currentCal = Calendar.getInstance();
					currentCal.set(currentYear, currentMonth - 1, currentDay, 0, 0, 0);
					currentCal.set(Calendar.MILLISECOND, 0);
					parent.fireDateChangedEvent(currentCal);
				}
			}
		});
	}

	/**
	 * When the data is selected, the month JTable is refreshed in this method.
	 * 
	 * @param daySelected - the selected date in the month calendar.
	 * @param tableModel - Table Model behind the JTable data source.
	 * @param monthTextLabel - JLabel reference to change the month/year.
	 */
	private void updateMonth(int daySelected, DefaultTableModel tableModel,JLabel monthTextLabel) {
		cal.set(Calendar.DAY_OF_MONTH, 1);

		String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
		int year = cal.get(Calendar.YEAR);
		monthTextLabel.setText(month + " " + year);

		currentMonth = cal.get(Calendar.MONTH) + 1;
		currentYear = year;

		int startDay = cal.get(Calendar.DAY_OF_WEEK);
		int numberOfDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		int weeks = cal.getActualMaximum(Calendar.WEEK_OF_MONTH);

		tableModel.setRowCount(0);
		tableModel.setRowCount(weeks);

		int i = startDay - 1;
		for (int day = 1; day <= numberOfDays; day++) {
			tableModel.setValueAt(day, i / 7, i % 7);
			if (day == daySelected) {
				currentRow = i / 7;
				currentCol = i % 7;
			}
			i = i + 1;
		}

	}

}
