package de.ingrid.interfaces.csw.tools;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class StringUtilsTest {

	@Test
	public void testSplitByFirstOccurence() {
		String[] segments = StringUtils.splitByFirstOccurence(
				"path/::urn:x-wmo:md:eu.baltrad.SE::baltrad.SE::", "::");
		assertEquals("path/", segments[0]);
		assertEquals("urn:x-wmo:md:eu.baltrad.SE::baltrad.SE::", segments[1]);
		segments = StringUtils.splitByFirstOccurence(
				"::urn:x-wmo:md:eu.baltrad.SE::baltrad.SE::", "::");
		assertEquals("", segments[0]);
		assertEquals("urn:x-wmo:md:eu.baltrad.SE::baltrad.SE::", segments[1]);
	}

	@Test
	public void testDOM2StringConversion() throws SAXException, IOException {
		String dom = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><test id=\"testid1\"><inner id=\"inner1\">abc</inner></test>";
		Document doc = StringUtils.stringToDocument(dom);
		assertEquals(dom, StringUtils.nodeToString(doc.getDocumentElement()));
	}

	@Test
	public void testMultiTHreadedDOM2StringConversion() throws SAXException,
			IOException {

		for (int i = 0; i < 10; i++) {
			new Thread("" + i) {
				public void run() {
					String dom = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><test id=\"testid1\"><inner id=\"inner1\">"+getName()+"</inner></test>";
					Document doc;
					try {
						doc = StringUtils.stringToDocument(dom);
						assertEquals(dom, StringUtils.nodeToString(doc.getDocumentElement()));
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println("Thread: " + getName() + " running");
				}
			}.start();
		}

	}

}
