/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
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
