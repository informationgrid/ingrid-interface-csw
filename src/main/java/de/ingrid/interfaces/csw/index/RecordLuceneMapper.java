/**
 * 
 */
package de.ingrid.interfaces.csw.index;

import org.apache.lucene.document.Document;

import de.ingrid.utils.dsc.Record;

/**
 * Defines the interface for mapping a {@link Record} to a lucene
 * {@link Document}.
 * 
 * @author joachim@wemove.com
 * 
 */
public interface RecordLuceneMapper {

    /**
     * Maps a record to a lucene document.
     * 
     * @param record
     * @return
     * @throws Exception
     */
    Document map(Record record) throws Exception;

}
