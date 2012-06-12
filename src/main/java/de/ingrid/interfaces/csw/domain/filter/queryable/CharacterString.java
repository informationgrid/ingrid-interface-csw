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
