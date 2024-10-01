/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
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
