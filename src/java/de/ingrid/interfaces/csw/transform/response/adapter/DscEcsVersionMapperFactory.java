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
public class DscEcsVersionMapperFactory {

	public static DscEcsVersionMapper getEcsDscVersionMapper(IngridHit hit) {

		if (IPlugVersionInspector.getIPlugVersion(hit).equals(IPlugVersionInspector.VERSION_IDC_1_0_5_DSC_OBJECT)) {
			return DscEcsVersionMapper_Udk_5_0_TO_IDC_1_0_5.instance();
		} else if (IPlugVersionInspector.getIPlugVersion(hit).equals(IPlugVersionInspector.VERSION_IDC_1_0_3_DSC_OBJECT)) {
			return DscEcsVersionMapper_Udk_5_0_TO_IDC_1_0_3.instance();
		} else if (IPlugVersionInspector.getIPlugVersion(hit).equals(IPlugVersionInspector.VERSION_IDC_1_0_2_DSC_OBJECT)) {
			return DscEcsVersionMapper_Udk_5_0_TO_IDC_1_0_2.instance();
		} else if (IPlugVersionInspector.getIPlugVersion(hit).equals(IPlugVersionInspector.VERSION_IDC_1_0_5_DSC_ADDRESS)) {
			return DscEcsVersionMapper_Udk_5_0_TO_IDC_1_0_5.instance();
		} else if (IPlugVersionInspector.getIPlugVersion(hit).equals(IPlugVersionInspector.VERSION_IDC_1_0_2_DSC_ADDRESS)) {
			return DscEcsVersionMapper_Udk_5_0_TO_IDC_1_0_2.instance();
		}

		return DscEcsVersionMapper_Dummy.instance();
	}

}
