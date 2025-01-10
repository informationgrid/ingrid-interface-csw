/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
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
 * Interface for types of queryables used in OGC filter queries.
 * @author ingo@wemove.com
 */
public interface QueryableType {

	/**
	 * Get the lower bound used for comparison operations
	 * like PropertyIsLessThan.
	 * @return Object
	 */
	public Object getLowerBound();

	/**
	 * Get the upper bound used for comparison operations
	 * like PropertyIsGreaterThan.
	 * @return Object
	 */
	public Object getUpperBound();
}
