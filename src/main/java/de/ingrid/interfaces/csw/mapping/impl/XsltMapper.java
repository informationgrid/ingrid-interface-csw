/**
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.mapping.impl;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.ingrid.interfaces.csw.cache.DocumentCache;
import de.ingrid.interfaces.csw.config.ConfigurationProvider;
import de.ingrid.interfaces.csw.domain.CSWRecord;
import de.ingrid.interfaces.csw.domain.constants.ElementSetName;
import de.ingrid.interfaces.csw.harvest.impl.RecordCache;
import de.ingrid.interfaces.csw.mapping.CSWRecordMapper;
import de.ingrid.interfaces.csw.search.CSWRecordRepository;
import de.ingrid.interfaces.csw.tools.IdfUtils;
import de.ingrid.interfaces.csw.tools.StringUtils;
import de.ingrid.interfaces.csw.tools.XsltUtils;
import de.ingrid.utils.dsc.Record;

/**
 * A CSWRecordMapper that maps records using XSLT.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
@Service
public class XsltMapper implements CSWRecordMapper {

    final protected static Log log = LogFactory.getLog(XsltMapper.class);

    /**
     * The update job configuration provider
     */
    @Autowired
    private ConfigurationProvider configurationProvider;

    /**
     * Used for xslt
     */
    private XsltUtils xslt = new XsltUtils();

    /**
     * The cache used to store records.
     */
    protected CSWRecordCache cache;

    /**
     * The xslt style sheet for csw iso19139
     */
    private static final File xslFull = new File("idf_1_0_0_to_iso_metadata.xsl");
    private static final File xslSummary = new File("iso-summary.xsl");
    private static final File xslBrief = new File("iso-brief.xsl");

    
    @Override
    public void run(List<RecordCache> recordCacheList) throws Exception {
        if (configurationProvider != null) {
            this.cache = new CSWRecordCache();
            this.cache.setCachePath(configurationProvider.getRecordCachePath());
        }
        
        DocumentCache<CSWRecord> tmpCache = this.cache.startTransaction(false);
        try {
            // iterate over all caches and records
            for (RecordCache recordCache : recordCacheList) {
                for (Serializable cacheId : recordCache.getCachedIds()) {
                    for (ElementSetName elementSetName : ElementSetName.values()) {
                        if (log.isDebugEnabled()) {
                            log.debug("Mapping record " + cacheId + " to csw " + elementSetName);
                        }
                        Node mappedRecord = this.map(recordCache.get(cacheId), elementSetName);
                        CSWRecord cswRecord = new CSWRecord(elementSetName, mappedRecord);
                        tmpCache.put(cswRecord);
                    }
                }
            }
            tmpCache.commitTransaction();
        } catch (Exception e) {
            log.error("Error mapping ISO data. Rolling back mapping transaction.", e);
            if (tmpCache.isInTransaction()) {
                tmpCache.rollbackTransaction();
            }
            throw e;
        }
    }

    @Override
    public CSWRecordRepository getRecordRepository() {
        if (configurationProvider != null) {
            this.cache = new CSWRecordCache();
            this.cache.setCachePath(configurationProvider.getRecordCachePath());
        }
        return this.cache;
    }

    /**
     * Set the record cache.
     * 
     * @param cache
     */
    public void setCache(CSWRecordCache cache) {
        this.cache = cache;
    }

    /**
     * Map the given IDF record to the given CSW element set.
     * 
     * @param record
     * @param elementSetName
     * @return Node
     * @throws Exception
     */
    public Node map(Record record, ElementSetName elementSetName) throws Exception {

        Node cswNode = null;

        // use appropriate stylesheet for each element set name
        switch (elementSetName) {
        case FULL:
            cswNode = this.mapFull(record);
            break;
        case SUMMARY:
            cswNode = this.mapSummary(record);
            break;
        case BRIEF:
            cswNode = this.mapBrief(record);
            break;
        }
        if (log.isDebugEnabled()) {
            log.debug("Mapping result (" + elementSetName + "): " + StringUtils.nodeToString(cswNode));
        }
        return cswNode;
    }

    /**
     * Map the given record to iso19139 FULL
     * 
     * @param record
     * @return Node
     * @throws Exception
     */
    protected Node mapFull(Record record) throws Exception {
        Document idfDoc = IdfUtils.getIdfDocument(record);
        return this.xslt.transform(idfDoc, xslFull);
    }

    /**
     * Map the given record to iso19139 SUMMARY
     * 
     * @param record
     * @return Node
     * @throws Exception
     */
    protected Node mapSummary(Record record) throws Exception {
        Document idfDoc = IdfUtils.getIdfDocument(record);
        // for summary we need to transform to full first
        Node full = this.xslt.transform(idfDoc, xslFull);
        return this.xslt.transform(full, xslSummary);
    }

    /**
     * Map the given record to iso19139 BRIEF
     * 
     * @param record
     * @return Node
     * @throws Exception
     */
    protected Node mapBrief(Record record) throws Exception {
        Document idfDoc = IdfUtils.getIdfDocument(record);
        // for brief we need to transform to full first
        Node full = this.xslt.transform(idfDoc, xslFull);
        return this.xslt.transform(full, xslBrief);
    }
}
