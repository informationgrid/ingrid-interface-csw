/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.catalog.action;

import java.util.ArrayList;
import java.util.List;

import de.ingrid.interfaces.csw.domain.CSWRecord;

/**
 * ActionResult describes the result of an action execution.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public class ActionResult {

    private Action action = null;
    private List<CSWRecord> records = new ArrayList<CSWRecord>();

    /**
     * Constructor
     * @param action
     */
    public ActionResult(Action action) {
        this.action = action;
    }

    /**
     * Get the associated action
     * @return Action
     */
    public Action getAction() {
        return this.action;
    }

    /**
     * Add a record to the list of affected records
     * @param record
     */
    public void addRecord(CSWRecord record) {
    	this.records.add(record);
    }

    /**
     * Get the list of affected records
     * @return List<CSWRecord>
     */
    public List<CSWRecord> getRecords() {
        return this.records;
    }
}
