/**
 * 
 */
package de.ingrid.interfaces.csw.transform.response.adapter;

import de.ingrid.interfaces.csw.utils.IPlugVersionInspector;
import de.ingrid.utils.IngridHit;

/**
 * @author joachim@wemove.com
 * 
 */
public class IngridQueryFactory {

	public static String getAddressParentQueryStr(IngridHit hit, String addrUuid) {

		if (IPlugVersionInspector.getIPlugVersion(hit).equals(IPlugVersionInspector.VERSION_IDC_1_0_2_DSC_ADDRESS)) {
			return "children.address_node.addr_uuid:".concat(addrUuid).concat(" datatype:address ranking:score");
		} else {
			return "T022_adr_adr.adr_to_id:".concat(addrUuid).concat(" datatype:address ranking:score");		
		}
	}

}
