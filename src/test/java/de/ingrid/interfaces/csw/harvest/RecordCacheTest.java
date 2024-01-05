/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.harvest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.Serializable;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import de.ingrid.interfaces.csw.harvest.impl.RecordCache;
import de.ingrid.interfaces.csw.tools.FileUtils;
import de.ingrid.interfaces.csw.tools.IdfUtils;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.idf.IdfTool;

/**
 * @author ingo@wemove.com
 */
public class RecordCacheTest {

	private static final String CACHE_PATH = "tmp/cache";

	private static final File IDF_FILE = new File("src/test/resources/idf-example.xml");

    @Test
    public void testPut() throws Exception {
		try {
    	    RecordCache cache = new RecordCache();
    		cache.setCachePath(new File(CACHE_PATH));
    		cache.removeAll();
    
    		Record record = new Record();
    		String idfContent = new Scanner(IDF_FILE).useDelimiter("\\A").next();
    		record.put(IdfTool.KEY_DATA, idfContent);
    		record.put(IdfTool.KEY_COMPRESSED, false);
    		Serializable cacheId = cache.put(record);
    		assertEquals("05F9A598-D866-11D2-AB09-00E0292DC06B", cacheId);
    		assertEquals(cache.getCachedIds().size(), 1);
		} finally {
            File tmp = new File("tmp");
            if (tmp.exists()) {
                FileUtils.deleteRecursive(tmp);
            }
		}
	}

    @Test
    public void testPutRecordWithSeparator() throws Exception {
        try {
            RecordCache cache = new RecordCache();
            cache.setCachePath(new File(CACHE_PATH));
    		cache.removeAll();
    
            Record record = new Record();
            String idfContent = new Scanner(new File("src/test/resources/idf-example_id_with_separator.xml")).useDelimiter("\\A").next();
            record.put(IdfTool.KEY_DATA, idfContent);
            record.put(IdfTool.KEY_COMPRESSED, false);
            Serializable cacheId = cache.put(record);
            assertEquals("::urn:x-wmo:md:eu.baltrad.SE::baltrad.SE::", cacheId);
            Record r = cache.get("::urn:x-wmo:md:eu.baltrad.SE::baltrad.SE::");
            Serializable id = IdfUtils.getRecordId(r);
            assertEquals(id, "::urn:x-wmo:md:eu.baltrad.SE::baltrad.SE::");
            
        } finally {
            File tmp = new File("tmp");
            if (tmp.exists()) {
                FileUtils.deleteRecursive(tmp);
            }
        }
    }
	
	
}
