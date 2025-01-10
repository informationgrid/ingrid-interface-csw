/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
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
/**
 * 
 */
package de.ingrid.interfaces.csw.admin.command;

import org.springframework.beans.BeanUtils;

import de.ingrid.interfaces.csw.config.model.RequestDefinition;

/**
 * @author joachim
 * 
 */
public class RequestDefinitionCommandObject extends RequestDefinition {

    public RequestDefinitionCommandObject(RequestDefinition rd) {
        super();
        BeanUtils.copyProperties(rd, this);
    }

    private String dataSourceName;

    private Boolean isCurrentlyRegistered;
    
    private Integer indexedRecords;

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public Boolean getIsCurrentlyRegistered() {
        return isCurrentlyRegistered;
    }

    public void setIsCurrentlyRegistered(Boolean isCurrentlyRegistered) {
        this.isCurrentlyRegistered = isCurrentlyRegistered;
    }

    public void setIndexedRecords(Integer indexedRecords) {
        this.indexedRecords = indexedRecords;
    }

    public Integer getIndexedRecords() {
        return indexedRecords;
    }

}
