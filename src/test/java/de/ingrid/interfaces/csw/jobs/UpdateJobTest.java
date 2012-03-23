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

/**
 * @author ingo@wemove.com
 */
public class UpdateJobTest extends TestCase {

	private static final File CONFIGURATION_FILE_1 = new File("src/test/resources/config-updatejobtest-1iplug.xml");
	private static final File CONFIGURATION_FILE_2 = new File("src/test/resources/config-updatejobtest-2iplugs.xml");

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

	/**
	 * Helper methods / classes
	 */
	public UpdateJob createJob(File configFile) {
		ConfigurationProvider configProvider = new ConfigurationProvider();
		configProvider.setConfigurationFile(configFile);

		UpdateJob job = new UpdateJob();
		job.setConfigurationProvider(configProvider);
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
				boolean result = UpdateJobTest.this.createJob(this.configFile).execute();
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
