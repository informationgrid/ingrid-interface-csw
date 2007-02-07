/*
 * Created on 25.10.2005
 *
 */
package de.ingrid.interfaces.csw.transform;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Vector;

import org.w3c.dom.Document;



import de.ingrid.interfaces.csw.CSW;
import de.ingrid.interfaces.csw.analyse.SessionParameters;
import de.ingrid.interfaces.csw.tools.AxisTools;
import de.ingrid.interfaces.csw.transform.ResponseTransformer;
import de.ingrid.utils.IngridDocument;
import junit.framework.TestCase;

/**
 * @author rschaefer
 */
public class ResponseTransformerTest extends TestCase {

    public final void testTransform() throws Exception {
        
        
      System.setProperty("javax.xml.parsers.DocumentBuilderFactory", 
      //"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
      "org.apache.crimson.jaxp.DocumentBuilderFactoryImpl");

     System.setProperty("org.xml.sax.parser", 
             //"org.apache.xerces.parsers.SAXParser");
     "org.apache.crimson.jaxp.SAXParserImpl");
        
        ResponseTransformer responseTransformer = new ResponseTransformer();
        
        
        
        Document responseDoc = null;
        
        
        IngridDocument[] ingridDocuments = CSW.createTestDocs();
  
        SessionParameters sessionParameters = new SessionParameters();
        
        sessionParameters.setElementSetName("full");
        
        responseDoc = responseTransformer.transform(ingridDocuments, sessionParameters);
        
        
        //responseTransformer.transform(responseDoc, sessionParameters);
        
        
        //AxisTools.createSOAPMessage(responseDoc).writeTo(System.out);
        
        //File target = new File(System.getProperty("java.io.tmpdir"), "test.xml");
        
        /*
        File target = new File("c:/", "test.xml");
        FileWriter writer = new FileWriter(target);
        
        writer.write(responseDoc.getDocumentElement().toString());
        writer.close();
        */
        
    }

}
