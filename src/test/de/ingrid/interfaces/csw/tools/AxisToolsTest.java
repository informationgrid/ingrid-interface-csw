/*
 * Created on 03.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.interfaces.csw.tools;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;

import org.apache.axis.Message;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.PrefixedQName;
import org.custommonkey.xmlunit.XMLTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.interfaces.csw.tools.AxisTools;
import de.ingrid.interfaces.csw.tools.XMLTools;

import junit.framework.TestCase;

/**
 * @author rschaefer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AxisToolsTest extends XMLTestCase {

    /*
     * Class under test for Message createSOAPMessage(InputStream)
     */
    public final void testCreateSOAPMessageInputStream() throws Exception {
        //TODO Implement createSOAPMessage().
    }

    /*
     * Class under test for Message createSOAPMessage(Document)
     */
    public final void testCreateSOAPMessageDocument() throws Exception {
        //TODO Implement createSOAPMessage().
    	
    	Document doc = null;
    	
    	doc = XMLTools.create();
    	
    	//doc.appendChild(doc.createElement("TEST"));
    	
    	//Element elemMetadata = doc.createElementNS("http://www.isotc211.org/iso19115/", "iso19115:MD_Metadata");
    	
    	//Element elemMetadata = doc.createElement("iso19115full:MD_Metadata");
    	
    	
    	Element elemRoot = doc.createElement("root");
    	
    	//Element elemMetadata = doc.createElement("iso19115full:MD_Metadata");
    	
    	
    	Element elemMetadata = doc.createElementNS(
				"http://schemas.opengis.net/iso19115full",
				"iso19115full:MD_Metadata");
    	
    	
    	Element elemGML = doc.createElement("gml:Geometry");
    	
    	elemGML.appendChild(doc.createTextNode("Hallo"));
    	

    	Element elemSMXML = doc.createElement("smXML:fileIdentifier");
    	
    	elemSMXML.setAttribute("testAttr", "testVal");
    	
    	elemSMXML.appendChild(doc.createTextNode("9789787Oziuzi-zioz"));
    	
    	 //Element elemMetadata = doc.createElementNS("http://www.isotc211.org/iso19115/", "iso19115:MD_Metadata");
    	
    	//Element elemMetadata = doc.createElement("MD_Metadata");
    	
    	//elemMetadata.setAttribute("aTest", "test");
    	
    	
    	
    	
    	
    	elemMetadata.appendChild(elemGML);
    	
    	elemMetadata.appendChild(elemSMXML);
    	
    	elemRoot.appendChild(elemMetadata);
    	
    	
    	
    	doc.appendChild(elemRoot);
    	
    	
    	
        Message smsg = AxisTools.createSOAPMessage(doc);
        
        
        
        
//        NodeList nl = smsg.getSOAPPart().getElementsByTagName("iso19115full:MD_Metadata");
//        
//        //Node nd = nl.item(0);
//        
//        MessageElement elem = (MessageElement) nl.item(0);
//        
//        //elem.namespaces = null;
//        
//        //elem.addNamespaceDeclaration("iso","http://bla/bla");
//        
//        elem.setNamespaceURI("http://schemas.opengis.net/iso19115full");
//        
//        //System.out.println("elem name:" + elem.getLocalName());
        
        
        //elem.setAttributeNS("http://schemas.opengis.net/iso19115full", "xmlns:iso19115full", "http://schemas.opengis.net/iso19115full");
        
        
        
         //elem.removeAttribute(new PrefixedQName(new QName("iso19115full")));
        
         //elem.removeAttribute(new PrefixedQName(new QName("aTest")));
      
        
//        if (elem.removeNamespaceDeclaration("iso")){
//        	
//        	System.out.println("OK");
//        }
        
      
        
        
        
        
        
       System.out.println("AxisToolsTest testMsg: " + smsg.getSOAPPartAsString());
    	
    	
    }

    /*
     * Class under test for Message createSOAPMessage(String)
     */
    public final void testCreateSOAPMessageString() throws Exception {
        //TODO Implement createSOAPMessage().
        
//        Message smsg = AxisTools.createSOAPMessage(TestRequests.DESCREC2);
//        
//        System.out.println("AxisToolsTest testMsg: " + smsg.getSOAPPartAsString());
        
    }

    public final void testIsSOAP12() throws Exception {
        //TODO Implement isSOAP12().
    }

}
