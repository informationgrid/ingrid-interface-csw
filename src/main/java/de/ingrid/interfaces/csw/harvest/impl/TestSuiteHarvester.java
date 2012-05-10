/**
 * 
 */
package de.ingrid.interfaces.csw.harvest.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.core.io.Resource;

import de.ingrid.interfaces.csw.tools.FileUtils;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.idf.IdfTool;

/**
 * @author joachim@wemve.com
 * 
 */
public class TestSuiteHarvester extends AbstractHarvester {

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ingrid.interfaces.csw.harvest.impl.AbstractHarvester#fetchRecords(
     * java.util.Date)
     */
    @Override
    protected List<Serializable> fetchRecords(Date lastExecutionDate) throws Exception {
        // get list of test datasets
        Resource[] resources = FileUtils.getPackageContent("classpath*:gdide_test_data/**");
        List<Serializable> cacheIds = new ArrayList<Serializable>();
        for (Resource resource : resources) {
            String iso = FileUtils.convertStreamToString(resource.getInputStream());
            Record record = IdfTool.createIdfRecord(iso, true);
            Serializable cacheId = this.cache.put(record);
            if (log.isDebugEnabled()) {
                log.debug("Fetched record " + resource.getFilename() + ". Cache id: " + cacheId);
            }
            cacheIds.add(cacheId);
        }
        return cacheIds;
    }

}
