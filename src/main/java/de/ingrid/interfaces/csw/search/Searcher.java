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
package de.ingrid.interfaces.csw.search;

import java.io.File;

import de.ingrid.interfaces.csw.domain.query.CSWQuery;

/**
 * Interface for CSW search implementations.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public interface Searcher {

	/**
	 * Start the searcher.
	 * @throws Exception
	 */
	public void start() throws Exception;

	/**
	 * Stop the searcher.
	 * @throws Exception
	 */
	public void stop() throws Exception;

    /**
     * Refresh the searcher.
     * @throws Exception
     */
    public void refresh() throws Exception;
	
	
	/**
	 * Search for CSW records using the given query.
	 * 
	 * @param query
	 * @return CSWRecordResults
	 * @throws Exception
	 */
	public CSWRecordResults search(CSWQuery query) throws Exception;

	/**
	 * Get the path to the Lucene index
	 * @return File
	 */
	public File getIndexPath();

	/**
	 * Set the repository where the CSW records are retrieved from
	 * @param recordRepository
	 */
	public void setRecordRepository(CSWRecordRepository recordRepository);
}
