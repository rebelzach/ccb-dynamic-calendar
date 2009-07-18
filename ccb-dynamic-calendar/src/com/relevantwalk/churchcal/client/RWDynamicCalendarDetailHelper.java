package com.relevantwalk.churchcal.client;

import java.util.ArrayList;
import java.util.Iterator;

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
	void onDisplayDay (ArrayList<RWEventItem> eventList);
}

public class RWDynamicCalendarDetailHelper implements RWDayPanelListener, RWEventLinkListener{
	private PopupPanel simplePopup = new PopupPanel(true);;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onDisplayDetail(RWEventItem returnedItem) {
		simplePopup.hide();
		simplePopup = new PopupPanel(true);
		simplePopup.setStyleName("rwdc-popup");
		simplePopup.setAnimationEnabled(true);
		VerticalPanel popupPanel = new VerticalPanel();
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
		popupPanel.add(new Label(returnedItem.getEventName()));
		popupPanel.add(new Label(returnedItem.getEventDescription()));
		if (!(returnedItem.getIsAllDayEvent())){
			popupPanel.add(new Label(returnedItem.getEventStartDate().toString()));
			popupPanel.add(new Label(returnedItem.getEventEndDate().toString()));
		}
		popupPanel.add(new Label(returnedItem.getEventLocation()));
		popupPanel.add(new Label(returnedItem.getEventGroupName()));
		
		simplePopup.show();
		
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onDisplayDay(ArrayList<RWEventItem> eventList) {
		simplePopup.hide();
		simplePopup = new PopupPanel(true);
		simplePopup.setStyleName("rwdc-popup");
		simplePopup.setAnimationEnabled(true);
		VerticalPanel popupPanel = new VerticalPanel();
		simplePopup.setWidth((Window.getClientWidth()/2 - 60) + "px");
		simplePopup.add(popupPanel);
		
		int itemDay = eventList.get(0).getEventStartDate().getDay();
		if (itemDay < 3) {
			int left = Window.getClientWidth()/2 - 30;
			int top = 90;
			simplePopup.setPopupPosition(left, top);
		} else {
			int left = 30;
			int top = 90;
			simplePopup.setPopupPosition(left, top);
		}
		int insertIndex = 0;
		int itemsAdded = 0;
		for(Iterator<RWEventItem> it = eventList.iterator(); it.hasNext();)
		{
			RWEventItem item = (RWEventItem) it.next();
			insertIndex = 0;
			if (itemsAdded > 0) {
				for (;insertIndex < itemsAdded; insertIndex++) { //Sorting Loop
					RWEventLink link = (RWEventLink) popupPanel.getWidget(insertIndex);
					//If the event we are adding is before the event in the list
					if (item.getEventStartDate().before(link.getEventItem().getEventStartDate()))
						break;
				}
			}
				final RWEventLink newEventLink = new RWEventLink(item, this);
				popupPanel.insert(newEventLink, insertIndex);
				itemsAdded++;
			
		}
		
		simplePopup.show();
	}
	
}