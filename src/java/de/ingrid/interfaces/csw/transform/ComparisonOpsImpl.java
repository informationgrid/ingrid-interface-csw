

package de.ingrid.interfaces.csw.transform;



import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.ingrid.interfaces.csw.exceptions.CSWFilterException;
import de.ingrid.interfaces.csw.tools.XMLTools;


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
public class ComparisonOpsImpl implements ComparisonOps {
	
	private Element element = null;
	
	public ComparisonOpsImpl(Element element) {
		this.element = element;
	}
	
   /**
    * creates a new empty logical operation tag
    */	
	public static ComparisonOps createComparison_Ops(Document doc, String ops)
	{
		//Debug.debugMethodBegin( "ComparisonOpsImpl", "createComparison_Ops" );
		
		ComparisonOps op = new ComparisonOpsImpl( doc.createElement( ops ) );		
		
		//Debug.debugMethodEnd();
		return op;
	}
	
   /**
    * returns the operation to be performed
    */	
	public CompOperation getCompOperation() throws CSWFilterException
	{
		//Debug.debugMethodBegin( this, "getCompOperation" );
		
		CompOperation comp = null;
		
		try {

			if ( element.getLocalName().equals( FilterConst.PropertyIsBetween ) ) {
				comp = new PropertyIsBetweenImpl();
			}
		   else	
		    if ( element.getLocalName().equals( FilterConst.PropertyIsEqualTo) ) {
				comp = new PropertyIsEqualToImpl();
			}
		    else	
			   if ( element.getLocalName().equals( FilterConst.PropertyIsNotEqualTo) ) {
					comp = new PropertyIsNotEqualToImpl();
				}
			
		   else	
		    if ( element.getLocalName().equals( FilterConst.PropertyIsGreaterThan) ) {
				comp = new PropertyIsGreaterThanImpl();
			}
		   else	
		    if ( element.getLocalName().equals( FilterConst.PropertyIsGreaterThanOrEqualTo) ) {
				comp = new PropertyIsGreaterThanOrEqualToImpl();
			}
		   else	
		    if ( element.getLocalName().equals( FilterConst.PropertyIsLessThan) ) {
				comp = new PropertyIsLessThanImpl();
			}
		   else	
		    if ( element.getLocalName().equals( FilterConst.PropertyIsLessThanOrEqualTo) ) {
				comp = new PropertyIsLessThanOrEqualToImpl();
			}
		   else	
		    if ( element.getLocalName().equals( FilterConst.PropertyIsLike) ) {
				comp = new PropertyIsLikeImpl();
			}
		   else	
		    if ( element.getLocalName().equals( FilterConst.PropertyIsNull) ) {
				comp = new PropertyIsNullImpl();
			}

		} catch(Exception e) {
			throw new CSWFilterException("invalid tag name: " + element.getLocalName() + 
			                           " could not create operation class.\n" + e);
		}		
		
		//Debug.debugMethodEnd();
		return comp;
	}
	
	////////////////////////////////////////////////////////////////////////
	//                       inner classes                                //
	////////////////////////////////////////////////////////////////////////	
	
   /**
    * root interface of all comparison operation interfaces
    */	
	public class CompOperationImpl implements CompOperation
	{
		
		public Element getAsElement() 
		{
			return element;
		}
		
		
	   /**
        * returns the first operator of the comparison 
        */	
    	public Expression getFirstExpression()
    	{
    		//Debug.debugMethodBegin( this, "getFirstExpression" );
    		
    		// first element contains first expression
    		Element elem = XMLTools.getFirstElement( element );
    		
    		// initialize expression and get contreted expression
    		Expression expr = new ExpressionImpl( elem );    		
    		
    		//Debug.debugMethodEnd();	
    		
    		return expr;
    	}
    	
