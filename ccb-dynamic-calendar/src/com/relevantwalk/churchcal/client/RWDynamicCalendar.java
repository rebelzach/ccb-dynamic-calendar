package com.relevantwalk.churchcal.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;


class RWDynamicCalendar extends Composite implements RWCalendarDataListener
{
	private FlexTable calendarTable = new FlexTable();
	@SuppressWarnings("unused")
	private RWEventCollection calendarEvents;
	/*
	 * initializeCalendar Creates a flex table for the current month.
	 * TODO:Account for weeks that start on Monday
	 */
	@SuppressWarnings("deprecation")
	public RWDynamicCalendar() {
		initWidget(calendarTable);
		Date startDate = new Date();
		Date endDate = new Date();
		startDate.setDate(1);
		endDate.setDate(daysInMonth(endDate));
		calendarEvents = new RWEventCollection(startDate,endDate,this);
	}
	
	@SuppressWarnings("deprecation")
	private int daysInMonth(Date inputDate) {
		Date testDate = new Date(inputDate.getTime());
		testDate.setDate(32);
		int daysInMonth = 32 - testDate.getDate();
		return daysInMonth;
	}
	@SuppressWarnings("deprecation")
	private int daysInPreviousMonth(Date inputDate) {
		int daysInMonth = 0;
		Date testDate = new Date(inputDate.getTime()); //Duplicate for safety
		int previousMonth = testDate.getMonth()-1;
		if (previousMonth == -1) { //-1 means December of the previous year
			daysInMonth = 31;
		} else {
			testDate.setMonth(previousMonth);
			daysInMonth = daysInMonth(testDate);
		}
		return daysInMonth;
	}
	@SuppressWarnings("deprecation")
	private int dayofFirst(Date inputDate) {
		Date testDate = new Date(inputDate.getTime());
		testDate.setDate(1);
		int dayofWeek = testDate.getDay();
		return dayofWeek;
	}
	@Override
	public void onDataInit(RWEventCollection returnedEvents) {
		Date requestedDate = new Date();
		int firstDayofMonth = dayofFirst(requestedDate);
		int dayCount = daysInMonth(requestedDate);
		int previousMonthDayCount = daysInPreviousMonth(requestedDate);
		int currentCol = 0;
		int currentRow = 0;
		int currentDate = 1;
		boolean tableFull = false;
		while (tableFull == false) { 
			//a formula for deciding whether we are in the current month
			currentDate = (currentRow * 7 + currentCol) - firstDayofMonth + 1;

			//Logic to decide when to populate a date value
			if (currentDate >= 1 && currentDate <= dayCount ){
				calendarTable.setWidget(currentRow, currentCol, new Label(Integer.toString(currentDate)));
				calendarTable.getFlexCellFormatter().setStyleName(currentRow, currentCol, "cal-table-cell");
			}
			/* Logic for preceeding days
			 *TODO: Previous month formatting
			 */
			if (currentDate < 1){
				calendarTable.setWidget(currentRow, currentCol, new Label(Integer.toString(currentDate + previousMonthDayCount)));
				calendarTable.getFlexCellFormatter().setStyleName(currentRow, currentCol, "cal-table-cell");
			}

			/* Logic for subsequent days
			 *TODO: Next month formatting
			 */
			if (currentDate > dayCount){  
				calendarTable.setWidget(currentRow, currentCol, new Label(Integer.toString(currentDate - dayCount)));
				calendarTable.getFlexCellFormatter().setStyleName(currentRow, currentCol, "cal-table-cell");
				}

			if (currentDate >= dayCount && currentCol == 6 ){
				//for now we are done evacuate!
				tableFull = true;
			}

			//If its Saturday go to Sunday of next week
			if (currentCol == 6){
				currentCol = 0;
				currentRow++;
			} else {
				// If its not Saturday move to the next day
				currentCol++;
			}
		}
		String testList = "";
		ArrayList<RWEventItem> eventList = returnedEvents.allEvents();
		for(Iterator<RWEventItem> it = eventList.iterator(); it.hasNext();)
		{
			RWEventItem item = (RWEventItem) it.next();
			testList += item.getEventName();
		}
		calendarTable.setWidget(0,0, new Label(testList));
	}

	@Override
	public void onDataError(String errMsg) {
		// TODO Auto-generated method stub
		
	}
	
}