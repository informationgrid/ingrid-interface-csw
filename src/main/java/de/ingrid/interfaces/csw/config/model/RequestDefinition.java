/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
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
	private int pause = 10;

	/**
	 * The number of records to request at once during fetching of records.
	 */
	private int recordsPerCall = 10;

	/**
	 * The query string to use when fetching records.
	 */
	private String queryString = "";

    /**
     * The proxy id of the iPlug.
     */
    private String plugId = "";
	
	
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

    public void setPlugId(String plugId) {
        this.plugId = plugId;
    }

    public String getPlugId() {
        return plugId;
    }
}
