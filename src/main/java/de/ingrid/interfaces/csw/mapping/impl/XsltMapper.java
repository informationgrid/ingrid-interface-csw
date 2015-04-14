/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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
import de.ingrid.interfaces.csw.index.StatusProvider;
import de.ingrid.interfaces.csw.mapping.CSWRecordMapper;
import de.ingrid.interfaces.csw.mapping.IPreCommitHandler;
import de.ingrid.interfaces.csw.search.CSWRecordRepository;
import de.ingrid.interfaces.csw.tools.IdfUtils;
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

	@Autowired
	private StatusProvider statusProvider;

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
	private static final String xslFull = "idf_1_0_0_to_iso_metadata.xsl";
	private static final String xslSummary = "iso-summary.xsl";
	private static final String xslBrief = "iso-brief.xsl";

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
					this.statusProvider.addState("iso-mapper", "Mapping records to ISO ... [" + idx + "/" + total
					        + "].");
					idx++;

					if (log.isDebugEnabled()) {
						log.debug("Mapping record " + cacheId + " to csw FULL");
					}
					Node mappedFullRecord = this.mapFull(recordCache.get(cacheId));
					CSWRecord cswRecord = new CSWRecord(ElementSetName.FULL, mappedFullRecord);
					tmpCache.put(cswRecord);

					if (log.isDebugEnabled()) {
						log.debug("Mapping record " + cacheId + " to csw SUMMARY");
					}
					Node mappedSummaryRecord = this.mapSummary(mappedFullRecord);
					cswRecord = new CSWRecord(ElementSetName.SUMMARY, mappedSummaryRecord);
					tmpCache.put(cswRecord);

					if (log.isDebugEnabled()) {
						log.debug("Mapping record " + cacheId + " to csw BRIEF");
					}
					Node mappedBriefRecord = this.mapSummary(mappedFullRecord);
					cswRecord = new CSWRecord(ElementSetName.BRIEF, mappedBriefRecord);
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
	 * Set the status provider.
	 * 
	 * @param statusProvider
	 */
	public void setStatusProvider(StatusProvider statusProvider) {
		this.statusProvider = statusProvider;
	}

	/**
	 * Map the given record to iso19139 FULL, based in an IDF record.
	 *
	 * @param record
	 * @return Node
	 * @throws Exception
	 */
	public Node mapFull(Record record) throws Exception {
		Document idfDoc = IdfUtils.getIdfDocument(record);
		return this.xslt.transform(idfDoc, xslFull);
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

	public void setPreCommitHandler(IPreCommitHandler preCommitHandler) {
		this.preCommitHandler = preCommitHandler;
	}

}
