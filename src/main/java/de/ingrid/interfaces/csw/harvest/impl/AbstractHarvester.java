/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/**
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.harvest.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.interfaces.csw.Status;
import de.ingrid.interfaces.csw.harvest.Harvester;
import de.ingrid.utils.statusprovider.StatusProvider;

/**
 * Default Harvester implementation.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public abstract class AbstractHarvester implements Harvester {

    final protected static Log log = LogFactory.getLog(AbstractHarvester.class);

    /**
     * The cache used to store records.
     */
    protected RecordCache cache;

    protected String name;

    protected StatusProvider statusProvider;

    @Override
    public Status getStatus() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getId() {
        return this.getClass().getName() + ":" + this.cache.getCachePath();
    }

    @Override
    public void run(Date lastExecutionDate) throws Exception {
        log.info("Running harvester " + this.getId());
        if (this.cache == null) {
            throw new RuntimeException("Harvester is not configured properly: cache not set.");
        }

        // get cached record ids (for later removal of records that do not exist
        // anymore)
        Set<Serializable> cachedRecordIds = this.cache.getCachedIds();

        // delegate execution to specialized method
        List<Serializable> allRecordIds = new ArrayList<Serializable>();
        try {
            allRecordIds = this.fetchRecords(lastExecutionDate);
        } catch (Exception e) {
            log.error("Error fetching records from harvester: " + this.getId(), e);
        }

        // remove deprecated records
        for (Serializable cachedRecordId : cachedRecordIds) {
            if (!allRecordIds.contains(cachedRecordId))
                this.cache.remove(cachedRecordId);
        }

        // duplicates are filtered out automatically by the cache, so there is
        // no need for action here
        int duplicates = allRecordIds.size() - new HashSet<Serializable>(allRecordIds).size();
        log.info("Fetched " + allRecordIds.size() + " records. Duplicates: " + duplicates);
        if (duplicates > 0)  {
            statusProvider.addState(this.getId() + "_duplicates", "Remove " + duplicates + " duplicates.");
        }
        allRecordIds.clear();
        cachedRecordIds.clear();
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
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatusProvider(StatusProvider statusProvider) {
        this.statusProvider = statusProvider;
    }

    /**
     * Actually fetch the records from the source and return the list of fetched
     * record ids.
     * 
     * @param lastExecutionDate
     * @return List<Serializable>
     * @throws Exception
     */
    protected abstract List<Serializable> fetchRecords(Date lastExecutionDate) throws Exception;
}
