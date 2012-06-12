/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.filter.queryable;


/**
 * @author ingo@wemove.com
 */
public class Boolean implements QueryableType {

	/**
	 * @see de.ingrid.interfaces.csw.domain.filter.queryable.QueryableType#getLowerBound()
	 */
	@Override
	public Object getLowerBound() {
		throw new UnsupportedOperationException("Lower bound is not supported for boolean queryables.");
	}

	/**
	 * @see de.ingrid.interfaces.csw.domain.filter.queryable.QueryableType#getUpperBound()
	 */
	@Override
	public Object getUpperBound() {
		throw new UnsupportedOperationException("Upper bound is not supported for boolean queryables.");
	}

}
