/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.server.impl;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.interfaces.csw.catalog.Manager;
import de.ingrid.interfaces.csw.catalog.action.Action;
import de.ingrid.interfaces.csw.catalog.action.ActionResult;
import de.ingrid.interfaces.csw.config.ApplicationProperties;
import de.ingrid.interfaces.csw.domain.CSWRecord;
import de.ingrid.interfaces.csw.domain.constants.ConfigurationKeys;
import de.ingrid.interfaces.csw.domain.constants.Namespace;
import de.ingrid.interfaces.csw.domain.constants.Operation;
import de.ingrid.interfaces.csw.domain.constants.ResultType;
import de.ingrid.interfaces.csw.domain.exceptions.CSWException;
import de.ingrid.interfaces.csw.domain.exceptions.CSWOperationNotSupportedException;
import de.ingrid.interfaces.csw.domain.query.CSWQuery;
import de.ingrid.interfaces.csw.domain.request.DescribeRecordRequest;
import de.ingrid.interfaces.csw.domain.request.GetCapabilitiesRequest;
import de.ingrid.interfaces.csw.domain.request.GetDomainRequest;
import de.ingrid.interfaces.csw.domain.request.GetRecordByIdRequest;
import de.ingrid.interfaces.csw.domain.request.GetRecordsRequest;
import de.ingrid.interfaces.csw.domain.request.TransactionRequest;
import de.ingrid.interfaces.csw.domain.transaction.CSWTransaction;
import de.ingrid.interfaces.csw.domain.transaction.CSWTransactionResult;
import de.ingrid.interfaces.csw.search.CSWRecordResults;
import de.ingrid.interfaces.csw.search.Searcher;
import de.ingrid.interfaces.csw.server.CSWServer;
import de.ingrid.interfaces.csw.tools.StringUtils;
import de.ingrid.utils.xml.Csw202NamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

@Service
public class GenericServer implements CSWServer {

    /** The logging object **/
    private static Log log = LogFactory.getLog(GenericServer.class);

    /** Tool for evaluating xpath **/
    private XPathUtils xpath = new XPathUtils(new Csw202NamespaceContext());

    private static final String RESPONSE_NAMESPACE = Namespace.CSW_2_0_2.getQName().getNamespaceURI();

    /**
     * The Searcher instance to use for searching records
     */
    @Autowired
    private Searcher searcher;

    /**
     * Set the Searcher instance
     *
     * @param searcher
     */
    public void setSearcher(Searcher searcher) {
        this.searcher = searcher;
    }

    /**
     * The Manager instance to use for processing transactions
     */
    @Autowired
    private Manager manager;

    /**
     * Set the Manager instance
     *
     * @param manager
     */
    public void setRepository(Manager manager) {
        this.manager = manager;
    }

    @Override
    public Document process(GetCapabilitiesRequest request, String variant) throws CSWException {

        final String documentKey = ConfigurationKeys.CAPABILITIES_DOC;

        Document doc = this.getDocument(documentKey, variant);

        // try to replace the interface URLs
        NodeList nodes = this.xpath.getNodeList(doc, "//ows:Operation/*/ows:HTTP/*/@xlink:href");
        // get host
        String host = ApplicationProperties.get(ConfigurationKeys.SERVER_INTERFACE_HOST, null);
        if (host == null) {
            log.info("The interface host address is not specified, use local hosts address instead.");
            try {
                InetAddress addr = InetAddress.getLocalHost();
                host = addr.getHostAddress();
            } catch (UnknownHostException e) {
                log.error("Unable to get interface host address.", e);
                throw new RuntimeException("Unable to get interface host address.", e);
            }
        }
        // get the port (defaults to 80)
        String port = ApplicationProperties.get(ConfigurationKeys.SERVER_INTERFACE_PORT, "80");
        // get the port (defaults to 80)
        String path = ApplicationProperties.get(ConfigurationKeys.SERVER_INTERFACE_PATH, "80");
        // replace interface host and port
        for (int idx = 0; idx < nodes.getLength(); idx++) {
            String s = nodes.item(idx).getTextContent();
            s = s.replaceAll(ConfigurationKeys.VARIABLE_INTERFACE_HOST, host);
            s = s.replaceAll(ConfigurationKeys.VARIABLE_INTERFACE_PORT, port);
            s = s.replaceAll(ConfigurationKeys.VARIABLE_INTERFACE_PATH, path);
            nodes.item(idx).setTextContent(s);
        }

        return doc;
    }

