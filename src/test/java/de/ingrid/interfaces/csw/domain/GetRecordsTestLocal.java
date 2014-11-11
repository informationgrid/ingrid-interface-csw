/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Mockery;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.interfaces.csw.server.CSWServlet;
import de.ingrid.interfaces.csw.tools.StringUtils;

public class GetRecordsTestLocal extends OperationTestBase {

    /**
     * Test GetRecords with with GET method using KVP encoding
     * @throws Exception
     */
    public void testKVPGetRecordsRequestSimple() throws Exception {

        StringBuffer result = new StringBuffer();
        Mockery context = new Mockery();
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        String requestStr = "GetRecords";
        final String constraint = "<Filter xmlns=\"http://www.opengis.net/ogc\"><PropertyIsEqualTo><PropertyName>Title</PropertyName><Literal>Wasser</Literal></PropertyIsEqualTo></Filter>";
        final String sortBy = "<SortBy><SortProperty><PropertyName>title</PropertyName><SortOrder>ASC</SortOrder></SortProperty></SortBy>";

        Map<String, String> additionalParams = new HashMap<String, String>() {{
            this.put("CONSTRAINT", constraint);
            this.put("SORTBY", sortBy);
        }};
        // expectations
        this.setupDefaultGetExpectations(context, request, response, result, requestStr, additionalParams);

        // make request
        CSWServlet servlet = this.createServlet();
        servlet.doGet(request, response);

        context.assertIsSatisfied();

        // check csw payload
        assertTrue("The response length is > 0.", result.length() > 0);
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        Node payload = xpath.getNode(responseDoc, "soapenv:Envelope/soapenv:Body").getLastChild();

        assertFalse("The response is no ExceptionReport.", payload.getLocalName().equals("Fault"));
        assertEquals("The response is a GetRecordsResponse document.", "GetRecordsResponse", payload.getLocalName());

        // check records
        NodeList recordNodes = payload.getChildNodes();
        assertEquals(1, recordNodes.getLength());
        Document record = StringUtils.stringToDocument(StringUtils.nodeToString(recordNodes.item(0)));
        assertEquals("04068592-709f-3c7a-85de-f2d68e585fca", xpath.getString(record, "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"));
    }

    /**
     * Test GetRecords with SOAP method using Soap encoding
     * @throws Exception
     */
    public void testSoapGetRecordsRequestSimple() throws Exception {

        StringBuffer result = new StringBuffer();
        Mockery context = new Mockery();
        HttpServletRequest request = context.mock(HttpServletRequest.class);
        HttpServletResponse response = context.mock(HttpServletResponse.class);
        String requestStr = TestRequests.getRequest(TestRequests.GETRECORDS_1_BRIEF_SOAP);

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

        assertFalse("The response is no ExceptionReport.", payload.getLocalName().equals("Fault"));
        assertEquals("The response is a GetRecordsResponse document.", "GetRecordsResponse", payload.getLocalName());

        // check records
        NodeList recordNodes = payload.getChildNodes();
        assertEquals(1, recordNodes.getLength());
        Document record = StringUtils.stringToDocument(StringUtils.nodeToString(recordNodes.item(0)));
        assertEquals("04068592-709f-3c7a-85de-f2d68e585fca", xpath.getString(record, "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"));
    }
}
