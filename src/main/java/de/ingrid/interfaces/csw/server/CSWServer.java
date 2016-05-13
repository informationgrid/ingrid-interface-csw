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
/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.server;

import org.w3c.dom.Document;

import de.ingrid.interfaces.csw.domain.exceptions.CSWException;
import de.ingrid.interfaces.csw.domain.request.DescribeRecordRequest;
import de.ingrid.interfaces.csw.domain.request.GetCapabilitiesRequest;
import de.ingrid.interfaces.csw.domain.request.GetDomainRequest;
import de.ingrid.interfaces.csw.domain.request.GetRecordByIdRequest;
import de.ingrid.interfaces.csw.domain.request.GetRecordsRequest;
import de.ingrid.interfaces.csw.domain.request.TransactionRequest;

/**
 * CSWServer defines the interface for request processing specific to
 * the csw domain. All domain logic will be defined in CSWServer
 * implementations.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public interface CSWServer {

	/**
	 * Do the OGC_Service.GetCapabilities request
	 * @param request A GetCapabilitiesRequest instance
     * @param variant A variant of the capabilities. Used to provide partner specific capabilities.
	 * @return A Document instance
	 */
	Document process(GetCapabilitiesRequest request, String variant) throws CSWException;

	/**
	 * Do the CSW-Discovery.DescribeRecord request
	 * @param request A DescribeRecordRequest instance
	 * @return A Document instance
	 */
	Document process(DescribeRecordRequest request) throws CSWException;

	/**
	 * Do the CSW-Discovery.GetDomain request
	 * @param request A GetDomainRequest instance
	 * @return A Document instance
	 */
	Document process(GetDomainRequest request) throws CSWException;

	/**
	 * Do the CSW-Discovery.GetRecords request
	 * @param request A GetRecordRequest instance
	 * @return A Document instance
	 */
	Document process(GetRecordsRequest request) throws CSWException;

	/**
	 * Do the CSW-Discovery.GetRecordById request
	 * @param request A GetRecordByIdRequest instance
	 * @return A Document instance
	 */
	Document process(GetRecordByIdRequest request) throws CSWException;
	
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
