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
