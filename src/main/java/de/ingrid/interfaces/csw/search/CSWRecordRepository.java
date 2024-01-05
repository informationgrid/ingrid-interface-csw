/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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
/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.search;

import de.ingrid.interfaces.csw.domain.CSWRecord;
import de.ingrid.interfaces.csw.domain.constants.ElementSetName;
import de.ingrid.interfaces.csw.domain.constants.Namespace;

import java.io.Serializable;

/**
 * Interface for repositories that contain CSW records.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public interface CSWRecordRepository {
	/**
	 * Get the record with the given id, element set name and output schema
	 */
	public CSWRecord getRecord(Serializable id, ElementSetName elementSetName, Namespace outputSchema) throws Exception;

	/**
	 * Check if the record with the given id is contained in the repository.
	 * 
	 * @param id
	 */
	public boolean containsRecord(String id);
	
	/**
	 * Remove the record from cache.
	 * 
     * @param id
     * @param elementSetName
	 * @param outputSchema
	 */
	public void removeRecord(Serializable id, ElementSetName elementSetName, Namespace outputSchema);
}
