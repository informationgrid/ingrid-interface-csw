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
public class CSWBuilderMetadata_summary_DE_1_0_1 extends CSWBuilderMetadataCommon {

    private static Log log = LogFactory.getLog(CSWBuilderMetadata_summary_DE_1_0_1.class);

    public Element build() throws Exception {
        
        this.setNSPrefix("iso19115summary");

        // define used name spaces
        Namespace smXML = new Namespace("smXML", "http://metadata.dgiwg.org/smXML");
        Namespace iso19115summary = new Namespace("iso19115summary", "http://schemas.opengis.net/iso19115summary");
        Namespace iso19119 = new Namespace("iso19119", "http://schemas.opengis.net/iso19119");

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

        Element metaData = DocumentFactory.getInstance().createElement("iso19115summary:MD_Metadata",
        "http://schemas.opengis.net/iso19115summary");
        metaData.add(iso19115summary);
        metaData.add(smXML);
        metaData.add(iso19119);

        this.addFileIdentifier(metaData, objectId);
        this.addLanguage(metaData, hit);
        this.addHierarchyLevel(metaData.addElement("hierarchyLevel"), typeName);
        this.addContact(metaData, hit);
        this.addDateStamp(metaData, hit);
        if (typeName.equals("dataset")) {
            this.addIdentificationInfoDataset(metaData, hit);
        } else {
            this.addIdentificationInfoService(metaData, hit);
        }

        return metaData;

    }

    private void addCitation(Element parent, IngridHit hit) {
        Element ciCitation = parent.addElement("citation")
        .addElement("iso19115summary:CI_Citation");
        // add title
        this.addSMXMLCharacterString(ciCitation.addElement("title"), IngridQueryHelper.getDetailValueAsString(hit,
                IngridQueryHelper.HIT_KEY_OBJECT_TITLE));
        // add dates (creation, revision etc.)
        super.addCitationReferenceDates(ciCitation, hit);
    }
    
    private void addIdentificationInfoService(Element metaData, IngridHit hit) {
        Element svServiceIdentification = metaData.addElement("identificationInfo").addElement(
                "iso19115summary:SV_ServiceIdentification");

        // add citation construct
        this.addCitation(svServiceIdentification, hit);

        
        // add abstract
        this.addSMXMLCharacterString(svServiceIdentification.addElement("abstract"), IngridQueryHelper.getDetailValueAsString(hit,
                IngridQueryHelper.HIT_KEY_OBJECT_DESCR));

        // add resourceConstraint
        this.addSMXMLCharacterString(svServiceIdentification.addElement("resourceConstraints").addElement("smXML:MD_Constraints").addElement("smXML:useLimitation"), IngridQueryHelper.getDetailValueAsString(hit,
                IngridQueryHelper.HIT_KEY_OBJECT_AVAIL_ACCESS_NOTE));
        
        
        this.addSMXMLCharacterString(svServiceIdentification.addElement("serviceType"), IngridQueryHelper
                        .getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERVICE_TYPE));

