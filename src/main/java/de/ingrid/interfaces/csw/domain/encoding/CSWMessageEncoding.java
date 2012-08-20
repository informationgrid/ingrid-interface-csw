/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.encoding;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;

import de.ingrid.interfaces.csw.domain.constants.Operation;
import de.ingrid.interfaces.csw.domain.exceptions.CSWException;
import de.ingrid.interfaces.csw.domain.exceptions.CSWOperationNotSupportedException;
import de.ingrid.interfaces.csw.domain.query.CSWQuery;

/**
 * CSWMessageEncoding defines the interface for dealing with
 * different formats of csw messages, for example requests that are
 * defined in key value pairs via the HTTP Get method or SOAP
 * messages sent via the HTTP POST method.
 * 
 * All format specific information retrieval and formatting is
 * handled in CSWMessageEncoding implementations.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public interface CSWMessageEncoding {

	/**
	 * Initialize the encoding with the content to encode
	 * @param request
	 * @param response
	 */
	void initialize(HttpServletRequest request, HttpServletResponse response);

	/**
	 * Request specific operations
	 */

	/**
	 * Get the request
	 * @return HttpServletRequest
	 */
	HttpServletRequest getRequest();

	/**
	 * Check if the request message conforms with general non-operation-specific
	 * requirements.
	 * @throws CSWException
	 */
	void validateRequest() throws CSWException;

	/**
	 * Get the supported operations
	 * @return A List
	 */
	List<Operation> getSupportedOperations();

	/**
	 * Get the requested operation
	 * @return The Operation
	 * @throws CSWOperationNotSupportedException
	 */
	Operation getOperation() throws CSWOperationNotSupportedException;

	/**
	 * Get the versions that the client accepts for the GetCapablities request
	 * @return The versions as array
	 */
	List<String> getAcceptVersions();

	/**
	 * Get the version that the client requests in the DescribeRecord request
	 * @return The version
	 */
	String getVersion();

	/**
	 * Get the csw query from the request
	 * @return CSWQuery
	 * @throws CSWException
	 */
	CSWQuery getQuery() throws CSWException;

	/**
	 * Response specific operations
	 */

	/**
	 * Get the response
	 * @return HttpServletResponse
	 */
	HttpServletResponse getResponse();

	/**
	 * Check if the response message conforms with general non-operation-specific
	 * requirements.
	 * @throws CSWException
	 */
	void validateResponse() throws CSWException;

	/**
	 * Write the result document to the response.
	 * @param document
	 * @throws Exception
	 */
	void writeResponse(Document document) throws Exception;

	/**
	 * Write an exception to the response.
	 * @param exception
	 * @throws Exception
	 */
	void reportError(Exception e) throws Exception;
}