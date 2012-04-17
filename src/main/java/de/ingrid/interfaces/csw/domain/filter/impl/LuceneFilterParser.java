/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.filter.impl;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apache.lucene.search.Query;
import org.w3c.dom.Document;

import de.ingrid.interfaces.csw.domain.filter.FilterParser;
import de.ingrid.interfaces.csw.domain.filter.ogc1_0_0.FilterType;

/**
 * A FilterParser that creates a Lucene query from an ogc filter document.
 * 
 * @author ingo@wemove.com
 */
public class LuceneFilterParser implements FilterParser {

	@Override
	public Query parse(Document filterDoc) throws Exception {
		JAXBContext context = JAXBContext.newInstance(FilterType.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		JAXBElement<FilterType> filterEl = unmarshaller.unmarshal(filterDoc, FilterType.class);
		FilterType filter = filterEl.getValue();
		// TODO actually transform the filter into a Lucene query

		return null;
	}
}
