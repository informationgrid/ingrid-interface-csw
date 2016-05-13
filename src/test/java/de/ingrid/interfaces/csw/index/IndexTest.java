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
/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.index;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.apache.lucene.analysis.Analyzer;

import de.ingrid.interfaces.csw.config.ConfigurationProvider;
import de.ingrid.interfaces.csw.config.model.Configuration;
import de.ingrid.interfaces.csw.config.model.HarvesterConfiguration;
import de.ingrid.interfaces.csw.config.model.impl.RecordCacheConfiguration;
import de.ingrid.interfaces.csw.harvest.Harvester;
import de.ingrid.interfaces.csw.harvest.impl.RecordCache;
import de.ingrid.interfaces.csw.index.impl.IngridGeoTKLuceneIndexer;
import de.ingrid.interfaces.csw.index.impl.ScriptedIDFRecordLuceneMapper;
import de.ingrid.interfaces.csw.tools.LuceneTools;

/**
 * @author ingo@wemove.com
 */
public class IndexTest extends TestCase {

    private static final File CONFIGURATION_FILE_2 = new File("src/test/resources/config-updatejobtest-2iplugs.xml");

    private static final File MAPPING_FILE = new File("src/main/resources/idf_to_lucene.js");

   
    /**
     * Check if MappingScript runs without any exceptions
     * @throws Exception
     */
    public void testMapping() throws Exception {
        // set up indexer
        ScriptedIDFRecordLuceneMapper recordMapper = new ScriptedIDFRecordLuceneMapper();
        recordMapper.setMappingScript(MAPPING_FILE);
        recordMapper.init();
        
        // create all instances from the configuration
        ConfigurationProvider configProvider = new ConfigurationProvider();
        configProvider.setConfigurationFile( CONFIGURATION_FILE_2 );

        HashMap<String, Object> utils = new HashMap<String, Object>();
        LuceneTools luceneTools = new LuceneTools();
        Analyzer myAnalyzer = luceneTools.createAnalyzer();
        utils.put("geometryMapper", new IngridGeoTKLuceneIndexer("", configProvider.getNewIndexPath(), myAnalyzer, null));
        recordMapper.setLuceneTools( luceneTools );
        
        Configuration configuration = configProvider.reloadConfiguration();
        List<RecordCache> recordCacheList = new ArrayList<RecordCache>();
        List<Harvester> harvesterInstanceList = new ArrayList<Harvester>();
        List<HarvesterConfiguration> harvesterConfigs = configuration.getHarvesterConfigurations();
        RecordCache cacheInstance = null;
        Harvester harvesterInstance = null;
        for (HarvesterConfiguration harvesterConfig : harvesterConfigs) {

            try {
                // set up the cache
                RecordCacheConfiguration cacheConfig = harvesterConfig.getCacheConfiguration();
                cacheInstance = configuration.createInstance(cacheConfig);

                // set up the harvester
                harvesterInstance = configuration.createInstance(harvesterConfig);
                harvesterInstance.setCache(cacheInstance);
            } catch (Exception e) {
                throw(e);
            }

            // add instances to lists
            recordCacheList.add(cacheInstance);
            harvesterInstanceList.add(harvesterInstance);
        }
        
        int docId = 0;
        for (RecordCache record : recordCacheList) {
            for (Serializable id : record.getCachedIds()) {
                utils.put("docid", docId++);
                recordMapper.map( record.get( id ), utils );
            }
        }
    }


}
