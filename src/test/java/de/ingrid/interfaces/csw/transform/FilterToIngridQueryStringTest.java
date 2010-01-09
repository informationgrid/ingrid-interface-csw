/*
 * Created on 04.10.2005
 *
 */
package de.ingrid.interfaces.csw.transform;



import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Document;
import org.w3c.dom.Element;



import de.ingrid.interfaces.csw.TestRequests;
import de.ingrid.interfaces.csw.tools.AxisTools;
import de.ingrid.interfaces.csw.tools.SOAPTools;
import de.ingrid.interfaces.csw.tools.XMLTools;
import de.ingrid.interfaces.csw.transform.FilterImpl;
import de.ingrid.interfaces.csw.transform.request.FilterToIngridQueryString;

import junit.framework.TestCase;

/**
 * @author rschaefer
 *
 */
public class FilterToIngridQueryStringTest extends TestCase {

    
 
    /**
     * @throws Exception e
     */
    public final void testGenerateQueryFromFilter1() throws Exception {
        
        
       
        SOAPMessage soapMessageRequest = null;
        
        String ingridQueryString = null;
        
        SOAPElement elem = null;
        
        Element  elemFilter = null;
        
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.GETREC1_brief);
        
        elem = (SOAPElement) soapMessageRequest.getSOAPBody().getElementsByTagName("Filter").item(0);
        
        
        Document doc = XMLTools.create();
	    
        doc.appendChild(doc.createElement("Filter"));
	    
        elemFilter = doc.getDocumentElement();
		                     elemFilter = (Element) SOAPTools.copyNode(elem, elemFilter);
        
        FilterImpl filter = new FilterImpl(elemFilter);
        
        
        FilterToIngridQueryString filterToIngrid = new FilterToIngridQueryString(null);
        
        ingridQueryString = filterToIngrid.generateQueryFromFilter(filter);
        
