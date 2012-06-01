/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.filter;

import org.geotoolkit.lucene.filter.SpatialQuery;

import de.ingrid.interfaces.csw.domain.query.CSWQuery;

/**
 * @author ingo herwig <ingo@wemove.com>
 */
public interface FilterParser {

    /**
     * Parse a CSW query document into a lucene query.
     * 
     * @param cswQuery
     * @return SpatialQuery
     * @throws Exception
     */
    SpatialQuery parse(CSWQuery cswQuery) throws Exception;
}
