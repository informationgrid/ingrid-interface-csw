

package de.ingrid.interfaces.csw.transform;

import de.ingrid.interfaces.csw.exceptions.CSWFilterException;




/**
* The following DTD fragment defines the fundamental components of an 
* expression:
* <pre>
* <!ENTITY % expr "(Add | Sub | Mul | Div | PropertyName | Literal | Function)">
* </pre>
* Subsequent sections (inner classes) define each of the elements in the 
* expression entity.
*
*/
public interface Expression {
	
   /**
    * returns the expression to be considered
    */		
	public BaseExpr getExpression();
		
	
	////////////////////////////////////////////////////////////////////////
	//                       inner interfaces                             //
	////////////////////////////////////////////////////////////////////////
		
   /**
    * base interface of all expressions
    */	 	
	public interface BaseExpr {
    }

   /**
    * represents the function expression. a function recieves one expression
    */
    public interface Function extends BaseExpr {
    	
       /**
	    * returns the expression that is submitted to the function
	    */	
    	public BaseExpr getExpression(); 
    	
       /**
	    * @see #getExpression
	    */	
	    public void setExpression(BaseExpr expression);
	    
	   /**
	    * returns the function name
	    */ 
	    public String getName();
	    
	   /**
	    * @see #getName
	    */ 
	    public void setName(String name);
    }

   /**
    * represents the property name expression. A property name identifies
    * the property that's targeted by a filter operation
    */
    public interface PropertyName extends BaseExpr {
    	
      /**
	   * returns the name of the property 
	   */	
    	public String getPropertyName();
    	
       /**
	    * @see #getPropertyName
	    */	
    	public void setPropertyName(String propertyName);
    	
    }

   /**
    * represents the literal expression. A literal can contain anything. 
    * Usually it contains the value a property is compared to.
    */
    public interface Literal extends BaseExpr {
    	
       /**
	    * returns the value of the literal. Because a literal
	    * can contain anything no concrete return type can be
	    * set.
	    */	
    	public Object getLiteral() throws CSWFilterException;
    	
       /**
	    * @see #getLiteral
	    */	
    	public void setLiteral(Object literal) throws CSWFilterException;
    	
    }
    
    interface MathOp extends BaseExpr {
    	
       /**
	    * returns the first expression that is submitted to the operation
	    */	
    	public BaseExpr getFirstExpression(); 
    	
       /**
	    * @see #getFirstExpression
	    */	
	    public void setFirstExpression(BaseExpr expression);	
	    
	   /**
	    * returns the first expression that is submitted to the operation
	    */	
    	public BaseExpr getSecondExpression(); 
    	
       /**
	    * @see #getSecondExpression
	    */	
	    public void setSecondExpression(BaseExpr expression);	
    	
    }

   /**
    * represents the mathimatical add expression.
    */
    public interface Add extends MathOp {
    }

   /**
    * represents the mathematical substract expression.
    */
    public interface Sub extends MathOp {
    }

   /**
    * represents the mathematical multiply expression.
    */
    public interface Mul extends MathOp {
    }

   /**
    * represents the mathematical divide expression.
    */
    public interface Div extends MathOp {
    }

}
