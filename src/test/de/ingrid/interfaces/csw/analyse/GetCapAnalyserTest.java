/*
 * Created on 29.09.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.interfaces.csw.analyse;

import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import de.ingrid.interfaces.csw.TestRequests;
import de.ingrid.interfaces.csw.analyse.GetCapAnalyser;
import de.ingrid.interfaces.csw.exceptions.CSWInvalidParameterValueException;
import de.ingrid.interfaces.csw.exceptions.CSWMissingParameterValueException;
import de.ingrid.interfaces.csw.exceptions.CSWOperationNotSupportedException;
import de.ingrid.interfaces.csw.exceptions.CSWVersionNegotiationFailedException;
import de.ingrid.interfaces.csw.tools.AxisTools;

import junit.framework.TestCase;

/**
 * @author rschaefer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GetCapAnalyserTest extends TestCase {

    public final void testAnalyseSOAPBodyElement() throws Exception {
       
        
        boolean getCapRequestValid = false;
        
        SOAPMessage soapMessageRequest = null;
        
        GetCapAnalyser getCapAnalyser = new GetCapAnalyser();
        
        //TEST1
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.GETCAP1);
        
        getCapRequestValid = getCapAnalyser.analyse(((SOAPBodyElement) soapMessageRequest.getSOAPBody().getFirstChild()));
       
        assertTrue(getCapRequestValid);
        
        //TEST2
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.GETCAP2);
        
        getCapRequestValid = getCapAnalyser.analyse(((SOAPBodyElement) soapMessageRequest.getSOAPBody().getFirstChild()));
       
        assertTrue(getCapRequestValid);
        
        // TEST3
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.GETCAPINVALID1);
        
        try {
        
          getCapAnalyser.analyse(((SOAPBodyElement) soapMessageRequest.getSOAPBody().getFirstChild()));
        
          fail("CSWOperationNotSupportedException expected.");
          
        } catch (CSWOperationNotSupportedException e){
            //expected
          
        }
        
        
        // TEST4
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.GETCAPINVALID2);
        
        try {
        
          getCapAnalyser.analyse(((SOAPBodyElement) soapMessageRequest.getSOAPBody().getFirstChild()));
        
          fail("CSWMissingParameterValueException expected.");
          
        } catch (CSWMissingParameterValueException e){
            //expected
          
        }
        
        // TEST5
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.GETCAPINVALID3);
        
        try {
        
          getCapAnalyser.analyse(((SOAPBodyElement) soapMessageRequest.getSOAPBody().getFirstChild()));
        
          fail("CSWInvalidParameterValueException expected.");
          
        } catch (CSWInvalidParameterValueException e){
            //expected
          
        }
        
        
      // TEST6
        soapMessageRequest = AxisTools.createSOAPMessage(TestRequests.GETCAPINVALID4);
        
        try {
        
          getCapAnalyser.analyse(((SOAPBodyElement) soapMessageRequest.getSOAPBody().getFirstChild()));
        
          fail("CSWVersionNegotiationFailedException expected.");
          
        } catch (CSWVersionNegotiationFailedException e){
            //expected
          
        }
       
        
    }
    
    
    public final void testAnalyseKVPString() throws Exception {
       
        boolean getCapRequestValid = false;
        
        GetCapAnalyser getCapAnalyser = new GetCapAnalyser();
        
        getCapRequestValid = getCapAnalyser.analyse(TestRequests.KVPGETCAP1);
        
        assertTrue("getCapRequestValid is not true.", getCapRequestValid);
              
        getCapRequestValid = getCapAnalyser.analyse(TestRequests.KVPGETCAP2);
        
        assertTrue("getCapRequestValid is not true.", getCapRequestValid);
        
        
        getCapRequestValid = getCapAnalyser.analyse(TestRequests.KVPGETCAP3);
        
        assertTrue("getCapRequestValid is not true.", getCapRequestValid);
        
        
        getCapRequestValid = getCapAnalyser.analyse(TestRequests.KVPGETCAP4);
        
        assertTrue("getCapRequestValid is not true.", getCapRequestValid);
        
        
        
        /* Not needed anymore, since checking the REQUEST parameter was
         * removed from the GetCapAnalyser.analyse(Properties) method.
         * 
        try {
            
              getCapAnalyser.analyse(TestRequests.KVPGETCAPINVALID1);
            
              fail("CSWMissingParameterValueException expected.");
              
        } catch (CSWMissingParameterValueException e){
                //expected
              
         }
            
            
        try {
                
           getCapAnalyser.analyse(TestRequests.KVPGETCAPINVALID2);
                
           fail("CSWMissingParameterValueException expected.");
                  
           } catch (CSWMissingParameterValueException e){
                    //expected
                  
          } 
           
        try {
               
          getCapAnalyser.analyse(TestRequests.KVPGETCAPINVALID3);
               
          fail("CSWInvalidParameterValueException expected.");
                 
          } catch (CSWInvalidParameterValueException e){
                   //expected
                 
         }        
         
          try {
              
         getCapAnalyser.analyse(TestRequests.KVPGETCAPINVALID4);
              
         fail("CSWInvalidParameterValueException expected.");
                
         } catch (CSWInvalidParameterValueException e){
                  //expected
                
        }   
        */
         
         
         try {
             
        getCapAnalyser.analyse(TestRequests.KVPGETCAPINVALID5);
             
        fail("CSWVersionNegotiationFailedException expected.");
               
        } catch (CSWVersionNegotiationFailedException e){
                 //expected
               
       }           
          
        
    }
    
    
 

}
