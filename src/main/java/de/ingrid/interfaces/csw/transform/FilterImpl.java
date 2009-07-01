

package de.ingrid.interfaces.csw.transform;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.ingrid.interfaces.csw.exceptions.CSWFilterException;
import de.ingrid.interfaces.csw.tools.XMLTools;

/**
*
* The Web Feature Server (WFS) Implementation Specification [1] defined a 
* set of open generic operations for inserting, updating, deleting, locking 
* and querying objects stored in heterogeneous web-accessible repositories. 
* Section Filter of that document defined an encoding for query predicates 
* using the Extensible Markup Language (XML) [3]. Using that XML encoding, 
* a query operation could be defined that retrieves objects that lie in a
* particular region. Similarly, a delete operation could be restricted to 
* those object instances that lie in a particular region and have a particular 
* value for some specified non-spatial property.
* <p>
* It was realized that the XML filter encoding defined in the WFS specification 
* could be used by various OpenGIS services. 
* <p>
* The root element of a filter expression, <Filter>, is defined by the 
* following DTD:
* <pre>
* <!ELEMENT Filter (%logical_ops; |
*                   %comparison_ops; |
*                   %spatial_ops; |
*                    FeatureId+) >
* </pre>
* The entities %logical_ops, %comparison_ops and %spatial_ops represent 
* the logical, scalar and spatial expressions previously described. In addition, 
* using the <FeatureId> element, a filter can conveniently encode a reference 
* to one or more enumerated feature instances.
*
*/
public class FilterImpl implements Filter {

	private org.w3c.dom.Element element = null;
	private int operation = FilterConst.UNKNOWNOPERATION;
	
	public FilterImpl(Element element) 
	{
		//Debug.debugMethodBegin( this, "FilterImpl()" );

		this.element = element;
		operation = createOperationType();

		//Debug.debugMethodEnd();
	}
	
   /**
    * returns the dom document representing the filter
    */
    public Element getAsElement()
    {
    	return element;
    }
    
    private int createOperationType()
    {
    	//Debug.debugMethodBegin( this, "createOperationType" );
    	
    	Element elemFirst = XMLTools.getFirstElement(element);
		
    	
		operation = FilterConst.UNKNOWNOPERATION;				

	    if (elemFirst != null) {
	    	
		    
	    	String nodeName = elemFirst.getLocalName();
		    
		    		
		    if ( nodeName.equals(FilterConst.PropertyIsBetween) ||
			     nodeName.equals(FilterConst.PropertyIsEqualTo) ||
			     nodeName.equals(FilterConst.PropertyIsNotEqualTo) ||
			     nodeName.equals(FilterConst.PropertyIsGreaterThan) ||
			     nodeName.equals(FilterConst.PropertyIsGreaterThanOrEqualTo) ||
			     nodeName.equals(FilterConst.PropertyIsLessThan) ||
			     nodeName.equals(FilterConst.PropertyIsLessThanOrEqualTo) ||
			     nodeName.equals(FilterConst.PropertyIsLike) ||
			     nodeName.equals(FilterConst.PropertyIsNull) ) {
				operation = FilterConst.COMPARISON;
			}
		   else
		    if ( nodeName.equals( FilterConst.And ) ||	
		    	 nodeName.equals( FilterConst.Or ) ||
		    	 nodeName.equals( FilterConst.Not ) ) {
		    	operation = FilterConst.LOGICAL;
		    }
		   else
		    if ( nodeName.equals( FilterConst.BBOX ) || 
		    	 nodeName.equals( FilterConst.Beyond ) ||
		    	 nodeName.equals( FilterConst.Contains ) ||
		    	 nodeName.equals( FilterConst.Crosses ) ||
		    	 nodeName.equals( FilterConst.Disjoint ) ||
		    	 nodeName.equals( FilterConst.Equals ) ||
		    	 nodeName.equals( FilterConst.Inside ) ||
		    	 nodeName.equals( FilterConst.Intersects ) ||
		    	 nodeName.equals( FilterConst.Overlaps ) ||
		    	 nodeName.equals( FilterConst.Touches ) ||
		    	 nodeName.equals( FilterConst.Within ) ) {
		    	operation = FilterConst.SPATIAL;
		    }
		   else 
		    if ( nodeName.equals( FilterConst.Add ) ||
		    	 nodeName.equals( FilterConst.Sub ) ||
		    	 nodeName.equals( FilterConst.Div ) ||
		    	 nodeName.equals( FilterConst.Mul ) ) {
		    	operation = FilterConst.MATH;
		    }
		   
	    
	 	}
	    //Debug.debugMethodEnd();
	    
	    return operation;
	}
    
    /**
    * returns the type of operation that's the toplevel tag
    * of the filter
    */	
	public int getOperationType()
	{		
		return operation;  	 
	}	
    
	
   /**
   	* returns the ids (incl. feature type names) of the features that 
   	* are target by the filter.
   	*/
	public FeatureId[] getFeatureIds()
	{
		//Debug.debugMethodBegin( this, "getFeatureIds" );
				
		//NodeList nl = element.getElementsByTagName( "FeatureId" );
		
		NodeList nl = element.getElementsByTagNameNS(element.getNamespaceURI(), "FeatureId");
		
		FeatureId[] fid = new FeatureId[nl.getLength()];
		
		if (nl != null && nl.getLength() > 0) {			
			for (int i = 0; i < nl.getLength(); i++) {				
				fid[i] = new FeatureIdImpl( (Element)nl.item(i) );				
		    }			
		}
		
		//Debug.debugMethodEnd();
		
		return fid;
	}
	
