/**
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.search.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import de.ingrid.interfaces.csw.domain.CSWRecord;
import de.ingrid.interfaces.csw.domain.constants.ElementSetName;
import de.ingrid.interfaces.csw.domain.filter.FilterParser;
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
	 * The FilterParser instance that converts csw queries to InGrid queries
	 */
	private FilterParser filterParser;

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
	public List<CSWRecord> search(CSWQuery query) throws Exception {
		if (this.filterParser == null) {
			throw new RuntimeException("LuceneSearcher is not configured properly: filterParser is not set.");
		}
		if (this.recordRepository == null) {
			throw new RuntimeException("LuceneSearcher is not configured properly: recordRepository is not set.");
		}
		if (this.indexSearcher == null) {
			throw new RuntimeException("LuceneSearcher is not started.");
		}

		List<CSWRecord> result = new ArrayList<CSWRecord>();
		ElementSetName elementSetName = query.getElementSetName();
		if (query.getIds() != null) {
			// there are records specified by id. So we can search in the record repository
			// directly
			for (String id : query.getIds()) {
				if (this.recordRepository.containsRecord(id)) {
					CSWRecord record = this.recordRepository.getRecord(id, elementSetName);
					result.add(record);
				}
			}
		}
		else {
			// use the query constraints to search for records in the Lucene index
			Query luceneQuery = this.filterParser.parse(query.getConstraint());
			if (luceneQuery == null) {
				throw new RuntimeException("Error parsing query constraint: Lucene query is null");
			}
			TopScoreDocCollector collector = TopScoreDocCollector.create(this.indexSearcher.maxDoc(), true);
			this.indexSearcher.search(luceneQuery, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
			log.debug("Found " + hits.length + " hits.");

			// get the CSWRecord for each document found in the index
			for (ScoreDoc hit : hits) {
				Document doc = this.indexSearcher.doc(hit.doc);
				String recordId = doc.get("recordId");
				CSWRecord record = this.recordRepository.getRecord(recordId, elementSetName);
				result.add(record);
			}
		}
		return result;
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

	/**
	 * Set the FilterParser instance
	 * @param filterParser
	 */
	public void setFilterParser(FilterParser filterParser) {
		this.filterParser = filterParser;
	}

	@Override
	public void setRecordRepository(CSWRecordRepository recordRepository) {
		this.recordRepository = recordRepository;
	}
}