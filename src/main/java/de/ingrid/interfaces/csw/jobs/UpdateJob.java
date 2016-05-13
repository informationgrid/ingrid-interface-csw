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
package de.ingrid.interfaces.csw.jobs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.interfaces.csw.config.ConfigurationProvider;
import de.ingrid.interfaces.csw.config.model.Configuration;
import de.ingrid.interfaces.csw.config.model.HarvesterConfiguration;
import de.ingrid.interfaces.csw.config.model.impl.RecordCacheConfiguration;
import de.ingrid.interfaces.csw.harvest.Harvester;
import de.ingrid.interfaces.csw.harvest.impl.RecordCache;
import de.ingrid.interfaces.csw.index.IsoIndexManager;
import de.ingrid.interfaces.csw.index.StatusProvider;

/**
 * The update job.
 *
 * @author ingo herwig <ingo@wemove.com>
 */
@Service
public class UpdateJob {

    final protected static Log log = LogFactory.getLog(UpdateJob.class);

    final private static String DATE_FILENAME = "updatejob.dat";
    final private static SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * The update job configuration provider
     */
    @Autowired
    private ConfigurationProvider configurationProvider;

    @Autowired
    private IsoIndexManager indexManager;

    @Autowired
    private StatusProvider statusProvider;

    /**
     * The lock assuring that there is only one execution at a time
     */
    public static ReentrantLock executeLock = new ReentrantLock();

    /**
     * Constructor
     */
    public UpdateJob() {
    }

    /**
     * Set the configuration provider.
     *
     * @param configurationProvider
     */
    public void setConfigurationProvider(ConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }

    /**
     * Set the status provider.
     *
     * @param statusProvider
     */
    public void setStatusProvider(StatusProvider statusProvider) {
        this.statusProvider = statusProvider;
    }

    /**
     * Execute the update job. Returns true/false whether the job was executed
     * or not.
     *
     * @return Boolean
     * @throws Exception
     */
    public boolean execute() throws Exception {
        // try to acquire the lock
        if (executeLock.tryLock(0, TimeUnit.SECONDS)) {
            try {
                this.statusProvider.clear();
                this.statusProvider.addState("start_harvesting", "Start harvesting.");
                Date start = new Date();

                Date lastExecutionDate = this.getLastExecutionDate();
                log.info("Starting update job.");
                log.info("Last execution was on " + DATEFORMAT.format(lastExecutionDate));

                // get the job configuration
                if (this.configurationProvider == null) {
                    throw new Exception("No configuration provider set for the update job.");
                }
                Configuration configuration = this.configurationProvider.reloadConfiguration();

                // TODO check configuration, indexer, mapper

                // create all instances from the configuration
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
                        harvesterInstance.setStatusProvider(this.statusProvider);
                    } catch (Exception e) {
                        log.error("Error setting up harvester: " + harvesterConfig.getName(), e);
                        continue;
                    }

                    // add instances to lists
                    recordCacheList.add(cacheInstance);
                    harvesterInstanceList.add(harvesterInstance);
                }

                // fetch all records
                for (Harvester harvester : harvesterInstanceList) {
                    log.info("Run harvester " + harvester.getId());
                    try {
                        harvester.run(lastExecutionDate);
                    } catch (Exception e) {
                        log.error("Error harvesting records from harvester: " + harvester.getId(), e);
                    }
                }

                // index, transform all records
                this.indexManager.index(recordCacheList);

                // write the execution date as last operation
                // this is the start date, to make sure that the next execution
                // will fetch
                // all modified records from the job execution start on
                this.writeLastExecutionDate(start);

                // summary
                Date end = new Date();
                long diff = end.getTime() - start.getTime();
                log.info("Job executed within " + diff + " ms.");
                this.statusProvider.addState("stop_harvesting", "Harvesting finished.");
                this.statusProvider.write();
                return true;
            } finally {
                // release the lock
                executeLock.unlock();
            }
        } else {
            log.info("Can't execute update job, because it is already running.");
            return false;
        }
    }

    public void setIndexManager(IsoIndexManager indexManager) {
        this.indexManager = indexManager;
    }

    /**
     * Get the last execution date of this job. Returns 1970-01-01 00:00:00 if
     * an error occurs.
     *
     * @return Date
     */
    public Date getLastExecutionDate() {
        File dateFile = new File(DATE_FILENAME);
        BufferedReader input = null;
        try {
            input = new BufferedReader(new FileReader(dateFile));
            // we expect only one line with the date string
            String line = input.readLine();
            if (line != null) {
                Date date = DATEFORMAT.parse(line.trim());
                return date;
            }
        } catch (Exception e) {
            log.warn("Could not read from " + DATE_FILENAME + ". " + "The update job fetches all records.");
        }
        finally {
            if (input != null) {
                try {
                    input.close();
              } catch (IOException e) {
                    // ignore
              }
            }
        }
        // return the minimum date if no date could be found
        return new Date(0);
    }

    /**
     * Write the last execution date of this job.
     *
     * @param Date
     */
    public void writeLastExecutionDate(Date date) {
        File dateFile = new File(DATE_FILENAME);

        try {
            Writer output = new BufferedWriter(new FileWriter(dateFile));
            output.write(DATEFORMAT.format(date));
            output.close();
        } catch (Exception e) {
            // delete the date file to make sure we do not use a corrupted
            // version
            if (Files.exists(dateFile.toPath()))
                try {
                    Files.delete(dateFile.toPath());
                } catch (IOException e1) {
                    log.error("Could not remove " + dateFile + ".",  e1);
                }
            log.warn("Could not write to " + DATE_FILENAME + ". " + "The update job fetches all records next time.");
        }
    }
}
