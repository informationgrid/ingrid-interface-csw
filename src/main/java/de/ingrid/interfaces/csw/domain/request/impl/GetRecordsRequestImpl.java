/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.request.impl;

import de.ingrid.interfaces.csw.domain.exceptions.CSWException;
import de.ingrid.interfaces.csw.domain.exceptions.CSWMissingParameterValueException;
import de.ingrid.interfaces.csw.domain.query.CSWQuery;
import de.ingrid.interfaces.csw.domain.request.GetRecordsRequest;

public class GetRecordsRequestImpl extends AbstractRequestImpl implements GetRecordsRequest {

    @Override
    public void validate() throws CSWException {

        if (this.getQuery().getTypeNames() == null) {
            throw new CSWMissingParameterValueException(
                    "Attribute 'typeNames' is not specified or has no value in Query", "typeNames");
        }
    }

    @Override
    public CSWQuery getQuery() throws CSWException {
        return this.getEncoding().getQuery();
    }
}
