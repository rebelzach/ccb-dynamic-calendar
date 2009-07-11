package com.relevantwalk.churchcal.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ChurchCalendar implements EntryPoint {
	
	private VerticalPanel mainPanel = new VerticalPanel();
	private TextArea xmlBox = new TextArea();
	private Label boxTitle = new Label();
	private DynamicCalendar dynamicCalendar;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		// Assemble Main panel.
		xmlBox.setCharacterWidth(90);
		xmlBox.setVisibleLines(10);
		xmlBox.setText("XML Soon!");
		String groupArg = Window.Location.getParameter("group"); 
		boxTitle.setText("Are you passing:"+ groupArg + " The HTTP call returned the following");
		//mainPanel.add(boxTitle);
		//mainPanel.add(eventList);
		dynamicCalendar = new DynamicCalendar();
		mainPanel.add(dynamicCalendar);
		
		// Associate the Main panel with the HTML host page.
		RootPanel.get().add(mainPanel);
		boxTitle.setText(boxTitle.getText() + "There are ");
	}	
}

class DynamicCalendar extends Composite implements CalendarDataListener
{
	private FlexTable calendarTable = new FlexTable();
	@SuppressWarnings("unused")
	private EventCollection calendarEvents;
	/*
	 * initializeCalendar Creates a flex table for the current month.
	 * TODO:Account for weeks that start on Monday
	 */
	@SuppressWarnings("deprecation")
	public DynamicCalendar() {
		initWidget(calendarTable);
		Date startDate = new Date();
		Date endDate = new Date();
		startDate.setDate(1);
		endDate.setDate(daysInMonth(endDate));
		calendarEvents = new EventCollection(startDate,endDate,this);
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
	public void onDataInit(EventCollection returnedEvents) {
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
		ArrayList<EventItem> eventList = returnedEvents.allEvents();
		for(Iterator<EventItem> it = eventList.iterator(); it.hasNext();)
		{
			EventItem item = (EventItem) it.next();
			testList += item.getEventName();
		}
		calendarTable.setWidget(0,0, new Label(testList));
	}

	@Override
	public void onDataError(String errMsg) {
		// TODO Auto-generated method stub
		
	}
	
}

class EventItem {
	private String eventName;
	private Date eventDate;
	
	public EventItem() {
		this.eventName = "";	
	}
	
	public EventItem(String eventName, Date eventDate) {
		this.eventName = eventName;
		this.eventDate = eventDate;	
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public Date getEventDate() {
		return eventDate;
	}
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}
}

/*
 * Interface for the dataListener for EventCollections
 * 
 */
interface CalendarDataListener extends java.util.EventListener
{
    void onDataInit(EventCollection returnedEvents);
    void onDataError(String errMsg);
    //void onEventAdd (could be super interesting when Google Wave comes out)
}

class EventCollection  {
	private ArrayList<CalendarDataListener> listeners = new ArrayList<CalendarDataListener>();
	private ArrayList<EventItem> eventList = new ArrayList<EventItem>();
	/*
	 * It is possible to add multiple listeners to this class though
	 * currently the Calendar widgets call their own EventCollections separately.
	 */
	public EventCollection(Date rangeStart, Date rangeEnd, CalendarDataListener dataListener){
		addCalendarDataListener(dataListener);
		fetchXML();
	}
	
	public ArrayList<EventItem> allEvents(){
		return eventList;
	}

	private void processXML(String xmlText){
		Document calendarDom = XMLParser.parse(xmlText);
		Element calendarElement = calendarDom.getDocumentElement();
		// Must do this if you ever use a raw node list that you expect to be
		// all elements.
		XMLParser.removeWhitespace(calendarElement);
		NodeList events = calendarElement.getElementsByTagName("item");

		for (int i = 0; i < events.getLength(); i++) {
			Element event = (Element) events.item(i);
			EventItem eventItem = new EventItem();
			eventItem.setEventName(getElementTextValue(event, "event_name"));
			eventList.add(eventItem);
		}
		for(Iterator<CalendarDataListener> it = listeners.iterator(); it.hasNext();)
		{
			CalendarDataListener listener = (CalendarDataListener) it.next();
			listener.onDataInit(this);
		}

	}

	/*
	 * Fetch the requested URL.
	 */
	private void fetchXML() {
		try {
			String queryURL = GWT.getModuleBaseURL() + "calendar.xml";
			RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, queryURL);
			requestBuilder.sendRequest(null, new ResponseTextHandler());
		} catch (RequestException ex) {
			Window.alert(ex.toString());
		}
	}

	/*
	 * Utility method to return the values of elements of the form <myTag>tag
	 * value</myTag>
	 */
	private String getElementTextValue(Element parent, String elementTag) {
		// If the xml is not coming from a known good source, this method would
		// have to include safety checks.
		return parent.getElementsByTagName(elementTag).item(0).getFirstChild()
		.getNodeValue();
	}

	public void addCalendarDataListener(CalendarDataListener listener)
    {
        listeners.add(listener);
    }

    public void removeCalendarDataListener(CalendarDataListener listener)
    {
        listeners.remove(listener);
    }

    /**
	 * Class for handling the response XML
	 */
	private class ResponseTextHandler implements RequestCallback {
		public void onError(Request request, Throwable exception) {
			for(Iterator<CalendarDataListener> it = listeners.iterator(); it.hasNext();)
			{
				CalendarDataListener listener = (CalendarDataListener) it.next();
				listener.onDataError(exception.toString());
			}
		}

		public void onResponseReceived(Request request, Response response) {
			String responseText = response.getText();
			processXML(responseText);
		}
	}

}

