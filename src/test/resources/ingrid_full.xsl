<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:smXML="http://metadata.dgiwg.org/smXML" xmlns:gml="http://www.opengis.net/gml" xmlns:iso19115brief="http://schemas.opengis.net/iso19115brief" xmlns:iso19115summary="http://schemas.opengis.net/iso19115summary" xmlns:iso19115full="http://schemas.opengis.net/iso19115full" xmlns:iso19119="http://schemas.opengis.net/iso19119" xmlns="http://www.opengis.net/cat/csw" version="1.0">
  <xsl:param name="elementSetName"/>
  <xsl:param name="typeName"/>
  <xsl:template match="/">
    <GetRecordsResponse>
      <RequestId></RequestId>
      <SearchStatus status="" timestamp=""></SearchStatus>
      <SearchResults resultSetId="" elementSet="" numberOfRecordsMatched="" numberOfRecordsReturned="">
        <xsl:apply-templates select="/IngridRecords"/>
      </SearchResults>
    </GetRecordsResponse>
  </xsl:template>
  <xsl:template match="/IngridRecords">
    <xsl:apply-templates select="iso19115full:MD_Metadata"/>
    <xsl:apply-templates select="iso19115summary:MD_Metadata"/>
    <xsl:apply-templates select="iso19115brief:MD_Metadata"/>
  </xsl:template>
  <xsl:template match="iso19115full:MD_Metadata">
    <iso19115full:MD_Metadata>
      <xsl:call-template name="Metadata"/>
    </iso19115full:MD_Metadata>
  </xsl:template>
  <xsl:template match="iso19115summary:MD_Metadata">
    <iso19115summary:MD_Metadata>
      <xsl:call-template name="Metadata"/>
    </iso19115summary:MD_Metadata>
  </xsl:template>
  <xsl:template match="iso19115brief:MD_Metadata">
    <iso19115brief:MD_Metadata>
      <xsl:call-template name="Metadata"/>
    </iso19115brief:MD_Metadata>
  </xsl:template>
  <xsl:template name="Metadata">
    <xsl:variable name="elementSetName" select="@elementSetName"/>
    <xsl:variable name="typeName" select="@typeName"/>
    <xsl:apply-templates select="t01_object.obj_id"/>
    <xsl:if test="($elementSetName='summary' or $elementSetName='full')">
      <xsl:apply-templates select="MAIN/MAINFEAT/MDLANG"/>
      <xsl:apply-templates select="MAIN/MAINFEAT/MDCHAR"/>
      <xsl:apply-templates select="t012_obj_obj/t012_obj_obj.object_from_id"/>
      <xsl:apply-templates select="HIERARCHY/HIERARCHYFEAT/MDHRLVNAME"/>
    </xsl:if>
    <xsl:apply-templates select="HIERARCHY/HIERARCHYFEAT/MDHRLV"/>
    <iso19115full:contact>
      <xsl:apply-templates select="t012_obj_adr/t02_address"/>
    </iso19115full:contact>
    <xsl:if test="($elementSetName='summary' or $elementSetName='full')">
      <xsl:apply-templates select="t01_object.mod_time"/>
      <xsl:apply-templates select="t01_object.metadata_standard_name"/>
      <xsl:apply-templates select="t01_object.metadata_standard_version"/>
    </xsl:if>
    <xsl:if test="($elementSetName='full')">
      <xsl:apply-templates select="dataSetURI"/>
    </xsl:if>
    <xsl:call-template name="identificationInfo">
      <xsl:with-param name="elementSetName" select="$elementSetName"/>
      <xsl:with-param name="typeName" select="$typeName"/>
    </xsl:call-template>
    <xsl:if test="($elementSetName='summary' or $elementSetName='full')">
      <xsl:apply-templates select="DQINFO/DQINFOFEAT">
        <xsl:with-param name="elementSetName" select="$elementSetName"/>
      </xsl:apply-templates>
    </xsl:if>
    <xsl:if test="($elementSetName='full')">
      <xsl:call-template name="metadataMaintenance"/>
      <xsl:call-template name="spatialRepresentationInfoVec"/>
      <xsl:call-template name="spatialRepresentationInfoGeorect"/>
      <xsl:call-template name="spatialRepresentationInfoGeoref"/>
    </xsl:if>
    <xsl:if test="($elementSetName='summary' or $elementSetName='full')">
      <xsl:apply-templates select="REFSYSINFO/REFSYSINFOFEAT"/>
    </xsl:if>
    <xsl:if test="($elementSetName='full')">
      <xsl:call-template name="contentInfoFetCatDesc"/>
      <xsl:call-template name="contentInfoImgDesc"/>
    </xsl:if>
    <xsl:if test="($elementSetName='summary' or $elementSetName='full')">
      <xsl:apply-templates select="DISTINFO/DISTINFOFEAT">
        <xsl:with-param name="elementSetName" select="$elementSetName"/>
      </xsl:apply-templates>
    </xsl:if>
    <xsl:if test="($elementSetName='full')">
      <xsl:call-template name="metadataExtensionInfo"/>
      <xsl:call-template name="applicationSchemaInfo"/>
      <!--xsl:call-template name="series"/>

                  <xsl:call-template name="describes"/>

                  <xsl:call-template name="propertyType"/>

                 <xsl:call-template name="featureAttributeMetadata"/-->
    </xsl:if>
    <!--xsl:call-template name="federatedCatalog"/-->
  </xsl:template>
  <xsl:template name="applicationSchemaInfo">
    <smXML:applicationSchemaInfo>
      <xsl:call-template name="MD_ApplicationSchemaInformation"/>
    </smXML:applicationSchemaInfo>
  </xsl:template>
  <xsl:template name="MD_ApplicationSchemaInformation">
    <smXML:MD_ApplicationSchemaInformation>
      <!--name CI_Citation-->
      <xsl:apply-templates select="asSchLang"/>
      <xsl:apply-templates select="asCstLang"/>
      <xsl:apply-templates select="asAscii"/>
      <!--graphicsFile Binary-->
      <!--softwareDevelopmentFile Binary-->
      <xsl:apply-templates select="asSwDevFiFt"/>
    </smXML:MD_ApplicationSchemaInformation>
  </xsl:template>
  <xsl:template match="asSwDevFiFt">
    <smXML:softwareDevelopmentFileFormat>
      <xsl:call-template name="CharacterString"/>
    </smXML:softwareDevelopmentFileFormat>
  </xsl:template>
  <xsl:template match="asAscii">
    <smXML:schemaAscii>
      <xsl:call-template name="CharacterString"/>
    </smXML:schemaAscii>
  </xsl:template>
  <xsl:template match="asCstLang">
    <smXML:constraintLanguage>
      <xsl:call-template name="CharacterString"/>
    </smXML:constraintLanguage>
  </xsl:template>
  <xsl:template match="asSchLang">
    <smXML:schemaLanguage>
      <xsl:call-template name="CharacterString"/>
    </smXML:schemaLanguage>
  </xsl:template>
  <xsl:template name="metadataExtensionInfo">
    <smXML:metadataExtensionInfo>
      <xsl:call-template name="MD_MetadataExtensionInformation"/>
    </smXML:metadataExtensionInfo>
  </xsl:template>
  <xsl:template name="MD_MetadataExtensionInformation">
    <smXML:MD_MetadataExtensionInformation>
      <!--extensionOnLineResource CI_OnlineResource-->
      <xsl:call-template name="extendedElementInformation"/>
    </smXML:MD_MetadataExtensionInformation>
  </xsl:template>
  <xsl:template name="extendedElementInformation">
    <smXML:extendedElementInformation>
      <xsl:call-template name="MD_ExtendedElementInformation"/>
    </smXML:extendedElementInformation>
  </xsl:template>
  <xsl:template name="MD_ExtendedElementInformation">
    <smXML:MD_ExtendedElementInformation>
      <xsl:apply-templates select="extEleName"/>
      <xsl:apply-templates select="extShortName"/>
      <xsl:apply-templates select="extEleDef"/>
      <xsl:apply-templates select="extEleOb"/>
      <xsl:apply-templates select="extEleCond"/>
      <xsl:apply-templates select="eleDataType"/>
      <xsl:apply-templates select="extEleMxOc"/>
      <xsl:apply-templates select="extEleDomVal"/>
      <xsl:apply-templates select="extEleParEnt"/>
      <xsl:apply-templates select="extEleRule"/>
      <xsl:apply-templates select="extEleRat"/>
      <!--source CI_ResponsibleParty-->
      <xsl:apply-templates select="extDomCode"/>
    </smXML:MD_ExtendedElementInformation>
  </xsl:template>
  <xsl:template match="extDomCode">
    <smXML:domainCode>
      <xsl:call-template name="nonNegativeInteger"/>
    </smXML:domainCode>
  </xsl:template>
  <xsl:template match="extEleRat">
    <smXML:rationale>
      <xsl:call-template name="CharacterString"/>
    </smXML:rationale>
  </xsl:template>
  <xsl:template match="extEleRule">
    <smXML:rule>
      <xsl:call-template name="CharacterString"/>
    </smXML:rule>
  </xsl:template>
  <xsl:template match="extEleParEnt">
    <smXML:parentEntity>
      <xsl:call-template name="CharacterString"/>
    </smXML:parentEntity>
  </xsl:template>
  <xsl:template match="extEleDomVal">
    <smXML:domainValue>
      <xsl:call-template name="CharacterString"/>
    </smXML:domainValue>
  </xsl:template>
  <xsl:template match="extEleMxOc">
    <smXML:maximumOccurrence>
      <xsl:call-template name="CharacterString"/>
    </smXML:maximumOccurrence>
  </xsl:template>
  <xsl:template match="eleDataType">
    <smXML:dataType>
      <xsl:call-template name="MD_DatatypeCode"/>
    </smXML:dataType>
  </xsl:template>
  <xsl:template name="MD_DatatypeCode">
    <smXML:MD_DatatypeCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_DatatypeCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </smXML:MD_DatatypeCode>
  </xsl:template>
  <xsl:template match="extEleCond">
    <smXML:condition>
      <xsl:call-template name="CharacterString"/>
    </smXML:condition>
  </xsl:template>
  <xsl:template match="extEleOb">
    <smXML:obligation>
      <xsl:call-template name="MD_ObligationCode"/>
    </smXML:obligation>
  </xsl:template>
  <xsl:template name="MD_ObligationCode">
    <smXML:MD_ObligationCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_ObligationCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </smXML:MD_ObligationCode>
  </xsl:template>
  <xsl:template match="extEleDef">
    <smXML:definition>
      <xsl:call-template name="CharacterString"/>
    </smXML:definition>
  </xsl:template>
  <xsl:template match="extShortName">
    <smXML:shortName>
      <xsl:call-template name="CharacterString"/>
    </smXML:shortName>
  </xsl:template>
  <xsl:template match="extEleName">
    <smXML:name>
      <xsl:call-template name="CharacterString"/>
    </smXML:name>
  </xsl:template>
  <xsl:template match="DISTINFO/DISTINFOFEAT">
    <xsl:param name="elementSetName"/>
    <smXML:distributionInfo>
      <xsl:call-template name="MD_Distribution">
        <xsl:with-param name="elementSetName" select="$elementSetName"/>
      </xsl:call-template>
    </smXML:distributionInfo>
  </xsl:template>
  <xsl:template name="MD_Distribution">
    <xsl:param name="elementSetName"/>
    <smXML:MD_Distribution>
      <xsl:call-template name="distributionFormat">
        <xsl:with-param name="elementSetName" select="$elementSetName"/>
      </xsl:call-template>
      <xsl:if test="($elementSetName='full')">
        <xsl:call-template name="distributor"/>
      </xsl:if>
      <xsl:call-template name="transferOptions">
        <xsl:with-param name="elementSetName" select="$elementSetName"/>
      </xsl:call-template>
    </smXML:MD_Distribution>
  </xsl:template>
  <xsl:template name="transferOptions">
    <xsl:param name="elementSetName"/>
    <smXML:transferOptions>
      <xsl:call-template name="MD_DigitalTransferOptions">
        <xsl:with-param name="elementSetName" select="$elementSetName"/>
      </xsl:call-template>
    </smXML:transferOptions>
  </xsl:template>
  <xsl:template name="MD_DigitalTransferOptions">
    <xsl:param name="elementSetName"/>
    <smXML:MD_DigitalTransferOptions>
      <xsl:if test="($elementSetName='full')">
        <xsl:apply-templates select="unitODist"/>
      </xsl:if>
      <xsl:call-template name="onLine"/>
      <xsl:if test="($elementSetName='full')">
        <xsl:call-template name="offLine"/>
        <xsl:apply-templates select="transSize"/>
      </xsl:if>
    </smXML:MD_DigitalTransferOptions>
  </xsl:template>
  <xsl:template name="offLine">
    <smXML:offLine>
      <xsl:call-template name="MD_Medium"/>
    </smXML:offLine>
  </xsl:template>
  <xsl:template name="MD_Medium">
    <smXML:MD_Medium>
      <xsl:apply-templates select="MEDNAME"/>
      <xsl:apply-templates select="medDenUnits"/>
      <xsl:apply-templates select="medFormat"/>
      <xsl:apply-templates select="medNote"/>
      <xsl:apply-templates select="medDensity"/>
      <xsl:apply-templates select="medVol"/>
    </smXML:MD_Medium>
  </xsl:template>
  <xsl:template match="medVol">
    <smXML:volumes>
      <xsl:call-template name="nonNegativeInteger"/>
    </smXML:volumes>
  </xsl:template>
  <xsl:template match="medDensity">
    <smXML:density>
      <xsl:call-template name="CharacterString"/>
    </smXML:density>
  </xsl:template>
  <xsl:template match="medNote">
    <smXML:mediumNote>
      <xsl:call-template name="CharacterString"/>
    </smXML:mediumNote>
  </xsl:template>
  <xsl:template match="medFormat">
    <smXML:mediumFormat>
      <xsl:call-template name="MD_MediumFormatCode"/>
    </smXML:mediumFormat>
  </xsl:template>
  <xsl:template name="MD_MediumFormatCode">
    <smXML:MD_MediumFormatCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_MediumFormatCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </smXML:MD_MediumFormatCode>
  </xsl:template>
  <xsl:template match="medDenUnits">
    <smXML:densityUnits>
      <xsl:call-template name="CharacterString"/>
    </smXML:densityUnits>
  </xsl:template>
  <xsl:template match="MEDNAME">
    <smXML:name>
      <xsl:call-template name="MD_MediumNameCode"/>
    </smXML:name>
  </xsl:template>
  <xsl:template name="MD_MediumNameCode">
    <smXML:MD_MediumNameCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_MediumNameCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </smXML:MD_MediumNameCode>
  </xsl:template>
  <xsl:template name="onLine">
    <smXML:onLine>
      <xsl:call-template name="CI_OnlineResource"/>
    </smXML:onLine>
  </xsl:template>
  <xsl:template match="transSize">
    <smXML:transferSize>
      <xsl:call-template name="CharacterString"/>
    </smXML:transferSize>
  </xsl:template>
  <xsl:template match="unitODist">
    <smXML:unitsOfDistribution>
      <xsl:call-template name="CharacterString"/>
    </smXML:unitsOfDistribution>
  </xsl:template>
  <xsl:template name="distributor">
    <smXML:distributor>
      <xsl:call-template name="MD_Distributor"/>
    </smXML:distributor>
  </xsl:template>
  <xsl:template name="MD_Distributor">
    <smXML:MD_Distributor>
      <!-- distributorContact CI_ResponsibleParty-->
      <!-- distributorFormat MD_Format-->
      <xsl:call-template name="distributionOrderProcess"/>
      <!--xsl:call-template name="distributorTransferOptions"/-->
    </smXML:MD_Distributor>
  </xsl:template>
  <xsl:template name="distributionOrderProcess">
    <smXML:distributionOrderProcess>
      <xsl:call-template name="MD_StandardOrderProcess"/>
    </smXML:distributionOrderProcess>
  </xsl:template>
  <xsl:template name="MD_StandardOrderProcess">
    <smXML:MD_StandardOrderProcess>
      <xsl:apply-templates select="RESFEES"/>
      <xsl:apply-templates select="planAvDtTm"/>
      <xsl:apply-templates select="ordInstr"/>
      <xsl:apply-templates select="ordTurn"/>
    </smXML:MD_StandardOrderProcess>
  </xsl:template>
  <xsl:template match="ordTurn">
    <smXML:turnaround>
      <xsl:call-template name="CharacterString"/>
    </smXML:turnaround>
  </xsl:template>
  <xsl:template match="ordInstr">
    <smXML:orderingInstructions>
      <xsl:call-template name="CharacterString"/>
    </smXML:orderingInstructions>
  </xsl:template>
  <xsl:template match="planAvDtTm">
    <smXML:plannedAvailableDateTime>
      <xsl:call-template name="DateTime"/>
    </smXML:plannedAvailableDateTime>
  </xsl:template>
  <xsl:template match="RESFEES">
    <smXML:fees>
      <xsl:call-template name="CharacterString"/>
    </smXML:fees>
  </xsl:template>
  <xsl:template name="distributionFormat">
    <smXML:distributionFormat>
      <xsl:call-template name="MD_Format"/>
    </smXML:distributionFormat>
  </xsl:template>
  <xsl:template name="contentInfoFetCatDesc">
    <smXML:contentInfo>
      <xsl:call-template name="MD_FeatureCatalogueDescription"/>
    </smXML:contentInfo>
  </xsl:template>
  <xsl:template name="MD_FeatureCatalogueDescription">
    <smXML:MD_FeatureCatalogueDescription>
      <xsl:apply-templates select="compCode"/>
      <xsl:apply-templates select="catLang"/>
      <xsl:apply-templates select="incWithDS"/>
      <!-- featureType (GenericName) -->
      <!-- feature Cat Cit (Citation) -->
    </smXML:MD_FeatureCatalogueDescription>
  </xsl:template>
  <xsl:template match="incWithDS">
    <smXML:includedWithDataset>
      <xsl:call-template name="Boolean"/>
    </smXML:includedWithDataset>
  </xsl:template>
  <xsl:template match="catLang">
    <smXML:language>
      <xsl:call-template name="CharacterString"/>
    </smXML:language>
  </xsl:template>
  <xsl:template match="compCode">
    <smXML:complianceCode>
      <xsl:call-template name="Boolean"/>
    </smXML:complianceCode>
  </xsl:template>
  <xsl:template name="contentInfoImgDesc">
    <smXML:contentInfo>
      <xsl:call-template name="MD_ImageDescription"/>
    </smXML:contentInfo>
  </xsl:template>
  <xsl:template name="MD_ImageDescription">
    <smXML:MD_ImageDescription>
      <xsl:call-template name="MD_CoverageDescription"/>
      <xsl:apply-templates select="illElevAng"/>
      <xsl:apply-templates select="imagCond"/>
      <!--imageQualityCode (MD_Identifier)-->
      <!--processingLevelCode (MD_Identifier)-->
      <xsl:apply-templates select="trianInd"/>
      <xsl:apply-templates select="radCalDatAv"/>
      <xsl:apply-templates select="camCalInAv"/>
      <xsl:apply-templates select="filmDistInAv"/>
      <xsl:apply-templates select="lensDistInAv"/>
      <xsl:apply-templates select="illAziAng"/>
      <xsl:apply-templates select="cloudCovPer"/>
      <xsl:apply-templates select="cmpGenQuan"/>
    </smXML:MD_ImageDescription>
  </xsl:template>
  <xsl:template match="cmpGenQuan">
    <smXML:compressionGenerationQuantity>
      <xsl:call-template name="nonNegativeInteger"/>
    </smXML:compressionGenerationQuantity>
  </xsl:template>
  <xsl:template match="cloudCovPer">
    <smXML:cloudCoverPercentage>
      <xsl:call-template name="nonNegativeReal"/>
    </smXML:cloudCoverPercentage>
  </xsl:template>
  <xsl:template match="illAziAng">
    <smXML:illuminationAzimuthAngle>
      <xsl:call-template name="nonNegativeReal"/>
    </smXML:illuminationAzimuthAngle>
  </xsl:template>
  <xsl:template match="lensDistInAv">
    <smXML:lensDistortionInformationAvailability>
      <xsl:call-template name="Boolean"/>
    </smXML:lensDistortionInformationAvailability>
  </xsl:template>
  <xsl:template match="filmDistInAv">
    <smXML:filmDistortionInformationAvailability>
      <xsl:call-template name="Boolean"/>
    </smXML:filmDistortionInformationAvailability>
  </xsl:template>
  <xsl:template match="camCalInAv">
    <smXML:cameraCalibrationInformationAvailability>
      <xsl:call-template name="Boolean"/>
    </smXML:cameraCalibrationInformationAvailability>
  </xsl:template>
  <xsl:template match="radCalDatAv">
    <smXML:radiometricCalibrationDataAvailability>
      <xsl:call-template name="Boolean"/>
    </smXML:radiometricCalibrationDataAvailability>
  </xsl:template>
  <xsl:template match="trianInd">
    <smXML:triangulationIndicator>
      <xsl:call-template name="Boolean"/>
    </smXML:triangulationIndicator>
  </xsl:template>
  <xsl:template match="imagCond">
    <smXML:imagingCondition>
      <xsl:call-template name="MD_ImagingConditionCode"/>
    </smXML:imagingCondition>
  </xsl:template>
  <xsl:template name="MD_ImagingConditionCode">
    <smXML:MD_ImagingConditionCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_ImagingConditionCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </smXML:MD_ImagingConditionCode>
  </xsl:template>
  <xsl:template match="illElevAng">
    <smXML:illuminationElevationAngle>
      <xsl:call-template name="Real"/>
    </smXML:illuminationElevationAngle>
  </xsl:template>
  <xsl:template name="MD_CoverageDescription">
    <!-- attributeDescription (RecordType)-->
    <xsl:apply-templates select="contentTyp"/>
    <xsl:call-template name="dimension"/>
  </xsl:template>
  <xsl:template name="dimension">
    <smXML:dimension>
      <xsl:call-template name="MD_Band"/>
    </smXML:dimension>
  </xsl:template>
  <xsl:template name="MD_Band">
    <smXML:MD_Band>
      <xsl:call-template name="MD_RangeDimension"/>
      <xsl:apply-templates select="contentTyp"/>
      <!-- units (UomLength)-->
      <xsl:apply-templates select="sclFac"/>
      <xsl:apply-templates select="offset"/>
      <xsl:apply-templates select="maxVal"/>
      <xsl:apply-templates select="minVal"/>
      <xsl:apply-templates select="pkResp"/>
      <xsl:apply-templates select="bitsPerVal"/>
      <xsl:apply-templates select="toneGrad"/>
    </smXML:MD_Band>
  </xsl:template>
  <xsl:template match="toneGrad">
    <smXML:toneGradation>
      <xsl:call-template name="nonNegativeInteger"/>
    </smXML:toneGradation>
  </xsl:template>
  <xsl:template match="bitsPerVal">
    <smXML:bitsPerValue>
      <xsl:call-template name="nonNegativeInteger"/>
    </smXML:bitsPerValue>
  </xsl:template>
  <xsl:template match="pkResp">
    <smXML:peakResponse>
      <xsl:call-template name="CharacterString"/>
    </smXML:peakResponse>
  </xsl:template>
  <xsl:template match="minVal">
    <smXML:minValue>
      <xsl:call-template name="CharacterString"/>
    </smXML:minValue>
  </xsl:template>
  <xsl:template match="maxVal">
    <smXML:maxValue>
      <xsl:call-template name="CharacterString"/>
    </smXML:maxValue>
  </xsl:template>
  <xsl:template match="offset">
    <smXML:offset>
      <xsl:call-template name="Real"/>
    </smXML:offset>
  </xsl:template>
  <xsl:template match="sclFac">
    <smXML:scaleFactor>
      <xsl:call-template name="Real"/>
    </smXML:scaleFactor>
  </xsl:template>
  <xsl:template name="MD_RangeDimension">
    <!-- sequenceIdentifier (Membername)-->
    <xsl:apply-templates select="dimDescrp"/>
  </xsl:template>
  <xsl:template match="dimDescrp">
    <smXML:descriptor>
      <xsl:call-template name="CharacterString"/>
    </smXML:descriptor>
  </xsl:template>
  <xsl:template match="contentTyp">
    <smXML:contentType>
      <xsl:call-template name="MD_CoverageContentTypeCode"/>
    </smXML:contentType>
  </xsl:template>
  <xsl:template name="MD_CoverageContentTypeCode">
    <smXML:MD_CoverageContentTypeCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_CoverageContentTypeCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </smXML:MD_CoverageContentTypeCode>
  </xsl:template>
  <xsl:template match="REFSYSINFO/REFSYSINFOFEAT">
    <smXML:referenceSystemInfo>
      <xsl:call-template name="MD_CRS"/>
    </smXML:referenceSystemInfo>
  </xsl:template>
  <xsl:template name="MD_ReferenceSystem">
    <!-- referencesys identifier-->
    <xsl:call-template name="RS_Identifier"/>
  </xsl:template>
  <xsl:template name="MD_CRS">
    <smXML:MD_CRS>
      <xsl:call-template name="MD_ReferenceSystem"/>
      <!--RS_Identfiers: projection, ellipsoid, datum-->
      <xsl:call-template name="projectionParameters"/>
      <xsl:call-template name="ellipsoidParameters"/>
    </smXML:MD_CRS>
  </xsl:template>
  <xsl:template name="ellipsoidParameters">
    <smXML:ellipsoidParameters>
      <xsl:call-template name="MD_EllipsoidParameters"/>
    </smXML:ellipsoidParameters>
  </xsl:template>
  <xsl:template name="MD_EllipsoidParameters">
    <smXML:MD_EllipsoidParameters>
      <!-- axisUnits UomLength-->
      <xsl:apply-templates select="semiMajAx"/>
      <xsl:apply-templates select="denFlatRat"/>
    </smXML:MD_EllipsoidParameters>
  </xsl:template>
  <xsl:template match="denFlatRat">
    <smXML:denominatorOfFlatteningRatio>
      <xsl:call-template name="CharacterString"/>
    </smXML:denominatorOfFlatteningRatio>
  </xsl:template>
  <xsl:template match="semiMajAx">
    <smXML:semiMajorAxis>
      <xsl:call-template name="CharacterString"/>
    </smXML:semiMajorAxis>
  </xsl:template>
  <xsl:template name="projectionParameters">
    <smXML:projectionParameters>
      <xsl:call-template name="MD_ProjectionParameters"/>
    </smXML:projectionParameters>
  </xsl:template>
  <xsl:template name="MD_ProjectionParameters">
    <smXML:MD_ProjectionParameters>
      <xsl:apply-templates select="zone"/>
      <!--falseEastingNorthingUnits UomLength-->
      <xsl:apply-templates select="stanParal"/>
      <xsl:apply-templates select="longCntMer"/>
      <xsl:apply-templates select="latProjOri"/>
      <xsl:apply-templates select="falEastng"/>
      <xsl:apply-templates select="falNorthng"/>
      <xsl:apply-templates select="sclFacEqu"/>
      <xsl:apply-templates select="hgtProsPt"/>
      <xsl:apply-templates select="longProjCnt"/>
      <xsl:apply-templates select="latProjCnt"/>
      <xsl:apply-templates select="sclFacCnt"/>
      <xsl:apply-templates select="stVrLongPl"/>
      <xsl:apply-templates select="sclFacPrOr"/>
      <xsl:call-template name="obliqueLineAzimuthParameter"/>
      <xsl:call-template name="obliqueLinePointParameter"/>
    </smXML:MD_ProjectionParameters>
  </xsl:template>
  <xsl:template name="obliqueLinePointParameter">
    <smXML:obliqueLinePointParameter>
      <xsl:call-template name="MD_ObliqueLinePoint"/>
    </smXML:obliqueLinePointParameter>
  </xsl:template>
  <xsl:template name="MD_ObliqueLinePoint">
    <smXML:MD_ObliqueLinePoint>
      <xsl:apply-templates select="obLineLat"/>
      <xsl:apply-templates select="obLineLong"/>
    </smXML:MD_ObliqueLinePoint>
  </xsl:template>
  <xsl:template match="obLineLat">
    <smXML:obliqueLineLatitude>
      <xsl:call-template name="realLatitude"/>
    </smXML:obliqueLineLatitude>
  </xsl:template>
  <xsl:template match="obLineLong">
    <smXML:obliqueLineLongitude>
      <xsl:call-template name="realLongitude"/>
    </smXML:obliqueLineLongitude>
  </xsl:template>
  <xsl:template name="obliqueLineAzimuthParameter">
    <smXML:obliqueLineAzimuthParameter>
      <xsl:call-template name="MD_ObliqueLineAzimuth"/>
    </smXML:obliqueLineAzimuthParameter>
  </xsl:template>
  <xsl:template name="MD_ObliqueLineAzimuth">
    <smXML:MD_ObliqueLineAzimuth>
      <xsl:apply-templates select="aziAngle"/>
      <xsl:apply-templates select="aziPtLong"/>
    </smXML:MD_ObliqueLineAzimuth>
  </xsl:template>
  <xsl:template match="aziPtLong">
    <smXML:azimuthMeasurePointLongitude>
      <xsl:call-template name="realLongitude"/>
    </smXML:azimuthMeasurePointLongitude>
  </xsl:template>
  <xsl:template match="aziAngle">
    <smXML:azimuthAngle>
      <xsl:call-template name="nonNegativeReal"/>
    </smXML:azimuthAngle>
  </xsl:template>
  <xsl:template match="sclFacPrOr">
    <smXML:scaleFactorAtProjectionOrigin>
      <xsl:call-template name="CharacterString"/>
    </smXML:scaleFactorAtProjectionOrigin>
  </xsl:template>
  <xsl:template match="stVrLongPl">
    <smXML:straightVerticalLongitudeFromPole>
      <xsl:call-template name="realLongitude"/>
    </smXML:straightVerticalLongitudeFromPole>
  </xsl:template>
  <xsl:template match="sclFacCnt">
    <smXML:scaleFactorAtCenterLine>
      <xsl:call-template name="CharacterString"/>
    </smXML:scaleFactorAtCenterLine>
  </xsl:template>
  <xsl:template match="latProjCnt">
    <smXML:latitudeOfProjectionCenter>
      <xsl:call-template name="realLatitude"/>
    </smXML:latitudeOfProjectionCenter>
  </xsl:template>
  <xsl:template match="longProjCnt">
    <smXML:longitudeOfProjectionCenter>
      <xsl:call-template name="realLongitude"/>
    </smXML:longitudeOfProjectionCenter>
  </xsl:template>
  <xsl:template match="hgtProsPt">
    <smXML:heightOfProspectivePointAboveSurface>
      <xsl:call-template name="CharacterString"/>
    </smXML:heightOfProspectivePointAboveSurface>
  </xsl:template>
  <xsl:template match="sclFacEqu">
    <smXML:scaleFactorAtEquator>
      <xsl:call-template name="CharacterString"/>
    </smXML:scaleFactorAtEquator>
  </xsl:template>
  <xsl:template match="falNorthng">
    <smXML:falseNorthing>
      <xsl:call-template name="nonNegativeReal"/>
    </smXML:falseNorthing>
  </xsl:template>
  <xsl:template match="falEastng">
    <smXML:falseEasting>
      <xsl:call-template name="nonNegativeReal"/>
    </smXML:falseEasting>
  </xsl:template>
  <xsl:template match="latProjOri">
    <smXML:latitudeOfProjectionOrigin>
      <xsl:call-template name="realLatitude"/>
    </smXML:latitudeOfProjectionOrigin>
  </xsl:template>
  <xsl:template match="longCntMer">
    <smXML:longitudeOfCentralMeridian>
      <xsl:call-template name="realLongitude"/>
    </smXML:longitudeOfCentralMeridian>
  </xsl:template>
  <xsl:template match="stanParal">
    <smXML:standardParallel>
      <xsl:call-template name="realLatitude"/>
    </smXML:standardParallel>
  </xsl:template>
  <xsl:template match="zone">
    <smXML:zone>
      <xsl:call-template name="Integer"/>
    </smXML:zone>
  </xsl:template>
  <xsl:template name="metadataMaintenance">
    <smXML:metadataMaintenance>


     </smXML:metadataMaintenance>
  </xsl:template>
  <xsl:template name="spatialRepresentationInfoGeoref">
    <smXML:spatialRepresentationInfo>
      <xsl:call-template name="MD_Georeferenceable"/>
    </smXML:spatialRepresentationInfo>
  </xsl:template>
  <xsl:template name="MD_Georeferenceable">
    <smXML:MD_Georeferenceable>
      <xsl:call-template name="MD_GridSpatialRepresentation"/>
      <xsl:apply-templates select="ctrlPtAv"/>
      <xsl:apply-templates select="orieParaAv"/>
      <xsl:apply-templates select="orieParaDs"/>
      <!--xsl:apply-templates select="georefPars"/-->
      <!--xsl:apply-templates select="ParaCit"/-->
    </smXML:MD_Georeferenceable>
  </xsl:template>
  <xsl:template match="orieParaDs">
    <smXML:orientationParameterDescription>
      <xsl:call-template name="CharacterString"/>
    </smXML:orientationParameterDescription>
  </xsl:template>
  <xsl:template match="orieParaAv">
    <smXML:orientationParameterAvailability>
      <xsl:call-template name="Boolean"/>
    </smXML:orientationParameterAvailability>
  </xsl:template>
  <xsl:template match="ctrlPtAv">
    <smXML:controlPointAvailability>
      <xsl:call-template name="Boolean"/>
    </smXML:controlPointAvailability>
  </xsl:template>
  <xsl:template name="MD_GridSpatialRepresentation">
    <!--xsl:apply-templates select="axDimProps"/-->
    <xsl:apply-templates select="cellGeo"/>
    <xsl:apply-templates select="tranParaAv"/>
    <xsl:apply-templates select="numDims"/>
  </xsl:template>
  <xsl:template match="numDims">
    <smXML:numberOfDimensions>
      <xsl:call-template name="CharacterString"/>
    </smXML:numberOfDimensions>
  </xsl:template>
  <xsl:template match="tranParaAv">
    <smXML:transformationParameterAvailability>
      <xsl:call-template name="Boolean"/>
    </smXML:transformationParameterAvailability>
  </xsl:template>
  <xsl:template match="cellGeo">
    <smXML:cellGeometry>
      <xsl:call-template name="MD_CellGeometryCode"/>
    </smXML:cellGeometry>
  </xsl:template>
  <xsl:template name="MD_CellGeometryCode">
    <smXML:MD_CellGeometryCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_CellGeometryCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </smXML:MD_CellGeometryCode>
  </xsl:template>
  <xsl:template name="spatialRepresentationInfoGeorect">
    <smXML:spatialRepresentationInfo>
      <xsl:call-template name="MD_Georectified"/>
    </smXML:spatialRepresentationInfo>
  </xsl:template>
  <xsl:template name="MD_Georectified">
    <smXML:MD_Georectified>
      <xsl:call-template name="MD_GridSpatialRepresentation"/>
      <xsl:apply-templates select="chkPtAv"/>
      <xsl:apply-templates select="chkPtDesc"/>
      <!--cornerPoints-->
      <!--centerPoint-->
      <xsl:apply-templates select="ptInPixel"/>
      <xsl:apply-templates select="transDimDesc"/>
      <xsl:apply-templates select="transDimMap"/>
    </smXML:MD_Georectified>
  </xsl:template>
  <xsl:template match="transDimMap">
    <smXML:transformationDimensionMapping>
      <xsl:call-template name="CharacterString"/>
    </smXML:transformationDimensionMapping>
  </xsl:template>
  <xsl:template match="transDimDesc">
    <smXML:transformationDimensionDescription>
      <xsl:call-template name="CharacterString"/>
    </smXML:transformationDimensionDescription>
  </xsl:template>
  <xsl:template match="ptInPixel">
    <smXML:pointInPixel>
      <xsl:call-template name="MD_PixelOrientationCode"/>
    </smXML:pointInPixel>
  </xsl:template>
  <xsl:template name="MD_PixelOrientationCode">
    <smXML:MD_PixelOrientationCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_PixelOrientationCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </smXML:MD_PixelOrientationCode>
  </xsl:template>
  <xsl:template match="chkPtDesc">
    <smXML:checkPointDescription>
      <xsl:call-template name="CharacterString"/>
    </smXML:checkPointDescription>
  </xsl:template>
  <xsl:template match="chkPtAv">
    <smXML:checkPointAvailability>
      <xsl:call-template name="Boolean"/>
    </smXML:checkPointAvailability>
  </xsl:template>
  <xsl:template name="spatialRepresentationInfoVec">
    <smXML:spatialRepresentationInfo>
      <xsl:call-template name="MD_VectorSpatialRepresentation"/>
    </smXML:spatialRepresentationInfo>
  </xsl:template>
  <xsl:template name="MD_VectorSpatialRepresentation">
    <smXML:MD_VectorSpatialRepresentation>
      <xsl:apply-templates select="topLvl"/>
      <xsl:call-template name="geometricObjects"/>
    </smXML:MD_VectorSpatialRepresentation>
  </xsl:template>
  <xsl:template name="geometricObjects">
    <smXML:geometricObjects>
      <xsl:call-template name="MD_GeometricObjects"/>
    </smXML:geometricObjects>
  </xsl:template>
  <xsl:template name="MD_GeometricObjects">
    <xsl:apply-templates select="geoObjTyp"/>
    <xsl:apply-templates select="geoObjCnt"/>
  </xsl:template>
  <xsl:template match="geoObjCnt">
    <smXML:geometricObjectCount>
      <xsl:call-template name="CharacterString"/>
    </smXML:geometricObjectCount>
  </xsl:template>
  <xsl:template match="geoObjTyp">
    <smXML:geometricObjectType>
      <xsl:call-template name="MD_GeometricObjectTypeCode"/>
    </smXML:geometricObjectType>
  </xsl:template>
  <xsl:template name="MD_GeometricObjectTypeCode">
    <smXML:MD_GeometricObjectTypeCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_GeometricObjectTypeCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </smXML:MD_GeometricObjectTypeCode>
  </xsl:template>
  <xsl:template match="topLvl">
    <smXML:topologyLevel>
      <xsl:call-template name="MD_TopologyLevelCode"/>
    </smXML:topologyLevel>
  </xsl:template>
  <xsl:template name="MD_TopologyLevelCode">
    <smXML:MD_TopologyLevelCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_TopologyLevelCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </smXML:MD_TopologyLevelCode>
  </xsl:template>
  <xsl:template match="dataSetURI">
    <smXML:dataSet>
      <xsl:call-template name="CharacterString"/>
    </smXML:dataSet>
  </xsl:template>
  <xsl:template match="DQINFO/DQINFOFEAT">
    <xsl:param name="elementSetName"/>
    <smXML:dataQualityInfo>
      <xsl:call-template name="DQ_DataQuality">
        <xsl:with-param name="elementSetName" select="$elementSetName"/>
      </xsl:call-template>
    </smXML:dataQualityInfo>
  </xsl:template>
  <xsl:template name="DQ_DataQuality">
    <xsl:param name="elementSetName"/>
    <smXML:DQ_DataQuality>
      <xsl:if test="($elementSetName='full')">
        <xsl:call-template name="scope"/>
      </xsl:if>
      <xsl:call-template name="lineage"/>
      <xsl:if test="($elementSetName='full')">
        <xsl:call-template name="report"/>
      </xsl:if>
    </smXML:DQ_DataQuality>
  </xsl:template>
  <xsl:template name="scope">
    <smXML:scope>
      <xsl:call-template name="DQ_Scope"/>
    </smXML:scope>
  </xsl:template>
  <xsl:template name="DQ_Scope">
    <smXML:DQ_Scope>
      <xsl:apply-templates select="scpLvl"/>
      <!-- extent/scpExt and levelDesc/scpLvlDesc ...-->
    </smXML:DQ_Scope>
  </xsl:template>
  <xsl:template name="lineage">
    <smXML:lineage>
      <xsl:call-template name="LI_Lineage"/>
    </smXML:lineage>
  </xsl:template>
  <xsl:template name="LI_Lineage">
    <smXML:LI_Lineage>
      <xsl:apply-templates select="STATEMENT"/>
      <xsl:call-template name="source"/>
      <xsl:call-template name="processStep"/>
    </smXML:LI_Lineage>
  </xsl:template>
  <xsl:template name="processStep">
    <smXML:processStep>
      <xsl:call-template name="LI_ProcessStep"/>
    </smXML:processStep>
  </xsl:template>
  <xsl:template name="LI_ProcessStep">
    <smXML:LI_ProcessStep>
      <xsl:apply-templates select="stepDesc"/>
      <xsl:apply-templates select="stepRat"/>
      <xsl:apply-templates select="stepDateTm"/>
      <!--resp party, source-->
    </smXML:LI_ProcessStep>
  </xsl:template>
  <xsl:template match="stepDateTm">
    <smXML:dateTime>
      <xsl:call-template name="DateTime"/>
    </smXML:dateTime>
  </xsl:template>
  <xsl:template match="stepRat">
    <smXML:rationale>
      <xsl:call-template name="CharacterString"/>
    </smXML:rationale>
  </xsl:template>
  <xsl:template match="stepDesc">
    <smXML:description>
      <xsl:call-template name="CharacterString"/>
    </smXML:description>
  </xsl:template>
  <xsl:template name="source">
    <smXML:source>
      <xsl:call-template name="LI_Source"/>
    </smXML:source>
  </xsl:template>
  <xsl:template name="LI_Source">
    <smXML:LI_Source>
      <xsl:apply-templates select="SRCDESC"/>
      <xsl:apply-templates select="SRCSCALE"/>
      <!--Ref sys, citation, extent, processStep-->
    </smXML:LI_Source>
  </xsl:template>
  <xsl:template match="SRCSCALE">
    <smXML:scaleDenominator>
      <xsl:call-template name="MD_RepresentativeFraction"/>
    </smXML:scaleDenominator>
  </xsl:template>
  <xsl:template match="SRCDESC">
    <smXML:description>
      <xsl:call-template name="CharacterString"/>
    </smXML:description>
  </xsl:template>
  <xsl:template match="STATEMENT">
    <smXML:statement>
      <xsl:call-template name="CharacterString"/>
    </smXML:statement>
  </xsl:template>
  <xsl:template name="report">
    <smXML:report>
      <!-- choose DQ_Element-->
      <xsl:call-template name="_DQ_Element"/>
    </smXML:report>
  </xsl:template>
  <!-- _DQ_Element is abstract; choose implementation!!-->
  <xsl:template name="_DQ_Element">
    <_DQ_Element>
      <xsl:apply-templates select="measName"/>
      <!-- MD_identifier-->
      <xsl:apply-templates select="measDesc"/>
      <xsl:apply-templates select="evalMethType"/>
      <xsl:apply-templates select="evalMethDesc"/>
      <!-- CI_Citation-->
      <xsl:apply-templates select="measDateTm"/>
      <xsl:call-template name="result"/>
    </_DQ_Element>
  </xsl:template>
  <xsl:template name="result">
    <smXML:result>
      <xsl:call-template name="DQ_ConformanceResult"/>
      <xsl:call-template name="DQ_QuantitativeResult"/>
    </smXML:result>
  </xsl:template>
  <xsl:template name="DQ_QuantitativeResult">
    <smXML:DQ_QuantitativeResult>
      <!-- valueType (RecordType)-->
      <!-- valueUnit  (UOM)-->
      <xsl:apply-templates select="errStat"/>
      <!-- value (Record)-->
    </smXML:DQ_QuantitativeResult>
  </xsl:template>
  <xsl:template match="errStat">
    <smXML:errorStatistic>
      <xsl:call-template name="CharacterString"/>
    </smXML:errorStatistic>
  </xsl:template>
  <xsl:template name="DQ_ConformanceResult">
    <smXML:DQ_ConformanceResult>
      <!-- CI_Citation-->
      <xsl:apply-templates select="conExpl"/>
      <xsl:apply-templates select="conPass"/>
    </smXML:DQ_ConformanceResult>
  </xsl:template>
  <xsl:template match="conPass">
    <smXML:pass>
      <xsl:call-template name="Boolean"/>
    </smXML:pass>
  </xsl:template>
  <xsl:template match="conExpl">
    <smXML:explanation>
      <xsl:call-template name="CharacterString"/>
    </smXML:explanation>
  </xsl:template>
  <xsl:template match="measDateTm">
    <smXML:dateTime>
      <xsl:call-template name="DateTime"/>
    </smXML:dateTime>
  </xsl:template>
  <xsl:template match="evalMethDesc">
    <smXML:evaluationMethodDescription>
      <xsl:call-template name="CharacterString"/>
    </smXML:evaluationMethodDescription>
  </xsl:template>
  <xsl:template match="evalMethType">
    <smXML:evaluationMethodType>
      <xsl:call-template name="DQ_EvaluationMethodTypeCode"/>
    </smXML:evaluationMethodType>
  </xsl:template>
  <xsl:template name="DQ_EvaluationMethodTypeCode">
    <smXML:DQ_EvaluationMethodTypeCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?DQ_EvaluationMethodTypeCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </smXML:DQ_EvaluationMethodTypeCode>
  </xsl:template>
  <xsl:template match="measDesc">
    <smXML:measureDescription>
      <xsl:call-template name="CharacterString"/>
    </smXML:measureDescription>
  </xsl:template>
  <xsl:template match="measName">
    <smXML:nameOfMeasure>
      <xsl:call-template name="CharacterString"/>
    </smXML:nameOfMeasure>
  </xsl:template>
  <xsl:template match="scpLvl">
    <smXML:level>
      <xsl:call-template name="MD_ScopeCode"/>
    </smXML:level>
  </xsl:template>
  <xsl:template match="t01_object.metadata_standard_version">
    <iso19115full:metadataStandardVersion>
      <xsl:call-template name="CharacterString"/>
    </iso19115full:metadataStandardVersion>
  </xsl:template>
  <xsl:template match="t01_object.metadata_standard_name">
    <iso19115full:metadataStandardName>
      <xsl:call-template name="CharacterString"/>
    </iso19115full:metadataStandardName>
  </xsl:template>
  <xsl:template match="HIERARCHY/HIERARCHYFEAT/MDHRLVNAME">
    <smXML:hierarchyLevelName>
      <xsl:call-template name="CharacterString"/>
    </smXML:hierarchyLevelName>
  </xsl:template>
  <xsl:template match="t012_obj_obj/t012_obj_obj.object_from_id">
    <xsl:if test="../t012_obj_obj.typ='0'">
	    <iso19115full:parentIdentifier>
	      <xsl:call-template name="CharacterString"/>
	    </iso19115full:parentIdentifier>
    </xsl:if>
  </xsl:template>
  <xsl:template match="t01_object.mod_time">
    <iso19115full:dateStamp>
      <xsl:call-template name="Date"/>
    </iso19115full:dateStamp>
  </xsl:template>
  <xsl:template name="federatedCatalog">
    <smXML:federatedCatalog>
      <xsl:call-template name="CI_OnlineResource"/>
    </smXML:federatedCatalog>
  </xsl:template>
  <xsl:template name="identificationInfo">
    <xsl:param name="elementSetName"/>
    <xsl:param name="typeName"/>
    <iso19115full:identificationInfo>
      <xsl:if test="($typeName='service')">
        <xsl:call-template name="CSW_ServiceIdentification">
          <xsl:with-param name="elementSetName" select="$elementSetName"/>
        </xsl:call-template>
      </xsl:if>
      <xsl:if test="($typeName='dataset')">
        <xsl:call-template name="MD_DataIdentification">
          <xsl:with-param name="elementSetName" select="$elementSetName"/>
        </xsl:call-template>
      </xsl:if>
    </iso19115full:identificationInfo>
  </xsl:template>
  <xsl:template name="MD_Identification">
    <xsl:param name="elementSetName"/>
    <xsl:call-template name="citation">
      <xsl:with-param name="elementSetName" select="$elementSetName"/>
    </xsl:call-template>
    <xsl:if test="($elementSetName='summary' or $elementSetName='full')">
      <xsl:apply-templates select="summary"/>
    </xsl:if>
    <xsl:if test="($elementSetName='full')">
      <xsl:apply-templates select="idPurp"/>
      <xsl:apply-templates select="idCredit"/>
      <xsl:apply-templates select="idStatus"/>
    </xsl:if>
    <xsl:if test="($elementSetName='summary' or $elementSetName='full')">
      <!-- POC-->
      <smXML:pointOfContact>
        <xsl:apply-templates select="t012_obj_adr/t02_address"/>
      </smXML:pointOfContact>
    </xsl:if>
  </xsl:template>
  <xsl:template name="CSW_ServiceIdentification">
    <xsl:param name="elementSetName"/>
    <iso19119:CSW_ServiceIdentification>
      <xsl:call-template name="SV_ServiceIdentification">
        <xsl:with-param name="elementSetName" select="$elementSetName"/>
      </xsl:call-template>
      <xsl:call-template name="extent"/>
      <iso19119:coupledResource>
        <xsl:call-template name="CSW_CoupledResource"/>
      </iso19119:coupledResource>
      <xsl:apply-templates select="SVSERVICEIDENT/SVSERVICEIDENTFEAT/COUPLINGTYPE"/>
    </iso19119:CSW_ServiceIdentification>
  </xsl:template>
  <xsl:template match="SVSERVICEIDENT/SVSERVICEIDENTFEAT/COUPLINGTYPE">
    <iso19119:couplingType>
      <xsl:call-template name="CSW_CouplingType"/>
    </iso19119:couplingType>
  </xsl:template>
  <xsl:template name="CSW_CouplingType">
    <iso19119:CSW_CouplingType>
      <xsl:value-of select="."/>
    </iso19119:CSW_CouplingType>
  </xsl:template>
  <xsl:template name="CSW_CoupledResource">
    <iso19119:CSW_CoupledResource>
      <xsl:apply-templates select="IDENTIFIER"/>
      <xsl:apply-templates select="OPERATIONNAME"/>
    </iso19119:CSW_CoupledResource>
  </xsl:template>
  <xsl:template match="IDENTIFIER">
    <iso19119:identifier>
      <xsl:call-template name="CharacterString"/>
    </iso19119:identifier>
  </xsl:template>
  <xsl:template name="SV_ServiceIdentification">
    <xsl:param name="elementSetName"/>
    <xsl:call-template name="MD_Identification">
      <xsl:with-param name="elementSetName" select="$elementSetName"/>
    </xsl:call-template>
    <xsl:apply-templates select="SVSERVICEPROVIDER/SVSERVICEPROVIDERFEAT"/>
    <xsl:apply-templates select="SVSERVICEIDENT/SVSERVICEIDENTFEAT"/>
    <xsl:apply-templates select="SVOPERATION/SVOPERATIONFEAT"/>
    <!--xsl:apply-templates select="operatesOn  MD_DataIdenfication"/-->
  </xsl:template>
  <xsl:template match="SVSERVICEIDENT/SVSERVICEIDENTFEAT">
    <xsl:apply-templates select="SERVICETYPE"/>
    <xsl:apply-templates select="SERVICETYPEVERSION"/>
    <xsl:call-template name="accessProperties"/>
    <xsl:call-template name="restrictions"/>
  </xsl:template>
  <xsl:template name="restrictions">
    <iso19119:restrictions>
      <xsl:call-template name="MD_Constraints"/>
    </iso19119:restrictions>
  </xsl:template>
  <xsl:template name="accessProperties">
    <iso19119:accessProperties>
      <xsl:call-template name="MD_StandardOrderProcess"/>
    </iso19119:accessProperties>
  </xsl:template>
  <xsl:template match="SVSERVICEPROVIDER/SVSERVICEPROVIDERFEAT">
    <iso19119:provider>
      <xsl:call-template name="SV_ServiceProvider"/>
    </iso19119:provider>
  </xsl:template>
  <xsl:template match="SVOPERATION/SVOPERATIONFEAT">
    <iso19119:containsOperations>
      <xsl:call-template name="SV_OperationMetadata"/>
    </iso19119:containsOperations>
  </xsl:template>
  <xsl:template name="SV_OperationMetadata">
    <xsl:apply-templates select="OPERATIONNAME"/>
    <xsl:apply-templates select="DCP"/>
    <xsl:apply-templates select="OPERATIONDESC"/>
    <xsl:apply-templates select="INVOCATIONNAME"/>
    <xsl:call-template name="parameters"/>
    <xsl:call-template name="connectPoint"/>
  </xsl:template>
  <xsl:template name="connectPoint">
    <iso19119:connectPoint>
      <xsl:call-template name="CI_OnlineResource"/>
    </iso19119:connectPoint>
  </xsl:template>
  <xsl:template name="parameters">
    <iso19119:parameters>
      <xsl:call-template name="SV_Parameter"/>
    </iso19119:parameters>
  </xsl:template>
  <xsl:template name="SV_Parameter">
    <SV_Parameter>
      <xsl:apply-templates select="PARANAME"/>
      <xsl:apply-templates select="PARADIRECTION"/>
      <xsl:apply-templates select="PARADESC"/>
      <xsl:apply-templates select="OPTIONALITY"/>
      <xsl:apply-templates select="REPEATABILITY"/>
    </SV_Parameter>
  </xsl:template>
  <xsl:template match="REPEATABILITY">
    <iso19119:repeatability>
      <xsl:call-template name="Boolean"/>
    </iso19119:repeatability>
  </xsl:template>
  <xsl:template match="OPTIONALITY">
    <iso19119:optionality>
      <xsl:call-template name="CharacterString"/>
    </iso19119:optionality>
  </xsl:template>
  <xsl:template match="PARADESC">
    <iso19119:description>
      <xsl:call-template name="CharacterString"/>
    </iso19119:description>
  </xsl:template>
  <xsl:template match="PARADIRECTION">
    <iso19119:direction>
      <xsl:call-template name="SV_ParameterDirection"/>
    </iso19119:direction>
  </xsl:template>
  <xsl:template name="SV_ParameterDirection">
    <iso19119:SV_ParameterDirection>
      <xsl:value-of select="."/>
    </iso19119:SV_ParameterDirection>
  </xsl:template>
  <xsl:template match="PARANAME">
    <iso19119:name>
      <xsl:call-template name="MemberName"/>
    </iso19119:name>
  </xsl:template>
  <xsl:template match="INVOCATIONNAME">
    <iso19119:invocationName>
      <xsl:call-template name="CharacterString"/>
    </iso19119:invocationName>
  </xsl:template>
  <xsl:template match="OPERATIONDESC">
    <iso19119:operationDescription>
      <xsl:call-template name="CharacterString"/>
    </iso19119:operationDescription>
  </xsl:template>
  <xsl:template match="DCP">
    <iso19119:DCP>
      <xsl:value-of select="."/>
    </iso19119:DCP>
  </xsl:template>
  <xsl:template match="OPERATIONNAME">
    <iso19119:operationName>
      <xsl:call-template name="CharacterString"/>
    </iso19119:operationName>
  </xsl:template>
  <xsl:template name="SV_ServiceProvider">
    <iso19119:SV_ServiceProvider>
      <xsl:call-template name="serviceContact"/>
      <xsl:apply-templates select="PROVIDERNAME"/>
    </iso19119:SV_ServiceProvider>
  </xsl:template>
  <xsl:template name="serviceContact">
    <iso19119:serviceContact>
      <xsl:call-template name="CI_ResponsibleParty"/>
    </iso19119:serviceContact>
  </xsl:template>
  <xsl:template match="PROVIDERNAME">
    <iso19119:providerName>
      <xsl:call-template name="CharacterString"/>
    </iso19119:providerName>
  </xsl:template>
  <xsl:template match="SERVICETYPE">
    <iso19119:serviceType>
      <xsl:call-template name="LocalName"/>
    </iso19119:serviceType>
  </xsl:template>
  <xsl:template match="SERVICETYPEVERSION">
    <iso19119:serviceTypeVersion>
      <xsl:call-template name="CharacterString"/>
    </iso19119:serviceTypeVersion>
  </xsl:template>
  <xsl:template name="MD_DataIdentification">
    <xsl:param name="elementSetName"/>
    <smXML:MD_DataIdentification>
      <xsl:call-template name="MD_Identification">
        <xsl:with-param name="elementSetName" select="$elementSetName"/>
      </xsl:call-template>
      <xsl:if test="($elementSetName='summary' or $elementSetName='full')">
        <xsl:call-template name="resourceSpecificUsage"/>
        <xsl:call-template name="descriptiveKeywords"/>
        <xsl:call-template name="graphicOverview"/>
      </xsl:if>
      <xsl:if test="($elementSetName='summary' or $elementSetName='full')">
        <xsl:apply-templates select="RESCONSTLEG/RESCONSTLEGFEAT"/>
        <xsl:apply-templates select="RESCONSTSEC/RESCONSTSECFEAT"/>
      </xsl:if>
      <xsl:if test="($elementSetName='full')">
        <xsl:call-template name="resourceMaintenance"/>
        <xsl:call-template name="resourceFormat">
          <xsl:with-param name="elementSetName" select="$elementSetName"/>
        </xsl:call-template>
      </xsl:if>
      <xsl:if test="($elementSetName='summary' or $elementSetName='full')">
        <xsl:apply-templates select="spatRpType"/>
        <xsl:call-template name="spatialResolution"/>
        <xsl:apply-templates select="MAIN/MAINFEAT/DATALANG"/>
        <xsl:apply-templates select="MAIN/MAINFEAT/DATACHAR"/>
      </xsl:if>
      <xsl:apply-templates select="tpCat"/>
      <xsl:if test="($elementSetName='full')">
        <xsl:apply-templates select="envirDesc"/>
      </xsl:if>
      <xsl:call-template name="extent"/>
      <xsl:if test="($elementSetName='full')">
        <xsl:apply-templates select="suppInfo"/>
      </xsl:if>
    </smXML:MD_DataIdentification>
  </xsl:template>
  <xsl:template match="RESCONSTLEG/RESCONSTLEGFEAT">
    <xsl:call-template name="resourceConstraintsLegal"/>
  </xsl:template>
  <xsl:template match="RESCONSTSEC/RESCONSTSECFEAT">
    <xsl:call-template name="resourceConstraintsSecurity"/>
  </xsl:template>
  <xsl:template name="spatialResolution">
    <smXML:spatialResolution>
      <xsl:call-template name="MD_Resolution"/>
    </smXML:spatialResolution>
  </xsl:template>
  <xsl:template name="MD_Resolution">
    <smXML:MD_Resolution>
      <xsl:apply-templates select="t011_obj_geo_scale.t011_obj_geo_scale.scale"/>
      <xsl:apply-templates select="scaleDist"/>
    </smXML:MD_Resolution>
  </xsl:template>
  <xsl:template match="t011_obj_geo_scale.t011_obj_geo_scale.scale">
    <smXML:equivalentScale>
      <xsl:call-template name="MD_RepresentativeFraction"/>
    </smXML:equivalentScale>
  </xsl:template>
  <xsl:template match="scaleDist">
    <smXML:distance>
      <xsl:call-template name="Distance"/>
    </smXML:distance>
  </xsl:template>
  <xsl:template name="Distance">
    <smXML:Distance>
      <xsl:call-template name="Measure"/>
    </smXML:Distance>
  </xsl:template>
  <xsl:template name="Measure">
    <smXML:value>
      <xsl:call-template name="Decimal"/>
    </smXML:value>
    <!--uom>

 </uom-->
  </xsl:template>
  <xsl:template name="MD_RepresentativeFraction">
    <smXML:MD_RepresentativeFraction>
      <smXML:denominator>
        <xsl:call-template name="positiveInteger"/>
      </smXML:denominator>
    </smXML:MD_RepresentativeFraction>
  </xsl:template>
  <xsl:template name="resourceFormat">
    <xsl:param name="elementSetName"/>
    <smXML:resourceFormat>
      <xsl:call-template name="MD_Format">
        <xsl:with-param name="elementSetName" select="$elementSetName"/>
      </xsl:call-template>
    </smXML:resourceFormat>
  </xsl:template>
  <xsl:template name="MD_Format">
    <xsl:param name="elementSetName"/>
    <smXML:MD_Format>
      <xsl:apply-templates select="FORMATNAME"/>
      <xsl:apply-templates select="FORMATVER"/>
      <xsl:if test="($elementSetName='full')">
        <xsl:apply-templates select="formatAmdNum"/>
        <xsl:apply-templates select="formatSpec"/>
        <xsl:apply-templates select="fileDecmTech"/>
        <!--xsl:apply-templates select="formatDistributor"/-->
      </xsl:if>
    </smXML:MD_Format>
  </xsl:template>
  <xsl:template match="fileDecmTech">
    <smXML:fileDecompressionTechnique>
      <xsl:call-template name="CharacterString"/>
    </smXML:fileDecompressionTechnique>
  </xsl:template>
  <xsl:template match="formatSpec">
    <smXML:specification>
      <xsl:call-template name="CharacterString"/>
    </smXML:specification>
  </xsl:template>
  <xsl:template match="formatAmdNum">
    <smXML:amendmentNumber>
      <xsl:call-template name="CharacterString"/>
    </smXML:amendmentNumber>
  </xsl:template>
  <xsl:template match="FORMATNAME">
    <smXML:name>
      <xsl:call-template name="CharacterString"/>
    </smXML:name>
  </xsl:template>
  <xsl:template match="FORMATVER">
    <smXML:version>
      <xsl:call-template name="CharacterString"/>
    </smXML:version>
  </xsl:template>
  <xsl:template name="resourceMaintenance">
    <smXML:resourceMaintenance>
      <xsl:call-template name="MD_MaintenanceInformation"/>
    </smXML:resourceMaintenance>
  </xsl:template>
  <xsl:template name="MD_MaintenanceInformation">
    <smXML:MD_MaintenanceInformation>
      <xsl:apply-templates select="maintFreq"/>
      <xsl:apply-templates select="dateNext"/>
      <xsl:apply-templates select="usrDefFreq"/>
      <xsl:apply-templates select="maintScp"/>
      <!--xsl:apply-templates select="upScpDesc"/-->
      <xsl:apply-templates select="maintNote"/>
      <!--xsl:apply-templates select="contact"/-->
    </smXML:MD_MaintenanceInformation>
  </xsl:template>
  <xsl:template match="maintNote">
    <smXML:maintenanceNote>
      <xsl:call-template name="CharacterString"/>
    </smXML:maintenanceNote>
  </xsl:template>
  <xsl:template match="maintScp">
    <smXML:updateScope>
      <xsl:call-template name="MD_ScopeCode"/>
    </smXML:updateScope>
  </xsl:template>
  <xsl:template match="usrDefFreq">
    <smXML:userDefinedMaintenanceFrequency>
      <xsl:call-template name="TM_PeriodDuration"/>
    </smXML:userDefinedMaintenanceFrequency>
  </xsl:template>
  <xsl:template name="TM_PeriodDuration">
    <smXML:TM_PeriodDuration>
      <xsl:value-of select="."/>
    </smXML:TM_PeriodDuration>
  </xsl:template>
  <xsl:template match="dateNext">
    <smXML:dateOfNextUpdate>
      <xsl:call-template name="Date"/>
    </smXML:dateOfNextUpdate>
  </xsl:template>
  <xsl:template match="maintFreq">
    <smXML:maintenanceAndUpdateFrequency>
      <xsl:call-template name="MD_MaintenanceFrequencyCode"/>
    </smXML:maintenanceAndUpdateFrequency>
  </xsl:template>
  <xsl:template name="MD_MaintenanceFrequencyCode">
    <smXML:MD_MaintenanceFrequencyCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_MaintenanceFrequencyCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </smXML:MD_MaintenanceFrequencyCode>
  </xsl:template>
  <xsl:template name="resourceConstraintsLegal">
    <smXML:resourceConstraints>
      <xsl:call-template name="MD_LegalConstraints"/>
    </smXML:resourceConstraints>
  </xsl:template>
  <xsl:template name="resourceConstraintsSecurity">
    <smXML:resourceConstraints>
      <xsl:call-template name="MD_SecurityConstraints"/>
    </smXML:resourceConstraints>
  </xsl:template>
  <xsl:template name="MD_LegalConstraints">
    <smXML:MD_LegalConstraints>
      <xsl:apply-templates select="USELIMIT"/>
      <xsl:apply-templates select="accessConsts"/>
      <xsl:apply-templates select="useConsts"/>
      <xsl:apply-templates select="otherConsts"/>
    </smXML:MD_LegalConstraints>
  </xsl:template>
  <xsl:template name="MD_SecurityConstraints">
    <smXML:MD_SecurityConstraints>
      <xsl:apply-templates select="USELIMIT"/>
      <xsl:apply-templates select="class"/>
      <xsl:apply-templates select="userNote"/>
      <xsl:apply-templates select="classSys"/>
      <xsl:apply-templates select="handDesc"/>
    </smXML:MD_SecurityConstraints>
  </xsl:template>
  <xsl:template name="MD_Constraints">
    <smXML:MD_Constraints>
      <xsl:apply-templates select="USELIMIT"/>
    </smXML:MD_Constraints>
  </xsl:template>
  <xsl:template match="USELIMIT">
    <smXML:useLimitation>
      <xsl:call-template name="CharacterString"/>
    </smXML:useLimitation>
  </xsl:template>
  <xsl:template match="handDesc">
    <smXML:handlingDescription>
      <xsl:call-template name="CharacterString"/>
    </smXML:handlingDescription>
  </xsl:template>
  <xsl:template match="classSys">
    <smXML:classificationSystem>
      <xsl:call-template name="CharacterString"/>
    </smXML:classificationSystem>
  </xsl:template>
  <xsl:template match="userNote">
    <smXML:userNote>
      <xsl:call-template name="CharacterString"/>
    </smXML:userNote>
  </xsl:template>
  <xsl:template match="class">
    <smXML:classification>
      <xsl:call-template name="MD_ClassificationCode"/>
    </smXML:classification>
  </xsl:template>
  <xsl:template name="MD_ClassificationCode">
    <smXML:MD_ClassificationCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_ClassificationCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </smXML:MD_ClassificationCode>
  </xsl:template>
  <xsl:template match="otherConsts">
    <smXML:otherConstraints>
      <xsl:call-template name="CharacterString"/>
    </smXML:otherConstraints>
  </xsl:template>
  <xsl:template match="useConsts">
    <smXML:useConstraints>
      <xsl:call-template name="MD_RestrictionCode"/>
    </smXML:useConstraints>
  </xsl:template>
  <xsl:template match="accessConsts">
    <smXML:accessConstraints>
      <xsl:call-template name="MD_RestrictionCode"/>
    </smXML:accessConstraints>
  </xsl:template>
  <xsl:template name="MD_RestrictionCode">
    <smXML:MD_RestrictionCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_RestrictionCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </smXML:MD_RestrictionCode>
  </xsl:template>
  <xsl:template name="graphicOverview">
    <smXML:graphicOverview>
      <xsl:call-template name="MD_BrowseGraphic"/>
    </smXML:graphicOverview>
  </xsl:template>
  <xsl:template name="MD_BrowseGraphic">
    <smXML:MD_BrowseGraphic>
      <xsl:apply-templates select="bgFileName"/>
      <xsl:apply-templates select="bgFileDesc"/>
      <xsl:apply-templates select="bgFileType"/>
    </smXML:MD_BrowseGraphic>
  </xsl:template>
  <xsl:template match="bgFileName">
    <smXML:fileName>
      <xsl:call-template name="CharacterString"/>
    </smXML:fileName>
  </xsl:template>
  <xsl:template match="bgFileDesc">
    <smXML:fileDescription>
      <xsl:call-template name="CharacterString"/>
    </smXML:fileDescription>
  </xsl:template>
  <xsl:template match="bgFileType">
    <smXML:fileType>
      <xsl:call-template name="CharacterString"/>
    </smXML:fileType>
  </xsl:template>
  <xsl:template name="descriptiveKeywords">
    <smXML:descriptiveKeywords>
      <xsl:call-template name="MD_Keywords"/>
    </smXML:descriptiveKeywords>
  </xsl:template>
  <xsl:template name="MD_Keywords">
    <smXML:MD_Keywords>
      <xsl:apply-templates select="t04_search/t04_search.searchterm"/>
      <xsl:apply-templates select="keyTyp"/>
      <!--xsl:apply-templates select="thesaurusName"/-->
    </smXML:MD_Keywords>
  </xsl:template>
  <xsl:template match="t04_search/t04_search.searchterm">
    <smXML:keyword>
      <xsl:call-template name="CharacterString"/>
    </smXML:keyword>
  </xsl:template>
  <xsl:template match="keyTyp">
    <smXML:type>
      <xsl:call-template name="MD_KeywordTypeCode"/>
    </smXML:type>
  </xsl:template>
  <xsl:template name="MD_KeywordTypeCode">
    <smXML:MD_KeywordTypeCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_KeywordTypeCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </smXML:MD_KeywordTypeCode>
  </xsl:template>
  <xsl:template name="resourceSpecificUsage">
    <smXML:resourceSpecificUsage>
      <xsl:call-template name="MD_Usage"/>
    </smXML:resourceSpecificUsage>
  </xsl:template>
  <xsl:template name="MD_Usage">
    <smXML:MD_Usage>
      <xsl:apply-templates select="specUsage"/>
      <xsl:apply-templates select="usageDate"/>
      <xsl:apply-templates select="usrDetLim"/>
      <!--xsl:apply-templates select="userContactInfo"/-->
    </smXML:MD_Usage>
  </xsl:template>
  <xsl:template match="usrDetLim">
    <smXML:userDeterminedLimitations>
      <xsl:call-template name="CharacterString"/>
    </smXML:userDeterminedLimitations>
  </xsl:template>
  <xsl:template match="usageDate">
    <smXML:usageDateTime>
      <xsl:call-template name="DateTime"/>
    </smXML:usageDateTime>
  </xsl:template>
  <xsl:template match="specUsage">
    <smXML:specificUsage>
      <xsl:call-template name="CharacterString"/>
    </smXML:specificUsage>
  </xsl:template>
  <xsl:template match="envirDesc">
    <smXML:environmentalDescription>
      <xsl:call-template name="CharacterString"/>
    </smXML:environmentalDescription>
  </xsl:template>
  <xsl:template match="suppInfo">
    <smXML:supplementalInformation>
      <xsl:call-template name="CharacterString"/>
    </smXML:supplementalInformation>
  </xsl:template>
  <xsl:template match="spatRpType">
    <smXML:spatialRepresentationType>
      <xsl:call-template name="MD_SpatialRepresentationTypeCode"/>
    </smXML:spatialRepresentationType>
  </xsl:template>
  <xsl:template name="MD_SpatialRepresentationTypeCode">
    <smXML:MD_SpatialRepresentationTypeCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_SpatialRepresentationTypeCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </smXML:MD_SpatialRepresentationTypeCode>
  </xsl:template>
  <xsl:template match="idStatus">
    <smXML:status>
      <xsl:call-template name="MD_ProgressCode"/>
    </smXML:status>
  </xsl:template>
  <xsl:template name="MD_ProgressCode">
    <smXML:MD_ProgressCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_ProgressCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </smXML:MD_ProgressCode>
  </xsl:template>
  <xsl:template match="idCredit">
    <smXML:credit>
      <xsl:call-template name="CharacterString"/>
    </smXML:credit>
  </xsl:template>
  <xsl:template match="idPurp">
    <smXML:purpose>
      <xsl:call-template name="CharacterString"/>
    </smXML:purpose>
  </xsl:template>
  <xsl:template match="summary">
    <smXML:abstract>
      <xsl:call-template name="CharacterString"/>
    </smXML:abstract>
  </xsl:template>
  <xsl:template name="extent">
    <smXML:extent>
      <xsl:call-template name="EX_Extent"/>
    </smXML:extent>
  </xsl:template>
  <xsl:template name="EX_Extent">
    <smXML:EX_Extent>
      <xsl:apply-templates select="exDesc"/>
      <xsl:call-template name="verticalElement"/>
      <xsl:call-template name="temporalElement"/>
      <xsl:call-template name="geographicElements"/>
    </smXML:EX_Extent>
  </xsl:template>
  <xsl:template match="exDesc">
    <smXML:description>
      <xsl:call-template name="CharacterString"/>
    </smXML:description>
  </xsl:template>
  <xsl:template name="temporalElement">
    <smXML:temporalElement>
      <xsl:call-template name="EX_TemporalExtent"/>
    </smXML:temporalElement>
  </xsl:template>
  <xsl:template name="EX_TemporalExtent">
    <smXML:EX_TemporalExtent>
      <smXML:extent>
        <xsl:call-template name="TM_Primitive"/>
      </smXML:extent>
    </smXML:EX_TemporalExtent>
  </xsl:template>
  <xsl:template name="TM_Primitive">
    <gml:TM_Primitive xsi:type="gml:TimePeriodType">
      <xsl:apply-templates select="MAIN/MAINFEAT/BEGIN"/>
      <xsl:apply-templates select="MAIN/MAINFEAT/END"/>
    </gml:TM_Primitive>
  </xsl:template>
  <xsl:template match="MAIN/MAINFEAT/BEGIN">
    <gml:begin>
      <xsl:call-template name="TimeInstant"/>
    </gml:begin>
  </xsl:template>
  <xsl:template match="MAIN/MAINFEAT/END">
    <gml:end>
      <xsl:call-template name="TimeInstant"/>
    </gml:end>
  </xsl:template>
  <xsl:template name="TimeInstant">
    <gml:TimeInstant>
      <xsl:call-template name="timePosition"/>
    </gml:TimeInstant>
  </xsl:template>
  <xsl:template name="timePosition">
    <gml:timePosition>
      <xsl:value-of select="."/>
    </gml:timePosition>
  </xsl:template>
  <xsl:template name="geographicElements">
    <smXML:geographicElement>
      <xsl:call-template name="EX_GeographicDescription"/>
    </smXML:geographicElement>
    <smXML:geographicElement>
      <xsl:call-template name="EX_GeographicBoundingBox"/>
    </smXML:geographicElement>
    <!--
     <geographicElement>
       <xsl:call-template name="EX_BoundingPolygon"/>
   </geographicElement>
   -->
  </xsl:template>
  <!--
