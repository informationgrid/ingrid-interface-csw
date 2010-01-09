

package de.ingrid.interfaces.csw.transform;

import de.ingrid.interfaces.csw.exceptions.CSWFilterException;


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
public interface Filter {		
	
   /**
    * returns the type of operation that's the toplevel tag
    * of the filter
    */	
	public int getOperationType();
	
   /**
   	* returns the ids (incl. feature type names) of the features that 
   	* are target by the filter.
   	*/
	public FeatureId[] getFeatureIds();
	
   /**
    * @see #getFeatureIds
    */	
	public void addFeatureId(FeatureId featureId);
	
   /**
    * returns the comparison operations that are to performed.
    * notice that it's possible to casscade operations, so that
    * at a lower level (as part of the logical operation for example)
    * more comparison operations can be defined.
    */	
	public ComparisonOps getComparisonOps() throws CSWFilterException;
	
   /**
    * @see #getComparisonOps
    */	
	public void setComparisonOps(ComparisonOps compOps);
	
   /**
    * returns the logical operations that are to performed.
    * notice that it's possible to casscade operations, so that
    * at a lower level (as part of the spatial operation for example)
    * more logical operations can be defined.
    */	
	public LogicalOps getLogicalOps() throws CSWFilterException;
	
   /**
    * @see #getLogicalOps
    */	
	public void setLogicalOps(LogicalOps logicalOps);
	
   /**
    * returns the spatial operations that are to performed.
    * notice that it's possible to casscade operations, so that
    * at a lower level (as part of the logical operation for example)
    * more spatial operations can be defined.
    */	
	public SpatialOps getSpatialOps() throws CSWFilterException;
	
   /**
    * @see #getSpatialOps
    */	
	public void setSpatialOp(SpatialOps spatialOps);
	
}
