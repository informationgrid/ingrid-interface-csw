/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.request.impl;

import de.ingrid.interfaces.csw.config.ApplicationProperties;
import de.ingrid.interfaces.csw.domain.constants.ConfigurationKeys;
import de.ingrid.interfaces.csw.domain.exceptions.CSWException;
import de.ingrid.interfaces.csw.domain.exceptions.CSWVersionNegotiationFailedException;
import de.ingrid.interfaces.csw.domain.request.DescribeRecordRequest;

public class DescribeRecordRequestImpl extends AbstractRequestImpl implements DescribeRecordRequest {

	@Override
	public void validate() throws CSWException {

		this.validateVersion();
	}

	/**
	 * Check if version parameter contains the configured csw version.
	 * @throws CSWVersionNegotiationFailedException
	 */
	protected void validateVersion() throws CSWVersionNegotiationFailedException {

		// get the version from the configuration
		String cswVersion = ApplicationProperties.getMandatory(ConfigurationKeys.CSW_VERSION);

		// the requested version must match the configured version
		String version = this.getEncoding().getVersion();
		if (version == null || !cswVersion.equals(version)) {
			StringBuffer errorMsg = new StringBuffer();
			errorMsg.append("Version has an unsupported value.\n");
			errorMsg.append("Supported value: ").append(cswVersion).append("\n");
			throw new CSWVersionNegotiationFailedException(errorMsg.toString());
		}
	}
}
