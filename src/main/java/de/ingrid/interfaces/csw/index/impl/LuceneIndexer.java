/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.index.impl;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.interfaces.csw.config.ConfigurationProvider;
import de.ingrid.interfaces.csw.harvest.impl.RecordCache;
import de.ingrid.interfaces.csw.index.Indexer;
import de.ingrid.interfaces.csw.index.RecordLuceneMapper;
import de.ingrid.interfaces.csw.index.StatusProvider;
import de.ingrid.interfaces.csw.tools.LuceneTools;

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
    
    @Autowired
    private StatusProvider statusProvider;

    @Autowired
    private LuceneTools luceneTools;

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

        // CREATE new analyzer ! This one will be closed by geotoolkit indexer when indexing finished !
        Analyzer myAnalyzer = luceneTools.createAnalyzer();
        IngridGeoTKLuceneIndexer geoTKIndexer = new IngridGeoTKLuceneIndexer("", this.indexConfigPath, myAnalyzer, statusProvider);
        // TODO: set log level
        geoTKIndexer.setRecordCacheList(recordCacheList);
        geoTKIndexer.setMapper(mapper);
        geoTKIndexer.createIndex();
        geoTKIndexer.destroy();
    }

    @Override
    public void removeDocs(Set<Serializable> records) throws Exception {
        if (configurationProvider != null) {
            File indexPath = configurationProvider.getIndexPath();
            IngridGeoTKLuceneIndexer geoTKIndexer = new IngridGeoTKLuceneIndexer("", indexPath, null, statusProvider);
            for (Serializable record : records) {
                geoTKIndexer.removeDocument(record.toString());
            }
            geoTKIndexer.optimize();
            geoTKIndexer.destroy();
        }
    }

    @Override
    public List<String> removeDocsByQuery(String queryString) throws Exception {
        File indexPath = configurationProvider.getIndexPath();
        IngridGeoTKLuceneIndexer geoTKIndexer = new IngridGeoTKLuceneIndexer("", indexPath, null, statusProvider);
        List<String> ids = geoTKIndexer.removeDocumentByQuery(queryString);
        geoTKIndexer.optimize();
        geoTKIndexer.destroy();
        return ids;
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
