/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.request;

import de.ingrid.interfaces.csw.domain.exceptions.CSWException;
import de.ingrid.interfaces.csw.domain.query.CSWQuery;

public interface GetRecordByIdRequest extends CSWRequest {

	/**
	 * Get the CSWQuery that describes the record to fetch
	 * @return CSWQuery
	 * @throws CSWException
	 */
	CSWQuery getQuery() throws CSWException;
}
