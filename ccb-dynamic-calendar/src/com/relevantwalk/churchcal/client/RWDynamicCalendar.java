package com.relevantwalk.churchcal.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import java.lang.Math;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.relevantwalk.churchcaldata.client.RWEventCollection;
import com.relevantwalk.churchcaldata.client.RWEventItem;
import com.relevantwalk.churchcaldata.client.RWCalendarDataListener;


public class RWDynamicCalendar extends Composite implements RWCalendarDataListener
{
	private final int headerOffset = 58;
	private FlexTable calendarTable = new FlexTable();
	private VerticalPanel mainPanel = new VerticalPanel();
	private FlowPanel navPanel= new FlowPanel();
	private Button prevMonthButton = new Button("Prev",
			new ClickHandler() {
				public void onClick(ClickEvent event) {
					goToPrevMonth();
				}
			});
	private Button nextMonthButton = new Button("Next", 
			new ClickHandler() {
				public void onClick(ClickEvent event) {
					goToNextMonth();
				}
			});
	private Label monthTitle = new Label();
	@SuppressWarnings("unused")
	private RWEventCollection calendarEvents;
	private int currentYear;
	private int currentMonth;
	private int firstDayofMonth; //the "Day" of the 1st
	private int dayCount;
	private int previousMonthDayCount;
	private int borderValue = 2;
	private int cellHeight;
	private int cellWidth;
	private Date firstDate;
	private Date endDate;
	private RWDynamicCalendarDetailHelper detailHelper = new RWDynamicCalendarDetailHelper();
	