<xsl:template name="EX_BoundingPolygon">
    <EX_BoundingPolygon>
     <xsl:call-template name="_EX_GeographicExtent"/>
      <polygon>
      </polygon>
    </EX_BoundingPolygon>
</xsl:template>
 -->
  <xsl:template name="EX_GeographicDescription">
    <smXML:EX_GeographicDescription>
      <xsl:call-template name="_EX_GeographicExtent"/>
      <xsl:apply-templates select="AREA/AREAFEAT"/>
    </smXML:EX_GeographicDescription>
  </xsl:template>
  <xsl:template match="AREA/AREAFEAT">
    <smXML:geographicIdentifier>
      <xsl:call-template name="MD_Identifier"/>
    </smXML:geographicIdentifier>
  </xsl:template>
  <xsl:template name="EX_GeographicBoundingBox">
    <smXML:EX_GeographicBoundingBox>
      <xsl:call-template name="_EX_GeographicExtent"/>
      <xsl:apply-templates select="t011_township[1]/t01_st_bbox/x1"/>
      <xsl:apply-templates select="t011_township[1]/t01_st_bbox/x2"/>
      <xsl:apply-templates select="t011_township[1]/t01_st_bbox/y1"/>
      <xsl:apply-templates select="t011_township[1]/t01_st_bbox/y2"/>
    </smXML:EX_GeographicBoundingBox>
  </xsl:template>
  <xsl:template match="t011_township[1]/t01_st_bbox/y1">
    <smXML:southBoundLatitude>
      <xsl:call-template name="approximateLatitude"/>
    </smXML:southBoundLatitude>
  </xsl:template>
  <xsl:template match="t011_township[1]/t01_st_bbox/y2">
    <smXML:northBoundLatitude>
      <xsl:call-template name="approximateLatitude"/>
    </smXML:northBoundLatitude>
  </xsl:template>
  <xsl:template match="t011_township[1]/t01_st_bbox/x2">
    <smXML:eastBoundLongitude>
      <xsl:call-template name="approximateLongitude"/>
    </smXML:eastBoundLongitude>
  </xsl:template>
  <xsl:template match="t011_township[1]/t01_st_bbox/x1">
    <smXML:westBoundLongitude>
      <xsl:call-template name="approximateLongitude"/>
    </smXML:westBoundLongitude>
  </xsl:template>
  <xsl:template name="approximateLongitude">
    <smXML:approximateLongitude>
      <xsl:value-of select="."/>
    </smXML:approximateLongitude>
  </xsl:template>
  <xsl:template name="approximateLatitude">
    <smXML:approximateLatitude>
      <xsl:value-of select="."/>
    </smXML:approximateLatitude>
  </xsl:template>
  <xsl:template name="_EX_GeographicExtent">
    <xsl:apply-templates select="exTypeCode"/>
  </xsl:template>
  <xsl:template match="exTypeCode">
    <smXML:extentTypeCode>
      <xsl:call-template name="Boolean"/>
    </smXML:extentTypeCode>
  </xsl:template>
  <xsl:template name="verticalElement">
    <smXML:verticalElement>
      <xsl:call-template name="EX_VerticalExtent"/>
    </smXML:verticalElement>
  </xsl:template>
  <xsl:template name="EX_VerticalExtent">
    <smXML:EX_VerticalExtent>
      <xsl:apply-templates select="vertMinVal"/>
      <xsl:apply-templates select="vertMaxVal"/>
      <xsl:apply-templates select="vertUom"/>
      <xsl:apply-templates select="vertDatum"/>
    </smXML:EX_VerticalExtent>
  </xsl:template>
  <xsl:template match="vertMinVal">
    <smXML:minimumValue>
      <xsl:call-template name="Real"/>
    </smXML:minimumValue>
  </xsl:template>
  <xsl:template match="vertMaxVal">
    <smXML:maximumValue>
      <xsl:call-template name="Real"/>
    </smXML:maximumValue>
  </xsl:template>
  <xsl:template match="vertUom">
    <smXML:unitOfMeasure>
      <xsl:call-template name="UomLength"/>
    </smXML:unitOfMeasure>
  </xsl:template>
  <xsl:template name="UomLength">
    <smXML:UomLength>
      <xsl:call-template name="_UnitOfMeasure_Type"/>
    </smXML:UomLength>
  </xsl:template>
  <xsl:template name="_UnitOfMeasure_Type">
    <xsl:value-of select="."/>
  </xsl:template>
  <xsl:template match="vertDatum">
    <smXML:verticalDatum>
      <xsl:call-template name="SC_VerticalDatum"/>
    </smXML:verticalDatum>
  </xsl:template>
  <xsl:template name="SC_VerticalDatum">
    <xsl:call-template name="datumID"/>
  </xsl:template>
  <xsl:template name="datumID">
    <xsl:call-template name="RS_Identifier"/>
  </xsl:template>
  <xsl:template name="MD_Identifier">
    <smXML:MD_Identifier>
      <!-- element authority (CI_Citation)?-->
      <xsl:apply-templates select="IDENTCODE"/>
    </smXML:MD_Identifier>
  </xsl:template>
  <xsl:template name="RS_Identifier">
    <smXML:RS_Identifier>
      <xsl:apply-templates select="IDENTCODE"/>
      <xsl:apply-templates select="IDENTCODESPACE"/>
    </smXML:RS_Identifier>
  </xsl:template>
  <xsl:template match="IDENTCODE">
    <smXML:code>
      <xsl:call-template name="CharacterString"/>
    </smXML:code>
  </xsl:template>
  <xsl:template match="IDENTCODESPACE">
    <smXML:codeSpace>
      <xsl:call-template name="CharacterString"/>
    </smXML:codeSpace>
  </xsl:template>
  <xsl:template match="tpCat">
    <smXML:topicCategory>
      <xsl:call-template name="MD_TopicCategoryCode"/>
    </smXML:topicCategory>
  </xsl:template>
  <xsl:template name="MD_TopicCategoryCode">
    <smXML:MD_TopicCategoryCode>
      <xsl:value-of select="."/>
    </smXML:MD_TopicCategoryCode>
  </xsl:template>
  <xsl:template name="citation">
    <xsl:param name="elementSetName"/>
    <smXML:citation>
      <xsl:call-template name="CI_Citation">
        <xsl:with-param name="elementSetName" select="$elementSetName"/>
      </xsl:call-template>
    </smXML:citation>
  </xsl:template>
  <xsl:template name="CI_Citation">
    <xsl:param name="elementSetName"/>
    <ns1:CI_Citation xmlns:ns1="http://schemas.opengis.net/iso19115summary">
      <xsl:apply-templates select="title"/>
      <xsl:if test="($elementSetName='full')">
        <xsl:apply-templates select="t01_object.dataset_alternate_name"/>
      </xsl:if>
      <xsl:if test="($elementSetName='summary' or $elementSetName='full')">
        <xsl:call-template name="creationDate"/>
      </xsl:if>
      <xsl:if test="($elementSetName='full')">
        <xsl:apply-templates select="resEd"/>
        <xsl:apply-templates select="RESEDDATE/RESEDDATEFEAT"/>
      </xsl:if>
      <xsl:if test="($elementSetName='summary' or $elementSetName='full')">
        <xsl:apply-templates select="citId"/>
        <!-- problem: flat hierarchy, e.g. how to encode two different rpIndName?-->
        <!--xsl:apply-templates select="citedResponsibleParty"/-->
      </xsl:if>
      <xsl:if test="($elementSetName='full')">
        <xsl:apply-templates select="presForm"/>
        <xsl:call-template name="CI_Series"/>
        <xsl:apply-templates select="otherCitDet"/>
        <xsl:apply-templates select="collTitle"/>
        <xsl:apply-templates select="isbn"/>
        <xsl:apply-templates select="issn"/>
      </xsl:if>
    </ns1:CI_Citation>
  </xsl:template>
  <xsl:template match="RESEDDATE/RESEDDATEFEAT">
    <smXML:editionDate>
      <xsl:call-template name="CI_Date"/>
    </smXML:editionDate>
  </xsl:template>
  <xsl:template name="creationDate">
    <smXML:date>
      <xsl:call-template name="CI_Date"/>
    </smXML:date>
  </xsl:template>
  <xsl:template match="issn">
    <smXML:ISSN>
      <xsl:call-template name="CharacterString"/>
    </smXML:ISSN>
  </xsl:template>
  <xsl:template match="isbn">
    <smXML:ISBN>
      <xsl:call-template name="CharacterString"/>
    </smXML:ISBN>
  </xsl:template>
  <xsl:template match="collTitle">
    <smXML:collectiveTitle>
      <xsl:call-template name="CharacterString"/>
    </smXML:collectiveTitle>
  </xsl:template>
  <xsl:template match="otherCitDet">
    <smXML:otherCitationDetails>
      <xsl:call-template name="CharacterString"/>
    </smXML:otherCitationDetails>
  </xsl:template>
  <xsl:template name="CI_Series">
    <smXML:CI_Series>
      <xsl:apply-templates select="seriesName"/>
      <xsl:apply-templates select="issId"/>
      <xsl:apply-templates select="artPage"/>
    </smXML:CI_Series>
  </xsl:template>
  <xsl:template match="seriesName">
    <smXML:name>
      <xsl:call-template name="CharacterString"/>
    </smXML:name>
  </xsl:template>
  <xsl:template match="issId">
    <smXML:issueIdentification>
      <xsl:call-template name="CharacterString"/>
    </smXML:issueIdentification>
  </xsl:template>
  <xsl:template match="artPage">
    <smXML:page>
      <xsl:call-template name="CharacterString"/>
    </smXML:page>
  </xsl:template>
  <xsl:template match="presForm">
    <smXML:presentationForm>
      <xsl:call-template name="CI_PresentationFormCode"/>
    </smXML:presentationForm>
  </xsl:template>
  <xsl:template name="CI_PresentationFormCode">
    <smXML:CI_PresentationFormCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?CI_PresentationFormCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </smXML:CI_PresentationFormCode>
  </xsl:template>
  <xsl:template match="citId">
    <smXML:identifier>
      <xsl:call-template name="MD_Identifier"/>
    </smXML:identifier>
  </xsl:template>
  <xsl:template match="resEdDate">
    <smXML:editionDate>
      <xsl:call-template name="Date"/>
    </smXML:editionDate>
  </xsl:template>
  <xsl:template name="CI_Date">
    <smXML:CI_Date>
      <xsl:apply-templates select="t01_object.create_time"/>
      <!--xsl:apply-templates select="REFDATETYPE"/-->
      <xsl:call-template name="REFDATETYPE"/>
    </smXML:CI_Date>
  </xsl:template>
  <xsl:template match="t01_object.create_time">
    <smXML:date>
      <xsl:call-template name="Date"/>
    </smXML:date>
  </xsl:template>
  <!--xsl:template match="REFDATETYPE">
      <smXML:dateType>
         <xsl:call-template name="CI_DateTypeCode"/>
      </smXML:dateType>
 </xsl:template-->
  <xsl:template name="REFDATETYPE">
    <smXML:dateType>
      <xsl:call-template name="CI_DateTypeCode"/>
    </smXML:dateType>
  </xsl:template>
  <!--xsl:template name="CI_DateTypeCode">

        <smXML:CI_DateTypeCode>
             <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?CI_DateTypeCode</xsl:attribute>
                 <xsl:attribute name="codeListValue"><xsl:value-of select="."/></xsl:attribute>
	</smXML:CI_DateTypeCode>
