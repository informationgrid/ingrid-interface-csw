/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw.harvest;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public interface HarvestStrategy {

	/**
	 * Execute the update strategy and return the list of fetched document
	 * ids.
	 *
	 * @param lastExecutionDate
	 * @return List<Serializable>
	 * @throws Exception
	 */
	public abstract List<Serializable> execute(Date lastExecutionDate) throws Exception;
}