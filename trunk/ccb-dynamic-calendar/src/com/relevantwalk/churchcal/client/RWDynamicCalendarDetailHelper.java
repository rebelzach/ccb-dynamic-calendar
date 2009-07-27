package com.relevantwalk.churchcal.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.relevantwalk.churchcaldata.client.RWEventItem;

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

	public void onDisplayDetail(RWEventItem returnedItem) {
		Date currentDate = returnedItem.getEventStartDate();
		FlowPanel popupPanel = buildPopup(currentDate);
		
		//Popup Header
		Label popupSubheadText = new Label("Event Detail");
		Label popupDate = new Label(DateTimeFormat.getLongDateFormat().format(currentDate));
		Label popupEventName = new Label(returnedItem.getEventName());
		popupSubheadText.setStyleName("rwdc-popup-detail-subhead-text");
		popupDate.setStyleName("rwdc-popup-detail-event-date");
		popupEventName.setStyleName("rwdc-popup-detail-event-name");
		popupPanel.add(popupEventName);
		popupPanel.add(popupDate);
		popupPanel.add(popupSubheadText);
		
		//Event Description
		if (!(returnedItem.getEventDescription() == "")){
			Label descriptionLabel = new Label("Description:");
			Label eventDescriptionLabel = new Label(returnedItem.getEventDescription());
			descriptionLabel.addStyleName("rwdc-detail-label");
			eventDescriptionLabel.addStyleName("rwdc-event-description");
			//FlowPanel descWrapper = new FlowPanel();
			popupPanel.add(descriptionLabel);
			popupPanel.add(eventDescriptionLabel);
			//popupPanel.add(descWrapper);
		}
		
		//Event Time
		if (!(returnedItem.getIsAllDayEvent())){
			Label timeLabel = new Label("Time:");
			String startTime = DateTimeFormat.getShortTimeFormat().format(returnedItem.getEventStartDate());
			String endTime = DateTimeFormat.getShortTimeFormat().format(returnedItem.getEventEndDate());
			Label eventTimeLabel = new Label(startTime + "-" + endTime);
			timeLabel.addStyleName("rwdc-detail-label");
			eventTimeLabel.addStyleName("rwdc-event-description");
			popupPanel.add(timeLabel);
			popupPanel.add(eventTimeLabel);
		}
		
		//Event Location
		if (!(returnedItem.getEventLocation() == "")){
			Label locationLabel = new Label("Location:");
			Label eventLocationLabel = new Label(returnedItem.getEventLocation()); 
			locationLabel.addStyleName("rwdc-detail-label");
			eventLocationLabel.addStyleName("rwdc-event-description");
			popupPanel.add(locationLabel);
			popupPanel.add(eventLocationLabel);
		}
		
		//Event Group
		if (!(returnedItem.getEventGroupName().isEmpty() || returnedItem.getEventGroupName().contains("Entire Church Group") )){
			Label groupLabel = new Label("Group:");
			Label eventGroupName = new Label(returnedItem.getEventGroupName()); 
			groupLabel.addStyleName("rwdc-detail-label");
			eventGroupName.addStyleName("rwdc-event-description");
			popupPanel.add(groupLabel);
			popupPanel.add(eventGroupName);
		}
		
		simplePopup.show();
		
	}

	public void onDisplayDay(ArrayList<RWEventItem> eventList) {
		Date currentDate = eventList.get(0).getEventStartDate();
		FlowPanel popupLayout = buildPopup(currentDate);
		
		Label popupTitle = new Label("Events");
		Label popupDate = new Label(DateTimeFormat.getLongDateFormat().format(currentDate));
		VerticalPanel daysEventsPanel = new VerticalPanel();
		popupLayout.add(popupTitle);
		popupLayout.add(popupDate);
		popupLayout.add(daysEventsPanel);
		
		//Fill in events from date
		int insertIndex = 0;
		int itemsAdded = 0;
		for(Iterator<RWEventItem> it = eventList.iterator(); it.hasNext();)
		{
			RWEventItem item = (RWEventItem) it.next();
			insertIndex = 0;
			if (itemsAdded > 0) {
				for (;insertIndex < itemsAdded; insertIndex++) { //Sorting Loop
					RWEventLink link = (RWEventLink) daysEventsPanel.getWidget(insertIndex);
					//If the event we are adding is before the event in the list
					if (item.getEventStartDate().before(link.getEventItem().getEventStartDate()))
						break;
				}
			}
				final RWEventLink newEventLink = new RWEventLink(item, this);
				daysEventsPanel.insert(newEventLink, insertIndex);
				itemsAdded++;
			
		}
		
		simplePopup.show();
	}
	@SuppressWarnings("deprecation")
	private FlowPanel buildPopup(Date currentDate) {

		//Get a new popup
		simplePopup.hide();
		simplePopup = new PopupPanel(true);
		simplePopup.setStyleName("rwdc-popup");
		simplePopup.setAnimationEnabled(true);
		
		//bits for a dropshadow
		FlowPanel popupShadow = new FlowPanel();
		popupShadow.setStyleName("shadow");
		simplePopup.add(popupShadow);
		
		//Layout Container
		FlowPanel popupPanel = new FlowPanel();
		popupPanel.setStyleName("rwdc-popup-content");
		popupShadow.add(popupPanel);
		
		//Size the popup
		int popupMinimumWidth = 375;
		int popupWidth = Window.getClientWidth()/2 - 60;
		if (popupWidth < popupMinimumWidth) popupWidth = popupMinimumWidth;
		simplePopup.setWidth(popupWidth + "px");
		
		GWT.log("Window Width:" + Window.getClientWidth() + "Popup Width" + popupWidth + "LeftSide Calc" +((Window.getClientWidth() - popupWidth) - 30), null);
		
		//Position the popup
		int itemDay = currentDate.getDay();
		if (itemDay < 3) {
			int left = (Window.getClientWidth() - popupWidth) - 70;
			int top = 90;
			simplePopup.setPopupPosition(left, top);
		} else {
			int left = 30;
			int top = 90;
			simplePopup.setPopupPosition(left, top);
		}
		
		//Popup close button
		PushButton popupClose = new PushButton();
		popupClose.setStylePrimaryName("rwdc-popup-closebutton");
		popupClose.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					simplePopup.hide();
				}
			});
		popupPanel.add(popupClose);
		return popupPanel;
	}
	
}