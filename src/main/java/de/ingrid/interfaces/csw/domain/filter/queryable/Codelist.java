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