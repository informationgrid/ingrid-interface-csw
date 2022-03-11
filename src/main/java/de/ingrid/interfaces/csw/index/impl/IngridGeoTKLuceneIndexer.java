/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.index.impl;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;
import org.geotoolkit.lucene.IndexingException;
import org.geotoolkit.lucene.LuceneUtils;
import org.geotoolkit.lucene.index.AbstractIndexer;

import de.ingrid.interfaces.csw.harvest.impl.RecordCache;
import de.ingrid.interfaces.csw.index.RecordLuceneMapper;
import de.ingrid.utils.statusprovider.StatusProvider;
import de.ingrid.interfaces.csw.tools.StringUtils;
import de.ingrid.utils.dsc.Record;

/**
 * @author joachim@wemove.com
 * 
 */
public class IngridGeoTKLuceneIndexer extends AbstractIndexer<Record> {

    private List<RecordCache> recordCacheList = null;

    private RecordLuceneMapper mapper;

    private Map<String, Object> mapperUtils = null;

    private List<String> allIdentifiers = new ArrayList<String>();

    private StatusProvider statusProvider;

    final protected static Log log = LogFactory.getLog(IngridGeoTKLuceneIndexer.class);

    public IngridGeoTKLuceneIndexer(String serviceID, File configDirectory, Analyzer analyzer,
            StatusProvider statusProvider) {
        super(serviceID, configDirectory, analyzer);
        mapperUtils = new HashMap<String, Object>();
        mapperUtils.put("geometryMapper", this);
        this.statusProvider = statusProvider;
    }

    @Override
    protected Document createDocument(Record record, int docId) throws IndexingException {
        if (mapper == null) {
            throw new RuntimeException("Indexer not initialized. Mapper is not set.");
        }
        try {
            mapperUtils.put("docid", docId);
            return mapper.map(record, mapperUtils);
        } catch (Exception e) {
            log.error("Error mapping record to lucene document: " + record, e);
            throw new IndexingException("Error mapping record to lucene document: " + record);
        }
    }

    @Override
    protected Collection<String> getAllIdentifiers() throws IndexingException {
        if (recordCacheList == null) {
            throw new RuntimeException("Indexer not initialized. Record cache list is not set.");
        }

        for (RecordCache cache : recordCacheList) {
            for (Serializable recordId : cache.getCachedIds()) {
                if (log.isDebugEnabled()) {
                    log.debug("Add Indexing record " + cache.getCachePath().getAbsolutePath() + "::" + recordId);
                }
                allIdentifiers.add(cache.getCachePath().getAbsolutePath() + "::" + recordId);
            }
        }

        log.info("Returning " + allIdentifiers.size() + " records for indexing.");
        return allIdentifiers;
    }

    @Override
    protected Record getEntry(String identifier) throws IndexingException {
        String[] cacheRecordId = StringUtils.splitByFirstOccurence(identifier, "::");

        statusProvider.addState("create-index", "Indexing records ... [" + (allIdentifiers.indexOf(identifier)+1) + "/" + allIdentifiers.size() + "].");

        for (RecordCache cache : recordCacheList) {
            if (cache.getCachePath().getAbsolutePath().equals(cacheRecordId[0])) {
                try {
                    return cache.get(cacheRecordId[1]);
                } catch (Exception e) {
                    log.error("Could not find cache record: " + identifier, e);
                }
            }
        }

        return null;
    }

    @Override
    protected String getIdentifier(Record record) {
        for (RecordCache cache : recordCacheList) {
            try {
                String recordId = (String) cache.getCacheId(record);
                if (recordId != null) {
                    return cache.getCachePath().getAbsolutePath() + "::" + recordId;
                }
            } catch (Exception e) {
            }
        }
        log.warn("Record not found in record caches: " + record);
        return null;
    }

    /**
     * Make protected method public.
     * 
     * @param doc
     * @param minx
     * @param maxx
     * @param miny
     * @param maxy
     * @param srid
     */
    public void addBoundingBox(final Document doc, final Double minx[], final Double maxx[], final Double miny[],
            final Double maxy[], final Integer srid) {
        try {
            super.addBoundingBox(doc, java.util.Arrays.asList(minx), java.util.Arrays.asList(maxx), java.util.Arrays
                    .asList(miny), java.util.Arrays.asList(maxy), srid);
        } catch (Exception e) {
            log.warn("Error adding bounding box to lucene document.");
        }
    }

    public void setRecordCacheList(List<RecordCache> recordCacheList) {
        this.recordCacheList = recordCacheList;
    }

    public List<RecordCache> getRecordCacheList() {
        return recordCacheList;
    }

    public RecordLuceneMapper getMapper() {
        return mapper;
    }

    public void setMapper(RecordLuceneMapper mapper) {
        this.mapper = mapper;
        mapper.init();
    }

    /**
     * This method remove documents identified by query from the index.
     * 
     * @param query
     * @throws ParseException
     */
    public List<String> removeDocumentByQuery(final String queryString) throws ParseException {
        List<String> deletedRecords = new ArrayList<String>();
        try {
            final QueryParser parser = new QueryParser(Version.LUCENE_36, "anytext", analyzer);

            Query query = parser.parse(queryString);

            final IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
            final IndexWriter writer = new IndexWriter(LuceneUtils.getAppropriateDirectory(getFileDirectory()), config);

            LOGGER.log(logLevel, "Query:{0}", query);

            IndexReader reader = IndexReader.open(writer, false);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs docs = searcher.search(query, Integer.MAX_VALUE);
            for (ScoreDoc doc : docs.scoreDocs) {
                deletedRecords.add(reader.document(doc.doc).get("id"));
            }
            writer.deleteDocuments(query);

            writer.commit();
            searcher.close();
            reader.close();
            writer.close();

        } catch (CorruptIndexException ex) {
            LOGGER.log(Level.WARNING, "CorruptIndexException while indexing document: " + ex.getMessage(), ex);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "IOException while indexing document: " + ex.getMessage(), ex);
        }
        return deletedRecords;
    }

}
