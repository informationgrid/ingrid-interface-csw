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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Mockery;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.interfaces.csw.server.CSWServlet;
import de.ingrid.interfaces.csw.server.cswt.CSWTServlet;
import de.ingrid.interfaces.csw.tools.StringUtils;

public class TransactionTest extends OperationTestBase {

    /**
     * Test Transaction with with GET method using KVP encoding
     * @throws Exception
     */
    public void testKVPTransactionRequestSimple() throws Exception {

        StringBuffer result = new StringBuffer();
        Mockery context = new Mockery();
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        String requestStr = "Transaction";

        // expectations
        this.setupDefaultGetExpectations(context, request, response, result, requestStr, null);

        // make request
        CSWServlet servlet = this.getServlet();
        servlet.doGet(request, response);

        context.assertIsSatisfied();

        // expect exception because KVP GET is not supported for Transaction operation
        assertTrue("The response length is > 0.", result.length() > 0);
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        Node payload = xpath.getNode(responseDoc, "/").getLastChild();

        assertTrue("The response is an ExceptionReport.", payload.getLocalName().equals("ExceptionReport"));
    }

    /**
     * Test GetRecords with SOAP method using Soap encoding
     * @throws Exception
     */
    public void testSoapTransactionRequestSimple() throws Exception {

        StringBuffer result = new StringBuffer();
        Mockery context = new Mockery();
        HttpServletRequest request = context.mock(HttpServletRequest.class);
        HttpServletResponse response = context.mock(HttpServletResponse.class);
        String requestStr = TestRequests.getRequest(TestRequests.TRANSACTION_SOAP);

        Map<String, String> additionalParams = new HashMap<String, String>() {
            private static final long serialVersionUID = 1L;

        {
            this.put("catalog", "test");
        }};
        // expectations
        this.setupDefaultSoapExpectations(context, request, response, result, requestStr, additionalParams);

        // make request
        CSWTServlet servlet = this.getCSWTServlet();
        servlet.doPost(request, response);

        context.assertIsSatisfied();

        // check csw payload
        assertTrue("The response length is > 0.", result.length() > 0);
        Document responseDoc = StringUtils.stringToDocument(result.toString());
        Node payload = xpath.getNode(responseDoc, "/soapenv:Envelope/soapenv:Body").getLastChild();

        assertFalse("The response is no ExceptionReport.", payload.getLocalName().equals("Fault"));
        assertEquals("The response is a TransactionResponse document.", "TransactionResponse", payload.getLocalName());

        // check summary
        Node summary = xpath.getNode(responseDoc, "/soapenv:Envelope/soapenv:Body/csw:TransactionResponse/csw:TransactionSummary");
        NodeList summaryNodes = summary.getChildNodes();
        assertEquals(3, summaryNodes.getLength());

        Node insertNode = summaryNodes.item(0);
        assertEquals("totalInserted", insertNode.getLocalName());
        assertEquals("1", insertNode.getTextContent());
        
        Node updateNode = summaryNodes.item(1);
        assertEquals("totalUpdated", updateNode.getLocalName());
        assertEquals("1", updateNode.getTextContent());

        Node deleteNode = summaryNodes.item(2);
        assertEquals("totalDeleted", deleteNode.getLocalName());
        assertEquals("1", deleteNode.getTextContent());

        // TODO: check insert result
        // Node insertResult = xpath.getNode(responseDoc, "/soapenv:Envelope/soapenv:Body/csw:TransactionResponse/csw:InsertResult");
        // NodeList insertResultNodes = insertResult.getChildNodes();
        // assertEquals(1, insertResultNodes.getLength());
        // 
        // Node recordNode = insertResultNodes.item(0);
        // assertEquals("Record", recordNode.getLocalName());
        // assertEquals("insert-1", recordNode.getAttributes().getNamedItem("handle").getTextContent());
        
    }
}
