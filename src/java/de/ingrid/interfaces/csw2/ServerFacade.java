/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import de.ingrid.interfaces.csw.tools.XMLTools;
import de.ingrid.interfaces.csw2.constants.ConfigurationKeys;
import de.ingrid.interfaces.csw2.constants.Operation;
import de.ingrid.interfaces.csw2.constants.RequestType;
import de.ingrid.interfaces.csw2.encoding.CSWMessageEncoding;
import de.ingrid.interfaces.csw2.exceptions.CSWException;
import de.ingrid.interfaces.csw2.exceptions.CSWOperationNotSupportedException;
import de.ingrid.interfaces.csw2.request.CSWRequest;
import de.ingrid.interfaces.csw2.request.DescribeRecordRequest;
import de.ingrid.interfaces.csw2.request.GetCapabilitiesRequest;
import de.ingrid.interfaces.csw2.request.GetDomainRequest;
import de.ingrid.interfaces.csw2.request.GetRecordByIdRequest;
import de.ingrid.interfaces.csw2.request.GetRecordsRequest;
import de.ingrid.interfaces.csw2.response.CSWResponse;
import de.ingrid.interfaces.csw2.tools.SimpleSpringBeanFactory;

public class ServerFacade {

	/** The logging object **/
	private static Log log = LogFactory.getLog(ServerFacade.class);

	/**
	 * Handle a GET Request 
	 * @param request
	 * @param response
	 * @throws CSWException 
	 */
	static public void handleGetRequest(HttpServletRequest request, HttpServletResponse response) throws CSWException {

		try {
			handleRequest(RequestType.GET, request, response);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			String errorXmlMsg = null;
			if (e instanceof CSWException) {
				errorXmlMsg = CSWException.createXmlExceptionReport(e.getMessage(), ((CSWException) e)
						.getExceptionCode(), ((CSWException) e).getLocator());
			} else {
				errorXmlMsg = CSWException.createXmlExceptionReport(e.getMessage(), "NoApplicableCode", null);
			}
			try {
				response.getOutputStream().print(errorXmlMsg);
			} catch (IOException ioe) {
				log.error("Unable to send error XML message to client: " + ioe.getMessage());
			}
		}
	}
	
	/**
	 * Handle a POST Request 
	 * @param request
	 * @param response
	 * @throws CSWException 
	 */
	static public void handlePostRequest(HttpServletRequest request, HttpServletResponse response) throws CSWException {

		try {
			handleRequest(RequestType.POST, request, response);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			// TODO exception handling
		}
	}
	
	/**
	 * Handle a SOAP Request 
	 * @param request
	 * @param response
	 * @throws CSWException 
	 */
	static public void handleSoapRequest(HttpServletRequest request, HttpServletResponse response) throws CSWException {

		try {
			handleRequest(RequestType.SOAP, request, response);
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			// TODO exception handling
		}
	}

