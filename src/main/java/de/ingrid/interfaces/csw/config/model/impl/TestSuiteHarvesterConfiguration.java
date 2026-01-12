/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2026 wemove digital solutions GmbH
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

    @Override
    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    @Override
    public String getWorkingDirectory() {
        return workingDirectory;
    }

}
