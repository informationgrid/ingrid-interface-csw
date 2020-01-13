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
import de.ingrid.interfaces.csw.tools.LuceneTools;
import de.ingrid.utils.statusprovider.StatusProviderService;

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
    private StatusProviderService statusProviderService;

    @Autowired
    private LuceneTools luceneTools;

    @Override
    public void run(List<RecordCache> recordCacheList) throws Exception {

        if (log.isDebugEnabled()) {
            log.debug("Running indexer");
        }

        // overwrite indexer path with configuration
        if (this.configurationProvider != null) {
            this.indexConfigPath = this.configurationProvider.getNewIndexPath();
        }

        // delete older indexing destination
        if (this.indexConfigPath.exists()) {
            this.indexConfigPath.delete();
        }

        // CREATE new analyzer ! This one will be closed by geotoolkit indexer when indexing finished !
        Analyzer myAnalyzer = this.luceneTools.createAnalyzer();
        IngridGeoTKLuceneIndexer geoTKIndexer = new IngridGeoTKLuceneIndexer("", this.indexConfigPath, myAnalyzer, this.statusProviderService.getDefaultStatusProvider());
        // TODO: set log level
        geoTKIndexer.setRecordCacheList(recordCacheList);
        geoTKIndexer.setMapper(this.mapper);
        geoTKIndexer.createIndex();
        geoTKIndexer.destroy();
    }

    @Override
    public void removeDocs(Set<Serializable> records) throws Exception {
        if (this.configurationProvider != null) {
            File indexPath = this.configurationProvider.getIndexPath();
            IngridGeoTKLuceneIndexer geoTKIndexer = new IngridGeoTKLuceneIndexer("", indexPath, null, this.statusProviderService.getDefaultStatusProvider());
            for (Serializable record : records) {
                geoTKIndexer.removeDocument(record.toString());
            }
            geoTKIndexer.optimize();
            geoTKIndexer.destroy();
        }
    }

    @Override
    public List<String> removeDocsByQuery(String queryString) throws Exception {
        File indexPath = this.configurationProvider.getIndexPath();
        IngridGeoTKLuceneIndexer geoTKIndexer = new IngridGeoTKLuceneIndexer("", indexPath, null, this.statusProviderService.getDefaultStatusProvider());
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

    public void setStatusProviderService(StatusProviderService statusProviderService) {
        this.statusProviderService = statusProviderService;
    }

    public void setLuceneTools(LuceneTools luceneTools) {
        this.luceneTools = luceneTools;
    }
}
