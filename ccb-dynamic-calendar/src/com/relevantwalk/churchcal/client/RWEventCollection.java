package com.relevantwalk.churchcal.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;



/*
 * A data access class for the CCB API. This IS NOT a proper RPC implementation. 
 * It simply asks for the XML with an asynchronous HTTP request
 * To use this class you must add a listener to know when the data has been retrieved
 */
public class RWEventCollection  {
	private ArrayList<RWCalendarDataListener> listeners = new ArrayList<RWCalendarDataListener>();
	private ArrayList<RWEventItem> eventList = new ArrayList<RWEventItem>();
	private Date rangeStart;
	private Date rangeEnd;
	/*
	 * It is possible to add multiple listeners to this class though
	 * currently the Calendar widgets call their own EventCollections separately.
	 */
	public RWEventCollection(Date rangeStart, Date rangeEnd, RWCalendarDataListener dataListener){
		addCalendarDataListener(dataListener);
		this.rangeStart = rangeStart;
		this.rangeEnd = rangeEnd;
		fetchXML(rangeStart,rangeEnd);
	}
	
	/**
	 * @return the rangeStart
	 */
	public Date getRangeStart() {
		return rangeStart;
	}

	/**
	 * @return the rangeEnd
	 */
	public Date getRangeEnd() {
		return rangeEnd;
	}

	public ArrayList<RWEventItem> allEvents(){
		return eventList;
	}
	
	//TODO Complete Functionality
	public ArrayList<RWEventItem> eventsOnDate(){
		ArrayList<RWEventItem> events = new ArrayList<RWEventItem>();
		for(Iterator<RWEventItem> it = eventList.iterator(); it.hasNext();)
		{
			RWEventItem event = (RWEventItem) it.next();
			event.getEventStartDate();
		}
		return events;
	}

