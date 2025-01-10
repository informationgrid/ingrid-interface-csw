/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
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
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.mapping.impl;

import java.io.Serializable;
import java.util.List;

import de.ingrid.interfaces.csw.config.ApplicationProperties;
import de.ingrid.interfaces.csw.domain.constants.ConfigurationKeys;
import de.ingrid.interfaces.csw.domain.constants.Namespace;
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
import de.ingrid.interfaces.csw.mapping.IPreCommitHandler;
import de.ingrid.interfaces.csw.search.CSWRecordRepository;
import de.ingrid.interfaces.csw.tools.IdfUtils;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.statusprovider.StatusProviderService;
import de.ingrid.utils.tool.XsltUtils;

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

    @Autowired
    private StatusProviderService statusProviderService;

    /**
     * Used for xslt
     */
    private XsltUtils xslt = new XsltUtils();

    /**
     * The cache used to store records.
     */
    protected CSWRecordCache cache;

    private IPreCommitHandler preCommitHandler;

    /**
     * The xslt style sheet for csw iso19139
     */
    private static final String xslSummary = "iso-summary.xsl";
    private static final String xslBrief = "iso-brief.xsl";

    /**
     * style sheets for csw OGC output schema
     */
    private static final String xslSummaryOgc = "ogc-summary.xsl";
    private static final String xslBriefOgc = "ogc-brief.xsl";


    @Override
    public void run(List<RecordCache> recordCacheList) throws Exception {
        if (this.configurationProvider != null) {
            this.cache = new CSWRecordCache();
            this.cache.setCachePath(this.configurationProvider.getRecordCachePath());
        }

        DocumentCache<CSWRecord> tmpCache = this.cache.startTransaction(false);
        try {
            // iterate over all caches and records
            Integer idx = 1;
            Integer total = 0;
            for (RecordCache recordCache : recordCacheList) {
                total += recordCache.getCachedIds().size();
            }

            for (RecordCache recordCache : recordCacheList) {
                for (Serializable cacheId : recordCache.getCachedIds()) {
                    this.statusProviderService.getDefaultStatusProvider().addState("iso-mapper", "Mapping records to ISO and OGC... [" + idx + "/" + total
                            + "].");
                    idx++;

                    // write with namespace GMD
                    if (log.isDebugEnabled()) {
                        log.debug("Mapping record " + cacheId + " to csw FULL with output schema GMD");
                    }
                    Node mappedFullRecord = this.mapFull(recordCache.get(cacheId));
                    CSWRecord cswRecord = new CSWRecord(ElementSetName.FULL, Namespace.GMD, mappedFullRecord);
                    tmpCache.put(cswRecord);

                    if (log.isDebugEnabled()) {
                        log.debug("Mapping record " + cacheId + " to csw SUMMARY with output schema GMD");
                    }
                    Node mappedSummaryRecord = this.mapSummary(mappedFullRecord);
                    cswRecord = new CSWRecord(ElementSetName.SUMMARY, Namespace.GMD, mappedSummaryRecord);
                    tmpCache.put(cswRecord);

                    if (log.isDebugEnabled()) {
                        log.debug("Mapping record " + cacheId + " to csw BRIEF with output schema GMD");
                    }
                    Node mappedBriefRecord = this.mapSummary(mappedFullRecord);
                    cswRecord = new CSWRecord(ElementSetName.BRIEF, Namespace.GMD, mappedBriefRecord);
                    tmpCache.put(cswRecord);

                    // write with namespace OGC
                    if (log.isDebugEnabled()) {
                        log.debug("Mapping record " + cacheId + " to csw FULL with output schema OGC");
                    }
                    Node mappedFullRecordOgc = this.mapFullOgc(recordCache.get(cacheId));
                    cswRecord = new CSWRecord(ElementSetName.FULL, Namespace.CSW_2_0_2, mappedFullRecordOgc);
                    tmpCache.put(cswRecord);

                    if (log.isDebugEnabled()) {
                        log.debug("Mapping record " + cacheId + " to csw SUMMARY with output schema OGC");
                    }
                    Node mappedSummaryRecordOgc = this.mapSummaryOgc(mappedFullRecordOgc);
                    cswRecord = new CSWRecord(ElementSetName.SUMMARY, Namespace.CSW_2_0_2, mappedSummaryRecordOgc);
                    tmpCache.put(cswRecord);

                    if (log.isDebugEnabled()) {
                        log.debug("Mapping record " + cacheId + " to csw BRIEF with output schema OGC");
                    }
                    Node mappedBriefRecordOgc = this.mapBriefOgc(mappedFullRecordOgc);
                    cswRecord = new CSWRecord(ElementSetName.BRIEF, Namespace.CSW_2_0_2, mappedBriefRecordOgc);
                    tmpCache.put(cswRecord);
                }
            }
            if (preCommitHandler != null) {
                preCommitHandler.beforeCommit(tmpCache);
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
        if (this.configurationProvider != null) {
            this.cache = new CSWRecordCache();
            this.cache.setCachePath(this.configurationProvider.getRecordCachePath());
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
     * Set the status providerService.
     *
     * @param statusProviderService
     */
    public void setStatusProviderService(StatusProviderService statusProviderService) {
        this.statusProviderService = statusProviderService;
    }

    /**
     * Map the given record to iso19139 FULL, based on an IDF record
     *
     * @param record
     * @return Node
     * @throws Exception
     */
    public Node mapFull(Record record) throws Exception {
        Document idfDoc = IdfUtils.getIdfDocument(record);
        return this.xslt.transform(idfDoc, ApplicationProperties.get(ConfigurationKeys.IDF_2_FULL_PROCESSING_XSLT, "idf_1_0_0_to_iso_metadata.xsl"));
    }

    /**
     * Map the given record to iso19139 SUMMARY, based on a FULL document.
     *
     * @param full
     * @return Node
     * @throws Exception
     */
    public Node mapSummary(Node full) throws Exception {
        return this.xslt.transform(full, xslSummary);
    }

    /**
     * Map the given record to iso19139 BRIEF, based on a FULL document.
     *
     * @param full
     * @return Node
     * @throws Exception
     */
    public Node mapBrief(Node full) throws Exception {
        return this.xslt.transform(full, xslBrief);
    }

    /**
     * Map passed idf document to OGC full
     */
    public Node mapFullOgc(Record record) throws Exception {
        Document idfDoc = IdfUtils.getIdfDocument(record);
        return this.xslt.transform(idfDoc, ApplicationProperties.get(ConfigurationKeys.IDF_2_FULL_PROCESSING_XSLT_OGC, "ogc-full.xsl"));
    }

    public Node mapSummaryOgc(Node full) throws Exception {
        return this.xslt.transform(full, xslSummaryOgc);
    }

    public Node mapBriefOgc(Node full) throws Exception {
        return this.xslt.transform(full, xslBriefOgc);
    }


    public void setPreCommitHandler(IPreCommitHandler preCommitHandler) {
        this.preCommitHandler = preCommitHandler;
    }

}
