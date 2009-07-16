package com.relevantwalk.churchcal.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/*
 * Interface for the dataListener for EventLinks
 * 
 */
interface RWEventLinkListener extends java.util.EventListener
{
	void onDisplayDetail(RWEventItem returnedItem);
}

interface RWDayPanelListener extends java.util.EventListener
{
	void onDisplayDay (RWEventItem returnedItem);
}

public class RWDynamicCalendarDetailHelper implements RWDayPanelListener, RWEventLinkListener{
	private PopupPanel simplePopup;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onDisplayDetail(RWEventItem returnedItem) {
		simplePopup = new PopupPanel(true);
		simplePopup.setStyleName("rwdc-popup");
		simplePopup.setAnimationEnabled(true);
		VerticalPanel popupPanel = new VerticalPanel();
		popupPanel.add(new Label(returnedItem.getEventName()));
		popupPanel.add(new Label(returnedItem.getEventDescription()));
		if (!(returnedItem.getIsAllDayEvent())){
			popupPanel.add(new Label(returnedItem.getEventStartDate().toString()));
			popupPanel.add(new Label(returnedItem.getEventEndDate().toString()));
		}
		popupPanel.add(new Label(returnedItem.getEventLocation()));
		popupPanel.add(new Label(returnedItem.getEventGroupName()));
		simplePopup.setWidth((Window.getClientWidth()/2 - 60) + "px");
		simplePopup.add(popupPanel);
		
		int itemDay = returnedItem.getEventStartDate().getDay();
		if (itemDay < 3) {
			int left = Window.getClientWidth()/2 - 30;
			int top = 90;
			simplePopup.setPopupPosition(left, top);
		} else {
			int left = 30;
			int top = 90;
			simplePopup.setPopupPosition(left, top);
		}
		simplePopup.show();
		
	}

	@Override
	public void onDisplayDay(RWEventItem returnedItem) {
		// TODO Auto-generated method stub
		
	}
	
}