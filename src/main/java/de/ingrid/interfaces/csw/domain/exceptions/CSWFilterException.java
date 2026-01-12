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
package de.ingrid.interfaces.csw.domain.exceptions;

/**
 * Die Klasse stellt eine Ausnahme im Filterencoding dar.
 * @author rschaefer
 */
public class CSWFilterException extends CSWException {

	static final long serialVersionUID = 0;

	@SuppressWarnings("unused")
	private String message = "CSWFilterException";

	/**
	 * Constructor
	 * @param message
	 * @param code
	 * @param locator
	 */
	public CSWFilterException(final String message, final String code, final String locator) {
		super(message, code, locator);
	}

	/**
	 * Constructor
	 * @param message
	 */
	public CSWFilterException(final String message) {
		super(message);
		this.message = message;
	}
}

