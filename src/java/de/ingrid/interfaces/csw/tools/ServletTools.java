/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.tools;

import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

/**
 * Miscellaneous functions to make life easier treating servlet functionality.
 * @author Dirk Schwarzmann
 * @date 2006-09-19
 */
public class ServletTools {
	/**
	 * Creates a Properties object containing all keys and values in the given http request.
	 * 
	 * @param request A servlet´s request as string representation
	 * @param uppercase Specify true if you want all key names to be in upper case. If false,
	 *        the case of the key names is left untouched. 
	 * @return java.util.Properties object containing all parameters and values. In case there
	 * were no parameters in the request, the Properties object remains empty (but is not null).
	 */
	public static Properties createPropertiesFromRequest(final HttpServletRequest request, final boolean uppercase) {
		Enumeration paramEnum = request.getParameterNames();
		Properties requestParams = new Properties();
		
		// Testing for uppercase once is faster than checking within the loop
		if (uppercase) {
			while (paramEnum.hasMoreElements()) {
				String key = (String) paramEnum.nextElement();
				requestParams.setProperty(key.toUpperCase(), request.getParameter(key));
			}
		} else {
			while (paramEnum.hasMoreElements()) {
				String key = (String) paramEnum.nextElement();
				requestParams.setProperty(key, request.getParameter(key));
			}
		}
		return requestParams;
	}
}
