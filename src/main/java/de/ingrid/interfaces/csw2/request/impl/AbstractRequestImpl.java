/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2.request.impl;

import de.ingrid.interfaces.csw2.encoding.CSWMessageEncoding;
import de.ingrid.interfaces.csw2.exceptions.CSWException;
import de.ingrid.interfaces.csw2.request.CSWRequest;

/**
 * AbstractRequestImpl provides a generic initialize method and
 * methods for common validations to be used in subclasses.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public abstract class AbstractRequestImpl implements CSWRequest {

	private CSWMessageEncoding encoding = null;

	@Override
	public void initialize(CSWMessageEncoding encoding) throws CSWException {
		this.encoding = encoding;
	}

	/**
	 * Get the CSWMessageEncodingInstance
	 * @return the encoding
	 */
	public CSWMessageEncoding getEncoding() {
		return encoding;
	}
}
