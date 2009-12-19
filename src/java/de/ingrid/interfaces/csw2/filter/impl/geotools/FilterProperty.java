/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw2.filter.impl.geotools;


public enum FilterProperty {

	/**
	 * The PropertyName that is currently processed.
	 * The property value is expected to be a String.
	 */
	CURRENT_PROPERTY_NAME,
	
	/**
	 * The Comparison Operator that is currently being used.
	 * The property value is expected to be a org.opengis.filter.BinaryComparisonOperator.
	 */
	CURRENT_COMPARISON_OPERATOR,
	
	/**
	 * The Comparison Operator that is currently being used.
	 * The property value is expected to be a org.opengis.filter.spatial.BinarySpatialOperator.
	 */
	CURRENT_SPATIAL_OPERATOR_NAME_TYPE, 
	
	/**
	 * The Logical Operator that is currently being used.
	 * The property value is expected to be a org.opengis.filter.spatial.BinaryLogicOperator.
	 */
	CURRENT_LOGICAL_OPERATION, 
	
	/**
	 * The lower and upper boundary of an PropertyIsBetween comparison.
	 * The property value is expected to be a object.
	 */
	CURRENT_PROPERTY_BETWEEN_LOWER_BOUNDARY, 
	CURRENT_PROPERTY_BETWEEN_UPPER_BOUNDARY
	
}
