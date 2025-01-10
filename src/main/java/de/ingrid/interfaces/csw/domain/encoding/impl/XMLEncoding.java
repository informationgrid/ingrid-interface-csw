/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
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
import de.ingrid.utils.xml.Csw202NamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    /**
     * Tool for evaluating xpath
     **/
    private XPathUtils xpath = new XPathUtils(new Csw202NamespaceContext());

    /**
     * Parameter xpath (namespace agnostic)
     **/
    private static String SERVICE_PARAM_XPATH = "/*/@service";
    private static String VERSION_PARAM_XPATH = "/*/@version";
    private static String DESCREC_VERSION_PARAM_XPATH = "/csw:DescribeRecord/@version";
    private static String GETCAP_VERSION_PARAM_XPATH = "/csw:GetCapabilities/csw:AcceptVersions/csw:Version";

    private static Log log = LogFactory.getLog(XMLEncoding.class);

    /**
     * Supported operations
     **/
    private static List<Operation> SUPPORTED_OPERATIONS = Collections.unmodifiableList(Arrays.asList(new Operation[]{
            Operation.GET_CAPABILITIES,
            Operation.DESCRIBE_RECORD,
            Operation.GET_RECORDS,
            Operation.GET_RECORD_BY_ID
    }));
    private static List<Operation> SUPPORTED_OPERATIONS_CSWT = Collections.unmodifiableList(Arrays.asList(new Operation[]{
            Operation.TRANSACTION
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

        if (log.isDebugEnabled()) {
            log.debug("Request initialized with: " + StringUtils.nodeToString(this.requestBody));
        }

    }

    protected static Document extractFromDocument(Node node) throws Exception {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Node copy = node.cloneNode(true);
        Node adopted = doc.adoptNode(copy);
        doc.appendChild(adopted);
        return doc;
    }

    /**
     * Extract the request body from the request. Subclasses will override this.
     *
     * @param request
     * @return Element
     */
    protected Node extractRequestBody(HttpServletRequest request) {
        DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
        df.setNamespaceAware(true);
        try {
            Document requestDocument = df.newDocumentBuilder().parse(request.getInputStream());
            return requestDocument.getDocumentElement();
        } catch (Exception e) {
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
                throw new CSWInvalidParameterValueException(errorMsg.toString(), "service");
            }
        }

        // check the version parameter
        String version = this.xpath.getString(this.getRequestBody(), VERSION_PARAM_XPATH);
        if (version != null && version.length() > 0) {
            if (!version.equals(ConfigurationKeys.CSW_VERSION_2_0_2)) {
                StringBuffer errorMsg = new StringBuffer();
                errorMsg.append("Parameter 'version' has an unsupported value.\n");
                errorMsg.append("Supported values: 2.0.2\n");
                throw new CSWInvalidParameterValueException(errorMsg.toString(), "version");
            }
        }

    }

    @Override
    public List<Operation> getSupportedOperations(Type type) {
        if (type == Type.CSWT) {
            return SUPPORTED_OPERATIONS_CSWT;
        } else {
            return SUPPORTED_OPERATIONS;
        }
    }

    @Override
    public Operation getOperation() throws CSWOperationNotSupportedException {
        this.checkInitialized();

        if (this.operation == null) {
            String operationName = this.getRequestBody().getLocalName();
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
            for (int i = 0; i < length; i++) {
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
    public CSWQuery getQuery() throws CSWException {
        this.checkInitialized();

        // NOTE: getting enum values may throw an IllegalArgumentException,
        // which is ok
        return this.getQuery(this.getRequestBody());

    }

    /**
     * Get a CSWQuery from a request node.
     *
     * @param requestNode
     * @return CSWQuery
     * @throws CSWException
     */
    public CSWQuery getQuery(Node requestNode) throws CSWException {

        this.requestBody = requestNode;

        if (this.query == null) {
            this.query = new GenericQuery();
            try {
                // only required for GetRecordById, GetRecords operations
                Operation operation = this.getOperation();
                if (operation == Operation.GET_RECORD_BY_ID) {
                    // extract the id
                    String id = this.xpath.getString(requestNode, "/csw:GetRecordById/csw:Id");
                    if (id != null) {
                        this.query.setId(id);
                    }
                    // extract the element set name
                    String elementSetNameStr = this.xpath.getString(requestNode,
                            "/csw:GetRecordById/csw:ElementSetName");
                    if (elementSetNameStr != null) {
                        this.query.setElementSetName(ElementSetName.valueOf(elementSetNameStr.toUpperCase()));
                    }
                    // extract the output schema
                    String schemaUri = this.xpath.getString(requestNode, "/csw:GetRecordById/@outputSchema");
                    if (schemaUri != null) {
                        Namespace outputSchema = validateOutputSchema(schemaUri);
                        this.query.setOutputSchema(outputSchema);
                    }
                } else if (operation == Operation.GET_RECORDS) {

                    // ADD request parameter to filter Query
                    // extract the filter constraint
                    if (this.getRequest() != null) {
                        Node query = this.xpath.getNode(requestNode, "/csw:GetRecords/csw:Query");
                        String[] queryConstraints = ApplicationProperties.get(
                                ConfigurationKeys.QUERY_PARAMETER_2_CONSTRAINTS, "").split(",");
                        for (String queryConstraint : queryConstraints) {
                            if (queryConstraint.trim().length() > 0) {
                                String param = this.getRequest().getParameter(queryConstraint);
                                if (param != null && param.length() >= 0) {
                                    OGCFilterTools.addPropertyIsEqual(query, queryConstraint, param);
                                }

                            }
                        }
                    }

                    // extract the filter constraint
                    Node filter = this.xpath
                            .getNode(requestNode, "/csw:GetRecords/csw:Query/csw:Constraint/ogc:Filter");

                    if (filter != null) {
                        this.query.setConstraint(extractFromDocument(filter));
                    }
                    // extract the constraint language version
                    String filterVersion = this.xpath.getString(requestNode,
                            "/csw:GetRecords/csw:Query/csw:Constraint/@version");
                    if (filterVersion != null) {
                        this.query.setConstraintLanguageVersion(filterVersion);
                    }
                    // extract the element set name
                    String elementSetNameStr = this.xpath.getString(requestNode,
                            "/csw:GetRecords/csw:Query/csw:ElementSetName");
                    if (elementSetNameStr != null) {
                        this.query.setElementSetName(ElementSetName.valueOf(elementSetNameStr.toUpperCase()));
                    }
                    // extract the typeNames
                    String typeNamesStr = this.xpath.getString(requestNode, "/csw:GetRecords/csw:Query/@typeNames");
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
                    // extract the result type
                    String resultTypeStr = this.xpath.getString(requestNode, "/csw:GetRecords/@resultType");
                    if (resultTypeStr != null) {
                        this.query.setResultType(ResultType.valueOf(resultTypeStr.toUpperCase()));
                    }
                    // extract the outputSchema
                    String schemaUri = this.xpath.getString(requestNode, "/csw:GetRecords/@outputSchema");
                    if (schemaUri != null ) {
                        Namespace outputSchema = validateOutputSchema(schemaUri);
                        this.query.setOutputSchema(outputSchema);
                    }

                    // extract the maxRecords
                    Integer maxRecords = this.xpath.getInt(requestNode, "/csw:GetRecords/@maxRecords");
                    if (maxRecords == null) {
                        maxRecords = Integer.MAX_VALUE;
                    }
                    maxRecords = Math.min(maxRecords, ApplicationProperties.getInteger(
                            ConfigurationKeys.MAX_RETURNED_HITS, Integer.MAX_VALUE));
                    if (maxRecords < 0) {
                        StringBuffer errorMsg = new StringBuffer();
                        errorMsg.append("Parameter 'maxRecords' has an unsupported value.\n");
                        errorMsg.append("Supported values: positive integer\n");
                        throw new CSWInvalidParameterValueException(errorMsg.toString(), "maxRecords");
                    }

                    this.query.setMaxRecords(maxRecords);

                    // extract the startPosition
                    Integer startPosition = this.xpath.getInt(requestNode, "/csw:GetRecords/@startPosition");
                    if (startPosition == null) {
                        startPosition = 1;
                    }
                    if (startPosition <= 0) {
                        StringBuffer errorMsg = new StringBuffer();
                        errorMsg.append("Parameter 'startPosition' has an unsupported value.\n");
                        errorMsg.append("Supported values: positive integer\n");
                        throw new CSWInvalidParameterValueException(errorMsg.toString(), "startPosition");
                    }
                    this.query.setStartPosition(startPosition);

                    // extract the sort part
                    Node sort = this.xpath.getNode(requestNode, "/csw:GetRecords/csw:Query/ogc:SortBy");

                    if (sort != null) {
                        this.query.setSortBy(extractFromDocument(sort));
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
        if (this.getRequestBody() == null) {
            throw new RuntimeException("No request document found. Maybe initialize() was not called.");
        }
    }

    /**
     * Get the request body
     *
     * @return Node
     */
    public Node getRequestBody() {
        return this.requestBody;
    }

    /**
     * Set the request body
     *
     * @param requestBody The requestBody node to set
     */
    protected void setRequestBody(Node requestBody) {
        this.requestBody = requestBody;
    }

    /**
     * set the output schema to default GMD if a value is passed
     * that is part of the namespace but is not GMD or OGC
     * @param schemaUri
     * @return
     */
    private Namespace validateOutputSchema(String schemaUri){
        Namespace queryOutputSchema = Namespace.getByUri(schemaUri);
        if(queryOutputSchema != Namespace.GMD && queryOutputSchema != Namespace.CSW_2_0_2){
            queryOutputSchema = Namespace.GMD;
            }
        return queryOutputSchema;
    }
}
