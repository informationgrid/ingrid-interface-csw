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
