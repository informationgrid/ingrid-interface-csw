/**
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.mapping.impl;

import java.io.Serializable;

import org.w3c.dom.Document;

import de.ingrid.interfaces.csw.cache.AbstractFileCache;
import de.ingrid.interfaces.csw.domain.CSWRecord;
import de.ingrid.interfaces.csw.tools.StringUtils;

/**
 * A cache that stores CSW records.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public class CSWRecordCache extends AbstractFileCache<CSWRecord> implements
Serializable {

	private static final long serialVersionUID = CSWRecordCache.class.getName()
			.hashCode();

	@Override
	public Serializable getCacheId(CSWRecord document) {
		return document.getId() + "_" + document.getElementSetName();
	}

	@Override
	public String serializeDocument(CSWRecord document) {
		return StringUtils.nodeToString(document.getDocument());
	}

	@Override
	public CSWRecord unserializeDocument(String str) {
		CSWRecord record = null;
		try {
			Document document = StringUtils.stringToDocument(str);
			// TODO create the record instance
			// record.initialize(elementSetName, document.getFirstChild());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return record;
	}

	@Override
	public AbstractFileCache<CSWRecord> newInstance() {
		return new CSWRecordCache();
	}
}
