/**
 * 
 */
package de.ingrid.interfaces.csw.transform.response.adapter;

/**
 * @author joachim@wemove.com
 *
 */
public interface DscEcsVersionMapper {

	/**
	 * Provides mapping between index field names of different DscEcs versions.
	 * 
	 * @param fromField The index field name to be mapped.
	 * @return The mapped index field name.
	 */
	public String mapIndexFieldName(String fromField);
	
}
