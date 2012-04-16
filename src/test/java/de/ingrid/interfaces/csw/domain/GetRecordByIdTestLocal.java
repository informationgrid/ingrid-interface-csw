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

public class GetRecordByIdTestLocal extends OperationTestBase {

	/**
	 * Test GetRecordById with POST method using Soap encoding
	 * @throws Exception
	 */
	public void testSoapGetRecordByIdRequestSimple() throws Exception {

		StringBuffer result = new StringBuffer();
		Mockery context = new Mockery();
		HttpServletRequest request = context.mock(HttpServletRequest.class);
		HttpServletResponse response = context.mock(HttpServletResponse.class);
		String requestStr = TestRequests.GETRECORDBYID_SOAP;

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

		assertNotSame("The response is no ExceptionReport.", "ExceptionReport", payload.getNodeName());
		assertEquals("The response is a GetRecordByIdResponse document.", "csw:GetRecordByIdResponse", payload.getNodeName());

		// check records
		NodeList recordNodes = payload.getChildNodes();
		assertEquals(1, recordNodes.getLength());
		Document record = StringUtils.stringToDocument(StringUtils.nodeToString(recordNodes.item(0)));
		assertEquals("04068592-709f-3c7a-85de-f2d68e585fca", xpath.getString(record, "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"));
	}

	/**
	 * Test GetRecordById with POST method using Soap encoding getting multiple records
	 * @throws Exception
	 */
	public void testSoapGetRecordByIdRequestMultiple() throws Exception {

		StringBuffer result = new StringBuffer();
		Mockery context = new Mockery();
		final HttpServletRequest request = context.mock(HttpServletRequest.class);
		final HttpServletResponse response = context.mock(HttpServletResponse.class);
		String requestStr = TestRequests.GETRECORDBYID_MULTIPLE_SOAP;

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

		assertNotSame("The response is no ExceptionReport.", "ExceptionReport", payload.getNodeName());
		assertEquals("The response is a GetRecordByIdResponse document.", "csw:GetRecordByIdResponse", payload.getNodeName());

		// check records
		NodeList recordNodes = payload.getChildNodes();
		assertEquals(2, recordNodes.getLength());
		Document record1 = StringUtils.stringToDocument(StringUtils.nodeToString(recordNodes.item(0)));
		assertEquals("04068592-709f-3c7a-85de-f2d68e585fca", xpath.getString(record1, "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"));
		Document record2 = StringUtils.stringToDocument(StringUtils.nodeToString(recordNodes.item(1)));
		assertEquals("16f6d74b-f5b7-3efb-ae3c-0128549b8ac6", xpath.getString(record2, "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"));
	}
}
