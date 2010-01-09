/*
 * Created on 04.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.interfaces.csw.analyse;

import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPMessage;

import de.ingrid.interfaces.csw.CSWServlet;
import de.ingrid.interfaces.csw.TestRequests;
import de.ingrid.interfaces.csw.analyse.DescRecAnalyser;
import de.ingrid.interfaces.csw.tools.AxisTools;
import junit.framework.TestCase;


/**
 * @author rschaefer
 *
 */
public class DescRecAnalyserTestLocal extends TestCase {
	
	

    /*
     * Class under test for boolean analyse(SOAPBodyElement)
     */
    public final void testAnalyseSOAPBodyElement() throws Exception {


    	
    	
        boolean descRecRequestValid = false;
        
        SOAPMessage soapMessageRequest = null;
        
      
        DescRecAnalyser descRecAnalyser = new DescRecAnalyser();
        
        //TEST1
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.DESCREC1);
        
        descRecRequestValid = descRecAnalyser.analyse(((SOAPBodyElement) soapMessageRequest.getSOAPBody().getFirstChild()));
       
        assertTrue(descRecRequestValid);
        
        
//      TEST2
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.DESCREC2);
        
        descRecRequestValid = descRecAnalyser.analyse(((SOAPBodyElement) soapMessageRequest.getSOAPBody().getFirstChild()));
       
        assertTrue(descRecRequestValid);
        
        
    }

    /*
     * Class under test for boolean analyse(String)
     */
    public final void testAnalyseString() {
        //TODO Implement analyse().
    }

}
