/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.config.model;

import de.ingrid.interfaces.csw.config.model.impl.RecordCacheConfiguration;

/**
 * Interface for harvester configurations.
 * 
 * @author ingo@wemove.com
 */
public interface HarvesterConfiguration extends ClassConfiguration {

	RecordCacheConfiguration getCacheConfiguration();
}