	/*
	 * initializeCalendar Creates a flextable for the current month.
	 * TODO:Account for weeks that start on Monday
	 */
	@SuppressWarnings("deprecation")
	public RWDynamicCalendar() {
		initWidget(mainPanel);
		RWDCUrlArgument borderArg = new RWDCUrlArgument("border");
		if (borderArg.isDefined()) {
			try {
				borderValue = borderArg.toInt();
			} catch(NumberFormatException e) { 
				//Do nothing
			}
		}
		
		styleCalendar();
		
		int windowWidth = Window.getClientWidth(); //breathing room
		cellWidth = ((windowWidth - borderValue)/(7)) - borderValue;
		
		navPanel.setStyleName("rwdc-navPanel");
		monthTitle.setStyleName("rwdc-navPanel-month");
		FlowPanel navButtonPanel = new FlowPanel();
		navButtonPanel.setStyleName("rwdc-navPanel-buttons-wrapper");
		navButtonPanel.add(prevMonthButton);
		prevMonthButton.setStyleName("rwdc-navPanel-buttons");
		nextMonthButton.setStyleName("rwdc-navPanel-buttons");
		navButtonPanel.add(nextMonthButton);
		navPanel.add(navButtonPanel);
		navPanel.add(monthTitle);
		mainPanel.setStyleName("rwdc-calendarMainPanel");
		mainPanel.add(navPanel);
		FlexTable headerTable = new FlexTable();
		headerTable.setStyleName("rwdc-header");
		String[] daysArray = {"Sunday", "Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
		for (int i=0; i < daysArray.length; i++){
			headerTable.setWidget(0, i, new Label(daysArray[i]));
			headerTable.getCellFormatter().setWidth(0, i, cellWidth + "px");
		}
		FlowPanel calendarPanel = new FlowPanel();
		mainPanel.add(headerTable);
		
		calendarPanel.setStyleName("rwdc-calendarTable");
		calendarPanel.add(calendarTable);
		mainPanel.add(calendarPanel);
		mainPanel.setCellHeight(calendarTable, "100%");
		calendarTable.setBorderWidth(0);
		calendarTable.setCellSpacing(0);
		calendarTable.setCellPadding(0);
		calendarTable.setWidget(0, 0, new Label("-"));
		Date today = new Date();
		
		buildCalendar(today.getMonth(),today.getYear());
	}
	
	private void styleCalendar() {
		RWDCUrlArgument borderArg = new RWDCUrlArgument("border");
		int borderValue = this.borderValue; //Yes this is getting redeclared
		if (borderArg.isDefined()) {
			boolean isInteger = true;
			try {
				borderValue = borderArg.toInt();
			} catch (NumberFormatException e){
				isInteger = false;
			}
			
			if (isInteger == true) {
				Element borderStyle = DOM.createElement("style");
				borderStyle.setInnerText(	".rwdc-calendarMainPanel td { " +
											"border-width: " + borderValue + "px " + borderValue + "px 0 0;" +
											"}"+
											".rwdc-calendarMainPanel table { " +
											"border-width: 0 0 " + borderValue + "px " + borderValue + "px;" +
											"}");
				
				DOM.insertChild(mainPanel.getElement(), borderStyle, 0);
			}
		}
		
	}
	
	@SuppressWarnings("deprecation")
	private void buildCalendar(int month, int year){
		Date dayInMonth = new Date(year,month,1);
		int currentCol = 0;
		int currentRow = 0;
		
		//These are set right off the bat for speed purposes, they are accessed frequently
		firstDayofMonth = dayofFirst(dayInMonth);
		dayCount = daysInMonth(dayInMonth);
		previousMonthDayCount = daysInPreviousMonth(dayInMonth);
		currentMonth = month;
		currentYear = year;
		monthTitle.setText(getMonthName(currentMonth) + " " + (currentYear + 1900));
		
		
		//this gets the coordinates for the size of the table
		int endCoordRow = ((int) Math.ceil((firstDayofMonth + dayCount)/7.0)) - 1;
		int endCoordCol = 6;
		int windowHeight = Window.getClientHeight();
		cellHeight = 
			((windowHeight - borderValue - headerOffset)/(endCoordRow + 1)) - borderValue;
		
		firstDate = gridCoordtoDate(0,0);
		endDate = gridCoordtoDate(endCoordCol, endCoordRow);
		endDate.setHours(23);
		endDate.setMinutes(59);
		int maxCells = (int) (Math.ceil((firstDayofMonth + dayCount)/7.0))*7;
		int rowCount = calendarTable.getRowCount();
		if (rowCount > 0) {
			for (int i = 0; (i < rowCount); i++ )
				calendarTable.removeRow(0);
		}
		
		for(int i = 1;i <= maxCells;i++) { 
			Date currentDate = gridCoordtoDate(currentCol,currentRow);
			boolean isMonthDay = (currentDate.getMonth() == currentMonth);
			calendarTable.setWidget(currentRow, currentCol, new DayPanel(
					currentDate.getDate(),isMonthDay, cellHeight, cellWidth));
			if (isMonthDay) {
				calendarTable.getFlexCellFormatter().setStyleName(currentRow, currentCol, "rwdc-calCell");
			} else {
				calendarTable.getFlexCellFormatter().setStyleName(currentRow, currentCol, "rwdc-calCell-inactive");
			}
			calendarTable.getFlexCellFormatter().setHeight(currentRow, currentCol, cellHeight + "px");
			calendarTable.getFlexCellFormatter().setWidth(currentRow, currentCol, cellWidth + "px");
			
			//If its Saturday go to Sunday of next week
			if (currentCol == 6){
				currentCol = 0;
				currentRow++;
			} else {
				// If its not Saturday move to the next day
				currentCol++;
			}
		}
		
		calendarEvents = new RWEventCollection(firstDate,endDate,this);
	}
	
	private void goToNextMonth() {
		int month;
		int year;	
		if (currentMonth == 11){
			month = 0;
			year = currentYear + 1;
		} else {
			month = currentMonth + 1;
			year = currentYear;
		}
		buildCalendar(month,year);
	}
	
	private void goToPrevMonth() {
		int month;
		int year;
		
		if (currentMonth == 0){
			month = 11;
			year = currentYear - 1;
		} else {
			month = currentMonth - 1;
			year = currentYear;
		}
		buildCalendar(month,year);
	}
	
	private String getMonthName(int month){
		String monthName;
		switch (month) {
	        case 0:  monthName = "January"; break;
	        case 1:  monthName = "February"; break;
	        case 2:  monthName = "March"; break;
	        case 3:  monthName = "April"; break;
	        case 4:  monthName = "May"; break;
	        case 5:  monthName = "June"; break;
	        case 6:  monthName = "July"; break;
	        case 7:  monthName = "August"; break;
	        case 8:  monthName = "September"; break;
	        case 9:  monthName = "October"; break;
	        case 10: monthName = "November"; break;
	        case 11: monthName = "December"; break;
	        default: monthName = "Invalid month.";break;
		}
		return monthName;
	}

	/**
	 * @return the currentYear
	 */
	public int getCurrentYear() {
		return currentYear;
	}

	/**
	 * @param currentYear the currentYear to set
	 */
	public void setCurrentYear(int currentYear) {
		this.currentYear = currentYear;
	}

	/**
	 * @return the currentMonth
	 */
	public int getCurrentMonth() {
		return currentMonth;
	}

	/**
	 * @param currentMonth the currentMonth to set
	 */
	public void setCurrentMonth(int currentMonth) {
		this.currentMonth = currentMonth;
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

	public void onDataInit(RWEventCollection returnedEvents) {
		ArrayList<RWEventItem> eventList = returnedEvents.allEvents();
		for(Iterator<RWEventItem> it = eventList.iterator(); it.hasNext();)
		{
			RWEventItem item = (RWEventItem) it.next();
			if (!(item.getEventStartDate().before(firstDate) || item.getEventStartDate().after(endDate))){
				RWGridCoord widgetPosition = dateToGridCoords(item.getEventStartDate());
				((DayPanel) calendarTable.getWidget(widgetPosition.getY(),widgetPosition.getX())).addEvent(item);
			}
		}
		
	}
	
	/*
	 * Returns the date of grid point of a 7 day wide grid
	 */
	@SuppressWarnings("deprecation")
	private Date gridCoordtoDate(int col, int row){
		//a formula for deciding whether we are in the current month
		int currentIndex = (row * 7 + col) - firstDayofMonth + 1;
		Date returnDate = new Date(currentYear,currentMonth,1);
		
		//Logic to decide what the date is.
		if (currentIndex >= 1 && currentIndex <= dayCount ){
			returnDate.setDate(currentIndex);
		} else if (currentIndex < 1){
			returnDate.setMonth(returnDate.getMonth() - 1);
			returnDate.setDate(currentIndex + previousMonthDayCount);
			
		} else if (currentIndex > dayCount){  
			returnDate.setMonth(returnDate.getMonth() + 1);
			returnDate.setDate(currentIndex - dayCount);
			
		} else {
			//TODO we should be throwing an error here
		}

		return returnDate;
	}
	
	@SuppressWarnings("deprecation")
	private RWGridCoord dateToGridCoords(Date date){
		RWGridCoord gridCoord = new RWGridCoord();
		int column = 0;
		int row = 0;
		int gridCount = 0;
		
		if (currentMonth == date.getMonth()){
			gridCount = date.getDate() + firstDayofMonth;
			column = (gridCount%7)-1;
			row = (int) Math.floor(gridCount/7);
			if (column == -1){ 
				column = 6;
				row--;
			}
		} else if ((currentMonth - 1) == date.getMonth()){
			gridCount = (firstDayofMonth - 1) + (date.getDate()-previousMonthDayCount);
			if (gridCount >= 0){	
				column = gridCount;
				row = 0;
			} else {
				//TODO This should throw an out of range exception
			}
		} else if ((currentMonth + 1) == date.getMonth()){
			gridCount = firstDayofMonth  + dayCount + date.getDate();
			column = (gridCount%7)-1;
			row = (int) Math.floor(gridCount/7);
			if (column == -1){ 
				column = 6;
				row--;
			}
		} else {
			//TODO This should throw an out of range exception
		}
		if (column < 0 || column > 6) {
			column = 0;
			Window.alert("column out of bounds");
		}
		if (row < 0 || row > 6) {
			row = 0;
			Window.alert("row out of bounds");
		}
		gridCoord.setX(column);
		gridCoord.setY(row);
		return gridCoord;
	}
	
	public void onDataError(String errMsg) {
		// TODO Auto-generated method stub

	}
	/*
	 * A tiny class to handle returning grid coordinates 
	 * (Is there a better way to do handle x,y coords in GWT?)
	 */
	private class RWGridCoord {
		private int x;
		private int y;
		RWGridCoord(int x, int y){
			this.x = x;
			this.y = y;
		}
		RWGridCoord(){
			this.x = 0;
			this.y = 0;
		}
		public int getX() {
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		public int getY() {
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}
	}

	
	private class DayPanel extends Composite implements ClickHandler{
		private FlowPanel dayPanel = new FlowPanel();
		private FlowPanel titlePanel = new FlowPanel();
		private FlowPanel eventsPanel = new FlowPanel();
		private int date;
		private int panelHeight;
		private int panelWidth;
		private boolean panelOverloaded = false;
		private int overloadCounter = 1;
		private boolean isMonthDay;
		private ArrayList<RWEventItem> eventList = new ArrayList<RWEventItem>();
		
		/*
		 * Creates a day panel with no events
		 * isMonthDay: 	True when the panel is part of the current month
		 * 				Only affects styling of the panel
		 */
		public DayPanel(int date, boolean isMonthDay, int height, int width){
			initWidget(dayPanel); //called in the constructor just so its called only once
			panelHeight = height;
			panelWidth = width;
			this.date = date;
			this.isMonthDay = isMonthDay;
			buildDayPanel();

		}
		
		private void buildDayPanel(){
			Label titleLabel = new Label(Integer.toString(date));
			titleLabel.addClickHandler(this);
			titlePanel.add(titleLabel);
			if (isMonthDay){
				dayPanel.setStyleName("rwdc-dayPanel");
			} else {
				dayPanel.setStyleName("rwdc-dayPanel-notcurrent");
			}	
			dayPanel.setHeight(panelHeight + "px");
			dayPanel.setWidth(panelWidth + "px");
			dayPanel.add(titlePanel);
			dayPanel.add(eventsPanel);
			eventsPanel.setStyleName("rwdc-eventpane");
			titlePanel.setStyleName("rwdc-day-date-title");
		}
		
		private boolean roomForEvent() {
			final int panelCount = eventsPanel.getWidgetCount();
			int eventPanelHeight = (eventsPanel.getOffsetHeight());
			int eventAvgHeight = eventPanelHeight/panelCount;
			int titlePanelOffset = eventsPanel.getAbsoluteTop() - titlePanel.getAbsoluteTop();
			eventPanelHeight = eventPanelHeight + titlePanelOffset;
			return (panelHeight - (eventPanelHeight) > eventAvgHeight);
		}
		/*
		 * public so that if you ever want to add events dynamically...
		 */
		public void addEvent(RWEventItem eventItem){
			eventList.add(eventItem);
			if (panelOverloaded) {
				overloadCounter++;
				addEventOverload();
				return;
			}
			final int panelCount = eventsPanel.getWidgetCount();
			boolean shouldCollapse = false;
			int insertIndex = 0;
			if(panelCount > 0) { 
					//if another event won't fit
					if (!roomForEvent()){
						//Ask the links to collapse
						for (;insertIndex < panelCount; insertIndex++) {
							RWEventLink link = (RWEventLink) eventsPanel.getWidget(insertIndex);
							link.collapse();
						}
						//If there is still no room
						if (!roomForEvent()){
							overloadCounter++;
							panelOverloaded = true;
							addEventOverload();
							return;
						} else {
							//The event we're adding should collapse because all the others were asked to.
							shouldCollapse = true;
						}
					}
					//Sort through events by date
					for (;insertIndex < panelCount; insertIndex++) { 
						RWEventLink link = (RWEventLink) eventsPanel.getWidget(insertIndex);
						//If the event we are adding is before the event in the list
						if (eventItem.getEventStartDate().before(link.getEventItem().getEventStartDate()))
							break;
					}

				}
			final RWEventLink newEventLink = new RWEventLink(eventItem, detailHelper);
			if (shouldCollapse) newEventLink.collapse();
			eventsPanel.insert(newEventLink, insertIndex);
			
			
		}
		
		private void addEventOverload(){
			final int panelCount = eventsPanel.getWidgetCount();
			eventsPanel.remove(panelCount - 1);
			Label overloadLabel = new Label("+" + overloadCounter + " more");
			overloadLabel.setStyleName("rwdc-moreevents");
			overloadLabel.addClickHandler(this);
			eventsPanel.add(overloadLabel);
		}
		
		/*
		 * public so that if you ever want to add events dynamically...
		 */
		public void addEvents(ArrayList<RWEventItem> daysEvents){
			for(Iterator<RWEventItem> it = daysEvents.iterator(); it.hasNext();)
			{
				RWEventItem item = (RWEventItem) it.next();
				addEvent(item);
			}
		}

		public void onClick(ClickEvent event) {
			// TODO Auto-generated method stub
			detailHelper.onDisplayDay(eventList);
		}

		/**
		 * @return the eventList
		 */
		public ArrayList<RWEventItem> getEventList() {
			return eventList;
		}
	}
}