	/**
	 * Generic request method
	 * @param type
	 * @param request
	 * @throws CSWException
	 */
	static protected void handleRequest(RequestType type, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

		// initialize the CSWMessageEncoding
		CSWMessageEncoding encodingImpl = ServerFacade.getMessageEncodingInstance(type);
		encodingImpl.initialize(request, response);
		if (log.isDebugEnabled()) {
			log.debug("Handle "+type+" request");
		}
		
		// validate the request message non-operation-specific
		try {
			encodingImpl.validateRequest();
		} catch(CSWException e) {
			throw e;
		}
		
		// check if the operation is supported
		Operation operation = encodingImpl.getOperation();
		List<Operation> supportedOperations = encodingImpl.getSupportedOperations();
		if (!supportedOperations.contains(operation)) {
			StringBuffer errorMsg = new StringBuffer();
			errorMsg.append("The operation '"+operation+"' is not supported in a "+type+" request.\n");
			errorMsg.append("Supported values:\n");
			errorMsg.append(supportedOperations.toString()+"\n");
			throw new CSWOperationNotSupportedException(errorMsg.toString(), operation.toString());
		}
		if (log.isDebugEnabled()) {
			log.debug("Operation: "+encodingImpl.getOperation());
		}
		
		// initialize the CSWRequest instance
		CSWRequest requestImpl = ServerFacade.getRequestInstance(operation);
		requestImpl.initialize(encodingImpl);
		
		// validate the request message operation-specific
		try {
			requestImpl.validate();
		} catch(CSWException e) {
			throw e;
		}
		
		// initialize the CSWServer instance
		CSWServer serverImpl = SimpleSpringBeanFactory.INSTANCE.getBean(
				ConfigurationKeys.CSW_SERVER_IMPLEMENTATION, CSWServer.class); 
		
		// perform the requested operation
		Document result = null;
		if (operation == Operation.GET_CAPABILITIES) {
			result = serverImpl.process((GetCapabilitiesRequest)requestImpl);
		}
		else if(operation == Operation.DESCRIBE_RECORD) {
			result = serverImpl.process((DescribeRecordRequest)requestImpl);
		}
		else if(operation == Operation.GET_DOMAIN) {
			result = serverImpl.process((GetDomainRequest)requestImpl);
		}
		else if(operation == Operation.GET_RECORDS) {
			result = serverImpl.process((GetRecordsRequest)requestImpl);
		}
		else if(operation == Operation.GET_RECORD_BY_ID) {
			result = serverImpl.process((GetRecordByIdRequest)requestImpl);
		}
		if (log.isDebugEnabled()) {
			log.debug("Result: "+XMLTools.toString(result));
		}
		
		// initialize the CSWResponse instance
		CSWResponse responseImpl = SimpleSpringBeanFactory.INSTANCE.getBean(
				ConfigurationKeys.CSW_RESPONSE_IMPLEMENTATION, CSWResponse.class);
		responseImpl.initialize(encodingImpl);
		responseImpl.setContent(result);
		
		// serialize the response
		responseImpl.serialize();
	}

	/**
	 * Get the CSWMesageEncoding implementation for a given request type from the server configuration
	 * @param type The request type
	 * @return The CSWMesageEncoding instance
	 */
	@SuppressWarnings({"unchecked"})
	private static CSWMessageEncoding getMessageEncodingInstance(RequestType type) {
		Map encodingImplementations = SimpleSpringBeanFactory.INSTANCE.getBean(
				ConfigurationKeys.CSW_ENCODING_IMPLEMENTATIONS, Map.class);
		if (encodingImplementations == null)
			throw new RuntimeException("ServerFacade is not configured properly. Parameter '"+
					ConfigurationKeys.CSW_ENCODING_IMPLEMENTATIONS+"' is missing");

		String beanId = (String)encodingImplementations.get(type.toString());
		if (beanId == null)
			throw new RuntimeException("Unknown encoding type requested for '"+
					ConfigurationKeys.CSW_ENCODING_IMPLEMENTATIONS+"' in server configuration: "+type);
		
		CSWMessageEncoding encoding = SimpleSpringBeanFactory.INSTANCE.getBean(beanId, CSWMessageEncoding.class);
		return encoding;
	}

	/**
	 * Get the CSWRequest implementation for a given request type from the server configuration
	 * @param operation The operation
	 * @return The CSWRequest instance
	 */
	@SuppressWarnings({"unchecked"})
	static CSWRequest getRequestInstance(Operation operation) {
		Map requestImplementations = SimpleSpringBeanFactory.INSTANCE.getBean(
				ConfigurationKeys.CSW_REQUEST_IMPLEMENTATIONS, Map.class);
		if (requestImplementations == null)
			throw new RuntimeException("ServerFacade is not configured properly. Parameter '"+
					ConfigurationKeys.CSW_REQUEST_IMPLEMENTATIONS+"' is missing");

		CSWRequest request = null;
		try {
			request = (CSWRequest)Class.forName(requestImplementations.get(operation.toString()).toString()).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("ServerFacade is not configured properly. No class found for operation "+
					operation+" in map '"+ConfigurationKeys.CSW_REQUEST_IMPLEMENTATIONS+"'.");
		}
		return request;
	}
}
