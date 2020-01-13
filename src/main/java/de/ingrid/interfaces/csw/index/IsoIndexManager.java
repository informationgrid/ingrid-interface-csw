/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
/**
 *
 */
package de.ingrid.interfaces.csw.index;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.interfaces.csw.cache.DocumentCache;
import de.ingrid.interfaces.csw.config.ApplicationProperties;
import de.ingrid.interfaces.csw.domain.CSWRecord;
import de.ingrid.interfaces.csw.domain.constants.ConfigurationKeys;
import de.ingrid.interfaces.csw.domain.constants.ElementSetName;
import de.ingrid.interfaces.csw.harvest.impl.RecordCache;
import de.ingrid.interfaces.csw.jobs.UpdateJob;
import de.ingrid.interfaces.csw.mapping.CSWRecordMapper;
import de.ingrid.interfaces.csw.mapping.IPreCommitHandler;
import de.ingrid.interfaces.csw.search.CSWRecordRepository;
import de.ingrid.interfaces.csw.search.Searcher;
import de.ingrid.interfaces.csw.tools.FileUtils;
import de.ingrid.utils.statusprovider.StatusProviderService;
import de.ingrid.utils.statusprovider.StatusProvider;

/**
 * Provides a single interface to all index / ISO cache related functionality.
 *
 *
 * @author joachim@wemove.com
 *
 */
@Service
public class IsoIndexManager implements IPreCommitHandler {

    final protected static Log log = LogFactory.getLog(IsoIndexManager.class);

    /**
     * The Indexer instance
     */
    @Autowired
    private Indexer indexer;

    /**
     * The Searcher instance
     */
    @Autowired
    private Searcher searcher;

    @Autowired
    private StatusProviderService statusProviderService;
    /**
     * The CSWRecordMapper instance
     */
    @Autowired
    private CSWRecordMapper cswRecordMapper;

    private Set<Serializable> toBeDeleted = null;
    private List<String> toBeDeletedQueries = null;

    public void index(List<RecordCache> recordCacheList) throws Exception {

        try {
            this.indexer.run(recordCacheList);
        } catch( Exception ex) {
            this.statusProviderService.getDefaultStatusProvider().addState( "error-index", "Could not index documents! Please check the logs. Old index will be used meanwhile.", StatusProvider.Classification.ERROR );
            return;
        }
        int recordCount = 0;
        for (RecordCache cache : recordCacheList) {
            recordCount += cache.getCachedIds().size();
        }
        log.info("Transforming " + recordCount + " idf documents into ISO element sets full, summary, brief.");
        // map ingrid records to csw records
        this.cswRecordMapper.run(recordCacheList);
        CSWRecordRepository cswRecordRepo = this.cswRecordMapper.getRecordRepository();
        log.info("Stop the searcher instance.");
        this.statusProviderService.getDefaultStatusProvider().addState("reload-index", "Reload index...");
        this.stopSearcher();
        this.activateNewIndex();
        this.searcher.setRecordRepository(cswRecordRepo);
        log.info("Start the searcher instance.");
        this.startSearcher();
        this.statusProviderService.getDefaultStatusProvider().addState("reload-index", "Reload index.");

        // remove records marked for removal
        if (this.toBeDeleted != null && this.toBeDeleted.size() > 0) {
            this.statusProviderService.getDefaultStatusProvider().addState("remove-deferred", "Remove records marked as deleted during harvesting...");
            this.wipe(this.toBeDeleted);
            this.statusProviderService.getDefaultStatusProvider().addState("remove-deferred", "Remove records marked as deleted during harvesting.");
        }
        if (this.toBeDeletedQueries != null && this.toBeDeletedQueries.size() > 0) {
            this.statusProviderService.getDefaultStatusProvider().addState("remove-deferred", "Remove records marked as deleted during harvesting...");
            this.wipeByQuery(this.toBeDeletedQueries);
            synchronized (this) {
                this.toBeDeletedQueries.clear();
            }
            this.statusProviderService.getDefaultStatusProvider().addState("remove-deferred", "Remove records marked as deleted during harvesting.");
        }

    }

    public void stopSearcher() throws Exception {
        this.searcher.stop();
    }

    public void startSearcher() throws Exception {
        this.searcher.start();
    }

