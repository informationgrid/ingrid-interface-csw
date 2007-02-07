

package de.ingrid.interfaces.csw.transform;


public interface LogicalOps extends FilterOperation {
	
   /**
    * returns the operation to be performed
    */	
	public Logic getLogicalOperation();
	
   /**
    * root interface of all logical operation interfaces
    */		
	public interface Logic {
		
	   /**
	    * returns the name of the operation represented
	    */	
		public String getOpName();
						
	   /**
	    * returns the first filter operation linked with
	    * with the logocal operation
	    */
	    public FilterOperation getFirstFilterOperation();
	    
	   /**
	    * @see #getFirstFilterOperation
	    */
	    public void setFirstFilterOperation(FilterOperation filterOperation);
	    
	   /**
	    * returns the additional filter operation linked with
	    * with the logocal operation. minimum requierment is one
	    * additional operation.
	    */
	    public FilterOperation[] getAdditionalFilterOperations();
	    
	   /**
	    *  @see #getAdditionalFilterOperations
	    */
	    public void addAdditionalFilterOperation(FilterOperation filterOperation); 
			
	}
	    
	
   /**
    * represents a logical AND between to conditions
    */		
	public interface And extends Logic {
    }

   /**
    * represents a logical OR between to conditions
    */
    public interface Or extends Logic {
    }


   /**
    * represents a logical NOT between to conditions
    */
    public interface Not extends Logic {
    }
    
}
