/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2026 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.server;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import de.ingrid.interfaces.csw.config.ApplicationProperties;
import de.ingrid.interfaces.csw.domain.constants.ConfigurationKeys;
import de.ingrid.interfaces.csw.domain.constants.Operation;
import de.ingrid.interfaces.csw.domain.constants.RequestType;
import de.ingrid.interfaces.csw.domain.encoding.CSWMessageEncoding;
import de.ingrid.interfaces.csw.domain.encoding.CSWMessageEncoding.Type;
import de.ingrid.interfaces.csw.domain.encoding.impl.KVPEncoding;
import de.ingrid.interfaces.csw.domain.encoding.impl.Soap12Encoding;
import de.ingrid.interfaces.csw.domain.encoding.impl.XMLEncoding;
import de.ingrid.interfaces.csw.domain.exceptions.CSWException;
import de.ingrid.interfaces.csw.domain.exceptions.CSWOperationNotSupportedException;
import de.ingrid.interfaces.csw.domain.request.CSWRequest;
import de.ingrid.interfaces.csw.domain.request.DescribeRecordRequest;
import de.ingrid.interfaces.csw.domain.request.GetCapabilitiesRequest;
import de.ingrid.interfaces.csw.domain.request.GetDomainRequest;
import de.ingrid.interfaces.csw.domain.request.GetRecordByIdRequest;
import de.ingrid.interfaces.csw.domain.request.GetRecordsRequest;
import de.ingrid.interfaces.csw.domain.request.impl.DescribeRecordRequestImpl;
import de.ingrid.interfaces.csw.domain.request.impl.GetCapabilitiesRequestImpl;
import de.ingrid.interfaces.csw.domain.request.impl.GetDomainRequestImpl;
import de.ingrid.interfaces.csw.domain.request.impl.GetRecordByIdRequestImpl;
import de.ingrid.interfaces.csw.domain.request.impl.GetRecordsRequestImpl;
import de.ingrid.interfaces.csw.tools.StringUtils;

/**
 * ServerFacade processes all server requests. It instantiates the appropriate
 * CSWMessageEncoding and CSWRequest instances and acts as mediator between the
 * servlet and the CSWServer.
 * 
 * @author ingo herwig <ingo@wemove.com>
 * 
 */
@Service
public class ServerFacade {

    private static Log log = LogFactory.getLog(ServerFacade.class);

    /**
     * The csw server implementation
     */
    @Autowired
    private CSWServer cswServerImpl;

    
    public ServerFacade() {
        super();
    }

    /**
     * Set the csw server implementation
     * 
     * @param cswServerImpl
     */
    public void setCswServerImpl(CSWServer cswServerImpl) {
        this.cswServerImpl = cswServerImpl;
    }

