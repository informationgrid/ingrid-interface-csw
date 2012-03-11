/**
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.mapping;

import org.w3c.dom.Node;

import de.ingrid.interfaces.csw.domain.enums.ElementSetName;
import de.ingrid.utils.dsc.Record;

/**
 * Interface for classes that map IDF files to CSW records.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public interface CSWRecordMapper {

    /**
     * Map an IDF record to a CSW record
     * 
     * @param record
     * @param elementSetName
     * @return Node
     */
    public Node map(Record record, ElementSetName elementSetName);
}
