/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.admin;

import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import it.sauronsoftware.cron4j.TaskExecutor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.interfaces.csw.config.ConfigurationProvider;

@Service
public class IndexScheduler {

    private final Scheduler scheduler;

    private IndexRunnable runnable;

    private String _scheduleId;

    private String _pattern;

    private File _patternFile;

    /**
     * The update job configuration provider
     */
    @Autowired
    private ConfigurationProvider configurationProvider;

    private static final Log LOG = LogFactory.getLog(IndexScheduler.class);

    private static class LockRunnable extends Task {

        private final Runnable _runnable;

        private static boolean _isRunning = false;

        public LockRunnable(final Runnable runnable) {
            _runnable = runnable;
        }

        @Override
        public void execute(TaskExecutionContext arg0) throws RuntimeException {
            LOG.debug("trying to run index scheduler");
            if (!_isRunning) {
                LOG.info("starting and locking index scheduler");
                _isRunning = true;
                try {
                    _runnable.run();
                } catch (final Throwable t) {
                    // TODO: logging
                    LOG.error(t);
                } finally {
                    LOG.info("unlocking index scheduler");
                    _isRunning = false;
                }
            } else {
                LOG.info("index scheduler is still busy");
            }
        }
        
    }

    @Autowired
    public IndexScheduler(final IndexRunnable runnable) {
        this.runnable = runnable;
        scheduler = new Scheduler();
    }
    
    
    @PostConstruct
    public void init() {
        _patternFile = configurationProvider.getSchedulingPatternFile();
        if (_patternFile.exists()) {
            loadPatternFile();
            schedule();
        }
    }
    

    public void setPattern(final String pattern) {
        _pattern = pattern;
        if (_patternFile != null) {
            savePatternFile();
        }
        schedule();
    }

    public String getPattern() {
        return _pattern;
    }

    public void deletePattern() {
        LOG.debug("delete pattern");
        _pattern = null;
        deletePatternFile();
        if (isStarted()) {
            LOG.info("stop scheduler");
            scheduler.stop();
        }
    }

    public boolean isStarted() {
        return scheduler.isStarted();
    }
    
    public boolean isRunning() {
        if (!scheduler.isStarted()) {
            return false;
        }
        for (TaskExecutor task : scheduler.getExecutingTasks()) {
            if (task.isAlive()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean triggerManually() {
        
        try {
            if (!isRunning()) {
                if (!scheduler.isStarted()) {
                    scheduler.start();
                }
                scheduler.launch(new LockRunnable(runnable));
                return true;
            }
        } catch (Exception e) {
            LOG.error("Error running task now!", e);
        }
        return false;
    }

    private void schedule() {
        if (_pattern == null) return;
        if (_scheduleId == null) {
            LOG.info("scheduling indexer with pattern '" + _pattern + "'");
            _scheduleId = scheduler.schedule(_pattern, new LockRunnable(runnable));
        } else {
            LOG.info("rescheduling indexer with pattern '" + _pattern + "'");
            scheduler.reschedule(_scheduleId, _pattern);
        }
        if (!isStarted()) {
            LOG.info("start scheduler");
            scheduler.start();
        }
    }

    private void loadPatternFile() {
        LOG.debug("try to load pattern from file");
        try {
            final ObjectInputStream reader = new ObjectInputStream(new FileInputStream(_patternFile));
            _pattern = (String) reader.readObject();
            reader.close();
        } catch (final Exception e) {
            LOG.error(e);
        }
    }

    private void savePatternFile() {
        deletePatternFile();
        LOG.debug("saving pattern to file: " + _patternFile.getAbsolutePath());
        try {

            final ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(_patternFile));
            writer.writeObject(_pattern);
            writer.close();
        } catch (final Exception e) {
            LOG.error(e);
        }
    }

    private void deletePatternFile() {
        if (_patternFile != null && _patternFile.exists()) {
            LOG.debug("deleting pattern file: "+ _patternFile.getAbsolutePath());
            _patternFile.delete();
        }
    }
}
