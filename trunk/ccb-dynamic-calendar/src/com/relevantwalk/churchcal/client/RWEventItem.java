package com.relevantwalk.churchcal.client;

import java.util.Date;

public class RWEventItem {
	private String eventName;
	private Date eventStartDate;
	private Date eventEndDate;
	private String eventDescription;
	private Boolean isAllDayEvent;
	private String eventType;
	private String eventLocation;
	private String eventGroupName;

	public RWEventItem() {
		this.eventName = "";	
	}

	public RWEventItem(String eventName, Date eventDate) {
		this.eventName = eventName;
		this.eventStartDate = eventDate;	
	}
	/**
	 * @return the eventEndDate
	 */
	public Date getEventEndDate() {
		return eventEndDate;
	}

	/**
	 * @param eventEndDate the eventEndDate to set
	 */
	public void setEventEndDate(Date eventEndDate) {
		this.eventEndDate = eventEndDate;
	}

	/**
	 * @return the eventDescription
	 */
	public String getEventDescription() {
		return eventDescription;
	}

	/**
	 * @param eventDescription the eventDescription to set
	 */
	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}

	/**
	 * @return the isAllDayEvent
	 */
	public Boolean getIsAllDayEvent() {
		return isAllDayEvent;
	}

	/**
	 * @param isAllDayEvent the isAllDayEvent to set
	 */
	public void setIsAllDayEvent(Boolean isAllDayEvent) {
		this.isAllDayEvent = isAllDayEvent;
	}

	/**
	 * @return the eventType
	 */
	public String getEventType() {
		return eventType;
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	/**
	 * @return the eventLocation
	 */
	public String getEventLocation() {
		return eventLocation;
	}

	/**
	 * @param eventLocation the eventLocation to set
	 */
	public void setEventLocation(String eventLocation) {
		this.eventLocation = eventLocation;
	}

	/**
	 * @return the eventGroupName
	 */
	public String getEventGroupName() {
		return eventGroupName;
	}

	/**
	 * @param eventGroupName the eventGroupName to set
	 */
	public void setEventGroupName(String eventGroupName) {
		this.eventGroupName = eventGroupName;
	}

	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public Date getEventStartDate() {
		return eventStartDate;
	}
	public void setEventStartDate(Date eventDate) {
		this.eventStartDate = eventDate;
	}
}