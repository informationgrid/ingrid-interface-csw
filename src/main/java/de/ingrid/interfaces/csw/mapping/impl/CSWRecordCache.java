/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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

import java.io.File;
import java.io.Serializable;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import de.ingrid.interfaces.csw.cache.AbstractFileCache;
import de.ingrid.interfaces.csw.config.ConfigurationProvider;
import de.ingrid.interfaces.csw.domain.CSWRecord;
import de.ingrid.interfaces.csw.domain.constants.ElementSetName;
import de.ingrid.interfaces.csw.search.CSWRecordRepository;
import de.ingrid.interfaces.csw.tools.StringUtils;

/**
 * A cache that stores CSW records.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
@Service
public class CSWRecordCache extends AbstractFileCache<CSWRecord> implements CSWRecordRepository, Serializable {

	private static final long serialVersionUID = CSWRecordCache.class.getName().hashCode();

	@Autowired
	private ConfigurationProvider configurationProvider = null;

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
	 * 
	 * @param id
	 * @param elementSetName
	 * @return Serializable
	 */
	protected Serializable getCacheId(Serializable id, ElementSetName elementSetName) {
		return id + "_" + elementSetName;
	}

	/**
	 * Get the record id from the given cache id
	 * 
	 * @param cacheId
	 * @return Serializable
	 */
	protected Serializable getRecordIdFromCacheId(Serializable cacheId) {
		if (cacheId != null) {
			int pos = cacheId.toString().lastIndexOf("_");
			return cacheId.toString().substring(0, pos);
		} else {
			throw new IllegalArgumentException("Id argument must not be null");
		}
	}

	/**
	 * Get the element set name from the given cache id
	 * 
	 * @param cacheId
	 * @return ElementSetName
	 */
	protected ElementSetName getElementSetNameFromCacheId(Serializable cacheId) {
		if (cacheId != null) {
			int pos = cacheId.toString().lastIndexOf("_");
			String elementSetNameStr = cacheId.toString().substring(pos + 1).toUpperCase();
			return ElementSetName.valueOf(elementSetNameStr);
		} else {
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

	@Override
	public File getCachePath() {
		if (this.cachePath == null) {
			this.cachePath = configurationProvider.getRecordCachePath();
		}
		return this.cachePath;
	}

	@Override
	public void removeRecord(Serializable id, ElementSetName elementSetName) {
		Serializable cacheId = this.getCacheId(id, elementSetName);
		this.remove(cacheId);
	}

	@Override
	protected String getRelativePath(Serializable id) {
		return DigestUtils.md5Hex(getRecordIdFromCacheId(id).toString()).substring(0, 3);
	}

}
