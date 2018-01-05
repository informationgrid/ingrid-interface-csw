/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.index;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.LinkedHashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.thoughtworks.xstream.XStream;

/**
 * Manages status messages. Messages added are sorted chronological.
 *
 * @author joachim@wemove.com
 *
 */
@Service
public class StatusProvider {

    final protected static Log log = LogFactory.getLog(StatusProvider.class);

    /**
     * The name of the system property that defines the last status file
     */
    private static final String LAST_STATUS_FILE_NAME_PROPERTY = "lastStatus";

    /**
     * The name of the system property that defines the ingrid home directory
     */
    private static final String INGRID_HOME = "ingrid_home";

    /**
     * The default name of the status file
     */
    private static final String LAST_STATUS_XML = "last_status.xml";

    public static enum Classification {
        INFO(1), WARN(2), ERROR(3);

        Integer level;

        Classification(Integer level) {
            this.level = level;
        }
    }

    private File lastStatusFile = null;

    public StatusProvider() {

        // check if the config property is set and load the appropriate
        // file if yes
        String lastStatusFilename = System.getProperty(LAST_STATUS_FILE_NAME_PROPERTY);

        if (lastStatusFilename == null) {
            // check if the ingrid home path is set and derive config file
            String ingridHome = System.getProperty(INGRID_HOME);
            if (ingridHome != null) {
                File f = new File(ingridHome, LAST_STATUS_XML);
                lastStatusFilename = f.getAbsolutePath();
            }
        }

        this.lastStatusFile = new File(lastStatusFilename != null ? lastStatusFilename : LAST_STATUS_XML);
        try {
            this.load();
        } catch (IOException e) {
            log.error("Error loading last status file.", e);
        }
    }

    public static class State {

        protected String value;

        protected Date time;

        protected Classification classification;

        State(String value, Date time, Classification classification) {
            this.value = value;
            this.time = time;
            this.classification = classification;
        }

    }

    private LinkedHashMap<String, State> states = new LinkedHashMap<String, State>();

    private String msgFormat = "%1$tF %1$tT - [%2$s] %3$s\n";

    /**
     * Add a message to the message list. If the key already exists, the message
     * is updated.
     *
     * @param key
     * @param value
     * @param classification
     */
    public void addState(String key, String value, Classification classification) {
        if (this.states.containsKey(key)) {
            this.states.get(key).value = value;
            this.states.get(key).classification = classification;
        } else {
            synchronized (this) {
                this.states.put(key, new State(value, new Date(), classification));
            }
        }
    }

    /**
     * Add a message to the message list. If the key already exists, the message
     * is updated. THis message is tagged as INFO message.
     *
     * @param key
     * @param value
     */
    public void addState(String key, String value) {
        this.addState(key, value, Classification.INFO);
    }

    /**
     * Clear the message list.
     *
     */
    public void clear() {
        synchronized (this) {
            this.states.clear();
        }
    }

    public Classification getMaxClassificationLevel() {
        Classification result = Classification.INFO;
        for (State state : this.states.values()) {
            if (state.classification.level > result.level) {
                result = state.classification;
            }
        }
        return result;
    }

    /**
     * Write the status to the disc. To keep the time for modifying the actual
     * status file as short as possible, the method writes the file into a
     * temporary file first and then renames this file to the original status
     * file name. Note: Since renaming a file is not atomic in Windows, if the
     * target file exists already (we need to delete and then rename), this
     * method is synchronized.
     *
     * @throws IOException
     */
    public synchronized void write() throws IOException {

        // serialize the Configuration instance to xml
        XStream xstream = new XStream();
        String xml = xstream.toXML(this.states);

        // write the configuration to a temporary file first
        File tmpFile = File.createTempFile("config", null);
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpFile.getAbsolutePath()), "UTF8"));
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
        this.lastStatusFile.delete();
        FileUtils.moveFile(tmpFile, this.lastStatusFile);
    }

    /**
     * Load the last status file from disk. Creates an empty status list if the
     * file does not exist or is empty.
     */
    @SuppressWarnings("unchecked")
    public synchronized void load() throws IOException {

        // create empty configuration, if not existing yet
        if (!this.lastStatusFile.exists()) {
            log.warn("Status file " + this.lastStatusFile + " does not exist.");
            if (this.lastStatusFile.getParentFile() != null && !this.lastStatusFile.getParentFile().exists() && !this.lastStatusFile.getParentFile().mkdirs()) {
                log.error("Unable to create directories for '" + this.lastStatusFile.getParentFile() + "'");
            }
            log.info("Creating configuration file " + this.lastStatusFile);
            this.lastStatusFile.createNewFile();
        }

        BufferedReader input = null;
        try {
            if (log.isDebugEnabled()) {
                log.debug("Read status file: " + this.lastStatusFile);
            }
            // read the configuration file content
            StringBuilder content = new StringBuilder();
            input = new BufferedReader(new InputStreamReader(new FileInputStream(this.lastStatusFile), "UTF-8"));
            String line = null;
            while ((line = input.readLine()) != null) {
                content.append(line);
                content.append(System.getProperty("line.separator"));
            }
            input.close();
            input = null;

            if (content.length() == 0) {
                log.warn("Last status file " + this.lastStatusFile + " is empty.");
            }

            // deserialize a temporary Configuration instance from xml
            String xml = content.toString();
            if (xml.length() > 0) {
                XStream xstream = new XStream();
                this.states = (LinkedHashMap<String, State>) xstream.fromXML(xml);
            } else {
                this.states = new LinkedHashMap<String, State>();
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
     * Get the message list as String. Message entries are formated according to
     * the format. the default format is "%1$tF %1$tT - %2$s\n".
     *
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        synchronized (this) {
            for (State state : this.states.values()) {
                if (state.classification.equals(Classification.ERROR)) {
                    sb.append("<span class=\"error\">" + String.format(this.msgFormat, state.time, state.classification.name(), state.value) + "</span>");
                } else if (state.classification.equals(Classification.WARN)) {
                    sb.append("<span class=\"warn\">" + String.format(this.msgFormat, state.time, state.classification.name(), state.value) + "</span>");
                } else {
                    sb.append("<span class=\"info\">" + String.format(this.msgFormat, state.time, state.classification.name(), state.value) + "</span>");
                }
            }
        }
        return sb.toString();
    }

    public String getMsgFormat() {
        return this.msgFormat;
    }

    /**
     * Set the message format.
     *
     * @param msgFormat
     */
    public void setMsgFormat(String msgFormat) {
        this.msgFormat = msgFormat;
    }

}
