package com.relevantwalk.churchcaldata.client;

/*
 * Interface for the dataListener for EventCollections
 * 
 */
public interface RWCalendarDataListener extends java.util.EventListener
{
	void onDataInit(RWEventCollection returnedEvents);
	void onDataError(String errMsg);
	//void onEventAdd (could be super interesting for a dynamically updated calendar)
}