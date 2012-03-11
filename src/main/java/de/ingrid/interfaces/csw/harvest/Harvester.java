/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.harvest;

import java.util.List;

import de.ingrid.interfaces.csw.Monitorable;
import de.ingrid.utils.dsc.Record;

/**
 * Harvester defines the interface for document harvesting implementations.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public interface Harvester extends Monitorable {

    /**
     * Get the identifier of the harvester.
     */
    public String getId();

    /**
     * Execute the harvesting job.
     */
    public void run();

    /**
     * Get all records fetched by the harvester.
     * 
     * @return List<Record>
     */
    public List<Record> getRecords();
}
