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
package de.ingrid.interfaces.csw.tools;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Node;

import de.ingrid.utils.tool.XsltUtils;

public class XsltUtilsTest {

    @Test
    void testTransform() throws IOException, Exception {

        XsltUtils xsl = new XsltUtils();

        Node result = xsl.transform(StringUtils.stringToDocument(FileUtils.convertStreamToString(new ClassPathResource(
                "idf_1_0_test.xml").getInputStream())), "idf_1_0_0_to_iso_metadata.xsl");
        System.out.println(StringUtils.nodeToString(result));

        result = xsl.transform(StringUtils.stringToDocument(FileUtils.convertStreamToString(new ClassPathResource(
                "0C12204F-5626-4A2E-94F4-514424F093A1.xml").getInputStream())), "idf_1_0_0_to_iso_metadata.xsl");
        System.out.println(StringUtils.nodeToString(result));

        result = xsl.transform(StringUtils.stringToDocument(FileUtils.convertStreamToString(new ClassPathResource(
                "namespace_test_gmd.xml").getInputStream())), "idf_1_0_0_to_iso_metadata.xsl");
        System.out.println(StringUtils.nodeToString(result));

        result = xsl.transform(StringUtils.stringToDocument(FileUtils.convertStreamToString(new ClassPathResource(
                "namespace_test_idf.xml").getInputStream())), "idf_1_0_0_to_iso_metadata.xsl");
        System.out.println(StringUtils.nodeToString(result));

        result = xsl.transform(StringUtils.stringToDocument(FileUtils.convertStreamToString(new ClassPathResource(
                "namespace_test_idf_gmd.xml").getInputStream())), "idf_1_0_0_to_iso_metadata.xsl");
        System.out.println(StringUtils.nodeToString(result));

    }

}
