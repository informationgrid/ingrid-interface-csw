/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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
