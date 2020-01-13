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

import de.ingrid.interfaces.csw.domain.encoding.CSWMessageEncoding;
import de.ingrid.interfaces.csw.domain.exceptions.CSWException;
import de.ingrid.interfaces.csw.domain.request.CSWRequest;

/**
 * AbstractRequestImpl provides a generic initialize method and
 * methods for common validations to be used in subclasses.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public abstract class AbstractRequestImpl implements CSWRequest {

	private CSWMessageEncoding encoding = null;

	@Override
	public void initialize(CSWMessageEncoding encoding) throws CSWException {
		this.encoding = encoding;
	}

	/**
	 * Get the CSWMessageEncodingInstance
	 * @return the encoding
	 */
	public CSWMessageEncoding getEncoding() {
		return this.encoding;
	}
}
