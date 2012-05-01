package de.ingrid.interfaces.csw.admin;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.interfaces.csw.jobs.UpdateJob;

@Service
public class IndexRunnable implements Runnable {

    private static final Logger LOG = Logger.getLogger(IndexRunnable.class);

    @Autowired
    private UpdateJob updateJob;

    public void run() {
        try {
            LOG.info("indexing starts");

            if (!updateJob.execute()) {
                LOG.error("Could not execut update job.");
            }
            LOG.info("indexing ends");

        } catch (final Exception e) {
            LOG.error("Could not execut update job.", e);
        }
    }

}
