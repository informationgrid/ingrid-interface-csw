/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2.encoding.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.ingrid.interfaces.csw2.encoding.CSWMessageEncoding;

/**
 * AbstractEncoding defines methods common to all CSWMessageEncoding
 * implementation.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public abstract class AbstractEncoding implements CSWMessageEncoding {

	private HttpServletRequest request;
	private HttpServletResponse response;

	@Override
	public HttpServletRequest getRequest() {
		return this.request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public HttpServletResponse getResponse() {
		return this.response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

}
