/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.harvest.ibus;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.ibus.client.BusClient;
import de.ingrid.ibus.client.MultipleBusClientFactory;
import de.ingrid.interfaces.csw.config.ApplicationProperties;
import de.ingrid.interfaces.csw.config.model.RequestDefinition;
import de.ingrid.interfaces.csw.domain.constants.ConfigurationKeys;
import de.ingrid.interfaces.csw.harvest.impl.AbstractHarvester;
import de.ingrid.utils.statusprovider.StatusProvider;
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
            return lockToken.equals( token );
        }

    }

    final protected static Log log = LogFactory.getLog( IBusHarvester.class );

    /**
     * The path to the communication XML file.
     */
    private String communicationXml;

    /**
     * The list of request definitions for harvesting.
     */
    private List<RequestDefinition> requestDefinitions;

    private Map<String, Integer> errorCounts = new HashMap<String, Integer>();
    private Map<String, Long> iPlugTotalResults = new HashMap<String, Long>();

    private Boolean useCachedDocs;

    public IBusHarvester() {
        super();
        CacheManager singletonManager = CacheManager.create();
        Cache memoryOnlyCache = null;
        if (!singletonManager.cacheExists( PLUGDESCRIPTION_CACHE_NAME )) {
            memoryOnlyCache = new Cache( PLUGDESCRIPTION_CACHE_NAME, 10, false, false, 60, 60 );
            singletonManager.addCache( memoryOnlyCache );
        }
        plugDescriptionCache = singletonManager.getCache( PLUGDESCRIPTION_CACHE_NAME );
        useCachedDocs = ApplicationProperties.getBoolean( ConfigurationKeys.CACHE_ENABLE_HARVEST, false );
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
    public void run(Date lastExecutionDate) throws Exception {
        statusProvider.addState( this.getId(), "Harvesting '" + this.getName() + "'... [iPlugs: " + requestDefinitions.size() + "]" );
        super.run( lastExecutionDate );
    }

    @Override
    public List<Serializable> fetchRecords(Date lastExecutionDate) throws Exception {

        if (this.requestDefinitions == null || this.requestDefinitions.size() == 0) {
            throw new RuntimeException( "IBusHarvesterConfiguration is not configured properly: requestDefinitions not set or empty." );
        }

        // this is responsible for adding partner, provoder and proxy id to the
        // idf record
        this.cache.setProcessor( new DefaultIdfRecordPreProcessor() );

        // record ids
        List<Serializable> allCacheIds = new ArrayList<Serializable>();

        // setup the IBus client
        File file = new File( this.communicationXml );
        BusClient client = null;
        try {
            client = MultipleBusClientFactory.getBusClient( file );
            // lock iBus client so it is not closed by accident
            IBusClosableLock.INSTANCE.lock( this.getClass().getName() );
            if (!client.allConnected()) {
                client.start();
            }
            IBus bus = client.getNonCacheableIBus();
            if (log.isDebugEnabled()) {
                log.debug( "Available i-plugs:" );
                for (PlugDescription desc : bus.getAllIPlugs()) {
                    log.debug( desc.getPlugId() );
                }
            }

            // process all request definitions
            String queryStr = null;
            for (RequestDefinition request : this.requestDefinitions) {

                int currentPage = 0;
                int startHit = 0;
                int pause = request.getPause();
                queryStr = request.getQueryString();

                try {

                    log.info( "Running harvesting request: [" + request.toString() + "]" );

                    IngridQuery query = QueryStringParser.parse( queryStr );

                    // first request
                    List<Serializable> cacheIds = this.makeRequest( bus, request, query, currentPage, startHit );

                    // do not use null elements. null elements can occur in case
                    // of
                    // an exception in the cacheRecords method.
                    for (Serializable element : cacheIds) {
                        if (element != null) {
                            allCacheIds.add( element );
                        }
                    }

                    // continue fetching as long as we get a full page
                    while (cacheIds.size() == request.getRecordsPerCall()) {
                        Thread.sleep( pause );
                        startHit = ++currentPage * request.getRecordsPerCall();

                        cacheIds = this.makeRequest( bus, request, query, currentPage, startHit );
                        // do not use null elements. null elements can occur in
                        // case
                        // of
                        // an exception in the cacheRecords method.
                        for (Serializable element : cacheIds) {
                            if (element != null) {
                                allCacheIds.add( element );
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error( "Error harvesting iPlug '" + request.getPlugId() + "'", e );
                    statusProvider.addState( request.getPlugId() + "Error", "Error harvesting iPlug '" + request.getPlugId() + "' (" + e.getMessage() + ")",
                            StatusProvider.Classification.ERROR );
                }
            }
        } catch (Exception e) {
            log.error( "Error in harvester '" + this.getName() + "'", e );
            statusProvider.addState( this.getId() + "Error", "Error executing harvester '" + this.getName() + "' + (" + (e.getMessage() == null ? e.getCause().getMessage() : e.getMessage())  + ").",
                    StatusProvider.Classification.ERROR );
        } finally {
            if (client != null && IBusClosableLock.INSTANCE.isLockedBy( this.getClass().getName() )) {
                client.shutdown();
                // unlock iBus client for close
                IBusClosableLock.INSTANCE.unlock();
            }
        }

        return allCacheIds;
    }

    private List<Serializable> makeRequest(IBus bus, final RequestDefinition request, final IngridQuery query, final int currentPage, final int startHit) throws Exception {
        int pageSize = request.getRecordsPerCall();
        int timeout = request.getTimeout();

        // TODO make sure that document ids in hits are the same as in the
        // finally fetched records
        IngridHits hits = null;
        int requestAttempt = 0;
        int numHits = -1;
        int endHit = -1;
        while (hits == null && (requestAttempt++ <= MAX_IBUS_REQUESTS_ATTEMPTS)) {
            try {
                hits = bus.search( query, pageSize, currentPage, startHit, timeout );
                // store max available hits of iPlug
                if (!iPlugTotalResults.containsKey( request.getPlugId() ) || hits.length() > iPlugTotalResults.get( request.getPlugId() )) {
                    iPlugTotalResults.put( request.getPlugId(), hits.length() );
                }
                numHits = hits.getHits().length;
                endHit = startHit + numHits > 0 ? startHit + numHits - 1 : 0;
                if (startHit == 0) {
                    log.info( "Fetching " + ((hits == null) ? 0 : hits.length()) + " records." );
                    if (hits == null || hits.getHits().length == 0) {
                        statusProvider.addState( request.getPlugId() + "fetch", "Fetch records for iPlug '" + request.getPlugId() + "'... no records found!",
                                StatusProvider.Classification.WARN );
                    }
                }
                // treat empty results as failures if not all records have been fetched
                if (hits.getHits().length == 0 && ((iPlugTotalResults.get( request.getPlugId() ) > startHit))) {
                    log.error( "No results querying ibus with communication setting in '" + this.communicationXml + "' in attempt " + requestAttempt + "  with query: " + query );
                    log.error( "Wait for " + WAIT_BETWEEN_IBUS_REQUESTS_ATTEMPTS + " ms." );
                    hits = null;
                    Thread.sleep( WAIT_BETWEEN_IBUS_REQUESTS_ATTEMPTS );
                }
            } catch (Exception e) {
                hits = null;
                log.error( "Error querying ibus with communication setting in '" + this.communicationXml + "' in attempt " + requestAttempt + "  with query: " + query, e );
                log.error( "Wait for " + WAIT_BETWEEN_IBUS_REQUESTS_ATTEMPTS + " ms." );
                Thread.sleep( WAIT_BETWEEN_IBUS_REQUESTS_ATTEMPTS );
            }
        }
        if (hits == null) {
            int errorCount = (errorCounts.get( request.getPlugId() ) == null ? 0 : errorCounts.get( request.getPlugId() ));
            int hitCount = iPlugTotalResults.get( request.getPlugId() ).intValue();
            errorCount += hitCount - (endHit + 1);
            statusProvider.addState( request.getPlugId() + "fetch", "Fetch records for iPlug '" + request.getPlugId() + "'... [" + (hitCount == 0 ? 0 : (endHit + 1)) + "/"
                    + hitCount + "] with " + errorCount + " errors.", errorCount > 0 ? StatusProvider.Classification.WARN : StatusProvider.Classification.INFO );
            throw new Exception( "Error querying ibus '" + bus + "' after " + MAX_IBUS_REQUESTS_ATTEMPTS + " attempts with query:" + query );
        }

        List<Serializable> cacheIds = this.cacheRecords( hits, bus );
        if (cacheIds != null && cacheIds.size() > 0) {
            int errorCount = (errorCounts.get( request.getPlugId() ) == null ? 0 : errorCounts.get( request.getPlugId() ));
            statusProvider.addState( request.getPlugId() + "fetch", "Fetch records for iPlug '" + request.getPlugId() + "'... [" + (hits.length() == 0 ? 0 : (endHit + 1)) + "/"
                    + hits.length() + "] with " + errorCount + " errors.", errorCount > 0 ? StatusProvider.Classification.WARN : StatusProvider.Classification.INFO );
            if (log.isInfoEnabled()) {
                log.info( "Fetched records " + (startHit + 1) + " to " + (endHit + 1) + " of " + hits.length() + " with " + errorCount + " errors." );
            }
        } else {
            if (log.isInfoEnabled()) {
                log.info( "No further hits to be retrieved." );
            }
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
                    // add caching information, which is identified in the
                    // communication layer of the iPlug
                    if (!useCachedDocs) {
                        hit.put( "cache", "cache: false" );
                    }
                    record = bus.getRecord( hit );
                } catch (Throwable t) {
                    log.warn( "Error getting record from ibus with communication setting in '" + this.communicationXml + "' in attempt " + requestAttempt + "  with index record: "
                            + hit.getDocumentId(), t );
                    log.info( "Wait for " + WAIT_BETWEEN_IBUS_REQUESTS_ATTEMPTS + " ms." );
                    Thread.sleep( WAIT_BETWEEN_IBUS_REQUESTS_ATTEMPTS );
                }
            }
            if (record == null) {
                log.error( "Skip record from ibus with communication setting in '" + this.communicationXml + "'from iPlug '" + hit.getPlugId() + "' in attempt " + requestAttempt
                        + "  with index record: " + hit.getDocumentId() );
                addError( hit.getPlugId() );
                cacheIds.add( null );
                continue;
            }

            PlugDescription pd = null;
            Element element;
            if ((element = plugDescriptionCache.get( hit.getPlugId() )) != null) {
                pd = (PlugDescription) element.getValue();
            } else {
                requestAttempt = 0;
                while (pd == null && (requestAttempt++ <= MAX_IBUS_REQUESTS_ATTEMPTS)) {
                    try {
                        pd = bus.getIPlug( hit.getPlugId() );
                    } catch (Throwable t) {
                        log.warn( "Error getting plugdescription from ibus with communication setting in '" + this.communicationXml + "' in attempt " + requestAttempt
                                + "  for plugid: " + hit.getPlugId(), t );
                        log.info( "Wait for " + WAIT_BETWEEN_IBUS_REQUESTS_ATTEMPTS + " ms." );
                        Thread.sleep( WAIT_BETWEEN_IBUS_REQUESTS_ATTEMPTS );
                    }
                }
                if (pd != null) {
                    plugDescriptionCache.put( new Element( hit.getPlugId(), pd ) );
                }
            }

            if (pd == null) {
                log.error( "Skip getting plugdescription from ibus with communication setting in '" + this.communicationXml + "' in attempt " + requestAttempt + "  for plugid: "
                        + hit.getPlugId() );
                log.error( "This results in incomplete ingrid specific data in the record." );
            } else {
                record.put( PLUGDESCRIPTION, pd );
            }

            Serializable cacheId = null;
            try {
                cacheId = this.cache.put( record );
                cacheIds.add( cacheId );
                if (log.isDebugEnabled()) {
                    log.debug( "Fetched record " + hit.getDocumentId() + ". Cache id: " + cacheId );
                }
            } catch (Exception e) {
                addError( hit.getPlugId() );
                // Add null as cache ID. The number of records retrieved is bound to the list of cached ids.
                //
                // see also line while (cacheIds.size() == request.getRecordsPerCall()) {
                //
                // As a result the number of records must not be altered, even in case an error in
                // processing the results occurs, which is the case here.
                //
                // There was a case at HMDK where a IDF content could not be retrieved from the result Record.
                // This resulted in a cacheIds List lower than the expected records per call which leads
                // to an premature termination of the harvesting process.
                cacheIds.add( null );
                Serializable docId = null;
                try {
                    docId = this.cache.getCacheId(record);
                } catch (Exception e1) {
                    log.error("Error extracting the record id.");
                    docId = "";
                }
                log.error( "Error putting record " + hit.getDocumentId() + " (docid: '" + docId + "') from iPlug '" + hit.getPlugId() + "' to cache.", e );
            }
        }
        return cacheIds;
    }

    private void addError(String plugId) {
        if (!errorCounts.containsKey( plugId )) {
            errorCounts.put( plugId, 1 );
        } else {
            errorCounts.put( plugId, errorCounts.get( plugId ) + 1 );
        }
    }
}
