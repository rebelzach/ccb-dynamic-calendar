package com.relevantwalk.churchcal.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ChurchCalendar implements EntryPoint {
	
	private VerticalPanel mainPanel = new VerticalPanel();
	private RWDynamicCalendar dynamicCalendar;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		// Assemble Main panel.
		//Note: Example of pulling a parameter passed in the URL
		//String groupArg = Window.Location.getParameter("group"); 
		dynamicCalendar = new RWDynamicCalendar();
		mainPanel.add(dynamicCalendar);
		
		// Associate the Main panel with the HTML host page.
		RootPanel.get().add(mainPanel);
	}	
}






