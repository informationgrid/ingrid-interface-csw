/**
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.harvest.impl;

import java.util.List;

import de.ingrid.interfaces.csw.Status;
import de.ingrid.interfaces.csw.harvest.Harvester;
import de.ingrid.utils.dsc.Record;

/**
 * A Harvester implementation that fetches records via an iBus.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public class IBusHarvester implements Harvester {

    /**
     * The cache used to store records.
     */
    private RecordCache cache;

    /**
     * Configure the harvester.
     * 
     * @param cache
     */
    public void configure(RecordCache cache) {
	this.cache = cache;
    }

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
    public void run() {
	// TODO Auto-generated method stub
    }

    @Override
    public List<Record> getRecords() {
	// TODO Auto-generated method stub
	return null;
    }
}
