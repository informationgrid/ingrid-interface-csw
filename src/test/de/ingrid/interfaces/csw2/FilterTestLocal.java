/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2;

import java.io.StringReader;

import org.w3c.dom.Document;

import de.ingrid.interfaces.csw2.data.TestFilter;
import de.ingrid.interfaces.csw2.filter.FilterParser;
import de.ingrid.interfaces.csw2.tools.XMLTools;
import de.ingrid.utils.query.IngridQuery;

public class FilterTestLocal extends OperationTestBase {

	/**
	 * Test ogc filter parsing 
	 * @throws Exception
	 */
	public void testGeoToolsFilterParsing() throws Exception {
		
		FilterParser parser = new de.ingrid.interfaces.csw2.filter.impl.geotools.FilterParserImpl();
		Document filterDoc = XMLTools.parse(new StringReader(TestFilter.SIMPLE_FILTER));
		IngridQuery query = parser.parse(filterDoc);
		
		assertTrue("The query is not null.", query != null);
	}
}