</xsl:template-->
  <xsl:template name="CI_DateTypeCode">
    <smXML:CI_DateTypeCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?CI_DateTypeCode</xsl:attribute>
      <xsl:attribute name="codeListValue">creation</xsl:attribute>
    </smXML:CI_DateTypeCode>
  </xsl:template>
  <xsl:template match="resEd">
    <smXML:edition>
      <xsl:call-template name="CharacterString"/>
    </smXML:edition>
  </xsl:template>
  <xsl:template match="title">
    <smXML:title>
      <xsl:call-template name="CharacterString"/>
    </smXML:title>
  </xsl:template>
  <xsl:template match="t01_object.dataset_alternate_name">
    <smXML:alternateTitle>
      <xsl:call-template name="CharacterString"/>
    </smXML:alternateTitle>
  </xsl:template>
  <xsl:template match="t012_obj_adr/t02_address">
    <!--xsl:if test="../t012_obj_adr.typ='0'">


      <smXML:contact>
         <xsl:call-template name="CI_ResponsibleParty"/>
      </smXML:contact>

  </xsl:if-->
    <!--xsl:if test="../t012_obj_adr.typ='1'">


     <smXML:pointOfContact>
           <xsl:call-template name="CI_ResponsibleParty"/>
       </smXML:pointOfContact>

  </xsl:if-->
    <xsl:call-template name="CI_ResponsibleParty"/>
  </xsl:template>
  <xsl:template name="CI_ResponsibleParty">
    <smXML:CI_ResponsibleParty>
      <xsl:call-template name="RpIndname"/>
      <xsl:apply-templates select="t02_address.institution"/>
      <xsl:apply-templates select="t02_address.job"/>
      <xsl:call-template name="contactInfo"/>
      <xsl:apply-templates select="role"/>
    </smXML:CI_ResponsibleParty>
  </xsl:template>
  <xsl:template name="contactInfo">
    <smXML:contactInfo>
      <xsl:call-template name="CI_Contact"/>
    </smXML:contactInfo>
  </xsl:template>
  <xsl:template name="CI_Contact">
    <smXML:CI_Contact>
      <xsl:call-template name="phone"/>
      <xsl:call-template name="address"/>
      <xsl:call-template name="onlineResource"/>
      <xsl:apply-templates select="hoursOfService"/>
      <xsl:apply-templates select="contactInstructions"/>
    </smXML:CI_Contact>
  </xsl:template>
  <xsl:template match="hoursOfService">
    <smXML:hoursOfService>
      <xsl:call-template name="CharacterString"/>
    </smXML:hoursOfService>
  </xsl:template>
  <xsl:template match="contactInstructions">
    <smXML:contactInstructions>
      <xsl:call-template name="CharacterString"/>
    </smXML:contactInstructions>
  </xsl:template>
  <xsl:template name="onlineResource">
    <smXML:onlineResource>
      <xsl:call-template name="CI_OnlineResource"/>
    </smXML:onlineResource>
  </xsl:template>
  <xsl:template name="CI_OnlineResource">
    <smXML:CI_OnlineResource>
      <!-- linkage/URL-->
      <xsl:apply-templates select="t021_communication" mode="line_4"/>
      <xsl:apply-templates select="protocol"/>
      <xsl:apply-templates select="appProfile"/>
      <xsl:apply-templates select="orName"/>
      <xsl:apply-templates select="orDesc"/>
      <xsl:apply-templates select="ORFUNKT"/>
    </smXML:CI_OnlineResource>
  </xsl:template>
  <xsl:template match="orDesc">
    <smXML:description>
      <xsl:call-template name="CharacterString"/>
    </smXML:description>
  </xsl:template>
  <xsl:template match="orName">
    <smXML:name>
      <xsl:call-template name="CharacterString"/>
    </smXML:name>
  </xsl:template>
  <xsl:template match="appProfile">
    <smXML:applicationProfile>
      <xsl:call-template name="CharacterString"/>
    </smXML:applicationProfile>
  </xsl:template>
  <xsl:template match="protocol">
    <smXML:protocol>
      <xsl:call-template name="CharacterString"/>
    </smXML:protocol>
  </xsl:template>
  <xsl:template match="ORFUNKT">
    <smXML:function>
      <xsl:call-template name="CI_OnLineFunctionCode"/>
    </smXML:function>
  </xsl:template>
  <xsl:template name="CI_OnLineFunctionCode">
    <smXML:CI_OnLineFunctionCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?CI_OnLineFunctionCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </smXML:CI_OnLineFunctionCode>
  </xsl:template>
  <xsl:template name="address">
    <smXML:address>
      <xsl:call-template name="CI_Address"/>
    </smXML:address>
  </xsl:template>
  <xsl:template name="CI_Address">
    <smXML:CI_Address>
      <xsl:apply-templates select="t02_address.street"/>
      <xsl:apply-templates select="t02_address.city"/>
      <xsl:apply-templates select="adminArea"/>
      <xsl:apply-templates select="t02_address.postcode"/>
      <xsl:apply-templates select="t02_address.state_id"/>
      <xsl:apply-templates select="t021_communication" mode="line_2"/>
    </smXML:CI_Address>
  </xsl:template>
  <xsl:template match="t02_address.street">
    <smXML:deliveryPoint>
      <xsl:call-template name="CharacterString"/>
    </smXML:deliveryPoint>
  </xsl:template>
  <xsl:template match="t02_address.city">
    <smXML:city>
      <xsl:call-template name="CharacterString"/>
    </smXML:city>
  </xsl:template>
  <xsl:template match="adminArea">
    <smXML:administrativeArea>
      <xsl:call-template name="CharacterString"/>
    </smXML:administrativeArea>
  </xsl:template>
  <xsl:template match="t02_address.postcode">
    <smXML:postalCode>
      <xsl:call-template name="CharacterString"/>
    </smXML:postalCode>
  </xsl:template>
  <xsl:template match="t02_address.state_id">
    <smXML:country>
      <xsl:call-template name="CharacterString"/>
    </smXML:country>
  </xsl:template>
  <xsl:template name="phone">
    <smXML:phone>
      <xsl:call-template name="CI_Telephone"/>
    </smXML:phone>
  </xsl:template>
  <xsl:template name="CI_Telephone">
    <smXML:CI_Telephone>
      <xsl:apply-templates select="t021_communication" mode="line_13"/>
    </smXML:CI_Telephone>
  </xsl:template>
  <xsl:template match="t021_communication" mode="line_13">
    <xsl:if test="t021_communication.line='1'">
      <smXML:voice>
        <xsl:apply-templates select="t021_communication.comm_value" mode="charString"/>
      </smXML:voice>
    </xsl:if>
    <xsl:if test="t021_communication.line='3'">
      <smXML:facsimile>
        <xsl:apply-templates select="t021_communication.comm_value" mode="charString"/>
      </smXML:facsimile>
    </xsl:if>
  </xsl:template>
  <xsl:template match="t021_communication" mode="line_2">
    <xsl:if test="t021_communication.line='2'">
      <smXML:electronicMailAddress>
        <xsl:apply-templates select="t021_communication.comm_value" mode="charString"/>
      </smXML:electronicMailAddress>
    </xsl:if>
  </xsl:template>
  <xsl:template match="t021_communication" mode="line_4">
    <xsl:if test="t021_communication.line='4'">
      <smXML:linkage>
        <xsl:apply-templates select="t021_communication.comm_value" mode="url"/>
      </smXML:linkage>
    </xsl:if>
  </xsl:template>
  <xsl:template match="t021_communication.comm_value" mode="charString">
    <xsl:call-template name="CharacterString"/>
  </xsl:template>
  <xsl:template match="t021_communication.comm_value" mode="url">
    <xsl:call-template name="URL"/>
  </xsl:template>
  <xsl:template match="t02_address.job">
    <smXML:positionName>
      <xsl:call-template name="CharacterString"/>
    </smXML:positionName>
  </xsl:template>
  <xsl:template match="t02_address.institution">
    <smXML:organisationName>
      <xsl:call-template name="CharacterString"/>
    </smXML:organisationName>
  </xsl:template>
  <xsl:template name="RpIndname">
    <smXML:individualName>
      <smXML:CharacterString>
        <xsl:apply-templates select="t02_address.title"/>
        <!--  Leerzeichen  -->
        <xsl:value-of select="' '"/>
        <xsl:apply-templates select="t02_address.firstname"/>
        <!-- Leerzeichen  -->
        <xsl:value-of select="' '"/>
        <xsl:apply-templates select="t02_address.lastname"/>
      </smXML:CharacterString>
    </smXML:individualName>
  </xsl:template>
  <xsl:template match="t02_address.title">
    <xsl:value-of select="."/>
  </xsl:template>
  <xsl:template match="t02_address.firstname">
    <xsl:value-of select="."/>
  </xsl:template>
  <xsl:template match="t02_address.lastname">
    <xsl:value-of select="."/>
  </xsl:template>
  <xsl:template match="role">
    <smXML:role>
      <xsl:call-template name="CI_RoleCode"/>
    </smXML:role>
  </xsl:template>
  <xsl:template name="CI_RoleCode">
    <smXML:CI_RoleCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?CI_RoleCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </smXML:CI_RoleCode>
  </xsl:template>
  <xsl:template match="HIERARCHY/HIERARCHYFEAT/MDHRLV">
    <smXML:hierarchyLevel>
      <xsl:call-template name="MD_ScopeCode"/>
    </smXML:hierarchyLevel>
  </xsl:template>
  <xsl:template match="t01_object.obj_id">
    <iso19115full:fileIdentifier>
      <xsl:call-template name="CharacterString"/>
    </iso19115full:fileIdentifier>
  </xsl:template>
  <xsl:template match="MDLANG">
    <smXML:language>
      <xsl:call-template name="CharacterString"/>
    </smXML:language>
  </xsl:template>
  <xsl:template match="MAIN/MAINFEAT/DATALANG">
    <smXML:language>
      <xsl:call-template name="CharacterString"/>
    </smXML:language>
  </xsl:template>
  <xsl:template match="MDCHAR">
    <xsl:call-template name="characterSet"/>
  </xsl:template>
  <xsl:template match="DATACHAR">
    <xsl:call-template name="characterSet"/>
  </xsl:template>
  <xsl:template name="characterSet">
    <smXML:characterSet>
      <xsl:call-template name="MD_CharacterSetCode"/>
    </smXML:characterSet>
  </xsl:template>
  <xsl:template name="MD_CharacterSetCode">
    <smXML:MD_CharacterSetCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_CharacterSetCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </smXML:MD_CharacterSetCode>
  </xsl:template>
  <xsl:template name="MD_ScopeCode">
    <smXML:MD_ScopeCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_ScopeCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </smXML:MD_ScopeCode>
  </xsl:template>
  <xsl:template name="CharacterString">
    <smXML:CharacterString>
      <xsl:value-of select="."/>
    </smXML:CharacterString>
  </xsl:template>
  <xsl:template name="Boolean">
    <smXML:Boolean>
      <xsl:value-of select="."/>
    </smXML:Boolean>
  </xsl:template>
  <xsl:template name="Date">
    <smXML:Date>
      <!--format to YYYY-MM-DD-->
      <xsl:value-of select="concat( substring(. , 1, 4),   '-',   substring(. , 5, 2) ,  '-',  substring(. , 7, 2) )"/>
    </smXML:Date>
  </xsl:template>
  <xsl:template name="DateTime">
    <smXML:DateTime>
      <!--format to YYYY-MM-DDThh:mm:ss -->
      <xsl:value-of select="concat( substring(. , 1, 4),   '-',   substring(. , 5, 2) ,  '-',  substring(. , 7, 2), 'T', substring(. , 9, 2), ':', substring(. , 11, 2),  ':', substring(. , 13, 2) )"/>
    </smXML:DateTime>
  </xsl:template>
  <xsl:template name="URL">
    <smXML:URL>
      <xsl:value-of select="."/>
    </smXML:URL>
  </xsl:template>
  <xsl:template name="Integer">
    <smXML:Integer>
      <xsl:value-of select="."/>
    </smXML:Integer>
  </xsl:template>
  <xsl:template name="positiveInteger">
    <smXML:positiveInteger>
      <xsl:value-of select="."/>
    </smXML:positiveInteger>
  </xsl:template>
  <xsl:template name="nonNegativeInteger">
    <smXML:nonNegativeInteger>
      <xsl:value-of select="."/>
    </smXML:nonNegativeInteger>
  </xsl:template>
  <xsl:template name="Real">
    <smXML:Real>
      <xsl:value-of select="."/>
    </smXML:Real>
  </xsl:template>
  <xsl:template name="nonNegativeReal">
    <smXML:nonNegativeReal>
      <xsl:value-of select="."/>
    </smXML:nonNegativeReal>
  </xsl:template>
  <xsl:template name="realLatitude">
    <smXML:realLatitude>
      <xsl:value-of select="."/>
    </smXML:realLatitude>
  </xsl:template>
  <xsl:template name="realLongitude">
    <smXML:realLongitude>
      <xsl:value-of select="."/>
    </smXML:realLongitude>
  </xsl:template>
  <xsl:template name="Decimal">
    <smXML:Decimal>
      <xsl:value-of select="."/>
    </smXML:Decimal>
  </xsl:template>
  <xsl:template name="_Number">
    <xsl:value-of select="."/>
  </xsl:template>
  <xsl:template name="LocalName">
    <smXML:LocalName>
      <xsl:value-of select="."/>
    </smXML:LocalName>
  </xsl:template>
  <xsl:template name="ScopedName">
    <smXML:ScopedName>
      <xsl:value-of select="."/>
    </smXML:ScopedName>
  </xsl:template>
  <xsl:template name="MemberName">
    <smXML:MemberName>
      <smXML:aName>
        <xsl:call-template name="CharacterString"/>
      </smXML:aName>
      <smXML:attributeType>
           <!--xsl:call-template name="TypeName"/-->
      </smXML:attributeType>
    </smXML:MemberName>
  </xsl:template>
  <xsl:template name="TypeName">
    <smXML:aName>
      <xsl:call-template name="CharacterString"/>
    </smXML:aName>
  </xsl:template>
</xsl:stylesheet>
