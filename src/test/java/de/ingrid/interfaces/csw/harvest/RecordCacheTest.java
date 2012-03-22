/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.harvest;

import java.io.File;
import java.io.Serializable;
import java.util.Scanner;

import junit.framework.TestCase;
import de.ingrid.interfaces.csw.harvest.impl.RecordCache;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.idf.IdfTool;

/**
 * @author ingo@wemove.com
 */
public class RecordCacheTest extends TestCase {

	private static final String CACHE_PATH = "tmp/cache";

	private static final File IDF_FILE = new File("src/test/resources/idf-example.xml");


	public void testPut() throws Exception {
		RecordCache cache = new RecordCache();
		cache.setCachePath(CACHE_PATH);

		Record record = new Record();
		String idfContent = new Scanner(IDF_FILE).useDelimiter("\\A").next();
		record.put(IdfTool.KEY_DATA, idfContent);
		record.put(IdfTool.KEY_COMPRESSED, false);
		Serializable cacheId = cache.put(record);
		assertEquals("05F9A598-D866-11D2-AB09-00E0292DC06B", cacheId);
	}
}
