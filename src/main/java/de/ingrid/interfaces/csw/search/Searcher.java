/**
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.search;

import java.util.List;

import de.ingrid.interfaces.csw.domain.CSWRecord;

/**
 * Interface for CSW search implementations.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public interface Searcher {

    public List<CSWRecord> search(CSWQuery query);
}
