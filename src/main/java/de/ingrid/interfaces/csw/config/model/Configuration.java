/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.config.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

/**
 * Configuration holds the dynamic configuration of the update job that
 * is managed in the administration application.
 * 
 * @author ingo@wemove.com
 */
public class Configuration {

	private List<HarvesterConfiguration> harvesterConfigs = new ArrayList<HarvesterConfiguration>();
	
	private String mappingScript = "idf_to_lucene.js";

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
}
