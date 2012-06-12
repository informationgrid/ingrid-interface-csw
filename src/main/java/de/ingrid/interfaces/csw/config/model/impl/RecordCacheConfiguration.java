/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.config.model.impl;

import java.io.File;

/**
 * Configuration a
 * de.ingrid.interfaces.csw.harvest.impl.RecordCache instance.
 * 
 * @author ingo@wemove.com
 */
public class RecordCacheConfiguration extends ClassConfigurationBase {

	private File cachePath;

	public void setCachePath(File cachePath) {
		this.cachePath = cachePath;
	}

	public File getCachePath() {
		return this.cachePath;
	}

	@Override
	public String getClassName() {
		return de.ingrid.interfaces.csw.harvest.impl.RecordCache.class.getName();
	}
}
