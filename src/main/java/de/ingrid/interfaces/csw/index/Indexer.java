/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.index;

import java.util.List;

import de.ingrid.interfaces.csw.harvest.impl.RecordCache;

/**
 * Indexer defines the interface for document indexing implementations.

 * @author ingo@wemove.com
 */
public interface Indexer {

	/**
	 * Execute the indexing job.
	 */
	void run(List<RecordCache> recordCacheList);
}
