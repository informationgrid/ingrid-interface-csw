/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.domain.transaction;

import java.util.List;

import de.ingrid.interfaces.csw.catalog.action.ActionResult;

/**
 * Representation of a CSW transaction result.
 * 
 * @see OpenGIS Catalogue Services Specification 2.0.2 - ISO Metadata
 *      Application Profile 10.11.4
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public class CSWTransactionResult {

    private String requestId;
    private int inserts;
    private int updates;
    private int deletes;
    private List<ActionResult> insertResults;

    /**
     * Constructor
     * @param requestId
     */
    public CSWTransactionResult(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Get the request id
     * @return String
     */
    public String getRequestId() {
        return this.requestId;
    }

    /**
     * Set the number of inserted records
     * @param inserts
     */
    public void setNumberOfInserts(int inserts) {
        this.inserts = inserts;
    }

    /**
     * Get the number of inserted records
     * @return Integer
     */
    public int getNumberOfInserts() {
        return this.inserts;
    }

    /**
     * Set the number of updated records
     * @param updates
     */
    public void setNumberOfUpdates(int updates) {
        this.updates = updates;
    }

    /**
     * Get the number of updated records
     * @return Integer
     */
    public int getNumberOfUpdates() {
        return this.updates;
    }

    /**
     * Set the number of deleted records
     * @param deletes
     */
    public void setNumberOfDeletes(int deletes) {
        this.deletes = deletes;
    }

    /**
     * Get the number of deleted records
     * @return Integer
     */
    public int getNumberOfDeletes() {
        return this.deletes;
    }

    /**
     * Set the list of results of insert actions 
     * @param insertResults
     */
    public void setInsertResults(List<ActionResult> insertResults) {
        this.insertResults = insertResults;
    }

    /**
     * Get the list of results of insert actions 
     * @return List<ActionResult>
     */
    public List<ActionResult> getInsertResults() {
        return this.insertResults;
    }
}
