/**
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.search.impl;

import java.io.File;
import java.util.List;

import de.ingrid.interfaces.csw.domain.CSWRecord;
import de.ingrid.interfaces.csw.search.CSWQuery;
import de.ingrid.interfaces.csw.search.CSWRecordRepository;
import de.ingrid.interfaces.csw.search.Searcher;

/**
 * A Searcher implementation that searches in a lucene index.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public class LuceneSearcher implements Searcher {

	/**
	 * The path to the lucene index
	 */
	private File indexPath;

	/**
	 * The repository where the CSW records are retrieved from
	 */
	private CSWRecordRepository recordRepository;

	@Override
	public void start() {
		// TODO Auto-generated method stub
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
	}

	@Override
	public List<CSWRecord> search(CSWQuery query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getIndexPath() {
		return this.indexPath;
	}

	/**
	 * Set the path to the Lucene index
	 * @param indexPath
	 */
	public void setIndexPath(File indexPath) {
		this.indexPath = indexPath;
	}

	@Override
	public void setRecordRepository(CSWRecordRepository recordRepository) {
		this.recordRepository = recordRepository;
	}
}
