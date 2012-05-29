/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.harvest.ibus;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.ibus.client.BusClient;
import de.ingrid.ibus.client.BusClientFactory;
import de.ingrid.interfaces.csw.config.model.RequestDefinition;
import de.ingrid.interfaces.csw.harvest.impl.AbstractHarvester;
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

    public static final String PLUGDESCRIPTION = "PLUGDESCRIPTION";

    public static final String PLUGDESCRIPTION_CACHE_NAME = "plugDescriptionCache";

    public static final int MAX_IBUS_REQUESTS_ATTEMPTS = 3;

    public static final int WAIT_BETWEEN_IBUS_REQUESTS_ATTEMPTS = 3000;

    private Cache plugDescriptionCache;

    public enum IBusClosableLock {

        INSTANCE;

        private String lockToken = null;

        public void lock(String token) {
            if (lockToken == null) {
                lockToken = token;
            }
        }

        public void unlock() {
            lockToken = null;
        }

        public boolean isLockedBy(String token) {
            return lockToken.equals(token);
        }

    }

    final protected static Log log = LogFactory.getLog(IBusHarvester.class);

    /**
     * The path to the communication XML file.
     */
    private String communicationXml;

    /**
     * The list of request definitions for harvesting.
     */
    private List<RequestDefinition> requestDefinitions;

    public IBusHarvester() {
        super();
        CacheManager singletonManager = CacheManager.create();
        Cache memoryOnlyCache = null;
        if (!singletonManager.cacheExists(PLUGDESCRIPTION_CACHE_NAME)) {
            memoryOnlyCache = new Cache(PLUGDESCRIPTION_CACHE_NAME, 10, false, false, 60, 60);
            singletonManager.addCache(memoryOnlyCache);
        }
        plugDescriptionCache = singletonManager.getCache(PLUGDESCRIPTION_CACHE_NAME);
    }

    /**
     * Set the path to the communication XML file.
     * 
     * @param communicationXml
     */
    public void setCommunicationXml(String communicationXml) {
        this.communicationXml = communicationXml;
    }

    /**
     * Set the list of request definitions for harvesting.
     * 
     * @param requestDefinitions
     */
    public void setRequestDefinitions(List<RequestDefinition> requestDefinitions) {
        this.requestDefinitions = requestDefinitions;
    }

    @Override
    public List<Serializable> fetchRecords(Date lastExecutionDate) throws Exception {

        if (this.requestDefinitions == null || this.requestDefinitions.size() == 0) {
            throw new RuntimeException(
                    "IBusHarvesterConfiguration is not configured properly: requestDefinitions not set or empty.");
        }

        // this is responsible for adding partner, provoder and proxy id to the
        // idf record
        this.cache.setProcessor(new DefaultIdfRecordPreProcessor());

        // record ids
        List<Serializable> allCacheIds = new ArrayList<Serializable>();

        // setup the IBus client
        File file = new File(this.communicationXml);
        BusClient client = null;
        try {
            client = BusClientFactory.createBusClient(file);
            // lock iBus client so it is not closed by accident
            IBusClosableLock.INSTANCE.lock(this.getClass().getName());
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

            // process all request definitions
            for (RequestDefinition request : this.requestDefinitions) {

                int currentPage = 0;
                int startHit = 0;
                int pageSize = request.getRecordsPerCall();
                int pause = request.getPause();
                int timeout = request.getTimeout();
                String queryStr = request.getQueryString();

                log.info("Running harvesting request: [" + request.toString() + "]");

                IngridQuery query = QueryStringParser.parse(queryStr);

                // first request
                List<Serializable> cacheIds = this.makeRequest(bus, query, pageSize, currentPage, startHit, timeout);

                // do not use null elements. null elements can occur in case of
                // an exception in the cacheRecords method.
                for (Serializable element : cacheIds) {
                    if (element != null) {
                        allCacheIds.add(element);
                    }
                }

                // continue fetching as long as we get a full page
                while (cacheIds.size() == request.getRecordsPerCall()) {
                    Thread.sleep(pause);
                    startHit = ++currentPage * pageSize;

                    cacheIds = this.makeRequest(bus, query, pageSize, currentPage, startHit, timeout);
                    // do not use null elements. null elements can occur in case of
                    // an exception in the cacheRecords method.
                    for (Serializable element : cacheIds) {
                        if (element != null) {
                            allCacheIds.add(element);
                        }
                    }
                }
            }
        } finally {
            if (client != null && IBusClosableLock.INSTANCE.isLockedBy(this.getClass().getName())) {
                client.shutdown();
                // unlock iBus client for close
                IBusClosableLock.INSTANCE.unlock();
            }
        }

        return allCacheIds;
    }

    private List<Serializable> makeRequest(IBus bus, IngridQuery query, int pageSize, int currentPage, int startHit,
            int timeout) throws Exception {
        // TODO make sure that document ids in hits are the same as in the
        // finally fetched records
        IngridHits hits = null;
        int requestAttempt = 0;
        while (hits == null && (requestAttempt++ <= MAX_IBUS_REQUESTS_ATTEMPTS)) {
            try {
                hits = bus.search(query, pageSize, currentPage, startHit, timeout);
                if (startHit == 0) {
                    log.info("Fetching " + hits.length() + " records.");
                }
            } catch (Exception e) {
                log.error("Error querying ibus with communication setting in '" + this.communicationXml
                        + "' in attempt " + requestAttempt + "  with query: " + query, e);
                log.error("Wait for " + WAIT_BETWEEN_IBUS_REQUESTS_ATTEMPTS + " ms.");
                Thread.sleep(WAIT_BETWEEN_IBUS_REQUESTS_ATTEMPTS);
            }
        }
        if (hits == null) {
            throw new Exception("Error querying ibus '" + bus + "' after " + MAX_IBUS_REQUESTS_ATTEMPTS
                    + " attempts with query:" + query);
        }

        List<Serializable> cacheIds = this.cacheRecords(hits, bus);
        int numHits = hits.getHits().length;
        if (log.isInfoEnabled()) {
            int endHit = startHit + numHits > 0 ? startHit + numHits - 1 : 0;
            log.info("Fetched records " + startHit + " to " + endHit + " of " + hits.length());
        }
        return cacheIds;
    }

    /**
     * Fetch and cache the records referenced in the given hits instance.
     * 
     * @param hits
     * @param bus
     * @return List<Serializable>
     * @throws Exception
     */
    private List<Serializable> cacheRecords(IngridHits hits, IBus bus) throws Exception {
        List<Serializable> cacheIds = new ArrayList<Serializable>();
        for (IngridHit hit : hits.getHits()) {
            int requestAttempt = 0;
            Record record = null;
            while (record == null && (requestAttempt++ <= MAX_IBUS_REQUESTS_ATTEMPTS)) {
                try {
                    record = bus.getRecord(hit);
                } catch (Throwable t) {
                    log.warn("Error getting record from ibus with communication setting in '" + this.communicationXml
                            + "' in attempt " + requestAttempt + "  with index record: " + hit.getDocumentId(), t);
                    log.info("Wait for " + WAIT_BETWEEN_IBUS_REQUESTS_ATTEMPTS + " ms.");
                    Thread.sleep(WAIT_BETWEEN_IBUS_REQUESTS_ATTEMPTS);
                }
            }
            if (record == null) {
                log.error("Skip record from ibus with communication setting in '" + this.communicationXml
                        + "'from iPlug '" + hit.getPlugId() + "' in attempt " + requestAttempt
                        + "  with index record: " + hit.getDocumentId());
                cacheIds.add(null);
                continue;
            }

            PlugDescription pd = null;
            Element element;
            if ((element = plugDescriptionCache.get(hit.getPlugId())) != null) {
                pd = (PlugDescription) element.getValue();
            } else {
                requestAttempt = 0;
                while (pd == null && (requestAttempt++ <= MAX_IBUS_REQUESTS_ATTEMPTS)) {
                    try {
                        pd = bus.getIPlug(hit.getPlugId());
                    } catch (Throwable t) {
                        log.warn("Error getting plugdescription from ibus with communication setting in '"
                                + this.communicationXml + "' in attempt " + requestAttempt + "  for plugid: "
                                + hit.getPlugId(), t);
                        log.info("Wait for " + WAIT_BETWEEN_IBUS_REQUESTS_ATTEMPTS + " ms.");
                        Thread.sleep(WAIT_BETWEEN_IBUS_REQUESTS_ATTEMPTS);
                    }
                }
                if (pd != null) {
                    plugDescriptionCache.put(new Element(hit.getPlugId(), pd));
                }
            }

            if (pd == null) {
                log
                        .error("Skip getting plugdescription from ibus with communication setting in '"
                                + this.communicationXml + "' in attempt " + requestAttempt + "  for plugid: "
                                + hit.getPlugId());
                log.error("This results in incomplete ingrid specific data in the record.");
            } else {
                record.put(PLUGDESCRIPTION, pd);
            }

            Serializable cacheId = null;
            try {
                cacheId = this.cache.put(record);
                cacheIds.add(cacheId);
                if (log.isDebugEnabled()) {
                    log.debug("Fetched record " + hit.getDocumentId() + ". Cache id: " + cacheId);
                }
            } catch (IOException e) {
                log.warn("Error putting record " + hit.getDocumentId() + " to cache. Does the iPlug '"
                        + hit.getPlugId() + "' deliver IDF records?");
            } catch (Exception e) {
                log.error("Error putting record " + hit.getDocumentId() + " from iPlug '" + hit.getPlugId()
                        + "'to cache.", e);
            }
        }
        return cacheIds;
    }
}
