/**
 * 
 */
package de.ingrid.interfaces.csw.harvest.ibus;

import de.ingrid.utils.dsc.Record;

/**
 * Interface for all record manipulating classes. Is used after the record has
 * been retrieved by the ibus client and before the record is serialized to
 * cache.
 * 
 * @author joachim@wemove.com
 * 
 */
public interface IdfRecordPreProcessor {

    /**
     * Manipulate a InGrid Record.
     * 
     * @param record
     */
    public void process(Record record);

}
