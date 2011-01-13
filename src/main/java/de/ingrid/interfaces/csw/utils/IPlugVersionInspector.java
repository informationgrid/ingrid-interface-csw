/**
 * 
 */
package de.ingrid.interfaces.csw.utils;

import java.util.ArrayList;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.interfaces.csw.tools.CSWInterfaceConfig;
import de.ingrid.interfaces.csw.transform.response.IngridQueryHelper;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.PlugDescription;

/**
 * Helper Class Obtains the iPlugVersion
 * 
 * 
 * @author joachim@wemove.com
 *
 */
public class IPlugVersionInspector {

	public static final String VERSION_IDC_1_0_2_DSC_OBJECT = "VERSION_IDC_1_0_2_DSC_OBJECT";

	public static final String VERSION_IDC_1_0_3_DSC_OBJECT = "VERSION_IDC_1_0_3_DSC_OBJECT";
	
	public static final String VERSION_IDC_1_0_5_DSC_OBJECT = "VERSION_IDC_1_0_5_DSC_OBJECT";
	
	public static final String VERSION_IDC_1_0_8_DSC_OBJECT = "VERSION_IDC_1_0_8_DSC_OBJECT";
	
	public static final String VERSION_UDK_5_0_DSC_OBJECT = "VERSION_UDK_5_0_DSC_OBJECT";

	public static final String VERSION_IDC_1_0_2_DSC_ADDRESS = "VERSION_IDC_1_0_2_DSC_ADDRESS";
	
	public static final String VERSION_IDC_1_0_5_DSC_ADDRESS = "VERSION_IDC_1_0_5_DSC_ADDRESS";
	
	public static final String VERSION_UDK_5_0_DSC_ADDRESS = "VERSION_UDK_5_0_DSC_ADDRESS";
	
	public static final String VERSION_UNKNOWN = "VERSION_UNKNOWN";
	
	private static CacheManager singletonManager;
	
	private static Log log = LogFactory.getLog(IPlugVersionInspector.class);
	
	static {
		try {
			singletonManager = CacheManager.create();
			Cache memoryOnlyCache = new Cache("plugVersionCache", 100, false, false, 300, 300);
			singletonManager.addCache(memoryOnlyCache);		
		} catch (CacheException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getIPlugVersion(PlugDescription plugDescription) {
		
		ArrayList fields = (ArrayList)plugDescription.get(PlugDescription.FIELDS);
		
		if (fields != null && fields.contains("object_use.terms_of_use")) {
			return VERSION_IDC_1_0_8_DSC_OBJECT;
		} else if (fields != null && fields.contains("t01_object.data_language_key") && fields.contains("t01_object.metadata_language_key") && fields.contains("t02_address.country_key")) {
			return VERSION_IDC_1_0_5_DSC_OBJECT;
		} else if (fields.contains("t01_object.obj_id") && fields.contains("parent.object_node.obj_uuid") && fields.contains("object_access.terms_of_use")) {
			return VERSION_IDC_1_0_3_DSC_OBJECT;
		} else if (fields.contains("t01_object.obj_id") && fields.contains("parent.object_node.obj_uuid")) {
			return VERSION_IDC_1_0_2_DSC_OBJECT;
		} else if (fields.contains("t01_object.obj_id") && fields.contains(IngridQueryHelper.HIT_KEY_OBJECT_ST_BBOX_LOC_TOWN_NO)) {
			return VERSION_UDK_5_0_DSC_OBJECT;
		} else if (fields != null && fields.contains("t02_address.adr_id") && fields.contains("parent.address_node.addr_uuid") && fields.contains("t02_address.country_key")) {
			return VERSION_IDC_1_0_5_DSC_ADDRESS;
		} else if (fields.contains("t02_address.adr_id") && fields.contains("parent.address_node.addr_uuid")) {
			return VERSION_IDC_1_0_2_DSC_ADDRESS;
		} else if (fields.contains("t02_address.adr_id") && fields.contains(IngridQueryHelper.HIT_KEY_ADDRESS_ADDR_FROM_ID)) {
			return VERSION_UDK_5_0_DSC_ADDRESS;
		}

		return VERSION_UNKNOWN;
	}
	
	public static String getIPlugVersion(IngridHit hit) {
		Cache cache = singletonManager.getCache("plugVersionCache");
		String iPlugVersion = null;
		Element cacheHit;
		try {
			cacheHit = cache.get(hit.getPlugId());
		} catch (Exception e) {
			cacheHit = null;
			if (log.isDebugEnabled()) {
				log.debug("Error accessing plugVersionCache.", e);
			}
		} 
		if (cacheHit == null || cache.isExpired(cacheHit)) {
			PlugDescription plugDescription = CSWInterfaceConfig.getInstance().getIBus().getIPlug(hit.getPlugId());;
			iPlugVersion = getIPlugVersion(plugDescription);
			cache.put(new Element(hit.getPlugId(), iPlugVersion));
		} else {
			iPlugVersion = (String)cacheHit.getValue();
		}
		return iPlugVersion;
	}
	
}
