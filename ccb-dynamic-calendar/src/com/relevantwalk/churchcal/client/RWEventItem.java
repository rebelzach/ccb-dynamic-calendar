package com.relevantwalk.churchcal.client;

import java.util.Date;

class RWEventItem {
	private String eventName;
	private Date eventDate;

	public RWEventItem() {
		this.eventName = "";	
	}

	public RWEventItem(String eventName, Date eventDate) {
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