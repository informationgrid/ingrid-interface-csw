

package de.ingrid.interfaces.csw.transform;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.ingrid.interfaces.csw.tools.XMLTools;







public class LogicalOpsImpl implements LogicalOps {
	
	private Element element = null;
	
	public LogicalOpsImpl(Element element) {
		this.element = element;
	}
	
   /**
    * creates a new empty logical operation tag
    */	
	public static LogicalOps createLocical_Ops(Document doc, String ops)
	{
		//Debug.debugMethodBegin( "LogicalOpsImpl", "createLocical_Ops" );
		
		LogicalOps op = new LogicalOpsImpl( doc.createElement( ops ) );		
		
		//Debug.debugMethodEnd();
		return op;
	}
		
   /**
    * returns the operation to be performed
    */	
	public Logic getLogicalOperation()
	{
		//Debug.debugMethodBegin( this, "getLogicalOperation" );
		
		Logic logic = null;
		
		if (element.getLocalName().equals(FilterConst.And)) {
			logic = new AndImpl();
		}
	   else
	    if (element.getLocalName().equals(FilterConst.Or)) {
			logic = new OrImpl();
		}
	   else		
	    if (element.getLocalName().equals(FilterConst.Not)) {
			logic = new NotImpl();
		}	
		
		//Debug.debugMethodEnd();
		return logic;
	}
	
	
	////////////////////////////////////////////////////////////////////////
	//                       inner classes                                //
	////////////////////////////////////////////////////////////////////////
	
   /**
    * root interface of all logical operation interfaces
    */		
	public class LogicImpl implements Logic {
		
		public Element getAsElement()
		{
			return element;
		}
		
	   /**
	    * returns the name of the operation represented
	    */		
		public String getOpName()
		{					    		    		    		
			return element.getLocalName();
		}
		
		private FilterOperation getOperation(Element elem)
		{
			//Debug.debugMethodBegin( this, "getOperation");
	    	
	    	FilterOperation operation = null;	    		    	
	    	
	    	String nodeName = elem.getLocalName();
	    	
			if ( nodeName.equals(FilterConst.PropertyIsEqualTo) ||
				 nodeName.equals(FilterConst.PropertyIsNotEqualTo) ||
			     nodeName.equals(FilterConst.PropertyIsLessThan) ||
			     nodeName.equals(FilterConst.PropertyIsGreaterThan) ||
			     nodeName.equals(FilterConst.PropertyIsLessThanOrEqualTo) ||
			     nodeName.equals(FilterConst.PropertyIsGreaterThanOrEqualTo) ||
			     nodeName.equals(FilterConst.PropertyIsLike) ||
			     nodeName.equals(FilterConst.PropertyIsNull) ||
			     nodeName.equals(FilterConst.PropertyIsBetween) ) {
				operation=  new ComparisonOpsImpl( elem );
			}
		   else
		    if ( nodeName.equals( FilterConst.And ) ||
		         nodeName.equals( FilterConst.Or ) ||
		         nodeName.equals( FilterConst.Not ) ){
		        operation = new LogicalOpsImpl( elem );
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
		        operation = new SpatialOpsImpl( elem );
		    }
		    //TODO support for mathematical operations 
		    //Debug.debugMethodEnd();    
		    return operation;
		}
			
		
	   /**
	    * returns the first filter operation linked with
	    * with the logocal operation
	    */
	    public FilterOperation getFirstFilterOperation()
	    {
	    	//Debug.debugMethodBegin( this, "getFirstFilterOperation");
	    	
	    	Element elem = XMLTools.getFirstElement( element );
	    	FilterOperation operation = getOperation( elem );
		    		
			//Debug.debugMethodEnd();    
	    	return operation;
	    }
	    
