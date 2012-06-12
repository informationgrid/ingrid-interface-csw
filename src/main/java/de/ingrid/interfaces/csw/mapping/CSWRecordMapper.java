/**
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.mapping;

import java.util.List;

import de.ingrid.interfaces.csw.harvest.impl.RecordCache;
import de.ingrid.interfaces.csw.search.CSWRecordRepository;

/**
 * Interface for classes that map IDF records to CSW records.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public interface CSWRecordMapper {

	/**
	 * Execute the mapping job.
	 * @throws Exception
	 */
	void run(List<RecordCache> recordCacheList) throws Exception;


	/**
	 * Get the repository containing the mapped records.
	 * 
	 * @return CSWRecordRepository
	 */
	public CSWRecordRepository getRecordRepository();
}