    /**
     * Handle a GET Request
     * 
     * @param request
     * @param response
     * @throws CSWException
     */
    public void handleGetRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.handleRequest(RequestType.GET, request, response);
    }

    /**
     * Handle a POST Request
     * 
     * @param request
     * @param response
     * @throws CSWException
     */
    public void handlePostRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.handleRequest(RequestType.POST, request, response);
    }

    /**
     * Handle a SOAP Request
     * 
     * @param request
     * @param response
     * @throws CSWException
     */
    public void handleSoapRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.handleRequest(RequestType.SOAP, request, response);
    }
    
    /**
     * Free all resources.
     */
    public void destroy() {
    	cswServerImpl.destroy();
    }


    /**
     * Generic request method
     * 
     * @param type
     * @param request
     * @throws CSWException
     */
    protected void handleRequest(RequestType type, HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        CSWMessageEncoding encodingImpl = null;
        try {
            // initialize the CSWMessageEncoding
            encodingImpl = this.getMessageEncodingInstance(type);
            encodingImpl.initialize(request, response);
            if (log.isDebugEnabled()) {
                log.debug("Handle " + type + " request");
            }

            // validate the request message non-operation-specific
            encodingImpl.validateRequest();

            // check if the operation is supported
            Operation operation = encodingImpl.getOperation();
            List<Operation> supportedOperations = encodingImpl.getSupportedOperations(Type.CSW);
            if (!supportedOperations.contains(operation)) {
                StringBuffer errorMsg = new StringBuffer();
                errorMsg.append("The operation '" + operation + "' is not supported in a " + type + " request.\n");
                errorMsg.append("Supported values:\n");
                errorMsg.append(supportedOperations.toString() + "\n");
                throw new CSWOperationNotSupportedException(errorMsg.toString(), String.valueOf(operation));
            }
            if (log.isDebugEnabled()) {
                log.debug("Operation: " + encodingImpl.getOperation());
            }

            // initialize the CSWRequest instance
            CSWRequest requestImpl = this.getRequestInstance(operation);
            requestImpl.initialize(encodingImpl);

            // validate the request message operation-specific
            requestImpl.validate();

            // check the CSWServer instance
            if (this.cswServerImpl == null) {
                throw new RuntimeException("ServerFacade is not configured properly: cswServerImpl is not set.");
            }
            

            // perform the requested operation
            Document result = null;
            if (operation == Operation.GET_CAPABILITIES) {
                String variant = request.getParameter(ApplicationProperties.get(ConfigurationKeys.QUERY_PARAMETER_2_CAPABILITIES_VARIANT, "")); 
                result = this.cswServerImpl.process((GetCapabilitiesRequest) requestImpl, variant);
            } else if (operation == Operation.DESCRIBE_RECORD) {
                result = this.cswServerImpl.process((DescribeRecordRequest) requestImpl);
            } else if (operation == Operation.GET_DOMAIN) {
                result = this.cswServerImpl.process((GetDomainRequest) requestImpl);
            } else if (operation == Operation.GET_RECORDS) {
                result = this.cswServerImpl.process((GetRecordsRequest) requestImpl);
            } else if (operation == Operation.GET_RECORD_BY_ID) {
                result = this.cswServerImpl.process((GetRecordByIdRequest) requestImpl);
            }
            if (log.isDebugEnabled()) {
                log.debug("Result: " + StringUtils.nodeToString(result));
            }

            // serialize the response
            encodingImpl.writeResponse(result);

        } catch (Exception e) {
            try {
                log.warn(e.getMessage(), e);
                if (encodingImpl != null) {
                    encodingImpl.reportError(e);
                }
            } catch (IOException ioe) {
                log.error("Unable to send error message to client.", ioe);
            }
        }
    }

    /**
     * Get the CSWMesageEncoding implementation for a given request type from
     * the server configuration
     * 
     * @param type
     *            The request type
     * @return The CSWMesageEncoding instance
     */
    private CSWMessageEncoding getMessageEncodingInstance(RequestType type) {

        CSWMessageEncoding encoding = null;
        
        if (type.equals(RequestType.GET)) {
            encoding = new KVPEncoding();
        } else if (type.equals(RequestType.POST)) {
            encoding = new XMLEncoding();
        } else if (type.equals(RequestType.SOAP)) {
            encoding = new Soap12Encoding();
        } else {
            log.error("Unknown encoding type requested: " + type);
            throw new RuntimeException("Unknown encoding type requested: " + type);
        }

        return encoding;
    }

    /**
     * Get the CSWRequest implementation for a given request type from the
     * server configuration
     * 
     * @param operation
     *            The operation
     * @return The CSWRequest instance
     */
    private CSWRequest getRequestInstance(Operation operation) {
        CSWRequest request = null;
        
        if (operation.equals(Operation.GET_CAPABILITIES)) {
            request = new GetCapabilitiesRequestImpl();
        } else if (operation.equals(Operation.DESCRIBE_RECORD)) {
            request = new DescribeRecordRequestImpl();
        } else if (operation.equals(Operation.GET_DOMAIN)) {
            request = new GetDomainRequestImpl();
        } else if (operation.equals(Operation.GET_RECORDS)) {
            request = new GetRecordsRequestImpl();
        } else if (operation.equals(Operation.GET_RECORD_BY_ID)) {
            request = new GetRecordByIdRequestImpl();
        } else {
            log.error("No request implementation found for operation: " + operation);
            throw new RuntimeException("No request implementation found for operation: " + operation);
        }
        
        return request;
    }
}
