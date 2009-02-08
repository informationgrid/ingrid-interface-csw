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

		String objectId = IngridQueryHelper.getDetailValueAsString(hit, "t01_object.obj_id");

		String udkClass = IngridQueryHelper.getDetailValueAsString(hit, "t01_object.obj_class");
        String typeName = getTypeName();
        if (typeName == null) {
        	return null;
        }

		Element metaData = DocumentFactory.getInstance().createElement("MD_Metadata",
				"http://www.isotc211.org/2005/gmd");
		metaData.add(gmd);
		metaData.add(gco);
		metaData.add(srv);

		this.addFileIdentifier(metaData, objectId);
		this.addHierarchyLevel(metaData.addElement("hierarchyLevel"), typeName);
		this.addContacts(metaData, hit, this.nsPrefix);
		this.addDateStamp(metaData, hit, this.nsPrefix);
		if (udkClass.equals("1")) {
			this.addIdentificationInfoDataset(metaData, hit);
		} else {
			this.addIdentificationInfoService(metaData, hit);
		}

		return metaData;
	}

	private void addIdentificationInfoService(Element metaData, IngridHit hit) {
		Element svServiceIdentification = metaData.addElement("gmd:identificationInfo").addElement(
				"gmd:SV_ServiceIdentification");
		this.addGCOCharacterString(svServiceIdentification.addElement("gmd:citation").addElement("gmd:CI_Citation").addElement("gmd:title"), IngridQueryHelper
				.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_TITLE));

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
        	this.addGCOCharacterString(svServiceIdentification.addElement("serviceType"), serviceType);        	
        }

		String[] serviceTypeVersions = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_SERVICE_TYPE_VERSION);
		if (serviceTypeVersions != null) {
			for (int i = 0; i < serviceTypeVersions.length; i++) {
				this.addGCOCharacterString(svServiceIdentification.addElement("srv:serviceTypeVersion"),
						serviceTypeVersions[i]);
			}
		}
		svServiceIdentification.addElement("srv:couplingType").addElement("srv:CSW_CouplingType")
				.addAttribute("codeList", "http://opengis.org/codelistRegistry?CSW_CouplingType").addAttribute(
						"codeListValue",
						IngridQueryHelper.getDetailValueAsString(hit,
								IngridQueryHelper.HIT_KEY_OBJECT_OBJECT_SPECIAL_REF));
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
				String codeVal = UtilsUDKCodeLists.getCodeListEntryName(new Long(527), code, new Long(94));
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
            
            Element geographicElement = exExent.addElement("gmd:geographicElement");
            Element exGeographicBoundingBox = geographicElement.addElement("gmd:EX_GeographicBoundingBox");
            // T01_st_bbox.x1 MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/geographicElement/EX_GeographicBoundingBox.westBoundLongitude/gmd:approximateLongitude
            if (stBoxX1.length == stTownship.length) {
            	super.addGCODecimal(exGeographicBoundingBox.addElement("gmd:westBoundLongitude").addElement("gmd:approximateLongitude"), stBoxX1[i].replaceAll(",", "."));
            }
            // T01_st_bbox.x2 MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/geographicElement/EX_GeographicBoundingBox.eastBoundLongitude/gmd:approximateLongitude
            if (stBoxX2.length == stTownship.length) {
            	super.addGCODecimal(exGeographicBoundingBox.addElement("gmd:eastBoundLongitude").addElement("gmd:approximateLongitude"), stBoxX2[i].replaceAll(",", "."));
            }
            // T01_st_bbox.y1 MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/geographicElement/EX_GeographicBoundingBox.southBoundLatitude/gmd:approximateLatitude
            if (stBoxY1.length == stTownship.length) {
            	super.addGCODecimal(exGeographicBoundingBox.addElement("gmd:southBoundLatitude").addElement("gmd:approximateLatitude"), stBoxY1[i].replaceAll(",", "."));
            }
            // T01_st_bbox.y2 MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/geographicElement/EX_GeographicBoundingBox.northBoundLatitude/gmd:approximateLatitude
            if (stBoxY2.length == stTownship.length) {
            	super.addGCODecimal(exGeographicBoundingBox.addElement("gmd:northBoundLatitude").addElement("gmd:approximateLatitude"), stBoxY2[i].replaceAll(",", "."));
            }
        }
    }	

}
