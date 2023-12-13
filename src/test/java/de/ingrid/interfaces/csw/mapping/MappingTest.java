/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.mapping;

import de.ingrid.interfaces.csw.config.ApplicationProperties;
import de.ingrid.interfaces.csw.domain.constants.ConfigurationKeys;
import de.ingrid.interfaces.csw.mapping.impl.XsltMapper;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.idf.IdfTool;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xml.XMLUtils;
import de.ingrid.utils.xpath.XPathUtils;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author ingo@wemove.com
 */
public class MappingTest {

    private static final File IDF_FILE = new File("src/test/resources/idf-example.xml");

    @Test
    public void testFull() throws Exception {
        XsltMapper mapper = new XsltMapper();

        String idfContent = new Scanner(IDF_FILE).useDelimiter("\\A").next();
        Record record = IdfTool.createIdfRecord(idfContent, false);
        Node result = mapper.mapFull(record);

        String xml = XMLUtils.toString((Document) result);
        System.out.println("\ntestFull\n");
        System.out.println(xml);

        XPathUtils xpath = new XPathUtils(new IDFWithXSINamespaceContext());
        assertNotNull(xpath.getString(result, "/gmd:MD_Metadata/@xsi:schemaLocation"));
        assertEquals("05F9A598-D866-11D2-AB09-00E0292DC06B",
                xpath.getString(result, "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"));
    }

    @Test
    public void testSummary() throws Exception {
        XsltMapper mapper = new XsltMapper();

        String idfContent = new Scanner(IDF_FILE).useDelimiter("\\A").next();
        Record record = IdfTool.createIdfRecord(idfContent, false);
        Node result = mapper.mapSummary(mapper.mapFull(record));

        String xml = XMLUtils.toString((Document) result);
        System.out.println("\ntestSummary\n");
        System.out.println(xml);

        XPathUtils xpath = new XPathUtils(new IDFWithXSINamespaceContext());
        assertNotNull(xpath.getString(result, "/gmd:MD_Metadata/@xsi:schemaLocation"));
        assertEquals("05F9A598-D866-11D2-AB09-00E0292DC06B",
                xpath.getString(result, "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"));
    }

    @Test
    public void testBrief() throws Exception {
        XsltMapper mapper = new XsltMapper();

        String idfContent = new Scanner(IDF_FILE).useDelimiter("\\A").next();
        Record record = IdfTool.createIdfRecord(idfContent, false);
        Node result = mapper.mapBrief(mapper.mapFull(record));

        String xml = XMLUtils.toString((Document) result);
        System.out.println("\ntestBrief\n");
        System.out.println(xml);

        XPathUtils xpath = new XPathUtils(new IDFWithXSINamespaceContext());
        assertNotNull(xpath.getString(result, "/gmd:MD_Metadata/@xsi:schemaLocation"));
        assertEquals("05F9A598-D866-11D2-AB09-00E0292DC06B",
                xpath.getString(result, "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"));
    }

    // mapping tests for OGC output schema
    @Test
    public void testFullOgc() throws Exception {
        XsltMapper mapper = new XsltMapper();

        String idfContent = new Scanner(IDF_FILE).useDelimiter("\\A").next();
        Record record = IdfTool.createIdfRecord(idfContent, false);
        Node result = mapper.mapFullOgc(record);

        String xml = XMLUtils.toString((Document) result);
        System.out.println("\n-------------- Test mapping full with output schema OGC --------------\n");
        System.out.println(xml);

        XPathUtils xpath = new XPathUtils(new IDFWithXSINamespaceContext());
        assertEquals("05F9A598-D866-11D2-AB09-00E0292DC06B",
                xpath.getString(result, "//dc:identifier"));
        assertEquals("Moosmonitoring (I) - Monitoring der Schwermetallbelastung in der Bundesrepublik Deutschland mit Hilfe von Moosanalysen (Moss Survey I)",
                xpath.getString(result, "//dc:title"));
        assertEquals("2009-04-30T00:00:00",
                xpath.getString(result, "//dc:date"));
    }

    @Test
    public void testSummaryOgc() throws Exception {
        XsltMapper mapper = new XsltMapper();

        String idfContent = new Scanner(IDF_FILE).useDelimiter("\\A").next();
        Record record = IdfTool.createIdfRecord(idfContent, false);
        Node result = mapper.mapSummaryOgc(mapper.mapFullOgc(record));

        String xml = XMLUtils.toString((Document) result);
        System.out.println("\n-------------- Test mapping summary with output schema OGC --------------\n");
        System.out.println(xml);

        XPathUtils xpath = new XPathUtils(new IDFWithXSINamespaceContext());
        assertNotNull(xpath.getString(result, "/gmd:MD_Metadata/@xsi:schemaLocation"));
        assertEquals("05F9A598-D866-11D2-AB09-00E0292DC06B",
                xpath.getString(result, "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"));
    }

