/**
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.mapping.impl;

import java.io.Serializable;

import org.w3c.dom.Document;

import de.ingrid.interfaces.csw.cache.AbstractFileCache;
import de.ingrid.interfaces.csw.domain.CSWRecord;
import de.ingrid.interfaces.csw.domain.constants.ElementSetName;
import de.ingrid.interfaces.csw.search.CSWRecordRepository;
import de.ingrid.interfaces.csw.tools.StringUtils;

/**
 * A cache that stores CSW records.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public class CSWRecordCache extends AbstractFileCache<CSWRecord> implements CSWRecordRepository, Serializable {

	private static final long serialVersionUID = CSWRecordCache.class.getName().hashCode();

	@Override
	public Serializable getCacheId(CSWRecord document) throws Exception {
		return document.getId() + "_" + document.getElementSetName();
	}

	@Override
	public String serializeDocument(Serializable id, CSWRecord document) throws Exception {
		return StringUtils.nodeToString(document.getDocument());
	}

	@Override
	public CSWRecord unserializeDocument(Serializable id, String str) throws Exception {
		Document document;
		document = StringUtils.stringToDocument(str);
		ElementSetName elementSetName = this.getElementSetNameFromCacheId(id);
		CSWRecord record = new CSWRecord(elementSetName, document);
		return record;
	}

	@Override
	public AbstractFileCache<CSWRecord> newInstance() {
		return new CSWRecordCache();
	}

	/**
	 * Get the cache id for a given id and element set name
	 * @param id
	 * @param elementSetName
	 * @return Serializable
	 */
	protected Serializable getCacheId(Serializable id, ElementSetName elementSetName) {
		return id + "_" + elementSetName;
	}

	/**
	 * Get the record id from the given cache id
	 * @param cacheId
	 * @return Serializable
	 */
	protected Serializable getRecordIdFromCacheId(Serializable cacheId) {
		if (cacheId != null) {
			return cacheId.toString().replaceAll("_\\w+$", "");
		}
		else {
			throw new IllegalArgumentException("Id argument must not be null");
		}
	}

	/**
	 * Get the element set name from the given cache id
	 * @param cacheId
	 * @return ElementSetName
	 */
	protected ElementSetName getElementSetNameFromCacheId(Serializable cacheId) {
		if (cacheId != null) {
			String elementSetNameStr = cacheId.toString().replaceAll("^.*_", "").toUpperCase();
			return ElementSetName.valueOf(elementSetNameStr);
		}
		else {
			throw new IllegalArgumentException("Id argument must not be null");
		}
	}

	/**
	 * CSWRecordRepository implementation
	 */

	@Override
	public CSWRecord getRecord(Serializable id, ElementSetName elementSetName) throws Exception {
		Serializable cacheId = this.getCacheId(id, elementSetName);
		return this.get(cacheId);
	}

	@Override
	public boolean containsRecord(String id) {
		Serializable cacheId = this.getCacheId(id, ElementSetName.FULL);
		return this.isCached(cacheId);
	}
}
