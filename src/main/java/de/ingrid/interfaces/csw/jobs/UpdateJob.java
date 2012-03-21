/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.jobs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Serializable;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

import de.ingrid.interfaces.csw.config.ConfigurationProvider;
import de.ingrid.interfaces.csw.config.model.Configuration;
import de.ingrid.interfaces.csw.config.model.HarvesterConfiguration;
import de.ingrid.interfaces.csw.config.model.impl.RecordCacheConfiguration;
import de.ingrid.interfaces.csw.domain.CSWRecord;
import de.ingrid.interfaces.csw.domain.enums.ElementSetName;
import de.ingrid.interfaces.csw.harvest.Harvester;
import de.ingrid.interfaces.csw.harvest.impl.RecordCache;
import de.ingrid.interfaces.csw.index.Indexer;
import de.ingrid.interfaces.csw.index.impl.LuceneIndexer;
import de.ingrid.interfaces.csw.mapping.CSWRecordMapper;
import de.ingrid.interfaces.csw.mapping.impl.CSWRecordCache;
import de.ingrid.interfaces.csw.mapping.impl.XsltMapper;
import de.ingrid.interfaces.csw.search.CSWRecordRepository;
import de.ingrid.interfaces.csw.search.Searcher;
import de.ingrid.interfaces.csw.search.impl.CachedRecordRepository;
import de.ingrid.interfaces.csw.search.impl.LuceneSearcher;

/**
 * The update job.
 * @author ingo herwig <ingo@wemove.com>
 */
public class UpdateJob {

	final protected static Log log = LogFactory.getLog(UpdateJob.class);
	final private static String DATE_FILENAME = "updatejob.dat";
	final private static SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private ConfigurationProvider configurationProvider;

	/**
	 * Constructor
	 */
	public UpdateJob() {}

	/**
	 * Set the configuration provider.
	 * @param configurationProvider
	 */
	public void setConfigurationProvider(ConfigurationProvider configurationProvider) {
		this.configurationProvider = configurationProvider;
	}

	/**
	 * Execute the update job.
	 * @throws Exception
	 */
	public void execute() throws Exception {
		Date start = new Date();

		Date lastExecutionDate = this.getLastExecutionDate();
		log.info("Starting update job");
		log.info("Last execution was on "+DATEFORMAT.format(lastExecutionDate));

		// get the job configuration
		if (this.configurationProvider == null) {
			throw new Exception("No configuration provider set for the update job.");
		}
		Configuration configuration = this.configurationProvider.getConfiguration();

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
			harvester.run(lastExecutionDate);
			recordCacheList.add(harvester.getCache());
		}

		// index all records
		// TODO index into a tmp directory and switch to live later
		Indexer indexer = new LuceneIndexer();
		indexer.run(recordCacheList);

		// map ingrid records to csw records
		CSWRecordCache cswCache = new CSWRecordCache();
		CSWRecordMapper mapper = new XsltMapper();
		for (RecordCache recordCache : recordCacheList) {
			for (Serializable recordId : recordCache.getCachedIds()) {
				for (ElementSetName elementSetName : ElementSetName.values()) {
					Node mappedRecord = mapper.map(recordCache.get(recordId), elementSetName);
					CSWRecord cswRecord = new CSWRecord(elementSetName, mappedRecord);
					cswCache.put(cswRecord);
				}
			}
		}

		// TODO get index path from config
		File indexPath = null;

		CSWRecordRepository cswRecordRepo = new CachedRecordRepository(cswCache);
		// TODO get running searcher instance
		Searcher searcher = new LuceneSearcher(indexPath, cswRecordRepo);
		searcher.stop();
		// TODO copy tmp index to live location
		searcher.start();

		// write the execution date as last operation
		// this is the start date, to make sure that the next execution will fetch
		// all modified records from the job execution start on
		this.writeLastExecutionDate(start);

		// summary
		Date end = new Date();
		long diff = end.getTime()-start.getTime();
		log.info("Job executed within "+diff+" ms.");
	}

	/**
	 * Get the last execution date of this job. Returns 1970-01-01 00:00:00 if an error occurs.
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
		}
		catch (Exception e) {
			log.warn("Could not read from "+DATE_FILENAME+". " +
					"The update job fetches all records.");
		}
		// return the minimum date if no date could be found
		return new Date(0);
	}

	/**
	 * Write the last execution date of this job.
	 * @param Date
	 */
	public void writeLastExecutionDate(Date date) {
		File dateFile = new File(DATE_FILENAME);

		try {
			Writer output = new BufferedWriter(new FileWriter(dateFile));
			output.write(DATEFORMAT.format(date));
			output.close();
		}
		catch (Exception e) {
			// delete the date file to make sure we do not use a corrupted version
			if (dateFile.exists())
				dateFile.delete();
			log.warn("Could not write to "+DATE_FILENAME+". " +
					"The update job fetches all records next time.");
		}
	}
}
