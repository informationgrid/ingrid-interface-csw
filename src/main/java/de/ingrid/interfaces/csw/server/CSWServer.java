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
	 * Free all resources. (important: close the index readers file handles on windows)
	 */
	void destroy();
}
