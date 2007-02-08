/*
 * Copyright (c) 1997-2007 by wemove GmbH
 */
package de.ingrid.interfaces.csw.transform.response;

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Namespace;

import de.ingrid.utils.IngridHit;
import de.ingrid.utils.udk.UtilsDate;
import de.ingrid.utils.udk.UtilsUDKCodeLists;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class CSWBuilderMetadata_full_DE_1_0_1 extends CSWBuilderMetadataCommon {

    private static Log log = LogFactory.getLog(CSWBuilderMetadata_full_DE_1_0_1.class);

    public Element build() throws Exception {

        this.setNSPrefix("iso19115full");
        
        // define used name spaces
        Namespace smXML = new Namespace("smXML", "http://metadata.dgiwg.org/smXML");
        Namespace iso19115full = new Namespace("iso19115full", "http://schemas.opengis.net/iso19115full");
        Namespace iso19119 = new Namespace("iso19119", "http://schemas.opengis.net/iso19119");
        Namespace gml = new Namespace("gml", "http://www.opengis.net/gml");

        String objectId = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_OBJ_ID);
        String udkClass = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_OBJ_CLASS);
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

        Element metaData = DocumentFactory.getInstance().createElement("iso19115full:MD_Metadata",
        "http://schemas.opengis.net/iso19115full");
        metaData.add(iso19115full);
        metaData.add(smXML);
        metaData.add(iso19119);
        metaData.add(gml);

        this.addFileIdentifier(metaData, objectId, this.getNSPrefix());
        this.addLanguage(metaData, hit, this.getNSPrefix());
        this.addCharacterSet(metaData, hit);
        this.addParentIdentifier(metaData, hit);
        this.addHierarchyLevel(metaData.addElement("iso19115full:hierarchyLevel"), typeName);
        this.addContact(metaData, hit, this.nsPrefix);
        this.addDateStamp(metaData, hit, this.nsPrefix);
        this.addSMXMLCharacterString(metaData.addElement("iso19115full:metadataStandardName"), IngridQueryHelper
                .getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_METADATA_STANDARD_NAME));
        this.addSMXMLCharacterString(metaData.addElement("iso19115full:metadataStandardVersion"), IngridQueryHelper
                .getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_METADATA_STANDARD_VERSION));
        if (typeName.equals("dataset")) {
            this.addIdentificationInfoDataset(metaData, hit);
        } else {
            this.addIdentificationInfoService(metaData, hit);
        }
        // dataQualityInfo
        if (udkClass.equals("1")) {
            this.addDataQualityInfoDataSet(metaData, hit);
        } else if (udkClass.equals("3")) {
            this.addDataQualityInfoService(metaData, hit);
        }
        
        // spatialRepresentationInfo
        this.addSpatialRepresentationInfo(metaData, hit);
        
        // referenceSystemInfo
        this.addReferenceSystemInfo(metaData, hit);
        
        
        // contentInfo
        this.addContentInfo(metaData, hit);
        
        // distributionInfo
        this.addDistributionInfo(metaData, hit);

        return metaData;

    }

    private void addDistributionInfo(Element metaData, IngridHit hit) {

        Element mdDistribution = metaData.addElement("iso19115full:distributionInfo").addElement("smXML:MD_Distribution");
        Element mdDistributor = mdDistribution.addElement("smXML:distributor").addElement("smXML:MD_Distributor");
        
        String[] formatNames = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_AVAIL_FORMAT_NAME);
        String[] formatVersions = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_AVAIL_FORMAT_VERSION);
        String[] formatFileDecompressionTechniques = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_AVAIL_FORMAT_FILE_DECOMPRESSION_TECHNIQUE);
        String[] formatSpecifications = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_AVAIL_FORMAT_SPECIFIKATION);
        for (int i=0; i<formatNames.length; i++) {
            Element mdFormat = mdDistributor.addElement("smXML:distributorFormat").addElement("smXML:MD_Format");
            // T0110_avail_format.name MD_Metadata/distributionInfo/MD_Distribution/distributor/MD_Distributor/distributorFormat/MD_Format/name
            this.addSMXMLCharacterString(mdFormat.addElement("smXML:name"), formatNames[i]);
            // T0110_avail_format.version MD_Metadata/distributionInfo/MD_Distribution/distributor/MD_Distributor/distributorFormat/MD_Format/version
            this.addSMXMLCharacterString(mdFormat.addElement("smXML:version"), formatVersions[i]);
            // T0110_avail_format.specification MD_Metadata/distributionInfo/MD_Distribution/distributor/MD_Distributor/distributorFormat/MD_Format/specification
            this.addSMXMLCharacterString(mdFormat.addElement("smXML:specification"), formatSpecifications[i]);
            // T0110_avail_format.file_decompression_technique MD_Metadata/distributionInfo/MD_Distribution/distributor/MD_Distributor/distributorFormat/MD_Format/fileDecompressionTechnique
            this.addSMXMLCharacterString(mdFormat.addElement("smXML:fileDecompressionTechnique"), formatFileDecompressionTechniques[i]);
        }
        
        Element mdStandardOrderProcess = mdDistributor.addElement("smXML:distributionOrderProcess").addElement("smXML:MD_StandardOrderProcess");
        // T01_object.fees MD_Metadata/distributionInfo/MD_Distribution/distributor/MD_Distributor/distributionOrderProcess/MD_StandardOrderProcess/fees
        this.addSMXMLCharacterString(mdStandardOrderProcess.addElement("smXML:fees"), IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_FEES));
        // T01_object.ordering_instructions MD_Metadata/distributionInfo/MD_Distribution/distributor/MD_Distributor/distributionOrderProcess/MD_StandardOrderProcess/orderingInstructions
        this.addSMXMLCharacterString(mdStandardOrderProcess.addElement("smXML:orderingInstructions"), IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_ORDER_INSTRUCTIONS));


        Element mdDigitalTransferOptions = mdDistribution.addElement("smXML:transferOptions").addElement("smXML:MD_DigitalTransferOptions");
        
        // MD_Metadata/distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/onLine/CI_OnlineResource/
        String[] urlRefUrlLinks = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_URL_REF_URL_LINK);
        String[] urlRefContent = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_URL_REF_CONTENT);
        String[] urlRefDescr = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_URL_REF_DESCR);
        for (int i=0; i<urlRefUrlLinks.length; i++) {
            Element ciOnlineResource = mdDigitalTransferOptions.addElement("smXML:online").addElement("smXML:CI_OnlineResource");
            // T017_url_ref.url_link MD_Metadata/distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/onLine/CI_OnlineResource/linkage/smXML:URL
            ciOnlineResource.addElement("smXML:linkage").addElement("smXML:URL").addText(urlRefUrlLinks[i]);
            // T017_url_ref.content MD_Metadata/full:distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/onLine/CI_OnlineResource/name
            this.addSMXMLCharacterString(ciOnlineResource.addElement("smXML:name"), urlRefContent[i]);
            // T017_url_ref.descr MD_Metadata/full:distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/onLine/CI_OnlineResource/description
            this.addSMXMLCharacterString(ciOnlineResource.addElement("smXML:description"), urlRefDescr[i]);
            // T017_url_ref.special_ref MD_Metadata/full:distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/onLine/CI_OnlineResource/function/CI_OnLineFunctionCode@codeListValue = "information"
            ciOnlineResource.addElement("smXML:function").addElement("smXML:CI_OnLineFunctionCode")
                .addAttribute("codeList", "http://www.tc211.org/ISO19139/resources/codeList.xml?CI_OnLineFunctionCode")
                .addAttribute("codeListValue", "information");
        }

        // MD_Metadata/distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/offLine/
        String[] mediaMediaNames = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_MEDIA_OPTION_MEDIUM_NAME);
        String[] mediaTransferSizes = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_MEDIA_OPTION_TANSFER_SIZE);
        String[] mediaMediumNotes = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_MEDIA_OPTION_MEDIUM_NOTE);
        for (int i=0; i<mediaMediaNames.length; i++) {
            try {
                // add another transferOptions branch, because transfersize does not fit into offline branch
                if (i>0) {
                    mdDigitalTransferOptions = mdDistribution.addElement("smXML:transferOptions").addElement("smXML:MD_DigitalTransferOptions");
                }
                Element mdMedium = mdDigitalTransferOptions.addElement("smXML:offLine").addElement("smXML:MD_Medium");
                
                // T0112_media_option.medium_name [Domain-ID Codeliste 520] MD_Metadata/distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/offLine/MD_Medium/name/MD_MediumNameCode@codeListValue
                Long code = Long.valueOf(mediaMediaNames[i]);
                String codeVal = UtilsUDKCodeLists.getCodeListEntryName(new Long(520), code, new Long(94));
                if (codeVal.length() > 0) {
                    mdMedium.addElement("smXML:name").addElement("smXML:MD_MediumNameCode")
                    .addAttribute("codeList", "http://www.tc211.org/ISO19139/resources/codeList.xml?MD_MediumNameCode")
                    .addAttribute("codeListValue", codeVal);
                }
                // T0112_media_option.medium_note MD_Metadata/distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/offLine/MD_Medium/mediumNote
                this.addSMXMLCharacterString(mdMedium.addElement("smXML:mediumNote"), mediaMediumNotes[i]);
                // T0112_media_option.transfer_size MD_Metadata/distributionInfo/MD_Distribution/transferOptions/MD_DigitalTransferOptions/transferSize
                this.addSMXMLCharacterString(mdDigitalTransferOptions.addElement("smXML:transferSize"), mediaTransferSizes[i]);
            } catch (NumberFormatException e) {}
        }
    }

    private void addContentInfo(Element metaData, IngridHit hit) {
        Element mdFeatureCatalogueDescription = metaData.addElement("iso19115full:contentInfo").addElement("MD_FeatureCatalogueDescription");
        // T011_obj_geo.keyc_incl_w_dataset Wert = 0 MD_Metadata/full:contentInfo/MD_FeatureCatalogueDescription/includedWithDataset
        String keycInclWDataset = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_KEYC_INCL_W_DATASET);
        this.addSMXMLBoolean(mdFeatureCatalogueDescription.addElement("smXML:includedWithDataset"), (keycInclWDataset != null && keycInclWDataset.equals("1")));
        
        String[] keycTitles = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_KEYC_SUBJECT_CAT);
        String[] keycDates = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_KEYC_KEY_DATE);
        String[] keycEditions = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_KEYC_EDITION);
        for (int i=0; i<keycTitles.length; i++) {
            Element ciCitation = mdFeatureCatalogueDescription.addElement("smXML:featureCatalogueCitation").addElement("smXML:CI_Citation");
            // T011_obj_geo_keyc.subject_cat MD_Metadata/full:contentInfo/MD_FeatureCatalogueDescription/featureCatalogueCitation/CI_Citation/title/CharacterString
            this.addSMXMLCharacterString(ciCitation.addElement("smXML:title"), keycTitles[i]);
            // T011_obj_geo_keyc.key_date MD_Metadata/full:contentInfo/MD_FeatureCatalogueDescription/featureCatalogueCitation/CI_Citation/date/CI_Date/date/Date UND .../CI_Date/date/DateType/CI_DateTypeCode/....
            String myDate = keycDates[i];
            Date d = UtilsDate.parseDateString(myDate);
            if (d != null) {
                myDate = DATE_TIME_FORMAT.format(d);
                Element ciDate = ciCitation.addElement("smXML:date").addElement("smXML:CI_Date");
                ciDate.addElement("smXML:Date").addText(myDate);
                ciDate.addElement("smXML:CI_DateTypeCode").addAttribute("codeList",
                        "http://www.tc211.org/ISO19139/resources/codeList.xml?CI_DateTypeCode").addAttribute(
                        "codeListValue", "creation");
            }
            // T011_obj_geo_keyc.edition MD_Metadata/full:contentInfo/MD_FeatureCatalogueDescription/featureCatalogueCitation/CI_Citation/edition/CharacterString
            this.addSMXMLCharacterString(ciCitation.addElement("smXML:edition"), keycEditions[i]);
            // T011_obj_geo_supplinfo.feature_type  MD_Metadata/contentInfo/MD_FeatureCatalogueDescription[featureCatalogueCitation/CI_Citation/title]/featureTypes/LocalName
            // kann im CSW schema nicht zugeordnet werden
        }
        
    }

    private void addReferenceSystemInfo(Element metaData, IngridHit hit) {
        
        // T011_obj_geo.referencesystem_id == [Domain-ID Codelist 100] MD_Metadata/full:referenceSystemInfo/MD_ReferenceSystem/referenceSystemIdentifier/RS_Identifier/code/CharacterString
        Long code;
        try {
            code = Long.valueOf(IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_GEO_REFERENCESYSTEM_ID));
            String codeVal = UtilsUDKCodeLists.getCodeListEntryName(new Long(100), code, new Long(94));
            if (codeVal.length() > 0) {
                this.addSMXMLCharacterString(metaData.addElement("iso19115full:referenceSystemInfo").addElement("smXML:MD_ReferenceSystem").addElement("smXML:referenceSystemIdentifier")
                    .addElement("smXML:RS_Identifier").addElement("smXML:code"), codeVal);
            }
        } catch (NumberFormatException e) {}
        
    }

    private void addSpatialRepresentationInfo(Element metaData, IngridHit hit) {
        Element mdVectorSpatialRepresentation = metaData.addElement("iso19115full:spatialRepresentationInfo").addElement("smXML:MD_VectorSpatialRepresentation");
        // T011_obj_geo.vector_topology_level -> MD_Metadata/full:spatialRepresentationInfo/MD_VectorSpatialRepresentation/topologyLevel/MD_TopologyLevelCode/CharacterString
        this.addSMXMLCharacterString(mdVectorSpatialRepresentation.addElement("smXML:topologyLevel").addElement("smXML:MD_TopologyLevelCode"), IngridQueryHelper
                .getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_VECTOR_TOPOLOGY_LEVEL));
        String[] geometricObjectsType = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_VECTOR_GEOMETRIC_OBJECT_TYPE);
        String[] geometricObjectsCount = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_VECTOR_GEOMETRIC_OBJECT_COUNT);
        for (int i=0; i< geometricObjectsType.length; i++) {
            Element mdGeometricObjects = mdVectorSpatialRepresentation.addElement("smXML:geometricObjects").addElement("smXML:MD_GeometricObjects");
            // T011_obj_geo_vector.geometric_object_type -> MD_Metadata/spatialRepresentationInfo/MD_SpatialRepresentation/MD_VectorSpatialRepresentation/geometricObjects/geometricObjectType/MD_GeometricObjectTypeCode@codeListValue
            mdGeometricObjects.addElement("smXML:geometricObjectType").addElement("smXML:MD_GeometricObjectTypeCode")
                .addAttribute("codeList", "http://www.tc211.org/ISO19139/resources/codeList.xml?MD_GeometricObjectTypeCode")
                .addAttribute("codeListValue", geometricObjectsType[i]);
            // T011_obj_geo_vector.geometric_object_count -> MD_Metadata/full:spatialRepresentationInfo/MD_VectorSpatialRepresentation/geometricObjects/MD_GeometricObjects/geometricObjectCount
            this.addSMXMLCharacterString(mdGeometricObjects.addElement("smXML:geometricObjectCount"), geometricObjectsCount[i]);
        }
        
        
    }

    private void addDataQualityInfoService(Element metaData, IngridHit hit) {
        Element dqQualityInfo = metaData.addElement("iso19115full:dataQualityInfo").addElement("smXML:DQ_DataQuality");
        // T011_obj_geo.special_base -> MD_Metadata/dataQualityInfo/udk:DQ_DataQuality/lineage/udk:LI_Lineage/statement
        Element liLineage = dqQualityInfo.addElement("smXML:lineage").addElement("LI_Lineage");
        // (dataset) T011_obj_serv.base -> MD_Metadata/dataQualityInfo/DQ_DataQuality/lineage/LI_Lineage/source/LI_Source/description
        this.addSMXMLCharacterString(liLineage.addElement("smXML:source").addElement("smXML:LI_Source").addElement("smXML:description"), IngridQueryHelper
                .getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_BASE));
        // (dataset) T011_obj_serv.history -> MD_Metadata/dataQualityInfo/udk:DQ_DataQuality/lineage/LI_Lineage/processStep/LI_ProcessStep/description
        this.addSMXMLCharacterString(liLineage.addElement("smXML:processStep").addElement("smXML:LI_ProcessStep").addElement("smXML:description"), IngridQueryHelper
                .getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_HISTORY));
        
    }

    private void addDataQualityInfoDataSet(Element metaData, IngridHit hit) {
        Element dqQualityInfo = metaData.addElement("iso19115full:dataQualityInfo").addElement("smXML:DQ_DataQuality");
        // T011_obj_geo.special_base -> MD_Metadata/dataQualityInfo/udk:DQ_DataQuality/lineage/udk:LI_Lineage/statement
        Element liLineage = dqQualityInfo.addElement("smXML:lineage").addElement("LI_Lineage");
        this.addSMXMLCharacterString(liLineage.addElement("smXML:statement"), IngridQueryHelper
                .getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_GEO_SPECIAL_BASE));
        // (dataset) T011_obj_geo.data_base -> MD_Metadata/dataQualityInfo/DQ_DataQuality/lineage/LI_Lineage/source/LI_Source/description
        this.addSMXMLCharacterString(liLineage.addElement("smXML:source").addElement("smXML:LI_Source").addElement("smXML:description"), IngridQueryHelper
                .getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_GEO_DATA_BASE));
        // (dataset) T011_obj_geo.method -> MD_Metadata/dataQualityInfo/udk:DQ_DataQuality/lineage/LI_Lineage/processStep/LI_ProcessStep/description
        this.addSMXMLCharacterString(liLineage.addElement("smXML:processStep").addElement("smXML:LI_ProcessStep").addElement("smXML:description"), IngridQueryHelper
                .getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_GEO_METHOD));
        
        // kann in CSW schema nicht gefunden werden.
        
        // T011_obj_geo.rec_grade -> MD_Metadata/dataQualityInfo/udk:DQ_DataQuality/report/DQ_CompletenessCommission/DQ_QuantitativeResult/value/Record
        // T011_obj_geo.rec_exact -> MD_Metadata/dataQualityInfo/udk:DQ_DataQuality/report/DQ_RelativeInternalPositionalAccuracy[measureDescription/CharacterString='geographic']/DQ_QuantitativeResult/value/Record
        // T011_obj_geo.pos_accuracy_vertical -> MD_Metadata/dataQualityInfo/DQ_DataQuality/report/DQ_RelativeInternalPositionalAccuracy[measureDescription/CharacterString='vertical']/DQ_QuantitativeResult.value
        
    }

    private void addParentIdentifier(Element metaData, IngridHit hit) {
        String parentIdentifier = IngridQueryHelper.getParentIdentifier(hit);
        this.addSMXMLCharacterString(metaData.addElement("iso19115full:parentIdentifier"), parentIdentifier);
    }

    private void addCharacterSet(Element metaData, IngridHit hit) {
        try {
            Long code = Long.valueOf(IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_DATASET_CHARACTER_SET));
            String codeVal = UtilsUDKCodeLists.getCodeListEntryName(new Long(510), code, new Long(94));
            if (codeVal.length() > 0) {
                metaData.addElement("iso19115full:characterSet").addElement("smXML:MD_CharacterSetCode")
                .addAttribute("codeList", "http://www.tc211.org/ISO19139/resources/codeList.xml?MD_CharacterSetCode")
                .addAttribute("codeListValue", codeVal);
            }
        } catch (NumberFormatException e) {}
    }

    private void addCitation(Element parent, IngridHit hit) {
        Element ciCitation = parent.addElement("smXML:citation")
        .addElement("smXML:CI_Citation");
        // add title
        this.addSMXMLCharacterString(ciCitation.addElement("smXML:title"), IngridQueryHelper.getDetailValueAsString(hit,
                IngridQueryHelper.HIT_KEY_OBJECT_TITLE));
        // add alternate title
        this.addSMXMLCharacterString(ciCitation.addElement("smXML:alternateTitle"), IngridQueryHelper.getDetailValueAsString(hit,
                IngridQueryHelper.HIT_KEY_OBJECT_DATASET_ALTERNATE_TITLE));
        
        // add dates (creation, revision etc.)
        super.addCitationReferenceDates(ciCitation, hit);
        
        // citedResponsibleParty not implemented, because only 
        // UDK classes 1 and 3 are supported. citedResponsibleParty always maps to 
        // udk class 2
        
        // presentationForm not implemented, see citedResponsibleParty

        // series, otherCitationDetails not implemented, see citedResponsibleParty
        
        // collectiveTitle, ISBN, etc. see citedResponsibleParty

        
    }
    
    private void addIdentificationInfoService(Element metaData, IngridHit hit) {
        Element cswServiceIdentification = metaData.addElement("iso19115full:identificationInfo").addElement(
                "iso19119:CSW_ServiceIdentification");

        addGenericMetadataIndentification(cswServiceIdentification, hit);
        
        this.addSMXMLCharacterString(cswServiceIdentification.addElement("iso19119:serviceType"), IngridQueryHelper
                        .getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERVICE_TYPE));

        String[] serviceTypeVersions = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERVICE_TYPE_VERSION);
        for (int i=0; i< serviceTypeVersions.length; i++) {
            this.addSMXMLCharacterString(cswServiceIdentification.addElement("iso19119:serviceTypeVersion"), serviceTypeVersions[i]);
        }

        super.addOperationMetadata(cswServiceIdentification, hit);
        
        // iso19119:operatesOn
        cswServiceIdentification.addElement("iso19119:operatesOn").addElement("smXML:Reference").addAttribute("uuidref", IngridQueryHelper.getParentIdentifier(hit));

        super.addExtent(cswServiceIdentification, hit, "iso19119");

        cswServiceIdentification.addElement("iso19119:couplingType").addElement("iso19119:CSW_CouplingType")
        .addAttribute("codeList", "http://opengis.org/codelistRegistry?CSW_CouplingType").addAttribute(
                "codeListValue",
                IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_OBJECT_SPECIAL_REF));
    
    }

    
    private void addGenericMetadataIndentification(Element parent, IngridHit hit) {
        // add citation construct
        this.addCitation(parent, hit);

        // add abstract
        this.addSMXMLCharacterString(parent.addElement("smXML:abstract"), IngridQueryHelper.getDetailValueAsString(hit,
                IngridQueryHelper.HIT_KEY_OBJECT_DESCR));

        // add purpose
        this.addSMXMLCharacterString(parent.addElement("smXML:purpose").addElement("smXML:MD_Constraints").addElement("smXML:useLimitation"), IngridQueryHelper.getDetailValueAsString(hit,
                IngridQueryHelper.HIT_KEY_OBJECT_INFO_NOTE));
        
        // add status
        try {
            Long code = Long.valueOf(IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_TIME_STATUS));
            String codeVal = UtilsUDKCodeLists.getCodeListEntryName(new Long(518), code, new Long(94));
            if (codeVal.length() > 0) {
                parent.addElement("smXML:status").addElement("smXML:MD_ProgressCode").addAttribute("codeList", "http://www.tc211.org/ISO19139/resources/codeList.xml?MD_ProgressCode")
                .addElement("codeListValue", codeVal);
            }
        } catch (NumberFormatException e) {}
        
        // add resourceSpecificUsage
        this.addSMXMLCharacterString(parent.addElement("iso19115full:resourceSpecificUsage").addElement("smXML:MD_Usage").addElement("smXML:specificUsage"), IngridQueryHelper.getDetailValueAsString(hit,
                IngridQueryHelper.HIT_KEY_OBJECT_DATASET_USAGE));
        
        // descriptiveKeywords
        String[] keywords = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_SEARCH_SEARCHTERM);
        String[] keywordTypes = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_SEARCH_TYPE);
        ArrayList thesaurusKeywords = new ArrayList();
        ArrayList freeKeywords = new ArrayList();
        for (int i=0; i < keywords.length; i++) {
            if (keywordTypes[i].equals("2")) {
                thesaurusKeywords.add(keywords[i]);
            } else if (keywordTypes[i].equals("1")) {
                freeKeywords.add(keywords[i]);
            }
        }
        if (thesaurusKeywords.size() > 0) {
            Element keywordType = parent.addElement("smXML:descriptiveKeywords").addElement("smXML:MD_Keywords");
            for (int i=0; i<thesaurusKeywords.size(); i++ ) {
                this.addSMXMLCharacterString(keywordType.addElement("smXML:keyword"), (String)thesaurusKeywords.get(i));
            }
            this.addSMXMLCharacterString(keywordType.addElement("smXML:title"), "UDK-Thesaurus");
            keywordType.addElement("smXML:type").addElement("smXML:MD_KeywordTypeCode")
                .addAttribute("codeList", "http://www.tc211.org/ISO19139/resources/codeList.xml?MD_KeywordTypeCode")
                .addAttribute("codeListValue", "Theme");
        }
        if (freeKeywords.size() > 0) {
            Element keywordType = parent.addElement("smXML:descriptiveKeywords").addElement("smXML:MD_Keywords");
            for (int i=0; i<freeKeywords.size(); i++ ) {
                this.addSMXMLCharacterString(keywordType.addElement("smXML:keyword"), (String)freeKeywords.get(i));
            }
        }
        
        // add resourceConstraint
        this.addSMXMLCharacterString(parent.addElement("smXML:resourceConstraints").addElement("smXML:MD_Constraints").addElement("smXML:useLimitation"), IngridQueryHelper.getDetailValueAsString(hit,
                IngridQueryHelper.HIT_KEY_OBJECT_AVAIL_ACCESS_NOTE));
        
        // resource maintenance
        Element mdMaintenanceInformation = parent.addElement("smXML:resourceMaintenance").addElement("smXML:MD_MaintenanceInformation");
        this.addSMXMLCharacterString(mdMaintenanceInformation.addElement("maintenanceNote"), IngridQueryHelper.getDetailValueAsString(hit,
                IngridQueryHelper.HIT_KEY_OBJECT_TIME_DESCR));
        try {
            Long code = Long.valueOf(IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_TIME_PERIOD));
            String codeVal = UtilsUDKCodeLists.getCodeListEntryName(new Long(518), code, new Long(94));
            if (codeVal.length() > 0) {
                mdMaintenanceInformation.addElement("smXML:maintenanceAndUpdateFrequency").addElement("smXML:MD_MaintenanceFrequencyCode")
                .addAttribute("codeList", "http://www.tc211.org/ISO19139/resources/codeList.xml?MD_MaintenanceFrequencyCode")
                .addAttribute("codeListValue", codeVal);
            }
        } catch (NumberFormatException e) {}
        // userDefinedMaintenanceFrequency
        String timeInterval = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_TIME_INTERVAL);
        if (timeInterval.length() > 0) {
            timeInterval = timeInterval.concat(" ").concat(IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_TIME_INTERVAL));
            mdMaintenanceInformation.addElement("smXML:userDefinedMaintenanceFrequency").addElement("smXML:TM_PeriodDuration").addText(timeInterval);
        }
        
    }
    
    private void addIdentificationInfoDataset(Element metaData, IngridHit hit) {
        Element mdDataIdentification = metaData.addElement("iso19115full:identificationInfo").addElement(
                "smXML:MD_DataIdentification");

        addGenericMetadataIndentification(mdDataIdentification, hit);

        // add language
        String dataLang =  IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_DATA_LANGUAGE);
        if (dataLang.equals("121")) {
            dataLang = "de";
        } else {
            dataLang = "en";
        }
        this.addSMXMLCharacterString(mdDataIdentification.addElement("smXML:language"), dataLang);

        String[] topicCategories = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_GEO_TOPIC_CATEGORY);
        for (int i=0; i<topicCategories.length; i++) {
            Long code;
            try {
                code = Long.valueOf(topicCategories[i]);
                String codeVal = UtilsUDKCodeLists.getCodeListEntryName(new Long(527), code, new Long(94));
                if (codeVal.length() > 0) {
                    mdDataIdentification.addElement("smXML:topicCategory").addElement("smXML:MD_TopicCategoryCode").addText(codeVal);
                }
            } catch (NumberFormatException e) {}
        }
        
        super.addExtent(mdDataIdentification, hit, "smXML");
    }

}
