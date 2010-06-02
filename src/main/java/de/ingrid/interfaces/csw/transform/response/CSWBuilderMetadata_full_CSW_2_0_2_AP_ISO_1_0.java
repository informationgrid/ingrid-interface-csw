/*
 * Copyright (c) 1997-2007 by wemove GmbH
 */
package de.ingrid.interfaces.csw.transform.response;

import java.util.List;
import java.util.UUID;

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
public class CSWBuilderMetadata_full_CSW_2_0_2_AP_ISO_1_0 extends CSW_2_0_2_BuilderMetadataCommon {

	private static Log log = LogFactory.getLog(CSWBuilderMetadata_full_CSW_2_0_2_AP_ISO_1_0.class);
	
	private String udkClass = null;

	public Element build() throws Exception {

		this.setNSPrefix("gmd");

		// define used name spaces
		Namespace gco = new Namespace("gco", "http://www.isotc211.org/2005/gco");
		Namespace gmd = new Namespace("gmd", "http://www.isotc211.org/2005/gmd");
		Namespace srv = new Namespace("srv", "http://www.isotc211.org/2005/srv");
		Namespace gml = new Namespace("gml", "http://www.opengis.net/gml");
		Namespace gts = new Namespace("gts", "http://www.isotc211.org/2005/gts");

		udkClass = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_OBJ_CLASS);
        HierarchyInfo hierarchyInfo = getTypeName();
        if (hierarchyInfo.hierarchyLevel == null) {
        	return null;
        }

		Element metaData = DocumentFactory.getInstance().createElement("gmd:MD_Metadata",
				"http://www.isotc211.org/2005/gmd");
		metaData.add(gco);
		metaData.add(gmd);
		metaData.add(srv);
		metaData.add(gml);
		metaData.add(gts);
		
		metaData.addAttribute("id", hit.getPlugId().replaceAll("[^_\\.\\-A-Za-z0-9]", "_") + "#" + hit.getDocumentId());

		this.addFileIdentifier(metaData, hit, this.getNSPrefix());
		this.addLanguage(metaData, hit, this.getNSPrefix());
		this.addCharacterSet(metaData, hit);
		this.addParentIdentifier(metaData, hit);
		this.addHierarchyLevel(metaData.addElement("gmd:hierarchyLevel"), hierarchyInfo.hierarchyLevel);
		if (IngridQueryHelper.hasValue(hierarchyInfo.hierarchyLevelName)) {
			this.addGCOCharacterString(metaData.addElement("gmd:hierarchyLevelName"), hierarchyInfo.hierarchyLevelName);
		}
		this.addContacts(metaData, hit, this.nsPrefix);
		this.addDateStamp(metaData, hit, this.nsPrefix);
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
		
		// spatialRepresentationInfo
		this.addSpatialRepresentationInfo(metaData, hit);

		// referenceSystemInfo
		this.addReferenceSystemInfo(metaData, hit);

		if (udkClass.equals("3")) {
			this.addIdentificationInfoService(metaData, hit);
		} else {
			this.addIdentificationInfoDataset(metaData, hit);
		}
		
		
		// contentInfo
		if (udkClass.equals("1")) {
			addContentInfoDataset(metaData, hit);
		} else if (udkClass.equals("5")) {
			addContentInfoDatabase(metaData, hit);
		}

		// distributionInfo
		this.addDistributionInfo(metaData, hit);

		// dataQualityInfo
		if (udkClass.equals("1")) {
			addDataQualityInfoDataSet(metaData, hit);
			addPortrayalCatalogueInfo(metaData, hit);
		} else if (udkClass.equals("3")) {
			addDataQualityInfoService(metaData, hit);
		} else if (udkClass.equals("5")) {
			addDataQualityInfoDatabase(metaData, hit);
		} else if (udkClass.equals("2")) {
			addDataQualityInfoLiterature(metaData, hit);
		}

