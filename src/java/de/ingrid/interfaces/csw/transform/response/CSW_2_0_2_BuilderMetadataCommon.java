/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.transform.response;

import java.util.ArrayList;
import java.util.Date;
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
public abstract class CSW_2_0_2_BuilderMetadataCommon extends CSW_2_0_2_BuilderMetaData {

    private static Log log = LogFactory.getLog(CSW_2_0_2_BuilderMetadataCommon.class);

	protected String getTypeName() {
        String typeName = null;
		String udkClass = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_OBJ_CLASS);
        if (udkClass.equals("1")) {
			Long code = Long.valueOf(IngridQueryHelper.getDetailValueAsString(hit,
					IngridQueryHelper.HIT_KEY_OBJECT_GEO_HIERARCHY_LEVEL));
			typeName = UtilsUDKCodeLists.getCodeListEntryName(new Long(525), code, new Long(94));
			if (!IngridQueryHelper.hasValue(typeName)) {
				typeName = "datatset";
			}
        } else if (udkClass.equals("3")) {
            typeName = "service";
        } else {
        	if (log.isInfoEnabled()) {
        		log.info("Unsupported UDK class " + udkClass
                    + ". Only class 1 and 3 are supported by the CSW interface.");
        	}
        }
        return typeName;
	}
    
    
    protected void addCharacterSet(Element metaData, IngridHit hit) {
		try {
			Long code = Long.valueOf(IngridQueryHelper.getDetailValueAsString(hit,
					IngridQueryHelper.HIT_KEY_OBJECT_DATASET_CHARACTER_SET));
			String codeVal = UtilsUDKCodeLists.getCodeListEntryName(new Long(510), code, new Long(94));
			if (IngridQueryHelper.hasValue(codeVal)) {
				metaData.addElement("gmd:characterSet").addElement("gmd:MD_CharacterSetCode").addAttribute(
						"codeList", "http://www.tc211.org/ISO19139/resources/codeList.xml?MD_CharacterSetCode")
						.addAttribute("codeListValue", codeVal);
			}
		} catch (NumberFormatException e) {
		}
	}

    
    protected void addParentIdentifier(Element metaData, IngridHit hit) {
		String parentIdentifier = IngridQueryHelper.getParentIdentifier(hit);
		if (IngridQueryHelper.hasValue(parentIdentifier)) {
			this.addGCOCharacterString(metaData.addElement("gmd:parentIdentifier"), parentIdentifier);
		}
	}

    
    /**
     * Add the hierarchy level construct to a given element
     * 
     * @param parent
     * @param level
     * @return The parent element
     */
    protected Element addHierarchyLevel(Element parent, String level) {
        // TODO: if dataset, get scope from T011_obj_geo.hierarchy_level and transform code with codelist 525
        parent.addElement("gmd:MD_ScopeCode").addAttribute("codeList",
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
            Element ciResponsibleParty = parent.addElement(getNSElementName(ns, "contact")).addElement("gmd:CI_ResponsibleParty");
        	
        	// get complete address information
            IngridHit address = IngridQueryHelper.getCompleteAddress(addressIds[i], hit.getPlugId());
            if (address != null) {
                
                this.addGCOCharacterString(ciResponsibleParty.addElement("gmd:individualName"), IngridQueryHelper.getCompletePersonName(address));
                String organisationName = IngridQueryHelper.getDetailValueAsString(address, IngridQueryHelper.HIT_KEY_ADDRESS_INSTITUITION);
                if (IngridQueryHelper.hasValue(organisationName)) {
	                this.addGCOCharacterString(ciResponsibleParty.addElement("gmd:organisationName"), organisationName);
                }
                String positionName = IngridQueryHelper.getDetailValueAsString(address, IngridQueryHelper.HIT_KEY_ADDRESS_JOB);
                if (IngridQueryHelper.hasValue(positionName)) {
	                this.addGCOCharacterString(ciResponsibleParty.addElement("gmd:positionName"), positionName);
                }
                Element CIContact = ciResponsibleParty.addElement("gmd:contactInfo").addElement("gmd:CI_Contact");
    
                HashMap communications = IngridQueryHelper.getCommunications(address);
                ArrayList phoneNumbers = (ArrayList) communications.get("phone");
                ArrayList faxNumbers = (ArrayList) communications.get("fax");
                if (phoneNumbers.size() > 0 || faxNumbers.size() > 0) {
                    Element CI_Telephone = CIContact.addElement("gmd:phone").addElement("gmd:CI_Telephone");
                    for (int j = 0; j < phoneNumbers.size(); j++) {
                        this.addGCOCharacterString(CI_Telephone.addElement("gmd:voice"), (String) phoneNumbers.get(j));
                    }
                    for (int j = 0; j < faxNumbers.size(); j++) {
                        this.addGCOCharacterString(CI_Telephone.addElement("gmd:facsimile"), (String) faxNumbers.get(j));
                    }
                }
    
                Element CIAddress = CIContact.addElement("gmd:address").addElement("gmd:CI_Address");
                String postBox = IngridQueryHelper.getDetailValueAsString(address, IngridQueryHelper.HIT_KEY_ADDRESS_POSTBOX);
                String zipPostBox = IngridQueryHelper.getDetailValueAsString(address, IngridQueryHelper.HIT_KEY_ADDRESS_ZIP_POSTBOX);
                if (postBox != null && postBox.length() > 0 && zipPostBox != null && zipPostBox.length() > 0) {
                    this.addGCOCharacterString(CIAddress.addElement("gmd:deliveryPoint"), postBox);
                    this.addGCOCharacterString(CIAddress.addElement("gmd:city"), IngridQueryHelper.getDetailValueAsString(address,
                            IngridQueryHelper.HIT_KEY_ADDRESS_CITY));
                    this.addGCOCharacterString(CIAddress.addElement("gmd:postalCode"), zipPostBox);
                } else {
                    this.addGCOCharacterString(CIAddress.addElement("gmd:deliveryPoint"), IngridQueryHelper.getDetailValueAsString(address,
                            IngridQueryHelper.HIT_KEY_ADDRESS_STREET));
                    this.addGCOCharacterString(CIAddress.addElement("gmd:city"), IngridQueryHelper.getDetailValueAsString(address,
                            IngridQueryHelper.HIT_KEY_ADDRESS_CITY));
                    this.addGCOCharacterString(CIAddress.addElement("gmd:postalCode"), IngridQueryHelper.getDetailValueAsString(address,
                            IngridQueryHelper.HIT_KEY_ADDRESS_ZIP));
                }
                this.addGCOCharacterString(CIAddress.addElement("gmd:country"), IngridQueryHelper.getDetailValueAsString(address,
                        IngridQueryHelper.HIT_KEY_ADDRESS_STATE_ID));
                ArrayList emails = (ArrayList) communications.get("email");
                for (int j = 0; j < emails.size(); j++) {
                    this.addGCOCharacterString(CIAddress.addElement("gmd:electronicMailAddress"), (String) emails.get(j));
                }
    
                // CSW 2.0 unterstützt nur eine online resource
                ArrayList url = (ArrayList) communications.get("url");
                if (url.size() > 0) {
                    Element CI_OnlineResource = CIContact.addElement("gmd:onlineResource").addElement(
                            "gmd:CI_OnlineResource");
                    this.addGMDUrl(CI_OnlineResource.addElement("gmd:linkage"), (String) url.get(0));
                }
                // t012_obj_adr.typ CodeList 505  MD_Metadata/contact/CI_ResponsibleParty/role/CI_RoleCode
                //  if t012_obj_adr.typ  999  use T012_obj_adr.special_name MD_Metadata/contact/CI_ResponsibleParty/role/CI_RoleCode
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
            	String codeVal = null;
            	try {
            		Long code = Long.valueOf(UtilsUDKCodeLists.udkToCodeList505(addressTypes[i]));
                    if (code.longValue() == 999 || code.longValue() == -1) {
                        codeVal = specialNames[i];
                    } else {
                        codeVal = UtilsUDKCodeLists.getCodeListEntryName(new Long(505), code, new Long(94));
                    }
            	} catch (NumberFormatException e) {
            		codeVal = specialNames[i];
            	}
                if (codeVal != null && codeVal.length() > 0) {
                    ciResponsibleParty.addElement("gmd:role").addElement("gmd:CI_RoleCode")
                    .addAttribute("codeList", "http://www.tc211.org/ISO19139/resources/codeList.xml?CI_RoleCode")
                    .addAttribute("codeListValue", codeVal);
                }
            }
        }    
    
    
    }

    
    protected void addPointOfContacts(Element parent, IngridHit hit) throws Exception {
    	this.aaddPointOfContactBlocks(parent, hit, null);
    }

    protected void addPointOfContacts(Element parent, IngridHit hit, String ns ) throws Exception {
    	this.aaddPointOfContactBlocks(parent, hit, ns);
    }
    
    protected void aaddPointOfContactBlocks(Element parent, IngridHit hit, String ns) throws Exception {

        String[] addressIds = IngridQueryHelper.getDetailValueAsArray(hit, "t012_obj_adr.adr_id");
        String[] addressTypes = IngridQueryHelper.getDetailValueAsArray(hit, "t012_obj_adr.typ");
        String[] specialNames = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_ADR_SPECIAL_NAME);
        for (int i = 0; i < addressTypes.length; i++) {
            Element ciResponsibleParty = parent.addElement(getNSElementName(ns, "pointOfContact")).addElement("gmd:CI_ResponsibleParty");
        	
        	// get complete address information
            IngridHit address = IngridQueryHelper.getCompleteAddress(addressIds[i], hit.getPlugId());
            if (address != null) {
                
                this.addGCOCharacterString(ciResponsibleParty.addElement("gmd:individualName"), IngridQueryHelper.getCompletePersonName(address));
                String organisationName = IngridQueryHelper.getDetailValueAsString(address, IngridQueryHelper.HIT_KEY_ADDRESS_INSTITUITION);
                if (IngridQueryHelper.hasValue(organisationName)) {
	                this.addGCOCharacterString(ciResponsibleParty.addElement("gmd:organisationName"), organisationName);
                }
                String positionName = IngridQueryHelper.getDetailValueAsString(address, IngridQueryHelper.HIT_KEY_ADDRESS_JOB);
                if (IngridQueryHelper.hasValue(positionName)) {
	                this.addGCOCharacterString(ciResponsibleParty.addElement("gmd:positionName"), positionName);
                }
                Element CIContact = ciResponsibleParty.addElement("gmd:contactInfo").addElement("gmd:CI_Contact");
    
                HashMap communications = IngridQueryHelper.getCommunications(address);
                ArrayList phoneNumbers = (ArrayList) communications.get("phone");
                ArrayList faxNumbers = (ArrayList) communications.get("fax");
                if (phoneNumbers.size() > 0 || faxNumbers.size() > 0) {
                    Element CI_Telephone = CIContact.addElement("gmd:phone").addElement("gmd:CI_Telephone");
                    for (int j = 0; j < phoneNumbers.size(); j++) {
                        this.addGCOCharacterString(CI_Telephone.addElement("gmd:voice"), (String) phoneNumbers.get(j));
                    }
                    for (int j = 0; j < faxNumbers.size(); j++) {
                        this.addGCOCharacterString(CI_Telephone.addElement("gmd:facsimile"), (String) faxNumbers.get(j));
                    }
                }
    
                Element CIAddress = CIContact.addElement("gmd:address").addElement("gmd:CI_Address");
                String postBox = IngridQueryHelper.getDetailValueAsString(address, IngridQueryHelper.HIT_KEY_ADDRESS_POSTBOX);
                String zipPostBox = IngridQueryHelper.getDetailValueAsString(address, IngridQueryHelper.HIT_KEY_ADDRESS_ZIP_POSTBOX);
                if (postBox != null && postBox.length() > 0 && zipPostBox != null && zipPostBox.length() > 0) {
                    this.addGCOCharacterString(CIAddress.addElement("gmd:deliveryPoint"), postBox);
                    this.addGCOCharacterString(CIAddress.addElement("gmd:city"), IngridQueryHelper.getDetailValueAsString(address,
                            IngridQueryHelper.HIT_KEY_ADDRESS_CITY));
                    this.addGCOCharacterString(CIAddress.addElement("gmd:postalCode"), zipPostBox);
                } else {
                    this.addGCOCharacterString(CIAddress.addElement("gmd:deliveryPoint"), IngridQueryHelper.getDetailValueAsString(address,
                            IngridQueryHelper.HIT_KEY_ADDRESS_STREET));
                    this.addGCOCharacterString(CIAddress.addElement("gmd:city"), IngridQueryHelper.getDetailValueAsString(address,
                            IngridQueryHelper.HIT_KEY_ADDRESS_CITY));
                    this.addGCOCharacterString(CIAddress.addElement("gmd:postalCode"), IngridQueryHelper.getDetailValueAsString(address,
                            IngridQueryHelper.HIT_KEY_ADDRESS_ZIP));
                }
                this.addGCOCharacterString(CIAddress.addElement("gmd:country"), IngridQueryHelper.getDetailValueAsString(address,
                        IngridQueryHelper.HIT_KEY_ADDRESS_STATE_ID));
                ArrayList emails = (ArrayList) communications.get("email");
                for (int j = 0; j < emails.size(); j++) {
                    this.addGCOCharacterString(CIAddress.addElement("gmd:electronicMailAddress"), (String) emails.get(j));
                }
    
                // CSW 2.0 unterstützt nur eine online resource
                ArrayList url = (ArrayList) communications.get("url");
                if (url.size() > 0) {
                    Element CI_OnlineResource = CIContact.addElement("gmd:onlineResource").addElement(
                            "gmd:CI_OnlineResource");
                    this.addGMDUrl(CI_OnlineResource.addElement("gmd:linkage"), (String) url.get(0));
                }
                // t012_obj_adr.typ CodeList 505  MD_Metadata/contact/CI_ResponsibleParty/role/CI_RoleCode
                //  if t012_obj_adr.typ  999  use T012_obj_adr.special_name MD_Metadata/contact/CI_ResponsibleParty/role/CI_RoleCode
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
            	String codeVal = null;
            	try {
            		Long code = Long.valueOf(UtilsUDKCodeLists.udkToCodeList505(addressTypes[i]));
                    if (code.longValue() == 999 || code.longValue() == -1) {
                        codeVal = specialNames[i];
                    } else {
                        codeVal = UtilsUDKCodeLists.getCodeListEntryName(new Long(505), code, new Long(94));
                    }
            	} catch (NumberFormatException e) {
            		codeVal = specialNames[i];
            	}
                if (codeVal != null && codeVal.length() > 0) {
                    ciResponsibleParty.addElement("gmd:role").addElement("gmd:CI_RoleCode")
                    .addAttribute("codeList", "http://www.tc211.org/ISO19139/resources/codeList.xml?CI_RoleCode")
                    .addAttribute("codeListValue", codeVal);
                }
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
        if (IngridQueryHelper.hasValue(id)) {
        	this.addGCOCharacterString(parent.addElement(getNSElementName(ns, "fileIdentifier")), id);
        }
    }

    protected void addFileIdentifier(Element parent, String id) {
        addFileIdentifier(parent, id, null);
    }
    
    

    protected void addLanguage(Element metaData, IngridHit hit) {
        addLanguage(metaData, hit, null);
    }

    protected void addLanguage(Element metaData, IngridHit hit, String ns) {
        String metadataLang =  IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_METADATA_LANGUAGE);
        if (IngridQueryHelper.hasValue(metadataLang)) {
        	this.addGCOCharacterString(metaData.addElement(getNSElementName(ns, "language")), getISO639_2LanguageCode(metadataLang));
        }
    }
    
    
    protected void addDateStamp(Element metaData, IngridHit hit) {
        addDateStamp(metaData, hit, null);
    }

    protected void addDateStamp(Element metaData, IngridHit hit, String ns) {
        String creationDate = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_MOD_TIME);
        if (IngridQueryHelper.hasValue(creationDate)) {
            String cswDate = Udk2CswDateFieldParser.instance().parse(creationDate);
            if (cswDate.indexOf("T") > -1) {
            	metaData.addElement(getNSElementName(ns, "dateStamp")).addElement("gco:DateTime").addText(Udk2CswDateFieldParser.instance().parse(creationDate));
            } else {
            	metaData.addElement(getNSElementName(ns, "dateStamp")).addElement("gco:Date").addText(Udk2CswDateFieldParser.instance().parse(creationDate));
            }
        }
    }
    
    protected void addCitationReferenceDates(Element parent, IngridHit hit, String ns) {
        // add dates (creation, revision etc.)
        String[] referenceDate = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_DATASET_REFERENCE_DATE);
        String[] referenceDateTypes = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_DATASET_REFERENCE_TYPE);
        if (referenceDate != null && referenceDate.length > 0) {
            for (int i = 0; i < referenceDate.length; i++) {
            	Element ciDate;
            	if (ns != null) {
            		ciDate = parent.addElement(ns + ":date").addElement("gmd:CI_Date");
            	} else {
            		ciDate = parent.addElement("date").addElement("gmd:CI_Date");
            	}
                String creationDate = referenceDate[i];
                if (creationDate != null && creationDate.length() > 0) {
                    String cswDate = Udk2CswDateFieldParser.instance().parse(creationDate);
                    if (cswDate.indexOf("T") > -1) {
                    	ciDate.addElement("gmd:date").addElement("gco:DateTime").addText(cswDate);
                    } else {
                    	ciDate.addElement("gmd:date").addElement("gco:Date").addText(cswDate);
                    }
                    
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
                    ciDate.addElement("gmd:dateType").addElement("gmd:CI_DateTypeCode").addAttribute("codeList",
                            "http://www.tc211.org/ISO19139/resources/codeList.xml?CI_DateTypeCode").addAttribute(
                            "codeListValue", codeListValue);
                    
                }
            }
        } else {
        	Element ciDate;
        	if (ns != null) {
        		ciDate = parent.addElement(ns + ":date").addElement("gmd:CI_Date");
        	} else {
        		ciDate = parent.addElement("date").addElement("gmd:CI_Date");
        	}
            String cswDate = null;
            String creationDate = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_MOD_TIME);
            if (IngridQueryHelper.hasValue(creationDate)) {
                cswDate = Udk2CswDateFieldParser.instance().parse(creationDate);
            } else {
                cswDate = DATE_TIME_FORMAT.format(new Date());
            }
            if (cswDate.indexOf("T") > -1) {
            	ciDate.addElement("gmd:date").addElement("gco:DateTime").addText(cswDate);
            } else {
            	ciDate.addElement("gmd:date").addElement("gco:Date").addText(cswDate);
            }
            ciDate.addElement("gmd:dateType").addElement("gmd:CI_DateTypeCode").addAttribute("codeList",
            "http://www.tc211.org/ISO19139/resources/codeList.xml?CI_DateTypeCode").addAttribute(
            "codeListValue", "publication");
        }
    }
    
    protected void addOperationMetadata(Element parent, IngridHit hit) {
        // operationMetadata
        Element svOperationMetadata = parent.addElement("srv:operationMetadata").addElement("srv:SV_OperationMetadata");
        // srv:operationMetadata -> srv:SV_OperationMetadata -> srv:operationName -> String
        String operationName = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_OPERATION_NAME);
        if (IngridQueryHelper.hasValue(operationName)) {
	        this.addGCOCharacterString(svOperationMetadata.addElement("srv:operationName"), operationName);
        }
        // srv:operationMetadata -> srv:SV_OperationMetadata -> srv:operationDescription -> String
        String operationDescription = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_OPERATION_DESCR); 
        if (IngridQueryHelper.hasValue(operationDescription)) {
            this.addGCOCharacterString(svOperationMetadata.addElement("srv:operationDescription"), operationDescription);
        }
        // srv:operationMetadata -> srv:SV_OperationMetadata ->(1:n) srv:DCP -> srv:SV_DCPList/@codeListValue
        String[] platforms = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_OP_PLATFORM);
        for (int i=0; i< platforms.length; i++) {
            svOperationMetadata.addElement("srv:DCP").addElement("srv:SV_DCPList")
                .addAttribute("codeList", "http://opengis.org/codelistRegistry?CSW_DCPCodeType")
                .addAttribute("codeList", platforms[i]);
        }
        // srv:operationMetadata -> srv:SV_OperationMetadata -> srv:invocationName -> String
        String invocationName = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_INVOCATION_NAME);
        if (IngridQueryHelper.hasValue(invocationName)) {
	        this.addGCOCharacterString(svOperationMetadata.addElement("srv:invocationName"), invocationName);
        }
        // srv:operationMetadata -> srv:SV_OperationMetadata ->(1:n) srv:connectPoint -> gmd:CI_OnlineResource
        String[] connectPoints = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_OP_CONNECT_POINT);
        for (int i=0; i< connectPoints.length; i++) {
            svOperationMetadata.addElement("srv:connectPoint")
                .addElement("gmd:CI_OnlineResource").addElement("gmd:linkage")
                    .addElement("gmd:URL").addText(connectPoints[i]);
        }
        // srv:paramaters
        String[] parameterNames = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_OP_PARAM_NAME);
        String[] parameterDirections = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_OP_PARAM_DIRECTION);
        String[] parameterDescriptions = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_OP_PARAM_DESCR);
        String[] parameterOptionality = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_OP_PARAM_OPTIONAL);
        String[] parameterRepeatability = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERV_OP_PARAM_REPEATABILITY);
        for (int i=0; i<parameterNames.length; i++) {
            Element parameters = svOperationMetadata.addElement("srv:parameters").addElement("SV_Parameter");
            // srv:operationMetadata -> srv:SV_OperationMetadata ->(1:n) srv:parameters -> srv:SV_Parameter -> srv:name -> gmd:MemberName -> gmd:aName -> gco:CharacterString
            this.addGCOCharacterString(parameters.addElement("srv:name").addElement("gmd:MemberName").addElement("gmd:aName"),parameterNames[i]); 
            // srv:operationMetadata -> srv:SV_OperationMetadata ->(1:n) srv:parameters -> srv:direction -> srv:SV_ParameterDirection -> (in|out|in/out)
            parameters.addElement("srv:direction").addElement("srv:SV_ParameterDirection").addText(parameterDirections[i]);
            // srv:operationMetadata -> srv:SV_OperationMetadata ->(1:n) srv:parameters -> srv:description -> gco:CharacterString
            this.addGCOCharacterString(parameters.addElement("srv:description"), parameterDescriptions[i]);
            // srv:operationMetadata -> srv:SV_OperationMetadata ->(1:n) srv:parameters -> srv:optionality -> gco:CharacterString
            this.addGCOCharacterString(parameters.addElement("srv:optionality"), parameterOptionality[i]);
            // srv:operationMetadata -> srv:SV_OperationMetadata ->(1:n) srv:parameters -> srv:repeatability -> gmd:Boolean
            this.addGCOBoolean(parameters.addElement("srv:repeatability"), (parameterRepeatability[i] !=null && parameterRepeatability[i].equals("1")));
        }
        
    }
    
    protected void addExtent (Element parent, IngridHit hit, String ns) {
        // extend
        Element exExent = parent.addElement(this.getNSElementName(ns, "extent")).addElement("gmd:EX_Extent");
        // T01_object.loc_descr MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/description
        String extDescription = IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_LOC_DESCR);
        if (IngridQueryHelper.hasValue(extDescription)) {
            super.addGCOCharacterString(exExent.addElement("gmd:description"), extDescription);
        }
        
        Element exVerticalExtent = exExent.addElement("gmd:verticalElement").addElement("gmd:EX_VerticalExtent");
        // T01_object.vertical_extent_minimum MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/verticalElement/EX_VerticalExtent.minimumValue
        super.addGCOReal(exVerticalExtent.addElement("gmd:minimumValue"), IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_VERTICAL_EXTENT_MINIMUM));
        // T01_object.vertical_extent_maximum MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/verticalElement/EX_VerticalExtent.maximumValue
        super.addGCOReal(exVerticalExtent.addElement("gmd:maximumValue"), IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_VERTICAL_EXTENT_MAXIMUM));
        
        // T01_object.vertical_extent_unit = Wert [Domain-ID Codelist 102] MD_Metadata/full:identificationInfo/MD_DataIdentification/extent/EX_Extent/verticalElement/EX_VerticalExtent/unitOfMeasure/UomLength/uomName/CharacterString
        String codeVal = "";
        try {
            Long code = Long.valueOf(IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_VERTICAL_EXTENT_UNIT));
            codeVal = UtilsUDKCodeLists.getCodeListEntryName(new Long(102), code, new Long(94));
        } catch (NumberFormatException e) {}
        super.addGCOCharacterString(exVerticalExtent.addElement("gmd:unitOfMeasure").addElement("gmd:UomLength").addElement("gmd:uomName"), codeVal);

        // T01_object.vertical_extent_vdatum = Wert [Domain-Id Codelist 101] MD_Metadata/gmd:identificationInfo/srv:CSW_ServiceIdentification/srv:extent/gmd:EX_Extent/verticalElement/EX_VerticalExtent/verticalDatum/gmd:RS_Identifier/code/gco:CharacterString
        codeVal = "";
        try {
            Long code = Long.valueOf(IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_VERTICAL_EXTENT_VDATUM));
            codeVal = UtilsUDKCodeLists.getCodeListEntryName(new Long(101), code, new Long(94));
        } catch (NumberFormatException e) {}
        super.addGCOCharacterString(exVerticalExtent.addElement("gmd:verticalDatum").addElement("gmd:RS_Identifier").addElement("gmd:code"), codeVal);
        
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
            Element timePeriod = exExent.addElement("gmd:temporalElement").addElement("gmd:EX_TemporalExtent").addElement("gmd:extent").addElement("gmd:TM_Primitive").addElement("gml:relatedTime").addElement("gml:TimePeriod");
            if (beginDate != null) {
                timePeriod.addElement("gml:beginPosition").addElement("gml:TimeInstant").addElement("gml:timePosition")
                    .addText(Udk2CswDateFieldParser.instance().parse(beginDate));
            }
            if (endDate != null) {
                timePeriod.addElement("gml:endPosition").addElement("gml:TimeInstant").addElement("gml:timePosition")
                    .addText(Udk2CswDateFieldParser.instance().parse(endDate));
            }
        }

        
        // ATTN, HIT_KEY_OBJECT_COORDINATES values are only valid for UDK5 based data models
        // They are never set in IGC catalog based data models
        String[] coordinatesBezug = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_COORDINATES_BEZUG);
        String[] coordinatesGeoX1 = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_COORDINATES_GEO_X1);
        String[] coordinatesGeoX2 = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_COORDINATES_GEO_X2);
        String[] coordinatesGeoY1 = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_COORDINATES_GEO_Y1);
        String[] coordinatesGeoY2 = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_COORDINATES_GEO_Y2);
        for (int i=0; i< coordinatesBezug.length; i++) {
            Element geographicElement = exExent.addElement("gmd:geographicElement");
            // T019_coordinates.bezug MD_Metadata/gmd:identificationInfo/srv:CSW_ServiceIdentification/srv:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicDescription/gmd:geographicIdentifier/gmd:RS_Identifier/code/gco:CharacterString
            if (coordinatesBezug[i]!= null && coordinatesBezug[i].length() > 0) {
            	super.addGCOCharacterString(geographicElement.addElement("gmd:EX_GeographicDescription").addElement("gmd:geographicIdentifier").addElement("gmd:MD_Identifier").addElement("gmd:code"), coordinatesBezug[i]);
            }
            geographicElement = exExent.addElement("gmd:geographicElement");
            Element exGeographicBoundingBox = geographicElement.addElement("gmd:EX_GeographicBoundingBox");
            // T019_coordinates.geo_x1 MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/geographicElement/EX_GeographicBoundingBox/westBoundLongitude/gmd:approximateLongitude
            exGeographicBoundingBox.addElement("gmd:westBoundLongitude").addElement("gmd:approximateLongitude").addText(coordinatesGeoX1[i].replaceAll(",", "."));
            // T019_coordinates.geo_x2 MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/geographicElement/EX_GeographicBoundingBox/eastBoundLongitude/gmd:approximateLongitude
            exGeographicBoundingBox.addElement("gmd:eastBoundLongitude").addElement("gmd:approximateLongitude").addText(coordinatesGeoX2[i].replaceAll(",", "."));
            // T019_coordinates.geo_y2 MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/geographicElement/EX_GeographicBoundingBox/southBoundLatitude/gmd:approximateLatitude
            exGeographicBoundingBox.addElement("gmd:southBoundLatitude").addElement("gmd:approximateLatitude").addText(coordinatesGeoY1[i].replaceAll(",", "."));
            // T019_coordinates.geo_y1 MD_Metadata/identificationInfo/MD_DataIdentification/extent/EX_Extent/geographicElement/EX_GeographicBoundingBox/northBoundLatitude/gmd:approximateLatitude
            exGeographicBoundingBox.addElement("gmd:northBoundLatitude").addElement("gmd:approximateLatitude").addText(coordinatesGeoY2[i].replaceAll(",", "."));
        }

        String[] stTownship = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_ST_BBOX_LOC_TOWN_NO);
        String[] stTownshipNames = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_ST_TOWNSHIP_TOWNSHIP);
        String[] stBoxX1 = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_ST_BOX_X1);
        String[] stBoxX2 = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_ST_BOX_X2);
        String[] stBoxY1 = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_ST_BOX_Y1);
        String[] stBoxY2 = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_ST_BOX_Y2);

        for (int i=0; i< stTownship.length; i++) {
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
