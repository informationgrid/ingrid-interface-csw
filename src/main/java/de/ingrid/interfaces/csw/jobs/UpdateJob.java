/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.jobs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
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
import de.ingrid.interfaces.csw.index.Indexer;
import de.ingrid.interfaces.csw.mapping.CSWRecordMapper;
import de.ingrid.interfaces.csw.search.CSWRecordRepository;
import de.ingrid.interfaces.csw.search.Searcher;
import de.ingrid.interfaces.csw.tools.FileUtils;

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

    /**
     * The Indexer instance
     */
    @Autowired
    private Indexer indexer;

    /**
     * The CSWRecordMapper instance
     */
    @Autowired
    private CSWRecordMapper cswRecordMapper;

    /**
     * The Searcher instance
     */
    @Autowired
    private Searcher searcher;

    /**
     * The lock assuring that there is only one execution at a time
     */
    private static ReentrantLock executeLock = new ReentrantLock();

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
     * Set the indexer.
     * 
     * @param indexer
     */
    public void setIndexer(Indexer indexer) {
        this.indexer = indexer;
    }

    /**
     * Set the record mapper.
     * 
     * @param cswRecordMapper
     */
    public void setCswRecordMapper(CSWRecordMapper cswRecordMapper) {
        this.cswRecordMapper = cswRecordMapper;
    }

    /**
     * Set the searcher.
     * 
     * @param searcher
     */
    public void setSearcher(Searcher searcher) {
        this.searcher = searcher;
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
                for (HarvesterConfiguration harvesterConfig : harvesterConfigs) {

                    // set up the cache
                    RecordCacheConfiguration cacheConfig = harvesterConfig.getCacheConfiguration();
                    RecordCache cacheInstance = configuration.createInstance(cacheConfig);

                    // set up the harvester
                    Harvester harvesterInstance = configuration.createInstance(harvesterConfig);
                    harvesterInstance.setCache(cacheInstance);

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

                // index all records into a temporary directory and switch to
                // live later
                
                log.info("Indexing " + recordCacheList.size() + " idf documents.");
                
                this.indexer.run(recordCacheList);

                log.info("Transforming " + recordCacheList.size() + " idf documents into ISO element sets full, summary, brief.");
                // map ingrid records to csw records
                this.cswRecordMapper.run(recordCacheList);

                CSWRecordRepository cswRecordRepo = this.cswRecordMapper.getRecordRepository();

                log.info("Stop the searcher instance.");
                
                // stop the searcher instance to access index in filesystem
                this.searcher.stop();

                // move temporary index to live location
                if (log.isDebugEnabled()) {
                    log.debug("Remove old index: " + this.searcher.getIndexPath().getAbsolutePath());
                }
                if (this.searcher.getIndexPath().exists()) {
                    FileUtils.deleteRecursive(this.searcher.getIndexPath());
                }
                if (log.isDebugEnabled()) {
                    log.debug("Rename new index: " + this.indexer.getIndexConfigPath().getAbsolutePath() + " to "
                            + this.searcher.getIndexPath().getAbsolutePath());
                }
                if (!this.indexer.getIndexConfigPath().renameTo(this.searcher.getIndexPath())) {
                    log.warn("Could not renam old index: " + this.indexer.getIndexConfigPath().getAbsolutePath() + " to "
                            + this.searcher.getIndexPath().getAbsolutePath());
                }

                // set the updated record repository on the searcher
                this.searcher.setRecordRepository(cswRecordRepo);

                log.info("Start the searcher instance.");

                // restart the searcher
                this.searcher.start();

                // write the execution date as last operation
                // this is the start date, to make sure that the next execution
                // will fetch
                // all modified records from the job execution start on
                this.writeLastExecutionDate(start);

                // summary
                Date end = new Date();
                long diff = end.getTime() - start.getTime();
                log.info("Job executed within " + diff + " ms.");
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

    /**
     * Get the last execution date of this job. Returns 1970-01-01 00:00:00 if
     * an error occurs.
     * 
     * @return Date
     */
    public Date getLastExecutionDate() {
        File dateFile = new File(DATE_FILENAME);

        try {
            BufferedReader input = new BufferedReader(new FileReader(dateFile));
            // we expect only one line with the date string
            String line = input.readLine();
            if (line != null) {
                Date date = DATEFORMAT.parse(line.trim());
                return date;
            }
        } catch (Exception e) {
            log.warn("Could not read from " + DATE_FILENAME + ". " + "The update job fetches all records.");
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
            if (dateFile.exists())
                dateFile.delete();
            log.warn("Could not write to " + DATE_FILENAME + ". " + "The update job fetches all records next time.");
        }
    }

    private void delete(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isDirectory()) {
                    delete(file);
                }
                if (!file.delete()) {
                    log.warn("Unable to delete file: " + file.getAbsolutePath());
                }
            }
        }
        if (folder.exists() && !folder.delete()) {
            log.warn("Unable to delete folder: " + folder.getAbsolutePath());

        }
    }
}
