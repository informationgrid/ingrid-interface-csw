/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2;

import java.io.StringReader;
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

import de.ingrid.interfaces.csw2.data.TestRequests;
import de.ingrid.interfaces.csw2.tools.SimpleSpringBeanFactory;
import de.ingrid.interfaces.csw2.tools.XMLTools;
import de.ingrid.interfaces.csw2.tools.XPathUtils;

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
			allowing(request).getParameterNames(); will(returnEnumeration(parameters));
			allowing(request).getParameter("SERVICE"); will(returnValue("CSW"));
			allowing(request).getParameter("REQUEST"); will(returnValue("DescribeRecord"));
			allowing(request).getParameter("version"); will(returnValue("2.0.2"));
			allowing(response).setContentType("application/xml");
			allowing(response).setCharacterEncoding("UTF-8");
			allowing(response).getOutputStream(); will(returnValue(sos));
	    }});
		
		// test
		SimpleSpringBeanFactory.INSTANCE.setBeanConfig("beans.xml");
		
		CSWServlet servlet = new CSWServlet();
		servlet.doGet(request, response);
		
		context.assertIsSatisfied();
		
		// expect describe record document
		assertTrue("The response length is > 0.", result.length() > 0);
		
		Document responseDoc = XMLTools.parse(new StringReader(result.toString()));
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
			allowing(request).getContentType(); will(returnValue("application/xml"));
			allowing(request).getHeaderNames();
			allowing(request).getInputStream(); will(returnValue(sis));
			allowing(response).setContentType("application/xml");
			allowing(response).setCharacterEncoding("UTF-8");
			allowing(response).getOutputStream(); will(returnValue(sos));
	    }});
		
		// test
		SimpleSpringBeanFactory.INSTANCE.setBeanConfig("beans.xml");
		
		CSWServlet servlet = new CSWServlet();
		servlet.doPost(request, response);
		
		context.assertIsSatisfied();
		
		// expect describe record document
		assertTrue("The response length is > 0.", result.length() > 0);

		Document responseDoc = XMLTools.parse(new StringReader(result.toString()));
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
			allowing(request).getHeaderNames();
			allowing(request).getContentType(); will(returnValue("application/soap+xml"));
			allowing(request).getInputStream(); will(returnValue(sis));
			allowing(response).setStatus(HttpServletResponse.SC_OK);
			allowing(response).setHeader("Content-Type", "application/soap+xml; charset=utf-8");
			allowing(response).setHeader(with(any(String.class)), with(any(String.class)));
			allowing(response).setContentType("application/soap+xml");
			allowing(response).setCharacterEncoding("UTF-8");
			allowing(response).getOutputStream(); will(returnValue(sos));
	    }});
		
		// test
		SimpleSpringBeanFactory.INSTANCE.setBeanConfig("beans.xml");
		
		CSWServlet servlet = new CSWServlet();
		servlet.doPost(request, response);
		
		context.assertIsSatisfied();
		
		// expect describe record document
		assertTrue("The response length is > 0.", result.length() > 0);
		Document responseDoc = XMLTools.parse(new StringReader(result.toString()));
		Node payload = XPathUtils.getNode(responseDoc, "soapenv:Envelope/soapenv:Body").getLastChild();
		
		assertTrue("The response is no ExceptionReport.", 
				!payload.getNodeName().equals("ExceptionReport"));
		assertTrue("The response is a DescribeRecordResponse document.", 
				payload.getNodeName().equals("csw:DescribeRecordResponse"));
	}
}
