/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.search;

import java.io.Serializable;

import de.ingrid.interfaces.csw.domain.CSWRecord;
import de.ingrid.interfaces.csw.domain.enums.ElementSetName;

/**
 * Interface for repositories that contain CSW records.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public interface CSWRecordRepository {

    /**
     * Get the record with the given id and elementset name.
     * 
     * @param id
     * @param elementSetName
     */
    public CSWRecord getRecord(Serializable id, ElementSetName elementSetName);
}