       /**
        * returns the second operator of the comparison 
        */	
    	public Expression getSecondExpression()
    	{
    		//Debug.debugMethodBegin( this, "getSecondExpression" );
    		
    		Expression expr = null;
    		
    		// second element contains second expression
    		NodeList nl = element.getChildNodes();
    		
    		if (nl != null && nl.getLength() > 0) {
    			int cnt = 0;
    			for (int i = 0; i < nl.getLength(); i++) {
    				if (nl.item(i) instanceof Element) {
    					cnt++;
    				}    	
    				if (cnt == 2) {
    					expr = new ExpressionImpl( (Element)nl.item(i) ); 
    					break;   		
    				}			
			    }
    		}
    		    		
    		//Debug.debugMethodEnd();	
    		
    		return expr;
    	}
    	
       /**
	    * @see #getFirstExpression
	    */
	    public void setExpressions(Expression expr1,
	    						   Expression expr2)
	    {
	    	//Debug.debugMethodBegin( this, "setExpressions" );
	    	
	    	// second element contains second expression
    		NodeList nl = element.getChildNodes();
    		    
    		// if expressions already exists remove them
    		if (nl != null && nl.getLength() > 0) {
    			
    			for (int i = 0; i < nl.getLength(); i++) {
    				element.removeChild( nl.item(i) );
			    }    			
			    
    		}	
    		
    		// insert first expression
    		Element elem = ((ExpressionImpl.BaseExprImpl)expr1.getExpression()).getAsElement();    		
    		XMLTools.insertNodeInto( elem, element );
    		
    		// insert second expression
    		elem = ((ExpressionImpl.BaseExprImpl)expr2.getExpression()).getAsElement();    		
    		XMLTools.insertNodeInto( elem, element );    		
    		
    		//Debug.debugMethodEnd();	
    	}
	    
    	
	}

	
   /**
    * represents the comparison if a property (value) is equal
    * to a given value
    */	
    public class PropertyIsEqualToImpl extends CompOperationImpl implements PropertyIsEqualTo {    	           	
    }
    
    
    public class PropertyIsNotEqualToImpl extends CompOperationImpl implements PropertyIsNotEqualTo {    	           	
    }
    

   /**
    * represents the comparison if a property (value) is less than
    * a given value
    */
    public class PropertyIsLessThanImpl extends CompOperationImpl implements PropertyIsLessThan {
    }

   /**
    * represents the comparison if a property (value) is greater
    * than a given value
    */
    public class PropertyIsGreaterThanImpl extends CompOperationImpl implements PropertyIsGreaterThan {
    }

   /**
    * represents the comparison if a property (value) is less than
    * or equal to a given value
    */
    public class PropertyIsLessThanOrEqualToImpl extends CompOperationImpl implements PropertyIsLessThanOrEqualTo {
    }

   /**
    * represents the comparison if a property (value) is greater
    * than or equal to a given value
    */
    public class PropertyIsGreaterThanOrEqualToImpl extends CompOperationImpl implements PropertyIsGreaterThanOrEqualTo {
    }

   /**
    * represents the comparison if a property (value) is like
    * a given value (blured equal)
    */
    public class PropertyIsLikeImpl extends CompOperationImpl implements PropertyIsLike {
    	
       /**
        * returns the name of the property that should be compared
        */	
    	public Expression.PropertyName getPropertyName()
    	{
    		//Debug.debugMethodBegin( this, "getPropertyName" );
    		
    		// get property name tag
    		//Element elem = (Element)element.getElementsByTagName(FilterConst.PropertyName).item(0);
    		
    		Element elem = (Element)element.getElementsByTagNameNS(element.getNamespaceURI(), FilterConst.PropertyName).item(0);
    	
       		ExpressionImpl expr = new ExpressionImpl( elem );
    		
    		Expression.PropertyName pn = (Expression.PropertyName)expr.getExpression();
    		
    		//Debug.debugMethodEnd();	
    		return pn;
    	}
    	
