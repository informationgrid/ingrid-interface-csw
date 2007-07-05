

package de.ingrid.interfaces.csw.transform;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import de.ingrid.interfaces.csw.exceptions.CSWFilterException;
import de.ingrid.interfaces.csw.tools.XMLTools;



/**
* The following DTD fragment defines the fundamental components of an 
* expression:
* <pre>
* <!ENTITY % expr "(Add | Sub | Mul | Div | PropertyName | Literal | Function)">
* </pre>
* Subsequent sections (inner classes) define each of the elements in the 
* expression entity.
*/
public class ExpressionImpl implements Expression {
	
	private Element element = null;
	
	public ExpressionImpl(Element element) {
		this.element = element;
	}
	
   /**
    * creates a new empty logical operation tag
    */	
	public static Expression createExpression(Document doc, String expr)
	{
		//Debug.debugMethodBegin( "ExpressionImpl", "createExpression" );
		
		Expression op = new ExpressionImpl( doc.createElement(expr) );		
		
		//Debug.debugMethodEnd();
		return op;
	}
	
	/**
    * returns the expression to be considered
    */		
	public BaseExpr getExpression()
	{
		//Debug.debugMethodBegin( this, "getExpression" );
		
		BaseExpr bex = null;
		
		if (element.getLocalName().equals(FilterConst.Function)) {
			bex = new FunctionImpl();
		}
	   else
	    if (element.getLocalName().equals(FilterConst.PropertyName)) {
			bex = new PropertyNameImpl();
		}
	   else		
	    if (element.getLocalName().equals(FilterConst.Literal)) {
			bex = new LiteralImpl();
		}	
	   else		
	    if (element.getLocalName().equals(FilterConst.Add)) {
			bex = new AddImpl();
		}	
	   else		
	    if (element.getLocalName().equals(FilterConst.Sub)) {
			bex = new SubImpl();
		}	
	   else		
	    if (element.getLocalName().equals(FilterConst.Mul)) {
			bex = new MulImpl();
		}	
	   else		
	    if (element.getLocalName().equals(FilterConst.Div)) {
			bex = new DivImpl();
		}	
		
		//Debug.debugMethodEnd();
		return bex;
	}
	
	////////////////////////////////////////////////////////////////////////
	//                       inner classes                                //
	////////////////////////////////////////////////////////////////////////
	
   /**
    * base class of all expressions
    */	
	public class BaseExprImpl implements BaseExpr {
		
		public Element getAsElement()
		{
			return element;
		}
		
    }
    
    
   /**
    * represents the function expression. a function recieves one expression
    */
    public class FunctionImpl extends BaseExprImpl implements Function {
    	
    	/**
	    * returns the expression that is submitted to the function
	    */	
    	public BaseExpr getExpression()
    	{
    		throw new NoSuchMethodError("not implemented yet");
    	}
    	
       /**
	    * @see #getExpression
	    */	
	    public void setExpression(BaseExpr expression)
	    {
    		throw new NoSuchMethodError("not implemented yet");
    	}
	    
	   /**
	    * returns the function name
	    */ 
	    public String getName()
	    {
    		throw new NoSuchMethodError("not implemented yet");
    	}
	    
	   /**
	    * @see #getName
	    */ 
	    public void setName(String name)
	    {
    		throw new NoSuchMethodError("not implemented yet");
    	}
	    
    }

   /**
    * represents the property name expression. A property name identifies
    * the property that's targeted by a filter operation
    */
    public class PropertyNameImpl extends BaseExprImpl implements PropertyName {
    	
       /**
	    * returns the name of the property 
	    */	
    	public String getPropertyName()
    	{
    		return element.getFirstChild().getNodeValue();
    	}
    	
       /**
	    * @see #getPropertyName
	    */	
    	public void setPropertyName(String propertyName)
    	{
    		//Debug.debugMethodBegin( this, "setPropertyName" );
    		
    		Node node = element.getFirstChild();
    		
    		// remove property name if it already exists
    		if (node != null) {
    			element.removeChild( node );
    		}
    		
    		Text text = element.getOwnerDocument().createTextNode( propertyName );
    		element.appendChild( text );
    		
    		//Debug.debugMethodEnd();
    	}
    	
    }

   /**
    * represents the literal expression. A literal can contain anything. 
    * Usually it contains the value a property is compared to.
    */
    public class LiteralImpl extends BaseExprImpl implements Literal {
    	
