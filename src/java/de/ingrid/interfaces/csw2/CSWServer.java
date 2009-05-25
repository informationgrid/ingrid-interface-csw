/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2;

import org.w3c.dom.Document;

import de.ingrid.interfaces.csw2.exceptions.CSWException;
import de.ingrid.interfaces.csw2.request.DescribeRecordRequest;
import de.ingrid.interfaces.csw2.request.GetCapabilitiesRequest;
import de.ingrid.interfaces.csw2.request.GetDomainRequest;
import de.ingrid.interfaces.csw2.request.GetRecordByIdRequest;
import de.ingrid.interfaces.csw2.request.GetRecordsRequest;

public interface CSWServer {

	/**
	 * Do the OGC_Service.GetCapabilities request
	 * @param request A GetCapabilitiesRequest instance
	 * @return A Document instance
	 */
	Document process(GetCapabilitiesRequest request) throws CSWException;

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
}
