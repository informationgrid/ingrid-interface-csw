/*----------------------------------------------------------------------------*
 *          @@@@@      @@@       @@@@@                                        *
 *      @@@@@@@@@@@    @@@@    @@@@@@@@        @                              *
 *     @@@@@@@@@@@@    @@@@   @@@@@@@@@     @@@@                              *
 *    @@@@@            @@@@  @@@@           @@@@                              *
 *   @@@@@             @@@@  @@@@@        @@@@@@@@@   @@@@@@@@      @@@@@@@   *
 *   @@@@    @@@@@@@   @@@@   @@@@@@@     @@@@@@@@@  @@@@@@@@@@   @@@@@@@@@   *
 *   @@@@   @@@@@@@@   @@@@    @@@@@@@@     @@@@    @@@@    @@@   @@@@        *
 *   @@@@    @@@@@@@   @@@@      @@@@@@@    @@@@    @@@@@@@@@@@@ @@@@         *
 *   @@@@@      @@@@   @@@@         @@@@    @@@@    @@@@@@@@@@@@ @@@@         *
 *    @@@@@     @@@@   @@@@   @     @@@@    @@@@    @@@@      @   @@@@        *
 *     @@@@@@@@@@@@@   @@@@   @@@@@@@@@@    @@@@@@@  @@@@@@@@@@   @@@@@@@@@   *
 *       @@@@@@@@@@@   @@@@   @@@@@@@@       @@@@@@   @@@@@@@@@     @@@@@@@   *
 *                            Neue Wege mit GIS                               *
 *                                                                            *
 * Fraunhoferstr. 5                                                           *
 * D-64283 Darmstadt                                                          *
 * info@gistec-online.de                          http://www.gistec-online.de *
 *----------------------------------------------------------------------------*
 *                                                                            *
 * Copyright © 2004 GIStec GmbH                                               *
 * ALL Rights Reserved.                                                       *
 *                                                                            *
 *+---------------------------------------------------------------------------*
 *                                                                            *
 * Author           : Ralf Schäfer                                            *
 * Erstellungsdatum : 24.05.2004                                              *
 * Version          : 1.0                                                     *
 * Beschreibung     :  Hilfsfunktionen fuer SOAP                              *
 *                                                                            *
 *                                                                            *
 *----------------------------------------------------------------------------*
 * Änderungen (Datum, Version, Author, Beschreibung)                          *
 *----------------------------------------------------------------------------*
 *            |         |          |                                          *
 *            |         |          |                                          *
 *----------------------------------------------------------------------------*
 */

package de.ingrid.interfaces.csw.tools;

//IMPORTS java.io
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.axis.Message;
import org.apache.axis.message.MimeHeaders;
import org.apache.axis.soap.SOAPConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.DOMException;

import de.ingrid.interfaces.csw.tools.CSWInterfaceConfig;

/**
 * Diese Klasse stellt Hilfsfunktionen fuer SOAP zur Verfuegung  
 * @author rschaefer
 */
public final class SOAPTools {

	/**
	 * constructor
	 */
	private SOAPTools() {
	}

	/**
	 * the log object
	 */
	private static Log log = LogFactory.getLog(SOAPTools.class);

    // Static object used to retrieve the config data 
    private static CSWInterfaceConfig cswConfig = CSWInterfaceConfig.getInstance();

    /**
	 * the soap envelope for SOAP version 1.2
	 */
	public static final String SOAP12ENV = "<?xml version=\"1.0\" encoding=\"" + cswConfig.getString(CSWInterfaceConfig.RESPONSE_ENCODING) + "\"?>\n" +
			"<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
			 "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
			 "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
			" <soapenv:Body>\n" +
			" </soapenv:Body>\n" +
			"</soapenv:Envelope>";

	/**
	 * the soap envelope for SOAP version 1.1
	 */
	public static final String SOAP11ENV = "<?xml version=\"1.0\" encoding=\"" + cswConfig.getString(CSWInterfaceConfig.RESPONSE_ENCODING) + "\"?>\n" +
			
