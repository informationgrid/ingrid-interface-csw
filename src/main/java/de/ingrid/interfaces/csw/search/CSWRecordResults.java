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
/**
 * 
 */
package de.ingrid.interfaces.csw.search;

import java.util.ArrayList;
import java.util.List;

import de.ingrid.interfaces.csw.domain.CSWRecord;

/**
 * 
 * Representation of a search result. Contains CSWRecords and total hits etc.
 * 
 * @author joachim@wemove.com
 * 
 */
public class CSWRecordResults {

    private List<CSWRecord> results;

    private int totalHits;

    public void setResults(List<CSWRecord> results) {
        this.results = results;
    }

    public void add(CSWRecord record) {
        if (results == null) {
            results = new ArrayList<CSWRecord>();
        }
        results.add(record);
    }

    public List<CSWRecord> getResults() {
        return results;
    }

    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
    }

    public int getTotalHits() {
        return totalHits;
    }

}
