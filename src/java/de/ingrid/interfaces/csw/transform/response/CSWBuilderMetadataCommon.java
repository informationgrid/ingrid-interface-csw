/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.transform.response;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import de.ingrid.interfaces.csw.utils.Udk2CswDateFieldParser;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.udk.UtilsUDKCodeLists;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public abstract class CSWBuilderMetadataCommon extends CSWBuilderMetaData {

    private static Log log = LogFactory.getLog(CSWBuilderMetadataCommon.class);

    /**
     * Add the hierarchy level construct to a given element
     * 
     * @param parent
     * @param level
     * @return The parent element
     */
    protected Element addHierarchyLevel(Element parent, String level) {
        // TODO: if dataset, get scope from T011_obj_geo.hierarchy_level and transform code with codelist 525
        parent.addElement("smXML:MD_ScopeCode").addAttribute("codeList",
                "http://www.tc211.org/ISO19139/resources/codeList.xml?MD_ScopeCode").addAttribute("codeListValue",
                level);
        return parent;
    }

    protected void addContacts(Element parent, IngridHit hit) throws Exception {
    	this.addContactBlocks(parent, hit, null);
    }

    protected void addContacts(Element parent, IngridHit hit, String ns ) throws Exception {
    	this.addContactBlocks(parent, hit, ns);
    }
    
    protected void addContactBlocks(Element parent, IngridHit hit, String ns) throws Exception {

        String[] addressIds = IngridQueryHelper.getDetailValueAsArray(hit, "t012_obj_adr.adr_id");
        String[] addressTypes = IngridQueryHelper.getDetailValueAsArray(hit, "t012_obj_adr.typ");
        String[] specialNames = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_ADR_SPECIAL_NAME);
        for (int i = 0; i < addressTypes.length; i++) {
            Element ciResponsibleParty = parent.addElement(getNSElementName(ns, "contact")).addElement("smXML:CI_ResponsibleParty");
        	
        	// get complete address information
            IngridHit address = IngridQueryHelper.getCompleteAddress(addressIds[i], hit.getPlugId());
            if (address != null) {
                
                this.addSMXMLCharacterString(ciResponsibleParty.addElement("smXML:individualName"), IngridQueryHelper.getCompletePersonName(address));
                this.addSMXMLCharacterString(ciResponsibleParty.addElement("smXML:organisationName"), IngridQueryHelper.getDetailValueAsString(address,
                        IngridQueryHelper.HIT_KEY_ADDRESS_INSTITUITION));
                this.addSMXMLCharacterString(ciResponsibleParty.addElement("smXML:positionName"), IngridQueryHelper.getDetailValueAsString(address,
                        IngridQueryHelper.HIT_KEY_ADDRESS_JOB));
                Element CIContact = ciResponsibleParty.addElement("smXML:contactInfo").addElement("smXML:CI_Contact");
    
                HashMap communications = IngridQueryHelper.getCommunications(address);
                ArrayList phoneNumbers = (ArrayList) communications.get("phone");
                ArrayList faxNumbers = (ArrayList) communications.get("fax");
                if (phoneNumbers.size() > 0 || faxNumbers.size() > 0) {
                    Element CI_Telephone = CIContact.addElement("smXML:phone").addElement("smXML:CI_Telephone");
                    for (int j = 0; j < phoneNumbers.size(); j++) {
                        this.addSMXMLCharacterString(CI_Telephone.addElement("smXML:voice"), (String) phoneNumbers.get(j));
                    }
                    for (int j = 0; j < faxNumbers.size(); j++) {
                        this.addSMXMLCharacterString(CI_Telephone.addElement("smXML:facsimile"), (String) faxNumbers.get(j));
                    }
                }
    
                Element CIAddress = CIContact.addElement("smXML:address").addElement("smXML:CI_Address");
                String postBox = IngridQueryHelper.getDetailValueAsString(address, IngridQueryHelper.HIT_KEY_ADDRESS_POSTBOX);
                String zipPostBox = IngridQueryHelper.getDetailValueAsString(address, IngridQueryHelper.HIT_KEY_ADDRESS_ZIP_POSTBOX);
                if (postBox != null && postBox.length() > 0 && zipPostBox != null && zipPostBox.length() > 0) {
                    this.addSMXMLCharacterString(CIAddress.addElement("smXML:deliveryPoint"), postBox);
                    this.addSMXMLCharacterString(CIAddress.addElement("smXML:city"), IngridQueryHelper.getDetailValueAsString(address,
                            IngridQueryHelper.HIT_KEY_ADDRESS_CITY));
                    this.addSMXMLCharacterString(CIAddress.addElement("smXML:postalCode"), zipPostBox);
                } else {
                    this.addSMXMLCharacterString(CIAddress.addElement("smXML:deliveryPoint"), IngridQueryHelper.getDetailValueAsString(address,
                            IngridQueryHelper.HIT_KEY_ADDRESS_STREET));
                    this.addSMXMLCharacterString(CIAddress.addElement("smXML:city"), IngridQueryHelper.getDetailValueAsString(address,
                            IngridQueryHelper.HIT_KEY_ADDRESS_CITY));
                    this.addSMXMLCharacterString(CIAddress.addElement("smXML:postalCode"), IngridQueryHelper.getDetailValueAsString(address,
                            IngridQueryHelper.HIT_KEY_ADDRESS_ZIP));
                }
                this.addSMXMLCharacterString(CIAddress.addElement("smXML:country"), IngridQueryHelper.getDetailValueAsString(address,
                        IngridQueryHelper.HIT_KEY_ADDRESS_STATE_ID));
                ArrayList emails = (ArrayList) communications.get("email");
                for (int j = 0; j < emails.size(); j++) {
                    this.addSMXMLCharacterString(CIAddress.addElement("smXML:electronicMailAddress"), (String) emails.get(j));
                }
    
                // CSW 2.0 unterstützt nur eine online resource
                ArrayList url = (ArrayList) communications.get("url");
                if (url.size() > 0) {
                    Element CI_OnlineResource = CIContact.addElement("smXML:onlineResource").addElement(
                            "smXML:CI_OnlineResource");
                    this.addSMXMLUrl(CI_OnlineResource.addElement("smXML:linkage"), (String) url.get(0));
                }
                // t012_obj_adr.typ CodeList 505  MD_Metadata/contact/CI_ResponsibleParty/role/CI_RoleCode
                //  if t012_obj_adr.typ  999  use T012_obj_adr.special_name MD_Metadata/contact/CI_ResponsibleParty/role/CI_RoleCode
                try {
                    /* mapping of UDK addresstypes to CSW address types
                    
                    UDK | CSW (codelist 505)| Name
                    0 | 7 | Auskunft
                    1 | 3 | Datenhalter
                    2 | 2 | Datenverantwortung
                    3 | 1 | Anbieter
                    4 | 4 | Benutzer
                    5 | 5 | Vertrieb
                    6 | 6 | Herkunft
                    7 | 8 | Datenerfassung
                    8 | 9 | Auswertung
                    9 | 10 | Herausgeber
                    999 | keine Entsprechung, mapping auf codeListValue | Sonstige Angaben
                     */
                    Long code = Long.valueOf(UtilsUDKCodeLists.udkToCodeList505(addressTypes[i]));
                    String codeVal;
                    if (code.longValue() == 999) {
                        codeVal = specialNames[i];
                    } else {
                        codeVal = UtilsUDKCodeLists.getCodeListEntryName(new Long(505), code, new Long(94));
                    }
                    if (codeVal != null && codeVal.length() > 0) {
                        ciResponsibleParty.addElement("smXML:role").addElement("smXML:CI_RoleCode")
                        .addAttribute("codeList", "http://www.tc211.org/ISO19139/resources/codeList.xml?CI_RoleCode")
                        .addAttribute("codeListValue", codeVal);
                    }
                } catch (NumberFormatException e) {}
            }
        }    
    
    
    }

    /**
     * Adds a CSW file identifier to a given element.
     * 
     * @param parent
     *            The Element to add the identifier to.
     * @param hit
     *            The IngridHit.
     * @param doc
     *            The Document.
     * @return The parent element.
     */
    protected void addFileIdentifier(Element parent, String id, String ns) {
        this.addSMXMLCharacterString(parent.addElement(getNSElementName(ns, "fileIdentifier")), id);
    }

    protected void addFileIdentifier(Element parent, String id) {
        addFileIdentifier(parent, id, null);
    }
    
    

    protected void addLanguage(Element metaData, IngridHit hit) {
        addLanguage(metaData, hit, null);
    }

    protected void addLanguage(Element metaData, IngridHit hit, String ns) {
        String metadataLang =  IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_METADATA_LANGUAGE);
        this.addSMXMLCharacterString(metaData.addElement(getNSElementName(ns, "language")), getISO639_2LanguageCode(metadataLang));
    }
    
    
    protected void addDateStamp(Element metaData, IngridHit hit) {
        addDateStamp(metaData, hit, null);
    }

    protected void addDateStamp(Element metaData, IngridHit hit, String ns) {
        String creationDate = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_MOD_TIME);
        if (creationDate != null && creationDate.length() > 0) {
            String cswDate = Udk2CswDateFieldParser.instance().parse(creationDate);
            if (cswDate.indexOf("T") > -1) {
            	metaData.addElement(getNSElementName(ns, "dateStamp")).addElement("smXML:DateTime").addText(Udk2CswDateFieldParser.instance().parse(creationDate));
            } else {
            	metaData.addElement(getNSElementName(ns, "dateStamp")).addElement("smXML:Date").addText(Udk2CswDateFieldParser.instance().parse(creationDate));
            }
        }
    }
    
    protected void addCitationReferenceDates(Element parent, IngridHit hit, String ns) {
        // add dates (creation, revision etc.)
        String[] referenceDate = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_DATASET_REFERENCE_DATE);
        String[] referenceDateTypes = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_DATASET_REFERENCE_TYPE);
        if (referenceDate != null) {
            for (int i = 0; i < referenceDate.length; i++) {
                String creationDate = referenceDate[i];
                if (creationDate != null && creationDate.length() > 0) {
                	Element ciDate;
                	if (ns != null) {
                		ciDate = parent.addElement(ns + ":date").addElement("smXML:CI_Date");
                	} else {
                		ciDate = parent.addElement("date").addElement("smXML:CI_Date");
                	}
                    ciDate.addElement("smXML:date").addElement("smXML:Date").addText(Udk2CswDateFieldParser.instance().parseToDate(creationDate));
                    String codeListValue;
                    if (referenceDateTypes[i].equals("1")) {
                        codeListValue = "creation";
                    } else if (referenceDateTypes[i].equals("2")) {
                        codeListValue = "publication";
                    } else if (referenceDateTypes[i].equals("3")) {
                        codeListValue = "revision";
                    } else {
                        log.warn("Invalid UDK dataset reference date type: " + referenceDateTypes[i] + ".");
                        codeListValue = "unspecified";
                    }
                    ciDate.addElement("smXML:dateType").addElement("smXML:CI_DateTypeCode").addAttribute("codeList",
                            "http://www.tc211.org/ISO19139/resources/codeList.xml?CI_DateTypeCode").addAttribute(
                            "codeListValue", codeListValue);
                    
                }
            }
        }
    }
    
    protected void addOperationMetadata(Element parent, IngridHit hit) {
        // operationMetadata
        Element svOperationMetadata = parent.addElement("iso19119:operationMetadata").addElement("iso19119:SV_OperationMetadata");
        // iso19119:operationMetadata -> iso19119:SV_OperationMetadata -> iso19119:operationName -> String
        this.addSMXMLCharacterString(svOperationMetadata.addElement("iso19119:operationName"), IngridQueryHelper
                .getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_OPERATION_NAME));
        // iso19119:operationMetadata -> iso19119:SV_OperationMetadata -> iso19119:operationDescription -> String
        this.addSMXMLCharacterString(svOperationMetadata.addElement("iso19119:operationDescription"), IngridQueryHelper
                .getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_OPERATION_DESCR));
        // iso19119:operationMetadata -> iso19119:SV_OperationMetadata ->(1:n) iso19119:DCP -> iso19119:SV_DCPList/@codeListValue
        String[] platforms = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_OP_PLATFORM);
        for (int i=0; i< platforms.length; i++) {
            svOperationMetadata.addElement("iso19119:DCP").addElement("iso19119:SV_DCPList")
                .addAttribute("codeList", "http://opengis.org/codelistRegistry?CSW_DCPCodeType")
                .addAttribute("codeList", platforms[i]);
        }
        // iso19119:operationMetadata -> iso19119:SV_OperationMetadata -> iso19119:invocationName -> String
        this.addSMXMLCharacterString(svOperationMetadata.addElement("iso19119:invocationName"), IngridQueryHelper
                .getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_INVOCATION_NAME));
        // iso19119:operationMetadata -> iso19119:SV_OperationMetadata ->(1:n) iso19119:connectPoint -> smXML:CI_OnlineResource
        String[] connectPoints = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_OP_CONNECT_POINT);
        for (int i=0; i< connectPoints.length; i++) {
            svOperationMetadata.addElement("iso19119:connectPoint")
                .addElement("smXML:CI_OnlineResource").addElement("smXML:linkage")
                    .addElement("smXML:URL").addText(connectPoints[i]);
        }
        // iso19119:paramaters
        String[] parameterNames = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_OP_PARAM_NAME);
        String[] parameterDirections = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_OP_PARAM_DIRECTION);
        String[] parameterDescriptions = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_OP_PARAM_DESCR);
        String[] parameterOptionality = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_OP_PARAM_OPTIONAL);
        String[] parameterRepeatability = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_OP_PARAM_REPEATABILITY);
        for (int i=0; i<parameterNames.length; i++) {
            Element parameters = svOperationMetadata.addElement("iso19119:parameters");
            // iso19119:operationMetadata -> iso19119:SV_OperationMetadata ->(1:n) iso19119:parameters -> iso19119:SV_Parameter -> iso19119:name -> smXML:MemberName -> smXML:aName -> smXML:CharacterString
            this.addSMXMLCharacterString(parameters.addElement("SV_Parameter").addElement("iso19119:name").addElement("smXML:MemberName").addElement("smXML:aName"),parameterNames[i]); 
            // iso19119:operationMetadata -> iso19119:SV_OperationMetadata ->(1:n) iso19119:parameters -> iso19119:direction -> iso19119:SV_ParameterDirection -> (in|out|in/out)
            parameters.addElement("iso19119:direction").addElement("iso19119:SV_ParameterDirection").addText(parameterDirections[i]);
            // iso19119:operationMetadata -> iso19119:SV_OperationMetadata ->(1:n) iso19119:parameters -> iso19119:description -> smXML:CharacterString
            this.addSMXMLCharacterString(parameters.addElement("iso19119:description"), parameterDescriptions[i]);
            // iso19119:operationMetadata -> iso19119:SV_OperationMetadata ->(1:n) iso19119:parameters -> iso19119:optionality -> smXML:CharacterString
            this.addSMXMLCharacterString(parameters.addElement("iso19119:optionality"), parameterOptionality[i]);
            // iso19119:operationMetadata -> iso19119:SV_OperationMetadata ->(1:n) iso19119:parameters -> iso19119:repeatability -> smXML:Boolean
            this.addSMXMLBoolean(parameters.addElement("iso19119:repeatability"), (parameterRepeatability[i] !=null && parameterRepeatability[i].equals("1")));
        }
        
    }
    
    protected void addExtent (Element parent, IngridHit hit, String ns) {
        // extend
        Element exExent = parent.addElement(this.getNSElementName(ns, "extent")).addElement("smXML:EX_Extent");
        // T01_object.loc_descr MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/description
        super.addSMXMLCharacterString(exExent.addElement("smXML:description"), IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_LOC_DESCR));
        
        Element exVerticalExtent = exExent.addElement("smXML:verticalElement").addElement("smXML:EX_VerticalExtent");
        // T01_object.vertical_extent_minimum MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/verticalElement/EX_VerticalExtent.minimumValue
        super.addSMXMLReal(exVerticalExtent.addElement("smXML:minimumValue"), IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_VERTICAL_EXTENT_MINIMUM));
        // T01_object.vertical_extent_maximum MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/verticalElement/EX_VerticalExtent.maximumValue
        super.addSMXMLReal(exVerticalExtent.addElement("smXML:maximumValue"), IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_VERTICAL_EXTENT_MAXIMUM));
        
        // T01_object.vertical_extent_unit = Wert [Domain-ID Codelist 102] MD_Metadata/full:identificationInfo/MD_DataIdentification/extent/EX_Extent/verticalElement/EX_VerticalExtent/unitOfMeasure/UomLength/uomName/CharacterString
        String codeVal = "";
        try {
            Long code = Long.valueOf(IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_VERTICAL_EXTENT_UNIT));
            codeVal = UtilsUDKCodeLists.getCodeListEntryName(new Long(102), code, new Long(94));
        } catch (NumberFormatException e) {}
        super.addSMXMLCharacterString(exVerticalExtent.addElement("smXML:unitOfMeasure").addElement("smXML:UomLength").addElement("smXML:uomName"), codeVal);

        // T01_object.vertical_extent_vdatum = Wert [Domain-Id Codelist 101] MD_Metadata/smXML:identificationInfo/iso19119:CSW_ServiceIdentification/iso19119:extent/smXML:EX_Extent/verticalElement/EX_VerticalExtent/verticalDatum/smXML:RS_Identifier/code/smXML:CharacterString
        codeVal = "";
        try {
            Long code = Long.valueOf(IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_VERTICAL_EXTENT_VDATUM));
            codeVal = UtilsUDKCodeLists.getCodeListEntryName(new Long(101), code, new Long(94));
        } catch (NumberFormatException e) {}
        super.addSMXMLCharacterString(exVerticalExtent.addElement("smXML:verticalDatum").addElement("smXML:RS_Identifier").addElement("smXML:code"), codeVal);
        
        // T01_object.time_from MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/temporalElement/EX_TemporalExtent/extent/TM_Primitive/gml:relatedTime/gml:TimePeriod/gml:beginPosition/gml:TimeInstant/gml:timePosition
        
        String myDateType = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_TIME_TYPE);
        String beginDate = null;
        String endDate = null;
        if (myDateType != null && myDateType.equals("von")) {
        	beginDate = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_TIME_T1);
        	endDate = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_TIME_T2);
        } else if (myDateType != null && myDateType.equals("seit")) {
        	beginDate = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_TIME_T1);
        } else if (myDateType != null && myDateType.equals("bis")) {
        	endDate = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_TIME_T2);
        } else if (myDateType != null && myDateType.equals("am")) {
        	beginDate = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_TIME_T0);
        	endDate = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_TIME_T0);
        }
        if (beginDate != null || endDate != null) {
            Element timePeriod = exExent.addElement("smXML:temporalElement").addElement("smXML:EX_TemporalExtent").addElement("smXML:extent").addElement("smXML:TM_Primitive").addElement("gml:relatedTime").addElement("gml:TimePeriod");
            if (beginDate != null) {
                timePeriod.addElement("gml:beginPosition").addElement("gml:TimeInstant").addElement("gml:timePosition")
                    .addText(Udk2CswDateFieldParser.instance().parse(beginDate));
            }
            if (endDate != null) {
                timePeriod.addElement("gml:endPosition").addElement("gml:TimeInstant").addElement("gml:timePosition")
                    .addText(Udk2CswDateFieldParser.instance().parse(endDate));
            }
        }

        String[] coordinatesBezug = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_COORDINATES_BEZUG);
        String[] coordinatesGeoX1 = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_COORDINATES_GEO_X1);
        String[] coordinatesGeoX2 = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_COORDINATES_GEO_X2);
        String[] coordinatesGeoY1 = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_COORDINATES_GEO_Y1);
        String[] coordinatesGeoY2 = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_COORDINATES_GEO_Y2);
        for (int i=0; i< coordinatesBezug.length; i++) {
            Element geographicElement = exExent.addElement("smXML:geographicElement");
            // T019_coordinates.bezug MD_Metadata/smXML:identificationInfo/iso19119:CSW_ServiceIdentification/iso19119:extent/smXML:EX_Extent/smXML:geographicElement/smXML:EX_GeographicDescription/smXML:geographicIdentifier/smXML:RS_Identifier/code/smXML:CharacterString
            super.addSMXMLCharacterString(geographicElement.addElement("smXML:EX_GeographicDescription").addElement("smXML:geographicIdentifier").addElement("smXML:MD_Identifier").addElement("smXML:code"), coordinatesBezug[i]);
            geographicElement = exExent.addElement("smXML:geographicElement");
            Element exGeographicBoundingBox = geographicElement.addElement("smXML:EX_GeographicBoundingBox");
            // T019_coordinates.geo_x1 MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/geographicElement/EX_GeographicBoundingBox/westBoundLongitude/smXML:approximateLongitude
            exGeographicBoundingBox.addElement("smXML:westBoundLongitude").addElement("smXML:approximateLongitude").addText(coordinatesGeoX1[i].replaceAll(",", "."));
            // T019_coordinates.geo_x2 MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/geographicElement/EX_GeographicBoundingBox/eastBoundLongitude/smXML:approximateLongitude
            exGeographicBoundingBox.addElement("smXML:eastBoundLongitude").addElement("smXML:approximateLongitude").addText(coordinatesGeoX2[i].replaceAll(",", "."));
            // T019_coordinates.geo_y2 MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/geographicElement/EX_GeographicBoundingBox/southBoundLatitude/smXML:approximateLatitude
            exGeographicBoundingBox.addElement("smXML:southBoundLatitude").addElement("smXML:approximateLatitude").addText(coordinatesGeoY1[i].replaceAll(",", "."));
            // T019_coordinates.geo_y1 MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/geographicElement/EX_GeographicBoundingBox/northBoundLatitude/smXML:approximateLatitude
            exGeographicBoundingBox.addElement("smXML:northBoundLatitude").addElement("smXML:approximateLatitude").addText(coordinatesGeoY2[i].replaceAll(",", "."));
        }

        String[] stTownship = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_ST_BBOX_LOC_TOWN_NO);
        String[] stBoxX1 = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_ST_BOX_X1);
        String[] stBoxX2 = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_ST_BOX_X2);
        String[] stBoxY1 = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_ST_BOX_Y1);
        String[] stBoxY2 = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_ST_BOX_Y2);

        for (int i=0; i< stTownship.length; i++) {
            Element geographicElement = exExent.addElement("smXML:geographicElement");
            // T011_township.township_no MD_Metadata/smXML:identificationInfo/iso19119:CSW_ServiceIdentification/iso19119:extent/smXML:EX_Extent/smXML:geographicElement/smXML:EX_GeographicDescription/smXML:geographicIdentifier/smXML:RS_Identifier/code/smXML:CharacterString
            super.addSMXMLCharacterString(geographicElement.addElement("smXML:EX_GeographicDescription").addElement("smXML:geographicIdentifier").addElement("smXML:MD_Identifier").addElement("smXML:code"), stTownship[i]);
            
            geographicElement = exExent.addElement("smXML:geographicElement");
            Element exGeographicBoundingBox = geographicElement.addElement("smXML:EX_GeographicBoundingBox");
            // T01_st_bbox.x1 MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/geographicElement/EX_GeographicBoundingBox.westBoundLongitude/smXML:approximateLongitude
            if (stBoxX1.length == stTownship.length) {
            	exGeographicBoundingBox.addElement("smXML:westBoundLongitude").addElement("smXML:approximateLongitude").addText(stBoxX1[i].replaceAll(",", "."));
            }
            // T01_st_bbox.x2 MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/geographicElement/EX_GeographicBoundingBox.eastBoundLongitude/smXML:approximateLongitude
            if (stBoxX2.length == stTownship.length) {
            	exGeographicBoundingBox.addElement("smXML:eastBoundLongitude").addElement("smXML:approximateLongitude").addText(stBoxX2[i].replaceAll(",", "."));
            }
            // T01_st_bbox.y1 MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/geographicElement/EX_GeographicBoundingBox.southBoundLatitude/smXML:approximateLatitude
            if (stBoxY1.length == stTownship.length) {
            	exGeographicBoundingBox.addElement("smXML:southBoundLatitude").addElement("smXML:approximateLatitude").addText(stBoxY1[i].replaceAll(",", "."));
            }
            // T01_st_bbox.y2 MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/geographicElement/EX_GeographicBoundingBox.northBoundLatitude/smXML:approximateLatitude
            if (stBoxY2.length == stTownship.length) {
            	exGeographicBoundingBox.addElement("smXML:northBoundLatitude").addElement("smXML:approximateLatitude").addText(stBoxY2[i].replaceAll(",", "."));
            }
        }
    }

}
