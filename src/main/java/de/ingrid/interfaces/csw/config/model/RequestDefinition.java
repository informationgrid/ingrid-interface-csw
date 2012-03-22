/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.config.model;

/**
 * Configuration for a harvesting request.
 * 
 * @author ingo@wemove.com
 */
public class RequestDefinition {

	/**
	 * The time in msec to wait for a response after sending a request
	 * to the harvesting source.
	 */
	private int timeout = 1000;

	/**
	 * The time in msec to pause between requests to harvesting source.
	 */
	private int pause = 1000;

	/**
	 * The number of records to request at once during fetching of records.
	 */
	private int recordsPerCall = 10;

	/**
	 * The query string to use when fetching records.
	 */
	private String queryString = "";

	/**
	 * Get the time in msec to wait for a response after sending a request
	 * to the harvesting source.
	 * 
	 * @return int
	 */
	public int getTimeout() {
		return this.timeout;
	}

	/**
	 * Set the time in msec to wait for a response after sending a request
	 * to the harvesting source.
	 * 
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * Get the time in msec to pause between requests to harvesting source.
	 * 
	 * @return int
	 */
	public int getPause() {
		return this.pause;
	}

	/**
	 * Set the time in msec to pause between requests to harvesting source.
	 * 
	 * @param pause
	 */
	public void setPause(int pause) {
		this.pause = pause;
	}

	/**
	 * Get the number of records to request at once during fetching of records.
	 * 
	 * @return int
	 */
	public int getRecordsPerCall() {
		return this.recordsPerCall;
	}

	/**
	 * Set the number of records to request at once during fetching of records.
	 * 
	 * @param recordsPerCall
	 */
	public void setRecordsPerCall(int recordsPerCall) {
		this.recordsPerCall = recordsPerCall;
	}

	/**
	 * Get the query string to use when fetching records.
	 * 
	 * @return String
	 */
	public String getQueryString() {
		return this.queryString;
	}

	/**
	 * Set the query string to use when fetching records.
	 * 
	 * @param queryString
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	@Override
	public String toString() {
		return "Query:'"+this.queryString+"', RecordsPerCall:"+this.recordsPerCall+", Pause:"+this.pause+", Timeout:"+this.timeout;
	}
}