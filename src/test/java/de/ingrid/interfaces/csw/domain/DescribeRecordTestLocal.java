/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain;

import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
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
		Mockery context = new Mockery();

		final HttpServletRequest request = context.mock(HttpServletRequest.class);
		final HttpServletResponse response = context.mock(HttpServletResponse.class);

		StringBuffer result = new StringBuffer();
		final ServletOutputStream sos = new TestServletOutputStream(result);

		// expectations
		final List<String> parameters = Arrays.asList(new String[]{"SERVICE", "REQUEST", "version"});
		context.checking(new Expectations() {{
			this.allowing(request).getParameterNames(); this.will(returnEnumeration(parameters));
			this.allowing(request).getParameter("SERVICE"); this.will(returnValue("CSW"));
			this.allowing(request).getParameter("REQUEST"); this.will(returnValue("DescribeRecord"));
			this.allowing(request).getParameter("version"); this.will(returnValue("2.0.2"));
			this.allowing(response).setContentType("application/xml");
			this.allowing(response).setCharacterEncoding("UTF-8");
			this.allowing(response).getOutputStream(); this.will(returnValue(sos));
		}});

		// test
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
		Mockery context = new Mockery();

		final HttpServletRequest request = context.mock(HttpServletRequest.class);
		final HttpServletResponse response = context.mock(HttpServletResponse.class);

		StringBuffer result = new StringBuffer();
		final ServletInputStream sis = new TestServletInputStream(TestRequests.DESCREC_POST);
		final ServletOutputStream sos = new TestServletOutputStream(result);

		// expectations
		context.checking(new Expectations() {{
			this.allowing(request).getContentType(); this.will(returnValue("application/xml"));
			this.allowing(request).getHeaderNames();
			this.allowing(request).getInputStream(); this.will(returnValue(sis));
			this.allowing(response).setContentType("application/xml");
			this.allowing(response).setCharacterEncoding("UTF-8");
			this.allowing(response).getOutputStream(); this.will(returnValue(sos));
		}});

		// test
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
		Mockery context = new Mockery();

		final HttpServletRequest request = context.mock(HttpServletRequest.class);
		final HttpServletResponse response = context.mock(HttpServletResponse.class);

		StringBuffer result = new StringBuffer();
		final ServletInputStream sis = new TestServletInputStream(TestRequests.DESCREC_SOAP);
		final ServletOutputStream sos = new TestServletOutputStream(result);

		// expectations
		context.checking(new Expectations() {{
			this.allowing(request).getHeaderNames();
			this.allowing(request).getContentType(); this.will(returnValue("application/soap+xml"));
			this.allowing(request).getInputStream(); this.will(returnValue(sis));
			this.allowing(response).setStatus(HttpServletResponse.SC_OK);
			this.allowing(response).setHeader("Content-Type", "application/soap+xml; charset=utf-8");
			this.allowing(response).setHeader(this.with(any(String.class)), this.with(any(String.class)));
			this.allowing(response).setContentType("application/soap+xml");
			this.allowing(response).setCharacterEncoding("UTF-8");
			this.allowing(response).getOutputStream(); this.will(returnValue(sos));
		}});

		// test
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