    @Override
    public Document process(DescribeRecordRequest request) throws CSWException {

        final String documentKey = ConfigurationKeys.RECORDDESC_DOC;

        // return the cached document
        return this.getDocument(documentKey);
    }

    @Override
    public Document process(GetDomainRequest request) throws CSWException {
        throw new CSWOperationNotSupportedException("The operation 'GetDomain' is not implemented",
                Operation.GET_DOMAIN.toString());
    }

    @Override
    public Document process(GetRecordsRequest request) throws CSWException {
        try {
            CSWQuery query = request.getQuery();
            CSWRecordResults result = this.searcher.search(query);

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            DOMImplementation domImpl = docBuilder.getDOMImplementation();
            Document doc = domImpl.createDocument(RESPONSE_NAMESPACE, "csw:GetRecordsResponse",
                    null);

            Element searchStatus = doc.createElementNS(RESPONSE_NAMESPACE, "csw:SearchStatus");

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("ThreeLetterISO8601TimeZone"));
            searchStatus.setAttribute("timestamp", df.format(new Date()));
            doc.getDocumentElement().appendChild(searchStatus);

            Element searchResults = doc.createElementNS(RESPONSE_NAMESPACE, "csw:SearchResults");
            searchResults.setAttribute("elementSet", request.getQuery().getElementSetName().name().toLowerCase());
            searchResults.setAttribute("numberOfRecordsMatched", String.valueOf(result.getTotalHits()));
            searchResults.setAttribute("numberOfRecordsReturned", String.valueOf((result.getResults() == null) ? 0
                    : result.getResults().size()));
            int nextRecord = query.getStartPosition() + ((result.getResults() == null) ? 0 : result.getResults().size());
            if (nextRecord > result.getTotalHits()) {
                nextRecord = 0;
            }
            searchResults.setAttribute("nextRecord", String.valueOf(nextRecord));
            doc.getDocumentElement().appendChild(searchResults);

            if (query.getResultType() == ResultType.RESULTS && result.getResults() != null) {
                for (CSWRecord record : result.getResults()) {
                    Node recordNode = record.getDocument().getFirstChild();
                    doc.adoptNode(recordNode);
                    searchResults.appendChild(recordNode);
                }
            }
            return doc;
        } catch (CSWException ex) {
            log.error("An error occured processing GetRecordsRequest", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("An error occured processing GetRecordsRequest", ex);
            throw new CSWException("An error occured processing GetRecordsRequest");
        }
    }