	   /**
	    * @see #getFirstFilterOperation
	    */
	    public void setFirstFilterOperation(FilterOperation filterOperation)
	    {
	    	//Debug.debugMethodBegin( this, "getFirstFilterOperation");
	    	
	    	Element elem = null;
	    	
	    	try {
		    	if (filterOperation instanceof LogicalOps) {
		    		Logic lo = ((LogicalOps)filterOperation).getLogicalOperation();
		    		elem = ((LogicImpl)lo).getAsElement();
		    	}
		       else	
		        if (filterOperation instanceof ComparisonOps) {
		    		ComparisonOps.CompOperation co = ((ComparisonOps)filterOperation).getCompOperation();
		    		elem = ((ComparisonOpsImpl.CompOperationImpl)co).getAsElement();
		    	}
		       else	
		        if (filterOperation instanceof SpatialOps) {
		    		SpatialOps.Spatial sp = ((SpatialOps)filterOperation).getSpatialOperation();
		    		elem = ((SpatialOpsImpl.SpatialImpl)sp).getAsElement();
		    	}	
	 		} catch(Exception e) {
	 			//Debug.debugSimpleMessage( e.toString() );
	 		}
	    	
	    	XMLTools.insertNodeInto( elem, element );
	    	
	    	//Debug.debugMethodEnd();
	    }
	    
	   /**
	    * returns the additional filter operation linked with
	    * with the logocal operation. minimum requierment is one
	    * additional operation.
	    */
	    public FilterOperation[] getAdditionalFilterOperations()
	    {
	    	//Debug.debugMethodBegin( this, "getAdditionalFilterOperations");
	    	
	    	FilterOperation[] fo = null;
	    	ArrayList list = new ArrayList();
	    	
	    	NodeList nl = element.getChildNodes();
	    	
	    	if (nl != null && nl.getLength() > 0) {
	    		
	    		int cnt = 0;
	    		for (int i = 0; i < nl.getLength(); i++) {
	    			if (nl.item(i) instanceof Element) {
	    				cnt++;
	    				if ( cnt > 1 ) {
	    					list.add( getOperation( (Element)nl.item(i) ) );
	    				}
	    			}
			    }
	    		
	    	}
	    	
	    	if (list.size() > 0) {
	    		fo = (FilterOperation[])list.toArray( new FilterOperation[ list.size() ] );
	    	}
	    	
	    	//Debug.debugMethodEnd();
	    	return fo;
	    }
	    
	   /**
	    *  @see #getAdditionalFilterOperations
	    */
	    public void addAdditionalFilterOperation(FilterOperation filterOperation)
	    {
	    	//Debug.debugMethodBegin( this, "addAdditionalFilterOperation");
	    	
	    	Element elem = null;
	    	
	    	try {
		    	if (filterOperation instanceof LogicalOps) {
		    		Logic lo = ((LogicalOps)filterOperation).getLogicalOperation();
		    		elem = ((LogicImpl)lo).getAsElement();
		    	}
		       else	
		        if (filterOperation instanceof ComparisonOps) {
		    		ComparisonOps.CompOperation co = ((ComparisonOps)filterOperation).getCompOperation();
		    		elem = ((ComparisonOpsImpl.CompOperationImpl)co).getAsElement();
		    	}
		       else	
		        if (filterOperation instanceof SpatialOps) {
		    		SpatialOps.Spatial sp = ((SpatialOps)filterOperation).getSpatialOperation();
		    		elem = ((SpatialOpsImpl.SpatialImpl)sp).getAsElement();
		    	}	
	 		} catch(Exception e) {
	 			//Debug.debugSimpleMessage( e.toString() );
	 		}
	    	
	    	XMLTools.insertNodeInto( elem, element );
	    	
	    	//Debug.debugMethodEnd();
	    }
				  			
	}
	    
	
   /**
    * represents a logical AND between to conditions
    */		
	public class AndImpl extends LogicImpl implements And {
    }

   /**
    * represents a logical OR between to conditions
    */
    public class OrImpl extends LogicImpl implements Or {
    }


   /**
    * represents a logical NOT between to conditions
    */
    public class NotImpl extends LogicImpl implements Not  {
    }
    
}
