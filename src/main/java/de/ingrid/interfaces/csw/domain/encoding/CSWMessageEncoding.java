/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.domain.encoding;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    public static enum Type {
        CSW,
        CSWT
    }

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
	 * @param type TODO
	 * @return A List
	 */
	List<Operation> getSupportedOperations(Type type);

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
	 * @throws Exception
	 */
	void reportError(Exception e) throws Exception;
}
