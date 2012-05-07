/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.index.impl;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.interfaces.csw.config.ConfigurationProvider;
import de.ingrid.interfaces.csw.harvest.impl.RecordCache;
import de.ingrid.interfaces.csw.index.Indexer;
import de.ingrid.interfaces.csw.index.RecordLuceneMapper;

/**
 * LuceneIndexer is used to put InGrid records into a Lucene index.
 * 
 * @author ingo@wemove.com
 * @author joachim@wemove.com
 */
@Service
public class LuceneIndexer implements Indexer {

    final protected static Log log = LogFactory.getLog(LuceneIndexer.class);

    /**
     * The path to the Lucene index
     */
    private File indexConfigPath = null;

    /**
     * The update job configuration provider
     */
    @Autowired
    private ConfigurationProvider configurationProvider;

    @Autowired
    private RecordLuceneMapper mapper;

    @Override
    public void run(List<RecordCache> recordCacheList) throws Exception {

        if (log.isDebugEnabled()) {
            log.debug("Running indexer");
        }

        // overwrite indexer path with configuration
        if (configurationProvider != null) {
            this.indexConfigPath = configurationProvider.getNewIndexPath();
        }

        // delete older indexing destination
        if (this.indexConfigPath.exists()) {
            this.indexConfigPath.delete();
        }

        IngridGeoTKLuceneIndexer geoTKIndexer = new IngridGeoTKLuceneIndexer("", this.indexConfigPath, null);
        // TODO: set log level
        geoTKIndexer.setRecordCacheList(recordCacheList);
        geoTKIndexer.setMapper(mapper);
        geoTKIndexer.createIndex();
        
    }

    @Override
    public File getIndexConfigPath() {
        return this.indexConfigPath;
    }

    /**
     * Set the path to the Lucene index
     * 
     * @param indexPath
     */
    public void setIndexConfigPath(File indexPath) {
        this.indexConfigPath = indexPath;
    }

    public void setMapper(RecordLuceneMapper mapper) {
        this.mapper = mapper;
    }

}
