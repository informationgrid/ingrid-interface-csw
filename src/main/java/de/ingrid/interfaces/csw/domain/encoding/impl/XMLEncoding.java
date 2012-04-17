/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.encoding.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.interfaces.csw.domain.constants.ElementSetName;
import de.ingrid.interfaces.csw.domain.constants.Namespace;
import de.ingrid.interfaces.csw.domain.constants.Operation;
import de.ingrid.interfaces.csw.domain.encoding.CSWMessageEncoding;
import de.ingrid.interfaces.csw.domain.exceptions.CSWException;
import de.ingrid.interfaces.csw.domain.exceptions.CSWInvalidParameterValueException;
import de.ingrid.interfaces.csw.domain.exceptions.CSWMissingParameterValueException;
import de.ingrid.interfaces.csw.domain.exceptions.CSWOperationNotSupportedException;
import de.ingrid.interfaces.csw.domain.query.CSWQuery;
import de.ingrid.interfaces.csw.domain.query.impl.GenericQuery;
import de.ingrid.interfaces.csw.tools.StringUtils;
import de.ingrid.utils.xml.Csw202NamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * XMLEncoding deals with messages defined in the XML format.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public class XMLEncoding extends DefaultEncoding implements CSWMessageEncoding {

	private Node requestBody = null;
	private Operation operation = null;
	private List<String> acceptVersions = null;
	private CSWQuery query = null;

	/** Tool for evaluating xpath **/
	private XPathUtils xpath = new XPathUtils(new Csw202NamespaceContext());

	/** Parameter xpath (namespace agnostic) **/
	private static String SERVICE_PARAM_XPATH = "/*/@service";
	private static String DESCREC_VERSION_PARAM_XPATH = "/child::*[name()='DescribeRecord']/@version";
	private static String GETCAP_VERSION_PARAM_XPATH = "/child::*[name()='GetCapabilities']/child::*[name()='AcceptVersions']/child::*[name()='Version']";

	/** Supported operations **/
	private static List<Operation> SUPPORTED_OPERATIONS = Collections.unmodifiableList(Arrays.asList(new Operation[] {
			Operation.GET_CAPABILITIES,
			Operation.DESCRIBE_RECORD,
			Operation.GET_RECORDS,
			Operation.GET_RECORD_BY_ID
	}));

	@Override
	public final void initialize(HttpServletRequest request, HttpServletResponse response) {
		this.setRequest(request);
		this.setResponse(response);

		// reset member variables
		this.requestBody = null;
		this.operation = null;
		this.acceptVersions = null;
		this.query = null;

		this.setRequestBody(this.extractRequestBody(request));
	}

	/**
	 * Extract the request body from the request. Subclasses will override this.
	 * @param request
	 * @return Element
	 */
	protected Node extractRequestBody(HttpServletRequest request) {
		DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
		df.setNamespaceAware(true);
		try {
			Document requestDocument = df.newDocumentBuilder().parse(request.getInputStream());
			return requestDocument.getDocumentElement();
		} catch(Exception e) {
			throw new RuntimeException("Error parsing request: ", e);
		}
	}

	@Override
	public void validateRequest() throws CSWException {
		this.checkInitialized();

		// check the service parameter
		String service = this.xpath.getString(this.getRequestBody(), SERVICE_PARAM_XPATH);
		if (service == null || service.length() == 0) {
			throw new CSWMissingParameterValueException("Attribute 'service' is not specified or has no value",
					"service");
		} else {
			if (!service.equals("CSW")) {
				StringBuffer errorMsg = new StringBuffer();
				errorMsg.append("Parameter 'service' has an unsupported value.\n");
				errorMsg.append("Supported values: CSW\n");
				throw new CSWInvalidParameterValueException(errorMsg.toString(), "version");
			}
		}
	}

	@Override
	public List<Operation> getSupportedOperations() {
		return SUPPORTED_OPERATIONS;
	}

	@Override
	public Operation getOperation() throws CSWOperationNotSupportedException {
		this.checkInitialized();

		if (this.operation == null) {
			String operationName = this.getRequestBody().getNodeName();
			this.operation = Operation.getByName(operationName);
		}
		return this.operation;
	}

	@Override
	public List<String> getAcceptVersions() {
		this.checkInitialized();

		if (this.acceptVersions == null) {
			this.acceptVersions = new ArrayList<String>();

			NodeList versionNodes = this.xpath.getNodeList(this.getRequestBody(), GETCAP_VERSION_PARAM_XPATH);
			int length = versionNodes.getLength();
			for (int i=0; i<length; i++) {
				Node curVersionNode = versionNodes.item(i);
				if (curVersionNode != null) {
					this.acceptVersions.add(curVersionNode.getTextContent());
				}
			}
			this.acceptVersions = Collections.unmodifiableList(this.acceptVersions);
		}
		return this.acceptVersions;
	}

	@Override
	public String getVersion() {
		this.checkInitialized();
		return this.xpath.getString(this.getRequestBody(), DESCREC_VERSION_PARAM_XPATH);
	}

	@Override
	public CSWQuery getQuery() {
		this.checkInitialized();

		if (this.query == null) {
			this.query = new GenericQuery();
			try {
				// NOTE: getting enum values may throw an IllegalArgumentException, which is ok
				Node requestBody = this.getRequestBody();
				// only required for GetRecordById, GetRecords operations
				Operation operation = this.getOperation();
				if (operation == Operation.GET_RECORD_BY_ID) {
					// extract the id
					String id = this.xpath.getString(requestBody, "/GetRecordById/Id");
					if (id != null) {
						this.query.setId(id);
					}
					// extract the element set name
					String elementSetNameStr = this.xpath.getString(requestBody, "/GetRecordById/ElementSetName");
					if (elementSetNameStr != null) {
						this.query.setElementSetName(ElementSetName.valueOf(elementSetNameStr.toUpperCase()));
					}
					// extract the output schema
					String schemaUri = this.xpath.getString(requestBody, "/GetRecordById/OutputSchema");
					if (schemaUri != null) {
						this.query.setOutputSchema(Namespace.getByUri(schemaUri));
					}
				}
				else if (operation == Operation.GET_RECORDS) {
					// extract the filter constraint
					Node filter = this.xpath.getNode(requestBody, "/child::*[name()='GetRecords']/child::*[name()='Query']/child::*[name()='Constraint']/child::*[name()='Filter']");
					if (filter != null) {
						this.query.setConstraint(StringUtils.stringToDocument(StringUtils.nodeToString(filter)));
					}
					// extract the constraint language version
					String filterVersion = this.xpath.getString(requestBody, "/GetRecords/Query/Constraint/@version");
					if (filterVersion != null) {
						this.query.setConstraintLanguageVersion(filterVersion);
					}
					// extract the element set name
					String elementSetNameStr = this.xpath.getString(requestBody, "/GetRecords/Query/ElementSetName");
					if (elementSetNameStr != null) {
						this.query.setElementSetName(ElementSetName.valueOf(elementSetNameStr.toUpperCase()));
					}
				}
			}
			catch (Exception ex) {
			}
		}
		return this.query;
	}

	@Override
	public void validateResponse() throws CSWException {
		// TODO Auto-generated method stub
	}

	/**
	 * Check if the instance is initialized
	 */
	protected void checkInitialized() {
		if (this.getRequestBody() == null) {
			throw new RuntimeException("No request document found. Maybe initialize() was not called.");
		}
	}

	/**
	 * Get the request body
	 * @return Node
	 */
	protected Node getRequestBody() {
		return this.requestBody;
	}

	/**
	 * Set the request body
	 * @param requestBody The requestBody node to set
	 */
	protected void setRequestBody(Node requestBody) {
		this.requestBody = requestBody;
	}
}
