/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.domain.request.impl;

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
     * 
     * @throws CSWVersionNegotiationFailedException
     */
    protected void validateVersion() throws CSWVersionNegotiationFailedException {

        // get the version from the configuration
        String cswVersion = ConfigurationKeys.CSW_VERSION_2_0_2;

        // the requested version must match the configured version
        String version = this.getEncoding().getVersion();
        if (version == null || !cswVersion.equals(version)) {
            StringBuffer errorMsg = new StringBuffer();
            errorMsg.append("Parameter 'version' has an unsupported value.\n");
            errorMsg.append("Supported value: ").append(cswVersion).append("\n");
            throw new CSWVersionNegotiationFailedException(errorMsg.toString());
        }
    }
}
