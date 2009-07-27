package com.relevantwalk.churchcal.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

public class RWDCUrlArgument {
	private String urlArgument;
	private boolean urlArgumentDefined;

	RWDCUrlArgument(String requestParameter) {
		//A border
		String urlArgument = Window.Location.getParameter(requestParameter);
		if (urlArgument == null){
			this.urlArgument = "";
			urlArgumentDefined = false;
		} else {
			//Security Check for this parameter
			urlArgument.replaceAll("/(<([^>]+)>)/ig", "");
			this.urlArgument = urlArgument;
			urlArgumentDefined = true;
		}
		GWT.log("Parameter: " + requestParameter + " retrieved", null);
	}
	
	/**
	 * @return the urlArgument
	 */
	public String getUrlArgument() {
		return urlArgument;
	}

	/**
	 * @return the borderWidth
	 */
	public int toInt() {
		if (isDefined()){
			try{
				return Integer.parseInt(urlArgument);
			} catch (NumberFormatException e) {
				throw e;
			}
		} else {
			return 0;
		}
	}
	
	public boolean toBoolean() {
		if (isDefined()){
			if (urlArgument.compareToIgnoreCase("true") == 0){
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	

	/**
	 * @return the borderDefined
	 */
	public boolean isDefined() {
		return urlArgumentDefined;
	}
}