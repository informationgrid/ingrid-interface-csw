package de.ingrid.interfaces.csw.tools;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Node;

public class XsltUtilsTest {

    @Test
    public void testTransform() throws IOException, Exception {

        XsltUtils xsl = new XsltUtils();
        
        Node result = xsl.transform(StringUtils.stringToDocument(FileUtils.convertStreamToString(new ClassPathResource("idf_1_0_test.xml").getInputStream())), new File("idf_1_0_0_to_iso_metadata.xsl"));
        System.out.println(StringUtils.nodeToString(result));
        
        result = xsl.transform(StringUtils.stringToDocument(FileUtils.convertStreamToString(new ClassPathResource("0C12204F-5626-4A2E-94F4-514424F093A1.xml").getInputStream())), new File("idf_1_0_0_to_iso_metadata.xsl"));
        System.out.println(StringUtils.nodeToString(result));

        result = xsl.transform(StringUtils.stringToDocument(FileUtils.convertStreamToString(new ClassPathResource("namespace_test_gmd.xml").getInputStream())), new File("idf_1_0_0_to_iso_metadata.xsl"));
        System.out.println(StringUtils.nodeToString(result));

        result = xsl.transform(StringUtils.stringToDocument(FileUtils.convertStreamToString(new ClassPathResource("namespace_test_idf.xml").getInputStream())), new File("idf_1_0_0_to_iso_metadata.xsl"));
        System.out.println(StringUtils.nodeToString(result));
        
        result = xsl.transform(StringUtils.stringToDocument(FileUtils.convertStreamToString(new ClassPathResource("namespace_test_idf_gmd.xml").getInputStream())), new File("idf_1_0_0_to_iso_metadata.xsl"));
        System.out.println(StringUtils.nodeToString(result));
        
    }

}