       /**
	    * @see #getPropertyName
	    */	
    	public void setPropertyName(Expression.PropertyName name)
    	{
    		//Debug.debugMethodBegin( this, "setPropertyName" );
    		
    		XMLTools.removeNamedChildNodes( element, FilterConst.PropertyName );
    		
    		Element elem = ((ExpressionImpl.PropertyNameImpl)name).getAsElement();
    		XMLTools.insertNodeInto( elem, element );
    		
    		//Debug.debugMethodEnd();	
    	}
    	
       /**
        * returns the literal the property should be compared to
        */	
    	public Expression.Literal getLiteral() {
    		
    		//Debug.debugMethodBegin( this, "getLiteral" );
    	
    		//NodeList nl =  element.getElementsByTagName(FilterConst.Literal);
    		
    		Element elem = (Element)element.getElementsByTagNameNS(element.getNamespaceURI(), FilterConst.Literal).item(0);
    		   
    		ExpressionImpl expr = new ExpressionImpl(elem);
    		
    		Expression.Literal literal = (Expression.Literal)expr.getExpression();    		
    		
    		//Debug.debugMethodEnd();	
    		return literal;
    	}
    	
       /**
	    * @see #getLiteral
	    */	
    	public void setLiteral(Expression.Literal literal)
    	{
    		//Debug.debugMethodBegin( this, "setLiteral" );
    		
    		// remove literal tag if already exists
    		XMLTools.removeNamedChildNodes( element, FilterConst.Literal);
    		
    		Element elem = ((ExpressionImpl.LiteralImpl)literal).getAsElement();
    		XMLTools.insertNodeInto( elem, element );
    		
    		//Debug.debugMethodEnd();	
    	}
    	
       /**
	    * returns the character that is used as wild card
	    */	
    	public char getWildCard()
    	{
    		//Debug.debugMethodBegin( this, "getWildCard" );
    		
    		/*
    		String s = XMLTools.getAttrValue( element, "wildCard" );
    		
    		//System.out.println("WildCard: " + s);
    		
    		char c = '%';
    		if (s != null) {
    			c = s.charAt(0); 
    		}
    		  
    	  */
    	  
		  char c = ' ';
    		
		  String s = XMLTools.getAttrValue( element, "wildCard" );
    		 
		  if (s != null) {
			 c = s.charAt(0); 
		  }	    		
    		    		  		
    		    		  		  		  		
    		//Debug.debugMethodEnd();	
    		return c;
    	}
    	
       /**
	    * @see #getWildCard
	    */	
    	public void setWildCard(char wildCard)
    	{
    		//Debug.debugMethodBegin( this, "setWildCard" );
    		
    		element.setAttribute( "wildCard", ""+wildCard);
    		
    		//Debug.debugMethodEnd();	
    	}
    	
       /**
	    * returns the character that is used a wild card for a
	    * single character
	    */	
    	public char getSingleChar()
    	{
    		//Debug.debugMethodBegin( this, "getSingleChar" );
    		
    		/*
    		String s = XMLTools.getAttrValue( element, "singleChar" );
    		
    		char c = '?';
    		if (s != null) {
    			c = s.charAt(0); 
    		}
    		*/
    		
			char c = ' ';
    		
			String s = XMLTools.getAttrValue( element, "singleChar" );
    		 
			if (s != null) {
				c = s.charAt(0); 
			}	  
    		
    		    		
    		//Debug.debugMethodEnd();	
    		return c;
    	}
    	
       /**
	    * @see #getSingleChar
	    */	
    	public void setSingleChar(char singleChar)
    	{
    		//Debug.debugMethodBegin( this, "setSingleChar" );
    		
    		element.setAttribute( "singleChar", ""+singleChar);
    		
    		//Debug.debugMethodEnd();	
    	}
    	
