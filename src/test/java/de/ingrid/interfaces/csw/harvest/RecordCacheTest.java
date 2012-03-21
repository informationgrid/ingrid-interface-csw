/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.harvest;

import java.io.IOException;
import java.io.Serializable;

import junit.framework.TestCase;
import de.ingrid.interfaces.csw.harvest.impl.RecordCache;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.dsc.Record;

/**
 * @author ingo@wemove.com
 */
public class RecordCacheTest extends TestCase {

	private static final String CACHE_PATH = "tmp/cache";

	public void testPut() throws IOException {
		RecordCache cache = new RecordCache();
		cache.setCachePath(CACHE_PATH);

		Record record = new Record();
		record.put(IngridDocument.DOCUMENT_ID, "TestRecord1");
		record.put(IngridDocument.DOCUMENT_CONTENT, "TestContent");
		Serializable cacheId = cache.put(record);
		assertEquals("TestRecord1", cacheId);
	}
}