       /**
	    * returns the value of the literal. Because a literal
	    * can contain anything no concrete return type can be
	    * set.<p>
	    * Possible return types are <tt>String</tt> <tt>Double</tt>
	    * and <tt>GMLGeometry</tt>
	    */	
    	public Object getLiteral() throws CSWFilterException
    	{
    		//Debug.debugMethodBegin( this, "getLiteral" );
    		
    		Object o = null;    		    		
    		
    		// at the moment there are three possibilities 
    		// of literals:
    		// 1. a gml geometry
    		// 2. a string
    		// 3. a number
    		Element elem = XMLTools.getFirstElement( element );
    		// if the literal node doesn't contain an element
    		// it must be a string or a number
    		if (elem == null) {
    			String s = element.getFirstChild().getNodeValue();
    			// simple test if s is a number
    			try {
    				o = new Double( s );
    				int d = ((Double)o).intValue();
    				
    				if (((Double)o).doubleValue() - d > 0.0000000001) {
    				} else {
    					o = new Integer( s );
    				}
    				
    			} catch(Exception e) {
    				o = s;
    			}
    		} 
    		// else it have to be a gml geometry
    		/*
    		else {
    			try {
    				o = GMLFactory.createGMLGeometry( elem );
    			} catch(Exception e) {
    				// if the node isn't a gml geometry
    				throw new CSWFilterException("Literal isn't a String, a Double or a GMLGeometry.\n" +
    				                           e );
    			}
    		}
    		*/
    		
    		//Debug.debugMethodEnd();
    		
    		return o;
    	}
    	
       /**
	    * @see #getLiteral
	    */	
    	public void setLiteral(Object literal) throws CSWFilterException
    	{
    		//Debug.debugMethodBegin( this, "setLiteral" );
    		
    		NodeList nl = element.getChildNodes();
    		
    		// remove literal values if already exists
    		if (nl != null && nl.getLength() > 0) {
    			for (int i = 0; i < nl.getLength(); i++) {
    				element.removeChild( nl.item(i) );
			    }
    		}
    		
    		// at the moment there are three possibilities 
    		// of literals:
    		// 1. a gml geometry
    		// 2. a string
    		// 3. a number
    		if (literal instanceof String) {
    			setString( (String)literal );
    		}
    	   else	
    		if (literal instanceof Double) {
    			setDouble( (Double)literal );
    		}
    		/*
    	   else	
    	    if (literal instanceof GMLGeometry) {
    	    	setGML( (GMLGeometry)literal );
    		}
    		*/
    	   else {
    	   		throw new CSWFilterException("Can't set literal. The submitted literal isn't a String, "+
    	   		                          "a Double or a GMLGeometry." );
    	   	}	
    	   
    		////Debug.debugMethodEnd();
    	}
    	
       /**
       	* sets a string as literal value
       	*/
    	private void setString(String literal)
    	{
    		Text text = element.getOwnerDocument().createTextNode( literal );
    		element.appendChild( text );
    	}
    	
       /**
       	* sets a Double as literal value
       	*/	
    	private void setDouble(Double literal)
    	{
    		setString( literal.toString() );
    	}
    	
       /**
       	* sets a GMLGeometry as literal value
       	*/	
       /*
    	private void setGML(GMLGeometry literal)
    	{
    		Element elem = ((GMLGeometry_Impl)literal).getAsElement();
    		XMLTools.insertNodeInto( elem, element );
      	}
       */  	
    }
    
    protected class MathOpImpl extends BaseExprImpl implements MathOp {
    	
       /**
	    * returns the first expression that is submitted to the operation
	    */	
    	public BaseExpr getFirstExpression()
    	{
    		throw new NoSuchMethodError("not implemented yet");
    	}
    	
       /**
	    * @see #getFirstExpression
	    */	
	    public void setFirstExpression(BaseExpr expression)
	    {
	    	throw new NoSuchMethodError("not implemented yet");
	    }
	    
	   /**
	    * returns the first expression that is submitted to the operation
	    */	
    	public BaseExpr getSecondExpression()
    	{
    		throw new NoSuchMethodError("not implemented yet");
    	}
    	
       /**
	    * @see #getSecondExpression
	    */	
	    public void setSecondExpression(BaseExpr expression)
	    {
	    	throw new NoSuchMethodError("not implemented yet");
	    }
    	
    }


   /**
    * represents the mathimatical add expression.
    */
    public class AddImpl extends MathOpImpl implements Add {
    }

   /**
    * represents the mathematical substract expression.
    */
    public class SubImpl extends MathOpImpl implements Sub {
    }

   /**
    * represents the mathematical multiply expression.
    */
    public class MulImpl extends MathOpImpl implements Mul {
    }

   /**
    * represents the mathematical divide expression.
    */
    public class DivImpl extends MathOpImpl implements Div {
    }

}
