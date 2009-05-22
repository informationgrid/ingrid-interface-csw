/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2.encoding.impl;

import java.util.List;

import de.ingrid.interfaces.csw2.constants.Operation;
import de.ingrid.interfaces.csw2.encoding.CSWMessageEncoding;
import de.ingrid.interfaces.csw2.exceptions.CSWException;
import de.ingrid.interfaces.csw2.exceptions.CSWInvalidParameterValueException;

public class XMLEncoding extends AbstractEncoding implements CSWMessageEncoding {

	@Override
	public void validateRequest() throws CSWException {
		
		String contentType = this.getRequest().getContentType();
		if (contentType.toLowerCase().indexOf("application/xml") == -1) {
			throw new CSWInvalidParameterValueException("Unsupported Content-Type in request: "+contentType+
					". Expected Content-Type: application/xml", "Content-Type");
		}
	}

	@Override
	public void validateResponse() throws CSWException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getAcceptVersions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Operation getOperation() {
		// TODO Auto-generated method stub
		return null;
	}
}
