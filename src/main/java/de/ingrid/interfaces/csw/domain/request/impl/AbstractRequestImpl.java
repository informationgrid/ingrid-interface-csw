/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.request.impl;

import de.ingrid.interfaces.csw.domain.encoding.CSWMessageEncoding;
import de.ingrid.interfaces.csw.domain.exceptions.CSWException;
import de.ingrid.interfaces.csw.domain.request.CSWRequest;

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
		return this.encoding;
	}
}
