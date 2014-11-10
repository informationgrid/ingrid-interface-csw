/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.encoding.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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


/**
 * KVPEncoding deals with messages defined in the key value pair format.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public class KVPEncoding extends DefaultEncoding implements CSWMessageEncoding {

	private Map<String, String> requestParams = null;
	private Operation operation;
	private List<String> acceptVersions = null;
	private CSWQuery query = null;

	/** Parameter names **/
	private static final String SERVICE_PARAM = "SERVICE";
	private static final String REQUEST_PARAM = "REQUEST";
	private static final String ACCEPTVERSION_PARAM = "ACCEPTVERSION";
	private static final String VERSION_PARAM = "VERSION";

	private static final String ID_PARAM = "ID";
	private static final String ELEMENTSETNAME_PARAM = "ELEMENTSETNAME";
	private static final String OUTPUTSCHEMA_PARAM = "OUTPUTSCHEMA";

	/** Supported operations **/
	private static List<Operation> SUPPORTED_OPERATIONS = Collections.unmodifiableList(Arrays.asList(new Operation[] {
			Operation.GET_CAPABILITIES,
			Operation.DESCRIBE_RECORD,
			Operation.GET_RECORD_BY_ID
	}));

	@Override
	public void initialize(HttpServletRequest request, HttpServletResponse response) {
		this.setRequest(request);
		this.setResponse(response);

		// reset member variables
		this.requestParams = null;
		this.operation = null;
		this.acceptVersions = null;
		this.query = null;

		// get all parameters from the request and store them in a map
		// to make sure they are uppercase
		this.requestParams = new Hashtable<String, String>();
		Enumeration<?> paramEnum = request.getParameterNames();
		while (paramEnum.hasMoreElements()) {
			String key = (String) paramEnum.nextElement();
			this.requestParams.put(key.toUpperCase(), request.getParameter(key));
		}
	}

	@Override
	public void validateRequest() throws CSWException {
		this.checkInitialized();

		// check the service parameter
		String serviceParam = this.requestParams.get(SERVICE_PARAM);
		if (serviceParam == null || serviceParam.length() == 0) {
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
		String requestParam = this.requestParams.get(REQUEST_PARAM);
		if (requestParam == null || requestParam.equals("")) {
			throw new CSWMissingParameterValueException("Parameter '"+REQUEST_PARAM+"' is not specified or has no value",
					REQUEST_PARAM);
		}
	}

	@Override
	public List<Operation> getSupportedOperations() {
		return SUPPORTED_OPERATIONS;
	}

	@Override
	public Operation getOperation() throws CSWOperationNotSupportedException {
		this.checkInitialized();

		// get the operation name from the REQUEST parameter
		if (this.operation == null) {
			String operationName = this.requestParams.get(REQUEST_PARAM);
			this.operation = Operation.getByName(operationName);
		}
		return this.operation;
	}

	@Override
	public List<String> getAcceptVersions() {
		this.checkInitialized();

		if (this.acceptVersions == null) {
			String versions = this.requestParams.get(ACCEPTVERSION_PARAM);
			if (versions != null)
				this.acceptVersions = Collections.unmodifiableList(Arrays.asList(versions.split(",")));
		}
		return this.acceptVersions;
	}

	@Override
	public String getVersion() {
		this.checkInitialized();

		return this.requestParams.get(VERSION_PARAM);
	}

	@Override
	public CSWQuery getQuery() {
		this.checkInitialized();

		if (this.query == null) {
			this.query = new GenericQuery();
			try {
				// only required for GetRecordById operation
				Operation operation = this.getOperation();
				if (operation == Operation.GET_RECORD_BY_ID) {
					// NOTE: getting enum values may throw an IllegalArgumentException,
					// which is ok

					// extract the ids
					if (this.requestParams.containsKey(ID_PARAM)) {
						this.query.setId(this.requestParams.get(ID_PARAM));
					}
					// extract the element set name
					if (this.requestParams.containsKey(ELEMENTSETNAME_PARAM)) {
						String elementSetNameStr = this.requestParams.get(ELEMENTSETNAME_PARAM);
						this.query.setElementSetName(ElementSetName.valueOf(elementSetNameStr.toUpperCase()));
					}
					// extract the output schema
					if (this.requestParams.containsKey(OUTPUTSCHEMA_PARAM)) {
						String schemaUri = this.requestParams.get(OUTPUTSCHEMA_PARAM);
						this.query.setOutputSchema(Namespace.getByUri(schemaUri));
					}
				}
			}
			catch (CSWOperationNotSupportedException ex) {}
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
		if (this.requestParams == null) {
			throw new RuntimeException("No request parameters found. Maybe initialize() was not called.");
		}
	}
}
