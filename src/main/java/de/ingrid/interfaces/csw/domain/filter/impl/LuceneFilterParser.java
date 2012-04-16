/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.filter.impl;

import org.apache.lucene.search.Query;
import org.w3c.dom.Document;

import de.ingrid.interfaces.csw.domain.filter.FilterParser;

/**
 * A FilterParser that creates a Lucene query from an ogc filter document.
 * 
 * @author ingo@wemove.com
 */
public class LuceneFilterParser implements FilterParser {

	@Override
	public Query parse(Document filterDoc) {
		return null;
	}
}
