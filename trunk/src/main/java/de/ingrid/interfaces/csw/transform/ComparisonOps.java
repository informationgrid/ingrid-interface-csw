

package de.ingrid.interfaces.csw.transform;

import de.ingrid.interfaces.csw.exceptions.CSWFilterException;



/**
*
* The %comparison_ops and %spatial_ops entities are used to represent 
* the XML encodings for simple scalar and spatial expressions. These 
* are defined in subsequent sections. Simple XML encoded scalar and 
* spatial expressions, as well as logical expressions, can be combined 
* using the <And>, <Or> and <Not> elements to encode more complex compound 
* expressions.
*
*/	
public interface ComparisonOps extends FilterOperation {
	
   
	/**
	 * returns the operation to be performed
	 * @return CompOperation
	 * @throws CSWFilterException e
	 */
	CompOperation getCompOperation() throws CSWFilterException;
	
   /**
    * root interface of all comparison operation interfaces
    */	
	public interface CompOperation
	{
	   /**
        * returns the first operator of the comparison 
        */	
    	public Expression getFirstExpression();    	      	
    	
       /**
        * returns the second operator of the comparison 
        */	
    	public Expression getSecondExpression();
    	
       /**
	    * @see #getFirstExpression
	    */
	    public void setExpressions(Expression expr1, Expression expr2);
	    	   
	}

	
   /**
    * represents the comparison if a property (value) is equal
    * to a given value
    */	
    public interface PropertyIsEqualTo extends CompOperation {    	           	
    }

    
    public interface PropertyIsNotEqualTo extends CompOperation {    	           	
    }
    
   /**
    * represents the comparison if a property (value) is less than
    * a given value
    */
    public interface PropertyIsLessThan extends CompOperation {
    }

   /**
    * represents the comparison if a property (value) is greater
    * than a given value
    */
    public interface PropertyIsGreaterThan extends CompOperation {
    }

   /**
    * represents the comparison if a property (value) is less than
    * or equal to a given value
    */
    public interface PropertyIsLessThanOrEqualTo extends CompOperation {
    }

   /**
    * represents the comparison if a property (value) is greater
    * than or equal to a given value
    */
    public interface PropertyIsGreaterThanOrEqualTo extends CompOperation {
    }

   /**
    * represents the comparison if a property (value) is like
    * a given value (blured equal)
    */
    public interface PropertyIsLike extends CompOperation {
    	
       /**
        * returns the name of the property that should be compared
        */	
    	public Expression.PropertyName getPropertyName();
    	
       /**
	    * @see #getPropertyName
	    */	
    	public void setPropertyName(Expression.PropertyName name);
    	
       /**
        * returns the literal the property should be compared to
        */	
    	public Expression.Literal getLiteral();
    	
       /**
	    * @see #getLiteral
	    */	
    	public void setLiteral(Expression.Literal literal);
    	
       /**
	    * returns the character that is used as wild card
	    */	
    	public char getWildCard();
    	
       /**
	    * @see #getWildCard
	    */	
    	public void setWildCard(char wildCard);
    	
       /**
	    * returns the character that is used a wild card for a
	    * single character
	    */	
    	public char getSingleChar();
    	
       /**
	    * @see #getSingleChar
	    */	
    	public void setSingleChar(char singleChar);
    	
       /**
	    * retuns the character that is used as escape sequence 
	    */	
    	public char getEscape();
    	
       /**
	    * @see #getEscape
	    */	
    	public void setEscape(char escape);
    	
    }

   /**
    * represents the comparison if a property (value) is  null
    */
    public interface PropertyIsNull extends CompOperation {
    	
    	/**
        * returns the name of the property that should be compared
        */	
    	public Expression.PropertyName getPropertyName();
    	
       /**
	    * @see #getPropertyName
	    */	
    	public void setPropertyName(Expression.PropertyName name);
    	
       /**
        * returns the literal the property should be compared to
        */	
    	public Expression.Literal getLiteral();
    	
       /**
	    * @see #getLiteral
	    */	
    	public void setLiteral(Expression.Literal literal);
    	
    }

   /**
    * represents the comparison if a property (value) is between
    * two values
    */
    public interface PropertyIsBetween extends CompOperation {
    	
       /**
        * returns the name of the property that should be compared
        */	
    	public Expression.PropertyName getPropertyName();
    	
       /**
	    * @see #getPropertyName
	    */	
    	public void setPropertyName(Expression.PropertyName name);
    	
       /**
        * returns the upper boundary for comparison
        */	
    	public UpperBoundary getUpperBoundary();
    	
       /**
	    * @see #getUpperBoundary
	    */	
    	public void setUpperBoundary(UpperBoundary upperBoundary);
    	
       /**
        * returns the lower boundary for comparison
        */	
    	public LowerBoundary getLowerBoundary();
    	
       /**
	    * @see #getLowerBoundary
	    */	
    	public void setLowerBoundary(LowerBoundary lowerBoundary);	
    	
    }
    
    interface Boundary {
    	
    	//String VALUE = "";
    	
    	
    	/**
    	 * returns the value as a string
    	 * @return boundaryValue String
    	 */
    	public String getBoundaryValue();
    	
    	public String setBoundaryValue();
    	
       /**
        * returns the expression that represents the boundary
        */	
    	public Expression.BaseExpr getExpression();
    	
       /**
	    * @see #getExpression
	    */	
    	public void setExpression(Expression.BaseExpr expr);
    	
    }
    
    public interface UpperBoundary extends Boundary {     	
    }
    
    public interface LowerBoundary extends Boundary {
    }
    
}
