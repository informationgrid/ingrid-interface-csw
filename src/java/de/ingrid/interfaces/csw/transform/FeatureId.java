

package de.ingrid.interfaces.csw.transform;

/**
*
* The <FeatureId> element is used to encode the unique identifier for 
* any feature instance. Within a filter expression, the <FeatureId> 
* is used as a reference to a particular feature instance.
*
*/
public interface FeatureId {
	
   	
   /**
    * returns the feature id. A feature id is build from its feature type
    * name and its id seperated by a ".". e.g. Road.A565
    */	
	public String getFeatureId();
	
   /**
    * @see #getFeatureId
    */	
	public void setFeatureId(String id);
	
	/**
    * returns the name of the feature type the feature belongs to.
    * if no feature type is defined <tt>null</tt> will be returned.
    */	
	public String extractFeatureTypeName();
	
	/**
    * returns the id of the feature. '*' is used as wild card. which
    * means each feature of the feature type should be processed.
    */	
	public String extractId();
	
}
