/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Mockery;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.interfaces.csw.server.CSWServlet;
import de.ingrid.interfaces.csw.tools.StringUtils;

public class GetRecordsTestLocal extends OperationTestBase {

	/**
	 * Test GetRecords with SOAP method using Soap encoding
	 * @throws Exception
	 */
	public void testSoapGetRecordsRequestSimple() throws Exception {

		StringBuffer result = new StringBuffer();
		Mockery context = new Mockery();
		HttpServletRequest request = context.mock(HttpServletRequest.class);
		HttpServletResponse response = context.mock(HttpServletResponse.class);
		String requestStr = TestRequests.getRequest(TestRequests.GETRECORDS_1_BRIEF_SOAP);

		// expectations
		this.setupDefaultSoapExpectations(context, request, response, result, requestStr);

		// make request
		CSWServlet servlet = this.createServlet();
		servlet.doPost(request, response);

		context.assertIsSatisfied();

		// check csw payload
		assertTrue("The response length is > 0.", result.length() > 0);
		Document responseDoc = StringUtils.stringToDocument(result.toString());
		Node payload = xpath.getNode(responseDoc, "soapenv:Envelope/soapenv:Body").getLastChild();

		assertFalse("The response is no ExceptionReport.", payload.getLocalName().equals("Fault"));
		assertEquals("The response is a GetRecordsResponse document.", "GetRecordsResponse", payload.getLocalName());

		// check records
		NodeList recordNodes = payload.getChildNodes();
		assertEquals(1, recordNodes.getLength());
		Document record = StringUtils.stringToDocument(StringUtils.nodeToString(recordNodes.item(0)));
		assertEquals("04068592-709f-3c7a-85de-f2d68e585fca", xpath.getString(record, "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"));
	}
}