			//      ns just for SOAP 1.1
			"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
		
			"                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
			 "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
			
			" <soapenv:Body>\n" +

			" </soapenv:Body>\n" + "</soapenv:Envelope>";

	/**
	 * fuehrt ein Deep copy von einem Dom-Node zu einem Soap-Node aus
	 * @param  source org.w3c.dom.Node source node
	 * @param  dest javax.xml.soap.Node destination node
	 * @param  env SOAPEnvelope soap envelope
	 * @return  dest javax.xml.soap.Node destination
	 * @throws SOAPException e
	 */

	public static javax.xml.soap.Node copyNode(final org.w3c.dom.Node source,
			final org.apache.axis.message.NodeImpl dest, 
			final org.apache.axis.message.SOAPEnvelope env)
	throws SOAPException {
		if (source.getNodeType() == org.w3c.dom.Node.TEXT_NODE) {
			// TODO  not really correct
			try {
   	    	 dest.getParentElement().addTextNode(source.getNodeValue());
			} catch (DOMException e) {
				log.error("SOAPTools DOMException: " + e);
			} catch (SOAPException e) {
				log.error("SOAPTools SOAPException: " + e);
			}
			return dest;
		} else {
			org.w3c.dom.NamedNodeMap attr = source.getAttributes();
			//TODO attr
			if (attr != null) {
				for (int i = 0; i < attr.getLength(); i++) {
					Name name = env.createName(attr.item(i).getNodeName());
					((SOAPElement) dest).addAttribute(name, (String) attr.item(i).getNodeValue());
				}
			}

			org.w3c.dom.NodeList list = source.getChildNodes();
			org.w3c.dom.Element elemListItem = null;
			
			for (int i = 0; i < list.getLength(); i++) {
				if (!(list.item(i) instanceof org.w3c.dom.Text)) {
					elemListItem = (org.w3c.dom.Element) list.item(i);
					String  nodeName = elemListItem.getNodeName();
					String namespaceURI = elemListItem.getNamespaceURI();
					 org.apache.axis.message.SOAPBodyElement newNode = 
					        new org.apache.axis.message.SOAPBodyElement(namespaceURI, nodeName);
					 dest.appendChild(newNode);
					 copyNode(list.item(i), newNode, env);
				} else {
					try {
		      	    	((SOAPElement) dest).addTextNode(list.item(i).getNodeValue());
					} catch (DOMException e) {
						log.error("DOMException: " + e.getMessage(), e);
					} catch (SOAPException e) {
						log.error("SOAPException: " + e.getMessage(), e);
					}
				}
			}
		}

		return dest;
	}

