/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.config.model;

/**
 * Interface for configuration classes that describe instances.
 * 
 * @author ingo@wemove.com
 */
public interface ClassConfiguration {

	/**
	 * Get the fully qualified name of the class to configure.
	 * @return String
	 */
	public String getClassName();
	
}