		return metaData;
	}

	private void addPortrayalCatalogueInfo(Element metaData, IngridHit hit) {
		String[] portrayalCatalogInfoTitles = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_SYMBOL_TITLE);
		String[] portrayalCatalogInfoDates = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_SYMBOL_DATE);
		String[] portrayalCatalogInfoVersions = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_SYMBOL_EDITION);
		for (int i = 0; i < portrayalCatalogInfoTitles.length; i++) {
			Element portrayalCICitation = metaData.addElement("gmd:portrayalCatalogueInfo").addElement(
					"gmd:MD_PortrayalCatalogueReference").addElement("gmd:portrayalCatalogueCitation").addElement(
					"gmd:CI_Citation");
			this.addGCOCharacterString(portrayalCICitation.addElement("gmd:title"), portrayalCatalogInfoTitles[i]);
			String myDate = portrayalCatalogInfoDates[i];
			Element ciDate = portrayalCICitation.addElement("gmd:date").addElement("gmd:CI_Date");
			if (myDate != null && myDate.length() > 0) {
				ciDate.addElement("gmd:date").addElement("gco:Date").addText(Udk2CswDateFieldParser.instance().parseToDate(myDate));
				ciDate.addElement("gmd:dateType").addElement("gmd:CI_DateTypeCode").addAttribute("codeList",
						"http://www.tc211.org/ISO19139/resources/codeList.xml#CI_DateTypeCode").addAttribute(
						"codeListValue", "creation");
			} else {
				ciDate.addElement("gmd:date").addElement("gco:Date").addAttribute("nilReason", "missing");
				ciDate.addElement("gmd:dateType").addElement("gmd:CI_DateTypeCode").addAttribute("codeList",
						"http://www.tc211.org/ISO19139/resources/codeList.xml#CI_DateTypeCode").addAttribute(
						"codeListValue", "creation");
			}
			this.addGCOCharacterString(portrayalCICitation.addElement("gmd:edition"),
					portrayalCatalogInfoVersions[i]);
		}
		// add portrayal catalog info references
		List<String> portrayalCatalogInfoReferenceIdentifiers = IngridQueryHelper.getPortrayalCatalogInfoReferenceIdentifiers(hit);
		for (int i=0; i < portrayalCatalogInfoReferenceIdentifiers.size(); i++) {
			metaData.addElement("gmd:portrayalCatalogueInfo").addAttribute("uuidref", portrayalCatalogInfoReferenceIdentifiers.get(i));
		}
		
	}

	private void addDistributionInfo(Element metaData, IngridHit hit) throws Exception {

		Element mdDistribution = null;

		String[] formatNames = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_AVAIL_FORMAT_NAME);
		String[] formatVersions = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_AVAIL_FORMAT_VERSION);
		String[] formatFileDecompressionTechniques = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_AVAIL_FORMAT_FILE_DECOMPRESSION_TECHNIQUE);
		String[] formatSpecifications = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_AVAIL_FORMAT_SPECIFIKATION);
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
			if (formatVersions.length > i && IngridQueryHelper.hasValue(formatVersions[i])) {
				this.addGCOCharacterString(mdFormat.addElement("gmd:version"), formatVersions[i]);
			}
			// T0110_avail_format.specification
			// MD_Metadata/distributionInfo/MD_Distribution/distributor/MD_Distributor/distributorFormat/MD_Format/specification
			if (formatSpecifications.length > i && IngridQueryHelper.hasValue(formatSpecifications[i])) {
				this.addGCOCharacterString(mdFormat.addElement("gmd:specification"), formatSpecifications[i]);
			}
			// T0110_avail_format.file_decompression_technique
			// MD_Metadata/distributionInfo/MD_Distribution/distributor/MD_Distributor/distributorFormat/MD_Format/fileDecompressionTechnique
			if (formatFileDecompressionTechniques.length > i && IngridQueryHelper.hasValue(formatFileDecompressionTechniques[i])) {
				this.addGCOCharacterString(mdFormat.addElement("gmd:fileDecompressionTechnique"),
						formatFileDecompressionTechniques[i]);
			}
		}

		Element mdDistributor = null;
        Element mdStandardOrderProcess = null;
        Element ciResponsibleParteDistributorContact = null;
        

		String fees = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_FEES);
        if (IngridQueryHelper.hasValue(fees)) {
			if (mdDistribution == null) {
				mdDistribution = metaData.addElement("gmd:distributionInfo").addElement("gmd:MD_Distribution");
			}
			if (mdDistributor == null) {
				mdDistributor = mdDistribution.addElement("gmd:distributor").addElement("gmd:MD_Distributor");
				// MD_Distributor need a distributorContact
				ciResponsibleParteDistributorContact = mdDistributor.addElement("gmd:distributorContact").addElement("gmd:CI_ResponsibleParty");
			}
        	if (mdStandardOrderProcess == null) {
				mdStandardOrderProcess = mdDistributor.addElement("gmd:distributionOrderProcess").addElement(
				"gmd:MD_StandardOrderProcess");				
			}
        	// T01_object.fees
			// MD_Metadata/distributionInfo/MD_Distribution/distributor/MD_Distributor/distributionOrderProcess/MD_StandardOrderProcess/fees
			this.addGCOCharacterString(mdStandardOrderProcess.addElement("gmd:fees"), IngridQueryHelper
					.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_FEES));
        }
		// T01_object.ordering_instructions
		// MD_Metadata/distributionInfo/MD_Distribution/distributor/MD_Distributor/distributionOrderProcess/MD_StandardOrderProcess/orderingInstructions
		String orderInstructions = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_ORDER_INSTRUCTIONS);
		if (IngridQueryHelper.hasValue(orderInstructions)) {
			if (mdDistribution == null) {
				mdDistribution = metaData.addElement("gmd:distributionInfo").addElement("gmd:MD_Distribution");
			}
			if (mdDistributor == null) {
				mdDistributor = mdDistribution.addElement("gmd:distributor").addElement("gmd:MD_Distributor");
				// MD_Distributor need a distributorContact
				ciResponsibleParteDistributorContact = mdDistributor.addElement("gmd:distributorContact").addElement("gmd:CI_ResponsibleParty");
			}
			if (mdStandardOrderProcess == null) {
				mdStandardOrderProcess = mdDistributor.addElement("gmd:distributionOrderProcess").addElement(
				"gmd:MD_StandardOrderProcess");				
			}
			this.addGCOCharacterString(mdStandardOrderProcess.addElement("gmd:orderingInstructions"), orderInstructions);
		}
		
		if (ciResponsibleParteDistributorContact != null) {
	        String[] addressIds = IngridQueryHelper.getDetailValueAsArray(hit, "t012_obj_adr.adr_id");
	        String[] addressTypes = IngridQueryHelper.getDetailValueAsArray(hit, "t012_obj_adr.typ");
	        boolean foundContact = false;
	        for (int i = 0; i < addressTypes.length; i++) {
	        	// leader
	        	if (addressTypes[i].equals("5") && addressIds.length >= i) {
	        		this.addResponsibleParty(ciResponsibleParteDistributorContact, hit, addressIds[i], "distributor");
	        		foundContact = true;
	        		break;
	        	}
	        }
	        if (!foundContact) {
		        // add dummy distributor role, because no distributor was found
	        	ciResponsibleParteDistributorContact.addElement("gmd:role").addElement("gmd:CI_RoleCode")
		        .addAttribute("codeList", "http://www.tc211.org/ISO19139/resources/codeList.xml#CI_RoleCode")
		        .addAttribute("codeListValue", "distributor");
	        }
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

		// MD_Metadata/distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/offLine/
		String[] mediaMediaNames = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_MEDIA_OPTION_MEDIUM_NAME);
		String[] mediaTransferSizes = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_MEDIA_OPTION_TANSFER_SIZE);
		String[] mediaMediumNotes = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_MEDIA_OPTION_MEDIUM_NOTE);
		for (int i = 0; i < mediaMediaNames.length; i++) {
			try {
				if (mdDistribution == null) {
					mdDistribution = metaData.addElement("gmd:distributionInfo").addElement("gmd:MD_Distribution");
				}
				Element mdDigitalTransferOptions = mdDistribution.addElement("gmd:transferOptions").addElement(
						"gmd:MD_DigitalTransferOptions");
				// T0112_media_option.transfer_size
				// MD_Metadata/distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/transferSize
				if (IngridQueryHelper.hasValue(mediaTransferSizes[i])) {
					this.addGCOReal(mdDigitalTransferOptions.addElement("gmd:transferSize"),
							mediaTransferSizes[i]);
				}

				Element mdMedium = null;

				// T0112_media_option.medium_name [Domain-ID Codeliste 520]
				// MD_Metadata/distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/offLine/MD_Medium/name/MD_MediumNameCode@codeListValue
				Long code = Long.valueOf(mediaMediaNames[i]);
				String codeVal = UtilsUDKCodeLists.getIsoCodeListEntryFromIgcId(520L, code);
				if (codeVal.length() > 0) {
					if (mdMedium == null) {
						mdMedium = mdDigitalTransferOptions.addElement("gmd:offLine").addElement("gmd:MD_Medium");
					}
					mdMedium.addElement("gmd:name").addElement("gmd:MD_MediumNameCode").addAttribute("codeList",
							"http://www.tc211.org/ISO19139/resources/codeList.xml#MD_MediumNameCode").addAttribute(
							"codeListValue", codeVal);
				}
				// T0112_media_option.medium_note
				// MD_Metadata/distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/offLine/MD_Medium/mediumNote
				if (IngridQueryHelper.hasValue(mediaMediumNotes[i])) {
					if (mdMedium == null) {
						mdMedium = mdDigitalTransferOptions.addElement("gmd:offLine").addElement("gmd:MD_Medium");
					}
					this.addGCOCharacterString(mdMedium.addElement("gmd:mediumNote"), mediaMediumNotes[i]);
				}
			} catch (NumberFormatException e) {
			}
		}
	}

	private void addContentInfoDataset(Element metaData, IngridHit hit) {

		Element mdFeatureCatalogueDescription = null;
		
		String[] keycTitles = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_KEYC_SUBJECT_CAT);
		String[] keycDates = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_KEYC_KEY_DATE);
		String[] keycEditions = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_KEYC_EDITION);
		if (keycTitles.length > 0 && keycDates.length > 0) {
			if (mdFeatureCatalogueDescription == null) {
				mdFeatureCatalogueDescription = metaData.addElement("gmd:contentInfo").addElement("gmd:MD_FeatureCatalogueDescription");
			}
			// T011_obj_geo.keyc_incl_w_dataset Wert = 0
			// MD_Metadata/full:contentInfo/MD_FeatureCatalogueDescription/includedWithDataset
			String keycInclWDataset = IngridQueryHelper.getDetailValueAsString(hit,
					IngridQueryHelper.HIT_KEY_OBJECT_KEYC_INCL_W_DATASET);
			this.addGCOBoolean(mdFeatureCatalogueDescription.addElement("gmd:includedWithDataset"),
					(keycInclWDataset != null && keycInclWDataset.equals("1")));

			// T011_obj_geo_supplinfo.feature_type
			// MD_Metadata/contentInfo/MD_FeatureCatalogueDescription[featureCatalogueCitation/CI_Citation/title]/featureTypes/LocalName
			String[] udkFeatureTypes = IngridQueryHelper.getDetailValueAsArray(hit,
					IngridQueryHelper.HIT_KEY_OBJECT_SUPPLINFO_FEATURE_TYPE);
			if (udkFeatureTypes.length > 0) {
				for (int i = 0; i < udkFeatureTypes.length; i++) {
					mdFeatureCatalogueDescription.addElement("gmd:featureTypes").addElement("gco:LocalName").addText(udkFeatureTypes[i]);
				}
			}
			
			for (int i = 0; i < keycTitles.length; i++) {
				Element ciCitation = mdFeatureCatalogueDescription.addElement("gmd:featureCatalogueCitation").addElement(
						"gmd:CI_Citation");
				// T011_obj_geo_keyc.subject_cat
				// MD_Metadata/full:contentInfo/MD_FeatureCatalogueDescription/featureCatalogueCitation/CI_Citation/title/CharacterString
				this.addGCOCharacterString(ciCitation.addElement("gmd:title"), keycTitles[i]);
				// T011_obj_geo_keyc.key_date
				// MD_Metadata/full:contentInfo/MD_FeatureCatalogueDescription/featureCatalogueCitation/CI_Citation/date/CI_Date/date/Date
				// UND .../CI_Date/date/DateType/CI_DateTypeCode/....
				String myDate = keycDates[i];
				Element ciDate = ciCitation.addElement("gmd:date").addElement("gmd:CI_Date");
				if (myDate != null && myDate.length() > 0) {
					ciDate.addElement("gmd:date").addElement("gco:Date").addText(Udk2CswDateFieldParser.instance().parseToDate(myDate));
				} else {
					ciDate.addElement("gmd:date").addElement("gco:Date").addAttribute("nilReason", "missing");
				}
				ciDate.addElement("gmd:dateType").addElement("gmd:CI_DateTypeCode").addAttribute("codeList",
				"http://www.tc211.org/ISO19139/resources/codeList.xml#CI_DateTypeCode").addAttribute(
				"codeListValue", "creation");
				// T011_obj_geo_keyc.edition
				// MD_Metadata/full:contentInfo/MD_FeatureCatalogueDescription/featureCatalogueCitation/CI_Citation/edition/CharacterString
				this.addGCOCharacterString(ciCitation.addElement("gmd:edition"), keycEditions[i]);
			}
		}
		
		// add content Info references
		List<String> contentInfoReferenceIdentifiers = IngridQueryHelper.getContentInfoReferenceIdentifiers(hit);
		for (int i=0; i < contentInfoReferenceIdentifiers.size(); i++) {
			metaData.addElement("gmd:contentInfo").addAttribute("uuidref", contentInfoReferenceIdentifiers.get(i));
		}
		
		
	}
	
	private void addContentInfoDatabase(Element metaData, IngridHit hit) {

		Element mdFeatureCatalogueDescription = null;
		
		String[] objDataParameter = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_DATA_PARA_PARAMETER);
		String[] objDataUnits = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_DATA_PARA_UNIT);
		if (objDataParameter.length > 0) {
			for (int i=0; i < objDataParameter.length; i++) {
				String featureType = objDataParameter[i];
				if (featureType != null && featureType.length() > 0) {
					if (mdFeatureCatalogueDescription == null) {
						mdFeatureCatalogueDescription = metaData.addElement("gmd:contentInfo").addElement("gmd:MD_FeatureCatalogueDescription");
						this.addGCOBoolean(mdFeatureCatalogueDescription.addElement("gmd:includedWithDataset"),
								false);
					}
					if (i < objDataUnits.length && objDataUnits[i] != null && objDataUnits[i].length() > 0) {
						featureType += objDataUnits[i];
					}
					mdFeatureCatalogueDescription.addElement("gmd:featureTypes").addElement("gco:LocalName").addText(featureType);
				}
			}
			if (mdFeatureCatalogueDescription != null) {
				Element ciCitation = mdFeatureCatalogueDescription.addElement("gmd:featureCatalogueCitation").addElement(
				"gmd:CI_Citation");
				this.addGCOCharacterString(ciCitation.addElement("gmd:title"), "German Environmental Catalog - parameters of object type 'database', version 1.0");
				Element ciDate = ciCitation.addElement("gmd:date").addElement("gmd:CI_Date");
				ciDate.addElement("gmd:date").addElement("gco:Date").addText("2006-05-01");
				ciDate.addElement("gmd:dateType").addElement("gmd:CI_DateTypeCode").addAttribute("codeList",
						"http://www.tc211.org/ISO19139/resources/codeList.xml#CI_DateTypeCode").addAttribute(
						"codeListValue", "publication");
			}
		}
		
		// add content Info references
		List<String> contentInfoReferenceIdentifiers = IngridQueryHelper.getContentInfoReferenceIdentifiers(hit);
		for (int i=0; i < contentInfoReferenceIdentifiers.size(); i++) {
			metaData.addElement("gmd:contentInfo").addAttribute("uuidref", contentInfoReferenceIdentifiers.get(i));
		}
		
		
	}	
	
	
	

	private void addReferenceSystemInfo(Element metaData, IngridHit hit) {

		// T011_obj_geo.referencesystem_id == [Domain-ID Codelist 100]
		// MD_Metadata/full:referenceSystemInfo/MD_ReferenceSystem/referenceSystemIdentifier/RS_Identifier/code/CharacterString
		Long code = null;
		String codeVal = null;
		try {
			code = Long.valueOf(IngridQueryHelper.getDetailValueAsString(hit,
					IngridQueryHelper.HIT_KEY_OBJECT_GEO_REFERENCESYSTEM_ID));
			codeVal = UtilsUDKCodeLists.getIsoCodeListEntryFromIgcId(100L, code);
		} catch (NumberFormatException e) {
			codeVal = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_GEO_REFERENCESYSTEM_ID);
		}
		if (IngridQueryHelper.hasValue(codeVal)) {
			Element rsIdentifier = metaData.addElement("gmd:referenceSystemInfo").addElement(
			"gmd:MD_ReferenceSystem").addElement("gmd:referenceSystemIdentifier").addElement(
			"gmd:RS_Identifier");
			this.addGCOCharacterString(rsIdentifier.addElement("gmd:code"), codeVal);
			if (codeVal.startsWith("EPSG")) {
				this.addGCOCharacterString(rsIdentifier.addElement("gmd:codeSpace"), "EPSG");
			}
		}

	}

	private void addSpatialRepresentationInfo(Element metaData, IngridHit hit) {
		Element mdVectorSpatialRepresentation = null;
		// T011_obj_geo.vector_topology_level ->
		// MD_Metadata/full:spatialRepresentationInfo/MD_VectorSpatialRepresentation/topologyLevel/MD_TopologyLevelCode/@CodeListValue
		String codeStr;
		try {
			Long code = Long.valueOf(IngridQueryHelper.getDetailValueAsString(hit,
					IngridQueryHelper.HIT_KEY_OBJECT_VECTOR_TOPOLOGY_LEVEL));
			codeStr = UtilsUDKCodeLists.getIsoCodeListEntryFromIgcId(528L, code);
			if (mdVectorSpatialRepresentation == null) {
				mdVectorSpatialRepresentation = metaData.addElement("gmd:spatialRepresentationInfo").addElement("gmd:MD_VectorSpatialRepresentation");
			}
			mdVectorSpatialRepresentation.addElement("gmd:topologyLevel").addElement("gmd:MD_TopologyLevelCode")
			.addAttribute("codeList","http://www.tc211.org/ISO19139/resources/codeList.xml#MD_TopologyLevelCode")
			.addAttribute("codeListValue", codeStr);
		} catch (NumberFormatException e) {}
		
		String[] geometricObjectsType = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_VECTOR_GEOMETRIC_OBJECT_TYPE);
		String[] geometricObjectsCount = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_VECTOR_GEOMETRIC_OBJECT_COUNT);
		for (int i = 0; i < geometricObjectsType.length; i++) {
			if (mdVectorSpatialRepresentation == null) {
				mdVectorSpatialRepresentation = metaData.addElement("gmd:spatialRepresentationInfo").addElement("gmd:MD_VectorSpatialRepresentation");
			}
			Element mdGeometricObjects = mdVectorSpatialRepresentation.addElement("gmd:geometricObjects").addElement(
					"gmd:MD_GeometricObjects");
			// T011_obj_geo_vector.geometric_object_type ->
			// MD_Metadata/spatialRepresentationInfo/MD_SpatialRepresentation/MD_VectorSpatialRepresentation/geometricObjects/geometricObjectType/MD_GeometricObjectTypeCode@codeListValue
			Long code = null;
			try {
				code = Long.valueOf(geometricObjectsType[i]);
			} catch (NumberFormatException e) {
			}
			codeStr = UtilsUDKCodeLists.getIsoCodeListEntryFromIgcId(515L, code);
			if (codeStr == null) {
				if (log.isDebugEnabled()) {
					log.debug("Unable to convert value '" + geometricObjectsType[i] + "' into ISO value with CodeList 515.");
				}
				codeStr = geometricObjectsType[i];
			}
			mdGeometricObjects.addElement("gmd:geometricObjectType").addElement("gmd:MD_GeometricObjectTypeCode")
					.addAttribute("codeList",
							"http://www.tc211.org/ISO19139/resources/codeList.xml#MD_GeometricObjectTypeCode")
					.addAttribute("codeListValue", codeStr);
			// T011_obj_geo_vector.geometric_object_count ->
			// MD_Metadata/full:spatialRepresentationInfo/MD_VectorSpatialRepresentation/geometricObjects/MD_GeometricObjects/geometricObjectCount
			this.addGCOInteger(mdGeometricObjects.addElement("gmd:geometricObjectCount"),
					geometricObjectsCount[i]);
		}

	}

	private void addDataQualityInfoService(Element metaData, IngridHit hit) {
		Element dqQualityInfo = null;

		
		// T011_obj_geo.special_base ->
		// MD_Metadata/dataQualityInfo/udk:DQ_DataQuality/lineage/udk:LI_Lineage/statement
		Element liLineage = null;
		// (dataset) T011_obj_serv.history ->
		// MD_Metadata/dataQualityInfo/udk:DQ_DataQuality/lineage/LI_Lineage/processStep/LI_ProcessStep/description
		String processStepDescription = IngridQueryHelper.getDetailValueAsString(hit,IngridQueryHelper.HIT_KEY_OBJECT_SERV_HISTORY);
		if (IngridQueryHelper.hasValue(processStepDescription)) {
			if (dqQualityInfo == null) {
				dqQualityInfo = metaData.addElement("gmd:dataQualityInfo").addElement("gmd:DQ_DataQuality");
				// add scope
				dqQualityInfo.addElement("gmd:scope").addElement("gmd:DQ_Scope").addElement("gmd:level").addElement("gmd:MD_ScopeCode").addAttribute("codeListValue", getTypeName().hierarchyLevel).addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#MD_ScopeCode");
			}
			if (liLineage == null) {
				liLineage = dqQualityInfo.addElement("gmd:lineage").addElement("gmd:LI_Lineage");
			}
			this.addGCOCharacterString(liLineage.addElement("gmd:processStep").addElement("gmd:LI_ProcessStep")
					.addElement("gmd:description"), processStepDescription);
		}
		// (dataset) T011_obj_serv.base ->
		// MD_Metadata/dataQualityInfo/DQ_DataQuality/lineage/LI_Lineage/source/LI_Source/description
		String liSourceDescription = null;
		if (IngridQueryHelper.hasValue(liSourceDescription)) {
			if (dqQualityInfo == null) {
				dqQualityInfo = metaData.addElement("gmd:dataQualityInfo").addElement("gmd:DQ_DataQuality");
				// add scope
				dqQualityInfo.addElement("gmd:scope").addElement("gmd:DQ_Scope").addElement("gmd:level").addElement("gmd:MD_ScopeCode").addAttribute("codeListValue", getTypeName().hierarchyLevel).addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#MD_ScopeCode");
			}
			if (liLineage == null) {
				liLineage = dqQualityInfo.addElement("gmd:lineage").addElement("gmd:LI_Lineage");
			}
			liLineage = dqQualityInfo.addElement("gmd:lineage").addElement("gmd:LI_Lineage");
			this.addGCOCharacterString(liLineage.addElement("gmd:source").addElement("gmd:LI_Source").addElement(
					"gmd:description"), liSourceDescription);
		}
		
		// add conformity object_conformity.*
		String[] objectConformitySpecifications = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_CONFORMITY_SPECIFICATION);
		String[] objectConformityDegreeKeys = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_CONFORMITY_DEGREE_KEY);
		String[] objectConformityPublicationDate = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_CONFORMITY_PUBLICTAION_DATE);
		
		for (int i = 0; i < objectConformitySpecifications.length; i++) {
			if (IngridQueryHelper.hasValue(objectConformityDegreeKeys[i]) && 
					(objectConformityDegreeKeys[i].equals("1") || objectConformityDegreeKeys[i].equals("2"))) {
				if (dqQualityInfo == null) {
					dqQualityInfo = metaData.addElement("gmd:dataQualityInfo").addElement("gmd:DQ_DataQuality");
					// add scope
					dqQualityInfo.addElement("gmd:scope").addElement("gmd:DQ_Scope").addElement("gmd:level").addElement("gmd:MD_ScopeCode").addAttribute("codeListValue", getTypeName().hierarchyLevel).addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#MD_ScopeCode");
				}
				Element dqDomainConsistency = dqQualityInfo.addElement("gmd:report").addElement("gmd:DQ_DomainConsistency");
				Element dqConformanceResult = dqDomainConsistency.addElement("gmd:result").addElement("gmd:DQ_ConformanceResult");
				
				// specification/CI_Citation/
				Element ciCitation = dqConformanceResult.addElement("gmd:specification").addElement("gmd:CI_Citation");
				// title/gco:CharacterString
				this.addGCOCharacterString(ciCitation.addElement("gmd:title"), objectConformitySpecifications[i]);
				// date/CI_Date/date/gco:Date
				Element ciDate = ciCitation.addElement("gmd:date").addElement("gmd:CI_Date");
				ciDate.addElement("gmd:date").addElement("gco:Date").addText(Udk2CswDateFieldParser.instance().parseToDate(objectConformityPublicationDate[i]));
				ciDate.addElement("gmd:dateType").addElement("gmd:CI_DateTypeCode").addAttribute("codeList",
						"http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/Codelist/ML_gmxCodelists.xml#CI_DateTypeCode").addAttribute(
						"codeListValue", "publication").addText("publication");
				// pass/gco:Boolean
				this.addGCOBoolean(dqConformanceResult.addElement("gmd:pass"), objectConformityDegreeKeys[i].equals("1"));

			}
		}
		

	}

	private void addDataQualityInfoDataSet(Element metaData, IngridHit hit) {
		Element dqQualityInfo = null;

		// T011_obj_geo.rec_grade ->
		// MD_Metadata/dataQualityInfo/udk:DQ_DataQuality/report/DQ_CompletenessCommission/DQ_QuantitativeResult/value/Record
		String completenessComission = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_GEO_REC_GRADE);
		if (IngridQueryHelper.hasValue(completenessComission)) {
			if (dqQualityInfo == null) {
				dqQualityInfo = metaData.addElement("gmd:dataQualityInfo").addElement("gmd:DQ_DataQuality");
				// add scope
				dqQualityInfo.addElement("gmd:scope").addElement("gmd:DQ_Scope").addElement("gmd:level").addElement("gmd:MD_ScopeCode").addAttribute("codeListValue", getTypeName().hierarchyLevel).addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#MD_ScopeCode");
			}
			Element completenessCommission = dqQualityInfo.addElement("gmd:report").addElement("gmd:DQ_CompletenessCommission");
			this.addGCOCharacterString(completenessCommission.addElement("gmd:measureDescription"), "completeness");
			Element dqQuantitativeResult = completenessCommission.addElement("gmd:result").addElement("gmd:DQ_QuantitativeResult");
			Element unitDefinition = dqQuantitativeResult.addElement("gmd:valueUnit").addElement("gml:UnitDefinition").addAttribute("gml:id", "unitDefinition_ID_" + UUID.randomUUID());
			unitDefinition.addElement("gml:identifier").addAttribute("codeSpace", "");
			unitDefinition.addElement("gml:name").addText("percent");
			unitDefinition.addElement("gml:quantityType").addText("completeness");
			unitDefinition.addElement("gml:catalogSymbol").addText("%");
			dqQuantitativeResult.addElement("gmd:value").addElement("gco:Record").addText(completenessComission);
		}

		// T011_obj_geo.pos_accuracy_vertical ->
		// MD_Metadata/dataQualityInfo/DQ_DataQuality/report/DQ_RelativeInternalPositionalAccuracy[measureDescription/CharacterString='vertical']/DQ_QuantitativeResult.value
		String verticalAccuracy = IngridQueryHelper.getDetailValueAsString(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_GEO_POS_ACCURACY_VERTICAL);
		if (IngridQueryHelper.hasValue(verticalAccuracy)) {
			if (dqQualityInfo == null) {
				dqQualityInfo = metaData.addElement("gmd:dataQualityInfo").addElement("gmd:DQ_DataQuality");
				// add scope
				dqQualityInfo.addElement("gmd:scope").addElement("gmd:DQ_Scope").addElement("gmd:level").addElement("gmd:MD_ScopeCode").addAttribute("codeListValue", getTypeName().hierarchyLevel).addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#MD_ScopeCode");
			}
			Element dqRelativeInternalPositionalAccuracy = dqQualityInfo.addElement("gmd:report")
					.addElement("gmd:DQ_RelativeInternalPositionalAccuracy");
			this.addGCOCharacterString(dqRelativeInternalPositionalAccuracy.addElement("gmd:measureDescription"),
					"vertical");

			Element dqQuantitativeResult = dqRelativeInternalPositionalAccuracy.addElement("gmd:result").addElement("gmd:DQ_QuantitativeResult");
			Element unitDefinition = dqQuantitativeResult.addElement("gmd:valueUnit").addElement("gml:UnitDefinition").addAttribute("gml:id", "unitDefinition_ID_" + UUID.randomUUID());
			unitDefinition.addElement("gml:identifier").addAttribute("codeSpace", "");
			unitDefinition.addElement("gml:name").addText("meter");
			unitDefinition.addElement("gml:quantityType").addText("vertical accuracy");
			unitDefinition.addElement("gml:catalogSymbol").addText("m");
			dqQuantitativeResult.addElement("gmd:value").addElement("gco:Record").addText(verticalAccuracy);
		}

		// T011_obj_geo.rec_exact ->
		// MD_Metadata/dataQualityInfo/udk:DQ_DataQuality/report/DQ_RelativeInternalPositionalAccuracy[measureDescription/CharacterString='geographic']/DQ_QuantitativeResult/value/Record
		String geographicAccuracy = IngridQueryHelper.getDetailValueAsString(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_GEO_REC_EXACT);
		if (IngridQueryHelper.hasValue(geographicAccuracy)) {
			if (dqQualityInfo == null) {
				dqQualityInfo = metaData.addElement("gmd:dataQualityInfo").addElement("gmd:DQ_DataQuality");
				// add scope
				dqQualityInfo.addElement("gmd:scope").addElement("gmd:DQ_Scope").addElement("gmd:level").addElement("gmd:MD_ScopeCode").addAttribute("codeListValue", getTypeName().hierarchyLevel).addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#MD_ScopeCode");
			}
			Element dqRelativeInternalPositionalAccuracy = dqQualityInfo.addElement("gmd:report")
					.addElement("gmd:DQ_RelativeInternalPositionalAccuracy");
			this.addGCOCharacterString(dqRelativeInternalPositionalAccuracy.addElement("gmd:measureDescription"),
					"geographic");

			Element dqQuantitativeResult = dqRelativeInternalPositionalAccuracy.addElement("gmd:result").addElement("gmd:DQ_QuantitativeResult");
			Element unitDefinition = dqQuantitativeResult.addElement("gmd:valueUnit").addElement("gml:UnitDefinition").addAttribute("gml:id", "unitDefinition_ID_" + UUID.randomUUID());
			unitDefinition.addElement("gml:identifier").addAttribute("codeSpace", "");
			unitDefinition.addElement("gml:name").addText("meter");
			unitDefinition.addElement("gml:quantityType").addText("geographic accuracy");
			unitDefinition.addElement("gml:catalogSymbol").addText("m");
			dqQuantitativeResult.addElement("gmd:value").addElement("gco:Record").addText(geographicAccuracy);
		}
		
		// add conformity object_conformity.*
		String[] objectConformitySpecifications = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_CONFORMITY_SPECIFICATION);
		String[] objectConformityDegreeKeys = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_CONFORMITY_DEGREE_KEY);
		String[] objectConformityPublicationDate = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_CONFORMITY_PUBLICTAION_DATE);
		
		for (int i = 0; i < objectConformitySpecifications.length; i++) {
			if (IngridQueryHelper.hasValue(objectConformityDegreeKeys[i]) && 
					(objectConformityDegreeKeys[i].equals("1") || objectConformityDegreeKeys[i].equals("2"))) {
				if (dqQualityInfo == null) {
					dqQualityInfo = metaData.addElement("gmd:dataQualityInfo").addElement("gmd:DQ_DataQuality");
					// add scope
					dqQualityInfo.addElement("gmd:scope").addElement("gmd:DQ_Scope").addElement("gmd:level").addElement("gmd:MD_ScopeCode").addAttribute("codeListValue", getTypeName().hierarchyLevel).addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#MD_ScopeCode");
				}
				Element dqDomainConsistency = dqQualityInfo.addElement("gmd:report").addElement("gmd:DQ_DomainConsistency");
				Element dqConformanceResult = dqDomainConsistency.addElement("gmd:result").addElement("gmd:DQ_ConformanceResult");
				// specification/CI_Citation/
				Element ciCitation = dqConformanceResult.addElement("gmd:specification").addElement("gmd:CI_Citation");
				// title/gco:CharacterString
				this.addGCOCharacterString(ciCitation.addElement("gmd:title"), objectConformitySpecifications[i]);
				// date/CI_Date/date/gco:Date
				Element ciDate = ciCitation.addElement("gmd:date").addElement("gmd:CI_Date");
				ciDate.addElement("gmd:date").addElement("gco:Date").addText(Udk2CswDateFieldParser.instance().parseToDate(objectConformityPublicationDate[i]));
				ciDate.addElement("gmd:dateType").addElement("gmd:CI_DateTypeCode").addAttribute("codeList",
						"http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/Codelist/ML_gmxCodelists.xml#CI_DateTypeCode").addAttribute(
						"codeListValue", "publication").addText("publication");
				this.addGCOCharacterString(dqConformanceResult.addElement("gmd:explanation"), "");
				// pass/gco:Boolean
				this.addGCOBoolean(dqConformanceResult.addElement("gmd:pass"), objectConformityDegreeKeys[i].equals("1"));
			}
		}
		
		
		
		// T011_obj_geo.special_base ->
		// MD_Metadata/dataQualityInfo/udk:DQ_DataQuality/lineage/udk:LI_Lineage/statement
		Element liLineage = null;
		String statement = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_GEO_SPECIAL_BASE);
		if (IngridQueryHelper.hasValue(statement)) {
			if (dqQualityInfo == null) {
				dqQualityInfo = metaData.addElement("gmd:dataQualityInfo").addElement("gmd:DQ_DataQuality");
				// add scope
				dqQualityInfo.addElement("gmd:scope").addElement("gmd:DQ_Scope").addElement("gmd:level").addElement("gmd:MD_ScopeCode").addAttribute("codeListValue", getTypeName().hierarchyLevel).addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#MD_ScopeCode");
			}
			if (liLineage == null) {
				liLineage = dqQualityInfo.addElement("gmd:lineage").addElement("gmd:LI_Lineage");
			}
			this.addGCOCharacterString(liLineage.addElement("gmd:statement"), IngridQueryHelper.getDetailValueAsString(
					hit, IngridQueryHelper.HIT_KEY_OBJECT_GEO_SPECIAL_BASE));
		}
		// (dataset) T011_obj_geo.method ->
		// MD_Metadata/dataQualityInfo/udk:DQ_DataQuality/lineage/LI_Lineage/processStep/LI_ProcessStep/description
		String processStepDescription = IngridQueryHelper.getDetailValueAsString(hit,IngridQueryHelper.HIT_KEY_OBJECT_GEO_METHOD);
		if (IngridQueryHelper.hasValue(processStepDescription)) {
			if (dqQualityInfo == null) {
				dqQualityInfo = metaData.addElement("gmd:dataQualityInfo").addElement("gmd:DQ_DataQuality");
				// add scope
				dqQualityInfo.addElement("gmd:scope").addElement("gmd:DQ_Scope").addElement("gmd:level").addElement("gmd:MD_ScopeCode").addAttribute("codeListValue", getTypeName().hierarchyLevel).addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#MD_ScopeCode");
			}
			if (liLineage == null) {
				liLineage = dqQualityInfo.addElement("gmd:lineage").addElement("gmd:LI_Lineage");
			}
			this.addGCOCharacterString(liLineage.addElement("gmd:processStep").addElement("gmd:LI_ProcessStep")
					.addElement("gmd:description"), processStepDescription);
		}
		// (dataset) T011_obj_geo.data_base ->
		// MD_Metadata/dataQualityInfo/DQ_DataQuality/lineage/LI_Lineage/source/LI_Source/description
		String liSourceDescription = IngridQueryHelper.getDetailValueAsString(hit,IngridQueryHelper.HIT_KEY_OBJECT_GEO_DATA_BASE);
		if (IngridQueryHelper.hasValue(liSourceDescription)) {
			if (dqQualityInfo == null) {
				dqQualityInfo = metaData.addElement("gmd:dataQualityInfo").addElement("gmd:DQ_DataQuality");
				// add scope
				dqQualityInfo.addElement("gmd:scope").addElement("gmd:DQ_Scope").addElement("gmd:level").addElement("gmd:MD_ScopeCode").addAttribute("codeListValue", getTypeName().hierarchyLevel).addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#MD_ScopeCode");
			}
			if (liLineage == null) {
				liLineage = dqQualityInfo.addElement("gmd:lineage").addElement("gmd:LI_Lineage");
			}
			this.addGCOCharacterString(liLineage.addElement("gmd:source").addElement("gmd:LI_Source").addElement(
					"gmd:description"), liSourceDescription);
		}
	}

	private void addDataQualityInfoDatabase(Element metaData, IngridHit hit) {
		Element dqQualityInfo = null;

		// t011_obj_data.base
		Element liLineage = null;
		String liSourceDescription = IngridQueryHelper.getDetailValueAsString(hit,IngridQueryHelper.HIT_KEY_OBJECT_DATA_BASE);
		if (IngridQueryHelper.hasValue(liSourceDescription)) {
			dqQualityInfo = metaData.addElement("gmd:dataQualityInfo").addElement("gmd:DQ_DataQuality");
			// add scope
			dqQualityInfo.addElement("gmd:scope").addElement("gmd:DQ_Scope").addElement("gmd:level").addElement("gmd:MD_ScopeCode").addAttribute("codeListValue", getTypeName().hierarchyLevel).addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#MD_ScopeCode");
			if (liLineage == null) {
				liLineage = dqQualityInfo.addElement("gmd:lineage").addElement("gmd:LI_Lineage");
			}
			this.addGCOCharacterString(liLineage.addElement("gmd:source").addElement("gmd:LI_Source").addElement(
					"gmd:description"), liSourceDescription);
		}
	}

	private void addDataQualityInfoLiterature(Element metaData, IngridHit hit) {
		
		// t011_obj_data.base
		Element liLineage = null;
		String liSourceDescription = IngridQueryHelper.getDetailValueAsString(hit,IngridQueryHelper.HIT_KEY_OBJECT_LITERATURE_BASE);
		if (IngridQueryHelper.hasValue(liSourceDescription)) {
			Element dqQualityInfo = metaData.addElement("gmd:dataQualityInfo").addElement("gmd:DQ_DataQuality");
			// add scope
			dqQualityInfo.addElement("gmd:scope").addElement("gmd:DQ_Scope").addElement("gmd:level").addElement("gmd:MD_ScopeCode").addAttribute("codeListValue", getTypeName().hierarchyLevel).addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#MD_ScopeCode");
			if (liLineage == null) {
				liLineage = dqQualityInfo.addElement("gmd:lineage").addElement("gmd:LI_Lineage");
			}
			this.addGCOCharacterString(liLineage.addElement("gmd:source").addElement("gmd:LI_Source").addElement(
					"gmd:description"), liSourceDescription);
		}
	}
	
	
	private void addCitation(Element parent, IngridHit hit) throws Exception {
		Element ciCitation = parent.addElement("gmd:citation").addElement("gmd:CI_Citation");
		// add title
		this.addGCOCharacterString(ciCitation.addElement("gmd:title"), IngridQueryHelper.getDetailValueAsString(
				hit, IngridQueryHelper.HIT_KEY_OBJECT_TITLE));
		// add alternate title
		String alternateTitle = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_DATASET_ALTERNATE_TITLE);
		if (IngridQueryHelper.hasValue(alternateTitle)) {
			this.addGCOCharacterString(ciCitation.addElement("gmd:alternateTitle"), alternateTitle);
		}
		
		// add dates (creation, revision etc.)
		super.addCitationReferenceDates(ciCitation, hit, "gmd");

		// add identifier
		String identifier = getCitationIdentifier(hit);
		if (IngridQueryHelper.hasValue(identifier)) {
			Element rsIdentifier = ciCitation.addElement("gmd:identifier").addElement("gmd:RS_Identifier");
			this.addGCOCharacterString(rsIdentifier.addElement("gmd:code"), identifier);
			this.addGCOCharacterString(rsIdentifier.addElement("gmd:codeSpace"), "ingrid");
		}
		
		
		// map literature properties
		if (udkClass.equals("2")) {
			
			String publishYear = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_LITERATURE_PUBLISH_YEAR);
			if (IngridQueryHelper.hasValue(publishYear)) {
				ciCitation.addElement("gmd:editionDate").addElement("gco:Date").addText(Udk2CswDateFieldParser.instance().parseToDate(publishYear));
			}
			String author = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_LITERATURE_AUTHOR);
			if (IngridQueryHelper.hasValue(author)) {
				Element responsiblePartyOriginator = ciCitation.addElement("gmd:citedResponsibleParty").addElement("gmd:CI_ResponsibleParty");
				this.addGCOCharacterString(responsiblePartyOriginator.addElement("gmd:individualName"), author);
				responsiblePartyOriginator.addElement("gmd:role").addElement("gmd:CI_RoleCode")
                .addAttribute("codeList", "http://www.tc211.org/ISO19139/resources/codeList.xml#CI_RoleCode")
                .addAttribute("codeListValue", "originator");
			}
			
			Element responsiblePartyResourceProvider = null;
			String location = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_LITERATURE_LOCATION);
			if (IngridQueryHelper.hasValue(location)) {
				responsiblePartyResourceProvider = ciCitation.addElement("gmd:citedResponsibleParty").addElement("gmd:CI_ResponsibleParty");
				this.addGCOCharacterString(responsiblePartyResourceProvider.addElement("gmd:organisationName"), "Contact intructions for the location of resource");
				this.addGCOCharacterString(responsiblePartyResourceProvider.addElement("gmd:contactInfo").addElement("gmd:CI_Contact")
						.addElement("gmd:contactInstructions"), location);
				responsiblePartyResourceProvider.addElement("gmd:role").addElement("gmd:CI_RoleCode")
                .addAttribute("codeList", "http://www.tc211.org/ISO19139/resources/codeList.xml#CI_RoleCode")
                .addAttribute("codeListValue", "resourceProvider");
			}
	        String[] addressIds = IngridQueryHelper.getDetailValueAsArray(hit, "t012_obj_adr.adr_id");
	        String[] addressTypes = IngridQueryHelper.getDetailValueAsArray(hit, "t012_obj_adr.typ");
	        for (int i = 0; i < addressTypes.length; i++) {
	        	// leader
	        	if (addressTypes[i].equals("3360")) {
	        		responsiblePartyResourceProvider = ciCitation.addElement("gmd:citedResponsibleParty").addElement("gmd:CI_ResponsibleParty");
	        		this.addResponsibleParty(responsiblePartyResourceProvider, hit, addressIds[i], "resourceProvider");
	        	}
	        }
			

			Element responsiblePartyPublisher = null;
			String editorLocation = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_LITERATURE_PUBLISH_LOC);
			String editor = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_LITERATURE_PUBLISHER);
			if (IngridQueryHelper.hasValue(editorLocation) || IngridQueryHelper.hasValue(editor)) {
				responsiblePartyPublisher = ciCitation.addElement("gmd:citedResponsibleParty").addElement("gmd:CI_ResponsibleParty");
				if (!IngridQueryHelper.hasValue(editor)) {
					this.addGCOCharacterString(responsiblePartyPublisher.addElement("gmd:individualName"), "Location of the editor");
				} else {
					this.addGCOCharacterString(responsiblePartyPublisher.addElement("gmd:individualName"), editor);
				}
				if (IngridQueryHelper.hasValue(editorLocation)) {
					this.addGCOCharacterString(responsiblePartyPublisher.addElement("gmd:contactInfo").addElement("gmd:CI_Contact")
							.addElement("gmd:address").addElement("gmd:CI_Address").addElement("gmd:city"), editorLocation);
				}
				responsiblePartyPublisher.addElement("gmd:role").addElement("gmd:CI_RoleCode")
                .addAttribute("codeList", "http://www.tc211.org/ISO19139/resources/codeList.xml#CI_RoleCode")
                .addAttribute("codeListValue", "publisher");
			}
			
			String publisher = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_LITERATURE_PUBLISHING);
			if (IngridQueryHelper.hasValue(publisher)) {
				Element responsiblePartyDistributor = ciCitation.addElement("gmd:citedResponsibleParty").addElement("gmd:CI_ResponsibleParty");
				this.addGCOCharacterString(responsiblePartyDistributor.addElement("gmd:organisationName"), publisher);
				responsiblePartyDistributor.addElement("gmd:role").addElement("gmd:CI_RoleCode")
                .addAttribute("codeList", "http://www.tc211.org/ISO19139/resources/codeList.xml#CI_RoleCode")
                .addAttribute("codeListValue", "distribute");
			}
					
			
			Element citationSeries = null;
			String publishedIn = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_LITERATURE_PUBLISH_IN);
			if (IngridQueryHelper.hasValue(publishedIn)) {
				if (citationSeries == null) {
					citationSeries = ciCitation.addElement("gmd:series").addElement("gmd:CI_Series");
				}
				this.addGCOCharacterString(citationSeries.addElement("gmd:name"), publishedIn);
			}
			String volume = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_LITERATURE_VOLUME);
			if (IngridQueryHelper.hasValue(volume)) {
				if (citationSeries == null) {
					citationSeries = ciCitation.addElement("gmd:series").addElement("gmd:CI_Series");
				}
				this.addGCOCharacterString(citationSeries.addElement("gmd:issueIdentification"), volume);
			}
			String pages = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_LITERATURE_SIDES);
			if (IngridQueryHelper.hasValue(pages)) {
				if (citationSeries == null) {
					citationSeries = ciCitation.addElement("gmd:series").addElement("gmd:CI_Series");
				}
				this.addGCOCharacterString(citationSeries.addElement("gmd:page"), pages);
			}

			String docInfo = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_LITERATURE_DOC_INFO);
			if (IngridQueryHelper.hasValue(docInfo)) {
				this.addGCOCharacterString(ciCitation.addElement("gmd:otherCitationDetails"), docInfo);
			}
			
			String isbn = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_LITERATURE_ISBN);
			if (IngridQueryHelper.hasValue(isbn)) {
				this.addGCOCharacterString(ciCitation.addElement("gmd:ISBN"), isbn);
			}
		} else if (udkClass.equals("4")) {
			Element responsiblePartyOriginator = null;
			String leader = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_PROJECT_LEADER);
			if (IngridQueryHelper.hasValue(leader)) {
				responsiblePartyOriginator = ciCitation.addElement("gmd:citedResponsibleParty").addElement("gmd:CI_ResponsibleParty");
				this.addGCOCharacterString(responsiblePartyOriginator.addElement("gmd:individualName"), leader);
				responsiblePartyOriginator.addElement("gmd:role").addElement("gmd:CI_RoleCode")
                .addAttribute("codeList", "http://www.tc211.org/ISO19139/resources/codeList.xml#CI_RoleCode")
                .addAttribute("codeListValue", "projectManager");
			}
	        String[] addressIds = IngridQueryHelper.getDetailValueAsArray(hit, "t012_obj_adr.adr_id");
	        String[] addressTypes = IngridQueryHelper.getDetailValueAsArray(hit, "t012_obj_adr.typ");
	        for (int i = 0; i < addressTypes.length; i++) {
	        	// leader
	        	if (addressTypes[i].equals("3400")) {
					responsiblePartyOriginator = ciCitation.addElement("gmd:citedResponsibleParty").addElement("gmd:CI_ResponsibleParty");
	        		this.addResponsibleParty(responsiblePartyOriginator, hit, addressIds[i], "projectManager");
	        	}
	        }
			
			
	        Element responsiblePartyMember = null;
	        String member = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_PROJECT_LEADER);
			if (IngridQueryHelper.hasValue(member)) {
				responsiblePartyMember = ciCitation.addElement("gmd:citedResponsibleParty").addElement("gmd:CI_ResponsibleParty");
				this.addGCOCharacterString(responsiblePartyOriginator.addElement("gmd:individualName"), member);
				responsiblePartyMember.addElement("gmd:role").addElement("gmd:CI_RoleCode")
                .addAttribute("codeList", "http://www.tc211.org/ISO19139/resources/codeList.xml#CI_RoleCode")
                .addAttribute("codeListValue", "projectParticipant");
			}
	        addressIds = IngridQueryHelper.getDetailValueAsArray(hit, "t012_obj_adr.adr_id");
	        addressTypes = IngridQueryHelper.getDetailValueAsArray(hit, "t012_obj_adr.typ");
	        for (int i = 0; i < addressTypes.length; i++) {
	        	// member
	        	if (addressTypes[i].equals("3410")) {
					responsiblePartyMember = ciCitation.addElement("gmd:citedResponsibleParty").addElement("gmd:CI_ResponsibleParty");
	        		this.addResponsibleParty(responsiblePartyMember, hit, addressIds[i], "projectParticipant");
	        	}
	        }
		}
		
		
		// citedResponsibleParty not implemented, because only
		// UDK classes 1 and 3 are supported. citedResponsibleParty always maps
		// to
		// udk class 2

		// presentationForm not implemented, see citedResponsibleParty

		// series, otherCitationDetails not implemented, see
		// citedResponsibleParty

		// collectiveTitle, ISBN, etc. see citedResponsibleParty

	}

	private void addIdentificationInfoService(Element metaData, IngridHit hit) throws Exception {
		Element svServiceIdentification = metaData.addElement("gmd:identificationInfo").addElement(
				"srv:SV_ServiceIdentification");

        svServiceIdentification.addAttribute("uuid", "ingrid#" + getCitationIdentifier(hit));
		
		addGenericMetadataIndentification(svServiceIdentification, hit);

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

		String[] serviceTypeVersions = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_SERVICE_TYPE_VERSION);
		for (int i = 0; i < serviceTypeVersions.length; i++) {
			this.addGCOCharacterString(svServiceIdentification.addElement("srv:serviceTypeVersion"),
					serviceTypeVersions[i]);
		}

		super.addExtent(svServiceIdentification, hit, "srv");

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

        super.addOperationMetadata(svServiceIdentification, hit);
		
		List references = IngridQueryHelper.getReferenceIdentifiers(hit);
		for (int i=0; i< references.size(); i++) {
			// srv:operatesOn
			svServiceIdentification.addElement("srv:operatesOn").addElement("gmd:Reference").addAttribute(
					"uuidref", (String)references.get(i));
		}
		
		// add second identification info for all information that cannot be mapped into a SV_ServiceIdentification element
		String systemEnvironment = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_ENVIROMENT);
		String supplementalInformation = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_DESCRIPTION);
		// add the elements that cannot be mapped into the SV_ServiceIdentification
		String[] spacialResolutionScale = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_SERVICE_SPATIAL_RES_SCALE);
		String[] spacialResolutionGround = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_SERVICE_SPATIAL_RES_GROUND);
		String[] spacialResolutionScan = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_SERVICE_SPATIAL_RES_SCAN);
		if (IngridQueryHelper.hasValue(systemEnvironment) || IngridQueryHelper.hasValue(supplementalInformation) || 
				spacialResolutionScale.length > 0 || spacialResolutionGround.length > 0 || spacialResolutionScan.length > 0) {
			Element mdDataIdentification = metaData.addElement("gmd:identificationInfo").addElement(
			"gmd:MD_DataIdentification");
			mdDataIdentification.addAttribute("uuid", this.getFileIdentifier(hit));
			// add necessary elements for schema validation
			Element ciCitation = mdDataIdentification.addElement("gmd:citation").addElement("gmd:CI_Citation");
			this.addGCOCharacterString(ciCitation.addElement("gmd:title").addAttribute("gco:nilReason", "other:providedInPreviousIdentificationInfo"), "");
			Element ciDate = ciCitation.addElement("gmd:date").addElement("gmd:CI_Date");
			ciDate.addElement("gmd:date").addAttribute("gco:nilReason", "other:providedInPreviousIdentification").addElement("gco:Date").addText("");
			ciDate.addElement("gmd:dateType").addElement("gmd:CI_DateTypeCode").addAttribute("gco:nilReason", "other:providedInPreviousIdentificationInfo").addAttribute("codeList",
					"").addAttribute("codeListValue", "");
			this.addGCOCharacterString(mdDataIdentification.addElement("gmd:abstract").addAttribute("gco:nilReason", "other:providedInPreviousIdentificationInfo"), "");
			this.addGCOCharacterString(mdDataIdentification.addElement("gmd:language").addAttribute("gco:nilReason", "other:providedInPreviousIdentificationInfo"), "");
			
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
			for (int i = 0; i < spacialResolutionScan.length; i++) {
				if (IngridQueryHelper.hasValue(spacialResolutionScan[i])) {
					mdDataIdentification.addElement("gmd:spatialResolution").addElement(
					"gmd:MD_Resolution").addElement("gmd:distance").addElement("gco:Distance").addAttribute("uom", "dpi").addText(spacialResolutionScan[i]);
				}
			}
			if (IngridQueryHelper.hasValue(systemEnvironment)) {
				this.addGCOCharacterString(mdDataIdentification.addElement("gmd:environmentDescription"), systemEnvironment);
			}
			if (IngridQueryHelper.hasValue(supplementalInformation)) {
				this.addGCOCharacterString(mdDataIdentification.addElement("gmd:supplementalInformation"), supplementalInformation);
			}
		}

	}

	/**
	 * Adds generic identification info elements. Takes a abtractPostfix parameter to add a postfix to the end of the 
	 * abstract. This is necessary for service objects that need to add information to the abstract that will be generated
	 * depending on the content of certain elements of the metadata.
	 * 
	 * @param parent
	 * @param hit
	 * @param abtractPostfix
	 * @throws Exception
	 */
	private void addGenericMetadataIndentification(Element parent, IngridHit hit) throws Exception {
		// add citation construct
		this.addCitation(parent, hit);

		String isoAbstract = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_DESCR);
		
		if (udkClass.equals("3")) {
		
			String abstractPostfix = null;
			final String abstractPostfixPrefix = "\n\n\nWeitere Daten des Dienstes, die nicht standard-konform (ISO 19119) hinterlegt werden können, zum Teil gemäß INSPIRE-Direktive aber bereit zu stellen sind*:\n\n\n";
			// Weitere Daten des Dienstes, die nicht standard-konform (ISO 19119) hinterlegt werden können, gemäß INSPIRE-Direktive aber bereit zu stellen sind*:
			String systemEnvironment = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_ENVIROMENT);
			if (IngridQueryHelper.hasValue(systemEnvironment)) {
				if (abstractPostfix == null) {
					abstractPostfix = abstractPostfixPrefix;
				}
				abstractPostfix += "Systemumgebung: " + systemEnvironment + "\n";
				abstractPostfix += "(environmentDescription/gco:CharacterString= " + systemEnvironment + ")\n\n";
			}
			
			String supplementalInformation = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_DESCRIPTION);
			if (IngridQueryHelper.hasValue(supplementalInformation)) {
				if (abstractPostfix == null) {
					abstractPostfix = abstractPostfixPrefix;
				}
				abstractPostfix += "Erläuterung zum Fachbezug: " + supplementalInformation + "\n";
				abstractPostfix += "(supplementalInformation/gco:CharacterString= " + supplementalInformation + ")\n\n";
			}
			
			String[] spacialResolutionScale = IngridQueryHelper.getDetailValueAsArray(hit,
					IngridQueryHelper.HIT_KEY_SERVICE_SPATIAL_RES_SCALE);
			String[] spacialResolutionGround = IngridQueryHelper.getDetailValueAsArray(hit,
					IngridQueryHelper.HIT_KEY_SERVICE_SPATIAL_RES_GROUND);
			String[] spacialResolutionScan = IngridQueryHelper.getDetailValueAsArray(hit,
					IngridQueryHelper.HIT_KEY_SERVICE_SPATIAL_RES_SCAN);
			for (int i = 0; i < spacialResolutionScale.length; i++) {
				if (IngridQueryHelper.hasValue(spacialResolutionScale[i])) {
					if (abstractPostfix == null) {
						abstractPostfix = abstractPostfixPrefix;
					}
					abstractPostfix += "Erstellungsmassstab: " + spacialResolutionScale[i] + "\n";
					abstractPostfix += "(spatialResolution/MD_Resolution/equivalentScale/MD_RepresentativeFraction/denominator/gco:Integer= " + spacialResolutionScale[i] + ")\n";
				}
			}
			for (int i = 0; i < spacialResolutionGround.length; i++) {
				if (IngridQueryHelper.hasValue(spacialResolutionGround[i])) {
					if (abstractPostfix == null) {
						abstractPostfix = abstractPostfixPrefix;
					}
					abstractPostfix += "Bodenauflösung (Meter): " + spacialResolutionGround[i] + "\n";
					abstractPostfix += "(spatialResolution/MD_Resolution/distance/gco:Distance[@uom=\"meter\"]= " + spacialResolutionGround[i] + ")\n";
				}
			}
			for (int i = 0; i < spacialResolutionScan.length; i++) {
				if (IngridQueryHelper.hasValue(spacialResolutionScan[i])) {
					if (abstractPostfix == null) {
						abstractPostfix = abstractPostfixPrefix;
					}
					abstractPostfix += "Scanauflösung (DPI): " + spacialResolutionScan[i] + "\n";
					abstractPostfix += "(spatialResolution/MD_Resolution/distance/gco:Distance[@uom=\"dpi\"]= " + spacialResolutionScan[i] + ")\n";
				}
				abstractPostfix += "\n";
			}
			
			if (abstractPostfix != null) {
				abstractPostfix += "\n\n---\n";
				abstractPostfix += "* Nähere Informationen zur INSPIRE-Direktive: http://inspire.jrc.ec.europa.eu/implementingRulesDocs_md.cfm";
				isoAbstract += abstractPostfix;
			}
		}
		
		
        // add abstract
		this.addGCOCharacterString(parent.addElement("gmd:abstract"), isoAbstract);

		// add purpose
		// combine Herstellungszweck and rechtliche Grundlagen
		String purpose = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_INFO_NOTE);
		String[] legis = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_LEGIST);
		for (int i = 0; i < legis.length; i++) {
			if (purpose.length() != 0) {
				purpose = purpose.concat("\n");
			}
			purpose = purpose.concat(legis[i]);
		}
		if (IngridQueryHelper.hasValue(purpose)) {
			this.addGCOCharacterString(parent.addElement("gmd:purpose"), purpose);
		}

		// add status
		try {
			Long code = Long.valueOf(IngridQueryHelper.getDetailValueAsString(hit,
					IngridQueryHelper.HIT_KEY_OBJECT_TIME_STATUS));
			String codeVal = UtilsUDKCodeLists.getIsoCodeListEntryFromIgcId(523L, code);
			if (codeVal.length() > 0) {
				parent.addElement("gmd:status").addElement("gmd:MD_ProgressCode").addAttribute("codeList",
						"http://www.tc211.org/ISO19139/resources/codeList.xml#MD_ProgressCode").addAttribute(
						"codeListValue", codeVal);
			}
		} catch (NumberFormatException e) {
		}

		addPointOfContacts(parent, hit, "gmd");
		
		// resource maintenance
		try {
			Long code = Long.valueOf(IngridQueryHelper.getDetailValueAsString(hit,
					IngridQueryHelper.HIT_KEY_OBJECT_TIME_PERIOD));
			String codeVal = UtilsUDKCodeLists.getIsoCodeListEntryFromIgcId(518L, code);
			if (codeVal.length() > 0) {
				Element mdMaintenanceInformation = parent.addElement("gmd:resourceMaintenance").addElement(
				"gmd:MD_MaintenanceInformation");
				mdMaintenanceInformation.addElement("gmd:maintenanceAndUpdateFrequency").addElement(
						"gmd:MD_MaintenanceFrequencyCode").addAttribute("codeList",
						"http://www.tc211.org/ISO19139/resources/codeList.xml#MD_MaintenanceFrequencyCode")
						.addAttribute("codeListValue", codeVal);
				// userDefinedMaintenanceFrequency
				String timeInterval = IngridQueryHelper.getDetailValueAsString(hit,
						IngridQueryHelper.HIT_KEY_OBJECT_TIME_INTERVAL);
				String timeAlle = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_TIME_ALLE);
				if (timeInterval.length() > 0 && timeAlle.length() > 0) {
					String period19108 = "P";
					if (timeInterval.equalsIgnoreCase("Tage")) {
						period19108 = period19108.concat(timeAlle).concat("D");
					} else if (timeInterval.equalsIgnoreCase("Jahre")) {
						period19108 = period19108.concat(timeAlle).concat("Y");
					} else if (timeInterval.equalsIgnoreCase("Monate")) {
						period19108 = period19108.concat(timeAlle).concat("M");
					} else if (timeInterval.equalsIgnoreCase("Stunden")) {
						period19108 = period19108.concat("T").concat(timeAlle).concat("H");
					} else if (timeInterval.equalsIgnoreCase("Minuten")) {
						period19108 = period19108.concat("T").concat(timeAlle).concat("M");
					} else if (timeInterval.equalsIgnoreCase("Sekunden")) {
						period19108 = period19108.concat("T").concat(timeAlle).concat("S");
					}
					mdMaintenanceInformation.addElement("gmd:userDefinedMaintenanceFrequency").addElement(
							"gts:TM_PeriodDuration").addText(period19108);
				}
				String maintenanceNote = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_TIME_DESCR);
				if (IngridQueryHelper.hasValue(maintenanceNote)) {
					this.addGCOCharacterString(mdMaintenanceInformation.addElement("gmd:maintenanceNote"), maintenanceNote);
				}
			}
		} catch (NumberFormatException e) {
		}
		
		// udk class literature
		if (udkClass.equals("2")) {
			String literatureType = null;
			try {
				Long code = Long.valueOf(IngridQueryHelper.getDetailValueAsString(hit,
						IngridQueryHelper.HIT_KEY_OBJECT_LITERATURE_TYPE_KEY));
				literatureType = UtilsUDKCodeLists.getIsoCodeListEntryFromIgcId(3385L, code);
			} catch (NumberFormatException e) {	}
			if (!IngridQueryHelper.hasValue(literatureType)) {
				literatureType = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_LITERATURE_TYPE_VALUE);
			}
			if (IngridQueryHelper.hasValue(literatureType)) {
				Element mdFormat = parent.addElement("gmd:resourceFormat").addElement("gmd:MD_Format");
				this.addGCOCharacterString(mdFormat.addElement("gmd:name"), literatureType);
				this.addGCOCharacterString(mdFormat.addElement("gmd:version").addAttribute("gco:nilReason", "inapplicable"), "");
			}
		}
		
		
		addDescriptiveKeywords(parent, hit);		
		
		// add resourceSpecificUsage
		String usage = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_DATASET_USAGE);
		if (IngridQueryHelper.hasValue(usage)) {
			Element usageType = parent.addElement("gmd:resourceSpecificUsage").addElement("gmd:MD_Usage");
			this.addGCOCharacterString(usageType.addElement("gmd:specificUsage"), usage);
			
			usageType.addElement("gmd:userContactInfo").addElement("gmd:CI_ResponsibleParty").addElement("gmd:role").addElement("gmd:CI_RoleCode")
	        .addAttribute("codeList", "http://www.tc211.org/ISO19139/resources/codeList.xml#CI_RoleCode")
	        .addAttribute("codeListValue", "pointOfContact");
		}

		// add resourceConstraint uselimitations
		Element mdLegalContraints = null;
		String[] resourceConstraints = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_AVAIL_ACCESS_NOTE);
		if (resourceConstraints.length > 0) {
			if (mdLegalContraints == null) {
				mdLegalContraints = parent.addElement("gmd:resourceConstraints").addElement("gmd:MD_LegalConstraints");
			}
			for (int i = 0; i < resourceConstraints.length; i++) {
				this.addGCOCharacterString(mdLegalContraints.addElement("gmd:useLimitation"), resourceConstraints[i]);
			}
		}
        
        // add resource constraint MD_SecurityConstraints/otherConstraints
		String[] securityConstraintsKey = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_ACCESS_RESTRICTION_KEY);
		if (securityConstraintsKey.length > 0) {
			if (mdLegalContraints == null) {
				mdLegalContraints = parent.addElement("gmd:resourceConstraints").addElement("gmd:MD_LegalConstraints");
			}
			mdLegalContraints.addElement("gmd:accessConstraints").addElement("gmd:MD_RestrictionCode")
			.addAttribute("codeListValue", "otherRestrictions")
			.addAttribute("codeList", "http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/Codelist/gmxCodelists.xml#MD_RestrictionCode")
			.addText("otherRestrictions");
			for (int i = 0; i < securityConstraintsKey.length; i++) {
				String securityConstraint = null;
				if (securityConstraintsKey[i].equals("1")) {
					securityConstraint = "no conditions apply";
				} else if (securityConstraintsKey[i].equals("2")) {
					securityConstraint = "the confidentiality of the proceedings of public authorities";
				} else if (securityConstraintsKey[i].equals("3")) {
					securityConstraint = "international relations, public security or national defence";
				} else if (securityConstraintsKey[i].equals("4")) {
					securityConstraint = "the course of justice";
				} else if (securityConstraintsKey[i].equals("5")) {
					securityConstraint = "the confidentiality of commercial or industrial information";
				} else if (securityConstraintsKey[i].equals("6")) {
					securityConstraint = "intellectual property rights";
				} else if (securityConstraintsKey[i].equals("7")) {
					securityConstraint = "the confidentiality of personal data and/or files";
				} else if (securityConstraintsKey[i].equals("8")) {
					securityConstraint = "the interests or protection of any person";
				} else if (securityConstraintsKey[i].equals("9")) {
					securityConstraint = "the protection of the environment";
				} else if (securityConstraintsKey[i].equals("10")) {
					securityConstraint = "conditions unknown";
				}
				if (securityConstraint != null) {
					this.addGCOCharacterString(mdLegalContraints.addElement("gmd:otherConstraints"), securityConstraint);
				}
			}
		}
		
		// add security constraints
		String publishId = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_PUBLISH_ID);
		if (IngridQueryHelper.hasValue(publishId)) {
			String securityConstraint = null;
			if (publishId.equals("1")) {
				securityConstraint = "unclassified";
			} else if (publishId.equals("2")) {
				securityConstraint = "restricted";
			} else if (publishId.equals("3")) {
				securityConstraint = "confidential";
			}
			
			if (IngridQueryHelper.hasValue(securityConstraint)) {
				mdLegalContraints = parent.addElement("gmd:resourceConstraints").addElement("gmd:MD_SecurityConstraints")
					.addElement("gmd:classification").addElement("gmd:MD_ClassificationCode")
					.addAttribute("codeListValue", securityConstraint)
					.addAttribute("codeList", "http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/Codelist/gmxCodelists.xml#gmd:MD_ClassificationCode")
					.addText(securityConstraint);
			}
		}
	}
	

	private void addIdentificationInfoDataset(Element metaData, IngridHit hit) throws Exception {
		Element mdDataIdentification = metaData.addElement("gmd:identificationInfo").addElement(
				"gmd:MD_DataIdentification");

        mdDataIdentification.addAttribute("uuid", "ingrid#" + getCitationIdentifier(hit));
		
        addGenericMetadataIndentification(mdDataIdentification, hit);

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
		// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:spatialResolution/gmd:MD_Resolution/gmd:distance/gco:Distance/gmd:value//Decimal
		// T011_obj_geo_scale.resolution_scan ->
		// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:spatialResolution/gmd:MD_Resolution/gmd:distance/gco:Distance/gmd:value/Decimal
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
		for (int i = 0; i < spacialResolutionScan.length; i++) {
			if (IngridQueryHelper.hasValue(spacialResolutionScan[i])) {
				mdDataIdentification.addElement("gmd:spatialResolution").addElement(
				"gmd:MD_Resolution").addElement("gmd:distance").addElement("gco:Distance").addAttribute("uom", "dpi").addText(spacialResolutionScan[i]);
			}
		}

		// add language
		String dataLang = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_DATA_LANGUAGE);
		if (IngridQueryHelper.hasValue(dataLang)) {
			mdDataIdentification.addElement("gmd:language").addElement("gmd:LanguageCode")
				.addAttribute("codeList", "http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/Codelist/ML_gmxCodelists.xml#LanguageCode")
				.addAttribute("codeListValue", getISO639_2LanguageCode(dataLang));
		}
		
		// add characterset
		try {
			Long code = Long.valueOf(IngridQueryHelper.getDetailValueAsString(hit,
					IngridQueryHelper.HIT_KEY_OBJECT_METADATA_CHARACTER_SET));
			String codeVal = UtilsUDKCodeLists.getIsoCodeListEntryFromIgcId(510L, code);
			if (IngridQueryHelper.hasValue(codeVal)) {
				mdDataIdentification.addElement("gmd:characterSet").addElement("gmd:MD_CharacterSetCode").addAttribute(
						"codeList", "http://www.tc211.org/ISO19139/resources/codeList.xml#MD_CharacterSetCode")
						.addAttribute("codeListValue", codeVal);
			}
		} catch (NumberFormatException e) {
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

		super.addExtent(mdDataIdentification, hit, "gmd");
		
		// add supplemental information
		String supplementalInformation = null;
		if (udkClass.equals("5")) {
			// database
			supplementalInformation = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_DATA_DESCRIPTION);
		} else if (udkClass.equals("2")) {
			// literature
			supplementalInformation = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_LITERATURE_DESCRIPTION);
		} else if (udkClass.equals("4")) {
			// project
			supplementalInformation = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_PROJECT_DESCRIPTION);
		}
		if (IngridQueryHelper.hasValue(supplementalInformation)) {
			this.addGCOCharacterString(mdDataIdentification.addElement("gmd:supplementalInformation"), supplementalInformation);
		}
		
	}	
	
}
