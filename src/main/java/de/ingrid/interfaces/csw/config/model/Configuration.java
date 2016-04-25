/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.config.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import de.ingrid.interfaces.csw.config.model.communication.Communication;

/**
 * Configuration holds the dynamic configuration of the update job that
 * is managed in the administration application.
 * 
 * @author ingo@wemove.com
 */
public class Configuration {

	private List<HarvesterConfiguration> harvesterConfigs = new ArrayList<HarvesterConfiguration>();
	
	private String mappingScript = "idf_to_lucene.js";
	
	private Communication cswtCommunication = null;

	public List<HarvesterConfiguration> getHarvesterConfigurations() {
		return this.harvesterConfigs;
	}

	public void setHarvesterConfigurations(List<HarvesterConfiguration> harvester) {
		this.harvesterConfigs = harvester;
	}

	/**
	 * Create an instance from the given class configuration
	 * @param config
	 * @return T
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <T> T createInstance(ClassConfiguration config) throws Exception {
		Class<?> clazz = Class.forName(config.getClassName());
		T instance = (T)clazz.newInstance();
		BeanUtils.copyProperties(config, instance);
		return instance;
	}

    public void setMappingScript(String mappingScript) {
        this.mappingScript = mappingScript;
    }

    public String getMappingScript() {
        return mappingScript;
    }

    public Communication getCswtCommunication() {
        return cswtCommunication;
    }

    public void setCswtCommunication(Communication cswtCommunication) {
        this.cswtCommunication = cswtCommunication;
    }
}
