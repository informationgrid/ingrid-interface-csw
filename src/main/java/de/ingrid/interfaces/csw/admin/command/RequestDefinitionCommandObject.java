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

}
