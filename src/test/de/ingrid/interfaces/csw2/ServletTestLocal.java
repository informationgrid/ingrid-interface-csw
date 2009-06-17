/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.w3c.dom.Document;

import de.ingrid.interfaces.csw2.tools.SimpleSpringBeanFactory;
import de.ingrid.interfaces.csw2.tools.XMLTools;

public class ServletTestLocal extends TestCase {

	private static final String GETCAP_SOAP = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
        + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
        + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" + "<soapenv:Body>\n"
        + "<GetCapabilities service=\"CSW\" >\n" + "<AcceptVersions>\n" + "<Version>2.0.2</Version>\n"
        + "</AcceptVersions>\n" + "</GetCapabilities>\n" + " </soapenv:Body>\n" + "</soapenv:Envelope>";

    private static final String GETCAP_POST = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<GetCapabilities service=\"CSW\" >\n" + "<AcceptVersions>\n" + "<Version>2.0.2</Version>\n"
        + "</AcceptVersions>\n" + "</GetCapabilities>";

    
    /**
	 * Test GetCapabilities with GET method using KVP encoding
	 * @throws Exception
	 */
	public void testKVPGetCapabilitiesRequest() throws Exception {
		Mockery context = new Mockery();

		final HttpServletRequest request = context.mock(HttpServletRequest.class);
		final HttpServletResponse response = context.mock(HttpServletResponse.class);

		StringBuffer result = new StringBuffer();
		final ServletOutputStream sos = new TestServletOutputStream(result);
		
		// expectations
		final List<String> parameters = Arrays.asList(new String[]{"SERVICE", "REQUEST", "ACCEPTVERSIONS"});
		context.checking(new Expectations() {{
			allowing(request).getParameterNames(); will(returnEnumeration(parameters));
			allowing(request).getParameter("SERVICE"); will(returnValue("CSW"));
			allowing(request).getParameter("REQUEST"); will(returnValue("GetCapabilities"));
			allowing(request).getParameter("ACCEPTVERSIONS"); will(returnValue("2.0.2"));
			allowing(response).setContentType("application/xml");
			allowing(response).setCharacterEncoding("UTF-8");
			allowing(response).getOutputStream(); will(returnValue(sos));
	    }});
		
		// test
		SimpleSpringBeanFactory.INSTANCE.setBeanConfig("beans.xml");
		
		CSWServlet servlet = new CSWServlet();
		servlet.doGet(request, response);
		
		context.assertIsSatisfied();
		
		// expect capabilities document
		System.out.println(result.toString());
		assertTrue("The response length is > 0.", result.length() > 0);
		
		Document responseDoc = XMLTools.parse(new StringReader(result.toString()));
		assertTrue("The response is no ExceptionReport.", 
				!responseDoc.getDocumentElement().getNodeName().equals("ExceptionReport"));
		assertTrue("The response is no GetCapabilities document.", 
				!responseDoc.getDocumentElement().getNodeName().equals("GetCapabilities"));
	}
	
    /**
	 * Test GetCapabilities with POST method using XML encoding
	 * @throws Exception
	 */
	public void testXMLPostCapabilitiesRequest() throws Exception {
		Mockery context = new Mockery();

		final HttpServletRequest request = context.mock(HttpServletRequest.class);
		final HttpServletResponse response = context.mock(HttpServletResponse.class);

		StringBuffer result = new StringBuffer();
		final ServletInputStream sis = new TestServletInputStream(GETCAP_POST);
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
		
		// expect capabilities document
		System.out.println(result.toString());
		assertTrue("The response length is > 0.", result.length() > 0);
		
		Document responseDoc = XMLTools.parse(new StringReader(result.toString()));
		assertTrue("The response is no ExceptionReport.", 
				!responseDoc.getDocumentElement().getNodeName().equals("ExceptionReport"));
		assertTrue("The response is no GetCapabilities document.", 
				!responseDoc.getDocumentElement().getNodeName().equals("GetCapabilities"));
	}
	
    /**
	 * Test GetCapabilities with POST method using Soap encoding
	 * @throws Exception
	 */
	public void testSoapPostCapabilitiesRequest() throws Exception {
		Mockery context = new Mockery();

		final HttpServletRequest request = context.mock(HttpServletRequest.class);
		final HttpServletResponse response = context.mock(HttpServletResponse.class);

		StringBuffer result = new StringBuffer();
		final ServletInputStream sis = new TestServletInputStream(GETCAP_SOAP);
		final ServletOutputStream sos = new TestServletOutputStream(result);

		// expectations
		context.checking(new Expectations() {{
			allowing(request).getHeaderNames();
			allowing(request).getContentType(); will(returnValue("application/soap+xml"));
			allowing(request).getInputStream(); will(returnValue(sis));
			allowing(response).setStatus(HttpServletResponse.SC_OK);
			allowing(response).setHeader("Content-Type", "application/soap+xml; charset=utf-8");
			allowing(response).setHeader("Content-Length", "9420");
			allowing(response).setContentType("application/soap+xml");
			allowing(response).setCharacterEncoding("UTF-8");
			allowing(response).getOutputStream(); will(returnValue(sos));
	    }});
		
		// test
		SimpleSpringBeanFactory.INSTANCE.setBeanConfig("beans.xml");
		
		CSWServlet servlet = new CSWServlet();
		servlet.doPost(request, response);
		
		context.assertIsSatisfied();
		
		// expect capabilities document
		System.out.println(result.toString());
		assertTrue("The response length is > 0.", result.length() > 0);
		
		Document responseDoc = XMLTools.parse(new StringReader(result.toString()));
		assertTrue("The response is no ExceptionReport.", 
				!responseDoc.getDocumentElement().getNodeName().equals("ExceptionReport"));
		assertTrue("The response is no GetCapabilities document.", 
				!responseDoc.getDocumentElement().getNodeName().equals("GetCapabilities"));
	}
	
	/**
	 * InputStream class used with servlet response
	 */
	protected class TestServletInputStream extends ServletInputStream {
		Reader buf = null;
		
		public TestServletInputStream(String input) {
			this.buf = new StringReader(input);
		}

		@Override
		public int read() throws IOException {
			return this.buf.read();
		}
	}

	/**
	 * OutputStream class used with servlet response
	 */
	protected class TestServletOutputStream extends ServletOutputStream {
		StringBuffer buf = null;
		
		public TestServletOutputStream(StringBuffer buf) {
			this.buf = buf;
		}
		
		@Override  
		public void write(int c) throws IOException {  
			this.buf.append((char)c);
		}  
	}
}
