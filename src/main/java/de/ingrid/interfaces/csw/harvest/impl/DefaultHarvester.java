/**
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.harvest.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.interfaces.csw.Status;
import de.ingrid.interfaces.csw.harvest.HarvestStrategy;
import de.ingrid.interfaces.csw.harvest.Harvester;

/**
 * Default Harvester implementation.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public class DefaultHarvester implements Harvester {

	final protected static Log log = LogFactory.getLog(DefaultHarvester.class);

	/**
	 * The cache used to store records.
	 */
	private RecordCache cache;

	/**
	 * The strategy used for fetching records.
	 */
	private HarvestStrategy harvestStrategy;

	/**
	 * The filter used to select records.
	 */
	private String filter;

	@Override
	public Status getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void run(Date lastExecutionDate) throws Exception {
		log.info("Running harvester "+this.getId());

		// get cached record ids (for later removal of records that do not exist anymore)
		Set<Serializable> cachedRecordIds = this.cache.getCachedIds();

		// delegate execution to the strategy
		List<Serializable> allRecordIds = this.harvestStrategy.execute(lastExecutionDate);

		// remove deprecated records
		for (Serializable cachedRecordId : cachedRecordIds) {
			if (!allRecordIds.contains(cachedRecordId))
				this.cache.remove(cachedRecordId);
		}

		// duplicates are filtered out automatically by the cache, so there is no need for action here
		int duplicates = allRecordIds.size() - new HashSet<Serializable>(allRecordIds).size();
		log.info("Fetched "+allRecordIds.size()+" records of "+allRecordIds.size()+". Duplicates: "+duplicates);
	}

	@Override
	public void setCache(RecordCache cache) {
		this.cache = cache;
	}

	@Override
	public RecordCache getCache() {
		return this.cache;
	}

	@Override
	public void setHarvestStrategy(HarvestStrategy harvestStrategy) {
		this.harvestStrategy = harvestStrategy;
	}

	@Override
	public void setFilter(String filter) {
		this.filter = filter;
	}
}
