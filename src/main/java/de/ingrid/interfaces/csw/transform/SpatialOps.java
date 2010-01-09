

package de.ingrid.interfaces.csw.transform;

import de.ingrid.interfaces.csw.exceptions.CSWFilterException;



public interface SpatialOps extends FilterOperation {
	
   /**
    * returns the operation to be performed
    */		
	public Spatial getSpatialOperation() throws CSWFilterException;
	
   /**
    * root interface of all spatia operation interfaces
    */		
	public interface Spatial {
		
		/**
        * returns the name of the property that should be compared
        */	
    	public Expression.PropertyName getPropertyName();
    	
       /**
	    * @see #getPropertyName
	    */	
    	public void setPropertyName(Expression.PropertyName name);
    	
       /**
	    * returns the geometry that's the target of comparison
	    */	
    	//public GMLGeometry getGeometry() throws CSWFilterException;
    	
       /**
	    * @see #getGeometry
	    */	
    	//public void setGeometry(GMLGeometry geometry);
		
	}
	
   /**
    * represents the comparison if two geomerties are spatial equal
    */	
    public interface Equals extends Spatial {
    }

   /**
    * represents the comparison if two geomerties are spatial disjoint
    */
    public interface Disjoint extends Spatial {
    }

   /**
    * represents the comparison if two geomerties spatially intersects
    */
    public interface Intersects extends Spatial {
    }

   /**
    * represents the comparison if two geomerties spatialy touches
    */
    public interface Touches extends Spatial {
    }

   /**
    * represents the comparison if two geomerties spatially crosses
    */
    public interface Crosses extends Spatial {
    }

   /**
    * represents the comparison if the first geometry is within a distance
    * around the second
    */
    public interface Within extends Spatial {
    	
       /**
	    * returns the max distance of the first geometry to the
	    * second to be selected
	    */	
    	public Distance getDistance();
    	
       /**
	    * @see #getDistance
	    */	
    	public void setDistance(Distance distance);
    }

   /**
    * represents the comparison if the first geometry contains the second
    */
    public interface Contains extends Spatial {
    }

   /**
    * represents the comparison if the first geometry is inside the second
    */
    public interface Inside extends Spatial {
    }

   /**
    * represents the comparison if the first geometry overlaps the second
    */
    public interface Overlaps extends Spatial {
    }

   /**
    * represents the comparison if the first geometry is beyond a
    * distance two the second
    */
    public interface Beyond extends Spatial {
    	
    	/**
	    * returns the max distance of the first geometry to the
	    * second to be selected
	    */	
    	public Distance getDistance();
    	
       /**
	    * @see #getDistance
	    */	
    	public void setDistance(Distance distance);
    	
    }
    
   /**
    * represents the comparison if the first geometry is within the box
    */
    public interface Box extends Spatial {
    	
       /**
        * returns the Box the first geometry is compared to
        */	
    	//public GMLBox getBox() throws CSWFilterException;
    	
    }
    
    public interface Distance {
    	
       /**
	    * returns the distance 
	    */	
    	public double getDistance();
    	
       /**
	    * sets the distance 
	    */	
    	public void setDistance(double distance);
    	
       /**
	    * returns the units the distance is measured at
	    */	
    	public String getUnits();
    	
       /**
	    * @see #getUnits
	    */	
    	public void setUnits(String units);
    	
    }
    
}
