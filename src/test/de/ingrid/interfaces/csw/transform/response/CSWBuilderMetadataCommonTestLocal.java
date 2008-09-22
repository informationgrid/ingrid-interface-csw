package de.ingrid.interfaces.csw.transform.response;

import junit.framework.TestCase;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Namespace;

import de.ingrid.ibus.client.BusClient;
import de.ingrid.interfaces.csw.tools.CSWInterfaceConfig;
import de.ingrid.utils.IBus;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

public class CSWBuilderMetadataCommonTestLocal extends TestCase {

	protected void setUp() throws Exception {
        if (CSWInterfaceConfig.getInstance().getIBus() != null) {
        	return;
        }
		
		// initialize ibus
        IBus bus = null;
        BusClient client = null;
        try {
            client = BusClient.instance();
            bus = (IBus) client.getBus();
        } catch (Exception e) {
            System.out.println("init iBus communication: " + e.getMessage());
        }

        if (bus != null) {
            CSWInterfaceConfig.getInstance().setIBus(bus);
        }
		
		super.setUp();
	}

	public void testAddContactBlock() throws Exception {
		// object connected to person "Herr Dr. Reinhard Hartwich", typ2, connected via 2 units to institution "BGR - Bundesanstalt für Geowissenschaften und Rohstoffe"
		IngridQuery query = QueryStringParser.parse("t01_object.obj_id:EC602AC5-8967-11D4-AAE5-0050DA769D0B iplugs:\"/torwald-group:torwald-iplug-udk-db_uba\" ranking:any datatype:any");
		IngridHits hits = CSWInterfaceConfig.getInstance().getIBus().search(query, 10, 1, 0, 10000);
		IngridHit hit = hits.getHits()[0];
		IngridHitDetail detail = CSWInterfaceConfig.getInstance().getIBus().getDetail(hit, query, IngridQueryHelper.REQUESTED_STRING_SUMMARY);
		hit.put("detail", detail);
		
		CSWBuilderMetadataCommon cswBuilderMetadataCommon = new CSWBuilderMetadata_summary_DE_1_0_1();
		
        // define used name spaces
        Namespace smXML = new Namespace("smXML", "http://metadata.dgiwg.org/smXML");
        Namespace iso19115summary = new Namespace("iso19115summary", "http://schemas.opengis.net/iso19115summary");
        Namespace iso19119 = new Namespace("iso19119", "http://schemas.opengis.net/iso19119");
        Namespace csw = new Namespace("csw", "http://www.opengis.net/cat/csw");

		Element metaData = DocumentFactory.getInstance().createElement("iso19115summary:MD_Metadata",
        "http://schemas.opengis.net/iso19115summary");
        metaData.add(iso19115summary);
        metaData.add(smXML);
        metaData.add(iso19119);
        metaData.add(csw);
        
		cswBuilderMetadataCommon.addContactBlocks(metaData, hit, "prefix");
		metaData.asXML();

		// object connected directly to an institution with no further parents
		// 895BFD4F-5661-11D3-AA34-0080AD746A41 (Schutzgebietskarte der Bundeswehr)
		query = QueryStringParser.parse("t01_object.obj_id:895BFD4F-5661-11D3-AA34-0080AD746A41 iplugs:\"/torwald-group:torwald-iplug-udk-db_uba\" ranking:any datatype:any");
		hits = CSWInterfaceConfig.getInstance().getIBus().search(query, 10, 1, 0, 10000);
		hit = hits.getHits()[0];
		detail = CSWInterfaceConfig.getInstance().getIBus().getDetail(hit, query, IngridQueryHelper.REQUESTED_STRING_SUMMARY);
		hit.put("detail", detail);
		
		cswBuilderMetadataCommon.addContactBlocks(metaData, hit, "prefix");
		metaData.asXML();
		
		// object in hh catalog, NO parent address mapping extension
		query = QueryStringParser.parse("t01_object.obj_id:E1F8ED16-BBA8-11D4-BDA8-00105AF533E3 iplugs:\"/torwald-group:torwald-iplug-udk-db_hh\" ranking:any datatype:any");
		hits = CSWInterfaceConfig.getInstance().getIBus().search(query, 10, 1, 0, 10000);
		hit = hits.getHits()[0];
		detail = CSWInterfaceConfig.getInstance().getIBus().getDetail(hit, query, IngridQueryHelper.REQUESTED_STRING_SUMMARY);
		hit.put("detail", detail);
		
		cswBuilderMetadataCommon.addContactBlocks(metaData, hit, "prefix");
		metaData.asXML();
	}
}
