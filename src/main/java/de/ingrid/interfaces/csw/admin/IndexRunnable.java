/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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
