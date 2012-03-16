/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.thoughtworks.xstream.XStream;

import de.ingrid.interfaces.csw.config.model.Configuration;
import de.ingrid.interfaces.csw.config.model.IBusHarvester;
import de.ingrid.interfaces.csw.config.model.RequestDefinition;

/**
 * ConfigurationProvider gives access to the configuration of the update job client.
 * 
 * @author ingo@wemove.com
 */
public class ConfigurationProvider {

	/**
	 * The XML configuration file
	 */
	private File configurationFile = null;

	/**
	 * The configuration instance
	 */
	private Configuration configuration = null;

	/**
	 * Constructor.
	 */
	public ConfigurationProvider() {}

	/**
	 * Set the configuration file.
	 * @param configurationFile
	 */
	public void setConfigurationFile(File configurationFile) {
		this.configurationFile = configurationFile;
	}

	/**
	 * Read the configuration from disk.
	 * Creates an empty Configuration instance if the file does not exist or
	 * is empty.
	 */
	private synchronized void read() throws IOException {

		// create empty configuration, if not existing yet
		if (!this.configurationFile.exists()) {
			this.configurationFile.createNewFile();
		}

		BufferedReader input = null;
		try {
			// read the configuration file content
			StringBuilder content = new StringBuilder();
			input = new BufferedReader(new InputStreamReader(new FileInputStream(this.configurationFile), "UTF-8"));
			String line = null;
			while((line = input.readLine()) != null) {
				content.append(line);
				content.append(System.getProperty("line.separator"));
			}
			input.close();
			input = null;

			// deserialize a temporary Configuration instance from xml
			String xml = content.toString();
			if (xml.length() > 0) {
				XStream xstream = new XStream();
				this.setXStreamAliases(xstream);
				this.configuration = (Configuration)xstream.fromXML(xml);
			}
			else {
				this.configuration = new Configuration();
			}
		}
		finally {
			if (input != null) {
				try {
					input.close();
				}
				catch (IOException e) {}
			}
		}
	}

	/**
	 * Write the given Configuration to the disc. To keep the time for modifying the actual configuration
	 * file as short as possible, the method writes the configuration into a temporary file first and then renames
	 * this file to the original configuration file name.
	 * Note: Since renaming a file is not atomic in Windows, if the target file exists already (we need to delete
	 * and then rename), this method is synchronized.
	 * @param configuration Configuration instance
	 * @throws IOException
	 */
	public synchronized void write(Configuration configuration) throws IOException {

		// make sure the configuration is loaded
		this.getConfiguration();

		// serialize the Configuration instance to xml
		XStream xstream = new XStream();
		this.setXStreamAliases(xstream);
		String xml = xstream.toXML(configuration);

		// write the configuration to a temporary file first
		File tmpFile = File.createTempFile("config", null);
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpFile.getAbsolutePath()),"UTF8"));
		try {
			output.write(xml);
			output.close();
			output = null;
		}
		finally {
			if (output != null) {
				output.close();
			}
		}

		// move the temporary file to the configuration file
		this.configurationFile.delete();
		boolean result = tmpFile.renameTo(this.configurationFile);
		if (!result) {
			throw new IOException("The configuration could not be stored. (Rename '" + tmpFile.getAbsolutePath() + "' to '" + this.configurationFile.getAbsolutePath() + "' failed.)");
		}
	}

	/**
	 * Get the configuration
	 * @return Configuration
	 * @throws IOException
	 */
	public Configuration getConfiguration() throws IOException {
		if (this.configuration == null) {
			this.read();
		}
		return this.configuration;
	}

	/**
	 * Set the xml aliases for model classes
	 * @param xstream XStream instance
	 */
	private void setXStreamAliases(XStream xstream) {
		xstream.alias("configuration", Configuration.class);
		xstream.alias("request", RequestDefinition.class);
		xstream.alias("iBusHarvester", IBusHarvester.class);
	}
}
