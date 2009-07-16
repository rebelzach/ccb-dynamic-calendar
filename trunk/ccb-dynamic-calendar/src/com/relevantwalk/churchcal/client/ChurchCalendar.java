package com.relevantwalk.churchcal.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class ChurchCalendar implements EntryPoint {
	private RWDynamicCalendar dynamicCalendar;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		// Assemble Main panel.
		//Note: Example of pulling a parameter passed in the URL
		//String groupArg = Window.Location.getParameter("group"); 
		dynamicCalendar = new RWDynamicCalendar();
		// Associate the Main panel with the HTML host page.
		RootPanel.get().add(dynamicCalendar);
	}	
}






