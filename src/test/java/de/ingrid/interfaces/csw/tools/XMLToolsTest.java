/*
 * Created on 19.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.interfaces.csw.tools;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.apache.axis.Message;
import org.apache.axis.SOAPPart;
import org.apache.axis.message.SOAPBody;
import org.apache.axis.message.SOAPEnvelope;

import junit.framework.TestCase;

/**
 * @author rschaefer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class XMLToolsTest extends TestCase {

   
    /*
     * Class under test for Document parse(Reader)
     */
    public final void testParseReader() throws Exception {
       
        
//  System.setProperty("javax.xml.parsers.DocumentBuilderFactory", 
//        //"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
//        "org.apache.crimson.jaxp.DocumentBuilderFactoryImpl");
//
//       System.setProperty("org.xml.sax.parser", 
//               //"org.apache.xerces.parsers.SAXParser");
//       "org.apache.crimson.jaxp.SAXParserImpl");
//        
//        System.out.println("javax.xml.parsers.DocumentBuilderFactory: " + System.getProperty("javax.xml.parsers.DocumentBuilderFactory"));
//        
//        System.out.println("org.xml.sax.parser: " + System.getProperty("org.xml.sax.parser"));
        
      /*  
        URL url = new URL("file:///C:/Program Files/eclipse/workspace/ingrid-ibus/xml/csw_capabilities.xml");
        
        Reader reader = new InputStreamReader( url.openStream() );
        
        org.w3c.dom.Document doc = XMLTools.parse( reader );
        
        Message soapResponseMessage = null;
        
        //System.out.println("cap doc: " + doc.getDocumentElement().toString());
        
        soapResponseMessage = new Message(SOAPTools.SOAP12ENV, false);
        
       
	    
	    SOAPPart sp = (SOAPPart) soapResponseMessage.getSOAPPart();
        
        SOAPEnvelope se = (SOAPEnvelope) sp.getEnvelope();
        
        SOAPBody body = (SOAPBody) se.getBody();
        
        body.addDocument(doc);
        
        System.out.println("cap mess: " + soapResponseMessage.getSOAPPartAsString()); 
        */
        
    }

}
