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
public class CSWBuilderMetadata_brief_DE_1_0_1 extends CSWBuilderMetadataCommon {

    private static Log log = LogFactory.getLog(CSWBuilderMetadata_brief_DE_1_0_1.class);

    public Element build() throws Exception {

        this.setNSPrefix("iso19115brief");
        
        // define used name spaces
        Namespace smXML = new Namespace("smXML", "http://metadata.dgiwg.org/smXML");
        Namespace iso19115brief = new Namespace("iso19115brief", "http://schemas.opengis.net/iso19115brief");
        Namespace iso19119 = new Namespace("iso19119", "http://schemas.opengis.net/iso19119");

        String objectId = IngridQueryHelper.getDetailValueAsString(hit, "t01_object.obj_id");

        String udkClass = IngridQueryHelper.getDetailValueAsString(hit, "t01_object.obj_class");
        String typeName;
        if (udkClass.equals("1")) {
            typeName = "dataset";
        } else if (udkClass.equals("3")) {
            typeName = "service";
        } else {
            log.info("Unsupported UDK class " + udkClass
                    + ". Only class 1 and 3 are supported by the CSW interface.");
            return null;
        }

        Element metaData = DocumentFactory.getInstance().createElement("iso19115brief:MD_Metadata",
                "http://schemas.opengis.net/iso19115brief");
        metaData.add(iso19115brief);
        metaData.add(smXML);
        metaData.add(iso19119);

        this.addFileIdentifier(metaData, objectId);
        this.addHierarchyLevel(metaData.addElement("hierarchyLevel"), typeName);
        this.addContact(metaData, hit);
        if (typeName.equals("dataset")) {
            this.addIdentificationInfoDataset(metaData, hit);
        } else {
            this.addIdentificationInfoService(metaData, hit);
        }

        return metaData;

    }

    private void addIdentificationInfoService(Element metaData, IngridHit hit) {
        Element cswServiceIdentification = metaData.addElement("iso19115brief:identificationInfo").addElement(
                "iso19115brief:CSW_ServiceIdentification");
        this.addSMXMLCharacterString(cswServiceIdentification.addElement("smXML:title"), IngridQueryHelper.getDetailValueAsString(
                hit, IngridQueryHelper.HIT_KEY_OBJECT_TITLE));
        this.addSMXMLCharacterString(cswServiceIdentification.addElement("iso19119:serviceType"), IngridQueryHelper
                .getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERVICE_TYPE));
        String[] serviceTypeVersions = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERVICE_TYPE_VERSION);
        if (serviceTypeVersions != null) {
            for (int i=0; i< serviceTypeVersions.length; i++) {
                this.addSMXMLCharacterString(cswServiceIdentification.addElement("iso19119:serviceTypeVersion"), serviceTypeVersions[i]);
            }
        }
        cswServiceIdentification.addElement("iso19119:couplingType").addElement("iso19119:CSW_CouplingType")
                .addAttribute("codeList", "http://opengis.org/codelistRegistry?CSW_CouplingType").addAttribute(
                        "codeListValue",
                        IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_OBJECT_SPECIAL_REF));
    }

    private void addIdentificationInfoDataset(Element metaData, IngridHit hit) {
        Element mdDataIdentification = metaData.addElement("iso19115brief:identificationInfo").addElement(
                "smXML:MD_DataIdentification");
        this.addSMXMLCharacterString(mdDataIdentification.addElement("smXML:title"), IngridQueryHelper.getDetailValueAsString(hit,
                IngridQueryHelper.HIT_KEY_OBJECT_TITLE));
        // TODO: transform via UDK codelist 527
        // T011_obj_geo_topic_cat.topic_category
        mdDataIdentification.addElement("smXML:topicCategory").addElement("smXML:MD_TopicCategoryCode").addText(
                IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_GEO_TOPIC_CATEGORY));
    }

}
