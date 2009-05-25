/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2.encoding.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.ingrid.interfaces.csw2.constants.Operation;
import de.ingrid.interfaces.csw2.encoding.CSWMessageEncoding;
import de.ingrid.interfaces.csw2.exceptions.CSWException;
import de.ingrid.interfaces.csw2.exceptions.CSWInvalidParameterValueException;
import de.ingrid.interfaces.csw2.exceptions.CSWMissingParameterValueException;

public class KVPEncoding extends AbstractEncoding implements CSWMessageEncoding {

	protected Map<String, String> requestParams = null;
	protected Operation operation;
	protected List<String> acceptVersions = null; 
	
	/** Parameter names **/
	private static String SERVICE_PARAM = "SERVICE";
	private static String REQUEST_PARAM = "REQUEST";
	private static String ACCEPTVERSIONS_PARAM = "ACCEPTVERSIONS";

	/** Supported operations **/
	protected static List<Operation> SUPPORTED_OPERATIONS = Collections.unmodifiableList(Arrays.asList(new Operation[] {
		Operation.GET_CAPABILITIES,
		Operation.DESCRIBE_RECORD, 
		Operation.GET_RECORD_BY_ID
	}));

	@Override
	@SuppressWarnings("unchecked")
	public void initialize(HttpServletRequest request, HttpServletResponse response) {
		super.initialize(request, response);
		
		// get all parameters from the request and store them in a map
		// to make sure they are uppercase
		requestParams = new Hashtable<String, String>();
		Enumeration paramEnum = request.getParameterNames();
		while (paramEnum.hasMoreElements()) {
			String key = (String) paramEnum.nextElement();
			requestParams.put(key.toUpperCase(), request.getParameter(key));
		}
	}

	@Override
	public void validateRequest() throws CSWException {
		checkInitialized();
		
		// check the service parameter 
		String serviceParam = requestParams.get(SERVICE_PARAM);
		if (serviceParam == null || serviceParam.equals("")) {
			throw new CSWMissingParameterValueException("Parameter '"+SERVICE_PARAM+"' is not specified or has no value", 
					SERVICE_PARAM);
		} else {
			if (!serviceParam.equals("CSW")) {
				StringBuffer errorMsg = new StringBuffer();
				errorMsg.append("Parameter '"+SERVICE_PARAM+"' has an unsupported value.\n");
				errorMsg.append("Supported values: CSW\n");
				throw new CSWInvalidParameterValueException(errorMsg.toString(), SERVICE_PARAM);
			}
		}
		
		// check the request parameter (operation)
		String requestParam = requestParams.get(REQUEST_PARAM);
		if (requestParam == null || requestParam.equals("")) {
			throw new CSWMissingParameterValueException("Parameter '"+REQUEST_PARAM+"' is not specified or has no value", 
					REQUEST_PARAM);
		}
	}
	
	@Override
	public void validateResponse() throws CSWException {
		// TODO Auto-generated method stub
	}

	@Override
	public List<Operation> getSupportedOperations() {
		return SUPPORTED_OPERATIONS;
	}

	@Override
	public Operation getOperation() {
		checkInitialized();
		
		// get the operation name from the REQUEST parameter
		if (this.operation == null) {
			String operationName = requestParams.get(REQUEST_PARAM);
			this.operation = Operation.getByName(operationName);
		}
		return this.operation;
	}

	@Override
	public List<String> getAcceptVersions() {
		checkInitialized();
		
		if (acceptVersions == null) {
			acceptVersions = Collections.unmodifiableList(Arrays.asList(new String[] {requestParams.get(ACCEPTVERSIONS_PARAM)}));
		}
		return acceptVersions;
	}

	/**
	 * Check if the instance is initialized
	 */
	protected void checkInitialized() {
		if (this.requestParams == null) {
			throw new RuntimeException("No request parameters found. Maybe initialize() was not called.");
		}
	}
}