    /**
     * Removes the old index directory, waits until the dir was removed and
     * moves the temporary into 'index'.
     * 
     * @throws IOException
     */
    public void activateNewIndex() throws IOException {

        Path indexPath = this.searcher.getIndexPath().toPath();
        Path newIndexPath = this.indexer.getIndexConfigPath().toPath();

        // move temporary index to live location
        if (log.isInfoEnabled()) {
            log.info("Remove old index: " + indexPath);
        }
        if (this.searcher.getIndexPath().exists()) {
            FileUtils.waitAndDelete(indexPath, ApplicationProperties.getInteger( ConfigurationKeys.FILE_OPERATION_TIMEOUT, 10000));
        }

        if (log.isInfoEnabled()) {
            log.info("Rename new index: " + newIndexPath + " to " + indexPath);
        }
        FileUtils.waitAndMove(newIndexPath, indexPath, ApplicationProperties.getInteger( ConfigurationKeys.FILE_OPERATION_TIMEOUT, 10000));
    }

    /**
     * Remove a list of CSWRecords from index. The searcher instance is
     * refreshed after successful removal.
     *
     * @param records
     */
    public void removeDocumentsByCSWRecord(List<CSWRecord> records) {
        if (records == null) {
            return;
        }
        try {
            Set<Serializable> toBeRemoved = new HashSet<Serializable>();
            for (CSWRecord record : records) {
                toBeRemoved.add(record.getId());
            }
            this.removeDocuments(toBeRemoved);
        } catch (Exception e) {
            log.error("Error removing documents.", e);
        }
    }

    /**
     * Remove a Set of Serializables from index. The searcher instance is
     * refreshed after successful removal.
     *
     * @param ids
     */
    public void removeDocuments(Set<Serializable> ids) {
        try {
            if (UpdateJob.executeLock.isLocked()) {
                // if indexing is in progress, store records in list for later
                // removal
                synchronized (this) {
                    if (this.toBeDeleted != null) {
                        this.toBeDeleted.addAll(ids);
                    } else {
                        this.toBeDeleted = ids;
                    }
                }
            } else {
                this.wipe(ids);
            }
        } catch (Exception e) {
            log.error("Error removing documents.", e);
        }
    }

    private void wipe(Set<Serializable> ids) throws Exception {
        this.indexer.removeDocs(ids);
        this.searcher.refresh();
        for (Serializable id : ids) {
            this.cswRecordMapper.getRecordRepository().removeRecord(id, ElementSetName.FULL);
            this.cswRecordMapper.getRecordRepository().removeRecord(id, ElementSetName.BRIEF);
            this.cswRecordMapper.getRecordRepository().removeRecord(id, ElementSetName.SUMMARY);
        }
    }

    /**
     * Removes all documents that match a certain query from the index. The
     * searcher instance is refreshed after successful removal.
     *
     * @param queryString
     */
    public void removeDocumentsByQuery(String queryString) {
        try {
            if (UpdateJob.executeLock.isLocked()) {
                // if indexing is in progress, store records in list for later
                // removal
                synchronized (this) {
                    if (this.toBeDeletedQueries == null) {
                        this.toBeDeletedQueries = new ArrayList<String>();
                    }
                    this.toBeDeletedQueries.add(queryString);
                }
            } else {
                List<String> queries = new ArrayList<String>();
                queries.add(queryString);
                this.wipeByQuery(queries);
                this.searcher.refresh();
            }
        } catch (Exception e) {
            log.error("Error removing documents.", e);
        }
    }

    private void wipeByQuery(List<String> queries) throws Exception {
        List<String> ids = new ArrayList<String>();
        for (String query : queries) {
            ids.addAll(this.indexer.removeDocsByQuery(query));
        }
        this.searcher.refresh();

        for (Serializable id : ids) {
            this.cswRecordMapper.getRecordRepository().removeRecord(id, ElementSetName.FULL);
            this.cswRecordMapper.getRecordRepository().removeRecord(id, ElementSetName.BRIEF);
            this.cswRecordMapper.getRecordRepository().removeRecord(id, ElementSetName.SUMMARY);
        }
    }

    public void setIndexer(Indexer indexer) {
        this.indexer = indexer;
    }

    public void setSearcher(Searcher searcher) {
        this.searcher = searcher;
    }

    public void setStatusProviderService(StatusProviderService statusProviderService) {
        this.statusProviderService = statusProviderService;
    }

    public void setCswRecordMapper(CSWRecordMapper cswRecordMapper) {
        this.cswRecordMapper = cswRecordMapper;
    }
    @Override
    public void beforeCommit(DocumentCache<?> cache) throws Exception {
        log.info("Stop the searcher instance.");
        statusProviderService.getDefaultStatusProvider().addState("reload-index", "Reload index...");
        stopSearcher();
    }

}
