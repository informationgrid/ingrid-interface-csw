/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.admin.command;

import org.springframework.beans.BeanUtils;

import de.ingrid.interfaces.csw.config.model.HarvesterConfiguration;
import de.ingrid.interfaces.csw.config.model.impl.ClassConfigurationBase;
import de.ingrid.interfaces.csw.config.model.impl.RecordCacheConfiguration;

public class HarvesterCommandObject extends ClassConfigurationBase implements HarvesterConfiguration {

    private Integer id;
    
    private String workingDirectory;
    
    private RecordCacheConfiguration cacheConfiguration;
    
    
    public HarvesterCommandObject() {
        super();
    };
    
    public HarvesterCommandObject(HarvesterConfiguration config) {
        super();
        BeanUtils.copyProperties(config, this);
    }
    
    private String name;

    private String type;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public RecordCacheConfiguration getCacheConfiguration() {
        return cacheConfiguration;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getClassName() {
        return de.ingrid.interfaces.csw.admin.command.HarvesterCommandObject.class.getName();
    }

    @Override
    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
        
    }

}
