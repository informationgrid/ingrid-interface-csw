/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.config.model.impl;

import de.ingrid.interfaces.csw.config.model.ClassConfiguration;

/**
 * Base class for class configurations. Subclasses provide
 * the name of the configured class in order to create instances
 * of that class.
 * 
 * @author ingo@wemove.com
 */
public abstract class ClassConfigurationBase implements ClassConfiguration {

	/**
	 * The class to be configured. XStream accesses this
	 * field directly.
	 */
	@SuppressWarnings("unused")
	private String className;

	/**
	 * The human readable name of the configured class. This is used for the
	 * administration interface.
	 */
	private String name;

	/**
	 * Constructor
	 */
	public ClassConfigurationBase() {
		// set the class name in order to be serialized
		// by xstream
		this.className = this.getClassName();
	}

	/**
	 * Set the name of the configured class.
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the name of the configured class.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}
}
