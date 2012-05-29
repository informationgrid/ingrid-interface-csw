/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.index;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

import de.ingrid.interfaces.csw.harvest.impl.RecordCache;

/**
 * Indexer defines the interface for document indexing implementations.
 * 
 * @author ingo@wemove.com
 * @author joachim@wemove.com
 */
public interface Indexer {

    /**
     * Execute the indexing job.
     * 
     * @throws Exception
     */
    void run(List<RecordCache> recordCacheList) throws Exception;

    /**
     * Remove documents from index.
     * 
     * @throws Exception
     */
    void removeDocs(Set<Serializable> docIds) throws Exception;

    /**
     * Remove documents from index by query. Returns a List of the "id" field
     * content of the removed index documents.
     * 
     * @throws Exception
     */
    List<String> removeDocsByQuery(String queryString) throws Exception;

    /**
     * Get the path to the Lucene index
     * 
     * @return File
     */
    public File getIndexConfigPath();
}
