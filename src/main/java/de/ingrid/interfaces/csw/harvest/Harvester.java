/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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
/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.harvest;

import java.util.Date;

import de.ingrid.interfaces.csw.Monitorable;
import de.ingrid.interfaces.csw.harvest.impl.RecordCache;
import de.ingrid.utils.statusprovider.StatusProvider;

/**
 * Harvester defines the interface for document harvesting implementations.
 * The documents are fetched into a cache by using the configured
 * HarvestStrategy. A filter string may be given to get only a subset of
 * all documents.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public interface Harvester extends Monitorable {

	/**
	 * Get the identifier of the harvester.
	 */
	public String getId();

	
    /**
     * Get the name of the harvester.
     */
    public String getName();
	
	/**
	 * Execute the harvesting job.
	 * 
	 * @param lastExecutionDate
	 * @throws Exception
	 */
	public void run(Date lastExecutionDate) throws Exception;

	/**
	 * Set the cache for the fetched records.
	 * 
	 * @param cache
	 */
	public void setCache(RecordCache cache);

	/**
	 * Get the cache containing the records fetched by the harvester.
	 * 
	 * @return RecordCache
	 */
	public RecordCache getCache();
	
	/**
	 * Set the {@link StatusProvider} to be used.
	 * 
	 * @param statusProvider
	 */
	public void setStatusProvider(StatusProvider statusProvider);
}
