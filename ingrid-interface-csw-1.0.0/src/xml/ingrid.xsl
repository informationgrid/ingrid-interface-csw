<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:smXML="http://metadata.dgiwg.org/smXML" xmlns:gml="http://www.opengis.net/gml" xmlns:iso19115brief="http://schemas.opengis.net/iso19115brief" xmlns:iso19115summary="http://schemas.opengis.net/iso19115summary" xmlns:iso19119="http://schemas.opengis.net/iso19119" xmlns="http://www.opengis.net/cat/csw" version="1.0">
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
    <xsl:apply-templates select="iso19115summary:MD_Metadata"/>
    <xsl:apply-templates select="iso19115brief:MD_Metadata"/>
  </xsl:template>
  <xsl:template match="iso19115summary:MD_Metadata">
    <MD_Metadata>
      <xsl:call-template name="Metadata"/>
    </MD_Metadata>
  </xsl:template>
  <xsl:template match="iso19115brief:MD_Metadata">
    <MD_Metadata>
      <xsl:call-template name="Metadata"/>
    </MD_Metadata>
  </xsl:template>
  <xsl:template name="Metadata">
    <xsl:variable name="elementSetName" select="@elementSetName"/>
    <xsl:variable name="typeName" select="@typeName"/>
    <xsl:apply-templates select="t01_object.obj_id"/>
    <xsl:if test="($elementSetName='summary' or $elementSetName='full')">
      <xsl:apply-templates select="MAIN/MAINFEAT/MDLANG"/>
      <xsl:apply-templates select="MAIN/MAINFEAT/MDCHAR"/>
      <xsl:apply-templates select="mdParentID"/>
      <xsl:apply-templates select="HIERARCHY/HIERARCHYFEAT/MDHRLVNAME"/>
    </xsl:if>
    <xsl:apply-templates select="HIERARCHY/HIERARCHYFEAT/MDHRLV"/>
    <contact>
      <xsl:apply-templates select="t012_obj_adr/t02_address"/>
    </contact>
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
    <applicationSchemaInfo>
      <xsl:call-template name="MD_ApplicationSchemaInformation"/>
    </applicationSchemaInfo>
  </xsl:template>
  <xsl:template name="MD_ApplicationSchemaInformation">
    <MD_ApplicationSchemaInformation>
      <!--name CI_Citation-->
      <xsl:apply-templates select="asSchLang"/>
      <xsl:apply-templates select="asCstLang"/>
      <xsl:apply-templates select="asAscii"/>
      <!--graphicsFile Binary-->
      <!--softwareDevelopmentFile Binary-->
      <xsl:apply-templates select="asSwDevFiFt"/>
    </MD_ApplicationSchemaInformation>
  </xsl:template>
  <xsl:template match="asSwDevFiFt">
    <softwareDevelopmentFileFormat>
      <xsl:call-template name="CharacterString"/>
    </softwareDevelopmentFileFormat>
  </xsl:template>
  <xsl:template match="asAscii">
    <schemaAscii>
      <xsl:call-template name="CharacterString"/>
    </schemaAscii>
  </xsl:template>
  <xsl:template match="asCstLang">
    <constraintLanguage>
      <xsl:call-template name="CharacterString"/>
    </constraintLanguage>
  </xsl:template>
  <xsl:template match="asSchLang">
    <schemaLanguage>
      <xsl:call-template name="CharacterString"/>
    </schemaLanguage>
  </xsl:template>
  <xsl:template name="metadataExtensionInfo">
    <metadataExtensionInfo>
      <xsl:call-template name="MD_MetadataExtensionInformation"/>
    </metadataExtensionInfo>
  </xsl:template>
  <xsl:template name="MD_MetadataExtensionInformation">
    <MD_MetadataExtensionInformation>
      <!--extensionOnLineResource CI_OnlineResource-->
      <xsl:call-template name="extendedElementInformation"/>
    </MD_MetadataExtensionInformation>
  </xsl:template>
  <xsl:template name="extendedElementInformation">
    <extendedElementInformation>
      <xsl:call-template name="MD_ExtendedElementInformation"/>
    </extendedElementInformation>
  </xsl:template>
  <xsl:template name="MD_ExtendedElementInformation">
    <MD_ExtendedElementInformation>
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
    </MD_ExtendedElementInformation>
  </xsl:template>
  <xsl:template match="extDomCode">
    <domainCode>
      <xsl:call-template name="nonNegativeInteger"/>
    </domainCode>
  </xsl:template>
  <xsl:template match="extEleRat">
    <rationale>
      <xsl:call-template name="CharacterString"/>
    </rationale>
  </xsl:template>
  <xsl:template match="extEleRule">
    <rule>
      <xsl:call-template name="CharacterString"/>
    </rule>
  </xsl:template>
  <xsl:template match="extEleParEnt">
    <parentEntity>
      <xsl:call-template name="CharacterString"/>
    </parentEntity>
  </xsl:template>
  <xsl:template match="extEleDomVal">
    <domainValue>
      <xsl:call-template name="CharacterString"/>
    </domainValue>
  </xsl:template>
  <xsl:template match="extEleMxOc">
    <maximumOccurrence>
      <xsl:call-template name="CharacterString"/>
    </maximumOccurrence>
  </xsl:template>
  <xsl:template match="eleDataType">
    <dataType>
      <xsl:call-template name="MD_DatatypeCode"/>
    </dataType>
  </xsl:template>
  <xsl:template name="MD_DatatypeCode">
    <MD_DatatypeCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_DatatypeCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </MD_DatatypeCode>
  </xsl:template>
  <xsl:template match="extEleCond">
    <condition>
      <xsl:call-template name="CharacterString"/>
    </condition>
  </xsl:template>
  <xsl:template match="extEleOb">
    <obligation>
      <xsl:call-template name="MD_ObligationCode"/>
    </obligation>
  </xsl:template>
  <xsl:template name="MD_ObligationCode">
    <MD_ObligationCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_ObligationCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </MD_ObligationCode>
  </xsl:template>
  <xsl:template match="extEleDef">
    <definition>
      <xsl:call-template name="CharacterString"/>
    </definition>
  </xsl:template>
  <xsl:template match="extShortName">
    <shortName>
      <xsl:call-template name="CharacterString"/>
    </shortName>
  </xsl:template>
  <xsl:template match="extEleName">
    <name>
      <xsl:call-template name="CharacterString"/>
    </name>
  </xsl:template>
  <xsl:template match="DISTINFO/DISTINFOFEAT">
    <xsl:param name="elementSetName"/>
    <distributionInfo>
      <xsl:call-template name="MD_Distribution">
        <xsl:with-param name="elementSetName" select="$elementSetName"/>
      </xsl:call-template>
    </distributionInfo>
  </xsl:template>
  <xsl:template name="MD_Distribution">
    <xsl:param name="elementSetName"/>
    <MD_Distribution>
      <xsl:call-template name="distributionFormat">
        <xsl:with-param name="elementSetName" select="$elementSetName"/>
      </xsl:call-template>
      <xsl:if test="($elementSetName='full')">
        <xsl:call-template name="distributor"/>
      </xsl:if>
      <xsl:call-template name="transferOptions">
        <xsl:with-param name="elementSetName" select="$elementSetName"/>
      </xsl:call-template>
    </MD_Distribution>
  </xsl:template>
  <xsl:template name="transferOptions">
    <xsl:param name="elementSetName"/>
    <transferOptions>
      <xsl:call-template name="MD_DigitalTransferOptions">
        <xsl:with-param name="elementSetName" select="$elementSetName"/>
      </xsl:call-template>
    </transferOptions>
  </xsl:template>
  <xsl:template name="MD_DigitalTransferOptions">
    <xsl:param name="elementSetName"/>
    <MD_DigitalTransferOptions>
      <xsl:if test="($elementSetName='full')">
        <xsl:apply-templates select="unitODist"/>
      </xsl:if>
      <xsl:call-template name="onLine"/>
      <xsl:if test="($elementSetName='full')">
        <xsl:call-template name="offLine"/>
        <xsl:apply-templates select="transSize"/>
      </xsl:if>
    </MD_DigitalTransferOptions>
  </xsl:template>
  <xsl:template name="offLine">
    <offLine>
      <xsl:call-template name="MD_Medium"/>
    </offLine>
  </xsl:template>
  <xsl:template name="MD_Medium">
    <MD_Medium>
      <xsl:apply-templates select="MEDNAME"/>
      <xsl:apply-templates select="medDenUnits"/>
      <xsl:apply-templates select="medFormat"/>
      <xsl:apply-templates select="medNote"/>
      <xsl:apply-templates select="medDensity"/>
      <xsl:apply-templates select="medVol"/>
    </MD_Medium>
  </xsl:template>
  <xsl:template match="medVol">
    <volumes>
      <xsl:call-template name="nonNegativeInteger"/>
    </volumes>
  </xsl:template>
  <xsl:template match="medDensity">
    <density>
      <xsl:call-template name="CharacterString"/>
    </density>
  </xsl:template>
  <xsl:template match="medNote">
    <mediumNote>
      <xsl:call-template name="CharacterString"/>
    </mediumNote>
  </xsl:template>
  <xsl:template match="medFormat">
    <mediumFormat>
      <xsl:call-template name="MD_MediumFormatCode"/>
    </mediumFormat>
  </xsl:template>
  <xsl:template name="MD_MediumFormatCode">
    <MD_MediumFormatCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_MediumFormatCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </MD_MediumFormatCode>
  </xsl:template>
  <xsl:template match="medDenUnits">
    <densityUnits>
      <xsl:call-template name="CharacterString"/>
    </densityUnits>
  </xsl:template>
  <xsl:template match="MEDNAME">
    <name>
      <xsl:call-template name="MD_MediumNameCode"/>
    </name>
  </xsl:template>
  <xsl:template name="MD_MediumNameCode">
    <MD_MediumNameCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_MediumNameCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </MD_MediumNameCode>
  </xsl:template>
  <xsl:template name="onLine">
    <onLine>
      <xsl:call-template name="CI_OnlineResource"/>
    </onLine>
  </xsl:template>
  <xsl:template match="transSize">
    <transferSize>
      <xsl:call-template name="CharacterString"/>
    </transferSize>
  </xsl:template>
  <xsl:template match="unitODist">
    <unitsOfDistribution>
      <xsl:call-template name="CharacterString"/>
    </unitsOfDistribution>
  </xsl:template>
  <xsl:template name="distributor">
    <distributor>
      <xsl:call-template name="MD_Distributor"/>
    </distributor>
  </xsl:template>
  <xsl:template name="MD_Distributor">
    <MD_Distributor>
      <!-- distributorContact CI_ResponsibleParty-->
      <!-- distributorFormat MD_Format-->
      <xsl:call-template name="distributionOrderProcess"/>
      <!--xsl:call-template name="distributorTransferOptions"/-->
    </MD_Distributor>
  </xsl:template>
  <xsl:template name="distributionOrderProcess">
    <distributionOrderProcess>
      <xsl:call-template name="MD_StandardOrderProcess"/>
    </distributionOrderProcess>
  </xsl:template>
  <xsl:template name="MD_StandardOrderProcess">
    <MD_StandardOrderProcess>
      <xsl:apply-templates select="RESFEES"/>
      <xsl:apply-templates select="planAvDtTm"/>
      <xsl:apply-templates select="ordInstr"/>
      <xsl:apply-templates select="ordTurn"/>
    </MD_StandardOrderProcess>
  </xsl:template>
  <xsl:template match="ordTurn">
    <turnaround>
      <xsl:call-template name="CharacterString"/>
    </turnaround>
  </xsl:template>
  <xsl:template match="ordInstr">
    <orderingInstructions>
      <xsl:call-template name="CharacterString"/>
    </orderingInstructions>
  </xsl:template>
  <xsl:template match="planAvDtTm">
    <plannedAvailableDateTime>
      <xsl:call-template name="DateTime"/>
    </plannedAvailableDateTime>
  </xsl:template>
  <xsl:template match="RESFEES">
    <fees>
      <xsl:call-template name="CharacterString"/>
    </fees>
  </xsl:template>
  <xsl:template name="distributionFormat">
    <distributionFormat>
      <xsl:call-template name="MD_Format"/>
    </distributionFormat>
  </xsl:template>
  <xsl:template name="contentInfoFetCatDesc">
    <contentInfo>
      <xsl:call-template name="MD_FeatureCatalogueDescription"/>
    </contentInfo>
  </xsl:template>
  <xsl:template name="MD_FeatureCatalogueDescription">
    <MD_FeatureCatalogueDescription>
      <xsl:apply-templates select="compCode"/>
      <xsl:apply-templates select="catLang"/>
      <xsl:apply-templates select="incWithDS"/>
      <!-- featureType (GenericName) -->
      <!-- feature Cat Cit (Citation) -->
    </MD_FeatureCatalogueDescription>
  </xsl:template>
  <xsl:template match="incWithDS">
    <includedWithDataset>
      <xsl:call-template name="Boolean"/>
    </includedWithDataset>
  </xsl:template>
  <xsl:template match="catLang">
    <language>
      <xsl:call-template name="CharacterString"/>
    </language>
  </xsl:template>
  <xsl:template match="compCode">
    <complianceCode>
      <xsl:call-template name="Boolean"/>
    </complianceCode>
  </xsl:template>
  <xsl:template name="contentInfoImgDesc">
    <contentInfo>
      <xsl:call-template name="MD_ImageDescription"/>
    </contentInfo>
  </xsl:template>
  <xsl:template name="MD_ImageDescription">
    <MD_ImageDescription>
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
    </MD_ImageDescription>
  </xsl:template>
  <xsl:template match="cmpGenQuan">
    <compressionGenerationQuantity>
      <xsl:call-template name="nonNegativeInteger"/>
    </compressionGenerationQuantity>
  </xsl:template>
  <xsl:template match="cloudCovPer">
    <cloudCoverPercentage>
      <xsl:call-template name="nonNegativeReal"/>
    </cloudCoverPercentage>
  </xsl:template>
  <xsl:template match="illAziAng">
    <illuminationAzimuthAngle>
      <xsl:call-template name="nonNegativeReal"/>
    </illuminationAzimuthAngle>
  </xsl:template>
  <xsl:template match="lensDistInAv">
    <lensDistortionInformationAvailability>
      <xsl:call-template name="Boolean"/>
    </lensDistortionInformationAvailability>
  </xsl:template>
  <xsl:template match="filmDistInAv">
    <filmDistortionInformationAvailability>
      <xsl:call-template name="Boolean"/>
    </filmDistortionInformationAvailability>
  </xsl:template>
  <xsl:template match="camCalInAv">
    <cameraCalibrationInformationAvailability>
      <xsl:call-template name="Boolean"/>
    </cameraCalibrationInformationAvailability>
  </xsl:template>
  <xsl:template match="radCalDatAv">
    <radiometricCalibrationDataAvailability>
      <xsl:call-template name="Boolean"/>
    </radiometricCalibrationDataAvailability>
  </xsl:template>
  <xsl:template match="trianInd">
    <triangulationIndicator>
      <xsl:call-template name="Boolean"/>
    </triangulationIndicator>
  </xsl:template>
  <xsl:template match="imagCond">
    <imagingCondition>
      <xsl:call-template name="MD_ImagingConditionCode"/>
    </imagingCondition>
  </xsl:template>
  <xsl:template name="MD_ImagingConditionCode">
    <MD_ImagingConditionCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_ImagingConditionCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </MD_ImagingConditionCode>
  </xsl:template>
  <xsl:template match="illElevAng">
    <illuminationElevationAngle>
      <xsl:call-template name="Real"/>
    </illuminationElevationAngle>
  </xsl:template>
  <xsl:template name="MD_CoverageDescription">
    <!-- attributeDescription (RecordType)-->
    <xsl:apply-templates select="contentTyp"/>
    <xsl:call-template name="dimension"/>
  </xsl:template>
  <xsl:template name="dimension">
    <dimension>
      <xsl:call-template name="MD_Band"/>
    </dimension>
  </xsl:template>
  <xsl:template name="MD_Band">
    <MD_Band>
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
    </MD_Band>
  </xsl:template>
  <xsl:template match="toneGrad">
    <toneGradation>
      <xsl:call-template name="nonNegativeInteger"/>
    </toneGradation>
  </xsl:template>
  <xsl:template match="bitsPerVal">
    <bitsPerValue>
      <xsl:call-template name="nonNegativeInteger"/>
    </bitsPerValue>
  </xsl:template>
  <xsl:template match="pkResp">
    <peakResponse>
      <xsl:call-template name="CharacterString"/>
    </peakResponse>
  </xsl:template>
  <xsl:template match="minVal">
    <minValue>
      <xsl:call-template name="CharacterString"/>
    </minValue>
  </xsl:template>
  <xsl:template match="maxVal">
    <maxValue>
      <xsl:call-template name="CharacterString"/>
    </maxValue>
  </xsl:template>
  <xsl:template match="offset">
    <offset>
      <xsl:call-template name="Real"/>
    </offset>
  </xsl:template>
  <xsl:template match="sclFac">
    <scaleFactor>
      <xsl:call-template name="Real"/>
    </scaleFactor>
  </xsl:template>
  <xsl:template name="MD_RangeDimension">
    <!-- sequenceIdentifier (Membername)-->
    <xsl:apply-templates select="dimDescrp"/>
  </xsl:template>
  <xsl:template match="dimDescrp">
    <descriptor>
      <xsl:call-template name="CharacterString"/>
    </descriptor>
  </xsl:template>
  <xsl:template match="contentTyp">
    <contentType>
      <xsl:call-template name="MD_CoverageContentTypeCode"/>
    </contentType>
  </xsl:template>
  <xsl:template name="MD_CoverageContentTypeCode">
    <MD_CoverageContentTypeCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_CoverageContentTypeCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </MD_CoverageContentTypeCode>
  </xsl:template>
  <xsl:template match="REFSYSINFO/REFSYSINFOFEAT">
    <referenceSystemInfo>
      <xsl:call-template name="MD_CRS"/>
    </referenceSystemInfo>
  </xsl:template>
  <xsl:template name="MD_ReferenceSystem">
    <!-- referencesys identifier-->
    <xsl:call-template name="RS_Identifier"/>
  </xsl:template>
  <xsl:template name="MD_CRS">
    <MD_CRS>
      <xsl:call-template name="MD_ReferenceSystem"/>
      <!--RS_Identfiers: projection, ellipsoid, datum-->
      <xsl:call-template name="projectionParameters"/>
      <xsl:call-template name="ellipsoidParameters"/>
    </MD_CRS>
  </xsl:template>
  <xsl:template name="ellipsoidParameters">
    <ellipsoidParameters>
      <xsl:call-template name="MD_EllipsoidParameters"/>
    </ellipsoidParameters>
  </xsl:template>
  <xsl:template name="MD_EllipsoidParameters">
    <MD_EllipsoidParameters>
      <!-- axisUnits UomLength-->
      <xsl:apply-templates select="semiMajAx"/>
      <xsl:apply-templates select="denFlatRat"/>
    </MD_EllipsoidParameters>
  </xsl:template>
  <xsl:template match="denFlatRat">
    <denominatorOfFlatteningRatio>
      <xsl:call-template name="CharacterString"/>
    </denominatorOfFlatteningRatio>
  </xsl:template>
  <xsl:template match="semiMajAx">
    <semiMajorAxis>
      <xsl:call-template name="CharacterString"/>
    </semiMajorAxis>
  </xsl:template>
  <xsl:template name="projectionParameters">
    <projectionParameters>
      <xsl:call-template name="MD_ProjectionParameters"/>
    </projectionParameters>
  </xsl:template>
  <xsl:template name="MD_ProjectionParameters">
    <MD_ProjectionParameters>
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
    </MD_ProjectionParameters>
  </xsl:template>
  <xsl:template name="obliqueLinePointParameter">
    <obliqueLinePointParameter>
      <xsl:call-template name="MD_ObliqueLinePoint"/>
    </obliqueLinePointParameter>
  </xsl:template>
  <xsl:template name="MD_ObliqueLinePoint">
    <MD_ObliqueLinePoint>
      <xsl:apply-templates select="obLineLat"/>
      <xsl:apply-templates select="obLineLong"/>
    </MD_ObliqueLinePoint>
  </xsl:template>
  <xsl:template match="obLineLat">
    <obliqueLineLatitude>
      <xsl:call-template name="realLatitude"/>
    </obliqueLineLatitude>
  </xsl:template>
  <xsl:template match="obLineLong">
    <obliqueLineLongitude>
      <xsl:call-template name="realLongitude"/>
    </obliqueLineLongitude>
  </xsl:template>
  <xsl:template name="obliqueLineAzimuthParameter">
    <obliqueLineAzimuthParameter>
      <xsl:call-template name="MD_ObliqueLineAzimuth"/>
    </obliqueLineAzimuthParameter>
  </xsl:template>
  <xsl:template name="MD_ObliqueLineAzimuth">
    <MD_ObliqueLineAzimuth>
      <xsl:apply-templates select="aziAngle"/>
      <xsl:apply-templates select="aziPtLong"/>
    </MD_ObliqueLineAzimuth>
  </xsl:template>
  <xsl:template match="aziPtLong">
    <azimuthMeasurePointLongitude>
      <xsl:call-template name="realLongitude"/>
    </azimuthMeasurePointLongitude>
  </xsl:template>
  <xsl:template match="aziAngle">
    <azimuthAngle>
      <xsl:call-template name="nonNegativeReal"/>
    </azimuthAngle>
  </xsl:template>
  <xsl:template match="sclFacPrOr">
    <scaleFactorAtProjectionOrigin>
      <xsl:call-template name="CharacterString"/>
    </scaleFactorAtProjectionOrigin>
  </xsl:template>
  <xsl:template match="stVrLongPl">
    <straightVerticalLongitudeFromPole>
      <xsl:call-template name="realLongitude"/>
    </straightVerticalLongitudeFromPole>
  </xsl:template>
  <xsl:template match="sclFacCnt">
    <scaleFactorAtCenterLine>
      <xsl:call-template name="CharacterString"/>
    </scaleFactorAtCenterLine>
  </xsl:template>
  <xsl:template match="latProjCnt">
    <latitudeOfProjectionCenter>
      <xsl:call-template name="realLatitude"/>
    </latitudeOfProjectionCenter>
  </xsl:template>
  <xsl:template match="longProjCnt">
    <longitudeOfProjectionCenter>
      <xsl:call-template name="realLongitude"/>
    </longitudeOfProjectionCenter>
  </xsl:template>
  <xsl:template match="hgtProsPt">
    <heightOfProspectivePointAboveSurface>
      <xsl:call-template name="CharacterString"/>
    </heightOfProspectivePointAboveSurface>
  </xsl:template>
  <xsl:template match="sclFacEqu">
    <scaleFactorAtEquator>
      <xsl:call-template name="CharacterString"/>
    </scaleFactorAtEquator>
  </xsl:template>
  <xsl:template match="falNorthng">
    <falseNorthing>
      <xsl:call-template name="nonNegativeReal"/>
    </falseNorthing>
  </xsl:template>
  <xsl:template match="falEastng">
    <falseEasting>
      <xsl:call-template name="nonNegativeReal"/>
    </falseEasting>
  </xsl:template>
  <xsl:template match="latProjOri">
    <latitudeOfProjectionOrigin>
      <xsl:call-template name="realLatitude"/>
    </latitudeOfProjectionOrigin>
  </xsl:template>
  <xsl:template match="longCntMer">
    <longitudeOfCentralMeridian>
      <xsl:call-template name="realLongitude"/>
    </longitudeOfCentralMeridian>
  </xsl:template>
  <xsl:template match="stanParal">
    <standardParallel>
      <xsl:call-template name="realLatitude"/>
    </standardParallel>
  </xsl:template>
  <xsl:template match="zone">
    <zone>
      <xsl:call-template name="Integer"/>
    </zone>
  </xsl:template>
  <xsl:template name="metadataMaintenance">
    <metadataMaintenance>


     </metadataMaintenance>
  </xsl:template>
  <xsl:template name="spatialRepresentationInfoGeoref">
    <spatialRepresentationInfo>
      <xsl:call-template name="MD_Georeferenceable"/>
    </spatialRepresentationInfo>
  </xsl:template>
  <xsl:template name="MD_Georeferenceable">
    <MD_Georeferenceable>
      <xsl:call-template name="MD_GridSpatialRepresentation"/>
      <xsl:apply-templates select="ctrlPtAv"/>
      <xsl:apply-templates select="orieParaAv"/>
      <xsl:apply-templates select="orieParaDs"/>
      <!--xsl:apply-templates select="georefPars"/-->
      <!--xsl:apply-templates select="ParaCit"/-->
    </MD_Georeferenceable>
  </xsl:template>
  <xsl:template match="orieParaDs">
    <orientationParameterDescription>
      <xsl:call-template name="CharacterString"/>
    </orientationParameterDescription>
  </xsl:template>
  <xsl:template match="orieParaAv">
    <orientationParameterAvailability>
      <xsl:call-template name="Boolean"/>
    </orientationParameterAvailability>
  </xsl:template>
  <xsl:template match="ctrlPtAv">
    <controlPointAvailability>
      <xsl:call-template name="Boolean"/>
    </controlPointAvailability>
  </xsl:template>
  <xsl:template name="MD_GridSpatialRepresentation">
    <!--xsl:apply-templates select="axDimProps"/-->
    <xsl:apply-templates select="cellGeo"/>
    <xsl:apply-templates select="tranParaAv"/>
    <xsl:apply-templates select="numDims"/>
  </xsl:template>
  <xsl:template match="numDims">
    <numberOfDimensions>
      <xsl:call-template name="CharacterString"/>
    </numberOfDimensions>
  </xsl:template>
  <xsl:template match="tranParaAv">
    <transformationParameterAvailability>
      <xsl:call-template name="Boolean"/>
    </transformationParameterAvailability>
  </xsl:template>
  <xsl:template match="cellGeo">
    <cellGeometry>
      <xsl:call-template name="MD_CellGeometryCode"/>
    </cellGeometry>
  </xsl:template>
  <xsl:template name="MD_CellGeometryCode">
    <MD_CellGeometryCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_CellGeometryCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </MD_CellGeometryCode>
  </xsl:template>
  <xsl:template name="spatialRepresentationInfoGeorect">
    <spatialRepresentationInfo>
      <xsl:call-template name="MD_Georectified"/>
    </spatialRepresentationInfo>
  </xsl:template>
  <xsl:template name="MD_Georectified">
    <MD_Georectified>
      <xsl:call-template name="MD_GridSpatialRepresentation"/>
      <xsl:apply-templates select="chkPtAv"/>
      <xsl:apply-templates select="chkPtDesc"/>
      <!--cornerPoints-->
      <!--centerPoint-->
      <xsl:apply-templates select="ptInPixel"/>
      <xsl:apply-templates select="transDimDesc"/>
      <xsl:apply-templates select="transDimMap"/>
    </MD_Georectified>
  </xsl:template>
  <xsl:template match="transDimMap">
    <transformationDimensionMapping>
      <xsl:call-template name="CharacterString"/>
    </transformationDimensionMapping>
  </xsl:template>
  <xsl:template match="transDimDesc">
    <transformationDimensionDescription>
      <xsl:call-template name="CharacterString"/>
    </transformationDimensionDescription>
  </xsl:template>
  <xsl:template match="ptInPixel">
    <pointInPixel>
      <xsl:call-template name="MD_PixelOrientationCode"/>
    </pointInPixel>
  </xsl:template>
  <xsl:template name="MD_PixelOrientationCode">
    <MD_PixelOrientationCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_PixelOrientationCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </MD_PixelOrientationCode>
  </xsl:template>
  <xsl:template match="chkPtDesc">
    <checkPointDescription>
      <xsl:call-template name="CharacterString"/>
    </checkPointDescription>
  </xsl:template>
  <xsl:template match="chkPtAv">
    <checkPointAvailability>
      <xsl:call-template name="Boolean"/>
    </checkPointAvailability>
  </xsl:template>
  <xsl:template name="spatialRepresentationInfoVec">
    <spatialRepresentationInfo>
      <xsl:call-template name="MD_VectorSpatialRepresentation"/>
    </spatialRepresentationInfo>
  </xsl:template>
  <xsl:template name="MD_VectorSpatialRepresentation">
    <MD_VectorSpatialRepresentation>
      <xsl:apply-templates select="topLvl"/>
      <xsl:call-template name="geometricObjects"/>
    </MD_VectorSpatialRepresentation>
  </xsl:template>
  <xsl:template name="geometricObjects">
    <geometricObjects>
      <xsl:call-template name="MD_GeometricObjects"/>
    </geometricObjects>
  </xsl:template>
  <xsl:template name="MD_GeometricObjects">
    <xsl:apply-templates select="geoObjTyp"/>
    <xsl:apply-templates select="geoObjCnt"/>
  </xsl:template>
  <xsl:template match="geoObjCnt">
    <geometricObjectCount>
      <xsl:call-template name="CharacterString"/>
    </geometricObjectCount>
  </xsl:template>
  <xsl:template match="geoObjTyp">
    <geometricObjectType>
      <xsl:call-template name="MD_GeometricObjectTypeCode"/>
    </geometricObjectType>
  </xsl:template>
  <xsl:template name="MD_GeometricObjectTypeCode">
    <MD_GeometricObjectTypeCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_GeometricObjectTypeCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </MD_GeometricObjectTypeCode>
  </xsl:template>
  <xsl:template match="topLvl">
    <topologyLevel>
      <xsl:call-template name="MD_TopologyLevelCode"/>
    </topologyLevel>
  </xsl:template>
  <xsl:template name="MD_TopologyLevelCode">
    <MD_TopologyLevelCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_TopologyLevelCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </MD_TopologyLevelCode>
  </xsl:template>
  <xsl:template match="dataSetURI">
    <dataSet>
      <xsl:call-template name="CharacterString"/>
    </dataSet>
  </xsl:template>
  <xsl:template match="DQINFO/DQINFOFEAT">
    <xsl:param name="elementSetName"/>
    <dataQualityInfo>
      <xsl:call-template name="DQ_DataQuality">
        <xsl:with-param name="elementSetName" select="$elementSetName"/>
      </xsl:call-template>
    </dataQualityInfo>
  </xsl:template>
  <xsl:template name="DQ_DataQuality">
    <xsl:param name="elementSetName"/>
    <DQ_DataQuality>
      <xsl:if test="($elementSetName='full')">
        <xsl:call-template name="scope"/>
      </xsl:if>
      <xsl:call-template name="lineage"/>
      <xsl:if test="($elementSetName='full')">
        <xsl:call-template name="report"/>
      </xsl:if>
    </DQ_DataQuality>
  </xsl:template>
  <xsl:template name="scope">
    <scope>
      <xsl:call-template name="DQ_Scope"/>
    </scope>
  </xsl:template>
  <xsl:template name="DQ_Scope">
    <DQ_Scope>
      <xsl:apply-templates select="scpLvl"/>
      <!-- extent/scpExt and levelDesc/scpLvlDesc ...-->
    </DQ_Scope>
  </xsl:template>
  <xsl:template name="lineage">
    <lineage>
      <xsl:call-template name="LI_Lineage"/>
    </lineage>
  </xsl:template>
  <xsl:template name="LI_Lineage">
    <LI_Lineage>
      <xsl:apply-templates select="STATEMENT"/>
      <xsl:call-template name="source"/>
      <xsl:call-template name="processStep"/>
    </LI_Lineage>
  </xsl:template>
  <xsl:template name="processStep">
    <processStep>
      <xsl:call-template name="LI_ProcessStep"/>
    </processStep>
  </xsl:template>
  <xsl:template name="LI_ProcessStep">
    <LI_ProcessStep>
      <xsl:apply-templates select="stepDesc"/>
      <xsl:apply-templates select="stepRat"/>
      <xsl:apply-templates select="stepDateTm"/>
      <!--resp party, source-->
    </LI_ProcessStep>
  </xsl:template>
  <xsl:template match="stepDateTm">
    <dateTime>
      <xsl:call-template name="DateTime"/>
    </dateTime>
  </xsl:template>
  <xsl:template match="stepRat">
    <rationale>
      <xsl:call-template name="CharacterString"/>
    </rationale>
  </xsl:template>
  <xsl:template match="stepDesc">
    <description>
      <xsl:call-template name="CharacterString"/>
    </description>
  </xsl:template>
  <xsl:template name="source">
    <source>
      <xsl:call-template name="LI_Source"/>
    </source>
  </xsl:template>
  <xsl:template name="LI_Source">
    <LI_Source>
      <xsl:apply-templates select="SRCDESC"/>
      <xsl:apply-templates select="SRCSCALE"/>
      <!--Ref sys, citation, extent, processStep-->
    </LI_Source>
  </xsl:template>
  <xsl:template match="SRCSCALE">
    <scaleDenominator>
      <xsl:call-template name="MD_RepresentativeFraction"/>
    </scaleDenominator>
  </xsl:template>
  <xsl:template match="SRCDESC">
    <description>
      <xsl:call-template name="CharacterString"/>
    </description>
  </xsl:template>
  <xsl:template match="STATEMENT">
    <statement>
      <xsl:call-template name="CharacterString"/>
    </statement>
  </xsl:template>
  <xsl:template name="report">
    <report>
      <!-- choose DQ_Element-->
      <xsl:call-template name="_DQ_Element"/>
    </report>
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
    <result>
      <xsl:call-template name="DQ_ConformanceResult"/>
      <xsl:call-template name="DQ_QuantitativeResult"/>
    </result>
  </xsl:template>
  <xsl:template name="DQ_QuantitativeResult">
    <DQ_QuantitativeResult>
      <!-- valueType (RecordType)-->
      <!-- valueUnit  (UOM)-->
      <xsl:apply-templates select="errStat"/>
      <!-- value (Record)-->
    </DQ_QuantitativeResult>
  </xsl:template>
  <xsl:template match="errStat">
    <errorStatistic>
      <xsl:call-template name="CharacterString"/>
    </errorStatistic>
  </xsl:template>
  <xsl:template name="DQ_ConformanceResult">
    <DQ_ConformanceResult>
      <!-- CI_Citation-->
      <xsl:apply-templates select="conExpl"/>
      <xsl:apply-templates select="conPass"/>
    </DQ_ConformanceResult>
  </xsl:template>
  <xsl:template match="conPass">
    <pass>
      <xsl:call-template name="Boolean"/>
    </pass>
  </xsl:template>
  <xsl:template match="conExpl">
    <explanation>
      <xsl:call-template name="CharacterString"/>
    </explanation>
  </xsl:template>
  <xsl:template match="measDateTm">
    <dateTime>
      <xsl:call-template name="DateTime"/>
    </dateTime>
  </xsl:template>
  <xsl:template match="evalMethDesc">
    <evaluationMethodDescription>
      <xsl:call-template name="CharacterString"/>
    </evaluationMethodDescription>
  </xsl:template>
  <xsl:template match="evalMethType">
    <evaluationMethodType>
      <xsl:call-template name="DQ_EvaluationMethodTypeCode"/>
    </evaluationMethodType>
  </xsl:template>
  <xsl:template name="DQ_EvaluationMethodTypeCode">
    <DQ_EvaluationMethodTypeCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?DQ_EvaluationMethodTypeCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </DQ_EvaluationMethodTypeCode>
  </xsl:template>
  <xsl:template match="measDesc">
    <measureDescription>
      <xsl:call-template name="CharacterString"/>
    </measureDescription>
  </xsl:template>
  <xsl:template match="measName">
    <nameOfMeasure>
      <xsl:call-template name="CharacterString"/>
    </nameOfMeasure>
  </xsl:template>
  <xsl:template match="scpLvl">
    <level>
      <xsl:call-template name="MD_ScopeCode"/>
    </level>
  </xsl:template>
  <xsl:template match="t01_object.metadata_standard_version">
    <metadataStandardVersion>
      <xsl:call-template name="CharacterString"/>
    </metadataStandardVersion>
  </xsl:template>
  <xsl:template match="t01_object.metadata_standard_name">
    <metadataStandardName>
      <xsl:call-template name="CharacterString"/>
    </metadataStandardName>
  </xsl:template>
  <xsl:template match="HIERARCHY/HIERARCHYFEAT/MDHRLVNAME">
    <hierarchyLevelName>
      <xsl:call-template name="CharacterString"/>
    </hierarchyLevelName>
  </xsl:template>
  <xsl:template match="mdParentID">
    <parentIdentifier>
      <xsl:call-template name="CharacterString"/>
    </parentIdentifier>
  </xsl:template>
  <xsl:template match="t01_object.mod_time">
    <dateStamp>
      <xsl:call-template name="Date"/>
    </dateStamp>
  </xsl:template>
  <xsl:template name="federatedCatalog">
    <federatedCatalog>
      <xsl:call-template name="CI_OnlineResource"/>
    </federatedCatalog>
  </xsl:template>
  <xsl:template name="identificationInfo">
    <xsl:param name="elementSetName"/>
    <xsl:param name="typeName"/>
    <identificationInfo>
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
    </identificationInfo>
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
      <pointOfContact>
        <xsl:apply-templates select="t012_obj_adr/t02_address"/>
      </pointOfContact>
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
    <MD_DataIdentification>
      <xsl:call-template name="MD_Identification">
        <xsl:with-param name="elementSetName" select="$elementSetName"/>
      </xsl:call-template>
      <xsl:if test="($elementSetName='full')">
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
    </MD_DataIdentification>
  </xsl:template>
  <xsl:template match="RESCONSTLEG/RESCONSTLEGFEAT">
    <xsl:call-template name="resourceConstraintsLegal"/>
  </xsl:template>
  <xsl:template match="RESCONSTSEC/RESCONSTSECFEAT">
    <xsl:call-template name="resourceConstraintsSecurity"/>
  </xsl:template>
  <xsl:template name="spatialResolution">
    <spatialResolution>
      <xsl:call-template name="MD_Resolution"/>
    </spatialResolution>
  </xsl:template>
  <xsl:template name="MD_Resolution">
    <MD_Resolution>
      <xsl:apply-templates select="t011_obj_geo_scale.t011_obj_geo_scale.scale"/>
      <xsl:apply-templates select="scaleDist"/>
    </MD_Resolution>
  </xsl:template>
  <xsl:template match="t011_obj_geo_scale.t011_obj_geo_scale.scale">
    <equivalentScale>
      <xsl:call-template name="MD_RepresentativeFraction"/>
    </equivalentScale>
  </xsl:template>
  <xsl:template match="scaleDist">
    <distance>
      <xsl:call-template name="Distance"/>
    </distance>
  </xsl:template>
  <xsl:template name="Distance">
    <Distance>
      <xsl:call-template name="Measure"/>
    </Distance>
  </xsl:template>
  <xsl:template name="Measure">
    <value>
      <xsl:call-template name="Decimal"/>
    </value>
    <!--uom>

 </uom-->
  </xsl:template>
  <xsl:template name="MD_RepresentativeFraction">
    <MD_RepresentativeFraction>
      <denominator>
        <xsl:call-template name="positiveInteger"/>
      </denominator>
    </MD_RepresentativeFraction>
  </xsl:template>
  <xsl:template name="resourceFormat">
    <xsl:param name="elementSetName"/>
    <resourceFormat>
      <xsl:call-template name="MD_Format">
        <xsl:with-param name="elementSetName" select="$elementSetName"/>
      </xsl:call-template>
    </resourceFormat>
  </xsl:template>
  <xsl:template name="MD_Format">
    <xsl:param name="elementSetName"/>
    <MD_Format>
      <xsl:apply-templates select="FORMATNAME"/>
      <xsl:apply-templates select="FORMATVER"/>
      <xsl:if test="($elementSetName='full')">
        <xsl:apply-templates select="formatAmdNum"/>
        <xsl:apply-templates select="formatSpec"/>
        <xsl:apply-templates select="fileDecmTech"/>
        <!--xsl:apply-templates select="formatDistributor"/-->
      </xsl:if>
    </MD_Format>
  </xsl:template>
  <xsl:template match="fileDecmTech">
    <fileDecompressionTechnique>
      <xsl:call-template name="CharacterString"/>
    </fileDecompressionTechnique>
  </xsl:template>
  <xsl:template match="formatSpec">
    <specification>
      <xsl:call-template name="CharacterString"/>
    </specification>
  </xsl:template>
  <xsl:template match="formatAmdNum">
    <amendmentNumber>
      <xsl:call-template name="CharacterString"/>
    </amendmentNumber>
  </xsl:template>
  <xsl:template match="FORMATNAME">
    <name>
      <xsl:call-template name="CharacterString"/>
    </name>
  </xsl:template>
  <xsl:template match="FORMATVER">
    <version>
      <xsl:call-template name="CharacterString"/>
    </version>
  </xsl:template>
  <xsl:template name="resourceMaintenance">
    <resourceMaintenance>
      <xsl:call-template name="MD_MaintenanceInformation"/>
    </resourceMaintenance>
  </xsl:template>
  <xsl:template name="MD_MaintenanceInformation">
    <MD_MaintenanceInformation>
      <xsl:apply-templates select="maintFreq"/>
      <xsl:apply-templates select="dateNext"/>
      <xsl:apply-templates select="usrDefFreq"/>
      <xsl:apply-templates select="maintScp"/>
      <!--xsl:apply-templates select="upScpDesc"/-->
      <xsl:apply-templates select="maintNote"/>
      <!--xsl:apply-templates select="contact"/-->
    </MD_MaintenanceInformation>
  </xsl:template>
  <xsl:template match="maintNote">
    <maintenanceNote>
      <xsl:call-template name="CharacterString"/>
    </maintenanceNote>
  </xsl:template>
  <xsl:template match="maintScp">
    <updateScope>
      <xsl:call-template name="MD_ScopeCode"/>
    </updateScope>
  </xsl:template>
  <xsl:template match="usrDefFreq">
    <userDefinedMaintenanceFrequency>
      <xsl:call-template name="TM_PeriodDuration"/>
    </userDefinedMaintenanceFrequency>
  </xsl:template>
  <xsl:template name="TM_PeriodDuration">
    <TM_PeriodDuration>
      <xsl:value-of select="."/>
    </TM_PeriodDuration>
  </xsl:template>
  <xsl:template match="dateNext">
    <dateOfNextUpdate>
      <xsl:call-template name="Date"/>
    </dateOfNextUpdate>
  </xsl:template>
  <xsl:template match="maintFreq">
    <maintenanceAndUpdateFrequency>
      <xsl:call-template name="MD_MaintenanceFrequencyCode"/>
    </maintenanceAndUpdateFrequency>
  </xsl:template>
  <xsl:template name="MD_MaintenanceFrequencyCode">
    <MD_MaintenanceFrequencyCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_MaintenanceFrequencyCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </MD_MaintenanceFrequencyCode>
  </xsl:template>
  <xsl:template name="resourceConstraintsLegal">
    <resourceConstraints>
      <xsl:call-template name="MD_LegalConstraints"/>
    </resourceConstraints>
  </xsl:template>
  <xsl:template name="resourceConstraintsSecurity">
    <resourceConstraints>
      <xsl:call-template name="MD_SecurityConstraints"/>
    </resourceConstraints>
  </xsl:template>
  <xsl:template name="MD_LegalConstraints">
    <MD_LegalConstraints>
      <xsl:apply-templates select="USELIMIT"/>
      <xsl:apply-templates select="accessConsts"/>
      <xsl:apply-templates select="useConsts"/>
      <xsl:apply-templates select="otherConsts"/>
    </MD_LegalConstraints>
  </xsl:template>
  <xsl:template name="MD_SecurityConstraints">
    <MD_SecurityConstraints>
      <xsl:apply-templates select="USELIMIT"/>
      <xsl:apply-templates select="class"/>
      <xsl:apply-templates select="userNote"/>
      <xsl:apply-templates select="classSys"/>
      <xsl:apply-templates select="handDesc"/>
    </MD_SecurityConstraints>
  </xsl:template>
  <xsl:template name="MD_Constraints">
    <MD_Constraints>
      <xsl:apply-templates select="USELIMIT"/>
    </MD_Constraints>
  </xsl:template>
  <xsl:template match="USELIMIT">
    <useLimitation>
      <xsl:call-template name="CharacterString"/>
    </useLimitation>
  </xsl:template>
  <xsl:template match="handDesc">
    <handlingDescription>
      <xsl:call-template name="CharacterString"/>
    </handlingDescription>
  </xsl:template>
  <xsl:template match="classSys">
    <classificationSystem>
      <xsl:call-template name="CharacterString"/>
    </classificationSystem>
  </xsl:template>
  <xsl:template match="userNote">
    <userNote>
      <xsl:call-template name="CharacterString"/>
    </userNote>
  </xsl:template>
  <xsl:template match="class">
    <classification>
      <xsl:call-template name="MD_ClassificationCode"/>
    </classification>
  </xsl:template>
  <xsl:template name="MD_ClassificationCode">
    <MD_ClassificationCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_ClassificationCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </MD_ClassificationCode>
  </xsl:template>
  <xsl:template match="otherConsts">
    <otherConstraints>
      <xsl:call-template name="CharacterString"/>
    </otherConstraints>
  </xsl:template>
  <xsl:template match="useConsts">
    <useConstraints>
      <xsl:call-template name="MD_RestrictionCode"/>
    </useConstraints>
  </xsl:template>
  <xsl:template match="accessConsts">
    <accessConstraints>
      <xsl:call-template name="MD_RestrictionCode"/>
    </accessConstraints>
  </xsl:template>
  <xsl:template name="MD_RestrictionCode">
    <MD_RestrictionCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_RestrictionCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </MD_RestrictionCode>
  </xsl:template>
  <xsl:template name="graphicOverview">
    <graphicOverview>
      <xsl:call-template name="MD_BrowseGraphic"/>
    </graphicOverview>
  </xsl:template>
  <xsl:template name="MD_BrowseGraphic">
    <MD_BrowseGraphic>
      <xsl:apply-templates select="bgFileName"/>
      <xsl:apply-templates select="bgFileDesc"/>
      <xsl:apply-templates select="bgFileType"/>
    </MD_BrowseGraphic>
  </xsl:template>
  <xsl:template match="bgFileName">
    <fileName>
      <xsl:call-template name="CharacterString"/>
    </fileName>
  </xsl:template>
  <xsl:template match="bgFileDesc">
    <fileDescription>
      <xsl:call-template name="CharacterString"/>
    </fileDescription>
  </xsl:template>
  <xsl:template match="bgFileType">
    <fileType>
      <xsl:call-template name="CharacterString"/>
    </fileType>
  </xsl:template>
  <xsl:template name="descriptiveKeywords">
    <descriptiveKeywords>
      <xsl:call-template name="MD_Keywords"/>
    </descriptiveKeywords>
  </xsl:template>
  <xsl:template name="MD_Keywords">
    <MD_Keywords>
      <xsl:apply-templates select="t04_search/t04_search.searchterm"/>
      <xsl:apply-templates select="keyTyp"/>
      <!--xsl:apply-templates select="thesaurusName"/-->
    </MD_Keywords>
  </xsl:template>
  <xsl:template match="t04_search/t04_search.searchterm">
    <keyword>
      <xsl:call-template name="CharacterString"/>
    </keyword>
  </xsl:template>
  <xsl:template match="keyTyp">
    <type>
      <xsl:call-template name="MD_KeywordTypeCode"/>
    </type>
  </xsl:template>
  <xsl:template name="MD_KeywordTypeCode">
    <MD_KeywordTypeCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_KeywordTypeCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </MD_KeywordTypeCode>
  </xsl:template>
  <xsl:template name="resourceSpecificUsage">
    <resourceSpecificUsage>
      <xsl:call-template name="MD_Usage"/>
    </resourceSpecificUsage>
  </xsl:template>
  <xsl:template name="MD_Usage">
    <MD_Usage>
      <xsl:apply-templates select="specUsage"/>
      <xsl:apply-templates select="usageDate"/>
      <xsl:apply-templates select="usrDetLim"/>
      <!--xsl:apply-templates select="userContactInfo"/-->
    </MD_Usage>
  </xsl:template>
  <xsl:template match="usrDetLim">
    <userDeterminedLimitations>
      <xsl:call-template name="CharacterString"/>
    </userDeterminedLimitations>
  </xsl:template>
  <xsl:template match="usageDate">
    <usageDateTime>
      <xsl:call-template name="DateTime"/>
    </usageDateTime>
  </xsl:template>
  <xsl:template match="specUsage">
    <specificUsage>
      <xsl:call-template name="CharacterString"/>
    </specificUsage>
  </xsl:template>
  <xsl:template match="envirDesc">
    <environmentalDescription>
      <xsl:call-template name="CharacterString"/>
    </environmentalDescription>
  </xsl:template>
  <xsl:template match="suppInfo">
    <supplementalInformation>
      <xsl:call-template name="CharacterString"/>
    </supplementalInformation>
  </xsl:template>
  <xsl:template match="spatRpType">
    <spatialRepresentationType>
      <xsl:call-template name="MD_SpatialRepresentationTypeCode"/>
    </spatialRepresentationType>
  </xsl:template>
  <xsl:template name="MD_SpatialRepresentationTypeCode">
    <MD_SpatialRepresentationTypeCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_SpatialRepresentationTypeCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </MD_SpatialRepresentationTypeCode>
  </xsl:template>
  <xsl:template match="idStatus">
    <status>
      <xsl:call-template name="MD_ProgressCode"/>
    </status>
  </xsl:template>
  <xsl:template name="MD_ProgressCode">
    <MD_ProgressCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_ProgressCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </MD_ProgressCode>
  </xsl:template>
  <xsl:template match="idCredit">
    <credit>
      <xsl:call-template name="CharacterString"/>
    </credit>
  </xsl:template>
  <xsl:template match="idPurp">
    <purpose>
      <xsl:call-template name="CharacterString"/>
    </purpose>
  </xsl:template>
  <xsl:template match="summary">
    <abstract>
      <xsl:call-template name="CharacterString"/>
    </abstract>
  </xsl:template>
  <xsl:template name="extent">
    <extent>
      <xsl:call-template name="EX_Extent"/>
    </extent>
  </xsl:template>
  <xsl:template name="EX_Extent">
    <EX_Extent>
      <xsl:apply-templates select="exDesc"/>
      <xsl:call-template name="verticalElement"/>
      <xsl:call-template name="temporalElement"/>
      <xsl:call-template name="geographicElements"/>
    </EX_Extent>
  </xsl:template>
  <xsl:template match="exDesc">
    <description>
      <xsl:call-template name="CharacterString"/>
    </description>
  </xsl:template>
  <xsl:template name="temporalElement">
    <temporalElement>
      <xsl:call-template name="EX_TemporalExtent"/>
    </temporalElement>
  </xsl:template>
  <xsl:template name="EX_TemporalExtent">
    <EX_TemporalExtent>
      <extent>
        <xsl:call-template name="TM_Primitive"/>
      </extent>
    </EX_TemporalExtent>
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
    <geographicElement>
      <xsl:call-template name="EX_GeographicDescription"/>
    </geographicElement>
    <geographicElement>
      <xsl:call-template name="EX_GeographicBoundingBox"/>
    </geographicElement>
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
    <EX_GeographicDescription>
      <xsl:call-template name="_EX_GeographicExtent"/>
      <xsl:apply-templates select="AREA/AREAFEAT"/>
    </EX_GeographicDescription>
  </xsl:template>
  <xsl:template match="AREA/AREAFEAT">
    <geographicIdentifier>
      <xsl:call-template name="MD_Identifier"/>
    </geographicIdentifier>
  </xsl:template>
  <xsl:template name="EX_GeographicBoundingBox">
    <EX_GeographicBoundingBox>
      <xsl:call-template name="_EX_GeographicExtent"/>
      <xsl:apply-templates select="t011_township[1]/t01_st_bbox/x1"/>
      <xsl:apply-templates select="t011_township[1]/t01_st_bbox/x2"/>
      <xsl:apply-templates select="t011_township[1]/t01_st_bbox/y1"/>
      <xsl:apply-templates select="t011_township[1]/t01_st_bbox/y2"/>
    </EX_GeographicBoundingBox>
  </xsl:template>
  <xsl:template match="t011_township[1]/t01_st_bbox/y1">
    <southBoundLatitude>
      <xsl:call-template name="approximateLatitude"/>
    </southBoundLatitude>
  </xsl:template>
  <xsl:template match="t011_township[1]/t01_st_bbox/y2">
    <northBoundLatitude>
      <xsl:call-template name="approximateLatitude"/>
    </northBoundLatitude>
  </xsl:template>
  <xsl:template match="t011_township[1]/t01_st_bbox/x2">
    <eastBoundLongitude>
      <xsl:call-template name="approximateLongitude"/>
    </eastBoundLongitude>
  </xsl:template>
  <xsl:template match="t011_township[1]/t01_st_bbox/x1">
    <westBoundLongitude>
      <xsl:call-template name="approximateLongitude"/>
    </westBoundLongitude>
  </xsl:template>
  <xsl:template name="approximateLongitude">
    <approximateLongitude>
      <xsl:value-of select="."/>
    </approximateLongitude>
  </xsl:template>
  <xsl:template name="approximateLatitude">
    <approximateLatitude>
      <xsl:value-of select="."/>
    </approximateLatitude>
  </xsl:template>
  <xsl:template name="_EX_GeographicExtent">
    <xsl:apply-templates select="exTypeCode"/>
  </xsl:template>
  <xsl:template match="exTypeCode">
    <extentTypeCode>
      <xsl:call-template name="Boolean"/>
    </extentTypeCode>
  </xsl:template>
  <xsl:template name="verticalElement">
    <verticalElement>
      <xsl:call-template name="EX_VerticalExtent"/>
    </verticalElement>
  </xsl:template>
  <xsl:template name="EX_VerticalExtent">
    <EX_VerticalExtent>
      <xsl:apply-templates select="vertMinVal"/>
      <xsl:apply-templates select="vertMaxVal"/>
      <xsl:apply-templates select="vertUom"/>
      <xsl:apply-templates select="vertDatum"/>
    </EX_VerticalExtent>
  </xsl:template>
  <xsl:template match="vertMinVal">
    <minimumValue>
      <xsl:call-template name="Real"/>
    </minimumValue>
  </xsl:template>
  <xsl:template match="vertMaxVal">
    <maximumValue>
      <xsl:call-template name="Real"/>
    </maximumValue>
  </xsl:template>
  <xsl:template match="vertUom">
    <unitOfMeasure>
      <xsl:call-template name="UomLength"/>
    </unitOfMeasure>
  </xsl:template>
  <xsl:template name="UomLength">
    <UomLength>
      <xsl:call-template name="_UnitOfMeasure_Type"/>
    </UomLength>
  </xsl:template>
  <xsl:template name="_UnitOfMeasure_Type">
    <xsl:value-of select="."/>
  </xsl:template>
  <xsl:template match="vertDatum">
    <verticalDatum>
      <xsl:call-template name="SC_VerticalDatum"/>
    </verticalDatum>
  </xsl:template>
  <xsl:template name="SC_VerticalDatum">
    <xsl:call-template name="datumID"/>
  </xsl:template>
  <xsl:template name="datumID">
    <xsl:call-template name="RS_Identifier"/>
  </xsl:template>
  <xsl:template name="MD_Identifier">
    <MD_Identifier>
      <!-- element authority (CI_Citation)?-->
      <xsl:apply-templates select="IDENTCODE"/>
    </MD_Identifier>
  </xsl:template>
  <xsl:template name="RS_Identifier">
    <RS_Identifier>
      <xsl:apply-templates select="IDENTCODE"/>
      <xsl:apply-templates select="IDENTCODESPACE"/>
    </RS_Identifier>
  </xsl:template>
  <xsl:template match="IDENTCODE">
    <code>
      <xsl:call-template name="CharacterString"/>
    </code>
  </xsl:template>
  <xsl:template match="IDENTCODESPACE">
    <codeSpace>
      <xsl:call-template name="CharacterString"/>
    </codeSpace>
  </xsl:template>
  <xsl:template match="tpCat">
    <topicCategory>
      <xsl:call-template name="MD_TopicCategoryCode"/>
    </topicCategory>
  </xsl:template>
  <xsl:template name="MD_TopicCategoryCode">
    <MD_TopicCategoryCode>
      <xsl:value-of select="."/>
    </MD_TopicCategoryCode>
  </xsl:template>
  <xsl:template name="citation">
    <xsl:param name="elementSetName"/>
    <citation>
      <xsl:call-template name="CI_Citation">
        <xsl:with-param name="elementSetName" select="$elementSetName"/>
      </xsl:call-template>
    </citation>
  </xsl:template>
  <xsl:template name="CI_Citation">
    <xsl:param name="elementSetName"/>
    <CI_Citation>
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
    </CI_Citation>
  </xsl:template>
  <xsl:template match="RESEDDATE/RESEDDATEFEAT">
    <editionDate>
      <xsl:call-template name="CI_Date"/>
    </editionDate>
  </xsl:template>
  <xsl:template name="creationDate">
    <date>
      <xsl:call-template name="CI_Date"/>
    </date>
  </xsl:template>
  <xsl:template match="issn">
    <ISSN>
      <xsl:call-template name="CharacterString"/>
    </ISSN>
  </xsl:template>
  <xsl:template match="isbn">
    <ISBN>
      <xsl:call-template name="CharacterString"/>
    </ISBN>
  </xsl:template>
  <xsl:template match="collTitle">
    <collectiveTitle>
      <xsl:call-template name="CharacterString"/>
    </collectiveTitle>
  </xsl:template>
  <xsl:template match="otherCitDet">
    <otherCitationDetails>
      <xsl:call-template name="CharacterString"/>
    </otherCitationDetails>
  </xsl:template>
  <xsl:template name="CI_Series">
    <CI_Series>
      <xsl:apply-templates select="seriesName"/>
      <xsl:apply-templates select="issId"/>
      <xsl:apply-templates select="artPage"/>
    </CI_Series>
  </xsl:template>
  <xsl:template match="seriesName">
    <name>
      <xsl:call-template name="CharacterString"/>
    </name>
  </xsl:template>
  <xsl:template match="issId">
    <issueIdentification>
      <xsl:call-template name="CharacterString"/>
    </issueIdentification>
  </xsl:template>
  <xsl:template match="artPage">
    <page>
      <xsl:call-template name="CharacterString"/>
    </page>
  </xsl:template>
  <xsl:template match="presForm">
    <presentationForm>
      <xsl:call-template name="CI_PresentationFormCode"/>
    </presentationForm>
  </xsl:template>
  <xsl:template name="CI_PresentationFormCode">
    <CI_PresentationFormCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?CI_PresentationFormCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </CI_PresentationFormCode>
  </xsl:template>
  <xsl:template match="citId">
    <identifier>
      <xsl:call-template name="MD_Identifier"/>
    </identifier>
  </xsl:template>
  <xsl:template match="resEdDate">
    <editionDate>
      <xsl:call-template name="Date"/>
    </editionDate>
  </xsl:template>
  <xsl:template name="CI_Date">
    <CI_Date>
      <xsl:apply-templates select="t01_object.create_time"/>
      <!--xsl:apply-templates select="REFDATETYPE"/-->
      <xsl:call-template name="REFDATETYPE"/>
    </CI_Date>
  </xsl:template>
  <xsl:template match="t01_object.create_time">
    <date>
      <xsl:call-template name="Date"/>
    </date>
  </xsl:template>
  <!--xsl:template match="REFDATETYPE">
      <dateType>
         <xsl:call-template name="CI_DateTypeCode"/>
      </dateType>
 </xsl:template-->
  <xsl:template name="REFDATETYPE">
    <dateType>
      <xsl:call-template name="CI_DateTypeCode"/>
    </dateType>
  </xsl:template>
  <!--xsl:template name="CI_DateTypeCode">

        <CI_DateTypeCode>
             <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?CI_DateTypeCode</xsl:attribute>
                 <xsl:attribute name="codeListValue"><xsl:value-of select="."/></xsl:attribute>
	</CI_DateTypeCode>