       System.out.println(" ingridQueryString: " + ingridQueryString);
        
     
        
     }
    
    
    /**
     * @throws Exception e
     */
    public final void testGenerateQueryFromFilter2() throws Exception {
      
        
       
        SOAPMessage soapMessageRequest = null;
        
        String ingridQueryString = null;
        
        SOAPElement elem = null;
        
        Element  elemFilter = null;
        
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.GETREC2);
        
        elem = (SOAPElement) soapMessageRequest.getSOAPBody().getElementsByTagName("Filter").item(0);
        
        
        Document doc = XMLTools.create();
	    
        doc.appendChild(doc.createElement("Filter"));
	    
        elemFilter = doc.getDocumentElement();
		                     elemFilter = (Element) SOAPTools.copyNode(elem, elemFilter);
        
        FilterImpl filter = new FilterImpl(elemFilter);
        
        
        FilterToIngridQueryString filterToIngrid = new FilterToIngridQueryString(null);
        
        ingridQueryString = filterToIngrid.generateQueryFromFilter(filter);
        
        System.out.println(" ingridQueryString: " + ingridQueryString);
        
     
        
     }

    
    /**
     * @throws Exception e
     */
    public final void testGenerateQueryFromFilter3() throws Exception {
        
        
       
        SOAPMessage soapMessageRequest = null;
        
        String ingridQueryString = null;
        
        SOAPElement elem = null;
        
        Element  elemFilter = null;
        
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.GETREC3);
        
        elem = (SOAPElement) soapMessageRequest.getSOAPBody().getElementsByTagName("Filter").item(0);
        
        
        Document doc = XMLTools.create();
	    
        doc.appendChild(doc.createElement("Filter"));
	    
        elemFilter = doc.getDocumentElement();
		                     elemFilter = (Element) SOAPTools.copyNode(elem, elemFilter);
        
        FilterImpl filter = new FilterImpl(elemFilter);
        
        
        FilterToIngridQueryString filterToIngrid = new FilterToIngridQueryString(null);
        
        ingridQueryString = filterToIngrid.generateQueryFromFilter(filter);
        
        System.out.println(" ingridQueryString: " + ingridQueryString);
        
     
        
     }

    
    /**
     * @throws Exception e
     */
    public final void testGenerateQueryFromFilter4() throws Exception {
        
        
       
        SOAPMessage soapMessageRequest = null;
        
        String ingridQueryString = null;
        
        SOAPElement elem = null;
        
        Element  elemFilter = null;
        
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.GETREC4);
        
        elem = (SOAPElement) soapMessageRequest.getSOAPBody().getElementsByTagName("Filter").item(0);
        
        
        Document doc = XMLTools.create();
	    
        doc.appendChild(doc.createElement("Filter"));
	    
        elemFilter = doc.getDocumentElement();
		                     elemFilter = (Element) SOAPTools.copyNode(elem, elemFilter);
        
        FilterImpl filter = new FilterImpl(elemFilter);
        
        
        FilterToIngridQueryString filterToIngrid = new FilterToIngridQueryString(null);
        
        ingridQueryString = filterToIngrid.generateQueryFromFilter(filter);
        
        System.out.println(" ingridQueryString: " + ingridQueryString);
        
     
        
     }
    
    
    /**
     * @throws Exception e
     */
    public final void testGenerateQueryFromFilter5() throws Exception {
       
        
       
        SOAPMessage soapMessageRequest = null;
        
        String ingridQueryString = null;
        
        SOAPElement elem = null;
        
        Element  elemFilter = null;
        
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.GETREC5);
        
        elem = (SOAPElement) soapMessageRequest.getSOAPBody().getElementsByTagName("Filter").item(0);
        
        
        Document doc = XMLTools.create();
	    
        doc.appendChild(doc.createElement("Filter"));
	    
        elemFilter = doc.getDocumentElement();
		                     elemFilter = (Element) SOAPTools.copyNode(elem, elemFilter);
        
        FilterImpl filter = new FilterImpl(elemFilter);
        
        
        FilterToIngridQueryString filterToIngrid = new FilterToIngridQueryString(null);
        
        ingridQueryString = filterToIngrid.generateQueryFromFilter(filter);
        
        System.out.println(" ingridQueryString: " + ingridQueryString);
        
     
        
     }
    
    
    /**
     * @throws Exception e
     */
    public final void testGenerateQueryFromFilter6() throws Exception {
       
        
       
        SOAPMessage soapMessageRequest = null;
        
        String ingridQueryString = null;
        
        SOAPElement elem = null;
        
        Element  elemFilter = null;
        
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.GETREC6);
        
        elem = (SOAPElement) soapMessageRequest.getSOAPBody().getElementsByTagName("Filter").item(0);
        
        
        Document doc = XMLTools.create();
	    
        doc.appendChild(doc.createElement("Filter"));
	    
        elemFilter = doc.getDocumentElement();
		                     elemFilter = (Element) SOAPTools.copyNode(elem, elemFilter);
        
        FilterImpl filter = new FilterImpl(elemFilter);
        
        
        FilterToIngridQueryString filterToIngrid = new FilterToIngridQueryString(null);
        
       try  {
           
        ingridQueryString = filterToIngrid.generateQueryFromFilter(filter);
        
        System.out.println(" ingridQueryString: " + ingridQueryString);
       
       } catch (Exception e) {
     
           e.printStackTrace();  
       
            
        }
     
        
     }
    
    
    
    /**
     * @throws Exception e
     */
    public final void testDeletePreOperator() throws Exception {
        
        StringBuffer stringBuffer = new StringBuffer();
        
        
        stringBuffer.append("BLA");
        
        FilterToIngridQueryString filterToIngrid = new FilterToIngridQueryString(null);
        
        stringBuffer = filterToIngrid.deletePreOperator(stringBuffer);
        
        assertEquals("BLA", stringBuffer.toString());
        
        
        stringBuffer = new StringBuffer();
        
        stringBuffer.append("BLA AND ");
        
        stringBuffer = filterToIngrid.deletePreOperator(stringBuffer);
        
        assertEquals("BLA ", stringBuffer.toString());
        
        
        stringBuffer = new StringBuffer();
        
        stringBuffer.append("BLA OR ");
        
        stringBuffer = filterToIngrid.deletePreOperator(stringBuffer);
        
        assertEquals("BLA ", stringBuffer.toString());
        
        
     //System.out.println("stringBuffer: " + stringBuffer);
        
        
    }
    

}
