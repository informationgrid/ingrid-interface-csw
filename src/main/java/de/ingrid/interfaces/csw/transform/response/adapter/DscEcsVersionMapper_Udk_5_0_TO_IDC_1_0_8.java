/**
 * 
 */
package de.ingrid.interfaces.csw.transform.response.adapter;

import java.util.HashMap;

import de.ingrid.interfaces.csw.transform.response.IngridQueryHelper;

/**
 * @author joachim@wemove.com
 * 
 */
public class DscEcsVersionMapper_Udk_5_0_TO_IDC_1_0_8 implements DscEcsVersionMapper {

	private static HashMap map = new HashMap();

	private static DscEcsVersionMapper_Udk_5_0_TO_IDC_1_0_8 instance = null;

	private DscEcsVersionMapper_Udk_5_0_TO_IDC_1_0_8() {
		map.put(IngridQueryHelper.HIT_KEY_ADDRESS_STATE_ID, IngridQueryHelper.HIT_KEY_ADDRESS_COUNTRY_KEY);
		map.put(IngridQueryHelper.HIT_KEY_OBJECT_ST_BBOX_LOC_TOWN_NO, "areaid");
		map.put(IngridQueryHelper.HIT_KEY_OBJECT_ST_TOWNSHIP_TOWNSHIP, "spatial_ref_value.name_value");
		map.put(IngridQueryHelper.HIT_KEY_OBJECT_OBJECT_SPECIAL_REF, "object_reference.special_ref");
		map.put(IngridQueryHelper.HIT_KEY_OBJECT_GEO_TOPIC_CATEGORY, "t011_obj_topic_cat.topic_category");
		map.put(IngridQueryHelper.HIT_KEY_ADDRESS_ADDR_FROM_ID3, "parent3.address_node.addr_uuid");
		map.put(IngridQueryHelper.HIT_KEY_ADDRESS_ADDR_FROM_ID, "parent.address_node.addr_uuid");
		map.put(IngridQueryHelper.HIT_KEY_ADDRESS_ADDRESS, "t02_address.address_value");
		map.put(IngridQueryHelper.HIT_KEY_ADDRESS_COMM_TYPE, "t021_communication.commtype_value");
		map.put(IngridQueryHelper.HIT_KEY_OBJECT_OBJ_TO_ID, "object_reference.obj_to_uuid");
		map.put(IngridQueryHelper.HIT_KEY_OBJECT_AVAIL_ACCESS_NOTE, IngridQueryHelper.HIT_KEY_OBJECT_USE_TERMS_OF_USE);
		map.put(IngridQueryHelper.HIT_KEY_OBJECT_METADATA_LANGUAGE, IngridQueryHelper.HIT_KEY_OBJECT_METADATA_LANGUAGE_KEY);
		map.put(IngridQueryHelper.HIT_KEY_OBJECT_DATA_LANGUAGE, IngridQueryHelper.HIT_KEY_OBJECT_DATA_LANGUAGE_KEY);
	}

	public static DscEcsVersionMapper_Udk_5_0_TO_IDC_1_0_8 instance() {
		if (instance == null) {
			instance = new DscEcsVersionMapper_Udk_5_0_TO_IDC_1_0_8();
		}
		return instance;
	}

	/**
	 * Provides mapping between index field names from UDK 5.0 index mapping to
	 * IDC 1.0.8 index mapping.
	 * 
	 * @param fromField
	 *            The index field name to be mapped.
	 * @return The mapped index field name.
	 */
	public String mapIndexFieldName(String fromField) {
		String result = (String) map.get(fromField);
		if (result != null) {
			return result;
		}
		return fromField;
	}
}