/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.filter.queryable;

/**
 * Date-8601, example: 1963-06-19.
 * 
 * @author ingo@wemove.com
 */
public class Date implements QueryableType {

	private static final String LOWER_BOUND = "0000-01-01";
	private static final String UPPER_BOUND = "9999-01-01";

	/**
	 * @see de.ingrid.interfaces.csw.domain.filter.queryable.QueryableType#getLowerBound()
	 */
	@Override
	public Object getLowerBound() {
		return LOWER_BOUND;
	}

	/**
	 * @see de.ingrid.interfaces.csw.domain.filter.queryable.QueryableType#getUpperBound()
	 */
	@Override
	public Object getUpperBound() {
		return UPPER_BOUND;
	}
}