        String[] serviceTypeVersions = IngridQueryHelper.getDetailValueAsArray(hit, IngridQueryHelper.HIT_KEY_OBJECT_SERVICE_TYPE_VERSION);
        if (serviceTypeVersions != null) {
            for (int i=0; i< serviceTypeVersions.length; i++) {
                this.addSMXMLCharacterString(svServiceIdentification.addElement("serviceTypeVersion"), serviceTypeVersions[i]);
            }
        }

/*        svServiceIdentification.addElement("iso19119:couplingType").addElement("iso19119:CSW_CouplingType")
        .addAttribute("codeList", "http://opengis.org/codelistRegistry?CSW_CouplingType").addAttribute(
                "codeListValue",
                IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_OBJECT_SPECIAL_REF));
*/        
        /*
         * Allgemeine ServiceIdentification Struktur, abgeleitet von GISTEC code
         * 
         * 
         * <iso19119:CSW_ServiceIdentification> <iso19119:provider>
         * <iso19119:SV_ServiceProvider> <iso19119:serviceContact>
         * CI_ResponsibleParty (siehe contact) <iso19119:providerName>
         * <iso19119:serviceType> (String) <iso19119:serviceTypeVersion> (1:n,
         * String) <iso19119:accessProperties> <smXML:MD_StandardOrderProcess>
         * <smXML:fees> <smXML:plannedAvailableDateTime> (dateTime)
         * <smXML:orderingInstructions> <smXML:turnaround>
         * <iso19119:restrictions> <smXML:MD_Constraints> <smXML:useLimitation>
         * (String) <iso19119:containsOperations> <iso19119:operationName>
         * <iso19119:DCP> <iso19119:operationDescription>
         * <iso19119:invocationName> <iso19119:parameters> <SV_Parameter>
         * <iso19119:name> <iso19119:direction> <iso19119:SV_ParameterDirection>
         * <iso19119:description> <iso19119:optionality>
         * <iso19119:repeatability> (Boolean) <iso19119:connectPoint>
         * <smXML:CI_OnlineResource> <smXML:linkage> <smXML:protocol>
         * <smXML:applicationProfile> <smXML:name> <smXML:description>
         * <smXML:function> <smXML:CI_OnLineFunctionCode> <xsl:attribute
         * name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?CI_OnLineFunctionCode</xsl:attribute>
         * <xsl:attribute name="codeListValue"> <xsl:value-of select="."/>
         * </xsl:attribute> <smXML:extent> <smXML:EX_Extent> <smXML:description>
         * <smXML:verticalElement> <smXML:EX_VerticalExtent>
         * <smXML:minimumValue> (real) <smXML:maximumValue> (real)
         * <smXML:unitOfMeasure> <smXML:UomLength> <smXML:verticalDatum>
         * <smXML:RS_Identifier> <smXML:code> (String) <smXML:codeSpace>
         * (String) <smXML:temporalElement> <smXML:EX_TemporalExtent>
         * <smXML:extent> <gml:TM_Primitive xsi:type="gml:TimePeriodType">
         * <gml:begin> <gml:TimeInstant> <gml:timePosition> (value) <gml:end>
         * <gml:TimeInstant> <gml:timePosition> (value)
         * <smXML:geographicElement> <smXML:EX_GeographicDescription>
         * <smXML:extentTypeCode> (Boolean) <smXML:geographicIdentifier>
         * <smXML:MD_Identifier> <smXML:code> (String) <smXML:geographicElement>
         * <smXML:EX_GeographicBoundingBox> <smXML:extentTypeCode> (Boolean)
         * <smXML:westBoundLongitude> <smXML:approximateLongitude> (value)
         * <smXML:eastBoundLongitude> <smXML:approximateLongitude> (value)
         * <smXML:southBoundLatitude> <smXML:approximateLongitude> (value)
         * <smXML:northBoundLatitude> <smXML:approximateLongitude> (value)
         * <iso19119:coupledResource> <iso19119:CSW_CoupledResource>
         * <iso19119:identifier> (String) <iso19119:operationName> (String)
         * <iso19119:couplingType> <iso19119:CSW_CouplingType> (value)
         * <xs:attribute name="codeList" type="xs:anyURI" use="required"/>
         * <xs:attribute name="codeListValue" type="xs:string" use="required"/>
         * <xs:attribute name="codeSpace" type="xs:string" use="optional"/>
         * 
         * 
         * 
         */
    }

    private void addIdentificationInfoDataset(Element metaData, IngridHit hit) {
        Element mdDataIdentification = metaData.addElement("identificationInfo").addElement(
                "iso19115summary:MD_DataIdentification");

        // add citation construct
        this.addCitation(mdDataIdentification, hit);

        // add abstract
        this.addSMXMLCharacterString(mdDataIdentification.addElement("abstract"), IngridQueryHelper.getDetailValueAsString(hit,
                IngridQueryHelper.HIT_KEY_OBJECT_DESCR));

        // add resourceConstraint
        this.addSMXMLCharacterString(mdDataIdentification.addElement("resourceConstraints").addElement("smXML:MD_Constraints").addElement("smXML:useLimitation"), IngridQueryHelper.getDetailValueAsString(hit,
                IngridQueryHelper.HIT_KEY_OBJECT_AVAIL_ACCESS_NOTE));
        
        // add pointOfContact
        // implemented only in full profile
        
        // add language
/*        String dataLang =  IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_DATA_LANGUAGE);
        if (dataLang.equals("121")) {
            dataLang = "de";
        } else {
            dataLang = "en";
        }
        this.addSMXMLCharacterString(mdDataIdentification.addElement("iso19115summary:language"), dataLang);
*/        
        
/*        Long code;
        try {
            code = Long.valueOf(IngridQueryHelper.getDetailValueAsString(hit, IngridQueryHelper.HIT_KEY_OBJECT_GEO_TOPIC_CATEGORY));
            String codeVal = UtilsUDKCodeLists.getCodeListEntryName(new Long(527), code, new Long(94));
            mdDataIdentification.addElement("iso19115summary:topicCategory").addElement("smXML:MD_TopicCategoryCode").addText(codeVal);
        } catch (NumberFormatException e) {}
*/        
    }

}
