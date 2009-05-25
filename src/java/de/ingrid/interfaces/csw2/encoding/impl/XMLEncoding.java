/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2.encoding.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.interfaces.csw2.constants.Operation;
import de.ingrid.interfaces.csw2.encoding.CSWMessageEncoding;
import de.ingrid.interfaces.csw2.exceptions.CSWException;
import de.ingrid.interfaces.csw2.exceptions.CSWInvalidParameterValueException;
import de.ingrid.interfaces.csw2.exceptions.CSWMissingParameterValueException;
import de.ingrid.interfaces.csw2.tools.XPathUtils;

public class XMLEncoding extends AbstractEncoding implements CSWMessageEncoding {
	
	Document requestDocument = null;
	Element requestBody = null;
	protected Operation operation;
	protected List<String> acceptVersions = null; 

	/** Parameter names **/
	private static String SERVICE_PARAM = "service";
	
	/** Supported operations **/
	protected static List<Operation> SUPPORTED_OPERATIONS = Collections.unmodifiableList(Arrays.asList(new Operation[] {
		Operation.GET_CAPABILITIES,
		Operation.DESCRIBE_RECORD, 
		Operation.GET_RECORDS, 
		Operation.GET_RECORD_BY_ID
	}));

	@Override
	public void initialize(HttpServletRequest request, HttpServletResponse response) {		
		super.initialize(request, response);
		
        // get the body of the HTTP request
		DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
		df.setNamespaceAware(true);
		df.setIgnoringElementContentWhitespace(true);
		try {
			requestDocument = df.newDocumentBuilder().parse(request.getInputStream());
		} catch(Exception e) {
			throw new RuntimeException("Error parsing request: ", e);
		}
		requestBody = (Element)requestDocument.getFirstChild();
	}

	@Override
	public void validateRequest() throws CSWException {
		checkInitialized();
		
		// check the service parameter 
		String serviceParam = requestBody.getAttribute(SERVICE_PARAM);
		if (serviceParam == null || serviceParam.equals("")) {
			throw new CSWMissingParameterValueException("Attribute '"+SERVICE_PARAM+"' is not specified or has no value", 
					SERVICE_PARAM);
		} else {
			if (!serviceParam.equals("CSW")) {
				StringBuffer errorMsg = new StringBuffer();
				errorMsg.append("Parameter '"+SERVICE_PARAM+"' has an unsupported value.\n");
				errorMsg.append("Supported values: CSW\n");
				throw new CSWInvalidParameterValueException(errorMsg.toString(), SERVICE_PARAM);
			}
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
		
		if (this.operation == null) {
			String operationName = requestBody.getLocalName();
			this.operation = Operation.getByName(operationName);
		}
		return this.operation;
	}

	@Override
	public List<String> getAcceptVersions() {
		checkInitialized();
		
		if (acceptVersions == null) {
			acceptVersions = new ArrayList<String>();
			
			NodeList versionNodes = XPathUtils.getNodeList(requestDocument, "//AcceptVersions/Version");
			int length = versionNodes.getLength();
			for (int i=0; i<length; i++) {
				Node curVersionNode = versionNodes.item(i);
				if (curVersionNode != null) {
					acceptVersions.add(curVersionNode.getTextContent());
				}
			}
			acceptVersions = Collections.unmodifiableList(acceptVersions);
		}
		return acceptVersions;
	}

	/**
	 * Check if the instance is initialized
	 */
	protected void checkInitialized() {
		if (this.requestDocument == null) {
			throw new RuntimeException("No request document found. Maybe initialize() was not called.");
		}
	}
}
