/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.encoding.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.ingrid.interfaces.csw.config.ApplicationProperties;
import de.ingrid.interfaces.csw.domain.constants.ConfigurationKeys;
import de.ingrid.interfaces.csw.domain.encoding.CSWMessageEncoding;
import de.ingrid.interfaces.csw.domain.exceptions.CSWException;
import de.ingrid.interfaces.csw.tools.StringUtils;

/**
 * Soap12Encoding deals with messages defined in the SOAP 1.2 format.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public class Soap12Encoding extends XMLEncoding implements CSWMessageEncoding {

	/**
	 * the soap envelope for SOAP version 1.2
	 */
	private static final String SOAP12ENV =
			"<?xml version=\"1.0\" encoding=\"" + ApplicationProperties.get(ConfigurationKeys.RESPONSE_ENCODING) + "\"?>\n" +
					"<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
					"		xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
					"		xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
					"	<soapenv:Body>\n" +
					"	</soapenv:Body>\n" +
					"</soapenv:Envelope>";

	private SOAPMessage soapMessage = null;

	/**
	 * Extract the request body from the request. Subclasses will override this.
	 * @param request
	 * @return Node
	 */
	@Override
	protected Node extractRequestBody(HttpServletRequest request) {
		try {
			// get the envelope and body of the SOAP request
			MessageFactory msgFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

			// get the request headers
			MimeHeaders headers = new MimeHeaders();
			Enumeration<?> headerEnum = request.getHeaderNames();
			while (headerEnum.hasMoreElements()) {
				String headerName = (String)headerEnum.nextElement();
				String headerValue = request.getHeader(headerName);
				StringTokenizer values = new StringTokenizer(headerValue, ",");
				while (values.hasMoreTokens()) {
					headers.addHeader(headerName, values.nextToken().trim());
				}
			}

			// create the soap message
			this.soapMessage = msgFactory.createMessage(headers, request.getInputStream());

			// get the request body
			Node requestBody = getMessagePayload(this.soapMessage);

			// create a new document with requestBody as root to get rid of the soap parts
			Document requestDoc = StringUtils.stringToDocument(StringUtils.nodeToString(requestBody));
			return requestDoc.getDocumentElement();
		} catch(Exception e) {
			throw new RuntimeException("Error parsing request: ", e);
		}
	}


	@Override
	public void validateRequest() throws CSWException {
		super.validateRequest();

		// check for soap version 1.2 is not necessary here, because an exception
		// will be thrown in initialize the initialize method already
	}

	@Override
	public void writeResponse(Document document) throws Exception {

		SOAPMessage message = Soap12Encoding.createSoapMessage(document);

		// need to saveChanges 'cos we're going to use the
		// MimeHeaders to set HTTP response information. These
		// MimeHeaders are generated as part of the save
		if (message.saveRequired()) {
			message.saveChanges();
		}

		HttpServletResponse response = this.getResponse();
		response.setStatus(HttpServletResponse.SC_OK);

		// copy MimeHeaders from the message to the response
		MimeHeaders headers = message.getMimeHeaders();
		Iterator<?> it = headers.getAllHeaders();
		while (it.hasNext()) {
			MimeHeader header = (MimeHeader)it.next();

			String[] values = headers.getHeader(header.getName());
			if (values.length == 1) {
				response.setHeader(header.getName(), header.getValue());
			}
			else
			{
				StringBuffer concat = new StringBuffer();
				int i = 0;
				while (i < values.length) {
					if (i != 0) {
						concat.append(',');
					}
					concat.append(values[i++]);
				}
				response.setHeader(header.getName(), concat.toString());
			}
		}

		// write out the message on the response stream
		OutputStream os = response.getOutputStream();
		message.writeTo(os);
		os.flush();
	}

	@Override
	public void reportError(Exception e) throws Exception {

		Document errorXmlMsg = null;
		if (e instanceof CSWException) {
			errorXmlMsg = ((CSWException)e).toSoapExceptionReport();
		} else {
			errorXmlMsg = new CSWException(e.getMessage(), "NoApplicableCode", null).toSoapExceptionReport();
		}
		this.writeResponse(errorXmlMsg);
	}

	/**
	 * Create a soap message from an xml document
	 * @param document The document
	 * @return SOAPMessage
	 * @throws Exception
	 */
	private static SOAPMessage createSoapMessage(final org.w3c.dom.Document document) throws Exception {

		MessageFactory msgFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

		// headers
		MimeHeaders mimeHeaders = new MimeHeaders();
		mimeHeaders.addHeader("Content-Type", "application/soap+xml");

		// create the message
		InputStream bais = new ByteArrayInputStream(SOAP12ENV.getBytes());
		SOAPMessage message = msgFactory.createMessage(mimeHeaders, bais);
		bais.close();

		// add the document to the message body
		SOAPBody soapBody = message.getSOAPPart().getEnvelope().getBody();
		soapBody.addDocument(document);

		return message;
	}

	/**
	 * Get the payload of a SOAP message
	 * @param message The message
	 * @return Node
	 * @throws SOAPException
	 */
	private static Node getMessagePayload(SOAPMessage message) throws SOAPException {
		Node requestBody = null;
		SOAPBody body = message.getSOAPBody();
		Iterator<?> children = body.getChildElements();
		while (children.hasNext()) {
			Object obj = children.next();
			if (obj instanceof SOAPElement) {
				requestBody = (Node)obj;
				break;
			}
		}
		return requestBody;
	}
}