	@SuppressWarnings("deprecation")
	private void processXML(String xmlText){
		String passedGroup = Window.Location.getParameter("group");
		if (passedGroup == null){
			GWT.log("No Group parameter", null);
			passedGroup = "";
		}
		
		Document calendarDom = XMLParser.parse(xmlText);
		Element calendarElement = calendarDom.getDocumentElement();
		// Must do this if you ever use a raw node list that you expect to be
		// all elements.
		XMLParser.removeWhitespace(calendarElement);
		NodeList events = calendarElement.getElementsByTagName("item");

		for (int i = 0; i < events.getLength(); i++) {
			//This loop is HUGE, lazy loading of data is probably in order...
			Element event = (Element) events.item(i);
			//EVENT Name
			String eventName;
			try {
				eventName = getElementTextValue(event, "event_name");
			} catch (Exception e) {
				continue; //An event without a name! Get out now!
			}
			
			//EVENT Date
			String dateStr;
			Date eventStartDate;
			try {
				dateStr = getElementTextValue(event, "date");
			} catch (Exception e) {
				continue; //An event without a date! Get out now!
			}
			int eventYear = Integer.parseInt(dateStr.substring(0, 4));
			eventYear = eventYear - 1900;
			int eventMonth = Integer.parseInt(dateStr.substring(5, 7))-1;
			int eventDate = Integer.parseInt(dateStr.substring(8, 10));
			eventStartDate = new Date(eventYear,eventMonth,eventDate);
			
			//EVENT Description
			String eventDescription;
			try {
				eventDescription = getElementTextValue(event, "event_description");
			}catch (Exception e) {
				eventDescription = "";
			}
			
			//EVENT Start Time
			String startTimeStr;
			try {
				startTimeStr = getElementTextValue(event, "start_time");
			}catch (Exception e) {
				startTimeStr = "00:00:00";
			}
			int startEventHours = Integer.parseInt(startTimeStr.substring(0, 2));
			int startEventMinutes = Integer.parseInt(startTimeStr.substring(3, 5));
			int startEventSeconds = Integer.parseInt(startTimeStr.substring(6, 8));
			eventStartDate.setHours(startEventHours);
			eventStartDate.setMinutes(startEventMinutes);
			eventStartDate.setSeconds(startEventSeconds);
			
			//EVENT End Time
			String endTimeStr;
			Date eventEndDate;
			try {
				endTimeStr = getElementTextValue(event, "end_time");
			}catch (Exception e) {
				endTimeStr = "00:00:00";
			}
			int endEventHours = Integer.parseInt(endTimeStr.substring(0, 2));
			int endEventMinutes = Integer.parseInt(endTimeStr.substring(3, 5));
			int endEventSeconds = Integer.parseInt(endTimeStr.substring(6, 8));
			eventEndDate = new Date(eventYear,eventMonth,eventDate);
			eventEndDate.setHours(endEventHours);
			eventEndDate.setMinutes(endEventMinutes);
			eventEndDate.setSeconds(endEventSeconds);

			//EVENT All Day Event
			boolean isAllDayEvent;
			if (startTimeStr == "00:00:00" && endTimeStr == "23:59:00"){
				isAllDayEvent = true;
			} else {
				isAllDayEvent = false;
			}
			
			//EVENT Type Registration Required or Open To All
			String eventType;
			try {
				eventType = getElementTextValue(event, "event_type");
			}catch (Exception e) {
				eventType = "";
			}
		
			//EVENT location
			String eventLocation;
			try {
				eventLocation = getElementTextValue(event, "location");
			}catch (Exception e) {
				eventLocation = "";
			}
		
			//EVENT group name
			String eventGroupName;
			try {
				eventGroupName = getElementTextValue(event, "group_name");
			}catch (Exception e) {
				eventGroupName = "";
			}
			
			if ((!(passedGroup.isEmpty())) && (!(passedGroup.equalsIgnoreCase(eventGroupName)))){
				GWT.log("You have passed a group and this event does not match:" + passedGroup, null);
				continue;
			}
			
			//EVENT group id
			String eventGroupid;
			try {
				Element groupNameElement =  (Element) event.getElementsByTagName("group_name").item(0);
				eventGroupid = groupNameElement.getAttribute("ccb_id");
			}catch (Exception e) {
				eventGroupid = "";
			}
			//EVENT group_type //TODO Does this need to be implemented?
			
			//all the following calls to eventItem could probably be rolled into an eventItem constructor

			RWEventItem eventItem = new RWEventItem();
			eventItem.setEventName(eventName);
			eventItem.setEventStartDate(eventStartDate);
			eventItem.setEventEndDate(eventEndDate);
			eventItem.setEventDescription(eventDescription);
			eventItem.setIsAllDayEvent(isAllDayEvent);
			eventItem.setEventType(eventType);
			eventItem.setEventLocation(eventLocation);
			eventItem.setEventGroupName(eventGroupName);
			eventItem.setEventGroupid(eventGroupid);
			eventList.add(eventItem);
		}
		
		for(Iterator<RWCalendarDataListener> it = listeners.iterator(); it.hasNext();)
		{
			RWCalendarDataListener listener = (RWCalendarDataListener) it.next();
			listener.onDataInit(this);
		}

	}

	/*
	 * Fetch the requested URL.
	 */
	private void fetchXML(Date rangeStart, Date rangeEnd) {
		try {
			//String dateStart = DateTimeFormat.getFormat("yyyy-MM-dd").format(rangeStart);
			//String dateEnd = DateTimeFormat.getFormat("yyyy-MM-dd").format(rangeEnd);
			//String queryURL = GWT.getModuleBaseURL() + "ccbcal.php?date_start=" + dateStart + "&date_end=" + dateEnd;
			String queryURL = GWT.getModuleBaseURL() + "calendar.xml"; //for Debug to get around the cross-scripting issue
			GWT.log("Query:"+ queryURL,null);
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

	public void addCalendarDataListener(RWCalendarDataListener listener)
	{
		listeners.add(listener);
	}

	public void removeCalendarDataListener(RWCalendarDataListener listener)
	{
		listeners.remove(listener);
	}

	/**
	 * Class for handling the response XML
	 */
	private class ResponseTextHandler implements RequestCallback {
		public void onError(Request request, Throwable exception) {
			for(Iterator<RWCalendarDataListener> it = listeners.iterator(); it.hasNext();)
			{
				RWCalendarDataListener listener = (RWCalendarDataListener) it.next();
				listener.onDataError(exception.toString());
			}
		}

		public void onResponseReceived(Request request, Response response) {
			String responseText = response.getText();
			processXML(responseText);
		}
	}

}