    @Test
    public void testBriefOgc() throws Exception {
        XsltMapper mapper = new XsltMapper();

        String idfContent = new Scanner(IDF_FILE).useDelimiter("\\A").next();
        Record record = IdfTool.createIdfRecord(idfContent, false);
        Node result = mapper.mapBriefOgc(mapper.mapFullOgc(record));

        String xml = XMLUtils.toString((Document) result);
        System.out.println("\n-------------- Test mapping brief with output schema OGC --------------\n");
        System.out.println(xml);

        XPathUtils xpath = new XPathUtils(new IDFWithXSINamespaceContext());
        assertNotNull(xpath.getString(result, "/gmd:MD_Metadata/@xsi:schemaLocation"));
        assertEquals("05F9A598-D866-11D2-AB09-00E0292DC06B",
                xpath.getString(result, "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"));
    }

    @Test
    public void test_ISSUE_INGRID_2194() throws Exception {
        XsltMapper mapper = new XsltMapper();

        String idfContent = new Scanner(new File("src/test/resources/81A36D07-BD83-495A-8A94-30416165C86D.xml"))
                .useDelimiter("\\A").next();
        Record record = IdfTool.createIdfRecord(idfContent, false);
        Node result = mapper.mapFull(record);

        XPathUtils xpath = new XPathUtils(new IDFNamespaceContext());

        // <gmd:CI_OnlineResource>
        // <gmd:linkage>
        // <gmd:URL>http://www.sachsen-anhalt.de/index.php?id=36252&lt;/gmd:URL>
        // </gmd:linkage>
        // <gmd:name>
        // <gco:CharacterString>Datenangebot</gco:CharacterString>
        // </gmd:name>
        // <gmd:function>
        // <gmd:CI_OnLineFunctionCode
        // codeList="http://www.tc211.org/ISO19139/resources/codeList.xml#CI_OnLineFunctionCode"
        // codeListValue="Basic Data"/>
        // </gmd:function>Basisdaten

        String xml = XMLUtils.toString((Document) result);
        System.out.println("\ntest_ISSUE_INGRID_2194\n");
        System.out.println(xml);

        assertFalse(xpath.getString(result,
                        "//gmd:CI_OnlineResource[gmd:function/gmd:CI_OnLineFunctionCode/@codeListValue='Basic Data']")
                .contains("Basisdaten"));
    }

