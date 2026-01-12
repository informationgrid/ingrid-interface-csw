/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2026 wemove digital solutions GmbH
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
