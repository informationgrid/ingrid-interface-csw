/*
 * Copyright (c) 1997-2007 by wemove GmbH
 */
package de.ingrid.interfaces.csw.transform.response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Namespace;

import de.ingrid.interfaces.csw.utils.Udk2CswDateFieldParser;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.udk.UtilsUDKCodeLists;

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

		String udkClass = IngridQueryHelper.getDetailValueAsString(hit, "t01_object.obj_class");
        HierarchyInfo hierarchyInfo = getTypeName();
        if (hierarchyInfo.hierarchyLevel == null) {
        	return null;
        }

		Element metaData = DocumentFactory.getInstance().createElement("MD_Metadata",
				"http://www.isotc211.org/2005/gmd");
		metaData.add(gmd);
		metaData.add(gco);
		metaData.add(srv);

		this.addFileIdentifier(metaData, hit);
		this.addHierarchyLevel(metaData.addElement("hierarchyLevel"), hierarchyInfo.hierarchyLevel);
		if (IngridQueryHelper.hasValue(hierarchyInfo.hierarchyLevelName)) {
			this.addGCOCharacterString(metaData.addElement("gmd:hierarchyLevelName"), hierarchyInfo.hierarchyLevelName);
		}
		this.addContacts(metaData, hit, this.nsPrefix);
		this.addDateStamp(metaData, hit, this.nsPrefix);
		if (udkClass.equals("3")) {
			this.addIdentificationInfoService(metaData, hit);
		} else {
			this.addIdentificationInfoDataset(metaData, hit);
		}

		return metaData;
	}

	private void addIdentificationInfoService(Element metaData, IngridHit hit) {
		Element svServiceIdentification = metaData.addElement("gmd:identificationInfo").addElement(
				"srv:SV_ServiceIdentification");
		Element ciCitation = svServiceIdentification.addElement("gmd:citation").addElement("gmd:CI_Citation");

		this.addGCOCharacterString(ciCitation.addElement("gmd:title"), IngridQueryHelper
				.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_TITLE));

		// add dates (creation, revision etc.)
		super.addCitationReferenceDates(ciCitation, hit, "gmd");

		// add abstract
		this.addGCOCharacterString(svServiceIdentification.addElement("gmd:abstract"), IngridQueryHelper.getDetailValueAsString(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_DESCR));
		
        // add service type
        String serviceTypeKey = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERVICE_TYPE);
        String serviceType = null;
        if (serviceTypeKey != null) {
        	if (serviceTypeKey.equals("1")) {
        		serviceType = "discovery";
        	} else if (serviceTypeKey.equals("2")) {
        		serviceType = "view";
        	} else if (serviceTypeKey.equals("3")) {
        		serviceType = "download";
        	} else if (serviceTypeKey.equals("4")) {
        		serviceType = "transformation";
        	} else if (serviceTypeKey.equals("5")) {
        		serviceType = "invoke";
        	} else  {
        		serviceType = "other";
        	}
        }
        if (serviceType != null) {
        	this.addGCOLocalName(svServiceIdentification.addElement("srv:serviceType"), serviceType);        	
        }

		String[] serviceTypeVersions = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_SERVICE_TYPE_VERSION);
		if (serviceTypeVersions != null) {
			for (int i = 0; i < serviceTypeVersions.length; i++) {
				this.addGCOCharacterString(svServiceIdentification.addElement("srv:serviceTypeVersion"),
						serviceTypeVersions[i]);
			}
		}

		String objReferenceSpecialRef = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_OBJECT_SPECIAL_REF);
        if (objReferenceSpecialRef != null && objReferenceSpecialRef.equals("3345")) {
			svServiceIdentification.addElement("srv:couplingType").addElement("srv:SV_CouplingType")
			.addAttribute("codeList", "http://opengis.org/codelistRegistry?CSW_CouplingType").addAttribute(
					"codeListValue", "tight");
		} else {
			svServiceIdentification.addElement("srv:couplingType").addElement("srv:SV_CouplingType")
			.addAttribute("codeList", "http://opengis.org/codelistRegistry?CSW_CouplingType").addAttribute(
					"codeListValue", "loose");
		}
		
		addOperationMetadata(svServiceIdentification, hit);
	}

	private void addIdentificationInfoDataset(Element metaData, IngridHit hit) {
		Element mdDataIdentification = metaData.addElement("gmd:identificationInfo").addElement(
				"gmd:MD_DataIdentification");
		Element ciCitation = mdDataIdentification.addElement("gmd:citation").addElement("gmd:CI_Citation");
		// add title
		this.addGCOCharacterString(ciCitation.addElement("gmd:title"), IngridQueryHelper.getDetailValueAsString(
				hit, IngridQueryHelper.HIT_KEY_OBJECT_TITLE));
		// add dates (creation, revision etc.)
		super.addCitationReferenceDates(ciCitation, hit, "gmd");

		// add abstract
		this.addGCOCharacterString(mdDataIdentification.addElement("gmd:abstract"), IngridQueryHelper.getDetailValueAsString(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_DESCR));
		
		// add language
		String dataLang = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_DATA_LANGUAGE);
		if (IngridQueryHelper.hasValue(dataLang)) {
			this.addGCOCharacterString(mdDataIdentification.addElement("gmd:language"), getISO639_2LanguageCode(dataLang));
		}

		// T011_obj_geo_topic_cat.topic_category
		String[] topicCategories = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_GEO_TOPIC_CATEGORY);
		for (int i = 0; i < topicCategories.length; i++) {
			Long code;
			try {
				code = Long.valueOf(topicCategories[i]);
				String codeVal = UtilsUDKCodeLists.getIsoCodeListEntryFromIgcId(527L, code);
				if (codeVal.length() > 0) {
					mdDataIdentification.addElement("gmd:topicCategory").addElement("gmd:MD_TopicCategoryCode")
							.addText(codeVal);
				}
			} catch (NumberFormatException e) {
				log.debug("Could not parse topic category id: " + topicCategories[i]);
			}
		}
		
		addExtent(mdDataIdentification, hit, "gmd");
	}
	
    protected void addExtent (Element parent, IngridHit hit, String ns) {
        // extend
        Element exExent = null;
        
        String[] stTownship = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_ST_BBOX_LOC_TOWN_NO);
        String[] stTownshipNames = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_ST_TOWNSHIP_TOWNSHIP);
        String[] stBoxX1 = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_ST_BOX_X1);
        String[] stBoxX2 = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_ST_BOX_X2);
        String[] stBoxY1 = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_ST_BOX_Y1);
        String[] stBoxY2 = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_ST_BOX_Y2);

        for (int i=0; i< stTownship.length; i++) {
            if (exExent == null) {
            	exExent = parent.addElement(this.getNSElementName(ns, "extent")).addElement("gmd:EX_Extent");
            }
            // T011_township.township_no MD_Metadata/gmd:identificationInfo/srv:CSW_ServiceIdentification/srv:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicDescription/gmd:geographicIdentifier/gmd:RS_Identifier/code/gco:CharacterString
            String geoIdentifier = null;
            if (stTownshipNames.length == stTownship.length && stTownshipNames[i] != null &&  stTownshipNames[i].length() > 0) {
            	geoIdentifier = stTownshipNames[i];
            }
            if (stTownship[i]!= null && stTownship[i].length() > 0) {
            	if (geoIdentifier != null) {
                	geoIdentifier += " "; 
            	}
            	geoIdentifier += "(" + stTownship[i] + ")"; 
            }
            if (geoIdentifier != null) {
            	super.addGCOCharacterString(exExent.addElement("gmd:geographicElement").addElement("gmd:EX_GeographicDescription").addElement("gmd:geographicIdentifier").addElement("gmd:MD_Identifier").addElement("gmd:code"), stTownship[i]);
            }
            
            Element exGeographicBoundingBox = null;
            if (stBoxX1.length == stTownship.length 
            		&& stBoxX1.length == stTownship.length
            		&& stBoxY1.length == stTownship.length
            		&& stBoxY2.length == stTownship.length
            		&& IngridQueryHelper.hasValue(stBoxX1[i])
            		&& IngridQueryHelper.hasValue(stBoxX2[i])
            		&& IngridQueryHelper.hasValue(stBoxY1[i])
            		&& IngridQueryHelper.hasValue(stBoxY2[i])
            		) {
            	if (exGeographicBoundingBox == null) {
            		exGeographicBoundingBox = exExent.addElement("gmd:geographicElement").addElement("gmd:EX_GeographicBoundingBox");
            	}
            	super.addGCODecimal(exGeographicBoundingBox.addElement("gmd:westBoundLongitude"), stBoxX1[i].replaceAll(",", "."));
            	super.addGCODecimal(exGeographicBoundingBox.addElement("gmd:eastBoundLongitude"), stBoxX2[i].replaceAll(",", "."));
            	super.addGCODecimal(exGeographicBoundingBox.addElement("gmd:southBoundLatitude"), stBoxY1[i].replaceAll(",", "."));
            	super.addGCODecimal(exGeographicBoundingBox.addElement("gmd:northBoundLatitude"), stBoxY2[i].replaceAll(",", "."));

            }
        }
    }	

}
