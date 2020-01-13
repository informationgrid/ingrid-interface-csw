/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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

import de.ingrid.interfaces.csw.server.CSWServlet;
import de.ingrid.interfaces.csw.tools.StringUtils;
import org.jmock.Mockery;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GetRecordsTest extends OperationTestBase {

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
        final String sortBy = "title,A";

        Map<String, String> additionalParams = new HashMap<String, String>() {{
            this.put("CONSTRAINT", constraint);
            this.put("SORTBY", sortBy);
            this.put("resultType", "results");
        }};
        // expectations
        this.setupDefaultGetExpectations(context, request, response, result, requestStr, additionalParams);

        // make request
        CSWServlet servlet = this.getServlet();
        servlet.doGet(request, response);

        context.assertIsSatisfied();

        // check csw payload
        assertTrue("The response length is > 0.", result.length() > 0);
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        Node payload = xpath.getNode(responseDoc, "/").getLastChild();

        assertFalse("The response is no ExceptionReport.", payload.getLocalName().equals("Fault"));
        assertEquals("The response is a GetRecordsResponse document.", "GetRecordsResponse", payload.getLocalName());

        // check records
        Node searchResult = xpath.getNode(responseDoc, "/csw:GetRecordsResponse/csw:SearchResults");
        NodeList recordNodes = searchResult.getChildNodes();
        assertEquals(1, recordNodes.getLength());
        Document record = StringUtils.stringToDocument(StringUtils.nodeToString(recordNodes.item(0)));
        assertEquals("655e5998-a20e-66b5-c888-00005553421", xpath.getString(record, "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"));
    }

    /**
     * Test GetRecords with with GET method using KVP encoding
     * @throws Exception
     */
    public void testKVPGetRecordsRequestLike() throws Exception {

        StringBuffer result = new StringBuffer();
        Mockery context = new Mockery();
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        String requestStr = "GetRecords";
        final String constraint = "<Filter xmlns=\"http://www.opengis.net/ogc\"><PropertyIsLike><PropertyName>Title</PropertyName><Literal>Wasser</Literal></PropertyIsLike></Filter>";
        final String sortBy = "title,A";

        Map<String, String> additionalParams = new HashMap<String, String>() {{
            this.put("CONSTRAINT", constraint);
            this.put("SORTBY", sortBy);
            this.put("resultType", "results");
        }};
        // expectations
        this.setupDefaultGetExpectations(context, request, response, result, requestStr, additionalParams);

        // make request
        CSWServlet servlet = this.getServlet();
        servlet.doGet(request, response);

        context.assertIsSatisfied();

        // check csw payload
        assertTrue("The response length is > 0.", result.length() > 0);
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        Node payload = xpath.getNode(responseDoc, "/").getLastChild();

        assertFalse("The response is no ExceptionReport.", payload.getLocalName().equals("Fault"));
        assertEquals("The response is a GetRecordsResponse document.", "GetRecordsResponse", payload.getLocalName());

        // check records
        Node searchResult = xpath.getNode(responseDoc, "/csw:GetRecordsResponse/csw:SearchResults");
        NodeList recordNodes = searchResult.getChildNodes();
        assertEquals(1, recordNodes.getLength());
        Document record = StringUtils.stringToDocument(StringUtils.nodeToString(recordNodes.item(0)));
        assertEquals("655e5998-a20e-66b5-c888-00005553421", xpath.getString(record, "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"));
    }


    /**
     * Test SortBy ASC in GetRecords with with GET method using KVP encoding
     * @throws Exception
     */
    public void testKVPGetRecordsSortAsc() throws Exception {

        StringBuffer result = new StringBuffer();
        Mockery context = new Mockery();
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        String requestStr = "GetRecords";
        final String constraint = "";
        final String sortBy = "title,A";

        Map<String, String> additionalParams = new HashMap<String, String>() {{
            this.put("CONSTRAINT", constraint);
            this.put("SORTBY", sortBy);
            this.put("resultType", "results");
        }};
        // expectations
        this.setupDefaultGetExpectations(context, request, response, result, requestStr, additionalParams);

        // make request
        CSWServlet servlet = this.getServlet();
        servlet.doGet(request, response);

        context.assertIsSatisfied();

        // check csw payload
        assertTrue("The response length is > 0.", result.length() > 0);
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        Node payload = xpath.getNode(responseDoc, "/").getLastChild();

        assertFalse("The response is no ExceptionReport.", payload.getLocalName().equals("Fault"));
        assertEquals("The response is a GetRecordsResponse document.", "GetRecordsResponse", payload.getLocalName());

        // check records
        Node searchResult = xpath.getNode(responseDoc, "/csw:GetRecordsResponse/csw:SearchResults");
        NodeList recordNodes = searchResult.getChildNodes();

        assertEquals(true, recordNodes.getLength() > 0);

        // sort
        String[] titles = new String[recordNodes.getLength()];
        for (int i=0; i<recordNodes.getLength(); i++) {
            Document record = StringUtils.stringToDocument(StringUtils.nodeToString(recordNodes.item(i)));
            titles[i] = xpath.getString(record, "//gmd:title/gco:CharacterString");
        }

        String[] sortedTitles = new String[titles.length];
        System.arraycopy(titles, 0, sortedTitles, 0, titles.length);
        Arrays.sort(sortedTitles);

        for (int i=0; i<titles.length; i++) {
            assertEquals(titles[i], sortedTitles[i]);
        }
    }

    /**
     * Test SortBy DESC in GetRecords with with GET method using KVP encoding
     * @throws Exception
     */
    public void testKVPGetRecordsSortDesc() throws Exception {

        StringBuffer result = new StringBuffer();
        Mockery context = new Mockery();
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        String requestStr = "GetRecords";
        final String constraint = "";
        final String sortBy = "title,D";

        Map<String, String> additionalParams = new HashMap<String, String>() {{
            this.put("CONSTRAINT", constraint);
            this.put("SORTBY", sortBy);
            this.put("resultType", "results");
        }};
        // expectations
        this.setupDefaultGetExpectations(context, request, response, result, requestStr, additionalParams);

        // make request
        CSWServlet servlet = this.getServlet();
        servlet.doGet(request, response);

        context.assertIsSatisfied();

        // check csw payload
        assertTrue("The response length is > 0.", result.length() > 0);
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        Node payload = xpath.getNode(responseDoc, "/").getLastChild();

        assertFalse("The response is no ExceptionReport.", payload.getLocalName().equals("Fault"));
        assertEquals("The response is a GetRecordsResponse document.", "GetRecordsResponse", payload.getLocalName());

        // check records
        Node searchResult = xpath.getNode(responseDoc, "/csw:GetRecordsResponse/csw:SearchResults");
        NodeList recordNodes = searchResult.getChildNodes();

        assertEquals(true, recordNodes.getLength() > 0);

        // sort
        String[] titles = new String[recordNodes.getLength()];
        for (int i=0; i<recordNodes.getLength(); i++) {
            Document record = StringUtils.stringToDocument(StringUtils.nodeToString(recordNodes.item(i)));
            titles[i] = xpath.getString(record, "//gmd:title/gco:CharacterString");
        }

        String[] sortedTitles = new String[titles.length];
        System.arraycopy(titles, 0, sortedTitles, 0, titles.length);
        Arrays.sort(sortedTitles, Collections.reverseOrder());

        for (int i=0; i<titles.length; i++) {
            assertEquals(titles[i], sortedTitles[i]);
        }
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
        CSWServlet servlet = this.getServlet();
        servlet.doPost(request, response);

        context.assertIsSatisfied();

        // check csw payload
        assertTrue("The response length is > 0.", result.length() > 0);
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        Node payload = xpath.getNode(responseDoc, "/soapenv:Envelope/soapenv:Body").getLastChild();

        assertFalse("The response is no ExceptionReport.", payload.getLocalName().equals("Fault"));
        assertEquals("The response is a GetRecordsResponse document.", "GetRecordsResponse", payload.getLocalName());

        // check records
        Node searchResult = xpath.getNode(responseDoc, "/soapenv:Envelope/soapenv:Body/csw:GetRecordsResponse/csw:SearchResults");
        NodeList recordNodes = searchResult.getChildNodes();
        assertEquals(1, recordNodes.getLength());
        Document record = StringUtils.stringToDocument(StringUtils.nodeToString(recordNodes.item(0)));
        assertEquals("655e5998-a20e-66b5-c888-00005553421", xpath.getString(record, "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"));
    }

    /**
     * Test GetRecords with SOAP method using Soap encoding with result type 'hits'
     * @throws Exception
     */
    public void testSoapGetRecordsRequestHits() throws Exception {

        StringBuffer result = new StringBuffer();
        Mockery context = new Mockery();
        HttpServletRequest request = context.mock(HttpServletRequest.class);
        HttpServletResponse response = context.mock(HttpServletResponse.class);
        String requestStr = TestRequests.getRequest(TestRequests.GETRECORDS_HITS_SOAP);

        // expectations
        this.setupDefaultSoapExpectations(context, request, response, result, requestStr);

        // make request
        CSWServlet servlet = this.getServlet();
        servlet.doPost(request, response);

        context.assertIsSatisfied();

        // check csw payload
        assertTrue("The response length is > 0.", result.length() > 0);
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        Node payload = xpath.getNode(responseDoc, "/soapenv:Envelope/soapenv:Body").getLastChild();

        assertFalse("The response is no ExceptionReport.", payload.getLocalName().equals("Fault"));
        assertEquals("The response is a GetRecordsResponse document.", "GetRecordsResponse", payload.getLocalName());

        // check records (none expected for resultType HITS)
        Node searchResult = xpath.getNode(responseDoc, "/soapenv:Envelope/soapenv:Body/csw:GetRecordsResponse/csw:SearchResults");
        NodeList recordNodes = searchResult.getChildNodes();
        assertEquals(0, recordNodes.getLength());
    }

    /**
     * Test SortBy ASC in GetRecords with SOAP method using Soap encoding
     * @throws Exception
     */
    public void testSoapGetRecordsSortByAsc() throws Exception {

        StringBuffer result = new StringBuffer();
        Mockery context = new Mockery();
        HttpServletRequest request = context.mock(HttpServletRequest.class);
        HttpServletResponse response = context.mock(HttpServletResponse.class);
        String requestStr = TestRequests.getRequest(TestRequests.GETRECORDS_11_SOAP);

        // expectations
        this.setupDefaultSoapExpectations(context, request, response, result, requestStr);

        // make request
        CSWServlet servlet = this.getServlet();
        servlet.doPost(request, response);

        context.assertIsSatisfied();

        // check csw payload
        assertTrue("The response length is > 0.", result.length() > 0);
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        Node payload = xpath.getNode(responseDoc, "/soapenv:Envelope/soapenv:Body").getLastChild();

        assertFalse("The response is no ExceptionReport.", payload.getLocalName().equals("Fault"));
        assertEquals("The response is a GetRecordsResponse document.", "GetRecordsResponse", payload.getLocalName());

        // check records
        Node searchResult = xpath.getNode(responseDoc, "/soapenv:Envelope/soapenv:Body/csw:GetRecordsResponse/csw:SearchResults");
        NodeList recordNodes = searchResult.getChildNodes();

        // sort
        String[] titles = new String[recordNodes.getLength()];
        for (int i=0; i<recordNodes.getLength(); i++) {
            Document record = StringUtils.stringToDocument(StringUtils.nodeToString(recordNodes.item(i)));
            titles[i] = xpath.getString(record, "//gmd:title/gco:CharacterString");
        }

        String[] sortedTitles = new String[titles.length];
        System.arraycopy(titles, 0, sortedTitles, 0, titles.length);
        Arrays.sort(sortedTitles);

        for (int i=0; i<titles.length; i++) {
            assertEquals(titles[i], sortedTitles[i]);
        }
    }

    /**
     * Test SortBy DESC in GetRecords with SOAP method using Soap encoding
     * @throws Exception
     */
    public void testSoapGetRecordsSortByDesc() throws Exception {

        StringBuffer result = new StringBuffer();
        Mockery context = new Mockery();
        HttpServletRequest request = context.mock(HttpServletRequest.class);
        HttpServletResponse response = context.mock(HttpServletResponse.class);
        String requestStr = TestRequests.getRequest(TestRequests.GETRECORDS_12_SOAP);

        // expectations
        this.setupDefaultSoapExpectations(context, request, response, result, requestStr);

        // make request
        CSWServlet servlet = this.getServlet();
        servlet.doPost(request, response);

        context.assertIsSatisfied();

        // check csw payload
        assertTrue("The response length is > 0.", result.length() > 0);
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        Node payload = xpath.getNode(responseDoc, "/soapenv:Envelope/soapenv:Body").getLastChild();

        assertFalse("The response is no ExceptionReport.", payload.getLocalName().equals("Fault"));
        assertEquals("The response is a GetRecordsResponse document.", "GetRecordsResponse", payload.getLocalName());

        // check records
        Node searchResult = xpath.getNode(responseDoc, "/soapenv:Envelope/soapenv:Body/csw:GetRecordsResponse/csw:SearchResults");
        NodeList recordNodes = searchResult.getChildNodes();

        // sort
        String[] titles = new String[recordNodes.getLength()];
        for (int i=0; i<recordNodes.getLength(); i++) {
            Document record = StringUtils.stringToDocument(StringUtils.nodeToString(recordNodes.item(i)));
            titles[i] = xpath.getString(record, "//gmd:title/gco:CharacterString");
        }

        String[] sortedTitles = new String[titles.length];
        System.arraycopy(titles, 0, sortedTitles, 0, titles.length);

        Arrays.sort(sortedTitles, Collections.reverseOrder());

        for (int i=0; i<titles.length; i++) {
            assertEquals(titles[i], sortedTitles[i]);
        }
    }
}
