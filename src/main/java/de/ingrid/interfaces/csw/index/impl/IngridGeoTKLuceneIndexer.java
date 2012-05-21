/**
 * 
 */
package de.ingrid.interfaces.csw.index.impl;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.geotoolkit.lucene.IndexingException;
import org.geotoolkit.lucene.index.AbstractIndexer;

import de.ingrid.interfaces.csw.harvest.impl.RecordCache;
import de.ingrid.interfaces.csw.index.RecordLuceneMapper;
import de.ingrid.utils.dsc.Record;
import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @author joachim@wemove.com
 * 
 */
public class IngridGeoTKLuceneIndexer extends AbstractIndexer<Record> {

    private List<RecordCache> recordCacheList = null;

    private RecordLuceneMapper mapper;
    
    private Map<String, Object> mapperUtils = null;

    final protected static Log log = LogFactory.getLog(IngridGeoTKLuceneIndexer.class);

    public IngridGeoTKLuceneIndexer(String serviceID, File configDirectory, Analyzer analyzer) {
        super(serviceID, configDirectory, analyzer);
        mapperUtils = new HashMap<String, Object>();
        mapperUtils.put("geometryMapper", this);
    }

    @Override
    protected Document createDocument(Record record, int docId) throws IndexingException {
        if (mapper == null) {
            throw new RuntimeException("Indexer not initialized. Mapper is not set.");
        }
        try {
            mapperUtils.put("docid", docId);
            return  mapper.map(record, mapperUtils);
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

        Collection<String> allIdentifiers = new ArrayList<String>();
        for (RecordCache cache : recordCacheList) {
            for (Serializable recordId : cache.getCachedIds()) {
                if (log.isDebugEnabled()) {
                    log.debug("Add Indexing record " + cache.getCachePath().getAbsolutePath() + "::" + recordId);
                }
                allIdentifiers.add(cache.getCachePath().getAbsolutePath() + "::" + recordId);
            }
        }
        return allIdentifiers;
    }

    @Override
    protected Record getEntry(String identifier) throws IndexingException {
        String[] cacheRecordId = identifier.split("::");

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
    public void addBoundingBox(final Document doc, final Double minx[], final Double maxx[], final Double miny[], final Double maxy[], final Integer srid) {
        super.addBoundingBox(doc, java.util.Arrays.asList(minx), java.util.Arrays.asList(maxx), java.util.Arrays.asList(miny), java.util.Arrays.asList(maxy), srid);
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
    }

}
