package de.ingrid.interfaces.csw.transform.response;

import junit.framework.TestCase;
import de.ingrid.ibus.client.BusClient;
import de.ingrid.ibus.client.BusClientFactory;
import de.ingrid.interfaces.csw.tools.CSWInterfaceConfig;
import de.ingrid.utils.IBus;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;

public class IngridQueryHelperTestLocal extends TestCase {

	
	protected void setUp() throws Exception {
        if (CSWInterfaceConfig.getInstance().getIBus() != null) {
        	return;
        }
		
		// initialize ibus
        IBus bus = null;
        BusClient client = null;
        try {
            client = BusClientFactory.createBusClient();
            bus = (IBus) client.getNonCacheableIBus();
        } catch (Exception e) {
            System.out.println("init iBus communication: " + e.getMessage());
        }

        if (bus != null) {
            CSWInterfaceConfig.getInstance().setIBus(bus);
        }
		
		super.setUp();
	}

	public void testGetCompleteAddress() throws Exception {

		// person "Herr Dr. Albrecht Blüthgen", typ=2
		IngridHit address = IngridQueryHelper.getCompleteAddress("05F9A571-D866-11D2-AB09-00E0292DC06B", "/torwald-group:torwald-iplug-udk-db_uba_addr");
		IngridHitDetail addressDetail = (IngridHitDetail) address.get("detail");
		assertEquals((String)addressDetail.get(IngridQueryHelper.HIT_KEY_ADDRESS_INSTITUITION), "BAfM - Bundesanstalt für Milchforschung, Institut für Hygiene und Produktsicherheit");
		
		// institution "AMilGeo - Amt für Militärisches Geowesen", typ=0
		address = IngridQueryHelper.getCompleteAddress("11F2FC81-6620-11D3-AA34-0080AD746A41", "/torwald-group:torwald-iplug-udk-db_uba_addr");
		addressDetail = (IngridHitDetail) address.get("detail");
		assertEquals((String)addressDetail.get(IngridQueryHelper.HIT_KEY_ADDRESS_INSTITUITION), "AMilGeo - Amt für Militärisches Geowesen");

		// unit "Institut für Hygiene und Produktsicherheit", typ=1
		address = IngridQueryHelper.getCompleteAddress("C178EAA1-5EF2-11D3-AA34-0080AD746A41", "/torwald-group:torwald-iplug-udk-db_uba_addr");
		addressDetail = (IngridHitDetail) address.get("detail");
		assertEquals((String)addressDetail.get(IngridQueryHelper.HIT_KEY_ADDRESS_INSTITUITION), "BAfM - Bundesanstalt für Milchforschung, Institut für Hygiene und Produktsicherheit");
		
		// person "Herr Dr. Bruno Stöckle", typ2, connected directly to typ=0 institution
		address = IngridQueryHelper.getCompleteAddress("05F9A57D-D866-11D2-AB09-00E0292DC06B", "/torwald-group:torwald-iplug-udk-db_uba_addr");
		addressDetail = (IngridHitDetail) address.get("detail");
		assertEquals((String)addressDetail.get(IngridQueryHelper.HIT_KEY_ADDRESS_INSTITUITION), "Bayrisches Landesamt für Denkmalpflege, Forschungsbereich Materialkonservierung");
		
		// person "Herr Dr. Reinhard Hartwich", typ2, connected via 2 units to institution "BGR - Bundesanstalt für Geowissenschaften und Rohstoffe"
		address = IngridQueryHelper.getCompleteAddress("918DB286-F7B5-11D6-AD71-0050DA769D0B", "/torwald-group:torwald-iplug-udk-db_uba_addr");
		addressDetail = (IngridHitDetail) address.get("detail");
		assertEquals((String)addressDetail.get(IngridQueryHelper.HIT_KEY_ADDRESS_INSTITUITION), "BGR - Bundesanstalt für Geowissenschaften und Rohstoffe, Fachgruppe 4.2 Mineralogie, Bodenkunde, Referat 4.24 Informationsgrundlagen im Boden- und Umweltschutz (Dienstbereich Berlin)");
		
		// person "Herr Thomas Reher", typ2, in hh udk address iplug with NO parent address mapping extension
		address = IngridQueryHelper.getCompleteAddress("2B60A1DD-DAC9-11D2-9A86-080000507261", "/torwald-group:torwald-iplug-udk-db_hh_addr");
		addressDetail = (IngridHitDetail) address.get("detail");
		assertEquals((String)addressDetail.get(IngridQueryHelper.HIT_KEY_ADDRESS_INSTITUITION), "Behörde für Inneres, Polizei Hamburg, Landespolizeiinspektion, Polizeiinspektion Süd, Fachabteilung Wasserschutzpolizei, Kriminalpolizeiliche Ermittlungsdienststellen (insb. PD 455)");
		
	}
}
