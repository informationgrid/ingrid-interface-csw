/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
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
 * Codelist: dataset, datasetcollection, service, application.
 * 
 * @author ingo@wemove.com
 */
public class Codelist implements QueryableType {

	/**
	 * @see de.ingrid.interfaces.csw.domain.filter.queryable.QueryableType#getLowerBound()
	 */
	@Override
	public Object getLowerBound() {
		// TODO What is a reasonable lower bound?
		return null;
	}

	/**
	 * @see de.ingrid.interfaces.csw.domain.filter.queryable.QueryableType#getUpperBound()
	 */
	@Override
	public Object getUpperBound() {
		// TODO What is a reasonable upper bound?
		return null;
	}

}
