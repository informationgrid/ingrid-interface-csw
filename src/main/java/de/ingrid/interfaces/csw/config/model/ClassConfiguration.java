/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.config.model;

/**
 * Base class for class configurations. Subclasses provide
 * the name of the configured class in order to create instances
 * of that class.
 * 
 * @author ingo@wemove.com
 */
public abstract class ClassConfiguration {

	/**
	 * The class to be configured. XStream accesses this
	 * field directly.
	 */
	private String className;

	/**
	 * Constructor
	 */
	public ClassConfiguration() {
		// set the class name in order to be serialized
		// by xstream
		this.className = this.getClassName();
	}

	/**
	 * Get the name of the class to configure.
	 * @return String
	 */
	public abstract String getClassName();
}
