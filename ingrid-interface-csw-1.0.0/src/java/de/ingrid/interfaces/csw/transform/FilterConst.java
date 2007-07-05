

package de.ingrid.interfaces.csw.transform;

/**
*
* The <FeatureId> element is used to encode the unique identifier for 
* any feature instance. Within a filter expression, the <FeatureId> 
* is used as a reference to a particular feature instance.
*
*/
public interface FilterConst {
	
	//operation type
	public static final int LOGICAL = 0;
	public static final int SPATIAL = 1;
	public static final int COMPARISON = 2;
	public static final int MATH = 3;
	public static final int UNKNOWNOPERATION = -9999;
	
	//comparison operations
	public static final String PropertyIsEqualTo = "PropertyIsEqualTo";
	
	public static final String PropertyIsNotEqualTo = "PropertyIsNotEqualTo";
	
	public static final String PropertyIsLessThan = "PropertyIsLessThan"; 
	public static final String PropertyIsGreaterThan = "PropertyIsGreaterThan"; 
	public static final String PropertyIsLessThanOrEqualTo = "PropertyIsLessThanOrEqualTo"; 
	public static final String PropertyIsGreaterThanOrEqualTo = "PropertyIsGreaterThanOrEqualTo"; 
	public static final String PropertyIsLike = "PropertyIsLike"; 
	public static final String PropertyIsNull = "PropertyIsNull"; 
	public static final String PropertyIsBetween = "PropertyIsBetween";
	
	public static final String LowerBoundary = "LowerBoundary";
	public static final String UpperBoundary = "UpperBoundary";
	
	
	
	//spatial operations
   	public static final String Equals = "Equals"; 
	public static final String Disjoint = "Disjoint"; 
	public static final String Intersects = "Intersects"; 
	public static final String Touches = "Touches"; 
	public static final String Crosses = "Crosses"; 
	public static final String Within = "Within"; 
	public static final String Contains = "Contains"; 
	public static final String Inside = "Inside"; 
	public static final String Overlaps = "Overlaps"; 
	public static final String Beyond = "Beyond"; 
	public static final String BBOX = "BBOX";
	public static final String BOX = "Box";
	public static final String ATTR_BOX_CS = "cs";
	public static final String ATTR_BOX_TS = "ts";
	public static final String ATTR_BOX_DECIMAL = "decimal";
	
	//logical operations
	public static final String And = "And";
	public static final String Or = "Or";
	public static final String Not = "Not";
	
	//Expressions 
	public static final String Add = "Add";
	public static final String Sub = "Sub";
	public static final String Mul = "Mul";
	public static final String Div = "Div";
	public static final String PropertyName = "PropertyName";
	public static final String Literal = "Literal";
	public static final String Function = "Function";
	
}
