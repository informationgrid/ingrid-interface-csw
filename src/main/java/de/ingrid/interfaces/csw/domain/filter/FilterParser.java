/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.filter;

import org.w3c.dom.Document;

import de.ingrid.utils.query.IngridQuery;

/**
 * @author ingo herwig <ingo@wemove.com>
 */
public interface FilterParser {

	/**
	 * Pass a OGC filter document into a IngridQuery
	 * @param filterDoc
	 * @return IngridQuery
	 */
	IngridQuery parse(Document filterDoc);
}
