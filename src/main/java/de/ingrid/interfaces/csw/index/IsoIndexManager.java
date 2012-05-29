/**
 * 
 */
package de.ingrid.interfaces.csw.index;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.interfaces.csw.domain.CSWRecord;
import de.ingrid.interfaces.csw.domain.constants.ElementSetName;
import de.ingrid.interfaces.csw.harvest.impl.RecordCache;
import de.ingrid.interfaces.csw.jobs.UpdateJob;
import de.ingrid.interfaces.csw.mapping.CSWRecordMapper;
import de.ingrid.interfaces.csw.search.CSWRecordRepository;
import de.ingrid.interfaces.csw.search.Searcher;
import de.ingrid.interfaces.csw.tools.FileUtils;

/**
 * Provides a single interface to all index / ISO cache related functionality.
 * 
 * 
 * @author joachim@wemove.com
 * 
 */
@Service
public class IsoIndexManager {

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

    /**
     * The CSWRecordMapper instance
     */
    @Autowired
    private CSWRecordMapper cswRecordMapper;

    private Set<Serializable> toBeDeleted = null;
    private List<String> toBeDeletedQueries = null;

    public void index(List<RecordCache> recordCacheList) throws Exception {
            int recordCount = 0;
            for (RecordCache cache : recordCacheList) {
                recordCount += cache.getCachedIds().size();
            }

            this.indexer.run(recordCacheList);
            log.info("Transforming " + recordCount + " idf documents into ISO element sets full, summary, brief.");
            // map ingrid records to csw records
            this.cswRecordMapper.run(recordCacheList);
            CSWRecordRepository cswRecordRepo = this.cswRecordMapper.getRecordRepository();
            log.info("Stop the searcher instance.");
            stopSearcher();
            activateNewIndex();
            this.searcher.setRecordRepository(cswRecordRepo);
            log.info("Start the searcher instance.");
            startSearcher();

        // remove records marked for removal
        if (toBeDeleted != null && toBeDeleted.size() > 0) {
            wipe(toBeDeleted);
        }
        if (toBeDeletedQueries != null && toBeDeletedQueries.size() > 0) {
            wipeByQuery(toBeDeletedQueries);
            synchronized (this) {
                toBeDeletedQueries.clear();
            }
        }

    }

    public void stopSearcher() throws Exception {
        this.searcher.stop();
    }

    public void startSearcher() throws Exception {
        this.searcher.start();
    }

    public void activateNewIndex() {
        // move temporary index to live location
        if (log.isDebugEnabled()) {
            log.debug("Remove old index: " + this.searcher.getIndexPath().getAbsolutePath());
        }
        if (this.searcher.getIndexPath().exists()) {
            FileUtils.deleteRecursive(this.searcher.getIndexPath());
        }
        if (log.isDebugEnabled()) {
            log.debug("Rename new index: " + this.indexer.getIndexConfigPath().getAbsolutePath() + " to "
                    + this.searcher.getIndexPath().getAbsolutePath());
        }
        if (!this.indexer.getIndexConfigPath().renameTo(this.searcher.getIndexPath())) {
            log.warn("Could not renam old index: " + this.indexer.getIndexConfigPath().getAbsolutePath() + " to "
                    + this.searcher.getIndexPath().getAbsolutePath());
        }
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
                    if (toBeDeleted != null) {
                        toBeDeleted.addAll(ids);
                    } else {
                        toBeDeleted = ids;
                    }
                }
            } else {
                wipe(ids);
            }
        } catch (Exception e) {
            log.error("Error removing documents.", e);
        }
    }
    
    private void wipe(Set<Serializable> ids) throws Exception {
        this.indexer.removeDocs(ids);
        this.searcher.refresh();
        for (Serializable id : ids) {
            cswRecordMapper.getRecordRepository().removeRecord(id, ElementSetName.FULL);
            cswRecordMapper.getRecordRepository().removeRecord(id, ElementSetName.BRIEF);
            cswRecordMapper.getRecordRepository().removeRecord(id, ElementSetName.SUMMARY);
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
                    if (toBeDeletedQueries == null) {
                        toBeDeletedQueries = new ArrayList<String>();
                    }
                    toBeDeletedQueries.add(queryString);
                }
            } else {
                List<String> queries = new ArrayList<String>();
                queries.add(queryString);
                wipeByQuery(queries);
                this.searcher.refresh();
            }
        } catch (Exception e) {
            log.error("Error removing documents.", e);
        }
    }

    private void wipeByQuery(List<String> queries) throws Exception {
        List<String> ids = new ArrayList<String>();
        for (String query : queries) {
            ids.addAll(indexer.removeDocsByQuery(query));
        }
        this.searcher.refresh();
        
        for (Serializable id : ids) {
            cswRecordMapper.getRecordRepository().removeRecord(id, ElementSetName.FULL);
            cswRecordMapper.getRecordRepository().removeRecord(id, ElementSetName.BRIEF);
            cswRecordMapper.getRecordRepository().removeRecord(id, ElementSetName.SUMMARY);
        }
    }
   
    
    public void setIndexer(Indexer indexer) {
        this.indexer = indexer;
    }

    public void setSearcher(Searcher searcher) {
        this.searcher = searcher;
    }

    public void setCswRecordMapper(CSWRecordMapper cswRecordMapper) {
        this.cswRecordMapper = cswRecordMapper;
    }
    

}