    @Test
    public void testFullHMDKSpecialTicket1531() throws Exception {
        XsltMapper mapper = new XsltMapper();

        ApplicationProperties.setProperty(ConfigurationKeys.IDF_2_FULL_PROCESSING_XSLT, "idf_1_0_0_to_iso_metadata_hmdk_5.8.1_4.6.9.xsl");

        String idfContent = new Scanner(new File("src/test/resources/idf_to_full_hmdk_5.2.1.xml")).useDelimiter("\\A").next();
        Record record = IdfTool.createIdfRecord(idfContent, false);
        Node result = mapper.mapFull(record);

        String xml = XMLUtils.toString((Document) result);
        System.out.println("\ntestFullHMDKSpecialTicket1531\n");
        System.out.println(xml);

        XPathUtils xpath = new XPathUtils(new IDFWithXSINamespaceContext());
        assertNotNull(xpath.getString(result, "/gmd:MD_Metadata/@xsi:schemaLocation"));
        assertEquals("069E1872-5EF3-475E-B426-CD86F23CA61C",
                xpath.getString(result, "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"));
        assertEquals("http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/gmxCodelists.xml#MD_ScopeCode",
                xpath.getString(result, "/gmd:MD_Metadata/gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeList"));
        assertEquals("http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/gmxCodelists.xml#CI_RoleCode",
                xpath.getString(result, "//gmd:CI_RoleCode/@codeList"));
        assertEquals("http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/gmxCodelists.xml#CI_DateTypeCode",
                xpath.getString(result, "//gmd:CI_DateTypeCode/@codeList"));
        assertEquals("http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/gmxCodelists.xml#MD_MaintenanceFrequencyCode",
                xpath.getString(result, "//gmd:MD_MaintenanceFrequencyCode/@codeList"));
        assertEquals("http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/gmxCodelists.xml#MD_SpatialRepresentationTypeCode",
                xpath.getString(result, "//gmd:MD_SpatialRepresentationTypeCode/@codeList"));
        assertEquals("http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/gmxCodelists.xml#MD_CharacterSetCode",
                xpath.getString(result, "//gmd:MD_CharacterSetCode/@codeList"));

        assertEquals("EPSG 25832: ETRS89 / UTM Zone 32N",
                xpath.getString(result, "//gmd:referenceSystemInfo[1]/gmd:MD_ReferenceSystem/gmd:referenceSystemIdentifier/gmd:RS_Identifier/gmd:code/gco:CharacterString"));
        assertEquals("EPSG 4258: ETRS89 / geographisch",
                xpath.getString(result, "//gmd:referenceSystemInfo[2]/gmd:MD_ReferenceSystem/gmd:referenceSystemIdentifier/gmd:RS_Identifier/gmd:code/gco:CharacterString"));
        assertEquals("Nutzungsbedingungen: Datenlizenz Deutschland – Namensnennung – Version 2.0",
                xpath.getString(result, "//gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:useLimitation/gco:CharacterString"));
        assertEquals("license",
                xpath.getString(result, "//gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:useConstraints[1]/gmd:MD_RestrictionCode/@codeListValue"));
        assertEquals("otherRestrictions",
                xpath.getString(result, "//gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:useConstraints[2]/gmd:MD_RestrictionCode/@codeListValue"));
        assertEquals("Nutzungsbedingungen: Datenlizenz Deutschland – Namensnennung – Version 2.0",
                xpath.getString(result, "//gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:otherConstraints[1]/gco:CharacterString"));
        assertEquals("Quellenvermerk: Freie und Hansestadt Hamburg, Landesbetrieb Geoinformation und Vermessung",
                xpath.getString(result, "//gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:otherConstraints[2]/gco:CharacterString"));
        assertEquals("{\"id\":\"dl-by-de/2.0\",\"name\":\"Datenlizenz Deutschland – Namensnennung – Version 2.0\",\"url\":\"https://www.govdata.de/dl-de/by-2-0\",\"quelle\":\"Freie und Hansestadt Hamburg, Landesbetrieb Geoinformation und Vermessung\"}",
                xpath.getString(result, "//gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:otherConstraints[3]/gco:CharacterString"));
        assertEquals("Es gelten keine Zugriffsbeschränkungen",
                xpath.getString(result, "//gmd:resourceConstraints[gmd:MD_LegalConstraints/gmd:accessConstraints/gmd:MD_RestrictionCode/@codeListValue='otherRestrictions'][1]/gmd:MD_LegalConstraints/gmd:otherConstraints/gco:CharacterString"));
        assertEquals("Hamburgisches Geodateninfrastrukturgesetz (HmbGDIG)",
                xpath.getString(result, "//gmd:resourceConstraints[gmd:MD_LegalConstraints/gmd:accessConstraints/gmd:MD_RestrictionCode/@codeListValue='otherRestrictions'][2]/gmd:MD_LegalConstraints/gmd:otherConstraints/gco:CharacterString"));
        assertEquals("INSPIRE Richtlinie",
                xpath.getString(result, "//gmd:resourceConstraints[gmd:MD_LegalConstraints/gmd:accessConstraints/gmd:MD_RestrictionCode/@codeListValue='otherRestrictions'][3]/gmd:MD_LegalConstraints/gmd:otherConstraints/gco:CharacterString"));
    }


