/*
 * Created on 28.10.2005
 *
 */
package de.ingrid.interfaces.csw.tools;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.ingrid.interfaces.csw.tools.AxisTools;
import de.ingrid.interfaces.csw.tools.XMLTools;
import de.ingrid.interfaces.csw.tools.XSLTools;

import junit.framework.TestCase;

/**
 * @author rschaefer
 *
 */
public class XSLToolsTest extends TestCase {

    public final void testTransform() throws Exception {
       
        
        
        XSLTools xslt = new XSLTools();
        
        Document originalDoc = XMLTools.create();
        
        Document transformedDoc = null;
        
        Element elemRoot = originalDoc.createElement("IngridDocuments");
        
       
        originalDoc.appendChild(elemRoot);
        
        Element elemIngridDoc = originalDoc.createElement("IngridDocument");
        
        elemRoot.appendChild(elemIngridDoc);
        
        Element elemTitle = originalDoc.createElement("title");
        
        elemTitle.appendChild(originalDoc.createTextNode("der Titel"));
        
        Element elemAbstract = originalDoc.createElement("abstract");
        
        elemAbstract.appendChild(originalDoc.createTextNode("die Zusammenfassung"));
        
        elemIngridDoc.appendChild(elemTitle);
        elemIngridDoc.appendChild(elemAbstract);
        
       
        //AxisTools.createSOAPMessage(originalDoc).writeTo(System.out);
        
       
 
        transformedDoc = xslt.transform(originalDoc, System.getProperty("user.dir")+ "/src/xml/ingrid.xsl");
        
        
        AxisTools.createSOAPMessage(transformedDoc).writeTo(System.out);
        
        //File target = new File(System.getProperty("java.io.tmpdir"), "test.xml");
        
//        File target = new File("c:/", "test.xml");
//        
//        FileWriter writer = new FileWriter(target);
//        
//        writer.write(responseDoc.getDocumentElement().toString());
//        writer.close();
        
        
        
        
       //
        
    }

}
