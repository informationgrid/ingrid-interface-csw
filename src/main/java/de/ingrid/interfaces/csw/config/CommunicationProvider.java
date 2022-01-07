/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.thoughtworks.xstream.security.AnyTypePermission;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.thoughtworks.xstream.XStream;

import de.ingrid.interfaces.csw.config.model.communication.Communication;
import de.ingrid.interfaces.csw.config.model.communication.CommunicationClient;
import de.ingrid.interfaces.csw.config.model.communication.CommunicationMessages;
import de.ingrid.interfaces.csw.config.model.communication.CommunicationServer;
import de.ingrid.interfaces.csw.config.model.communication.CommunicationServerSecurity;
import de.ingrid.interfaces.csw.config.model.communication.CommunicationServerSocket;

@Service
public class CommunicationProvider {

    final protected static Log log = LogFactory.getLog(CommunicationProvider.class);

    /**
     * The XML configuration file
     */
    private File configurationFile = null;

    /**
     * The configuration instance
     */
    private Communication configuration = null;

    /**
     * Constructor.
     */
    public CommunicationProvider() {

    }

    /**
     * Set the configuration file.
     * 
     * @param configurationFile
     */
    public void setConfigurationFile(File configurationFile) {
        this.configurationFile = configurationFile;
    }

    public File getConfigurationFile() {
        return configurationFile;
    }

    /**
     * Set the configuration file based on the working directory. It actually
     * creates a file ./conf/communication.xml.
     * 
     * @param workingDirectory
     */
    public void setWorkingDirectory(File workingDirectory) {
        this.configurationFile = new File(new File(workingDirectory.getAbsolutePath(), "conf"), "communication.xml");
    }

    /**
     * Read the configuration from disk. Creates an empty Communication instance
     * if the file does not exist or is empty.
     */
    private synchronized void read() throws IOException {

        // create empty configuration, if not existing yet
        if (!this.configurationFile.exists()) {
            log.warn("Configuration file " + this.configurationFile + " does not exist.");
            log.info("Creating configuration file " + this.configurationFile);
            if (this.configurationFile.getParentFile() != null && !this.configurationFile.getParentFile().exists()
                    && !this.configurationFile.getParentFile().mkdirs()) {
                log.error("Unable to create directories for '" + this.configurationFile.getParentFile() + "'");
            }
            this.configurationFile.createNewFile();
        }

        BufferedReader input = null;
        try {
            // read the configuration file content
            StringBuilder content = new StringBuilder();
            input = new BufferedReader(new InputStreamReader(new FileInputStream(this.configurationFile), "UTF-8"));
            String line = null;
            while ((line = input.readLine()) != null) {
                content.append(line);
                content.append(System.getProperty("line.separator"));
            }
            input.close();
            input = null;

            if (content.length() == 0) {
                log.warn("Configuration file " + this.configurationFile + " is empty.");
            }

            // deserialize a temporary Configuration instance from xml
            String xml = content.toString();
            if (xml.length() > 0) {
                XStream xstream = new XStream();
                xstream.addPermission(AnyTypePermission.ANY);
                this.setXStreamAliases(xstream);
                this.configuration = (Communication) xstream.fromXML(xml);
            } else {
                this.configuration = new Communication();
            }
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Write the given communication Configuration to the disc. To keep the time
     * for modifying the actual configuration file as short as possible, the
     * method writes the configuration into a temporary file first and then
     * renames this file to the original configuration file name. Note: Since
     * renaming a file is not atomic in Windows, if the target file exists
     * already (we need to delete and then rename), this method is synchronized.
     * 
     * @param configuration
     *            Configuration instance
     * @throws IOException
     */
    public synchronized void write(Communication configuration) throws IOException {

        // make sure the configuration is loaded
        this.getConfiguration();

        // serialize the Configuration instance to xml
        XStream xstream = new XStream();
        xstream.addPermission(AnyTypePermission.ANY);
        this.setXStreamAliases(xstream);
        String xml = xstream.toXML(configuration);

        // write the configuration to a temporary file first
        File tmpFile = File.createTempFile("communication", null);
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpFile
                .getAbsolutePath()), "UTF8"));
        try {
            output.write(xml);
            output.close();
            output = null;
        } finally {
            if (output != null) {
                output.close();
            }
        }

        // move the temporary file to the configuration file
        this.configurationFile.delete();
        FileUtils.moveFile(tmpFile, this.configurationFile);
    }

    /**
     * Get the communictaion configuration
     * 
     * @return Communication
     * @throws IOException
     */
    public Communication getConfiguration() throws IOException {
        if (this.configuration == null) {
            this.read();
        }
        return this.configuration;
    }

    /**
     * Set the xml aliases for model classes
     * 
     * @param xstream
     *            XStream instance
     */
    private void setXStreamAliases(XStream xstream) {
        xstream.alias("communication", Communication.class);
        xstream.alias("client", CommunicationClient.class);
        xstream.alias("server", CommunicationServer.class);
        xstream.alias("socket", CommunicationServerSocket.class);
        xstream.alias("security", CommunicationServerSecurity.class);
        xstream.alias("messages", CommunicationMessages.class);

        xstream.aliasAttribute(CommunicationClient.class, "name", "name");
        xstream.aliasAttribute(CommunicationServer.class, "name", "name");
        xstream.aliasAttribute(CommunicationServerSecurity.class, "keystore", "keystore");
        xstream.aliasAttribute(CommunicationServerSecurity.class, "password", "password");
        xstream.aliasAttribute(CommunicationServerSocket.class, "port", "port");
        xstream.aliasAttribute(CommunicationServerSocket.class, "timeout", "timeout");
        xstream.aliasAttribute(CommunicationServerSocket.class, "ip", "ip");
        xstream.aliasAttribute(CommunicationMessages.class, "maximumSize", "maximumSize");
        xstream.aliasAttribute(CommunicationMessages.class, "threadCount", "threadCount");
        xstream.aliasAttribute(CommunicationMessages.class, "handleTimeout", "handleTimeout");
        xstream.aliasAttribute(CommunicationMessages.class, "queueSize", "queueSize");
    }
}
