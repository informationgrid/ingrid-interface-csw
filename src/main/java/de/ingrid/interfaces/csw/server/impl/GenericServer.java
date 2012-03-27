/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.server.impl;

import java.io.FileReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import de.ingrid.interfaces.csw.domain.constants.ConfigurationKeys;
import de.ingrid.interfaces.csw.domain.constants.Operation;
import de.ingrid.interfaces.csw.domain.exceptions.CSWException;
import de.ingrid.interfaces.csw.domain.exceptions.CSWOperationNotSupportedException;
import de.ingrid.interfaces.csw.domain.filter.FilterParser;
import de.ingrid.interfaces.csw.domain.query.CSWQuery;
import de.ingrid.interfaces.csw.domain.request.DescribeRecordRequest;
import de.ingrid.interfaces.csw.domain.request.GetCapabilitiesRequest;
import de.ingrid.interfaces.csw.domain.request.GetDomainRequest;
import de.ingrid.interfaces.csw.domain.request.GetRecordByIdRequest;
import de.ingrid.interfaces.csw.domain.request.GetRecordsRequest;
import de.ingrid.interfaces.csw.server.CSWServer;
import de.ingrid.utils.query.IngridQuery;

public class GenericServer implements CSWServer {

	/** The logging object **/
	private static Log log = LogFactory.getLog(GenericServer.class);

	/** A cache for static documents returned for special requests **/
	protected Map<String, Document> documentCache = new Hashtable<String, Document>();

	@Override
	public Document process(GetCapabilitiesRequest request) throws CSWException {

		final String documentKey = ConfigurationKeys.CAPABILITIES_DOC;

		// replace document variables on first call
		if (!this.documentCache.containsKey(documentKey)) {

			Document doc = this.getDocument(documentKey);

			// try to replace the interface URLs
			NodeList nodes = new XPathUtils.getNodeList(doc, "//ows:Operation/*/ows:HTTP/*/@xlink:href");
			// get host
			String host = CSWConfig.getInstance().getString(ConfigurationKeys.SERVER_INTERFACE_HOST, null);
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
			String port = CSWConfig.getInstance().getString(ConfigurationKeys.SERVER_INTERFACE_PORT, "80");
			// replace interface host and port
			for (int idx = 0; idx < nodes.getLength(); idx++) {
				String s = nodes.item(idx).getTextContent();
				s = s.replaceAll(ConfigurationKeys.VARIABLE_INTERFACE_HOST, host);
				s = s.replaceAll(ConfigurationKeys.VARIABLE_INTERFACE_PORT, port);
				nodes.item(idx).setTextContent(s);
			}

			// override the cached document
			this.documentCache.put(documentKey, doc);
		}

		// return the cached document
		return this.getDocument(documentKey);
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
		CSWQuery query = request.getQuery();
		FilterParser filterParser = this.getFilterParserInstance();
		IngridQuery ingridQuery = filterParser.parse(query.getConstraint());
		return null;
	}

	@Override
	public Document process(GetRecordByIdRequest request) throws CSWException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Get a Document from the configured resources in beans.xml
	 * @param key One of the keys of the map defined in ConfigurationKeys.CSW_RESOURCES
	 * @return The Document instance
	 */
	protected Document getDocument(String key) {
		if (!this.documentCache.containsKey(key)) {

			// fetch the document from the file system if it is not cached
			String filename = CSWConfig.getInstance().getStringMandatory(key);
			try {
				Reader reader = new FileReader(this.getClass().getClassLoader()
						.getResource(filename).getPath().replaceAll("%20", " "));
				Document doc = XMLTools.parse(reader);

				// cache the document
				this.documentCache.put(key, doc);
			}
			catch (Exception e) {
				throw new RuntimeException("Error reading document configured in configuration key '"+
						key+"': "+filename, e);
			}
		}
		return this.documentCache.get(key);
	}

	/**
	 * Get the CSWMesageEncoding implementation for a given request type from the server configuration
	 * @param type The request type
	 * @return The CSWMesageEncoding instance
	 */
	private FilterParser getFilterParserInstance() {
		return SimpleSpringBeanFactory.INSTANCE.getBeanMandatory(
				ConfigurationKeys.CSW_FILTERPARSER_IMPLEMENTATION, FilterParser.class);
	}
}
