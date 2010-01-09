/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2.request.impl;

import de.ingrid.interfaces.csw2.constants.ConfigurationKeys;
import de.ingrid.interfaces.csw2.exceptions.CSWException;
import de.ingrid.interfaces.csw2.exceptions.CSWVersionNegotiationFailedException;
import de.ingrid.interfaces.csw2.request.DescribeRecordRequest;
import de.ingrid.interfaces.csw2.tools.CSWConfig;

public class DescribeRecordRequestImpl extends AbstractRequestImpl implements DescribeRecordRequest {

	@Override
	public void validate() throws CSWException {

		validateVersion();
	}

	/**
	 * Check if version parameter contains the configured csw version.
	 * @throws CSWVersionNegotiationFailedException
	 */
	protected void validateVersion() throws CSWVersionNegotiationFailedException {
				
		// get the version from the configuration
		String cswVersion = CSWConfig.getInstance().getStringMandatory(ConfigurationKeys.CSW_VERSION);
	
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
