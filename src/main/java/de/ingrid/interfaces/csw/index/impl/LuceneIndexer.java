/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.index.impl;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.interfaces.csw.harvest.impl.RecordCache;
import de.ingrid.interfaces.csw.index.Indexer;
import de.ingrid.utils.dsc.Record;

/**
 * @author ingo@wemove.com
 */
public class LuceneIndexer implements Indexer {

	final protected static Log log = LogFactory.getLog(LuceneIndexer.class);

	/**
	 * The path to the Lucene index
	 */
	private File indexPath = null;

	@Override
	public void run(List<RecordCache> recordCacheList) throws Exception {

		if (log.isDebugEnabled()) {
			log.debug("Running indexer");
		}

		// TODO open the index

		int indexedRecords = 0;
		try {
			// TODO set up javascript mapper

			// iterate over all caches an all records
			for (RecordCache cache : recordCacheList) {
				if (log.isDebugEnabled()) {
					log.debug("Indexing cache "+cache.getCachePath());
				}
				for (Serializable cacheId : cache.getCachedIds()) {
					Record record = cache.get(cacheId);
					if (log.isDebugEnabled()) {
						log.debug("Indexing record "+cacheId);
					}
					// TODO map record data using script
					indexedRecords++;
				}
			}
		}
		finally {
			// TODO close the index
		}
		if (log.isDebugEnabled()) {
			log.debug("Created index from "+indexedRecords+" records");
		}
	}

	/**
	 * Get the path to the Lucene index
	 * @return File
	 */
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
}
