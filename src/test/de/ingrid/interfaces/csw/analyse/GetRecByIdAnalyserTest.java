/*
 * Created on 04.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.interfaces.csw.analyse;

import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPMessage;

import de.ingrid.interfaces.csw.TestRequests;
import de.ingrid.interfaces.csw.analyse.GetRecByIdAnalyser;
import de.ingrid.interfaces.csw.analyse.SessionParameters;
import de.ingrid.interfaces.csw.exceptions.CSWMissingParameterValueException;
import de.ingrid.interfaces.csw.tools.AxisTools;
import junit.framework.TestCase;

/**
 * @author rschaefer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GetRecByIdAnalyserTest extends TestCase {

    /*
     * Class under test for boolean analyse(SOAPBodyElement)
     */
    public final void testAnalyseSOAPBodyElement() throws Exception {
        
        
        boolean getRecByIdRequestValid = false;
        
        SOAPMessage soapMessageRequest = null;
        
        SessionParameters sessionParameters = new SessionParameters();
        
        GetRecByIdAnalyser getRecByIdAnalyser = new GetRecByIdAnalyser(sessionParameters);
        
        //TEST1
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.GETRECBYID1);
        
        getRecByIdRequestValid = getRecByIdAnalyser.analyse(((SOAPBodyElement) soapMessageRequest.getSOAPBody().getFirstChild()));
       
        assertTrue(getRecByIdRequestValid);
        
        assertEquals("F3659A31-26C3-4017-BF82-6DCB483460D6", sessionParameters.getIds());
        
        assertEquals("full", sessionParameters.getElementSetName());
        
        
        
        //TEST2
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.GETRECBYID2);
        
        getRecByIdRequestValid = getRecByIdAnalyser.analyse(((SOAPBodyElement) soapMessageRequest.getSOAPBody().getFirstChild()));
       
        assertTrue(getRecByIdRequestValid);
        
        assertEquals("2, 6, 8", sessionParameters.getIds());
        
        
        
        //TEST3
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.GETRECBYIDINVALID1);
        
        try {
            
        
             getRecByIdAnalyser.analyse(((SOAPBodyElement) soapMessageRequest.getSOAPBody().getFirstChild()));
        
             fail("CSWMissingParameterValueException expected.");
             
        } catch (CSWMissingParameterValueException e) {
               //expected
             
               assertEquals("Id", e.getLocator());
        }     
             
        
    }

    /*
     * Class under test for boolean analyse(String)
     */
    public final void testAnalyseString() {
        //TODO Implement analyse().
    }

}
