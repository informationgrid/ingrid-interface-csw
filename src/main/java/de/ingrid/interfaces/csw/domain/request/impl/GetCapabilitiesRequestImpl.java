/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.request.impl;

import java.util.List;

import de.ingrid.interfaces.csw.domain.constants.ConfigurationKeys;
import de.ingrid.interfaces.csw.domain.exceptions.CSWException;
import de.ingrid.interfaces.csw.domain.exceptions.CSWVersionNegotiationFailedException;
import de.ingrid.interfaces.csw.domain.request.GetCapabilitiesRequest;

public class GetCapabilitiesRequestImpl extends AbstractRequestImpl implements GetCapabilitiesRequest {

	@Override
	public void validate() throws CSWException {

		this.validateAcceptVersions();
	}

	/**
	 * Check if ACCEPTVERSIONS parameter contains the configured csw version
	 * or is omitted.
	 * @throws CSWVersionNegotiationFailedException
	 */
	protected void validateAcceptVersions() throws CSWVersionNegotiationFailedException {

		// get the version from the configuration
		String cswVersion = CSWConfig.getInstance().getStringMandatory(ConfigurationKeys.CSW_VERSION);

		// if accepted versions are given in the request, they must include the
		// configured version. if no version are requested, we default to the configured one
		List<String> versions = this.getEncoding().getAcceptVersions();
		if (versions != null && versions.size() > 0 && !versions.contains(cswVersion)) {
			StringBuffer errorMsg = new StringBuffer();
			errorMsg.append("AcceptVersions has an unsupported value.\n");
			errorMsg.append("Supported value: ").append(cswVersion).append("\n");
			throw new CSWVersionNegotiationFailedException(errorMsg.toString());
		}
	}
}
