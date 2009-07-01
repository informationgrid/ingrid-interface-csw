/*
 * Created on 30.09.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.interfaces.csw.tools;


import java.io.InputStream;

import org.apache.axis.Message;
import org.apache.axis.SOAPPart;
import org.apache.axis.message.MimeHeaders;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.soap.SOAP11Constants;
import org.apache.axis.soap.SOAP12Constants;
import org.apache.axis.soap.SOAPConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

/**
 * @author rschaefer
 *
 */
public final class AxisTools {
	/**
	 * the log object
	 */
	private static Log log = LogFactory.getLog(AxisTools.class);

    /**
     * 
     */
    private AxisTools() { }

    /**
     * 
     * @param inputStream InputStream 
     * @return Message smsg
     * @throws Exception e
     */
    public static Message createSOAPMessage(final InputStream inputStream) throws Exception {
        Message smsg = null;       
        boolean bodyInStream = true;
        //inputStream contains no SOAP envelope!
        MimeHeaders mimeHeaders = new MimeHeaders();
        mimeHeaders.addHeader("Content-Type", "application/soap+xml");
         smsg = new Message(inputStream, bodyInStream, mimeHeaders);
// TODO creates invalid message?       
//        SOAPPart sp = (SOAPPart) smsg.getSOAPPart();
//        
//        SOAPEnvelope se = (SOAPEnvelope) sp.getEnvelope();
//        
//        //set SOAP Version 
//        se.setSoapConstants(SOAPConstants.SOAP12_CONSTANTS);
        //se.setSoapConstants(SOAPConstants.SOAP11_CONSTANTS);
        
        return smsg;
    }

    /**
     * 
     * @param doc Document
     * @return Message smsg
     * @throws Exception e
     */
    public static Message createSOAPMessage(final Document doc) throws Exception {
        Message smsg = null;
        MimeHeaders mimeHeaders = new MimeHeaders();
        mimeHeaders.addHeader("Content-Type", "application/xml;charset=UTF-8");
        smsg = new Message(SOAPTools.SOAP12ENV, false, mimeHeaders);
        //smsg = new Message(SOAPTools.SOAP11ENV, false);
	    SOAPPart sp = (SOAPPart) smsg.getSOAPPart();
        SOAPEnvelope se = (SOAPEnvelope) sp.getEnvelope();
//        SOAPBody body = (SOAPBody) se.getBody();

        // Method addDocument not to be used because of empty namespaceURIs 
        //if there are elements with ns prefixes !?!  
        //Use copyNode instead.
//        body.addDocument(doc);

        //set SOAP Version 
        se.setSoapConstants(SOAPConstants.SOAP12_CONSTANTS);
        //SOAPHeader sh = (org.apache.axis.message.SOAPHeader) se.getHeader();
        SOAPTools.copyNode(doc, (org.apache.axis.message.NodeImpl) se.getBody(), se);
                  
        return smsg;
    }
   
    
    /**
     * @param xmlString String
     * @return smsg Message the Axis Message
     * @throws Exception e
     */
    public static Message createSOAPMessage(final String xmlString) throws Exception {
        Message smsg = null;
        MimeHeaders mimeHeaders = new MimeHeaders();
        mimeHeaders.addHeader("Content-Type", "application/soap+xml");
		smsg = new Message(xmlString, mimeHeaders);
		SOAPPart sp = (SOAPPart) smsg.getSOAPPart();
		SOAPEnvelope se = (SOAPEnvelope) sp.getEnvelope();

		//set SOAP Version 
		se.setSoapConstants(SOAPConstants.SOAP12_CONSTANTS);
			  
        return smsg;
    }
    
    /**
     * This method returns true if the message 
     * is SOAP version 1.2.
     * @param message Message
     * @return isSOAP12 boolean
     * @throws Exception e
     */
    public static boolean isSOAP12(final Message message) throws Exception {
        boolean isSOAP12 = false;
        SOAPConstants soapconsts = null;
        SOAPPart sp = (SOAPPart) message.getSOAPPart();
        SOAPEnvelope se = (SOAPEnvelope) sp.getEnvelope();
        soapconsts = se.getSOAPConstants();
      
        if (soapconsts == null) {
            Exception e = new Exception("soapconsts is null.");
            throw e;
        }
        
	    if (soapconsts instanceof SOAP12Constants) {
	        if (log.isDebugEnabled()) {
	        	log.debug("message is SOAP 1.2. ");
	        }
	        isSOAP12 = true;
	    } else if (soapconsts instanceof SOAP11Constants) {
	        if (log.isDebugEnabled()) {
	        	log.debug("message is SOAP 1.1. ");
	        }
	        isSOAP12 = false;
	    }
        
        return isSOAP12;
    }
}
