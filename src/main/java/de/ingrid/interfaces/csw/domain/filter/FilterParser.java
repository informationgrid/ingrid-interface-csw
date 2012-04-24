/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.filter;

import org.geotoolkit.lucene.filter.SpatialQuery;
import org.w3c.dom.Document;

/**
 * @author ingo herwig <ingo@wemove.com>
 */
public interface FilterParser {

	/**
	 * Pass a OGC filter document into a Lucene query
	 * @param filterDoc
	 * @return SpatialQuery
	 * @throws Exception
	 */
	SpatialQuery parse(Document filterDoc) throws Exception;
}
