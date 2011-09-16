/*
 * Created on 05.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.interfaces.csw;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.client.Call;
import org.apache.axis.soap.SOAPConstants;

import de.ingrid.interfaces.csw.tools.AxisTools;

/**
 * @author rschaefer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CSWAxisClient {
    
    

    
    
    public static void main(final String[] args) {
        
       Message smsg = null;
       
       Message respsmsg = null; 
        
       Call call = null;
      
      
      try {
      
            //smsg = AxisTools.createSOAPMessage(TestRequests.GETCAP1);
          
           //smsg = AxisTools.createSOAPMessage(TestRequests.GETCAPINVALID1);
           
           //smsg = AxisTools.createSOAPMessage(TestRequests.DESCREC1);
          
           //smsg = AxisTools.createSOAPMessage(TestRequests.GETHITS);
          
            smsg = AxisTools.createSOAPMessage(TestRequests.GETREC1_brief);
          
          //smsg = AxisTools.createSOAPMessage(TestRequests.GETRECBYID1);
    	  
    	   //smsg = AxisTools.createSOAPMessage(TestRequests.UDK);
    	  
    	    //smsg = AxisTools.createSOAPMessage(TestRequests.WCAS006);
   
      } catch (Exception e2) {
        // TODO Auto-generated catch block
        e2.printStackTrace();
    }
      
      
    try {
        
         call = new Call("http://localhost:8080/csw/csw");
        
        //call = new Call("https://localhost:80/csw/csw");
        
        //call = new Call("http://www.uok.bayern.de/axis/services/UDK_Soap_Service");
         
        //call = new Call("http://146.140.211.20:8080/wcas/ingeowcas");
        
        //call = new Call("http://localhost:8080/advwcasportal/advwcasportal");
        
    	//call = new Call("http://www.udk.rlp.de/mufwcas/ingeowcas");
    	
    	//call = new Call("http://141.90.2.25:80/udksoap/wcas");
    	
    	 //call = new Call("http://localhost:8080/ingeomiswcas/ingeomiswcas");
    	
    	
        
        
    } catch (MalformedURLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    
     //set SOAP Version 
      
       call.setSOAPVersion(SOAPConstants.SOAP12_CONSTANTS);
     
       //call.setSOAPVersion(SOAPConstants.SOAP11_CONSTANTS);
     
       call.setRequestMessage(smsg);
       
       
       //set proxy host and proxy port
       
       //System.setProperty("http.proxyHost", "82.127.21.60");
       //System.setProperty("http.proxyPort", "80");

      
   
      try {
       
    	  
         call.invoke();
        
       
         respsmsg = call.getResponseMessage();
        
       
         /*
         File target = new File("c:/", "test.xml");
         FileWriter writer = new FileWriter(target);
         
         writer.write(respsmsg.getSOAPPartAsString());
         writer.close();
         */
         
         
       
       try {
           
        if (!AxisTools.isSOAP12((Message) respsmsg)) {
               
            System.out.println("CSW Axis Client: received message is SOAP 1.1.");
        
        } else {
            
            System.out.println("CSW Axis Client: received message is SOAP 1.2.");
        }
        
        
        
    } catch (Exception e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
    }
       
     
	 	  
	   System.out.println("CSW Axis Client: received message: " + respsmsg.getSOAPPartAsString());
       
        
    } catch (AxisFault e) {
        
        // TODO Auto-generated catch block
        e.printStackTrace();
        
          
    
   
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
        
        
    }
    
    
    
    

}
