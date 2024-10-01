/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
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
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.ingrid.interfaces.csw.server.CSWServlet;
import de.ingrid.interfaces.csw.tools.StringUtils;

public class DescribeRecordTest extends OperationTestBase {

    /**
     * Test DescribeRecord with GET method using KVP encoding
     * @throws Exception
     */
    @Test
    public void testKVPDescribeRecordRequest() throws Exception {

        StringBuffer result = new StringBuffer();
        Mockery context = new Mockery();
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        String requestStr = "DescribeRecord";

        // expectations
        this.setupDefaultGetExpectations(context, request, response, result, requestStr, null);

        // make request
        CSWServlet servlet = this.getServlet();
        servlet.doGet(request, response);

        context.assertIsSatisfied();

        // expect describe record document
        assertTrue(result.length() > 0, "The response length is > 0.");
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        Node payload = responseDoc.getLastChild();

        assertNotEquals(payload.getLocalName(), "Fault", "The response is no ExceptionReport.");
        assertEquals("DescribeRecordResponse", payload.getLocalName(), "The response is a DescribeRecordResponse document.");
    }

    /**
     * Test DescribeRecord with POST method using XML encoding
     * @throws Exception
     */
    @Test
    public void testXMLDescribeRecordRequest() throws Exception {

        StringBuffer result = new StringBuffer();
        Mockery context = new Mockery();
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        String requestStr = TestRequests.getRequest(TestRequests.DESCREC_POST);

        // expectations
        this.setupDefaultPostExpectations(context, request, response, result, requestStr);

        // make request
        CSWServlet servlet = this.getServlet();
        servlet.doPost(request, response);

        context.assertIsSatisfied();

        // expect describe record document
        assertTrue(result.length() > 0, "The response length is > 0.");
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        Node payload = responseDoc.getLastChild();

        assertNotEquals(payload.getLocalName(), "Fault", "The response is no ExceptionReport.");
        assertEquals("DescribeRecordResponse", payload.getLocalName(), "The response is a DescribeRecordResponse document.");
    }

    /**
     * Test DescribeRecord with POST method using Soap encoding
     * @throws Exception
     */
    @Test
    public void testSoapDescribeRecordRequest() throws Exception {

        StringBuffer result = new StringBuffer();
        Mockery context = new Mockery();
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        String requestStr = TestRequests.getRequest(TestRequests.DESCREC_SOAP);

        // expectations
        this.setupDefaultSoapExpectations(context, request, response, result, requestStr);

        // make request
        CSWServlet servlet = this.getServlet();
        servlet.doPost(request, response);

        context.assertIsSatisfied();

        // expect describe record document
        assertTrue(result.length() > 0, "The response length is > 0.");
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        Node payload = xpath.getNode(responseDoc, "soapenv:Envelope/soapenv:Body").getLastChild();

        assertNotEquals(payload.getLocalName(), "Fault", "The response is no ExceptionReport.");
        assertEquals("DescribeRecordResponse", payload.getLocalName(), "The response is a DescribeRecordResponse document.");
    }
}
