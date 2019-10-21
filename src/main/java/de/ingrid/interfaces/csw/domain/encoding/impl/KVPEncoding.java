/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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

import de.ingrid.interfaces.csw.config.ApplicationProperties;
import de.ingrid.interfaces.csw.domain.constants.*;
import de.ingrid.interfaces.csw.domain.encoding.CSWMessageEncoding;
import de.ingrid.interfaces.csw.domain.exceptions.CSWException;
import de.ingrid.interfaces.csw.domain.exceptions.CSWInvalidParameterValueException;
import de.ingrid.interfaces.csw.domain.exceptions.CSWMissingParameterValueException;
import de.ingrid.interfaces.csw.domain.exceptions.CSWOperationNotSupportedException;
import de.ingrid.interfaces.csw.domain.query.CSWQuery;
import de.ingrid.interfaces.csw.domain.query.impl.GenericQuery;
import de.ingrid.interfaces.csw.tools.OGCFilterTools;
import de.ingrid.interfaces.csw.tools.StringUtils;
import org.w3c.dom.Document;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


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
    private static final String CONSTRAINT_PARAM = "CONSTRAINT";
    private static final String TYPENAMES_PARAM = "TYPENAMES";
    private static final String RESULTTYPE_PARAM = "RESULTTYPE";
    private static final String MAXRECORDS_PARAM = "MAXRECORDS";
    private static final String STARTPOSITION_PARAM = "STARTPOSITION";
    private static final String SORTBY_PARAM = "SORTBY";

    /** Fixed values **/
    private static final ConstraintLanguage CONSTRAINT_LANGUAGE_VALUE = ConstraintLanguage.FILTER;
    private static final String CONSTRAINT_LANGUAGE_VERSION_VALUE = "1.1.0";

    /** Supported operations **/
    private static List<Operation> SUPPORTED_OPERATIONS = Collections.unmodifiableList(Arrays.asList(
            Operation.GET_CAPABILITIES,
            Operation.DESCRIBE_RECORD,
            Operation.GET_RECORDS,
            Operation.GET_RECORD_BY_ID
    ));

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
                StringBuilder errorMsg = new StringBuilder();
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
    public List<Operation> getSupportedOperations(Type type) {
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
    public CSWQuery getQuery() throws CSWException {
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
                else if (operation == Operation.GET_RECORDS) {

                    // extract the filter constraint
                    if (this.requestParams.containsKey(CONSTRAINT_PARAM) && !this.requestParams.get(CONSTRAINT_PARAM).isEmpty()) {
                        Document constraint = StringUtils.stringToDocument(this.requestParams.get(CONSTRAINT_PARAM));

                        // add query additional parameters from configuration
                        String[] queryConstraints = ApplicationProperties.get(
                                ConfigurationKeys.QUERY_PARAMETER_2_CONSTRAINTS, "").split(",");
                        for (String queryConstraint : queryConstraints) {
                            if (queryConstraint.trim().length() > 0) {
                                String param = this.requestParams.get(queryConstraint);
                                if (param != null && param.length() > 0) {
                                    OGCFilterTools.addPropertyIsEqual(constraint, queryConstraint, param);
                                }

                            }
                        }
                        this.query.setConstraint(constraint);
                    }
                    // set the constraint language
                    this.query.setConstraintLanguage(CONSTRAINT_LANGUAGE_VALUE);
                    this.query.setConstraintLanguageVersion(CONSTRAINT_LANGUAGE_VERSION_VALUE);

                    // extract the element set name
                    if (this.requestParams.containsKey(ELEMENTSETNAME_PARAM)) {
                        String elementSetNameStr = this.requestParams.get(ELEMENTSETNAME_PARAM);
                        this.query.setElementSetName(ElementSetName.valueOf(elementSetNameStr.toUpperCase()));
                    }
                    // extract the typeNames
                    if (this.requestParams.containsKey(TYPENAMES_PARAM)) {
                        String typeNamesStr = this.requestParams.get(TYPENAMES_PARAM);
                        if (typeNamesStr != null) {
                            String[] typeNameStrings = typeNamesStr.split(",");
                            TypeName[] typeNames = new TypeName[typeNameStrings.length];
                            for (int i = 0; i < typeNameStrings.length; i++) {
                                typeNames[i] = TypeName.getFromQualifiedString(typeNameStrings[i].trim());
                            }
                            this.query.setTypeNames(typeNames);
                        } else {
                            this.query.setTypeNames(null);
                        }
                    }
                    // extract the result type
                    if (this.requestParams.containsKey(RESULTTYPE_PARAM)) {
                        String resultTypeStr = this.requestParams.get(RESULTTYPE_PARAM);
                        this.query.setResultType(ResultType.valueOf(resultTypeStr.toUpperCase()));
                    }

                    // extract the maxRecords
                    if (this.requestParams.containsKey(MAXRECORDS_PARAM)) {
                        int maxRecords = Integer.MAX_VALUE;
                        String maxRecordsStr = this.requestParams.get(MAXRECORDS_PARAM);
                        if (maxRecordsStr != null) {
                            maxRecords = Integer.parseInt(maxRecordsStr);
                        }
                        maxRecords = Math.min(maxRecords, ApplicationProperties.getInteger(
                                ConfigurationKeys.MAX_RETURNED_HITS, Integer.MAX_VALUE));
                        if (maxRecords <0) {
                            StringBuilder errorMsg = new StringBuilder();
                            errorMsg.append("Parameter 'maxRecords' has an unsupported value.\n");
                            errorMsg.append("Supported values: positive integer\n");
                            throw new CSWInvalidParameterValueException(errorMsg.toString(), "maxRecords");
                        }
                        this.query.setMaxRecords(maxRecords);
                    }

                    // extract the startPosition
                    if (this.requestParams.containsKey(STARTPOSITION_PARAM)) {
                        int startPosition = 1;
                        String startPositionStr = this.requestParams.get(STARTPOSITION_PARAM);
                        if (startPositionStr != null) {
                            startPosition = Integer.parseInt(startPositionStr);
                        }
                        if (startPosition <=0) {
                            StringBuilder errorMsg = new StringBuilder();
                            errorMsg.append("Parameter 'startPosition' has an unsupported value.\n");
                            errorMsg.append("Supported values: positive integer\n");
                            throw new CSWInvalidParameterValueException(errorMsg.toString(), "startPosition");
                        }
                        this.query.setStartPosition(startPosition);
                    }

                    // extract the sort part
                    if (this.requestParams.containsKey(SORTBY_PARAM)) {
                        String sortParam = this.requestParams.get(SORTBY_PARAM);
                        String[] sortParamArray= sortParam.split(",");
                        if ((sortParamArray.length % 2) != 0) {
                            StringBuffer errorMsg = new StringBuffer();
                            errorMsg.append("Parameter '" + SORTBY_PARAM + "' has an unsupported value.\n");
                            errorMsg.append("Supported values: comma separated list of field name and 'A' or 'D' as sort order\n");
                            throw new CSWInvalidParameterValueException(errorMsg.toString(), SORTBY_PARAM);
                        }

                        String propertyName = null;
                        String sortOrder = null;
                        StringBuffer sortXmlStr = new StringBuffer();
                        for (int i=0; i< sortParamArray.length; i++) {
                            if (sortXmlStr.length() == 0) {
                                sortXmlStr.append("<SortBy xmlns=\"http://www.opengis.net/ogc\">");
                            }
                            if (i % 2 == 0) {
                                propertyName = sortParamArray[i].trim().toLowerCase();
                            } else {
                                sortOrder = sortParamArray[i].trim().toLowerCase();
                                if (!sortOrder.equals("a") && !sortOrder.equals("d")) {
                                    StringBuffer errorMsg = new StringBuffer();
                                    errorMsg.append("Parameter '" + SORTBY_PARAM + "' has an unsupported value.\n");
                                    errorMsg.append("Supported values: comma separated list of field name and 'A' or 'D' as sort order\n");
                                    throw new CSWInvalidParameterValueException(errorMsg.toString(), SORTBY_PARAM);
                                }
                                sortXmlStr.append("<SortProperty>");
                                sortXmlStr.append("<PropertyName>" + propertyName + "</PropertyName>");
                                sortXmlStr.append("<SortOrder>" + (sortOrder.equals("a") ? "ASC" : "DESC" )+ "</SortOrder>");
                                sortXmlStr.append("</SortProperty>");
                            }
                        }
                        if (sortXmlStr.length() > 0) {
                            sortXmlStr.append("</SortBy>");
                        }

                        Document sort = StringUtils.stringToDocument(sortXmlStr.toString());
                        if (sort != null) {
                            this.query.setSortBy(sort);
                        }
                    }
                }
            } catch (CSWInvalidParameterValueException ex) {
                // re-throw CSW Exception
                throw ex;
            } catch (Exception ex) {
                throw new CSWException("An error occured while extracting the query: ", ex);
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
        if (this.requestParams == null) {
            throw new RuntimeException("No request parameters found. Maybe initialize() was not called.");
        }
    }
}
