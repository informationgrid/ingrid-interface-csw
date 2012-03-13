/**
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.harvest.impl;

import java.io.Serializable;

import de.ingrid.interfaces.csw.cache.AbstractFileCache;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.idf.IdfTool;

/**
 * A cache that stores InGrid records.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public class RecordCache extends AbstractFileCache<Record> implements
Serializable {

	private static final long serialVersionUID = RecordCache.class.getName()
			.hashCode();

	@Override
	public Serializable getCacheId(Record document) {
		return document.getId();
	}

	@Override
	public String serializeDocument(Record document) {
		return IdfTool.getIdfDataFromRecord(document);
	}

	@Override
	public Record unserializeDocument(String str) {
		return IdfTool.createIdfRecord(str, true);
	}

	@Override
	public AbstractFileCache<Record> newInstance() {
		return new RecordCache();
	}
}
