/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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

    /**
     * Do some initialization if any needed. This will be called every time the mapper
     * is set, which means once per indexing.
     */
	void init();

}
