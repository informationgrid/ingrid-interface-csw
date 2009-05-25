/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2.request;

import java.util.List;

import de.ingrid.interfaces.csw2.constants.ConfigurationKeys;
import de.ingrid.interfaces.csw2.exceptions.CSWVersionNegotiationFailedException;
import de.ingrid.interfaces.csw2.encoding.CSWMessageEncoding;
import de.ingrid.interfaces.csw2.exceptions.CSWException;
import de.ingrid.interfaces.csw2.tools.CSWConfig;

public class GetCapabilitiesRequest implements CSWRequest {
	
	protected CSWMessageEncoding encoding = null;

	@Override
	public void initialize(CSWMessageEncoding encoding) throws CSWException {
		this.encoding = encoding;
	}

	@Override
	public void validate() throws CSWException {

		// check if ACCEPTVERSIONS parameter contains the configured csw version
		
		// get the version from the configuration
		final String versionKey = ConfigurationKeys.CSW_VERSION;
		CSWConfig config = CSWConfig.getInstance();
		if (!config.containsKey(versionKey))
			throw new RuntimeException("Unknown configuration key in interface configuration: "+versionKey);
		String cswVersion = CSWConfig.getInstance().getString(versionKey);

		// if accepted versions are given in the request, they must include the 
		// configured version. if no version are requested, we default to the configured one 
		List<String> versions = encoding.getAcceptVersions();
		if (versions != null && versions.size() > 0 && !versions.contains(cswVersion)) {
			StringBuffer errorMsg = new StringBuffer();
			errorMsg.append("Parameter 'ACCEPTVERSIONS' has an unsupported value.\n");
			errorMsg.append("Supported values: ").append(cswVersion).append("\n");
			throw new CSWVersionNegotiationFailedException(errorMsg.toString());
		}
	}
}
