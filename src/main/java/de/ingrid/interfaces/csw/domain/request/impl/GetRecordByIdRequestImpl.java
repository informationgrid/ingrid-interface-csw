/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.request.impl;

import de.ingrid.interfaces.csw.domain.exceptions.CSWException;
import de.ingrid.interfaces.csw.domain.exceptions.CSWMissingParameterValueException;
import de.ingrid.interfaces.csw.domain.query.CSWQuery;
import de.ingrid.interfaces.csw.domain.request.GetRecordByIdRequest;

public class GetRecordByIdRequestImpl extends AbstractRequestImpl implements GetRecordByIdRequest {

	@Override
	public void validate() throws CSWException {
		CSWQuery query = this.getEncoding().getQuery();
		if (query.getIds() == null || query.getIds().size() == 0) {
			throw new CSWMissingParameterValueException("Id is not specified or has no value", "GetRecordById");
		}
	}

	@Override
	public CSWQuery getQuery() {
		return this.getEncoding().getQuery();
	}
}
