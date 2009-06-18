/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2.request.impl;

import de.ingrid.interfaces.csw2.exceptions.CSWException;
import de.ingrid.interfaces.csw2.request.DescribeRecordRequest;

public class DescribeRecordRequestImpl extends AbstractRequestImpl implements DescribeRecordRequest {

	@Override
	public void validate() throws CSWException {

		validateVersion();
	}
}
