/**
 * 
 */
package de.ingrid.interfaces.csw.admin.command;

import org.springframework.beans.BeanUtils;

import de.ingrid.interfaces.csw.config.model.HarvesterConfiguration;
import de.ingrid.interfaces.csw.config.model.impl.TestSuiteHarvesterConfiguration;

/**
 * @author joachim@wemove.com
 * 
 */
public class TestSuiteHarvesterCommandObject extends TestSuiteHarvesterConfiguration implements Identificable {

    private Integer id;

    public TestSuiteHarvesterCommandObject() {
        super();
    }

    public TestSuiteHarvesterCommandObject(HarvesterConfiguration config) {
        super();
        BeanUtils.copyProperties(config, this);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

}
