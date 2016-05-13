/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.domain;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hamcrest.Matchers;
import org.jmock.Mockery;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.ingrid.interfaces.csw.server.CSWServlet;
import de.ingrid.interfaces.csw.tools.StringUtils;

public class GetCapabilitiesTest extends OperationTestBase {

    /**
     * Test GetCapabilities with GET method using KVP encoding
     * @throws Exception
     */
    public void testKVPGetCapabilitiesRequest() throws Exception {

        StringBuffer result = new StringBuffer();
        Mockery context = new Mockery();
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        String requestStr = "GetCapabilities";

        // expectations
        this.setupDefaultGetExpectations(context, request, response, result, requestStr, null);

        // make request
        CSWServlet servlet = this.getServlet();
        servlet.doGet(request, response);
        servlet.destroy();

        context.assertIsSatisfied();

        // expect capabilities document
        assertTrue("The response length is > 0.", result.length() > 0);
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        assertTrue(xpath.nodeExists(responseDoc, "//ows:Title[text()='InGrid-Portal Catalog Server TEST']"));

        Node payload = responseDoc.getLastChild();

        assertFalse("The response is no ExceptionReport.", payload.getLocalName().equals("Fault"));
        assertEquals("The response is a Capabilities document.", "Capabilities", payload.getLocalName());

    }

    /**
     * Test GetCapabilities with POST method using XML encoding
     * @throws Exception
     */
    public void testXMLPostCapabilitiesRequest() throws Exception {

        StringBuffer result = new StringBuffer();
        Mockery context = new Mockery();
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        String requestStr = TestRequests.getRequest(TestRequests.GETCAP_POST);

        // expectations
        this.setupDefaultPostExpectations(context, request, response, result, requestStr);

        // make request
        CSWServlet servlet = this.getServlet();
        servlet.doPost(request, response);
        servlet.destroy();

        context.assertIsSatisfied();

        // expect capabilities document
        assertTrue("The response length is > 0.", result.length() > 0);
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        Node payload = responseDoc.getLastChild();

        assertFalse("The response is no ExceptionReport.", payload.getLocalName().equals("Fault"));
        assertEquals("The response is a Capabilities document.", "Capabilities", payload.getLocalName());
    }

    /**
     * Test GetCapabilities with POST method using Soap encoding
     * @throws Exception
     */
    public void testSoapPostCapabilitiesRequest() throws Exception {

        StringBuffer result = new StringBuffer();
        Mockery context = new Mockery();
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        String requestStr = TestRequests.getRequest(TestRequests.GETCAP_SOAP);

        // expectations
        this.setupDefaultSoapExpectations(context, request, response, result, requestStr);

        // make request
        CSWServlet servlet = this.getServlet();
        servlet.doPost(request, response);
        servlet.destroy();

        context.assertIsSatisfied();

        // expect capabilities document
        assertTrue("The response length is > 0.", result.length() > 0);
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        Node payload = xpath.getNode(responseDoc, "soapenv:Envelope/soapenv:Body").getLastChild();

        assertFalse("The response is no ExceptionReport.", payload.getLocalName().equals("Fault"));
        assertEquals("The response is a Capabilities document.", "Capabilities", payload.getLocalName());
    }

    /**
     * Test GetCapabilities with POST method using Soap encoding and invalid request
     * @throws Exception
     */
    public void testSoapPostCapabilitiesRequestInvalid1() throws Exception {
        Node responseDoc = this.testSoapPostCapabilitiesRequestInvalid(TestRequests.GETCAPINVALID1);
        Assert.assertThat(xpath.getString(responseDoc, "//soapenv:Detail"),
                Matchers.containsString("The operation 'GetCap' is unknown"));
    }

    /**
     * Test GetCapabilities with POST method using Soap encoding and invalid request
     * @throws Exception
     */
    public void testSoapPostCapabilitiesRequestInvalid2() throws Exception {
        Node responseDoc = this.testSoapPostCapabilitiesRequestInvalid(TestRequests.GETCAPINVALID2);
        Assert.assertThat(xpath.getString(responseDoc, "//soapenv:Detail"),
                Matchers.containsString("Attribute 'service' is not specified or has no value"));
    }

    /**
     * Test GetCapabilities with POST method using Soap encoding and invalid request
     * @throws Exception
     */
    public void testSoapPostCapabilitiesRequestInvalid3() throws Exception {
        Node responseDoc = this.testSoapPostCapabilitiesRequestInvalid(TestRequests.GETCAPINVALID3);
        Assert.assertThat(xpath.getString(responseDoc, "//soapenv:Detail"),
                Matchers.containsString("Parameter 'service' has an unsupported value"));
    }

    /**
     * Test GetCapabilities with POST method using Soap encoding and invalid request
     * @return Node
     * @throws Exception
     */
    private Node testSoapPostCapabilitiesRequestInvalid(String requestName) throws Exception {

        StringBuffer result = new StringBuffer();
        Mockery context = new Mockery();
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        String requestStr = TestRequests.getRequest(requestName);

        // expectations
        this.setupDefaultSoapExpectations(context, request, response, result, requestStr);

        // make request
        CSWServlet servlet = this.getServlet();
        servlet.doPost(request, response);
        servlet.destroy();

        context.assertIsSatisfied();

        // expect capabilities document
        assertTrue("The response length is > 0.", result.length() > 0);
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        Node payload = xpath.getNode(responseDoc, "soapenv:Envelope/soapenv:Body").getLastChild();

        assertTrue("The response is an ExceptionReport.", payload.getLocalName().equals("Fault"));
        return payload;
    }

    /**
     * Test GetCapabilities with GET method using KVP encoding and a variant
     * @throws Exception
     */
    public void testKVPGetCapabilitiesVariantRequest() throws Exception {

        StringBuffer result = new StringBuffer();
        Mockery context = new Mockery();
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        String requestStr = "GetCapabilities";

        // expectations
        this.setupPartnerParameterGetExpectations(context, request, response, result, requestStr);

        // make request
        CSWServlet servlet = this.getServlet();
        servlet.doGet(request, response);
        servlet.destroy();

        context.assertIsSatisfied();

        // expect capabilities document
        assertTrue("The response length is > 0.", result.length() > 0);
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        assertTrue(xpath.nodeExists(responseDoc, "//ows:Title[text()='InGrid-Portal Catalog Server TEST']"));
        Node payload = responseDoc.getLastChild();

        assertFalse("The response is no ExceptionReport.", payload.getLocalName().equals("Fault"));
        assertEquals("The response is a Capabilities document.", "Capabilities", payload.getLocalName());
    }


}
