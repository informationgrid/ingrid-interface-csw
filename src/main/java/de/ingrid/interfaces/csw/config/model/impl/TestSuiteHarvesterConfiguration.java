/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.config.model.impl;

import de.ingrid.interfaces.csw.config.model.HarvesterConfiguration;

/**
 * Configuration a de.ingrid.interfaces.csw.harvest.impl.TestSuiteHarvester
 * instance.
 * 
 * @author joachim@wemove.com
 */
public class TestSuiteHarvesterConfiguration extends ClassConfigurationBase implements HarvesterConfiguration {

    /**
     * The working directory of the harvester
     */
    private String workingDirectory;

    /**
     * The record cache configuration
     */
    private RecordCacheConfiguration cache;

    @Override
    public RecordCacheConfiguration getCacheConfiguration() {
        return this.cache;
    }

    public void setCacheConfiguration(RecordCacheConfiguration cache2) {
        this.cache = cache2;
    }

    @Override
    public String getClassName() {
        return de.ingrid.interfaces.csw.harvest.impl.TestSuiteHarvester.class.getName();
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    @Override
    public String getWorkingDirectory() {
        return workingDirectory;
    }

}
