/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2.request;

import de.ingrid.interfaces.csw2.encoding.CSWMessageEncoding;
import de.ingrid.interfaces.csw2.exceptions.CSWException;

/**
 * CSWRequest defines the common interface for all csw operations.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public interface CSWRequest {

	/**
	 * Initialize the request instance
	 * @param encoding
	 * @throws CSWException
	 */
	void initialize(CSWMessageEncoding encoding) throws CSWException;

	/**
	 * Check if the request is valid
	 * @return boolean
	 * @throws CSWException
	 */
	void validate() throws CSWException;
}
