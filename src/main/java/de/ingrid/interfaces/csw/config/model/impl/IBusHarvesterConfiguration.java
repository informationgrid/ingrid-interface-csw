/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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

import java.util.List;

import de.ingrid.interfaces.csw.config.model.HarvesterConfiguration;
import de.ingrid.interfaces.csw.config.model.RequestDefinition;

/**
 * Configuration a
 * de.ingrid.interfaces.csw.harvest.impl.IBusHarvester instance.
 * 
 * @author ingo@wemove.com
 */
public class IBusHarvesterConfiguration extends ClassConfigurationBase implements HarvesterConfiguration {

	/**
	 * The communication xml file path
	 */
	private String communicationXml;

    /**
     * The working directory of the harvester
     */
    private String workingDirectory;
	
	/**
	 * List of request definitions
	 */
	private List<RequestDefinition> requestDefinitions;
	
	/**
	 * The record cache configuration
	 */
	private RecordCacheConfiguration cache;

	public String getCommunicationXml() {
		return this.communicationXml;
	}

	public void setCommunicationXml(String communicationXml) {
		this.communicationXml = communicationXml;
	}

	public List<RequestDefinition> getRequestDefinitions() {
		return this.requestDefinitions;
	}

	public void setRequestDefinitions(List<RequestDefinition> requestDefinitions) {
		this.requestDefinitions = requestDefinitions;
	}

	@Override
	public RecordCacheConfiguration getCacheConfiguration() {
		return this.cache;
	}

	public void setCacheConfiguration(RecordCacheConfiguration cache2) {
		this.cache = cache2;
	}

	@Override
	public String getClassName() {
		return de.ingrid.interfaces.csw.harvest.ibus.IBusHarvester.class.getName();
	}

    @Override
    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    @Override
    public String getWorkingDirectory() {
        return workingDirectory;
    }

}
