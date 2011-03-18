/**
 * 
 */
package de.ingrid.interfaces.csw.tools;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

/**
 * @author joachim
 * 
 */
public class DocumentStylerTest extends TestCase {

    /**
     * Test method for
     * {@link de.ingrid.interfaces.csw.transform.response.CSWBuilderType_GetRecordById_CSW_2_0_2_AP_ISO_1_0.DocumentStyler#transform(org.dom4j.Document)}
     * .
     * 
     * @throws Exception
     */
    public void testTransform() throws Exception {

        Source style = new StreamSource(new FileSystemResource("src/main/resources/idf_1_0_0_to_iso_metadata.xsl").getInputStream());
        DocumentStyler docStyler = new DocumentStyler(style);
        
        
        SAXReader xmlReader = new SAXReader();
        Document source = xmlReader.read(new ClassPathResource("idf_1_0_test.xml").getInputStream());

        Document dest = docStyler.transform(source);
        
        System.out.println(dest.asXML());

    }

}
