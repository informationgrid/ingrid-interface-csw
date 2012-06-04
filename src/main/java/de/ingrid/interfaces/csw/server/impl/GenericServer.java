/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.server.impl;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;

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

import de.ingrid.interfaces.csw.config.ApplicationProperties;
import de.ingrid.interfaces.csw.domain.CSWRecord;
import de.ingrid.interfaces.csw.domain.constants.ConfigurationKeys;
import de.ingrid.interfaces.csw.domain.constants.Operation;
import de.ingrid.interfaces.csw.domain.exceptions.CSWException;
import de.ingrid.interfaces.csw.domain.exceptions.CSWOperationNotSupportedException;
import de.ingrid.interfaces.csw.domain.query.CSWQuery;
import de.ingrid.interfaces.csw.domain.request.DescribeRecordRequest;
import de.ingrid.interfaces.csw.domain.request.GetCapabilitiesRequest;
import de.ingrid.interfaces.csw.domain.request.GetDomainRequest;
import de.ingrid.interfaces.csw.domain.request.GetRecordByIdRequest;
import de.ingrid.interfaces.csw.domain.request.GetRecordsRequest;
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

    /** A cache for static documents returned for special requests **/
    protected Map<String, Document> documentCache = new Hashtable<String, Document>();

    /** Tool for evaluating xpath **/
    private XPathUtils xpath = new XPathUtils(new Csw202NamespaceContext());

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
            Document doc = domImpl.createDocument("http://www.opengis.net/cat/csw/2.0.2", "csw:GetRecordsResponse",
                    null);
            Element searchResults = doc.createElementNS("http://www.opengis.net/cat/csw/2.0.2", "csw:SearchResults");
            searchResults.setAttribute("elementSet", request.getQuery().getElementSetName().name());
            searchResults.setAttribute("numberOfRecordsMatched", String.valueOf(result.getTotalHits()));
            searchResults.setAttribute("numberOfRecordsReturned", String.valueOf((result.getResults() == null) ? 0
                    : result.getResults().size()));
            doc.getDocumentElement().appendChild(searchResults);

            if (result.getResults() != null) {
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
            Document doc = domImpl.createDocument("http://www.opengis.net/cat/csw/2.0.2", "csw:GetRecordByIdResponse",
                    null);
            Element rootElement = doc.getDocumentElement();
            for (CSWRecord record : result.getResults()) {
                Node recordNode = record.getDocument().getFirstChild();
                doc.adoptNode(recordNode);
                rootElement.appendChild(recordNode);
            }
            return doc;
        } catch (Exception ex) {
            log.error("An error occured processing GetRecordByIdRequest", ex);
            throw new CSWException("An error occured processing GetRecordByIdRequest");
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
        String cacheKey = key;
        if (variant != null) {
            cacheKey = cacheKey + variant;
        }

        if (!this.documentCache.containsKey(cacheKey)
                || !ApplicationProperties.getBoolean(ConfigurationKeys.CACHE_ENABLE, false)) {

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
            try {
                URL resource = this.getClass().getClassLoader().getResource(filenameVariant);
                if (resource == null) {
                    log.warn("Document '" + filenameVariant + "' could not be found in class path.");
                    resource = this.getClass().getClassLoader().getResource(filename);
                }
                String path = resource.getPath().replaceAll("%20", " ");
                File file = new File(path);
                String content = new Scanner(file).useDelimiter("\\A").next();
                Document doc = StringUtils.stringToDocument(content);

                // cache the document
                this.documentCache.put(cacheKey, doc);
            } catch (Exception e) {
                throw new RuntimeException("Error reading document configured in configuration key '" + cacheKey
                        + "': " + filename + ", " + variant, e);
            }
        }
        return this.documentCache.get(cacheKey);
    }
}
