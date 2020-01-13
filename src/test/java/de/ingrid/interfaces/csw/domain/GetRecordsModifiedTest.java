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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Mockery;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.interfaces.csw.domain.constants.ResultType;
import de.ingrid.interfaces.csw.server.CSWServlet;
import de.ingrid.interfaces.csw.tools.StringUtils;

public class GetRecordsModifiedTest extends OperationTestBase {

    /**
     * Test GetRecords by modified date GET method using KVP encoding
     * @throws Exception
     */
    public void testKVPGetRecordsModified() throws Exception {
        // index data:

        // modified   | identifier
        // -------------------------------------------------
        // 2009-12-22 | 486d9622-c29d-44e5-b878-44389740011
        // 2009-12-22 | 655e5998-a20e-66b5-c888-00005553421
        // 2010-01-22 | 2cc6788a-0388-4a4c-b76b-38cc1e4d8f26
        // 2009-12-22 | 550e8400-e29b-41d4-a716-44389665234
        // 2009-12-22 | 1d87204e-0317-5e2e-99af4-514424f665d4
        // 2009-12-22 | 6cf2886f-0124-4a4c-b76b-38cc1d3d0d85
        // 2009-12-22 | 5ac8844e-0989-4a4c-b76b-38cc1e999722
        // 2009-12-22 | 111c0076-b23f-76e5-c888-94327664111
        // 2009-12-22 | 0c12204f-5626-4a2e-94f4-514424f093a1

        // >= 2009-12-21: 9
        assertEquals(9, this.makeRequest("2009-12-21", "PropertyIsGreaterThanOrEqualTo"));

        // >= 2009-12-22: 9
        assertEquals(9, this.makeRequest("2009-12-22", "PropertyIsGreaterThanOrEqualTo"));

        // >  2009-12-22: 1
        assertEquals(1, this.makeRequest("2009-12-22", "PropertyIsGreaterThan"));

        // >  2009-12-23: 1
        assertEquals(1, this.makeRequest("2009-12-23", "PropertyIsGreaterThan"));

        // >= 2010-01-22: 1
        assertEquals(1, this.makeRequest("2010-01-22", "PropertyIsGreaterThanOrEqualTo"));

        // >  2010-01-22: 0
        assertEquals(0, this.makeRequest("2010-01-22", "PropertyIsGreaterThan"));

        // <= 2010-01-23: 9
        assertEquals(9, this.makeRequest("2010-01-23", "PropertyIsLessThanOrEqualTo"));

        // <= 2010-01-22: 9
        assertEquals(9, this.makeRequest("2010-01-22", "PropertyIsLessThanOrEqualTo"));

        // <  2010-01-22: 8
        assertEquals(8, this.makeRequest("2010-01-22", "PropertyIsLessThan"));

        // <  2010-01-21: 8
        assertEquals(8, this.makeRequest("2010-01-21", "PropertyIsLessThan"));

        // <= 2009-12-22: 8
        assertEquals(8, this.makeRequest("2009-12-22", "PropertyIsLessThanOrEqualTo"));

        // <  2009-12-22: 0
        assertEquals(0, this.makeRequest("2009-12-22", "PropertyIsLessThan"));

        // =  2010-01-22: 1
        assertEquals(1, this.makeRequest("2010-01-22", "PropertyIsEqualTo"));
    }

    /**
     * Make the server call
     * @param date
     * @param operator
     * @return int
     * @throws Exception
     */
    private int makeRequest(String date, String operator) throws Exception {

        StringBuffer result = new StringBuffer();
        Mockery context = new Mockery();
        final HttpServletRequest request = context.mock(HttpServletRequest.class);
        final HttpServletResponse response = context.mock(HttpServletResponse.class);
        String requestStr = "GetRecords";
        final String constraint = "<Filter xmlns=\"http://www.opengis.net/ogc\"><"+operator+"><PropertyName>modified</PropertyName><Literal>"+date+"</Literal></"+operator+"></Filter>";

        Map<String, String> additionalParams = new HashMap<String, String>() {{
            this.put("CONSTRAINT", constraint);
            this.put("RESULTTYPE",ResultType.RESULTS.toString());
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
        return recordNodes.getLength();
    }
}
