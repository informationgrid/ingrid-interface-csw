/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2.response;

import org.w3c.dom.Document;

import de.ingrid.interfaces.csw2.encoding.CSWMessageEncoding;

public interface CSWResponse {
	
	/**
	 * Initialize the request instance
	 * @param encoding
	 */
	void initialize(CSWMessageEncoding encoding);

	/**
	 * Set the response content
	 * @param document
	 */
	void setContent(Document document);
	
	/**
	 * Write the response content to the response
	 * @throws Exception 
	 */
	void serialize() throws Exception;
}
