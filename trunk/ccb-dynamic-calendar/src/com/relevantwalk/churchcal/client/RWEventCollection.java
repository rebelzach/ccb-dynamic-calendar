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
 * Interface for the dataListener for EventCollections
 * 
 */
interface RWCalendarDataListener extends java.util.EventListener
{
    void onDataInit(RWEventCollection returnedEvents);
    void onDataError(String errMsg);
    //void onEventAdd (could be super interesting when Google Wave comes out)
}

class RWEventCollection  {
	private ArrayList<RWCalendarDataListener> listeners = new ArrayList<RWCalendarDataListener>();
	private ArrayList<RWEventItem> eventList = new ArrayList<RWEventItem>();
	/*
	 * It is possible to add multiple listeners to this class though
	 * currently the Calendar widgets call their own EventCollections separately.
	 */
	public RWEventCollection(Date rangeStart, Date rangeEnd, RWCalendarDataListener dataListener){
		addCalendarDataListener(dataListener);
		fetchXML();
	}
	
	public ArrayList<RWEventItem> allEvents(){
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
			RWEventItem eventItem = new RWEventItem();
			eventItem.setEventName(getElementTextValue(event, "event_name"));
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