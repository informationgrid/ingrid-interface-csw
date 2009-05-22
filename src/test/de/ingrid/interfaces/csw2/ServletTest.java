/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.w3c.dom.Document;

import de.ingrid.interfaces.csw2.tools.SimpleSpringBeanFactory;
import de.ingrid.interfaces.csw2.tools.XMLTools;

public class ServletTest extends TestCase {

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
		Document responseDoc = XMLTools.parse(new StringReader(result.toString()));
		assertTrue("The response is no ExceptionReport.", 
				!responseDoc.getDocumentElement().getNodeName().equals("ExceptionReport"));
		assertTrue("The response is no GetCapabilities document.", 
				!responseDoc.getDocumentElement().getNodeName().equals("GetCapabilities"));
		
		System.out.println(result.toString());
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
