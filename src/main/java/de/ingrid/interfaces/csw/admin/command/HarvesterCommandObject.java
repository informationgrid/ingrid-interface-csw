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
