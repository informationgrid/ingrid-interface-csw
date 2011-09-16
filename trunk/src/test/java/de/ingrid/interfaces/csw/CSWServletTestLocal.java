/*
 * Created on 28.09.2005
 *
 */
package de.ingrid.interfaces.csw;

import org.apache.axis.Message;
import org.custommonkey.xmlunit.XMLTestCase;

import de.ingrid.ibus.client.BusClient;
import de.ingrid.ibus.client.BusClientFactory;
import de.ingrid.interfaces.csw.tools.AxisTools;
import de.ingrid.interfaces.csw.tools.CSWInterfaceConfig;
import de.ingrid.utils.IBus;


/**
 * @author rschaefer
 *
 
 */
public class CSWServletTestLocal extends XMLTestCase {

    
    private CSWServlet cswServlet = null;
    
    public final void testOnMessage() throws Exception {
       
        
        Message soapMessageRequest = null;
        Message soapMessageResponse = null;
        assertNotNull(cswServlet);
        
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.GETCAP1);
        assertNotNull(soapMessageRequest);
        soapMessageResponse = (Message) cswServlet.onMessage(soapMessageRequest);
        assertTrue(soapMessageResponse.getSOAPBody().toString().indexOf("<ows:Keywords>") > -1);

        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.DESCREC1);
        soapMessageResponse = (Message) cswServlet.onMessage(soapMessageRequest);
        assertTrue(soapMessageResponse.getSOAPBody().toString().indexOf("<xs:complexType name=\"MD_Metadata_Type\">") > -1);

        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.DESCREC2);
        soapMessageResponse = (Message) cswServlet.onMessage(soapMessageRequest);
        assertTrue(soapMessageResponse.getSOAPBody().toString().indexOf("<xs:complexType name=\"MD_Metadata_Type\">") > -1);
        
        
        // initialize ibus
        IBus bus = null;
        BusClient client = null;
        try {
            client = BusClientFactory.createBusClient();
            bus = (IBus) client.getNonCacheableIBus();
        } catch (Exception e) {
            System.out.println("init iBus communication: " + e.getMessage());
        }

        if (bus != null) {
            CSWInterfaceConfig.getInstance().setIBus(bus);
        }
        

        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.GETREC1_brief);
        soapMessageResponse = (Message) cswServlet.onMessage(soapMessageRequest);
        assertNotNull(soapMessageResponse.getSOAPBody().toString());
        soapMessageResponse = (Message) cswServlet.onMessage(soapMessageRequest);
        assertNotNull(soapMessageResponse.getSOAPBody().toString());
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.GETREC1_summary);
        soapMessageResponse = (Message) cswServlet.onMessage(soapMessageRequest);
        assertNotNull(soapMessageResponse.getSOAPBody().toString());
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.GETREC1_full);
        soapMessageResponse = (Message) cswServlet.onMessage(soapMessageRequest);
        assertNotNull(soapMessageResponse.getSOAPBody().toString());

        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.GETRECBYID1);
        soapMessageResponse = (Message) cswServlet.onMessage(soapMessageRequest);
        assertNotNull(soapMessageResponse.getSOAPBody().toString());
        
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