       /**
	    * retuns the character that is used as escape sequence 
	    */	
    	public char getEscape()
    	{
    		//Debug.debugMethodBegin( this, "getEscape" );
    		
    		/*
    		String s = XMLTools.getAttrValue( element, "escapeChar" );
    		
    		char c = '\\';
    		if (s != null) {
    			c = s.charAt(0); 
    		}
    		*/
    		
			char c = ' ';
    		
			String s = XMLTools.getAttrValue( element, "escapeChar" );
    		
    		//auch 'escape' zulassen!?
    		if(s == null){
    			
				s = XMLTools.getAttrValue( element, "escape" );
    		}
		    
			if (s != null) {
				c = s.charAt(0); 
			}
		    
		   
    		
    		 
    		    		  		
    		//Debug.debugMethodEnd();	
    		return c;
    	}
    	
       /**
	    * @see #getEscape
	    */	
    	public void setEscape(char escapeChar)
    	{
    		//Debug.debugMethodBegin( this, "setEscape" );
    		
    		element.setAttribute( "escapeChar", ""+escapeChar);
    		
    		//Debug.debugMethodEnd();	
    	}
    	
    }

   /**
    * represents the comparison if a property (value) is  null
    */
    public class PropertyIsNullImpl extends CompOperationImpl implements PropertyIsNull {
    	
    	/**
        * returns the name of the property that should be compared
        */	
    	public Expression.PropertyName getPropertyName()
    	{
    		//Debug.debugMethodBegin( this, "getPropertyName" );
    		
    		// get property name tag
    		
    		//Element elem = (Element)element.getElementsByTagName(FilterConst.PropertyName).item(0);
    		
            Element elem = (Element)element.getElementsByTagNameNS(element.getNamespaceURI(), FilterConst.PropertyName).item(0);

    		ExpressionImpl expr = new ExpressionImpl( elem );
    		
    		Expression.PropertyName pn = (Expression.PropertyName)expr.getExpression();
    		
    		//Debug.debugMethodEnd();	
    		return pn;
    	}
    	
       /**
	    * @see #getPropertyName
	    */	
    	public void setPropertyName(Expression.PropertyName name)
    	{
    		//Debug.debugMethodBegin( this, "setPropertyName" );
    		
    		XMLTools.removeNamedChildNodes( element, FilterConst.PropertyName );
    		
    		Element elem = ((ExpressionImpl.PropertyNameImpl)name).getAsElement();
    		XMLTools.insertNodeInto( elem, element );
    		
    		//Debug.debugMethodEnd();	
    	}
    	
       /**
        * returns the literal the property should be compared to
        */	
    	public Expression.Literal getLiteral()
    	{
    		//Debug.debugMethodBegin( this, "getLiteral" );
    	
    		// get property name tag
    		//Element elem = (Element)element.getElementsByTagName(FilterConst.Literal).item(0);
    		
    		Element elem = (Element)element.getElementsByTagNameNS(element.getNamespaceURI(), FilterConst.Literal).item(0);
    		
    		ExpressionImpl expr = new ExpressionImpl( elem );
    			
    		Expression.Literal literal = (Expression.Literal)expr.getExpression();    		
    		
    		//Debug.debugMethodEnd();	
    		return literal;
    	}
    	
       /**
	    * @see #getLiteral
	    */	
    	public void setLiteral(Expression.Literal literal)
    	{
    		//Debug.debugMethodBegin( this, "setLiteral" );
    		
    		// remove literal tag if already exists
    		XMLTools.removeNamedChildNodes( element, FilterConst.Literal);
    		
    		Element elem = ((ExpressionImpl.LiteralImpl)literal).getAsElement();
    		XMLTools.insertNodeInto( elem, element );
    		
    		//Debug.debugMethodEnd();	
    	}
    	
    }

   /**
    * represents the comparison if a property (value) is between
    * two values
    */
    public class PropertyIsBetweenImpl extends CompOperationImpl implements PropertyIsBetween {
    	
    	private UpperBoundaryImpl upperBoundary = null;
    	
