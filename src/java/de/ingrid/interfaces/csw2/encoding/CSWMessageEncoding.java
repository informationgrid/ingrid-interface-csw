/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2.encoding;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.ingrid.interfaces.csw2.constants.Operation;
import de.ingrid.interfaces.csw2.exceptions.CSWException;

public interface CSWMessageEncoding {

	/**
	 * Initialize the encoding with the content to encode
	 * @param request
	 * @param response
	 */
	void initialize(HttpServletRequest request, HttpServletResponse response);

	/**
	 * Get the request
	 * @return HttpServletRequest
	 */
	HttpServletRequest getRequest();

	/**
	 * Get the response
	 * @return HttpServletResponse
	 */
	HttpServletResponse getResponse();

	/**
	 * Check if the request message conforms with general non-operation-specific
	 * requirements.
	 * @throws CSWException
	 */
	void validateRequest() throws CSWException;

	/**
	 * Check if the response message conforms with general non-operation-specific
	 * requirements.
	 * @throws CSWException
	 */
	void validateResponse() throws CSWException;

	/**
	 * Get the requested operation
	 * @return The Operation
	 */
	Operation getOperation();

	/**
	 * Get the versions that the client accepts for the getCapablities request
	 * @return The versions as array
	 */
	List<String> getAcceptVersions();
}
