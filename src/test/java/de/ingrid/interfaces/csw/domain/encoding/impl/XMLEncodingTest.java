/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/**
 *
 */
package de.ingrid.interfaces.csw.domain.encoding.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import de.ingrid.interfaces.csw.domain.constants.Namespace;
import de.ingrid.interfaces.csw.domain.query.CSWQuery;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.ingrid.interfaces.csw.domain.TestRequests;
import de.ingrid.interfaces.csw.domain.exceptions.CSWInvalidParameterValueException;
import de.ingrid.interfaces.csw.tools.StringUtils;
import de.ingrid.utils.xml.Csw202NamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * @author joachim
 */
public class XMLEncodingTest {

    /**
     * Tool for evaluating xpath
     **/
    private XPathUtils xpath = new XPathUtils(new Csw202NamespaceContext());

    /**
     * Test method for
     * {@link de.ingrid.interfaces.csw.domain.encoding.impl.XMLEncoding#getQuery(org.w3c.dom.Node)}
     * .
     */
    @Test
    void testGetQueryNode() throws Exception {
        XMLEncoding encoding = new XMLEncoding();
        String requestStr = TestRequests.getRequest(TestRequests.GETRECORDS_INVALID_MAXRECORDS);
        Node requestBody = this.xpath.getNode(StringUtils.stringToDocument(requestStr), "//csw:GetRecords");
        encoding.setRequestBody(requestBody);
        try {
            encoding.getQuery();
            fail("Must throw CSWInvalidParameterValueException!");
        } catch (CSWInvalidParameterValueException e) {
        }

        encoding = new XMLEncoding();
        requestStr = TestRequests.getRequest(TestRequests.GETRECORDS_INVALID_STARTPOSITION);
        requestBody = this.xpath.getNode(StringUtils.stringToDocument(requestStr), "//csw:GetRecords");
        encoding.setRequestBody(requestBody);
        try {
            encoding.getQuery();
            fail("Must throw CSWInvalidParameterValueException!");
        } catch (CSWInvalidParameterValueException e) {
        }

    }


    /**
     * Test the outputSchema is extract correctly from a getRecordId request
     *
     * @throws Exception
     */
    @Test
    void testExtractOutputSchemaOgcGetId() throws Exception {
        XMLEncoding encoding = new XMLEncoding();
        String requestStr = TestRequests.getRequest(TestRequests.GETRECORDS_ID_OUTPUT_SCHEMA_OGC);
        Document doc = StringUtils.stringToDocument(requestStr);
        Node requestBody = this.xpath.getNode(doc, "//csw:GetRecordById");
        encoding.setRequestBody(requestBody);

        try {
            CSWQuery q = encoding.getQuery();
            assertEquals(Namespace.OGC.getQName(), q.getOutputSchema().getQName(), "Output schema is not correct");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    /**
     * Test the outputSchema is extract correctly from a getRecords request
     *
     * @throws Exception
     */
    @Test
    void testExtractOutputSchemaOgcGetRecords() throws Exception {
        XMLEncoding encoding = new XMLEncoding();
        String requestStr = TestRequests.getRequest(TestRequests.GETRECORDS_OUTPUT_SCHEMA_OGC);
        Document doc = StringUtils.stringToDocument(requestStr);
        Node requestBody = this.xpath.getNode(doc, "//csw:GetRecords");
        encoding.setRequestBody(requestBody);

        try {
            CSWQuery q = encoding.getQuery();
            assertEquals(Namespace.OGC.getQName(), q.getOutputSchema().getQName(), "Output schema is not correct");
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

}
