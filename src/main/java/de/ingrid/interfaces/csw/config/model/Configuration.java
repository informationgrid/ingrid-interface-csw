/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.config.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration holds the dynamic configuration of the update job that
 * is managed in the administration application.
 * 
 * @author ingo@wemove.com
 */
public class Configuration {

	private List<IBusHarvester> harvesters = new ArrayList<IBusHarvester>();

	public List<IBusHarvester> getHarvesters() {
		return this.harvesters;
	}

	public void setHarvesters(List<IBusHarvester> harvester) {
		this.harvesters = harvester;
	}
}
