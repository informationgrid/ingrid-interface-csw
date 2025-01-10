/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Mockery;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.interfaces.csw.server.CSWServlet;
import de.ingrid.interfaces.csw.tools.StringUtils;

public class GetRecordByIdTest extends OperationTestBase {

    /**
     * Test GetRecordById with SOAP method using Soap encoding
     * @throws Exception
     */
    @Test
    public void testSoapGetRecordByIdRequestSimple() throws Exception {

        StringBuffer result = new StringBuffer();
        Mockery context = new Mockery();
        HttpServletRequest request = context.mock(HttpServletRequest.class);
        HttpServletResponse response = context.mock(HttpServletResponse.class);
        String requestStr = TestRequests.getRequest(TestRequests.GETRECORDBYID_SOAP);

        // expectations
        this.setupDefaultSoapExpectations(context, request, response, result, requestStr);

        // make request
        CSWServlet servlet = this.getServlet();
        servlet.doPost(request, response);

        context.assertIsSatisfied();

        // check csw payload
        assertTrue(result.length() > 0, "The response length is > 0.");
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        Node payload = xpath.getNode(responseDoc, "soapenv:Envelope/soapenv:Body").getLastChild();

        assertNotEquals(payload.getLocalName(), "Fault", "The response is no ExceptionReport.");
        assertEquals("GetRecordByIdResponse", payload.getLocalName(), "The response is a GetRecordByIdResponse document.");

        // check records
        NodeList recordNodes = payload.getChildNodes();
        assertEquals(1, recordNodes.getLength());
        Document record = StringUtils.stringToDocument(StringUtils.nodeToString(recordNodes.item(0)));
        assertEquals("0C12204F-5626-4A2E-94F4-514424F093A1", xpath.getString(record, "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"));
    }

    /**
     * Test GetRecordById with POST method using Soap encoding getting multiple records
     * @throws Exception
     */
    @Test
    public void testSoapGetRecordByIdRequestMultiple() throws Exception {

        StringBuffer result = new StringBuffer();
        Mockery context = new Mockery();
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        String requestStr = TestRequests.getRequest(TestRequests.GETRECORDBYID_MULTIPLE_SOAP);

        // expectations
        this.setupDefaultSoapExpectations(context, request, response, result, requestStr);

        // make request
        CSWServlet servlet = this.getServlet();
        servlet.doPost(request, response);

        context.assertIsSatisfied();

        // check csw payload
        assertTrue(result.length() > 0, "The response length is > 0.");
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        Node payload = xpath.getNode(responseDoc, "soapenv:Envelope/soapenv:Body").getLastChild();

        assertNotEquals(payload.getLocalName(), "Fault", "The response is no ExceptionReport.");
        assertEquals("GetRecordByIdResponse", payload.getLocalName(), "The response is a GetRecordByIdResponse document.");

        // check records
        NodeList recordNodes = payload.getChildNodes();
        assertEquals(2, recordNodes.getLength());
        Document record1 = StringUtils.stringToDocument(StringUtils.nodeToString(recordNodes.item(0)));
        assertEquals("0C12204F-5626-4A2E-94F4-514424F093A1", xpath.getString(record1, "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"));
        Document record2 = StringUtils.stringToDocument(StringUtils.nodeToString(recordNodes.item(1)));
        assertEquals("111c0076-b23f-76e5-c888-94327664111", xpath.getString(record2, "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"));
    }

    /**
     * Test GetRecordById with POST method using Soap encoding and invalid request
     * @throws Exception
     */
    @Test
    public void testSoapGetRecordByIdRequestInvalid() throws Exception {

        StringBuffer result = new StringBuffer();
        Mockery context = new Mockery();
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        String requestStr = TestRequests.getRequest(TestRequests.GETRECBYIDINVALID_SOAP);

        // expectations
        this.setupDefaultSoapExpectations(context, request, response, result, requestStr);

        // make request
        CSWServlet servlet = this.getServlet();
        servlet.doPost(request, response);

        context.assertIsSatisfied();

        // check csw payload
        assertTrue(result.length() > 0, "The response length is > 0.");
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        Node payload = xpath.getNode(responseDoc, "soapenv:Envelope/soapenv:Body").getLastChild();

        assertEquals(payload.getLocalName(), "Fault", "The response is no ExceptionReport.");
    }

    /**
     * Test GetRecordById with SOAP method using Soap encoding using the default elementSet
     * @throws Exception
     */
    @Test
    public void testSoapGetRecordByIdRequestDefaultElementSet() throws Exception {

        // do request with elementSetName FULL first
        StringBuffer result1 = new StringBuffer();
        Mockery context1 = new Mockery();
        HttpServletRequest request1 = context1.mock(HttpServletRequest.class);
        HttpServletResponse response1 = context1.mock(HttpServletResponse.class);
        String requestStr1 = TestRequests.getRequest(TestRequests.GETRECORDBYID_SOAP);

        // expectations
        this.setupDefaultSoapExpectations(context1, request1, response1, result1, requestStr1);

        // make request
        CSWServlet servlet = this.getServlet();
        servlet.doPost(request1, response1);
        context1.assertIsSatisfied();

        // do request without elementSetParameter (should default to FULL)
        StringBuffer result2 = new StringBuffer();
        Mockery context2 = new Mockery();
        HttpServletRequest request2 = context2.mock(HttpServletRequest.class);
        HttpServletResponse response2 = context2.mock(HttpServletResponse.class);
        String requestStr2 = TestRequests.getRequest(TestRequests.GETRECORDBYID_SOAP_DEFAULT_ELEMENTSET);

        // expectations
        this.setupDefaultSoapExpectations(context2, request2, response2, result2, requestStr2);

        // make request
        servlet.doPost(request2, response2);
        context2.assertIsSatisfied();

        // check result
        assertEquals(result1.toString(), result2.toString(), "The response is the same as elementSetName FULL response.");
    }
}
