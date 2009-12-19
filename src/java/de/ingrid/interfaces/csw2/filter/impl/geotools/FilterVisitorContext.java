/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw2.filter.impl.geotools;

import java.util.Hashtable;
import java.util.Map;

public class FilterVisitorContext {

	protected String queryString = "";
	protected Map<FilterProperty, Object> properties = new Hashtable<FilterProperty, Object>();

	/**
	 * Set the InGrid query string that corresponds to the filter
	 * @param queryString
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	/**
	 * Append a string to the InGrid query string
	 * @param queryString
	 */
	public void appendQueryString(String queryString) {
		this.queryString += queryString;
	}

	/**
	 * Get the InGrid query string
	 * @return The query string
	 */
	public String getQueryString() {
		return queryString;
	}

	/**
	 * Set a FilterProperty value
	 * @param name
	 * @param value
	 */
	public void setProperty(FilterProperty name, Object value) {
		if (value == null) {
			properties.remove(name);
		} else {
			properties.put(name, value);
		}
	}

	/**
	 * Get a FilterProperty value
	 * @param name
	 * @return The value
	 */
	public Object getProperty(FilterProperty name) {
		if (properties.containsKey(name))
			return properties.get(name);
		else
			return null; 
	}
}
