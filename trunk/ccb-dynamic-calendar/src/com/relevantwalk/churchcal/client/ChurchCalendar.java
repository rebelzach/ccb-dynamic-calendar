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
		dynamicCalendar = new RWDynamicCalendar();
		// Associate the Main panel with the HTML host page.
		RootPanel.get().add(dynamicCalendar);
	}	
}






