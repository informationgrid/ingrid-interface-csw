/**
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.search;

import java.io.File;
import java.util.List;

import de.ingrid.interfaces.csw.domain.CSWRecord;

/**
 * Interface for CSW search implementations.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public interface Searcher {

	/**
	 * Start the searcher.
	 */
	public void start();

	/**
	 * Stop the searcher.
	 */
	public void stop();

	/**
	 * Search for CSW records using the given query.
	 * 
	 * @param query
	 * @return List<CSWRecord>
	 */
	public List<CSWRecord> search(CSWQuery query);

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
