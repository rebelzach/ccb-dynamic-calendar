package com.relevantwalk.churchcal.client;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

public class RWEventLink extends Composite implements ClickHandler{
	private ArrayList<RWEventLinkListener> listeners = new ArrayList<RWEventLinkListener>();
	private RWEventItem eventItem;
	private Label label = new Label("" , false);
	
	public RWEventLink(RWEventItem eventItem, RWEventLinkListener listener){
		addEventLinkListener(listener);
		this.eventItem = eventItem;
		initWidget(label);
		label.addClickHandler(this);
		label.setText(eventItem.getEventName());
	}
	
	public void addEventLinkListener(RWEventLinkListener listener)
	{
		listeners.add(listener);
	}

	public void removeEventLinkListener(RWEventLinkListener listener)
	{
		listeners.remove(listener);
	}

	@Override
	public void onClick(ClickEvent event) {
		for(Iterator<RWEventLinkListener> it = listeners.iterator(); it.hasNext();)
		{
			RWEventLinkListener listener = (RWEventLinkListener) it.next();
			listener.onDisplayDetail(eventItem);
		}
	}
}