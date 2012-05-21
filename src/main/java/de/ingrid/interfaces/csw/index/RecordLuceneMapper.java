/**
 * 
 */
package de.ingrid.interfaces.csw.index;

import java.util.Map;

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
     * Maps a record to a lucene document. Utility classes can be injected via
     * utils. This map will be passed to the underlying mapper itself (i.e.
     * JavaScript). There are some reserved keys though: "recordId",
     * "recordNode", "document", "log".
     * 
     * @param record
     * @param utils
     *            A map of util classes to be used in the mapper
     *            imnplementation.
     * @return
     * @throws Exception
     */
    Document map(Record record, Map<String, Object> utils) throws Exception;

}