</xsl:template-->
  <xsl:template name="CI_DateTypeCode">
    <CI_DateTypeCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?CI_DateTypeCode</xsl:attribute>
      <xsl:attribute name="codeListValue">creation</xsl:attribute>
    </CI_DateTypeCode>
  </xsl:template>
  <xsl:template match="resEd">
    <edition>
      <xsl:call-template name="CharacterString"/>
    </edition>
  </xsl:template>
  <xsl:template match="title">
    <title>
      <xsl:call-template name="CharacterString"/>
    </title>
  </xsl:template>
  <xsl:template match="t01_object.dataset_alternate_name">
    <alternateTitle>
      <xsl:call-template name="CharacterString"/>
    </alternateTitle>
  </xsl:template>
  <xsl:template match="t012_obj_adr/t02_address">
    <!--xsl:if test="../t012_obj_adr.typ='0'">


      <contact>
         <xsl:call-template name="CI_ResponsibleParty"/>
      </contact>

  </xsl:if-->
    <!--xsl:if test="../t012_obj_adr.typ='1'">


     <pointOfContact>
           <xsl:call-template name="CI_ResponsibleParty"/>
       </pointOfContact>

  </xsl:if-->
    <xsl:call-template name="CI_ResponsibleParty"/>
  </xsl:template>
  <xsl:template name="CI_ResponsibleParty">
    <CI_ResponsibleParty>
      <xsl:call-template name="RpIndname"/>
      <xsl:apply-templates select="t02_address.institution"/>
      <xsl:apply-templates select="t02_address.job"/>
      <xsl:call-template name="contactInfo"/>
      <xsl:apply-templates select="role"/>
    </CI_ResponsibleParty>
  </xsl:template>
  <xsl:template name="contactInfo">
    <contactInfo>
      <xsl:call-template name="CI_Contact"/>
    </contactInfo>
  </xsl:template>
  <xsl:template name="CI_Contact">
    <CI_Contact>
      <xsl:call-template name="phone"/>
      <xsl:call-template name="address"/>
      <xsl:call-template name="onlineResource"/>
      <xsl:apply-templates select="hoursOfService"/>
      <xsl:apply-templates select="contactInstructions"/>
    </CI_Contact>
  </xsl:template>
  <xsl:template match="hoursOfService">
    <hoursOfService>
      <xsl:call-template name="CharacterString"/>
    </hoursOfService>
  </xsl:template>
  <xsl:template match="contactInstructions">
    <contactInstructions>
      <xsl:call-template name="CharacterString"/>
    </contactInstructions>
  </xsl:template>
  <xsl:template name="onlineResource">
    <onlineResource>
      <xsl:call-template name="CI_OnlineResource"/>
    </onlineResource>
  </xsl:template>
  <xsl:template name="CI_OnlineResource">
    <CI_OnlineResource>
      <!-- linkage/URL-->
      <xsl:apply-templates select="t021_communication" mode="line_4"/>
      <xsl:apply-templates select="protocol"/>
      <xsl:apply-templates select="appProfile"/>
      <xsl:apply-templates select="orName"/>
      <xsl:apply-templates select="orDesc"/>
      <xsl:apply-templates select="ORFUNKT"/>
    </CI_OnlineResource>
  </xsl:template>
  <xsl:template match="orDesc">
    <description>
      <xsl:call-template name="CharacterString"/>
    </description>
  </xsl:template>
  <xsl:template match="orName">
    <name>
      <xsl:call-template name="CharacterString"/>
    </name>
  </xsl:template>
  <xsl:template match="appProfile">
    <applicationProfile>
      <xsl:call-template name="CharacterString"/>
    </applicationProfile>
  </xsl:template>
  <xsl:template match="protocol">
    <protocol>
      <xsl:call-template name="CharacterString"/>
    </protocol>
  </xsl:template>
  <xsl:template match="ORFUNKT">
    <function>
      <xsl:call-template name="CI_OnLineFunctionCode"/>
    </function>
  </xsl:template>
  <xsl:template name="CI_OnLineFunctionCode">
    <CI_OnLineFunctionCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?CI_OnLineFunctionCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </CI_OnLineFunctionCode>
  </xsl:template>
  <xsl:template name="address">
    <address>
      <xsl:call-template name="CI_Address"/>
    </address>
  </xsl:template>
  <xsl:template name="CI_Address">
    <CI_Address>
      <xsl:apply-templates select="t02_address.street"/>
      <xsl:apply-templates select="t02_address.city"/>
      <xsl:apply-templates select="adminArea"/>
      <xsl:apply-templates select="t02_address.postcode"/>
      <xsl:apply-templates select="t02_address.state_id"/>
      <xsl:apply-templates select="t021_communication" mode="line_2"/>
    </CI_Address>
  </xsl:template>
  <xsl:template match="t02_address.street">
    <deliveryPoint>
      <xsl:call-template name="CharacterString"/>
    </deliveryPoint>
  </xsl:template>
  <xsl:template match="t02_address.city">
    <city>
      <xsl:call-template name="CharacterString"/>
    </city>
  </xsl:template>
  <xsl:template match="adminArea">
    <administrativeArea>
      <xsl:call-template name="CharacterString"/>
    </administrativeArea>
  </xsl:template>
  <xsl:template match="t02_address.postcode">
    <postalCode>
      <xsl:call-template name="CharacterString"/>
    </postalCode>
  </xsl:template>
  <xsl:template match="t02_address.state_id">
    <country>
      <xsl:call-template name="CharacterString"/>
    </country>
  </xsl:template>
  <xsl:template name="phone">
    <phone>
      <xsl:call-template name="CI_Telephone"/>
    </phone>
  </xsl:template>
  <xsl:template name="CI_Telephone">
    <CI_Telephone>
      <xsl:apply-templates select="t021_communication" mode="line_13"/>
    </CI_Telephone>
  </xsl:template>
  <xsl:template match="t021_communication" mode="line_13">
    <xsl:if test="t021_communication.line='1'">
      <voice>
        <xsl:apply-templates select="t021_communication.comm_value" mode="charString"/>
      </voice>
    </xsl:if>
    <xsl:if test="t021_communication.line='3'">
      <facsimile>
        <xsl:apply-templates select="t021_communication.comm_value" mode="charString"/>
      </facsimile>
    </xsl:if>
  </xsl:template>
  <xsl:template match="t021_communication" mode="line_2">
    <xsl:if test="t021_communication.line='2'">
      <electronicMailAddress>
        <xsl:apply-templates select="t021_communication.comm_value" mode="charString"/>
      </electronicMailAddress>
    </xsl:if>
  </xsl:template>
  <xsl:template match="t021_communication" mode="line_4">
    <xsl:if test="t021_communication.line='4'">
      <linkage>
        <xsl:apply-templates select="t021_communication.comm_value" mode="url"/>
      </linkage>
    </xsl:if>
  </xsl:template>
  <xsl:template match="t021_communication.comm_value" mode="charString">
    <xsl:call-template name="CharacterString"/>
  </xsl:template>
  <xsl:template match="t021_communication.comm_value" mode="url">
    <xsl:call-template name="URL"/>
  </xsl:template>
  <xsl:template match="t02_address.job">
    <positionName>
      <xsl:call-template name="CharacterString"/>
    </positionName>
  </xsl:template>
  <xsl:template match="t02_address.institution">
    <organisationName>
      <xsl:call-template name="CharacterString"/>
    </organisationName>
  </xsl:template>
  <xsl:template name="RpIndname">
    <individualName>
      <CharacterString>
        <xsl:apply-templates select="t02_address.title"/>
        <!--  Leerzeichen  -->
        <xsl:value-of select="' '"/>
        <xsl:apply-templates select="t02_address.firstname"/>
        <!-- Leerzeichen  -->
        <xsl:value-of select="' '"/>
        <xsl:apply-templates select="t02_address.lastname"/>
      </CharacterString>
    </individualName>
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
    <role>
      <xsl:call-template name="CI_RoleCode"/>
    </role>
  </xsl:template>
  <xsl:template name="CI_RoleCode">
    <CI_RoleCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?CI_RoleCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </CI_RoleCode>
  </xsl:template>
  <xsl:template match="HIERARCHY/HIERARCHYFEAT/MDHRLV">
    <hierarchyLevel>
      <xsl:call-template name="MD_ScopeCode"/>
    </hierarchyLevel>
  </xsl:template>
  <xsl:template match="t01_object.obj_id">
    <fileIdentifier>
      <xsl:call-template name="CharacterString"/>
    </fileIdentifier>
  </xsl:template>
  <xsl:template match="MDLANG">
    <language>
      <xsl:call-template name="CharacterString"/>
    </language>
  </xsl:template>
  <xsl:template match="MAIN/MAINFEAT/DATALANG">
    <language>
      <xsl:call-template name="CharacterString"/>
    </language>
  </xsl:template>
  <xsl:template match="MDCHAR">
    <xsl:call-template name="characterSet"/>
  </xsl:template>
  <xsl:template match="DATACHAR">
    <xsl:call-template name="characterSet"/>
  </xsl:template>
  <xsl:template name="characterSet">
    <characterSet>
      <xsl:call-template name="MD_CharacterSetCode"/>
    </characterSet>
  </xsl:template>
  <xsl:template name="MD_CharacterSetCode">
    <MD_CharacterSetCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_CharacterSetCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </MD_CharacterSetCode>
  </xsl:template>
  <xsl:template name="MD_ScopeCode">
    <MD_ScopeCode>
      <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_ScopeCode</xsl:attribute>
      <xsl:attribute name="codeListValue">
        <xsl:value-of select="."/>
      </xsl:attribute>
    </MD_ScopeCode>
  </xsl:template>
  <xsl:template name="CharacterString">
    <CharacterString>
      <xsl:value-of select="."/>
    </CharacterString>
  </xsl:template>
  <xsl:template name="Boolean">
    <Boolean>
      <xsl:value-of select="."/>
    </Boolean>
  </xsl:template>
  <xsl:template name="Date">
    <Date>
      <!--format to YYYY-MM-DD-->
      <xsl:value-of select="concat( substring(. , 1, 4),   '-',   substring(. , 5, 2) ,  '-',  substring(. , 7, 2) )"/>
    </Date>
  </xsl:template>
  <xsl:template name="DateTime">
    <DateTime>
      <!--format to YYYY-MM-DDThh:mm:ss -->
      <xsl:value-of select="concat( substring(. , 1, 4),   '-',   substring(. , 5, 2) ,  '-',  substring(. , 7, 2), 'T', substring(. , 9, 2), ':', substring(. , 11, 2),  ':', substring(. , 13, 2) )"/>
    </DateTime>
  </xsl:template>
  <xsl:template name="URL">
    <URL>
      <xsl:value-of select="."/>
    </URL>
  </xsl:template>
  <xsl:template name="Integer">
    <Integer>
      <xsl:value-of select="."/>
    </Integer>
  </xsl:template>
  <xsl:template name="positiveInteger">
    <positiveInteger>
      <xsl:value-of select="."/>
    </positiveInteger>
  </xsl:template>
  <xsl:template name="nonNegativeInteger">
    <nonNegativeInteger>
      <xsl:value-of select="."/>
    </nonNegativeInteger>
  </xsl:template>
  <xsl:template name="Real">
    <Real>
      <xsl:value-of select="."/>
    </Real>
  </xsl:template>
  <xsl:template name="nonNegativeReal">
    <nonNegativeReal>
      <xsl:value-of select="."/>
    </nonNegativeReal>
  </xsl:template>
  <xsl:template name="realLatitude">
    <realLatitude>
      <xsl:value-of select="."/>
    </realLatitude>
  </xsl:template>
  <xsl:template name="realLongitude">
    <realLongitude>
      <xsl:value-of select="."/>
    </realLongitude>
  </xsl:template>
  <xsl:template name="Decimal">
    <Decimal>
      <xsl:value-of select="."/>
    </Decimal>
  </xsl:template>
  <xsl:template name="_Number">
    <xsl:value-of select="."/>
  </xsl:template>
  <xsl:template name="LocalName">
    <LocalName>
      <xsl:value-of select="."/>
    </LocalName>
  </xsl:template>
  <xsl:template name="ScopedName">
    <ScopedName>
      <xsl:value-of select="."/>
    </ScopedName>
  </xsl:template>
  <xsl:template name="MemberName">
    <MemberName>
      <aName>
        <xsl:call-template name="CharacterString"/>
      </aName>
      <attributeType>
           <!--xsl:call-template name="TypeName"/-->
      </attributeType>
    </MemberName>
  </xsl:template>
  <xsl:template name="TypeName">
    <aName>
      <xsl:call-template name="CharacterString"/>
    </aName>
  </xsl:template>
</xsl:stylesheet>
