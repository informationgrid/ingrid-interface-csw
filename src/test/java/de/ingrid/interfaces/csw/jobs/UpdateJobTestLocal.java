/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.jobs;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

import junit.framework.TestCase;
import de.ingrid.interfaces.csw.config.ConfigurationProvider;
import de.ingrid.interfaces.csw.index.impl.LuceneIndexer;
import de.ingrid.interfaces.csw.mapping.impl.CSWRecordCache;
import de.ingrid.interfaces.csw.mapping.impl.XsltMapper;
import de.ingrid.interfaces.csw.search.impl.LuceneSearcher;

/**
 * @author ingo@wemove.com
 */
public class UpdateJobTestLocal extends TestCase {

	private static final File CONFIGURATION_FILE_1 = new File("src/test/resources/config-updatejobtest-1iplug.xml");
	private static final File CONFIGURATION_FILE_2 = new File("src/test/resources/config-updatejobtest-2iplugs.xml");

	private static final File MAPPING_FILE = new File("src/main/resources/idf_to_lucene-igc-1.0.3.js");

	private static final String CSW_CACHE_PATH = "tmp/cache/csw";
	private static final String TMP_INDEX_PATH = "tmp/index/tmp";
	private static final String LIVE_INDEX_PATH = "tmp/index/live";

	public void testSimple() throws Exception {
		UpdateJob job = this.createJob(CONFIGURATION_FILE_2);

		boolean result = job.execute();
		assertTrue(result);
	}

	public void testConcurrentExecution() throws Exception {
		ThreadPerTaskExecutor executor = new ThreadPerTaskExecutor();

		FutureTask<Boolean> execution1 = new FutureTask<Boolean>(new JobRunner(CONFIGURATION_FILE_1, "Job1"));
		FutureTask<Boolean> execution2 = new FutureTask<Boolean>(new JobRunner(CONFIGURATION_FILE_1, "Job2"));

		executor.execute(execution1);
		executor.execute(execution2);

		assertTrue(execution1.get());
		assertFalse(execution2.get()); // Job2 did not run, because execution was blocked by Job1
	}

	public void testWithRunningSearcher() throws Exception {
		LuceneSearcher searcher = new LuceneSearcher();
		searcher.setIndexPath(new File(LIVE_INDEX_PATH));
		searcher.start();

		UpdateJob job = this.createJob(CONFIGURATION_FILE_1);
		boolean result = job.execute();
		assertTrue(result);
	}

	/**
	 * Helper methods / classes
	 */

	/**
	 * Set up an update job with the given configuration
	 * @param configFile
	 * @return UpdateJob
	 */
	private UpdateJob createJob(File configFile) {
		ConfigurationProvider configProvider = new ConfigurationProvider();
		configProvider.setConfigurationFile(configFile);

		UpdateJob job = new UpdateJob();
		job.setConfigurationProvider(configProvider);

		// set up indexer
		LuceneIndexer indexer = new LuceneIndexer();
		indexer.setIndexPath(new File(TMP_INDEX_PATH));
		indexer.setMappingScript(MAPPING_FILE);
		job.setIndexer(indexer);

		// set up mapper
		CSWRecordCache cache = new CSWRecordCache();
		cache.setCachePath(new File(CSW_CACHE_PATH));
		XsltMapper mapper = new XsltMapper();
		mapper.setCache(cache);
		job.setCswRecordMapper(mapper);

		// set up searcher
		LuceneSearcher searcher = new LuceneSearcher();
		searcher.setIndexPath(new File(LIVE_INDEX_PATH));
		job.setSearcher(searcher);

		return job;
	}

	class ThreadPerTaskExecutor implements Executor {
		@Override
		public void execute(Runnable r) {
			new Thread(r).start();
		}
	};

	class JobRunner implements Callable<Boolean> {
		private File configFile;
		private String name;

		public JobRunner(File configFile, String name) {
			this.configFile = configFile;
			this.name = name;
		}
		@Override
		public Boolean call() {
			try {
				System.out.println("Starting: "+this.name);
				boolean result = UpdateJobTestLocal.this.createJob(this.configFile).execute();
				System.out.println("Finished: "+this.name);
				return result;
			}
			catch (Exception ex) {
				System.out.println("Finished: "+this.name+" with exception");
				return true;
			}
		}
	};
}
