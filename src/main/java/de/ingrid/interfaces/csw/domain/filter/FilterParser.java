/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.filter;

import org.apache.lucene.search.Query;
import org.w3c.dom.Document;

/**
 * @author ingo herwig <ingo@wemove.com>
 */
public interface FilterParser {

	/**
	 * Pass a OGC filter document into a Lucene query
	 * @param filterDoc
	 * @return Query
	 * @throws Exception
	 */
	Query parse(Document filterDoc) throws Exception;
}
