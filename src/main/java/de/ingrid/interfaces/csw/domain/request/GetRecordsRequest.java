/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.request;

import de.ingrid.interfaces.csw.domain.query.CSWQuery;

public interface GetRecordsRequest extends CSWRequest {

	/**
	 * Get the CSWQuery that describes the records to fetch
	 * @return CSWQuery
	 */
	CSWQuery getQuery();
}
