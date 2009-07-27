package com.relevantwalk.churchcal.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.relevantwalk.churchcaldata.client.RWEventItem;

public class RWEventLink extends Composite implements ClickHandler{
	private ArrayList<RWEventLinkListener> listeners = new ArrayList<RWEventLinkListener>();
	private RWEventItem eventItem;
	private FlowPanel linkPanel = new FlowPanel();
	private Label nameLabel;
	
	public RWEventLink(RWEventItem eventItem, RWEventLinkListener listener){
		addEventLinkListener(listener);
		this.eventItem = eventItem;
		initWidget(linkPanel);
		
		if (!(eventItem.getIsAllDayEvent())){
			Label timeLabel = new Label(buildTime(eventItem.getEventStartDate()));
			timeLabel.setStyleName("rwdc-time");
			linkPanel.add(timeLabel);
			timeLabel.addClickHandler(this);
		}
		nameLabel = new Label(eventItem.getEventName(),true);
		nameLabel.setStyleName("rwdc-event-name");
		
		linkPanel.add(nameLabel);
		
		
		nameLabel.addClickHandler(this);
	}
	
	public void collapse() {
		nameLabel.setWordWrap(false);
	}
	
	@SuppressWarnings("deprecation")
	private String buildTime(Date date){ //needs to use GWT date formatting
		String ampm;
		int hours = date.getHours();
		if (hours > 12) {
			ampm = "pm";
			hours = hours - 12;
		} else ampm = "am";
		String minutes = Integer.toString(date.getMinutes());
		if (minutes.length() == 1) minutes = "0" + minutes;
		String time = Integer.toString(hours) + ":" + minutes + ampm;
		return time;
	}
	
	/**
	 * @return the eventItem
	 */
	public RWEventItem getEventItem() {
		return eventItem;
	}
	
	public void addEventLinkListener(RWEventLinkListener listener)
	{
		listeners.add(listener);
	}

	public void removeEventLinkListener(RWEventLinkListener listener)
	{
		listeners.remove(listener);
	}

	public void onClick(ClickEvent event) {
		//not sure multiple listeners makes sense
		for(Iterator<RWEventLinkListener> it = listeners.iterator(); it.hasNext();)
		{
			RWEventLinkListener listener = (RWEventLinkListener) it.next();
			listener.onDisplayDetail(eventItem);
		}
	}
}