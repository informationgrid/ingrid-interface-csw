/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.request;

import de.ingrid.interfaces.csw.domain.encoding.CSWMessageEncoding;
import de.ingrid.interfaces.csw.domain.exceptions.CSWException;

/**
 * CSWRequest defines the common interface for all csw operations.
 * Inheriting interfaces may define additional operation specific
 * methods.
 * NOTE: CSWRequest instances are mainly used for validation and
 * storage of operation specific data, the operation execution is
 * done in the CSWServer instance.
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
	 * Check if the request is valid. Only operation-specific validation
	 * should be done here. Common validation is done in CSWMessageEncoding.validateRequest
	 * @return boolean
	 * @throws CSWException
	 */
	void validate() throws CSWException;
}