    @Override
    public Document process(GetRecordByIdRequest request) throws CSWException {
        try {
            CSWQuery query = request.getQuery();
            CSWRecordResults result = this.searcher.search(query);

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setNamespaceAware(true);
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            DOMImplementation domImpl = docBuilder.getDOMImplementation();
            Document doc = domImpl.createDocument(RESPONSE_NAMESPACE, "csw:GetRecordByIdResponse",
                    null);
            if (result.getResults() != null) {
                Element rootElement = doc.getDocumentElement();
                for (CSWRecord record : result.getResults()) {
                    Node recordNode = record.getDocument().getFirstChild();
                    doc.adoptNode(recordNode);
                    rootElement.appendChild(recordNode);
                }
            }
            return doc;
        } catch (CSWException ex) {
            log.error("An error occured processing GetRecordByIdRequest", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("An error occured processing GetRecordByIdRequest", ex);
            throw new CSWException("An error occured processing GetRecordByIdRequest");
        }
    }

    @Override
    public Document process(TransactionRequest request) throws CSWException {
        try {
            CSWTransaction transaction = request.getTransaction();
            CSWTransactionResult result = manager.process(transaction);

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setNamespaceAware(true);
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            DOMImplementation domImpl = docBuilder.getDOMImplementation();
            Document doc = domImpl.createDocument(RESPONSE_NAMESPACE, "csw:TransactionResponse",
                    null);

            // create summary
            Element summary = doc.createElementNS(RESPONSE_NAMESPACE, "csw:TransactionSummary");
            summary.setAttribute("requestId", result.getRequestId());
            doc.getDocumentElement().appendChild(summary);

            int inserts = result.getNumberOfInserts();
            if (inserts > 0) {
                summary.appendChild(doc.createElementNS(RESPONSE_NAMESPACE, "totalInserted"))
                    .appendChild(doc.createTextNode(String.valueOf(inserts)));
            }
            int updates = result.getNumberOfUpdates();
            if (updates > 0) {
                summary.appendChild(doc.createElementNS(RESPONSE_NAMESPACE, "totalUpdated"))
                    .appendChild(doc.createTextNode(String.valueOf(updates)));
            }
            int deletes = result.getNumberOfDeletes();
            if (deletes > 0) {
                summary.appendChild(doc.createElementNS(RESPONSE_NAMESPACE, "totalDeleted"))
                    .appendChild(doc.createTextNode(String.valueOf(deletes)));
            }

            // add insert results
            if (inserts > 0) {
                Element insertResult = doc.createElementNS(RESPONSE_NAMESPACE, "csw:InsertResult");
                doc.getDocumentElement().appendChild(insertResult);
                for (ActionResult curResult : result.getInsertResults()) {
                    List<CSWRecord> records = curResult.getRecords();
                    if (records.size() > 0) {
                        Node recordNode = records.get(0).getDocument().getFirstChild();
                        Action action = curResult.getAction();
                        String handle = action.getHandle();
                        if (handle != null && recordNode instanceof Element) {
                            ((Element)recordNode).setAttribute("handle", handle);
                        }
                        doc.adoptNode(recordNode);
                        insertResult.appendChild(recordNode);
                    }
                }
            }
            return doc;
        } catch (CSWException ex) {
            log.error("An error occured processing TransactionRequest", ex);
            throw ex;
        } catch (Exception ex) {
            log.error("An error occured processing TransactionRequest", ex);
            throw new CSWException("An error occured processing TransactionRequest", "TransactionUnspecifiedError", "");
        }
    }

    @Override
    public void destroy() {
        try {
            this.searcher.stop();
        } catch (Exception e) {
            log.error("Error closing searcher.", e);
        }
    }

    /**
     * Get a Document from the configured resources in beans.xml
     *
     * @param key
     *            One of the keys of the map defined in
     *            ConfigurationKeys.CSW_RESOURCES
     * @return The Document instance
     */
    protected Document getDocument(String key) {
        return this.getDocument(key, null);
    }

    /**
     * Get a Document from a class path location. The actual name of the file is
     * retrieved from the config.properties file.
     *
     * With variant a specific variant (like a localization) can be retrieved.
     * The file name is extended by the variant in the form
     * [name]_[variant].[extension].
     *
     * If the variant could not be retrieved, the base file is returned as a
     * fall back.
     *
     * The content is cached. The cache can be controlled by the
     * config.properties entry 'cache.enable'.
     *
     * @param key
     *            One of the keys config.properties, defining the actual
     *            filename to be retrieved.
     * @param variant
     *            The variant of the file.
     * @return The Document instance
     */
    protected Document getDocument(String key, String variant) {

        // fetch the document from the file system if it is not cached
        String filename = ApplicationProperties.getMandatory(key);
        String filenameVariant = filename;
        if (variant != null && variant.length() > 0) {
            if (filename.contains(FilenameUtils.EXTENSION_SEPARATOR_STR)) {
                filenameVariant = FilenameUtils.getBaseName(filename) + "_" + variant
                        + FilenameUtils.EXTENSION_SEPARATOR_STR + FilenameUtils.getExtension(filename);
            } else {
                filenameVariant = FilenameUtils.getBaseName(filename) + "_" + variant;
            }
        }
        Document doc = null;
        Scanner scanner = null;
        try {
            URL resource = this.getClass().getClassLoader().getResource(filenameVariant);
            if (resource == null) {
                log.warn("Document '" + filenameVariant + "' could not be found in class path.");
                resource = this.getClass().getClassLoader().getResource(filename);
            }
            String path = resource.getPath().replaceAll("%20", " ");
            File file = new File(path);
            scanner = new Scanner(file);
            scanner.useDelimiter("\\A");
            String content = scanner.next();
            scanner.close();
            doc = StringUtils.stringToDocument(content);

        } catch (Exception e) {
            log.error("Error reading document configured in configuration key '" + key + "': " + filename + ", "
                    + variant, e);
            throw new RuntimeException("Error reading document configured in configuration key '" + key + "': "
                    + filename + ", " + variant, e);
        }
        finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        return doc;
    }
}