	/**
	 * Creates a SOAP Fault Envelope with 
	 * exceptionText, exceptionCode and locator as 
	 * reason.
	 * @param exceptionText String
	 * @param exceptionCode String
	 * @param locator String
	 * @param createSOAP12 boolean
	 * @return faultMessage SOAPMessage 
	 * @throws Exception e
	 */
	public static SOAPMessage createSoapFault(final String exceptionText, 
			                                  final String exceptionCode, 
			                                  final String locator, 
			                                  final boolean createSOAP12)
	throws Exception {
		SOAPMessage faultMessage = null;
		SOAPPart sp = null;
		SOAPEnvelope se = null;
		
		if (createSOAP12) {
			System.setProperty("javax.xml.soap.MessageFactory", "org.apache.axis.soap.MessageFactoryImpl");
			System.setProperty("javax.xml.soap.SOAPFactory", "org.apache.axis.soap.SOAPFactoryImpl");
	        MimeHeaders mimeHeaders = new MimeHeaders();
	        mimeHeaders.addHeader("Content-Type", "application/soap+xml");
			faultMessage = new Message(SOAP12ENV, false, mimeHeaders);
			sp = (org.apache.axis.SOAPPart) faultMessage.getSOAPPart();
			se = (org.apache.axis.message.SOAPEnvelope) sp.getEnvelope();
			((org.apache.axis.message.SOAPEnvelope) se).setSoapConstants(SOAPConstants.SOAP12_CONSTANTS);
			SOAPBody soapBody = se.getBody();
			SOAPElement elemFault = soapBody.addChildElement("Fault");
			SOAPElement elemCode = elemFault.addChildElement("Code");
			
			//TODO choose Sender or Receiver etc.
			//elemCode.addChildElement("Value").addTextNode("env:Sender");
			elemCode.addChildElement("Value").addTextNode("unknown");
			SOAPElement elemReason = elemFault.addChildElement("Reason");
			SOAPElement elemReasonText = elemReason.addChildElement("Text");
			
            //choose fault reason e.g 'Processing error'...
			//elemReasonText.addTextNode("unknown");
			elemReasonText.addTextNode("exceptionText: " + exceptionText +
					                   " exceptionCode: " + exceptionCode +
					                   " locator: " + locator);
			Name nameLang = se.createName("xml:lang");
			elemReasonText.addAttribute(nameLang, "en-US");
			elemFault.addChildElement("Detail");
		} else {
			System.setProperty("javax.xml.soap.MessageFactory", "com.sun.xml.messaging.saaj.soap.MessageFactoryImpl");
			System.setProperty("javax.xml.soap.SOAPFactory", "com.sun.xml.messaging.saaj.soap.SOAPFactoryImpl");
			MessageFactory msgFactory = MessageFactory.newInstance();
			faultMessage = msgFactory.createMessage();
			sp = (SOAPPart) faultMessage.getSOAPPart();
			se = (javax.xml.soap.SOAPEnvelope) sp.getEnvelope();
			//TODO elements SOAP 1.1 fault ...
		}
		
		  return faultMessage;
	}
	
	/**
	 * creates an Exception Report within SOAP Fault
	 * @param exceptionText String
	 * @param exceptionCode String
	 * @param locator String
	 * @param createSOAP12 boolean
	 * @return exceptionReportMessage SOAPMessage
	 * @throws Exception e
	 */
	public static SOAPMessage createExceptionReport(final String exceptionText,
			final String exceptionCode, final String locator, final boolean createSOAP12)
	throws Exception {
        if (log.isDebugEnabled()) {
        	log.debug("entering createExceptionReport");
			log.debug("createExceptionReport  exceptionText: " + exceptionText);
			log.debug("createExceptionReport  exceptionCode: " + exceptionCode);
			log.debug("createExceptionReport  locator: " + locator);
        }

		SOAPMessage exceptionReportMessage = null;
		SOAPPart sp = null;
		SOAPEnvelope se = null;
		
		exceptionReportMessage = createSoapFault(exceptionText, 
				                                 exceptionCode, 
				                                 locator, 
				                                 createSOAP12);
		sp = (SOAPPart) exceptionReportMessage.getSOAPPart();
	    se = (javax.xml.soap.SOAPEnvelope) sp.getEnvelope();
		
		// se.clearBody();
		SOAPBody soapBody = se.getBody();
		SOAPElement elemDetail = (SOAPElement) soapBody.getElementsByTagName("Detail").item(0);
		SOAPElement elemExceptionReport = 
			 elemDetail.addChildElement("ExceptionReport", "ows", "http://www.opengis.net/ows");
		Name nameVersion = se.createName("version");
		elemExceptionReport.addAttribute(nameVersion, "1.0.0");
		Name nameLanguage = se.createName("language");
		elemExceptionReport.addAttribute(nameLanguage, "en");

		//TODO mehrere Exceptions ?
		SOAPElement elemException = elemExceptionReport.addChildElement("Exception");
		Name nameExceptionCode = se.createName("exceptionCode");
		elemException.addAttribute(nameExceptionCode, exceptionCode);

		if (locator != null) {
			Name nameLocator = se.createName("locator");
			elemException.addAttribute(nameLocator, locator);
		}

		SOAPElement elemExceptionText = elemException.addChildElement("ExceptionText");
		elemExceptionText.addTextNode(exceptionText);

        if (log.isDebugEnabled()) {
        	log.debug("exiting createExceptionReport");
        }

		return exceptionReportMessage;
	}
	

