/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.domain.request;

import de.ingrid.interfaces.csw.domain.encoding.CSWMessageEncoding;
import de.ingrid.interfaces.csw.domain.exceptions.CSWException;

/**
 * CSWRequest defines the common interface for all csw operations.
 * Inheriting interfaces may define additional operation specific
 * methods.
 * NOTE: CSWRequest instances are mainly used for validation and
 * storage of operation specific data, the operation execution is
 * done in the CSWServer instance.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public interface CSWRequest {

	/**
	 * Initialize the request instance. This should reset the internal state,
	 * because request classes may be reused.
	 * @param encoding
	 * @throws CSWException
	 */
	void initialize(CSWMessageEncoding encoding) throws CSWException;

	/**
	 * Check if the request is valid. Only operation-specific validation
	 * should be done here. Common validation is done in CSWMessageEncoding.validateRequest
	 * @return boolean
	 * @throws CSWException
	 */
	void validate() throws CSWException;
}
