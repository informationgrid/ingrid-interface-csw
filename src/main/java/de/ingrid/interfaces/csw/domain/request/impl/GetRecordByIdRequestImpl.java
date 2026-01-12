/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2026 wemove digital solutions GmbH
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

import de.ingrid.interfaces.csw.domain.exceptions.CSWException;
import de.ingrid.interfaces.csw.domain.exceptions.CSWMissingParameterValueException;
import de.ingrid.interfaces.csw.domain.query.CSWQuery;
import de.ingrid.interfaces.csw.domain.request.GetRecordByIdRequest;

public class GetRecordByIdRequestImpl extends AbstractRequestImpl implements GetRecordByIdRequest {

	@Override
	public void validate() throws CSWException {
		CSWQuery query = this.getEncoding().getQuery();
		if (query.getIds() == null || query.getIds().size() == 0) {
			throw new CSWMissingParameterValueException("Id is not specified or has no value", "GetRecordById");
		}
	}

	@Override
	public CSWQuery getQuery() throws CSWException {
		return this.getEncoding().getQuery();
	}
}
