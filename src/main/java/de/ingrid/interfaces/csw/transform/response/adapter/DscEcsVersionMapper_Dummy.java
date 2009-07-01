/**
 * 
 */
package de.ingrid.interfaces.csw.transform.response.adapter;


/**
 * @author joachim@wemove.com
 * 
 */
public class DscEcsVersionMapper_Dummy implements DscEcsVersionMapper {

	private static DscEcsVersionMapper_Dummy instance = null;

	private DscEcsVersionMapper_Dummy() {
	}

	public static DscEcsVersionMapper_Dummy instance() {
		if (instance == null) {
			instance = new DscEcsVersionMapper_Dummy();
		}
		return instance;
	}

	/**
	 * Dummy mapper method, bounces input to output.
	 * 
	 * @param fromField
	 *            The index field name to be mapped.
	 * @return The mapped index field name.
	 */
	public String mapIndexFieldName(String fromField) {
		return fromField;
	}
}
