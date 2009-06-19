/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2.request;

import de.ingrid.interfaces.csw2.query.CSWQuery;

public interface GetRecordsRequest extends CSWRequest {
	
	/**
	 * Get the CSWQuery that describes the records to fetch 
	 * @return CSWQuery
	 */
	CSWQuery getQuery();
}
