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
        Resource[] resources = FileUtils.getPackageContent("classpath*:gdide_test_data/*xml");
        List<Serializable> cacheIds = new ArrayList<Serializable>();
        statusProvider.addState(this.getId() + "harvesting", "Fetch records... [" + resources.length + "]");
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

    @Override
    public void run(Date lastExecutionDate) throws Exception {
        statusProvider.addState(this.getId(), "Harvesting '" + this.getName() + "'...");
        super.run(lastExecutionDate);
    }

}