	/**
	 * performs a deep copy of a soap node to dom node
	 * @param source javax.xml.soap.Node source node
	 * @param  dest org.w3c.dom.Node destination node
	 * @return dest org.w3c.dom.Node destination node
	 * @throws Exception e
	 */
	public static org.w3c.dom.Node copyNode(final javax.xml.soap.Node source,
			final org.w3c.dom.Node dest) throws Exception {

		if (source instanceof javax.xml.soap.Text) {
			String v = ((javax.xml.soap.Text) source).getValue();

			org.w3c.dom.Text tn = dest.getOwnerDocument().createTextNode(v);
			return tn;
		} else {
			Iterator attr = ((SOAPElement) source).getAllAttributes();
			if (attr != null) {
				while (attr.hasNext()) {
					Name attrName = (Name) attr.next();
					String name = attrName.getQualifiedName();
					String value = ((SOAPElement) source).getAttributeValue(attrName);
					
                    if (!"%".equals(value)) {
                    	try {
				       	     String txtEncoding = cswConfig.getString(CSWInterfaceConfig.RESPONSE_ENCODING);
				      	     if (!txtEncoding.equals("NONE")) {
				      	    	value = URLDecoder.decode(value, txtEncoding);
				      	     }
					    } catch (UnsupportedEncodingException e) {
					    	log.error("UnsupportedEncodingException: " + e.getMessage(), e);
					    	throw e;
					   }
                    }
                    
					((org.w3c.dom.Element) dest).setAttribute(name, value);
				}
				//TODO nsprefixes
				/*
				 Iterator nsprefixes = ((SOAPElement)source).getNamespacePrefixes();
				 
				 if (nsprefixes != null) {
				 
				 while ( nsprefixes.hasNext() ) {
				 String nsPrefix = (String)nsprefixes.next();
				 String nsURI = ((SOAPElement)source).getNamespaceURI(nsPrefix);
				 
				 //System.out.println("nsPrefix: " + nsPrefix + "  nsURI: " + nsURI);
				 
				 ((org.w3c.dom.Element)dest).setAttribute( "xmlns:" + nsPrefix, nsURI );
				 
				 }	
				 
				 
				 }	
				 
				 
				 */

			}

			Iterator list = ((SOAPElement) source).getChildElements();

			while (list.hasNext()) {
				Object o = list.next();
				if (o instanceof SOAPElement) {
					SOAPElement elem = (SOAPElement) o;
					 String qualifiedName = elem.getElementName().getQualifiedName();
					 String uri = elem.getElementName().getURI();
					 org.w3c.dom.Element en = dest.getOwnerDocument().createElementNS(uri, qualifiedName);
					org.w3c.dom.Node n = copyNode(elem, en);
					dest.appendChild(n);

					// if node contains a textnode
					String v = (elem).getValue();
					if (v != null && v.trim().length() > 0) {
						try {
				       	     String txtEncoding = cswConfig.getString(CSWInterfaceConfig.RESPONSE_ENCODING);
				      	     if (!txtEncoding.equals("NONE")) {
				      	    	v = URLDecoder.decode(v, txtEncoding);
				      	     }
						} catch (UnsupportedEncodingException e) {
					    	log.error("UnsupportedEncodingException: " + e.getMessage(), e);
					    	throw e;
						}
						org.w3c.dom.Text tn = dest.getOwnerDocument().createTextNode(v);
						n.appendChild(tn);
					}
				}
			}
		}

		return dest;
	}
}
