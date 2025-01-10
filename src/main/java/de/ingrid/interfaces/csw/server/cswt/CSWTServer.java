/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.server.cswt;

import org.w3c.dom.Document;

import de.ingrid.interfaces.csw.domain.exceptions.CSWException;
import de.ingrid.interfaces.csw.domain.request.TransactionRequest;

/**
 * CSWServer defines the interface for request processing specific to
 * the csw domain. All domain logic will be defined in CSWServer
 * implementations.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public interface CSWTServer {

	/**
	 * Do the CSW-Publication.Transaction request
	 * @param request A Transaction instance
	 * @return A Document instance
	 */
	Document process(TransactionRequest request) throws CSWException;

	/**
	 * Free all resources. (important: close the index readers file handles on windows)
	 */
	void destroy();
}
