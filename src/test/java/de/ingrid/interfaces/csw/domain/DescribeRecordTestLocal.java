/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Mockery;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.ingrid.interfaces.csw.server.CSWServlet;
import de.ingrid.interfaces.csw.tools.StringUtils;

public class DescribeRecordTestLocal extends OperationTestBase {

	/**
	 * Test DescribeRecord with GET method using KVP encoding
	 * @throws Exception
	 */
	public void testKVPDescribeRecordRequest() throws Exception {

		StringBuffer result = new StringBuffer();
		Mockery context = new Mockery();
		final HttpServletRequest request = context.mock(HttpServletRequest.class);
		final HttpServletResponse response = context.mock(HttpServletResponse.class);
		String requestStr = "DescribeRecord";

		// expectations
		this.setupDefaultGetExpectations(context, request, response, result, requestStr);

		// make request
		CSWServlet servlet = this.createServlet();
		servlet.doGet(request, response);

		context.assertIsSatisfied();

		// expect describe record document
		assertTrue("The response length is > 0.", result.length() > 0);

		Document responseDoc = StringUtils.stringToDocument(result.toString());
		assertTrue("The response is no ExceptionReport.",
				!responseDoc.getDocumentElement().getNodeName().equals("ExceptionReport"));
		assertTrue("The response is a DescribeRecordResponse document.",
				responseDoc.getDocumentElement().getNodeName().equals("csw:DescribeRecordResponse"));
	}

	/**
	 * Test DescribeRecord with POST method using XML encoding
	 * @throws Exception
	 */
	public void testXMLDescribeRecordRequest() throws Exception {

		StringBuffer result = new StringBuffer();
		Mockery context = new Mockery();
		final HttpServletRequest request = context.mock(HttpServletRequest.class);
		final HttpServletResponse response = context.mock(HttpServletResponse.class);
		String requestStr = TestRequests.DESCREC_POST;

		// expectations
		this.setupDefaultPostExpectations(context, request, response, result, requestStr);

		// make request
		CSWServlet servlet = this.createServlet();
		servlet.doPost(request, response);

		context.assertIsSatisfied();

		// expect describe record document
		assertTrue("The response length is > 0.", result.length() > 0);

		Document responseDoc = StringUtils.stringToDocument(result.toString());
		assertTrue("The response is no ExceptionReport.",
				!responseDoc.getDocumentElement().getNodeName().equals("ExceptionReport"));
		assertTrue("The response is a DescribeRecordResponse document.",
				responseDoc.getDocumentElement().getNodeName().equals("csw:DescribeRecordResponse"));
	}

	/**
	 * Test DescribeRecord with POST method using Soap encoding
	 * @throws Exception
	 */
	public void testSoapDescribeRecordRequest() throws Exception {

		StringBuffer result = new StringBuffer();
		Mockery context = new Mockery();
		final HttpServletRequest request = context.mock(HttpServletRequest.class);
		final HttpServletResponse response = context.mock(HttpServletResponse.class);
		String requestStr = TestRequests.DESCREC_SOAP;

		// expectations
		this.setupDefaultSoapExpectations(context, request, response, result, requestStr);

		// make request
		CSWServlet servlet = this.createServlet();
		servlet.doPost(request, response);

		context.assertIsSatisfied();

		// expect describe record document
		assertTrue("The response length is > 0.", result.length() > 0);
		Document responseDoc = StringUtils.stringToDocument(result.toString());
		Node payload = xpath.getNode(responseDoc, "soapenv:Envelope/soapenv:Body").getLastChild();

		assertTrue("The response is no ExceptionReport.",
				!payload.getNodeName().equals("ExceptionReport"));
		assertTrue("The response is a DescribeRecordResponse document.",
				payload.getNodeName().equals("csw:DescribeRecordResponse"));
	}
}
