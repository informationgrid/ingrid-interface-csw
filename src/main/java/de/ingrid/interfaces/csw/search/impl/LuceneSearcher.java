/**
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.search.impl;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import de.ingrid.interfaces.csw.domain.CSWRecord;
import de.ingrid.interfaces.csw.domain.query.CSWQuery;
import de.ingrid.interfaces.csw.search.CSWRecordRepository;
import de.ingrid.interfaces.csw.search.Searcher;

/**
 * A Searcher implementation that searches in a Lucene index.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public class LuceneSearcher implements Searcher {

	final protected static Log log = LogFactory.getLog(LuceneSearcher.class);

	/**
	 * The path to the Lucene index
	 */
	private File indexPath;

	/**
	 * The Lucene index searcher
	 */
	protected IndexSearcher indexSearcher;

	/**
	 * The started state
	 */
	protected boolean isStarted = false;

	/**
	 * The repository where the CSW records are retrieved from
	 */
	private CSWRecordRepository recordRepository;

	@Override
	public void start() throws Exception {
		if (this.isStarted) {
			this.stop();
		}
		log.info("Open search index: "+this.indexPath);
		Directory indexDir = new SimpleFSDirectory(this.indexPath);
		this.indexSearcher = new IndexSearcher(IndexReader.open(indexDir, true));
		log.info("Number of docs: "+this.indexSearcher.maxDoc());
		this.isStarted = true;
	}

	@Override
	public void stop() throws Exception {
		if (this.indexSearcher != null) {
			log.info("Close search index: "+this.indexPath);
			this.indexSearcher.close();
			this.isStarted = false;
		}
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
