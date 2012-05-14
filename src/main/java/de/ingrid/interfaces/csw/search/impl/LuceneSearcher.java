/**
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.search.impl;

import java.io.File;
import java.util.Set;
import java.util.logging.Level;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotoolkit.lucene.filter.SpatialQuery;
import org.geotoolkit.lucene.index.LuceneIndexSearcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.interfaces.csw.config.ConfigurationProvider;
import de.ingrid.interfaces.csw.domain.CSWRecord;
import de.ingrid.interfaces.csw.domain.constants.ElementSetName;
import de.ingrid.interfaces.csw.domain.filter.FilterParser;
import de.ingrid.interfaces.csw.domain.query.CSWQuery;
import de.ingrid.interfaces.csw.search.CSWRecordRepository;
import de.ingrid.interfaces.csw.search.CSWRecordResults;
import de.ingrid.interfaces.csw.search.Searcher;
import de.ingrid.interfaces.csw.tools.StringUtils;

/**
 * A Searcher implementation that searches in a Lucene index.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
@Service
public class LuceneSearcher implements Searcher {

    final protected static Log log = LogFactory.getLog(LuceneSearcher.class);

    /**
     * The path to the Lucene index
     */
    private File indexPath;

    /**
     * The update job configuration provider
     */
    @Autowired
    private ConfigurationProvider configurationProvider;

    /**
     * The FilterParser instance that converts csw queries to InGrid queries
     */
    @Autowired
    private FilterParser filterParser;

    /**
     * The Lucene index searcher
     */
    protected LuceneIndexSearcher lis = null;

    /**
     * The started state
     */
    protected boolean isStarted = false;

    /**
     * The repository where the CSW records are retrieved from
     */
    @Autowired
    private CSWRecordRepository recordRepository;

    @Override
    public void start() throws Exception {
        if (this.isStarted) {
            this.stop();
        }
        
        // overwrite indexer path with configuration
        if (configurationProvider != null) {
            this.indexPath = configurationProvider.getIndexPath();
        }
        
        log.info("Start search index: " + this.indexPath);

        lis = new LuceneIndexSearcher(this.indexPath, "");
        lis.setLogLevel(log.isDebugEnabled() ? Level.FINEST : (log.isInfoEnabled() ? Level.INFO : (log.isWarnEnabled() ? Level.WARNING : Level.SEVERE)));
        
        this.isStarted = true;
    }

    @Override
    public void stop() throws Exception {
        if (lis != null) {
            log.info("Close search index: " + this.indexPath);
            lis.destroy();
            this.isStarted = false;
        }
    }

    @Override
    public CSWRecordResults search(CSWQuery query) throws Exception {
        if (this.filterParser == null) {
            throw new RuntimeException("LuceneSearcher is not configured properly: filterParser is not set.");
        }
        if (this.recordRepository == null) {
            throw new RuntimeException("LuceneSearcher is not configured properly: recordRepository is not set.");
        }
        if (!this.isStarted) {
            start();
        }
        if (this.lis == null) {
            throw new RuntimeException("LuceneSearcher is not started.");
        }

        CSWRecordResults results = new CSWRecordResults();

        ElementSetName elementSetName = query.getElementSetName();
        if (query.getIds() != null) {
            // there are records specified by id. So we can search in the record
            // repository
            // directly
            for (String id : query.getIds()) {
                if (this.recordRepository.containsRecord(id)) {
                    CSWRecord record = this.recordRepository.getRecord(id, elementSetName);
                    results.add(record);
                }
            }
            // add number of results as total hits
            results.setTotalHits(results.getResults().size());
        } else {
            // use the query constraints to search for records in the Lucene
            // index
            if (log.isDebugEnabled()) {
                log.debug("Incoming constraint:" + StringUtils.nodeToString(query.getConstraint().getDocumentElement()));
            }
            SpatialQuery spatialQuery = this.filterParser.parse(query.getConstraint());
            if (log.isDebugEnabled()) {
                log.debug("Incoming spatial query:" + spatialQuery);
            }
            if (spatialQuery == null) {
                throw new RuntimeException("Error parsing query constraint: Lucene query is null");
            }
            Set<String> resultIds = lis.doSearch(spatialQuery);
            
            if (log.isDebugEnabled()) {
                log.debug("Found " + resultIds.size() + " hits, returning " + Math.min((query.getStartPosition() + query.getMaxRecords() -1), resultIds.size()) + ".");
            }

            // get the CSWRecord for each document found in the index
            int cnt=1;
            int endIdx = Math.min((query.getStartPosition() + query.getMaxRecords() -1), resultIds.size());
            for (String resultId : resultIds) {
                if (cnt >= query.getStartPosition() && cnt <= endIdx) {
                    CSWRecord record = this.recordRepository.getRecord(resultId, elementSetName);
                    results.add(record);
                } else if (cnt > endIdx) {
                    break;
                }
                cnt++;
            }
            results.setTotalHits(resultIds.size());
        }
        return results;
    }

    @Override
    public File getIndexPath() {
        if (this.indexPath == null && configurationProvider != null) {
            this.indexPath = configurationProvider.getIndexPath();
        }
        return this.indexPath;
    }

    /**
     * Set the path to the Lucene index
     * 
     * @param indexPath
     */
    public void setIndexPath(File indexPath) {
        this.indexPath = indexPath;
    }

    /**
     * Set the FilterParser instance
     * 
     * @param filterParser
     */
    public void setFilterParser(FilterParser filterParser) {
        this.filterParser = filterParser;
    }

    @Override
    public void setRecordRepository(CSWRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public boolean isStarted() {
        return isStarted;
    }

}