/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.mapping;

import java.io.File;
import java.util.Scanner;

import junit.framework.TestCase;

import org.w3c.dom.Node;

import de.ingrid.interfaces.csw.domain.enums.ElementSetName;
import de.ingrid.interfaces.csw.mapping.impl.XsltMapper;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.idf.IdfTool;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * @author ingo@wemove.com
 */
public class MappingTest extends TestCase {

	private static final File IDF_FILE = new File("src/test/resources/idf-example.xml");

	public void testFull() throws Exception {
		XsltMapper mapper = new XsltMapper();

		String idfContent = new Scanner(IDF_FILE).useDelimiter("\\A").next();
		Record record = IdfTool.createIdfRecord(idfContent, false);
		Node result = mapper.map(record, ElementSetName.FULL);

		XPathUtils xpath = new XPathUtils(new IDFNamespaceContext());
		assertEquals("05F9A598-D866-11D2-AB09-00E0292DC06B", xpath.getString(result, "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"));
	}

	public void testSummary() throws Exception {
		XsltMapper mapper = new XsltMapper();

		String idfContent = new Scanner(IDF_FILE).useDelimiter("\\A").next();
		Record record = IdfTool.createIdfRecord(idfContent, false);
		Node result = mapper.map(record, ElementSetName.SUMMARY);

		XPathUtils xpath = new XPathUtils(new IDFNamespaceContext());
		assertEquals("05F9A598-D866-11D2-AB09-00E0292DC06B", xpath.getString(result, "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"));
	}

	public void testBrief() throws Exception {
		XsltMapper mapper = new XsltMapper();

		String idfContent = new Scanner(IDF_FILE).useDelimiter("\\A").next();
		Record record = IdfTool.createIdfRecord(idfContent, false);
		Node result = mapper.map(record, ElementSetName.BRIEF);

		XPathUtils xpath = new XPathUtils(new IDFNamespaceContext());
		assertEquals("05F9A598-D866-11D2-AB09-00E0292DC06B", xpath.getString(result, "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"));
	}
}
