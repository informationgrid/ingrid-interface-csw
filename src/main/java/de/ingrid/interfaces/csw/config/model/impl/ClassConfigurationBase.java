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
	 * The name of the harvester, this is used for the adminstration interface.
	 * 
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



    public void setName(String name) {
        this.name = name;
    }



    public String getName() {
        return name;
    }
}
