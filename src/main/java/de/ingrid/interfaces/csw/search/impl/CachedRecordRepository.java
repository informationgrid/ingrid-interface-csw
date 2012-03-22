/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.search.impl;

import java.io.Serializable;

import de.ingrid.interfaces.csw.cache.DocumentCache;
import de.ingrid.interfaces.csw.domain.CSWRecord;
import de.ingrid.interfaces.csw.domain.enums.ElementSetName;
import de.ingrid.interfaces.csw.search.CSWRecordRepository;

/**
 * A repository that retrieves CSW records from a cache.
 * 
 * @author ingo@wemove.com
 */
public class CachedRecordRepository implements CSWRecordRepository {

	/**
	 * The record cache.
	 */
	private DocumentCache<CSWRecord> cache;

	/**
	 * Constructor.
	 * 
	 * @param cache
	 */
	public CachedRecordRepository(DocumentCache<CSWRecord> cache) {
		this.cache = cache;
	}

	@Override
	public CSWRecord getRecord(Serializable id, ElementSetName elementSetName) throws Exception {
		// create a proxy to get the cache id for the record
		CSWRecord proxy = CSWRecord.getProxy(id, elementSetName);
		Serializable cacheId = this.cache.getCacheId(proxy);
		return this.cache.get(cacheId);
	}

}