    	private LowerBoundaryImpl lowerBoundary = null;
    	
    	
    	/**
        * returns the name of the property that should be compared
        */	
    	public Expression.PropertyName getPropertyName()
    	{
    		//Debug.debugMethodBegin( this, "getPropertyName" );
    		
    		// get property name tag
    		//Element elem = (Element)element.getElementsByTagName(FilterConst.PropertyName).item(0);
    		
    		Element elem = (Element)element.getElementsByTagNameNS(element.getNamespaceURI(), FilterConst.PropertyName).item(0);
    		
    		ExpressionImpl expr = new ExpressionImpl( elem );
    		
    		Expression.PropertyName pn = (Expression.PropertyName)expr.getExpression();
    		
    		//Debug.debugMethodEnd();	
    		return pn;
    	}
    	
       /**
	    * @see #getPropertyName
	    */	
    	public void setPropertyName(Expression.PropertyName name)
    	{
    		//Debug.debugMethodBegin( this, "setPropertyName" );
    		
    		XMLTools.removeNamedChildNodes( element, FilterConst.PropertyName );
    		
    		Element elem = ((ExpressionImpl.PropertyNameImpl)name).getAsElement();
    		XMLTools.insertNodeInto( elem, element );
    		
    		//Debug.debugMethodEnd();	
    	}
    	
       /**
        * returns the upper boundary for comparison
        */	
    	public UpperBoundary getUpperBoundary()
    	{
    		//throw new NoSuchMethodError("Operation not supported yet");
    	
    		upperBoundary = new UpperBoundaryImpl();
    		
    	   //TODO set?
    		
    		return upperBoundary;
    		
    	}
    	
       /**
	    * @see #getUpperBoundary
	    */	
    	public void setUpperBoundary(UpperBoundary upperBoundary)
    	{
    		throw new NoSuchMethodError("Operation not supported yet");
    	}
    	
       /**
        * returns the lower boundary for comparison
        */	
    	public LowerBoundary getLowerBoundary()
    	{
    		//throw new NoSuchMethodError("Operation not supported yet");
    	
    		lowerBoundary = new LowerBoundaryImpl();
    		
    		 //TODO set?
    		
    		return lowerBoundary;
    	}
    	
       /**
	    * @see #getLowerBoundary
	    */	
    	public void setLowerBoundary(LowerBoundary lowerBoundary)
    	{
    		throw new NoSuchMethodError("Operation not supported yet");
    	}
    	
    }
    
    class BoundaryImpl implements Boundary {
    	
       /**
        * returns the expression that represents the boundary
        */	
    	public Expression.BaseExpr getExpression()
    	{
    		throw new NoSuchMethodError("Operation not supported yet");
    	}
    	
       /**
	    * @see #getExpression
	    */	
    	public void setExpression(Expression.BaseExpr expr)
    	{
    		throw new NoSuchMethodError("Operation not supported yet");
    	}

	
    /**
     * @see com.gistec.ingeocsw.transform.ComparisonOps.Boundary#getBoundaryValue()
     */
    public String getBoundaryValue() {
		
		String valueStr = "";
		
		Element elemValue = null;
		
		if (this instanceof LowerBoundary) {
			
			//elemValue = (Element)element.getElementsByTagName(FilterConst.LowerBoundary).item(0);
			
			elemValue = (Element)element.getElementsByTagNameNS(element.getNamespaceURI(), FilterConst.LowerBoundary).item(0);
			
			valueStr = elemValue.getFirstChild().getNodeValue();
			
		}  else if (this instanceof UpperBoundary) {
			
			//elemValue = (Element)element.getElementsByTagName(FilterConst.UpperBoundary).item(0);
			
			elemValue = (Element)element.getElementsByTagNameNS(element.getNamespaceURI(), FilterConst.UpperBoundary).item(0);
			
			valueStr = elemValue.getFirstChild().getNodeValue();
			
		} //TODO else error
		
		
		
		return valueStr;
	}

	public String setBoundaryValue() {
		
		throw new NoSuchMethodError("Operation not supported yet");
	}
    	
    }
    
    public class UpperBoundaryImpl extends BoundaryImpl implements UpperBoundary { 
    	
    	
    }
    
    public class LowerBoundaryImpl extends BoundaryImpl implements LowerBoundary {
    
    
    }
    
    
}
