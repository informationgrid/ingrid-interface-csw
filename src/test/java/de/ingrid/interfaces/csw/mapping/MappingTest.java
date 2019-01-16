/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.mapping;

import java.io.File;
import java.util.Scanner;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.ingrid.interfaces.csw.mapping.impl.XsltMapper;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.idf.IdfTool;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xml.XMLUtils;
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
		Node result = mapper.mapFull(record);

		String xml = XMLUtils.toString((Document) result);
		System.out.println("\ntestFull\n");
		System.out.println(xml);

		XPathUtils xpath = new XPathUtils(new IDFWithXSINamespaceContext());
		assertNotNull(xpath.getString(result, "/gmd:MD_Metadata/@xsi:schemaLocation"));
		assertEquals("05F9A598-D866-11D2-AB09-00E0292DC06B",
		        xpath.getString(result, "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"));
	}

	public void testSummary() throws Exception {
		XsltMapper mapper = new XsltMapper();

		String idfContent = new Scanner(IDF_FILE).useDelimiter("\\A").next();
		Record record = IdfTool.createIdfRecord(idfContent, false);
		Node result = mapper.mapSummary(mapper.mapFull(record));

		String xml = XMLUtils.toString((Document) result);
		System.out.println("\ntestSummary\n");
		System.out.println(xml);

		XPathUtils xpath = new XPathUtils(new IDFWithXSINamespaceContext());
		assertNotNull(xpath.getString(result, "/gmd:MD_Metadata/@xsi:schemaLocation"));
		assertEquals("05F9A598-D866-11D2-AB09-00E0292DC06B",
		        xpath.getString(result, "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"));
	}

	public void testBrief() throws Exception {
		XsltMapper mapper = new XsltMapper();

		String idfContent = new Scanner(IDF_FILE).useDelimiter("\\A").next();
		Record record = IdfTool.createIdfRecord(idfContent, false);
		Node result = mapper.mapBrief(mapper.mapFull(record));

		String xml = XMLUtils.toString((Document) result);
		System.out.println("\ntestBrief\n");
		System.out.println(xml);

		XPathUtils xpath = new XPathUtils(new IDFWithXSINamespaceContext());
		assertNotNull(xpath.getString(result, "/gmd:MD_Metadata/@xsi:schemaLocation"));
		assertEquals("05F9A598-D866-11D2-AB09-00E0292DC06B",
		        xpath.getString(result, "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"));
	}

	public void test_ISSUE_INGRID_2194() throws Exception {
		XsltMapper mapper = new XsltMapper();

		String idfContent = new Scanner(new File("src/test/resources/81A36D07-BD83-495A-8A94-30416165C86D.xml"))
		        .useDelimiter("\\A").next();
		Record record = IdfTool.createIdfRecord(idfContent, false);
		Node result = mapper.mapFull(record);

		XPathUtils xpath = new XPathUtils(new IDFNamespaceContext());

		// <gmd:CI_OnlineResource>
		// <gmd:linkage>
		// <gmd:URL>http://www.sachsen-anhalt.de/index.php?id=36252&lt;/gmd:URL>
		// </gmd:linkage>
		// <gmd:name>
		// <gco:CharacterString>Datenangebot</gco:CharacterString>
		// </gmd:name>
		// <gmd:function>
		// <gmd:CI_OnLineFunctionCode
		// codeList="http://www.tc211.org/ISO19139/resources/codeList.xml#CI_OnLineFunctionCode"
		// codeListValue="Basic Data"/>
		// </gmd:function>Basisdaten

		String xml = XMLUtils.toString((Document) result);
		System.out.println("\ntest_ISSUE_INGRID_2194\n");
		System.out.println(xml);

		assertFalse(xpath.getString(result,
		        "//gmd:CI_OnlineResource[gmd:function/gmd:CI_OnLineFunctionCode/@codeListValue='Basic Data']")
		        .contains("Basisdaten"));
	}

	public class IDFWithXSINamespaceContext extends IDFNamespaceContext {
		public String getNamespaceURI(String prefix) {
			if (prefix.equals("xsi")) {
				return "http://www.w3.org/2001/XMLSchema-instance";
			} else {
				return super.getNamespaceURI(prefix);
			}
		}
	}
}
