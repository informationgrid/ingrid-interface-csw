/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.domain.filter.queryable;


/**
 * @author ingo@wemove.com
 */
public class CharacterString implements QueryableType {

	private static final String LOWER_BOUND = "0";
	private static final String UPPER_BOUND = "~";

	/**
	 * @see de.ingrid.interfaces.csw.domain.filter.QueryableType#getLowerBound()
	 */
	@Override
	public Object getLowerBound() {
		return LOWER_BOUND;
	}

	/**
	 * @see de.ingrid.interfaces.csw.domain.filter.QueryableType#getUpperBound()
	 */
	@Override
	public Object getUpperBound() {
		return UPPER_BOUND;
	}
}
