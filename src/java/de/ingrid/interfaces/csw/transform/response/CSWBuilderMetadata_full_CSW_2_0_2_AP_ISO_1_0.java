/*
 * Copyright (c) 1997-2007 by wemove GmbH
 */
package de.ingrid.interfaces.csw.transform.response;

import java.util.ArrayList;
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

	public Element build() throws Exception {

		this.setNSPrefix("gmd");

		// define used name spaces
		Namespace gco = new Namespace("gco", "http://www.isotc211.org/2005/gco");
		Namespace gmd = new Namespace("gmd", "http://www.isotc211.org/2005/gmd");
		Namespace srv = new Namespace("srv", "http://www.isotc211.org/2005/srv");
		Namespace gml = new Namespace("gml", "http://www.opengis.net/gml");
		Namespace gts = new Namespace("gts", "http://www.isotc211.org/2005/gts");

		String objectId = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_OBJ_ID);
		String udkClass = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_OBJ_CLASS);
        String typeName = getTypeName();
        if (typeName == null) {
        	return null;
        }

		Element metaData = DocumentFactory.getInstance().createElement("gmd:MD_Metadata",
				"http://www.isotc211.org/2005/gmd");
		metaData.add(gco);
		metaData.add(gmd);
		metaData.add(srv);
		metaData.add(gml);
		metaData.add(gts);

		this.addFileIdentifier(metaData, objectId, this.getNSPrefix());
		this.addLanguage(metaData, hit, this.getNSPrefix());
		this.addCharacterSet(metaData, hit);
		this.addParentIdentifier(metaData, hit);
		this.addHierarchyLevel(metaData.addElement("gmd:hierarchyLevel"), typeName);
		this.addContacts(metaData, hit, this.nsPrefix);
		this.addDateStamp(metaData, hit, this.nsPrefix);
		String metaDataStandardName = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_METADATA_STANDARD_NAME);
		if (IngridQueryHelper.hasValue(metaDataStandardName)) {
			this.addGCOCharacterString(metaData.addElement("gmd:metadataStandardName"), metaDataStandardName);
		}
		
		String metaDataStandardVersion = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_METADATA_STANDARD_VERSION);
		if (IngridQueryHelper.hasValue(metaDataStandardVersion)) {
			this.addGCOCharacterString(metaData.addElement("gmd:metadataStandardVersion"), metaDataStandardVersion);
		}
		
		// spatialRepresentationInfo
		this.addSpatialRepresentationInfo(metaData, hit);

		if (udkClass.equals("1")) {
			this.addIdentificationInfoDataset(metaData, hit);
		} else {
			this.addIdentificationInfoService(metaData, hit);
		}
		
		// contentInfo
		this.addContentInfo(metaData, hit);

		// distributionInfo
		this.addDistributionInfo(metaData, hit);

		// dataQualityInfo
		if (udkClass.equals("1")) {
			this.addDataQualityInfoDataSet(metaData, hit);
			this.addPortrayalCatalogueInfo(metaData, hit);
		} else if (udkClass.equals("3")) {
			this.addDataQualityInfoService(metaData, hit);
		}

		// referenceSystemInfo
		this.addReferenceSystemInfo(metaData, hit);

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
			if (myDate != null && myDate.length() > 0) {
				Element ciDate = portrayalCICitation.addElement("gmd:date").addElement("gmd:CI_Date");
				ciDate.addElement("gmd:date").addElement("gmd:Date").addText(Udk2CswDateFieldParser.instance().parseToDate(myDate));
				ciDate.addElement("gmd:dateType").addElement("gmd:CI_DateTypeCode").addAttribute("codeList",
						"http://www.tc211.org/ISO19139/resources/codeList.xml?CI_DateTypeCode").addAttribute(
						"codeListValue", "creation");
			}
			this.addGCOCharacterString(portrayalCICitation.addElement("gmd:edition"),
					portrayalCatalogInfoVersions[i]);
		}
	}

	private void addDistributionInfo(Element metaData, IngridHit hit) {

		Element mdDistribution = metaData.addElement("gmd:distributionInfo").addElement(
				"gmd:MD_Distribution");

		String[] formatNames = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_AVAIL_FORMAT_NAME);
		String[] formatVersions = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_AVAIL_FORMAT_VERSION);
		String[] formatFileDecompressionTechniques = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_AVAIL_FORMAT_FILE_DECOMPRESSION_TECHNIQUE);
		String[] formatSpecifications = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_AVAIL_FORMAT_SPECIFIKATION);
		for (int i = 0; i < formatNames.length; i++) {
			Element mdFormat = mdDistribution.addElement("gmd:distributionFormat").addElement("gmd:MD_Format");
			// T0110_avail_format.name
			// MD_Metadata/distributionInfo/MD_Distribution/distributor/MD_Distributor/distributorFormat/MD_Format/name
			this.addGCOCharacterString(mdFormat.addElement("gmd:name"), formatNames[i]);
			// T0110_avail_format.version
			// MD_Metadata/distributionInfo/MD_Distribution/distributor/MD_Distributor/distributorFormat/MD_Format/version
			this.addGCOCharacterString(mdFormat.addElement("gmd:version"), formatVersions[i]);
			// T0110_avail_format.specification
			// MD_Metadata/distributionInfo/MD_Distribution/distributor/MD_Distributor/distributorFormat/MD_Format/specification
			if (IngridQueryHelper.hasValue(formatSpecifications[i])) {
				this.addGCOCharacterString(mdFormat.addElement("gmd:specification"), formatSpecifications[i]);
			}
			// T0110_avail_format.file_decompression_technique
			// MD_Metadata/distributionInfo/MD_Distribution/distributor/MD_Distributor/distributorFormat/MD_Format/fileDecompressionTechnique
			if (IngridQueryHelper.hasValue(formatFileDecompressionTechniques[i])) {
				this.addGCOCharacterString(mdFormat.addElement("gmd:fileDecompressionTechnique"),
						formatFileDecompressionTechniques[i]);
			}
		}

		Element mdDistributor = mdDistribution.addElement("gmd:distributor").addElement("gmd:MD_Distributor");
		
		// add dummy distributor
		mdDistributor.addElement("gmd:distributorContact").addElement("gmd:CI_ResponsibleParty").addElement("gmd:role").addElement("gmd:CI_RoleCode")
        .addAttribute("codeList", "http://www.tc211.org/ISO19139/resources/codeList.xml?CI_RoleCode")
        .addAttribute("codeListValue", "distributor");;

        Element mdStandardOrderProcess = mdDistributor.addElement("gmd:distributionOrderProcess").addElement(
				"gmd:MD_StandardOrderProcess");
		
        // T01_object.fees
		// MD_Metadata/distributionInfo/MD_Distribution/distributor/MD_Distributor/distributionOrderProcess/MD_StandardOrderProcess/fees
		this.addGCOCharacterString(mdStandardOrderProcess.addElement("gmd:fees"), IngridQueryHelper
				.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_FEES));
		// T01_object.ordering_instructions
		// MD_Metadata/distributionInfo/MD_Distribution/distributor/MD_Distributor/distributionOrderProcess/MD_StandardOrderProcess/orderingInstructions
		String orderInstructions = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_ORDER_INSTRUCTIONS);
		if (IngridQueryHelper.hasValue(orderInstructions)) {
			this.addGCOCharacterString(mdStandardOrderProcess.addElement("gmd:orderingInstructions"), orderInstructions);
		}

		// MD_Metadata/distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/onLine/CI_OnlineResource/
		String[] urlRefUrlLinks = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_URL_REF_URL_LINK);
		String[] urlRefContent = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_URL_REF_CONTENT);
		String[] urlRefDescr = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_URL_REF_DESCR);
		for (int i = 0; i < urlRefUrlLinks.length; i++) {
			Element ciOnlineResource = mdDistribution.addElement("gmd:transferOptions").addElement(
			"gmd:MD_DigitalTransferOptions").addElement("gmd:onLine").addElement(
					"gmd:CI_OnlineResource");
			// T017_url_ref.url_link
			// MD_Metadata/distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/onLine/CI_OnlineResource/linkage/gmd:URL
			ciOnlineResource.addElement("gmd:linkage").addElement("gmd:URL").addText(urlRefUrlLinks[i]);
			// T017_url_ref.content
			// MD_Metadata/full:distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/onLine/CI_OnlineResource/name
			this.addGCOCharacterString(ciOnlineResource.addElement("gmd:name"), urlRefContent[i]);
			// T017_url_ref.descr
			// MD_Metadata/full:distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/onLine/CI_OnlineResource/description
			this.addGCOCharacterString(ciOnlineResource.addElement("gmd:description"), urlRefDescr[i]);
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
				Element mdDigitalTransferOptions = mdDistribution.addElement("gmd:transferOptions").addElement(
						"gmd:MD_DigitalTransferOptions");
				// T0112_media_option.transfer_size
				// MD_Metadata/distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/transferSize
				if (IngridQueryHelper.hasValue(mediaTransferSizes[i])) {
					this.addGCOReal(mdDigitalTransferOptions.addElement("gmd:transferSize"),
							mediaTransferSizes[i]);
				}

				Element mdMedium = mdDigitalTransferOptions.addElement("gmd:offLine").addElement("gmd:MD_Medium");

				// T0112_media_option.medium_name [Domain-ID Codeliste 520]
				// MD_Metadata/distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/offLine/MD_Medium/name/MD_MediumNameCode@codeListValue
				Long code = Long.valueOf(mediaMediaNames[i]);
				String codeVal = UtilsUDKCodeLists.getCodeListEntryName(new Long(520), code, new Long(94));
				if (codeVal.length() > 0) {
					mdMedium.addElement("gmd:name").addElement("gmd:MD_MediumNameCode").addAttribute("codeList",
							"http://www.tc211.org/ISO19139/resources/codeList.xml?MD_MediumNameCode").addAttribute(
							"codeListValue", codeVal);
				}
				// T0112_media_option.medium_note
				// MD_Metadata/distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/offLine/MD_Medium/mediumNote
				if (IngridQueryHelper.hasValue(mediaMediumNotes[i])) {
					this.addGCOCharacterString(mdMedium.addElement("gmd:mediumNote"), mediaMediumNotes[i]);
				}
			} catch (NumberFormatException e) {
			}
		}
	}

	private void addContentInfo(Element metaData, IngridHit hit) {

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
				if (mdFeatureCatalogueDescription == null) {
					mdFeatureCatalogueDescription = metaData.addElement("gmd:contentInfo").addElement("gmd:MD_FeatureCatalogueDescription");
				}
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
				if (myDate != null && myDate.length() > 0) {
					Element ciDate = ciCitation.addElement("gmd:date").addElement("gmd:CI_Date");
					ciDate.addElement("gmd:date").addElement("gco:Date").addText(Udk2CswDateFieldParser.instance().parseToDate(myDate));
					ciDate.addElement("gmd:dateType").addElement("gmd:CI_DateTypeCode").addAttribute("codeList",
							"http://www.tc211.org/ISO19139/resources/codeList.xml?CI_DateTypeCode").addAttribute(
							"codeListValue", "creation");
				}
				// T011_obj_geo_keyc.edition
				// MD_Metadata/full:contentInfo/MD_FeatureCatalogueDescription/featureCatalogueCitation/CI_Citation/edition/CharacterString
				this.addGCOCharacterString(ciCitation.addElement("gmd:edition"), keycEditions[i]);
			}
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
			codeVal = UtilsUDKCodeLists.getCodeListEntryName(new Long(100), code, new Long(94));
		} catch (NumberFormatException e) {
			codeVal = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_GEO_REFERENCESYSTEM_ID);
		}
		if (IngridQueryHelper.hasValue(codeVal)) {
			this.addGCOCharacterString(metaData.addElement("gmd:referenceSystemInfo").addElement(
					"gmd:MD_ReferenceSystem").addElement("gmd:referenceSystemIdentifier").addElement(
					"gmd:RS_Identifier").addElement("gmd:code"), codeVal);
		}

	}

	private void addSpatialRepresentationInfo(Element metaData, IngridHit hit) {
		Element mdVectorSpatialRepresentation = metaData.addElement("gmd:spatialRepresentationInfo")
				.addElement("gmd:MD_VectorSpatialRepresentation");
		// T011_obj_geo.vector_topology_level ->
		// MD_Metadata/full:spatialRepresentationInfo/MD_VectorSpatialRepresentation/topologyLevel/MD_TopologyLevelCode/@CodeListValue
		String codeStr;
		try {
			Long code = Long.valueOf(IngridQueryHelper.getDetailValueAsString(hit,
					IngridQueryHelper.HIT_KEY_OBJECT_VECTOR_TOPOLOGY_LEVEL));
			codeStr = UtilsUDKCodeLists.getCodeListEntryName(new Long(518), code, new Long(94));
			mdVectorSpatialRepresentation.addElement("gmd:topologyLevel").addElement("gmd:MD_TopologyLevelCode")
			.addAttribute("codeList","http://www.tc211.org/ISO19139/resources/codeList.xml?MD_TopologyLevelCode")
			.addAttribute("codeListValue", codeStr);
		} catch (NumberFormatException e) {}
		
		String[] geometricObjectsType = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_VECTOR_GEOMETRIC_OBJECT_TYPE);
		String[] geometricObjectsCount = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_VECTOR_GEOMETRIC_OBJECT_COUNT);
		for (int i = 0; i < geometricObjectsType.length; i++) {
			Element mdGeometricObjects = mdVectorSpatialRepresentation.addElement("gmd:geometricObjects").addElement(
					"gmd:MD_GeometricObjects");
			// T011_obj_geo_vector.geometric_object_type ->
			// MD_Metadata/spatialRepresentationInfo/MD_SpatialRepresentation/MD_VectorSpatialRepresentation/geometricObjects/geometricObjectType/MD_GeometricObjectTypeCode@codeListValue
			mdGeometricObjects.addElement("gmd:geometricObjectType").addElement("gmd:MD_GeometricObjectTypeCode")
					.addAttribute("codeList",
							"http://www.tc211.org/ISO19139/resources/codeList.xml?MD_GeometricObjectTypeCode")
					.addAttribute("codeListValue", geometricObjectsType[i]);
			// T011_obj_geo_vector.geometric_object_count ->
			// MD_Metadata/full:spatialRepresentationInfo/MD_VectorSpatialRepresentation/geometricObjects/MD_GeometricObjects/geometricObjectCount
			this.addGCOInteger(mdGeometricObjects.addElement("gmd:geometricObjectCount"),
					geometricObjectsCount[i]);
		}

	}

	private void addDataQualityInfoService(Element metaData, IngridHit hit) {
		Element dqQualityInfo = metaData.addElement("gmd:dataQualityInfo").addElement("gmd:DQ_DataQuality");

		
		Element dqScopeType = dqQualityInfo.addElement("gmd:scope").addElement("gmd:DQ_Scope");
		// add level
		dqScopeType.addElement("gmd:level").addElement("gmd:MD_ScopeCode").addAttribute("codeListValue", getTypeName()).addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#MD_ScopeCode");
		dqScopeType.addElement("gmd:levelDescription").addElement("gmd:MD_ScopeDescription").addElement("gmd:featureInstances");
		
		// T011_obj_geo.special_base ->
		// MD_Metadata/dataQualityInfo/udk:DQ_DataQuality/lineage/udk:LI_Lineage/statement
		Element liLineage = dqQualityInfo.addElement("gmd:lineage").addElement("gmd:LI_Lineage");
		// (dataset) T011_obj_serv.history ->
		// MD_Metadata/dataQualityInfo/udk:DQ_DataQuality/lineage/LI_Lineage/processStep/LI_ProcessStep/description
		String processStepDescription = IngridQueryHelper.getDetailValueAsString(hit,IngridQueryHelper.HIT_KEY_OBJECT_SERV_HISTORY);
		if (IngridQueryHelper.hasValue(processStepDescription)) {
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
			liLineage = dqQualityInfo.addElement("gmd:lineage").addElement("gmd:LI_Lineage");
			this.addGCOCharacterString(liLineage.addElement("gmd:source").addElement("gmd:LI_Source").addElement(
					"gmd:description"), liSourceDescription);
		}

	}

	private void addDataQualityInfoDataSet(Element metaData, IngridHit hit) {
		Element dqQualityInfo = metaData.addElement("gmd:dataQualityInfo").addElement("gmd:DQ_DataQuality");
		
		// add scope
		dqQualityInfo.addElement("gmd:scope").addElement("gmd:DQ_Scope").addElement("gmd:level").addElement("gmd:MD_ScopeCode").addAttribute("codeListValue", getTypeName()).addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#MD_ScopeCode");

		// T011_obj_geo.rec_grade ->
		// MD_Metadata/dataQualityInfo/udk:DQ_DataQuality/report/DQ_CompletenessCommission/DQ_QuantitativeResult/value/Record
		String completenessComission = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_GEO_REC_GRADE);
		if (IngridQueryHelper.hasValue(completenessComission)) {
			Element completenessCommission = dqQualityInfo.addElement("gmd:report").addElement("gmd:DQ_CompletenessCommission");
			this.addGCOCharacterString(completenessCommission.addElement("gmd:measureDescription"), "completeness");
			Element dqQuantitativeResult = completenessCommission.addElement("gmd:result").addElement("gmd:DQ_QuantitativeResult");
			dqQuantitativeResult.addElement("gmd:value").addElement("gmd:Record").addText(completenessComission);
			Element unitDefinition = dqQuantitativeResult.addElement("gmd:valueUnit").addElement("gml:UnitDefinition").addAttribute("gml:id", "unitDefinition_ID_" + UUID.randomUUID());
			unitDefinition.addElement("gml:identifier").addAttribute("codeSpace", "");
			unitDefinition.addElement("gml:name").addText("percent");
			unitDefinition.addElement("gml:quantityType").addText("completeness");
			unitDefinition.addElement("gml:catalogSymbol").addText("%");
		}

		// T011_obj_geo.pos_accuracy_vertical ->
		// MD_Metadata/dataQualityInfo/DQ_DataQuality/report/DQ_RelativeInternalPositionalAccuracy[measureDescription/CharacterString='vertical']/DQ_QuantitativeResult.value

		String verticalAccuracy = IngridQueryHelper.getDetailValueAsString(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_GEO_POS_ACCURACY_VERTICAL);
		if (IngridQueryHelper.hasValue(verticalAccuracy)) {
			Element dqRelativeInternalPositionalAccuracy = dqQualityInfo.addElement("gmd:report")
					.addElement("gmd:DQ_RelativeInternalPositionalAccuracy");
			this.addGCOCharacterString(dqRelativeInternalPositionalAccuracy.addElement("gmd:measureDescription"),
					"vertical");
			
			dqRelativeInternalPositionalAccuracy.addElement("gmd:DQ_QuantitativeResult").addElement("gmd:value")
					.addElement("gmd:Record").addText(verticalAccuracy);
		}

		// T011_obj_geo.rec_exact ->
		// MD_Metadata/dataQualityInfo/udk:DQ_DataQuality/report/DQ_RelativeInternalPositionalAccuracy[measureDescription/CharacterString='geographic']/DQ_QuantitativeResult/value/Record
		String geographicAccuracy = IngridQueryHelper.getDetailValueAsString(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_GEO_REC_EXACT);
		if (IngridQueryHelper.hasValue(geographicAccuracy)) {
			Element dqRelativeInternalPositionalAccuracy = dqQualityInfo.addElement("gmd:report")
					.addElement("gmd:DQ_RelativeInternalPositionalAccuracy");
			this.addGCOCharacterString(dqRelativeInternalPositionalAccuracy.addElement("gmd:measureDescription"),
					"geographic");
			dqRelativeInternalPositionalAccuracy.addElement("gmd:DQ_QuantitativeResult").addElement("gmd:value")
					.addElement("gmd:Record").addText(geographicAccuracy);
		}		
		
		// T011_obj_geo.special_base ->
		// MD_Metadata/dataQualityInfo/udk:DQ_DataQuality/lineage/udk:LI_Lineage/statement
		Element liLineage = dqQualityInfo.addElement("gmd:lineage").addElement("gmd:LI_Lineage");
		String statement = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_GEO_SPECIAL_BASE);
		if (IngridQueryHelper.hasValue(statement)) {
			this.addGCOCharacterString(liLineage.addElement("gmd:statement"), IngridQueryHelper.getDetailValueAsString(
					hit, IngridQueryHelper.HIT_KEY_OBJECT_GEO_SPECIAL_BASE));
		}
		// (dataset) T011_obj_geo.method ->
		// MD_Metadata/dataQualityInfo/udk:DQ_DataQuality/lineage/LI_Lineage/processStep/LI_ProcessStep/description
		String processStepDescription = IngridQueryHelper.getDetailValueAsString(hit,IngridQueryHelper.HIT_KEY_OBJECT_GEO_METHOD);
		if (IngridQueryHelper.hasValue(processStepDescription)) {
			this.addGCOCharacterString(liLineage.addElement("gmd:processStep").addElement("gmd:LI_ProcessStep")
					.addElement("gmd:description"), processStepDescription);
		}
		// (dataset) T011_obj_geo.data_base ->
		// MD_Metadata/dataQualityInfo/DQ_DataQuality/lineage/LI_Lineage/source/LI_Source/description
		String liSourceDescription = IngridQueryHelper.getDetailValueAsString(hit,IngridQueryHelper.HIT_KEY_OBJECT_GEO_DATA_BASE);
		if (IngridQueryHelper.hasValue(liSourceDescription)) {
			this.addGCOCharacterString(liLineage.addElement("gmd:source").addElement("gmd:LI_Source").addElement(
					"gmd:description"), liSourceDescription);
		}



	}

	private void addCitation(Element parent, IngridHit hit) {
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

		addGenericMetadataIndentification(svServiceIdentification, hit);

		this.addGCOCharacterString(svServiceIdentification.addElement("srv:serviceType"), IngridQueryHelper
				.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERVICE_TYPE));

		String[] serviceTypeVersions = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_SERVICE_TYPE_VERSION);
		for (int i = 0; i < serviceTypeVersions.length; i++) {
			this.addGCOCharacterString(svServiceIdentification.addElement("srv:serviceTypeVersion"),
					serviceTypeVersions[i]);
		}

		super.addOperationMetadata(svServiceIdentification, hit);
		
		List references = IngridQueryHelper.getReferenceIdentifiers(hit);
		for (int i=0; i< references.size(); i++) {
			// srv:operatesOn
			svServiceIdentification.addElement("srv:operatesOn").addElement("gmd:Reference").addAttribute(
					"uuidref", (String)references.get(i));
		}


		super.addExtent(svServiceIdentification, hit, "srv");

		String objReferenceSpecialRef = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_OBJECT_SPECIAL_REF);
        if (objReferenceSpecialRef != null && objReferenceSpecialRef.equals("3345")) {
			svServiceIdentification.addElement("srv:couplingType").addElement("srv:CSW_CouplingType")
			.addAttribute("codeList", "http://opengis.org/codelistRegistry?CSW_CouplingType").addAttribute(
					"codeListValue", "tight");
		}

	}

	private void addGenericMetadataIndentification(Element parent, IngridHit hit) throws Exception {
		// add citation construct
		this.addCitation(parent, hit);

        // add abstract
        this.addGCOCharacterString(parent.addElement("gmd:abstract"), IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_DESCR));

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
		this.addGCOCharacterString(parent.addElement("gmd:purpose"), purpose);

		// add status
		try {
			Long code = Long.valueOf(IngridQueryHelper.getDetailValueAsString(hit,
					IngridQueryHelper.HIT_KEY_OBJECT_TIME_STATUS));
			String codeVal = UtilsUDKCodeLists.getCodeListEntryName(new Long(518), code, new Long(94));
			if (codeVal.length() > 0) {
				parent.addElement("gmd:status").addElement("gmd:MD_ProgressCode").addAttribute("codeList",
						"http://www.tc211.org/ISO19139/resources/codeList.xml?MD_ProgressCode").addAttribute(
						"codeListValue", codeVal);
			}
		} catch (NumberFormatException e) {
		}

		addPointOfContacts(parent, hit, "gmd");
		
		// resource maintenance
		try {
			Long code = Long.valueOf(IngridQueryHelper.getDetailValueAsString(hit,
					IngridQueryHelper.HIT_KEY_OBJECT_TIME_PERIOD));
			String codeVal = UtilsUDKCodeLists.getCodeListEntryName(new Long(518), code, new Long(94));
			if (codeVal.length() > 0) {
				Element mdMaintenanceInformation = parent.addElement("gmd:resourceMaintenance").addElement(
				"gmd:MD_MaintenanceInformation");
				mdMaintenanceInformation.addElement("gmd:maintenanceAndUpdateFrequency").addElement(
						"gmd:MD_MaintenanceFrequencyCode").addAttribute("codeList",
						"http://www.tc211.org/ISO19139/resources/codeList.xml?MD_MaintenanceFrequencyCode")
						.addAttribute("codeListValue", codeVal);
				// userDefinedMaintenanceFrequency
				String timeInterval = IngridQueryHelper.getDetailValueAsString(hit,
						IngridQueryHelper.HIT_KEY_OBJECT_TIME_INTERVAL);
				if (timeInterval.length() > 0) {
					String timeAlle = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_TIME_ALLE);
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
		
		
		// descriptiveKeywords
		String[] keywords = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_SEARCH_SEARCHTERM);
		String[] keywordTypes = IngridQueryHelper.getDetailValueAsArray(hit,
				IngridQueryHelper.HIT_KEY_OBJECT_SEARCH_TYPE);
		ArrayList thesaurusKeywords = new ArrayList();
		ArrayList freeKeywords = new ArrayList();
		ArrayList inspireKeywords = new ArrayList();
		ArrayList gemetKeywords = new ArrayList();
		for (int i = 0; i < keywords.length; i++) {
			if (keywordTypes[i].equals("2") || keywordTypes[i].equals("T")) {
				thesaurusKeywords.add(keywords[i]);
			} else if (keywordTypes[i].equals("1") || keywordTypes[i].equals("F")) {
				freeKeywords.add(keywords[i]);
			} else if (keywordTypes[i].equals("G")) {
				gemetKeywords.add(keywords[i]);
			} else if (keywordTypes[i].equals("I")) {
				inspireKeywords.add(keywords[i]);
			}
		}
		if (thesaurusKeywords.size() > 0) {
			Element keywordType = parent.addElement("gmd:descriptiveKeywords").addElement("gmd:MD_Keywords");
			for (int i = 0; i < thesaurusKeywords.size(); i++) {
				this.addGCOCharacterString(keywordType.addElement("gmd:keyword"), (String) thesaurusKeywords
								.get(i));
			}
			keywordType.addElement("gmd:type").addElement("gmd:MD_KeywordTypeCode").addAttribute("codeList",
			"http://www.tc211.org/ISO19139/resources/codeList.xml?MD_KeywordTypeCode").addAttribute(
			"codeListValue", "theme");
			Element thesaurusCitation = keywordType.addElement("gmd:thesaurusName").addElement("gmd:CI_Citation");
			this.addGCOCharacterString(thesaurusCitation.addElement("gmd:title"), "UMTHES Thesaurus");
			Element thesaurusCitationDate = thesaurusCitation.addElement("gmd:date").addElement("gmd:CI_Date");
			thesaurusCitationDate.addElement("gmd:date").addElement("gco:Date").addText("2009-01-15");
			thesaurusCitationDate.addElement("gmd:dateType").addElement("gmd:CI_DateTypeCode")
				.addAttribute("codeListValue", "publication")
				.addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#CI_DateTypeCode");
		}
		if (gemetKeywords.size() > 0) {
			Element keywordType = parent.addElement("gmd:descriptiveKeywords").addElement("gmd:MD_Keywords");
			for (int i = 0; i < thesaurusKeywords.size(); i++) {
				this.addGCOCharacterString(keywordType.addElement("gmd:keyword"), (String) thesaurusKeywords
								.get(i));
			}
			keywordType.addElement("gmd:type").addElement("gmd:MD_KeywordTypeCode").addAttribute("codeList",
			"http://www.tc211.org/ISO19139/resources/codeList.xml?MD_KeywordTypeCode").addAttribute(
			"codeListValue", "theme");
			Element thesaurusCitation = keywordType.addElement("gmd:thesaurusName").addElement("gmd:CI_Citation");
			this.addGCOCharacterString(thesaurusCitation.addElement("gmd:title"), "GEMET - Concepts, version 2.1");
			Element thesaurusCitationDate = thesaurusCitation.addElement("gmd:date").addElement("gmd:CI_Date");
			thesaurusCitationDate.addElement("gmd:date").addElement("gco:Date").addText("2008-06-13");
			thesaurusCitationDate.addElement("gmd:dateType").addElement("gmd:CI_DateTypeCode")
				.addAttribute("codeListValue", "publication")
				.addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#CI_DateTypeCode");
		}		
		if (inspireKeywords.size() > 0) {
			Element keywordType = parent.addElement("gmd:descriptiveKeywords").addElement("gmd:MD_Keywords");
			for (int i = 0; i < thesaurusKeywords.size(); i++) {
				this.addGCOCharacterString(keywordType.addElement("gmd:keyword"), (String) thesaurusKeywords
								.get(i));
			}
			keywordType.addElement("gmd:type").addElement("gmd:MD_KeywordTypeCode").addAttribute("codeList",
			"http://www.tc211.org/ISO19139/resources/codeList.xml?MD_KeywordTypeCode").addAttribute(
			"codeListValue", "theme");
			Element thesaurusCitation = keywordType.addElement("gmd:thesaurusName").addElement("gmd:CI_Citation");
			this.addGCOCharacterString(thesaurusCitation.addElement("gmd:title"), "GEMET - INSPIRE themes, version 1.0");
			Element thesaurusCitationDate = thesaurusCitation.addElement("gmd:date").addElement("gmd:CI_Date");
			thesaurusCitationDate.addElement("gmd:date").addElement("gco:Date").addText("2008-06-01");
			thesaurusCitationDate.addElement("gmd:dateType").addElement("gmd:CI_DateTypeCode")
				.addAttribute("codeListValue", "publication")
				.addAttribute("codeList", "http://www.isotc211.org/2005/resources/codeList.xml#CI_DateTypeCode");
		}		
		if (freeKeywords.size() > 0) {
			Element keywordType = parent.addElement("gmd:descriptiveKeywords").addElement("gmd:MD_Keywords");
			for (int i = 0; i < freeKeywords.size(); i++) {
				this.addGCOCharacterString(keywordType.addElement("gmd:keyword"), (String) freeKeywords.get(i));
			}
		}		
		
		// add resourceSpecificUsage
		String usage = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_DATASET_USAGE);
		if (IngridQueryHelper.hasValue(usage)) {
			Element usageType = parent.addElement("gmd:resourceSpecificUsage").addElement("gmd:MD_Usage");
			this.addGCOCharacterString(usageType.addElement("gmd:specificUsage"), usage);
			
			usageType.addElement("gmd:userContactInfo").addElement("gmd:CI_ResponsibleParty").addElement("gmd:role").addElement("gmd:CI_RoleCode")
	        .addAttribute("codeList", "http://www.tc211.org/ISO19139/resources/codeList.xml?CI_RoleCode")
	        .addAttribute("codeListValue", "pointOfContact");
		}


		// add resourceConstraint
		String resourceConstraints = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_AVAIL_ACCESS_NOTE);
        if (IngridQueryHelper.hasValue(resourceConstraints)) {
        	this.addGCOCharacterString(parent.addElement("gmd:resourceConstraints").addElement("gmd:MD_Constraints").addElement("gmd:useLimitation"), resourceConstraints);
        }


		

	}

	private void addIdentificationInfoDataset(Element metaData, IngridHit hit) throws Exception {
		Element mdDataIdentification = metaData.addElement("gmd:identificationInfo").addElement(
				"gmd:MD_DataIdentification");

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
				String codeVal = UtilsUDKCodeLists.getCodeListEntryName(new Long(526), code, new Long(94));
				if (codeVal.length() > 0) {
					mdDataIdentification.addElement("gmd:spatialRepresentationType").addElement(
							"gmd:MD_SpatialRepresentationTypeCode").addAttribute("codeList",
							"http://www.tc211.org/ISO19115/resources/codeList.xml?MD_SpatialRepresentationTypeCode")
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

			if (IngridQueryHelper.hasValue(spacialResolutionGround[i])) {
				Element distance = mdDataIdentification.addElement("gmd:spatialResolution").addElement(
				"gmd:MD_Resolution").addElement("gmd:distance").addElement("gco:Distance").addAttribute("uom", "meter").addText(spacialResolutionGround[i]);
			}

			if (IngridQueryHelper.hasValue(spacialResolutionScan[i])) {
				mdDataIdentification.addElement("gmd:spatialResolution").addElement(
				"gmd:MD_Resolution").addElement("gmd:distance").addElement("gco:Distance").addAttribute("uom", "dpi").addText(spacialResolutionScan[i]);
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
				String codeVal = UtilsUDKCodeLists.getCodeListEntryName(new Long(527), code, new Long(94));
				if (codeVal.length() > 0) {
					mdDataIdentification.addElement("gmd:topicCategory").addElement("gmd:MD_TopicCategoryCode")
							.addText(codeVal);
				}
			} catch (NumberFormatException e) {
			}
		}

		super.addExtent(mdDataIdentification, hit, "gmd");
	}	
	
}
