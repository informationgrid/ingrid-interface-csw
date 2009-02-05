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
public class CSWBuilderMetadata_brief_CSW_2_0_2_AP_ISO_1_0 extends CSW_2_0_2_BuilderMetadataCommon {

	private static Log log = LogFactory.getLog(CSWBuilderMetadata_brief_CSW_2_0_2_AP_ISO_1_0.class);

	public Element build() throws Exception {

		this.setNSPrefix("gmd");

		// define used name spaces
		Namespace gco = new Namespace("gco", "http://www.isotc211.org/2005/gco");
		Namespace gmd = new Namespace("gmd", "http://www.isotc211.org/2005/gmd");
		Namespace srv = new Namespace("srv", "http://www.isotc211.org/2005/srv");

		String objectId = IngridQueryHelper.getDetailValueAsString(hit, "t01_object.obj_id");

		String udkClass = IngridQueryHelper.getDetailValueAsString(hit, "t01_object.obj_class");
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

		Element metaData = DocumentFactory.getInstance().createElement("MD_Metadata",
				"http://www.isotc211.org/2005/gmd");
		metaData.add(gmd);
		metaData.add(gco);
		metaData.add(srv);

		this.addFileIdentifier(metaData, objectId);
		this.addHierarchyLevel(metaData.addElement("hierarchyLevel"), typeName);
		// this.addContact(metaData, hit);
		if (typeName.equals("dataset")) {
			this.addIdentificationInfoDataset(metaData, hit);
		} else {
			this.addIdentificationInfoService(metaData, hit);
		}

		return metaData;
	}

	private void addIdentificationInfoService(Element metaData, IngridHit hit) {
		Element cswServiceIdentification = metaData.addElement("gmd:identificationInfo").addElement(
				"gmd:SV_ServiceIdentification");
		this.addSMXMLCharacterString(cswServiceIdentification.addElement("gmd:title"), IngridQueryHelper
				.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_TITLE));
		this.addSMXMLCharacterString(cswServiceIdentification.addElement("srv:serviceType"), IngridQueryHelper
				.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERVICE_TYPE));
		String[] serviceTypeVersions = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_SERVICE_TYPE_VERSION);
		if (serviceTypeVersions != null) {
			for (int i = 0; i < serviceTypeVersions.length; i++) {
				this.addSMXMLCharacterString(cswServiceIdentification.addElement("srv:serviceTypeVersion"),
						serviceTypeVersions[i]);
			}
		}
		cswServiceIdentification.addElement("srv:couplingType").addElement("srv:CSW_CouplingType")
				.addAttribute("codeList", "http://opengis.org/codelistRegistry?CSW_CouplingType").addAttribute(
						"codeListValue",
						IngridQueryHelper.getDetailValueAsString(hit,
								IngridQueryHelper.HIT_KEY_OBJECT_OBJECT_SPECIAL_REF));
	}

	private void addIdentificationInfoDataset(Element metaData, IngridHit hit) {
		Element mdDataIdentification = metaData.addElement("gmd:identificationInfo").addElement(
				"gmd:MD_DataIdentification");
		this.addSMXMLCharacterString(mdDataIdentification.addElement("gmd:title"), IngridQueryHelper
				.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_TITLE));
		// T011_obj_geo_topic_cat.topic_category
		mdDataIdentification.addElement("gmd:topicCategory").addElement("gmd:MD_TopicCategoryCode").addText(
				IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_GEO_TOPIC_CATEGORY));
	}

}
