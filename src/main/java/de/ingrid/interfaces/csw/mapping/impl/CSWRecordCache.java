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

import de.ingrid.interfaces.csw.cache.AbstractFileCache;
import de.ingrid.interfaces.csw.config.ConfigurationProvider;
import de.ingrid.interfaces.csw.domain.CSWRecord;
import de.ingrid.interfaces.csw.domain.constants.ElementSetName;
import de.ingrid.interfaces.csw.domain.constants.Namespace;
import de.ingrid.interfaces.csw.search.CSWRecordRepository;
import de.ingrid.interfaces.csw.tools.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.io.File;
import java.io.Serializable;

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
		return document.getId() + "_" + document.getElementSetName() + "_" + document.getOutputSchema();
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
		Namespace outputSchema = this.getOutputSchemaFromCacheId(id);

		return new CSWRecord(elementSetName, outputSchema, document);
	}

	@Override
	public AbstractFileCache<CSWRecord> newInstance() {
		return new CSWRecordCache();
	}

	/**
	 * Get the cache id for a given id, element set name and output schema
	 *
	 * @param id id of the records we are looking for
	 * @param elementSetName set name of the record. Either: full, summary or brief
	 * @param outputSchema desired output schema
	 * @return Serializable
	 */
	protected Serializable getCacheId(Serializable id, ElementSetName elementSetName, Namespace outputSchema) {
		return id + "_" + elementSetName + "_" + outputSchema;
	}

	/**
	 * extract id, elementsetname and the output schema from the passed cache id
	 *
	 * @param cacheId
	 * @return ElementSetName
	 */
	protected ImmutableTriple<Serializable, ElementSetName, Namespace> extractFromCacheId(Serializable cacheId) {
		if (cacheId != null) {
			String[] splitCacheId = cacheId.toString().split("_");
			String id = splitCacheId[0];
			String elementSetName = splitCacheId[1];
			String outputSchema = splitCacheId[2];

			return ImmutableTriple.of(id, ElementSetName.valueOf(elementSetName.toUpperCase()),
					Namespace.valueOf(outputSchema.toUpperCase()));
		} else {
			throw new IllegalArgumentException("Id argument must not be null");
		}
	}

	/**
	 * Get the id from extracted triple in extractFromCacheId
	 *
	 * @param cacheId
	 * @return ElementSetName
	 */
	protected Serializable getRecordIdFromCacheId(Serializable cacheId) {
		ImmutableTriple<Serializable, ElementSetName, Namespace> elements = extractFromCacheId(cacheId);
		return elements.getLeft();
	}

	/**
	 * Get the element set name from extracted triple in extractFromCacheId
	 *
	 * @param cacheId
	 * @return ElementSetName
	 */
	protected ElementSetName getElementSetNameFromCacheId(Serializable cacheId) {
		ImmutableTriple<Serializable, ElementSetName, Namespace> elements = extractFromCacheId(cacheId);
		return elements.getMiddle();
	}

	/**
	 * Get the outputSchema from extracted triple in extractFromCacheId
	 *
	 * @param cacheId
	 * @return Namespace
	 */
	protected Namespace getOutputSchemaFromCacheId(Serializable cacheId) {
		ImmutableTriple<Serializable, ElementSetName, Namespace> elements = extractFromCacheId(cacheId);
		return elements.getRight();
	}

	/**
	 * CSWRecordRepository implementation
	 */
	@Override
	public CSWRecord getRecord(Serializable id, ElementSetName elementSetName, Namespace outputSchema) throws Exception {
		Serializable cacheId = this.getCacheId(id, elementSetName, outputSchema);
		return this.get(cacheId);
	}


	/**
	 * check if the record is present in the cache with the standard values for element set name and namespace
	 * @param id
	 * @return
	 */
	@Override
	public boolean containsRecord(String id) {
		Serializable cacheId = this.getCacheId(id, ElementSetName.FULL, Namespace.GMD);
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
	public void removeRecord(Serializable id, ElementSetName elementSetName, Namespace outputSchema) {
		Serializable cacheId = this.getCacheId(id, elementSetName, outputSchema);
		this.remove(cacheId);
	}

	@Override
	protected String getRelativePath(Serializable id) {
		return DigestUtils.md5Hex(getRecordIdFromCacheId(id).toString()).substring(0, 3);
	}

}
