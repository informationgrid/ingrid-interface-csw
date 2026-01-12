/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2026 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;

import de.ingrid.interfaces.csw.domain.TestRequests;
import de.ingrid.utils.xml.Csw202NamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

public class OGCFilterToolsTest {

    /** Tool for evaluating xpath **/
    private XPathUtils xpath = new XPathUtils(new Csw202NamespaceContext());

    @Test
    void testAddPropertyIsEqual() throws Exception {
        String requestStr = TestRequests.getRequest(TestRequests.GETRECORDS_1_BRIEF_SOAP);
        Node query = this.xpath.getNode(StringUtils.stringToDocument(requestStr),
                "//csw:GetRecords/csw:Query");

        OGCFilterTools.addPropertyIsEqual(query, "partner", "hh");
        @SuppressWarnings("unused")
        String result = StringUtils.nodeToString(query);
        Assertions.assertTrue(!this.xpath.nodeExists(query, "ogc:PropertyIsEqualTo"));
        Assertions.assertTrue(this.xpath.nodeExists(query,
                        "//ogc:Filter/ogc:And/ogc:PropertyIsEqualTo/ogc:PropertyName[text()='partner']"));

        requestStr = TestRequests.getRequest(TestRequests.GETRECORDS_2_SOAP);
        query = this.xpath.getNode(StringUtils.stringToDocument(requestStr),
                "//csw:GetRecords/csw:Query");

        OGCFilterTools.addPropertyIsEqual(query, "partner", "hh");
        result = StringUtils.nodeToString(query);
        Assertions.assertTrue(this.xpath.nodeExists(query,
                        "//ogc:Filter/ogc:And/ogc:PropertyIsEqualTo/ogc:PropertyName[text()='partner']"));

        requestStr = TestRequests.getRequest(TestRequests.GETRECORDS_3_SOAP);
        query = this.xpath.getNode(StringUtils.stringToDocument(requestStr),
                "//csw:GetRecords/csw:Query");

        OGCFilterTools.addPropertyIsEqual(query, "partner", "hh");
        result = StringUtils.nodeToString(query);
        Assertions.assertTrue(this.xpath.nodeExists(query,
                        "//ogc:Filter/ogc:And/ogc:PropertyIsEqualTo/ogc:PropertyName[text()='partner']"));

        requestStr = TestRequests.getRequest(TestRequests.GETRECORDS_4_SOAP);
        query = this.xpath.getNode(StringUtils.stringToDocument(requestStr),
                "//csw:GetRecords/csw:Query");

        OGCFilterTools.addPropertyIsEqual(query, "partner", "hh");
        result = StringUtils.nodeToString(query);
        Assertions.assertTrue(this.xpath.nodeExists(query,
                        "//ogc:Filter/ogc:And/ogc:PropertyIsEqualTo/ogc:PropertyName[text()='partner']"));

        requestStr = TestRequests.getRequest(TestRequests.GETRECORDS_5_SOAP);
        query = this.xpath.getNode(StringUtils.stringToDocument(requestStr),
                "//csw:GetRecords/csw:Query");

        OGCFilterTools.addPropertyIsEqual(query, "partner", "hh");
        result = StringUtils.nodeToString(query);
        Assertions.assertTrue(this.xpath.nodeExists(query,
                        "//ogc:Filter/ogc:And/ogc:PropertyIsEqualTo/ogc:PropertyName[text()='partner']"));

        requestStr = TestRequests.getRequest(TestRequests.GETRECORDS_6_SOAP);
        query = this.xpath.getNode(StringUtils.stringToDocument(requestStr),
                "//csw:GetRecords/csw:Query");

        OGCFilterTools.addPropertyIsEqual(query, "partner", "hh");
        result = StringUtils.nodeToString(query);
        Assertions.assertTrue(this.xpath.nodeExists(query,
                        "//ogc:Filter/ogc:And/ogc:PropertyIsEqualTo/ogc:PropertyName[text()='partner']"));

        requestStr = TestRequests.getRequest(TestRequests.GETRECORDS_7_SOAP);
        query = this.xpath.getNode(StringUtils.stringToDocument(requestStr),
                "//csw:GetRecords/csw:Query");

        OGCFilterTools.addPropertyIsEqual(query, "partner", "hh");
        result = StringUtils.nodeToString(query);
        Assertions.assertTrue(this.xpath.nodeExists(query,
                        "//ogc:Filter/ogc:And/ogc:PropertyIsEqualTo/ogc:PropertyName[text()='partner']"));

        requestStr = TestRequests.getRequest(TestRequests.GETRECORDS_8_SOAP);
        query = this.xpath.getNode(StringUtils.stringToDocument(requestStr),
                "//csw:GetRecords/csw:Query");

        OGCFilterTools.addPropertyIsEqual(query, "partner", "hh");
        result = StringUtils.nodeToString(query);
        Assertions.assertTrue(this.xpath.nodeExists(query,
                        "//ogc:Filter/ogc:And/ogc:PropertyIsEqualTo/ogc:PropertyName[text()='partner']"));

        requestStr = TestRequests.getRequest(TestRequests.GETRECORDS_9_SOAP);
        query = this.xpath.getNode(StringUtils.stringToDocument(requestStr),
                "//csw:GetRecords/csw:Query");

        OGCFilterTools.addPropertyIsEqual(query, "partner", "hh");
        result = StringUtils.nodeToString(query);
        Assertions.assertTrue(this.xpath.nodeExists(query,
                        "//ogc:Filter/ogc:PropertyIsEqualTo/ogc:PropertyName[text()='partner']"));

        requestStr = TestRequests.getRequest(TestRequests.GETRECORDS_10_SOAP);
        query = this.xpath.getNode(StringUtils.stringToDocument(requestStr),
                "//csw:GetRecords/csw:Query");

        OGCFilterTools.addPropertyIsEqual(query, "partner", "hh");
        result = StringUtils.nodeToString(query);
        Assertions.assertTrue(this.xpath.nodeExists(query,
                        "//ogc:Filter/ogc:PropertyIsEqualTo/ogc:PropertyName[text()='partner']"));
    }

}