    @Test
    public void testFullHMDKNewGmlNamespaceTicket2505() throws Exception {
        XsltMapper mapper = new XsltMapper();

        ApplicationProperties.setProperty(ConfigurationKeys.IDF_2_FULL_PROCESSING_XSLT, "idf_1_0_0_to_iso_metadata_hmdk_5.8.1_4.6.9.xsl");

        String idfContent = new Scanner(new File("src/test/resources/idf_to_full_hmdk_5.2.1_new_gml_namespace.xml")).useDelimiter("\\A").next();
        Record record = IdfTool.createIdfRecord(idfContent, false);
        Node result = mapper.mapFull(record);

        String xml = XMLUtils.toString((Document) result);
        System.out.println("\ntestFullHMDKSpecialTicket1531\n");
        System.out.println(xml);

        XPathUtils xpath = new XPathUtils(new IDFWithXSINamespaceContext());
        assertNotNull(xpath.getString(result, "/gmd:MD_Metadata/@xsi:schemaLocation"));
        assertEquals("069E1872-5EF3-475E-B426-CD86F23CA61C",
                xpath.getString(result, "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString"));
        assertEquals("http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/gmxCodelists.xml#MD_ScopeCode",
                xpath.getString(result, "/gmd:MD_Metadata/gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeList"));
        assertEquals("http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/gmxCodelists.xml#CI_RoleCode",
                xpath.getString(result, "//gmd:CI_RoleCode/@codeList"));
        assertEquals("http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/gmxCodelists.xml#CI_DateTypeCode",
                xpath.getString(result, "//gmd:CI_DateTypeCode/@codeList"));
        assertEquals("http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/gmxCodelists.xml#MD_MaintenanceFrequencyCode",
                xpath.getString(result, "//gmd:MD_MaintenanceFrequencyCode/@codeList"));
        assertEquals("http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/gmxCodelists.xml#MD_SpatialRepresentationTypeCode",
                xpath.getString(result, "//gmd:MD_SpatialRepresentationTypeCode/@codeList"));
        assertEquals("http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/gmxCodelists.xml#MD_CharacterSetCode",
                xpath.getString(result, "//gmd:MD_CharacterSetCode/@codeList"));

        assertEquals("EPSG 25832: ETRS89 / UTM Zone 32N",
                xpath.getString(result, "//gmd:referenceSystemInfo[1]/gmd:MD_ReferenceSystem/gmd:referenceSystemIdentifier/gmd:RS_Identifier/gmd:code/gco:CharacterString"));
        assertEquals("EPSG 4258: ETRS89 / geographisch",
                xpath.getString(result, "//gmd:referenceSystemInfo[2]/gmd:MD_ReferenceSystem/gmd:referenceSystemIdentifier/gmd:RS_Identifier/gmd:code/gco:CharacterString"));
        assertEquals("Nutzungsbedingungen: Datenlizenz Deutschland – Namensnennung – Version 2.0",
                xpath.getString(result, "//gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:useLimitation/gco:CharacterString"));
        assertEquals("license",
                xpath.getString(result, "//gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:useConstraints[1]/gmd:MD_RestrictionCode/@codeListValue"));
        assertEquals("otherRestrictions",
                xpath.getString(result, "//gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:useConstraints[2]/gmd:MD_RestrictionCode/@codeListValue"));
        assertEquals("Nutzungsbedingungen: Datenlizenz Deutschland – Namensnennung – Version 2.0",
                xpath.getString(result, "//gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:otherConstraints[1]/gco:CharacterString"));
        assertEquals("Quellenvermerk: Freie und Hansestadt Hamburg, Landesbetrieb Geoinformation und Vermessung",
                xpath.getString(result, "//gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:otherConstraints[2]/gco:CharacterString"));
        assertEquals("{\"id\":\"dl-by-de/2.0\",\"name\":\"Datenlizenz Deutschland – Namensnennung – Version 2.0\",\"url\":\"https://www.govdata.de/dl-de/by-2-0\",\"quelle\":\"Freie und Hansestadt Hamburg, Landesbetrieb Geoinformation und Vermessung\"}",
                xpath.getString(result, "//gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:otherConstraints[3]/gco:CharacterString"));
        assertEquals("Es gelten keine Zugriffsbeschränkungen",
                xpath.getString(result, "//gmd:resourceConstraints[gmd:MD_LegalConstraints/gmd:accessConstraints/gmd:MD_RestrictionCode/@codeListValue='otherRestrictions'][1]/gmd:MD_LegalConstraints/gmd:otherConstraints/gco:CharacterString"));
        assertEquals("Hamburgisches Geodateninfrastrukturgesetz (HmbGDIG)",
                xpath.getString(result, "//gmd:resourceConstraints[gmd:MD_LegalConstraints/gmd:accessConstraints/gmd:MD_RestrictionCode/@codeListValue='otherRestrictions'][2]/gmd:MD_LegalConstraints/gmd:otherConstraints/gco:CharacterString"));
        assertEquals("INSPIRE Richtlinie",
                xpath.getString(result, "//gmd:resourceConstraints[gmd:MD_LegalConstraints/gmd:accessConstraints/gmd:MD_RestrictionCode/@codeListValue='otherRestrictions'][3]/gmd:MD_LegalConstraints/gmd:otherConstraints/gco:CharacterString"));
        assertEquals("completeness omission", xpath.getString(result, "//gml:quantityType"));
        // check if old elements with old gml namespace are still present
        assertEquals("completeness omission old", xpath.getNodeList(result, "//gml:quantityType").item(1).getTextContent());

    }

    public class IDFWithXSINamespaceContext extends IDFNamespaceContext {
        public String getNamespaceURI(String prefix) {
            if (prefix.equals("xsi")) {
                return "http://www.w3.org/2001/XMLSchema-instance";
            } else if (prefix.equals("gml")) {
                return "http://www.opengis.net/gml";
            } else {
                return super.getNamespaceURI(prefix);
            }
        }
    }
}
