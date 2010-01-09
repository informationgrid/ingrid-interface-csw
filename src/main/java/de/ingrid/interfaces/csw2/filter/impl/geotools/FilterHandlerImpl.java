/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw2.filter.impl.geotools;

import org.geotools.filter.FilterHandler;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class FilterHandlerImpl implements FilterHandler {

	private org.opengis.filter.Filter filter = null;

	/**
	 * Constructor
	 */
	public FilterHandlerImpl() {}
	
	/**
	 * ContentHandler implementation
	 */
	public void characters(char[] ch, int start, int length) throws SAXException {}

	public void endDocument() throws SAXException {}

	public void endElement(String uri, String localName, String name) throws SAXException {}

	public void endPrefixMapping(String prefix) throws SAXException {}

	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}

	public void processingInstruction(String target, String data) throws SAXException {}

	public void setDocumentLocator(Locator locator) {}

	public void skippedEntity(String name) throws SAXException {}

	public void startDocument() throws SAXException {System.out.println("start");}

	public void startElement(String uri, String localName, String name,	Attributes atts) throws SAXException {System.out.println(name);}

	public void startPrefixMapping(String prefix, String uri) throws SAXException {}

	/**
	 * FilterHandler implementation
	 */
	public void filter(org.opengis.filter.Filter filter) {
		this.filter = filter;
	}
	
	/**
	 * Get the filter.
	 * @return (OGC WFS) Filter from (SAX) filter.
	 */
	public org.opengis.filter.Filter getFilter() {
		return this.filter;
	}
}
