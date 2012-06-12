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
