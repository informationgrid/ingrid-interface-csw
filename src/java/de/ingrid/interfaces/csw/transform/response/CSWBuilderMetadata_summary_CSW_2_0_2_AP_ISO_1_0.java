/*
 * Copyright (c) 1997-2007 by wemove GmbH
 */
package de.ingrid.interfaces.csw.transform.response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Namespace;

import de.ingrid.utils.IngridHit;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class CSWBuilderMetadata_summary_CSW_2_0_2_AP_ISO_1_0 extends CSW_2_0_2_BuilderMetadataCommon {

    private static Log log = LogFactory.getLog(CSWBuilderMetadata_summary_CSW_2_0_2_AP_ISO_1_0.class);

    public Element build() throws Exception {
        
        this.setNSPrefix("gmd");

        // define used name spaces
		Namespace gco = new Namespace("gco", "http://www.isotc211.org/2005/gco");
		Namespace gmd = new Namespace("gmd", "http://www.isotc211.org/2005/gmd");
		Namespace srv = new Namespace("srv", "http://www.isotc211.org/2005/srv");

        String objectId = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_OBJ_ID);
        String udkClass = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_OBJ_CLASS);
        String typeName;
        if (udkClass.equals("1")) {
            typeName = "dataset";
        } else if (udkClass.equals("3")) {
            typeName = "service";
        } else {
        	if (log.isInfoEnabled()) {
        		log.info("Unsupported UDK class " + udkClass
                    + ". Only class 1 and 3 are supported by the CSW interface.");
        	}
            return null;
        }

        Element metaData = DocumentFactory.getInstance().createElement("gmd:MD_Metadata",
        "http://schemas.opengis.net/iso19115summary");
        metaData.add(gco);
        metaData.add(gmd);
        metaData.add(srv);

        this.addFileIdentifier(metaData, objectId);
        this.addLanguage(metaData, hit);
        this.addHierarchyLevel(metaData.addElement("hierarchyLevel"), typeName);
        this.addContacts(metaData, hit);
        this.addDateStamp(metaData, hit);
        if (typeName.equals("dataset")) {
            this.addIdentificationInfoDataset(metaData, hit);
        } else {
            this.addIdentificationInfoService(metaData, hit);
        }

        return metaData;
    }

    private void addCitation(Element parent, IngridHit hit) {
        Element ciCitation = parent.addElement("citation")
        .addElement("gmd:CI_Citation");
        // add title
        this.addSMXMLCharacterString(ciCitation.addElement("title"), IngridQueryHelper.getDetailValueAsString(hit,
                IngridQueryHelper.HIT_KEY_OBJECT_TITLE));
        // add dates (creation, revision etc.)
        super.addCitationReferenceDates(ciCitation, hit, null);
    }
    
    private void addIdentificationInfoService(Element metaData, IngridHit hit) {
        Element svServiceIdentification = metaData.addElement("identificationInfo").addElement(
                "gmd:SV_ServiceIdentification");

        // add citation construct
        this.addCitation(svServiceIdentification, hit);

        
        // add abstract
        this.addSMXMLCharacterString(svServiceIdentification.addElement("abstract"), IngridQueryHelper.getDetailValueAsString(hit,
                IngridQueryHelper.HIT_KEY_OBJECT_DESCR));

        // add resourceConstraint
        this.addSMXMLCharacterString(svServiceIdentification.addElement("resourceConstraints").addElement("gmd:MD_Constraints").addElement("gmd:useLimitation"), IngridQueryHelper.getDetailValueAsString(hit,
                IngridQueryHelper.HIT_KEY_OBJECT_AVAIL_ACCESS_NOTE));
        
        
        this.addSMXMLCharacterString(svServiceIdentification.addElement("serviceType"), IngridQueryHelper
                        .getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERVICE_TYPE));

        String[] serviceTypeVersions = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERVICE_TYPE_VERSION);
        if (serviceTypeVersions != null) {
            for (int i=0; i< serviceTypeVersions.length; i++) {
                this.addSMXMLCharacterString(svServiceIdentification.addElement("serviceTypeVersion"), serviceTypeVersions[i]);
            }
        }

    }

    private void addIdentificationInfoDataset(Element metaData, IngridHit hit) {
        Element mdDataIdentification = metaData.addElement("identificationInfo").addElement(
                "gmd:MD_DataIdentification");

        // add citation construct
        this.addCitation(mdDataIdentification, hit);

        // add abstract
        this.addSMXMLCharacterString(mdDataIdentification.addElement("abstract"), IngridQueryHelper.getDetailValueAsString(hit,
                IngridQueryHelper.HIT_KEY_OBJECT_DESCR));

        // add resourceConstraint
        this.addSMXMLCharacterString(mdDataIdentification.addElement("resourceConstraints").addElement("gmd:MD_Constraints").addElement("gmd:useLimitation"), IngridQueryHelper.getDetailValueAsString(hit,
                IngridQueryHelper.HIT_KEY_OBJECT_AVAIL_ACCESS_NOTE));
        
    }    
    
}
