/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2.request.impl;


import de.ingrid.interfaces.csw2.exceptions.CSWException;
import de.ingrid.interfaces.csw2.request.GetCapabilitiesRequest;

public class GetCapabilitiesRequestImpl extends AbstractRequestImpl implements GetCapabilitiesRequest {
	
	@Override
	public void validate() throws CSWException {

		validateVersion();
	}
}
