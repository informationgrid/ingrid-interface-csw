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
public class CSWBuilderMetadata_summary_CSW_2_0_2_AP_ISO_1_0 extends CSW_2_0_2_BuilderMetadataCommon {

    private static Log log = LogFactory.getLog(CSWBuilderMetadata_summary_CSW_2_0_2_AP_ISO_1_0.class);

    public Element build() throws Exception {
        
        this.setNSPrefix("gmd");

        // define used name spaces
		Namespace gco = new Namespace("gco", "http://www.isotc211.org/2005/gco");
		Namespace gmd = new Namespace("gmd", "http://www.isotc211.org/2005/gmd");
		Namespace srv = new Namespace("srv", "http://www.isotc211.org/2005/srv");

        String udkClass = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_OBJ_CLASS);
        HierarchyInfo hierarchyInfo = getTypeName();
        if (hierarchyInfo.hierarchyLevel == null) {
        	return null;
        }

        Element metaData = DocumentFactory.getInstance().createElement("MD_Metadata",
        "http://www.isotc211.org/2005/gmd");
        metaData.add(gco);
        metaData.add(gmd);
        metaData.add(srv);

		metaData.addAttribute("id", hit.getPlugId().replaceAll("[^_\\.\\-A-Za-z0-9]", "_") + "_" + hit.getDocumentId());
        