   /**
    * @see #getFeatureIds
    */	
	public void addFeatureId(FeatureId featureId)
	{
		//Debug.debugMethodBegin( this, "getFeatureIds" );				
		
		XMLTools.insertNodeInto( ((FeatureIdImpl)featureId).getAsElement(), element);
		
		//Debug.debugMethodEnd();
	}
	
   /**
    * returns the comparison operations that are to performed.
    * notice that it's possible to casscade operations, so that
    * at a lower level (as part of the logical operation for example)
    * more comparison operations can be defined.
    */	
	public ComparisonOps getComparisonOps() throws CSWFilterException
	{
		//Debug.debugMethodBegin( this, "getComparisonOps" );
		
		// throw an exception if the first filter operation 
		// isn't a comparison
		if ( getOperationType() != FilterConst.COMPARISON ) {
			throw new CSWFilterException("Filter doesn't contain a comparison as " +
									  "toplevel tag.");
		}
		
		ComparisonOps comp = null;
		
		Element elem = XMLTools.getFirstElement( element );
						
		comp = new ComparisonOpsImpl( elem );				
		
		//Debug.debugMethodEnd();
		return comp;
	}
	
   /**
    * @see #getComparisonOps
    */	
	public void setComparisonOps(ComparisonOps compOps)
	{
		//Debug.debugMethodBegin( this, "setComparisonOps" );
		
		Element elem = XMLTools.getFirstElement( element );
		// remove operation tag if already exists
		if (elem != null) {
			element.removeChild( elem );
		}
		
		try {
			elem = ((ComparisonOpsImpl.CompOperationImpl)compOps.getCompOperation()).getAsElement();
		} catch(Exception e) {}
		
		XMLTools.insertNodeInto( elem, element );
		
		//Debug.debugMethodEnd();
	}
	
   /**
    * returns the logical operations that are to performed.
    * notice that it's possible to casscade operations, so that
    * at a lower level (as part of the spatial operation for example)
    * more logical operations can be defined.
    */	
	public LogicalOps getLogicalOps() throws CSWFilterException
	{
		//Debug.debugMethodBegin( this, "getLogicalOps" );
		
		// throw an exception if the first filter operation 
		// isn't a logical operation
		if ( getOperationType() != FilterConst.LOGICAL ) {
			throw new CSWFilterException("Filter doesn't contain a logical operation as " +
									  "toplevel tag.");
		}
		
		LogicalOps logical = null;
		
		Element elem = XMLTools.getFirstElement( element );
						
		logical = new LogicalOpsImpl( elem );				
		
		//Debug.debugMethodEnd();
		return logical;
	}
	
   /**
    * @see #getLogicalOps
    */	
	public void setLogicalOps(LogicalOps logicalOps)
	{
		//Debug.debugMethodBegin( this, "setLogicalOps" );
		
		Element elem = XMLTools.getFirstElement( element );
		// remove operation tag if already exists
		if (elem != null) {
			element.removeChild( elem );
		}
		
		try {
			elem = ((LogicalOpsImpl.LogicImpl)logicalOps.getLogicalOperation()).getAsElement();
		} catch(Exception e) {}
		
		XMLTools.insertNodeInto( elem, element );
		
		//Debug.debugMethodEnd();
	}
	
   /**
    * returns the spatial operations that are to performed.
    * notice that it's possible to casscade operations, so that
    * at a lower level (as part of the logical operation for example)
    * more spatial operations can be defined.
    */	
	public SpatialOps getSpatialOps() throws CSWFilterException
	{
		//Debug.debugMethodBegin( this, "getSpatialOps" );
		
		// throw an exception if the first filter operation 
		// isn't a logical operation
		if ( getOperationType() != FilterConst.SPATIAL ) {
			throw new CSWFilterException("Filter doesn't contain a spatial operation as " +
									  "toplevel tag.");
		}
		
		SpatialOps spatial = null;
		
		Element elem = XMLTools.getFirstElement( element );
						
		spatial = new SpatialOpsImpl( elem );				
		
		//Debug.debugMethodEnd();
		return spatial;
	}
	
   /**
    * @see #getSpatialOps
    */	
	public void setSpatialOp(SpatialOps spatialOps)
	{
		//Debug.debugMethodBegin( this, "setSpatialOp" );
		
		Element elem = XMLTools.getFirstElement( element );
		// remove operation tag if already exists
		if (elem != null) {
			element.removeChild( elem );
		}
		
		try {
			elem = ((SpatialOpsImpl.SpatialImpl)spatialOps.getSpatialOperation()).getAsElement();
		} catch(Exception e) {}
		
		XMLTools.insertNodeInto( elem, element );
		
		//Debug.debugMethodEnd();
	}
	
}
