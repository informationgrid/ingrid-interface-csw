/*
 * Created on 28.09.2005
 *
 */
package de.ingrid.interfaces.csw;

import org.apache.axis.Message;
import org.custommonkey.xmlunit.XMLTestCase;

import de.ingrid.interfaces.csw.tools.AxisTools;


/**
 * @author rschaefer
 *
 
 */
public class CSWServletTest extends XMLTestCase {

    
    private CSWServlet cswServlet = null;
    
    public final void testOnMessage() throws Exception {
       
        
        Message soapMessageRequest = null;
        Message soapMessageResponse = null;
        assertNotNull(cswServlet);
        
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.GETCAP1);
        assertNotNull(soapMessageRequest);
        soapMessageResponse = (Message) cswServlet.onMessage(soapMessageRequest);
        assertTrue(soapMessageResponse.getSOAPBody().toString().indexOf("<ows:Keywords>") > -1);
        
    }

   

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        cswServlet = new CSWServlet();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        
        cswServlet = null;
        
    }  
    

   
}
