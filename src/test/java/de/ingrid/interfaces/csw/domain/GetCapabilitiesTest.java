/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hamcrest.Matchers;
import org.jmock.Mockery;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.ingrid.interfaces.csw.server.CSWServlet;
import de.ingrid.interfaces.csw.tools.StringUtils;

public class GetCapabilitiesTest extends OperationTestBase {

    /**
     * Test GetCapabilities with GET method using KVP encoding
     * @throws Exception
     */
    @Test
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
        assertTrue(result.length() > 0, "The response length is > 0.");
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        assertTrue(xpath.nodeExists(responseDoc, "//ows:Title[text()='InGrid-Portal Catalog Server TEST']"));

        Node payload = responseDoc.getLastChild();

        assertNotEquals(payload.getLocalName(), "Fault", "The response is no ExceptionReport.");
        assertEquals("Capabilities", payload.getLocalName(), "The response is a Capabilities document.");

    }

    /**
     * Test GetCapabilities with POST method using XML encoding
     * @throws Exception
     */
    @Test
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
        assertTrue(result.length() > 0, "The response length is > 0.");
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        Node payload = responseDoc.getLastChild();

        assertNotEquals(payload.getLocalName(), "Fault", "The response is no ExceptionReport.");
        assertEquals("Capabilities", payload.getLocalName(), "The response is a Capabilities document.");
    }

    /**
     * Test GetCapabilities with POST method using Soap encoding
     * @throws Exception
     */
    @Test
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
        assertTrue(result.length() > 0, "The response length is > 0.");
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        Node payload = xpath.getNode(responseDoc, "soapenv:Envelope/soapenv:Body").getLastChild();

        assertNotEquals(payload.getLocalName(), "Fault", "The response is no ExceptionReport.");
        assertEquals("Capabilities", payload.getLocalName(), "The response is a Capabilities document.");
    }

    /**
     * Test GetCapabilities with POST method using Soap encoding and invalid request
     * @throws Exception
     */
    @Test
    public void testSoapPostCapabilitiesRequestInvalid1() throws Exception {
        Node responseDoc = this.testSoapPostCapabilitiesRequestInvalid(TestRequests.GETCAPINVALID1);
        assertThat(xpath.getString(responseDoc, "//soapenv:Detail"),
                Matchers.containsString("The operation 'GetCap' is unknown"));
    }

    /**
     * Test GetCapabilities with POST method using Soap encoding and invalid request
     * @throws Exception
     */
    @Test
    public void testSoapPostCapabilitiesRequestInvalid2() throws Exception {
        Node responseDoc = this.testSoapPostCapabilitiesRequestInvalid(TestRequests.GETCAPINVALID2);
        assertThat(xpath.getString(responseDoc, "//soapenv:Detail"),
                Matchers.containsString("Attribute 'service' is not specified or has no value"));
    }

    /**
     * Test GetCapabilities with POST method using Soap encoding and invalid request
     * @throws Exception
     */
    @Test
    public void testSoapPostCapabilitiesRequestInvalid3() throws Exception {
        Node responseDoc = this.testSoapPostCapabilitiesRequestInvalid(TestRequests.GETCAPINVALID3);
        assertThat(xpath.getString(responseDoc, "//soapenv:Detail"),
                Matchers.containsString("Parameter 'service' has an unsupported value"));
    }

    /**
     * Test GetCapabilities with POST method using Soap encoding and invalid request
     * @return Node
     * @throws Exception
     */
    @Test
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
        assertTrue(result.length() > 0, "The response length is > 0.");
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        Node payload = xpath.getNode(responseDoc, "soapenv:Envelope/soapenv:Body").getLastChild();

        assertEquals(payload.getLocalName(), "Fault", "The response is an ExceptionReport.");
        return payload;
    }

    /**
     * Test GetCapabilities with GET method using KVP encoding and a variant
     * @throws Exception
     */
    @Test
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
        assertTrue(result.length() > 0, "The response length is > 0.");
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        assertTrue(xpath.nodeExists(responseDoc, "//ows:Title[text()='InGrid-Portal Catalog Server TEST']"));
        Node payload = responseDoc.getLastChild();

        assertNotEquals(payload.getLocalName(), "Fault", "The response is no ExceptionReport.");
        assertEquals("Capabilities", payload.getLocalName(), "The response is a Capabilities document.");
    }


}