        this.addFileIdentifier(metaData, hit);
        this.addLanguage(metaData, hit);
		this.addCharacterSet(metaData, hit);
		this.addParentIdentifier(metaData, hit);
		this.addHierarchyLevel(metaData.addElement("hierarchyLevel"), hierarchyInfo.hierarchyLevel);
		if (IngridQueryHelper.hasValue(hierarchyInfo.hierarchyLevelName)) {
			this.addGCOCharacterString(metaData.addElement("gmd:hierarchyLevelName"), hierarchyInfo.hierarchyLevelName);
		}
        this.addContacts(metaData, hit);
        this.addDateStamp(metaData, hit);
		String metaDataStandardName = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_METADATA_STANDARD_NAME);
		if (IngridQueryHelper.hasValue(metaDataStandardName)) {
			this.addGCOCharacterString(metaData.addElement("gmd:metadataStandardName"), metaDataStandardName);
		} else {
			this.addGCOCharacterString(metaData.addElement("gmd:metadataStandardName"), "ISO19115");
		}
		String metaDataStandardVersion = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_METADATA_STANDARD_VERSION);
		if (IngridQueryHelper.hasValue(metaDataStandardVersion)) {
			this.addGCOCharacterString(metaData.addElement("gmd:metadataStandardVersion"), metaDataStandardVersion);
		} else {
			this.addGCOCharacterString(metaData.addElement("gmd:metadataStandardVersion"), "2003/Cor.1:2006");
		}
        if (udkClass.equals("3")) {
            this.addIdentificationInfoService(metaData, hit);
        } else {
            this.addIdentificationInfoDataset(metaData, hit);
        }
        
		// contentInfo
		this.addContentInfo(metaData, hit);

		// distributionInfo
		this.addDistributionInfo(metaData, hit);

		// dataQualityInfo
		if (udkClass.equals("1")) {
			this.addDataQualityInfoDataSet(metaData, hit);
		} else if (udkClass.equals("3")) {
			this.addDataQualityInfoService(metaData, hit);
		}
        

        
        return metaData;
    }

    private void addCitation(Element parent, IngridHit hit) {
        Element ciCitation = parent.addElement("citation")
        .addElement("gmd:CI_Citation");
        // add title
        this.addGCOCharacterString(ciCitation.addElement("title"), IngridQueryHelper.getDetailValueAsString(hit,
                IngridQueryHelper.HIT_KEY_OBJECT_TITLE));
        // add dates (creation, revision etc.)
        super.addCitationReferenceDates(ciCitation, hit, null);
		
        // add identifier
		String identifier = getCitationIdentifier(hit);
		if (IngridQueryHelper.hasValue(identifier)) {
			Element rsIdentifier = ciCitation.addElement("gmd:identifier").addElement("gmd:RS_Identifier");
			this.addGCOCharacterString(rsIdentifier.addElement("gmd:code"), identifier);
			this.addGCOCharacterString(rsIdentifier.addElement("gmd:codeSpace"), "ingrid");
		}
    }
    
    private void addIdentificationInfoService(Element metaData, IngridHit hit) throws Exception {
        Element svServiceIdentification = metaData.addElement("identificationInfo").addElement(
                "srv:SV_ServiceIdentification");

        svServiceIdentification.addAttribute("uuid", "ingrid#" + getCitationIdentifier(hit));
        
        // add citation construct
        this.addCitation(svServiceIdentification, hit);

        
        // add abstract
        this.addGCOCharacterString(svServiceIdentification.addElement("abstract"), IngridQueryHelper.getDetailValueAsString(hit,
                IngridQueryHelper.HIT_KEY_OBJECT_DESCR));

		addPointOfContacts(svServiceIdentification, hit, "gmd");

        // add resourceConstraint
        String resourceConstraint = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_AVAIL_ACCESS_NOTE);
		if (IngridQueryHelper.hasValue(resourceConstraint)) {
			this.addGCOCharacterString(svServiceIdentification.addElement("resourceConstraints").addElement("gmd:MD_Constraints").addElement("gmd:useLimitation"), resourceConstraint);
		}
        
        
        // add service type
        String serviceTypeKey = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERVICE_TYPE_KEY);
        String serviceType = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERVICE_TYPE);;
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
        

        String[] serviceTypeVersions = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERVICE_TYPE_VERSION);
        if (serviceTypeVersions != null) {
            for (int i=0; i< serviceTypeVersions.length; i++) {
                this.addGCOCharacterString(svServiceIdentification.addElement("srv:serviceTypeVersion"), serviceTypeVersions[i]);
            }
        }
        
		String objReferenceSpecialRef = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_OBJECT_SPECIAL_REF);
        if (objReferenceSpecialRef != null && objReferenceSpecialRef.equals("3345")) {
			svServiceIdentification.addElement("srv:couplingType").addElement("srv:SV_CouplingType")
			.addAttribute("codeList", "http://opengis.org/codelistRegistry?SV_CouplingType").addAttribute(
					"codeListValue", "tight");
		} else {
			svServiceIdentification.addElement("srv:couplingType").addElement("srv:SV_CouplingType")
			.addAttribute("codeList", "http://opengis.org/codelistRegistry?SV_CouplingType").addAttribute(
					"codeListValue", "loose");
		}

        addOperationMetadata(svServiceIdentification, hit);
    }

    private void addIdentificationInfoDataset(Element metaData, IngridHit hit) throws Exception {
        Element mdDataIdentification = metaData.addElement("identificationInfo").addElement(
                "gmd:MD_DataIdentification");
        
        mdDataIdentification.addAttribute("uuid", "ingrid#" + getCitationIdentifier(hit));

        // add citation construct
        this.addCitation(mdDataIdentification, hit);

        // add abstract
        this.addGCOCharacterString(mdDataIdentification.addElement("abstract"), IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_DESCR));

		addPointOfContacts(mdDataIdentification, hit, "gmd");

		// add resourceConstraint
		String resourceConstraints = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_AVAIL_ACCESS_NOTE);
        if (IngridQueryHelper.hasValue(resourceConstraints)) {
        	this.addGCOCharacterString(mdDataIdentification.addElement("resourceConstraints").addElement("gmd:MD_Constraints").addElement("gmd:useLimitation"), resourceConstraints);
        }
		
		// add digital representation
		// T011_obj_geo_spatial_rep.type ->
		// MD_Metadata/udk:identificationInfo/spatialRepresentationType
		String[] digitalRepresentations = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_SPATIAL_REP_TYPE);
		for (int i = 0; i < digitalRepresentations.length; i++) {
			Long code;
			try {
				code = Long.valueOf(digitalRepresentations[i]);
				String codeVal = UtilsUDKCodeLists.getIsoCodeListEntryFromIgcId(526L, code);
				if (codeVal.length() > 0) {
					mdDataIdentification.addElement("gmd:spatialRepresentationType").addElement(
							"gmd:MD_SpatialRepresentationTypeCode").addAttribute("codeList",
							"http://www.tc211.org/ISO19115/resources/codeList.xml#MD_SpatialRepresentationTypeCode")
							.addAttribute("codeListValue", codeVal);
				}
			} catch (NumberFormatException e) {
			}
		}

		// add spacial resolution
		// T011_obj_geo_scale.scale ->
		// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:spatialResolution/gmd:MD_Resolution/gmd:equivalentScale/gmd:MD_RepresentativeFraction/gmd:denominator/gco:Integer
		// T011_obj_geo_scale.resolution_ground ->
		// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:spatialResolution/gmd:MD_Resolution/gmd:distance/gco:Distance
		// T011_obj_geo_scale.resolution_scan ->
		// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:spatialResolution/gmd:MD_Resolution/gmd:distance/gco:Distance
		String[] spacialResolutionScale = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_SPATIAL_RES_SCALE);
		String[] spacialResolutionGround = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_SPATIAL_RES_GROUND);
		String[] spacialResolutionScan = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_SPATIAL_RES_SCAN);
		for (int i = 0; i < spacialResolutionScale.length; i++) {
			if (IngridQueryHelper.hasValue(spacialResolutionScale[i])) {
				this.addGCOInteger(mdDataIdentification.addElement("gmd:spatialResolution").addElement(
				"gmd:MD_Resolution").addElement("gmd:equivalentScale").addElement(
						"gmd:MD_RepresentativeFraction").addElement("gmd:denominator"), spacialResolutionScale[i]);
			}
		}
		for (int i = 0; i < spacialResolutionGround.length; i++) {
			if (IngridQueryHelper.hasValue(spacialResolutionGround[i])) {
				mdDataIdentification.addElement("gmd:spatialResolution").addElement(
				"gmd:MD_Resolution").addElement("gmd:distance").addElement("gco:Distance").addAttribute("uom", "meter").addText(spacialResolutionGround[i]);
			}
		}
		for (int i = 0; i < spacialResolutionGround.length; i++) {
			if (IngridQueryHelper.hasValue(spacialResolutionScan[i])) {
				mdDataIdentification.addElement("gmd:spatialResolution").addElement(
				"gmd:MD_Resolution").addElement("gmd:distance").addElement("gco:Distance").addAttribute("uom", "dpi").addText(spacialResolutionScan[i]);;
			}
		}

		// add language
		String dataLang = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_DATA_LANGUAGE);
		if (IngridQueryHelper.hasValue(dataLang)) {
			this.addGCOCharacterString(mdDataIdentification.addElement("gmd:language"), getISO639_2LanguageCode(dataLang));
		}
		

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
    
	private void addDistributionInfo(Element metaData, IngridHit hit) {

		Element mdDistribution = null; 

		String[] formatNames = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_AVAIL_FORMAT_NAME);
		String[] formatVersions = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_AVAIL_FORMAT_VERSION);
		for (int i = 0; i < formatNames.length; i++) {
			if (mdDistribution == null) {
				mdDistribution = metaData.addElement("gmd:distributionInfo").addElement("gmd:MD_Distribution");
			}
			Element mdFormat = mdDistribution.addElement("gmd:distributionFormat").addElement("gmd:MD_Format");
			// T0110_avail_format.name
			// MD_Metadata/distributionInfo/MD_Distribution/distributor/MD_Distributor/distributorFormat/MD_Format/name
			this.addGCOCharacterString(mdFormat.addElement("gmd:name"), formatNames[i]);
			// T0110_avail_format.version
			// MD_Metadata/distributionInfo/MD_Distribution/distributor/MD_Distributor/distributorFormat/MD_Format/version
			this.addGCOCharacterString(mdFormat.addElement("gmd:version"), formatVersions[i]);
		}
		
		// MD_Metadata/distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/onLine/CI_OnlineResource/
		String[] urlRefUrlLinks = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_URL_REF_URL_LINK);
		String[] urlRefContent = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_URL_REF_CONTENT);
		String[] urlRefDescr = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_URL_REF_DESCR);
		for (int i = 0; i < urlRefUrlLinks.length; i++) {
			if (IngridQueryHelper.hasValue(urlRefUrlLinks[i])) {
				if (mdDistribution == null) {
					mdDistribution = metaData.addElement("gmd:distributionInfo").addElement("gmd:MD_Distribution");
				}
				Element ciOnlineResource = mdDistribution.addElement("gmd:transferOptions").addElement(
				"gmd:MD_DigitalTransferOptions").addElement("gmd:onLine").addElement(
						"gmd:CI_OnlineResource");
				// T017_url_ref.url_link
				// MD_Metadata/distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/onLine/CI_OnlineResource/linkage/gmd:URL
				ciOnlineResource.addElement("gmd:linkage").addElement("gmd:URL").addText(urlRefUrlLinks[i]);
				// T017_url_ref.content
				// MD_Metadata/full:distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/onLine/CI_OnlineResource/name
				if (urlRefContent.length > i && IngridQueryHelper.hasValue(urlRefContent[i])) {
					this.addGCOCharacterString(ciOnlineResource.addElement("gmd:name"), urlRefContent[i]);
				}
				// T017_url_ref.descr
				// MD_Metadata/full:distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/onLine/CI_OnlineResource/description
				if (urlRefDescr.length > i && IngridQueryHelper.hasValue(urlRefDescr[i])) {
					this.addGCOCharacterString(ciOnlineResource.addElement("gmd:description"), urlRefDescr[i]);
				}
			}
		}
		

	}

	private void addDataQualityInfoService(Element metaData, IngridHit hit) {
	}

	private void addDataQualityInfoDataSet(Element metaData, IngridHit hit) {
		Element dqQualityInfo = null;
		// T011_obj_geo.special_base ->
		// MD_Metadata/dataQualityInfo/udk:DQ_DataQuality/lineage/udk:LI_Lineage/statement
		String statement = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_GEO_SPECIAL_BASE);
		if (IngridQueryHelper.hasValue(statement)) {
			if (dqQualityInfo == null) {
				dqQualityInfo = metaData.addElement("gmd:dataQualityInfo").addElement("gmd:DQ_DataQuality");
				// add scope
				dqQualityInfo.addElement("scope").addElement("DQ_Scope").addElement("level").addElement("MD_ScopeCode").addAttribute("codeListValue", getTypeName().hierarchyLevel).addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#MD_ScopeCode");
			}
			Element liLineage = dqQualityInfo.addElement("gmd:lineage").addElement("gmd:LI_Lineage");
			this.addGCOCharacterString(liLineage.addElement("gmd:statement"), IngridQueryHelper.getDetailValueAsString(
					hit, IngridQueryHelper.HIT_KEY_OBJECT_GEO_SPECIAL_BASE));
		}
	}	

	private void addContentInfo(Element metaData, IngridHit hit) {

		Element mdFeatureCatalogueDescription = null;
		// T011_obj_geo_supplinfo.feature_type
		// MD_Metadata/contentInfo/MD_FeatureCatalogueDescription/featureTypes/gco:LocalName
		String[] udkFeatureTypes = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_SUPPLINFO_FEATURE_TYPE);
		if (udkFeatureTypes.length > 0) {
			if (mdFeatureCatalogueDescription == null) {
				mdFeatureCatalogueDescription = metaData.addElement("gmd:contentInfo").addElement("gmd:MD_FeatureCatalogueDescription");
			}
			Element featureTypes = mdFeatureCatalogueDescription.addElement("gmd:featureTypes");
			for (int i = 0; i < udkFeatureTypes.length; i++) {
				featureTypes.addElement("gco:LocalName").addText(udkFeatureTypes[i]);
			}
		}
	}	

    protected void addOperationMetadata(Element parent, IngridHit hit) {
        // operationMetadata
        Element svOperationMetadata = null;

        // srv:operationMetadata -> srv:SV_OperationMetadata -> srv:operationName -> String
        String operationName = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_OPERATION_NAME);
        if (IngridQueryHelper.hasValue(operationName)) {
	        if (svOperationMetadata == null) {
	        	svOperationMetadata = parent.addElement("srv:operationMetadata").addElement("srv:SV_OperationMetadata");
	        }
        	this.addGCOCharacterString(svOperationMetadata.addElement("srv:operationName"), operationName);
        }

        // srv:operationMetadata -> srv:SV_OperationMetadata ->(1:n) srv:DCP -> srv:SV_DCPList/@codeListValue
        String[] platforms = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_OP_PLATFORM);
        for (int i=0; i< platforms.length; i++) {
	        if (svOperationMetadata == null) {
	        	svOperationMetadata = parent.addElement("srv:operationMetadata").addElement("srv:SV_OperationMetadata");
	        }
            svOperationMetadata.addElement("srv:DCP").addElement("srv:SV_DCPList")
                .addAttribute("codeList", "http://opengis.org/codelistRegistry?CSW_DCPCodeType")
                .addAttribute("codeList", platforms[i]);
        }
        // srv:operationMetadata -> srv:SV_OperationMetadata ->(1:n) srv:connectPoint -> gmd:CI_OnlineResource
        String[] connectPoints = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_OP_CONNECT_POINT);
        for (int i=0; i< connectPoints.length; i++) {
	        if (svOperationMetadata == null) {
	        	svOperationMetadata = parent.addElement("srv:operationMetadata").addElement("srv:SV_OperationMetadata");
	        }
            svOperationMetadata.addElement("srv:connectPoint")
                .addElement("gmd:CI_OnlineResource").addElement("gmd:linkage")
                    .addElement("gmd:URL").addText(connectPoints[i]);
        }
    }	
	
}
