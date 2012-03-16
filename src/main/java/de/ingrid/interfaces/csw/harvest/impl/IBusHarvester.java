/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.harvest.impl;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.ibus.client.BusClient;
import de.ingrid.ibus.client.BusClientFactory;
import de.ingrid.interfaces.csw.config.model.RequestDefinition;
import de.ingrid.utils.IBus;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

/**
 * A Harvester that fetches records from an IBus.
 * 
 * @author ingo@wemove.com
 */
public class IBusHarvester extends AbstractHarvester {

	final protected static Log log = LogFactory.getLog(IBusHarvester.class);

	/**
	 * The path to the communication XML file.
	 */
	private String communicationXml;

	/**
	 * The list of request definitions for harvesting.
	 */
	private List<RequestDefinition> requestDefinitions;

	/**
	 * Set the path to the communication XML file.
	 * @param communicationXml
	 */
	public void setCommunicationXml(String communicationXml) {
		this.communicationXml = communicationXml;
	}

	/**
	 * Set the list of request definitions for harvesting.
	 * @param requestDefinitions
	 */
	public void setRequestDefinitions(List<RequestDefinition> requestDefinitions) {
		this.requestDefinitions = requestDefinitions;
	}

	@Override
	public List<Serializable> fetchRecords(Date lastExecutionDate) throws Exception {

		if (this.requestDefinitions == null || this.requestDefinitions.size() == 0) {
			throw new RuntimeException("IBusHarvester is not configured properly: requestDefinitions not set or empty.");
		}

		// setup the IBus client
		File file = new File(this.communicationXml);
		BusClient client = BusClientFactory.createBusClient(file);
		if (!client.allConnected()) {
			client.start();
		}
		IBus bus = client.getNonCacheableIBus();
		if (log.isDebugEnabled()) {
			log.debug("Available i-plugs:");
			for (PlugDescription desc : bus.getAllIPlugs()) {
				log.debug(desc.getPlugId());
			}
		}

		// fetch the records
		List<Serializable> allCacheIds = new ArrayList<Serializable>();

		// process all request definitions
		for (RequestDefinition request : this.requestDefinitions) {

			int currentPage = 1;
			int startHit = 0;
			int pageSize = request.getRecordsPerCall();
			int pause = request.getPause();
			int timeout = request.getTimeout();
			String queryStr = request.getQueryString();

			if (log.isDebugEnabled()) {
				log.debug("Running harvesting request: ["+request.toString()+"]");
			}

			IngridQuery query = QueryStringParser.parse(queryStr);

			// first request
			List<Serializable> cacheIds = this.makeRequest(bus, query, pageSize, currentPage, startHit, timeout);
			allCacheIds.addAll(cacheIds);

			// continue fetching as long as we get a full page
			while (cacheIds.size() == request.getRecordsPerCall()) {
				Thread.sleep(pause);
				startHit = ++currentPage*pageSize;

				cacheIds = this.makeRequest(bus, query, pageSize, currentPage, startHit, timeout);
				allCacheIds.addAll(cacheIds);
			}
		}

		return allCacheIds;
	}

	private List<Serializable> makeRequest(IBus bus, IngridQuery query, int pageSize, int currentPage,
			int startHit, int timeout) throws Exception {
		// TODO make sure that document ids in hits are the same as in the finally fetched records
		IngridHits hits = bus.search(query, pageSize, currentPage, startHit, timeout);
		int numHits = hits.getHits().length;
		if (log.isDebugEnabled()) {
			log.debug("Found "+numHits+" hits starting from "+startHit);
		}
		List<Serializable> cacheIds = this.cacheRecords(hits, bus);
		return cacheIds;
	}

	/**
	 * Fetch and cache the records referenced in the given hits instance.
	 * @param hits
	 * @param bus
	 * @return List<Serializable>
	 * @throws Exception
	 */
	private List<Serializable> cacheRecords(IngridHits hits, IBus bus) throws Exception {
		List<Serializable> cacheIds = new ArrayList<Serializable>();
		for (IngridHit hit : hits.getHits()) {
			Record record = bus.getRecord(hit);
			Serializable cacheId = this.cache.put(record);
			if (log.isDebugEnabled()) {
				log.debug("Fetched record ["+record.getId()+"]. Cache id is "+cacheId);
			}
			cacheIds.add(cacheId);
		}
		return cacheIds;
	}
}
