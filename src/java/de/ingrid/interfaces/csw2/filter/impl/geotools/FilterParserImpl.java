/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw2.filter.impl.geotools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.filter.FilterFilter;
import org.geotools.gml.GMLFilterDocument;
import org.geotools.gml.GMLFilterGeometry;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterVisitor;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import de.ingrid.interfaces.csw2.filter.FilterParser;
import de.ingrid.interfaces.csw2.tools.XMLTools;
import de.ingrid.utils.query.IngridQuery;

/**
 * This FilterParser implementation uses the GeoTools library for parsing.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public class FilterParserImpl implements FilterParser {

	/** The logging object **/
	private static Log log = LogFactory.getLog(FilterParserImpl.class);

	@Override
	public IngridQuery parse(Document filterDoc) {

		try {
		    if (log.isDebugEnabled())
		    	log.debug("Parsing filter document: "+XMLTools.toString(filterDoc));
		
		    // setup ContentHandler
			FilterHandlerImpl filterHandler = new FilterHandlerImpl();
		    FilterFilter filterFilter = new FilterFilter(filterHandler, null);
		    GMLFilterGeometry filterGeometry = new GMLFilterGeometry(filterFilter);
		    GMLFilterDocument filterDocument = new GMLFilterDocument(filterGeometry);

		    XMLReader reader = null;
			reader = XMLReaderFactory.createXMLReader();
		    reader.setContentHandler(filterDocument);
			reader.parse(new InputSource(XMLTools.toString(filterDoc)));
	    
		    Filter filter = filterHandler.getFilter();
		    if (log.isDebugEnabled())
		    	log.debug("Parsed filter: "+filter.toString());
	
		    FilterVisitorContext ctx = new FilterVisitorContext();
			
			FilterVisitor visitor = new IngridFilterVisitor();
			ctx = (FilterVisitorContext)filter.accept(visitor, ctx);
		    if (log.isDebugEnabled())
		    	log.debug("Resulting query string: "+ctx.getQueryString());
		    
		    return null;

		} catch (Exception e) {
			throw new RuntimeException("Error parsing filter document: "+e);
		}
	}
}
