<?xml version="1.0"?>
<xsl:stylesheet	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
       xmlns:iso19115="http://www.isotc211.org/iso19115/"
       xmlns:gml="http://www.opengis.net/gml" 
      
 version="1.0">
               
      

     <xsl:template match="/">   

         <GetRecordsResponse>
	      <RequestId></RequestId>
	      <SearchStatus status="" timestamp=""></SearchStatus>
		<SearchResults resultSetId=""
		               elementSet="" 
			       numberOfRecordsMatched=""
			       numberOfRecordsReturned="">	
		
		   <xsl:apply-templates select="/IngridDocuments"/>	
	
               </SearchResults>
	   </GetRecordsResponse>	
    
		
	</xsl:template>


<xsl:template match="/IngridDocuments">

 
   <xsl:apply-templates select="IngridDocument"/>

</xsl:template>


<xsl:template match="IngridDocument">

   <MD_Metadata>
 
      <xsl:apply-templates select="fileIdentifier"/>
      <xsl:apply-templates select="hierarchyLevel"/>
      <xsl:apply-templates select="contacts/IngridDocument"/>
      
       <!-- ist nicht bei 'brief' dabei ?! 
       <xsl:apply-templates select="dateStamp"/> 
       -->    
       <xsl:apply-templates select="identificationInfos/IngridDocument"/>
      <xsl:call-template name="federatedCatalog"/>
     
  </MD_Metadata>


</xsl:template>


<xsl:template match="dateStamp">
  
  <dateStamp>
     <xsl:call-template name="Date"/>
  </dateStamp>

</xsl:template>




<xsl:template name="federatedCatalog">
		<federatedCatalog>
			<xsl:call-template name="CI_OnlineResource"/>
		</federatedCatalog>
	</xsl:template>



<xsl:template match="identificationInfos/IngridDocument">
  
  <identificationInfo>
     <MD_DataIdentification>

      <xsl:call-template name="citation"/>
      
      <!--xsl:apply-templates select="abstract"/>
      <xsl:apply-templates select="purpose"/>
      <xsl:apply-templates select="status"/>
      <xsl:apply-templates select="pointOfContact"/>
      <xsl:apply-templates select="resourceSpecificUsage"/>
      <xsl:apply-templates select="descriptiveKeywords"/>
       <xsl:apply-templates select="graphicOverview"/>
       <xsl:apply-templates select="resourceConstraints"/>
       <xsl:apply-templates select="resourceMaintenance"/>
       
       <xsl:apply-templates select="spatialRepresentationType"/>
       <xsl:apply-templates select="spatialResolution"/>
       <xsl:apply-templates select="language"/>
       <xsl:apply-templates select="characterSet"/-->
      
       <xsl:apply-templates select="topicCategories/IngridDocument/topicCategory"/>
       <xsl:call-template name="extent"/>

  
  </MD_DataIdentification>
</identificationInfo>

</xsl:template>



<xsl:template name="extent">
  
 <extent>
   <xsl:call-template name="EX_Extent"/>
 </extent>

</xsl:template>


<xsl:template name="EX_Extent">
   <EX_Extent>
     <xsl:apply-templates select="description"/>
     <xsl:call-template name="verticalElement"/>
     <xsl:call-template name="temporalElement"/>
     <xsl:call-template name="geographicElements"/>
  </EX_Extent>
</xsl:template>


<xsl:template match="description">
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
    <TM_Primitive xsi:type="gml:TimePeriodType">
            <xsl:apply-templates select="tempExtent_begin"/>
	    <xsl:apply-templates select="tempExtent_end"/>  
     </TM_Primitive>
</xsl:template>



<xsl:template match="tempExtent_begin">
     <gml:begin>
        <xsl:call-template name="TimeInstant"/>  
    </gml:begin>
</xsl:template>

<xsl:template match="tempExtent_end">
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
        <xsl:apply-templates select="geographicDescriptionCode"/>  
    </EX_GeographicDescription> 
</xsl:template>



<xsl:template match="geographicDescriptionCode"> 
    <geographicIdentifier> 
       <xsl:call-template name="MD_Identifier"/>
     </geographicIdentifier>
 </xsl:template>
 
 
 
<xsl:template name="EX_GeographicBoundingBox">
    <EX_GeographicBoundingBox>
           <xsl:call-template name="_EX_GeographicExtent"/>
     
           <xsl:apply-templates select="westBoundLongitude"/>  
           <xsl:apply-templates select="eastBoundLongitude"/>
	   <xsl:apply-templates select="southBoundLatitude"/>
	   <xsl:apply-templates select="northBoundLatitude"/>
            
    </EX_GeographicBoundingBox> 
</xsl:template>


<xsl:template match="southBoundLatitude"> 
    <southBoundLatitude> 
       <xsl:call-template name="approximateLatitude"/>
    </southBoundLatitude>
 </xsl:template>

 <xsl:template match="northBoundLatitude"> 
    <northBoundLatitude> 
       <xsl:call-template name="approximateLatitude"/>
    </northBoundLatitude>
 </xsl:template>
 

<xsl:template match="eastBoundLongitude"> 
    <eastBoundLongitude> 
       <xsl:call-template name="approximateLongitude"/>
    </eastBoundLongitude>
 </xsl:template>

<xsl:template match="westBoundLongitude"> 
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
   <xsl:apply-templates select="extentTypeCode"/>
</xsl:template>

 <xsl:template match="extentTypeCode"> 
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
      <xsl:apply-templates select="minimumValue"/>
      <xsl:apply-templates select="maximumValue"/>
      <xsl:apply-templates select="unitOfMeasure"/>
      <xsl:apply-templates select="verticalDatum"/>
  </EX_VerticalExtent>
</xsl:template>

<xsl:template match="minimumValue">  
  <minimumValue>
    <xsl:call-template name="Real"/>
  </minimumValue>
</xsl:template>

<xsl:template match="maximumValue">  
  <maximumValue>
    <xsl:call-template name="Real"/>
  </maximumValue>
</xsl:template>


<xsl:template match="unitOfMeasure">  
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



 
<xsl:template match="verticalDatum">  
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
      <xsl:call-template name="code"/>
   </MD_Identifier>
</xsl:template>

<xsl:template name="RS_Identifier">
   <RS_Identifier>
      <xsl:call-template name="code"/>
   </RS_Identifier>
</xsl:template>
 
 <xsl:template name="code">
     <code>
         <xsl:call-template name="CharacterString"/>
     </code>
 </xsl:template>
 
 
<xsl:template match="topicCategories/IngridDocument/topicCategory">
  <topicCategory>
       <xsl:call-template name="MD_TopicCategoryCode"/>
   </topicCategory>
</xsl:template>

<xsl:template name="MD_TopicCategoryCode">
        
        <MD_TopicCategoryCode>
             <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_TopicCategoryCode</xsl:attribute>
                 <xsl:attribute name="codeListValue"><xsl:value-of select="."/></xsl:attribute>
	</MD_TopicCategoryCode>        
</xsl:template>



<xsl:template name="citation">
  
      <citation>
           <xsl:call-template name="CI_Citation"/>
      </citation>

</xsl:template>



<xsl:template name="CI_Citation">
   
   <CI_Citation>
      <xsl:apply-templates select="title"/>
      
      <!--xsl:apply-templates select="alternateTitle"/>
      <xsl:apply-templates select="date" mode="citation"/>
      <xsl:apply-templates select="edition"/>
      <xsl:apply-templates select="editionDate"/>
      <xsl:apply-templates select="citedResponsibleParty"/>
      <xsl:apply-templates select="presentationForm"/>
      <xsl:apply-templates select="series"/>
      <xsl:apply-templates select="ISBN"/>
      <xsl:apply-templates select="ISSN"/-->
  
  </CI_Citation>

</xsl:template>


<xsl:template match="title">
  <title>
       <xsl:call-template name="CharacterString"/>
   </title> 
</xsl:template>



<xsl:template match="contacts/IngridDocument">
  
 <contact>
   <xsl:call-template name="CI_ResponsibleParty"/>
 </contact>

</xsl:template>



<xsl:template name="CI_ResponsibleParty">
  
  <CI_ResponsibleParty>
      <xsl:apply-templates select="individualName"/>
      <xsl:apply-templates select="organisationName"/>
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
        <xsl:apply-templates select="linkage"/>
        <xsl:apply-templates select="function"/>
   </CI_OnlineResource>
</xsl:template>


<xsl:template match="function">
   <function>
        <xsl:call-template name="CI_OnLineFunctionCode"/>   
   </function>
</xsl:template>



<xsl:template name="CI_OnLineFunctionCode">
  
    <CI_OnLineFunctionCode>
		<xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?CI_OnLineFunctionCode</xsl:attribute>
             <xsl:attribute name="codeListValue"><xsl:value-of select="."/></xsl:attribute>
   </CI_OnLineFunctionCode>    
   
</xsl:template>



<xsl:template match="linkage">
   <linkage>
        <xsl:call-template name="URL"/>
   </linkage>
</xsl:template>



<xsl:template name="address">
  
  <address>
     <xsl:call-template name="CI_Address"/>
   </address>
   
</xsl:template>


<xsl:template name="CI_Address">    
   <CI_Address>
      <xsl:apply-templates select="deliveryPoint"/>
      <xsl:apply-templates select="city"/>
      <xsl:apply-templates select="administrativeArea"/>
      <xsl:apply-templates select="postalCode"/>
      <xsl:apply-templates select="country"/>
      <xsl:apply-templates select="electronicMailAddress"/>
    </CI_Address>
</xsl:template>



<xsl:template match="deliveryPoint">
 <deliveryPoint>
      <xsl:call-template name="CharacterString"/>
   </deliveryPoint>
</xsl:template>

<xsl:template match="city">
   <city>
      <xsl:call-template name="CharacterString"/>
   </city>
</xsl:template>

<xsl:template match="administrativeArea">
   <administrativeArea>
      <xsl:call-template name="CharacterString"/>
   </administrativeArea>
</xsl:template>

<xsl:template match="postalCode">
   <postalCode>
      <xsl:call-template name="CharacterString"/>
   </postalCode>
</xsl:template>

<xsl:template match="country">
   <country>
      <xsl:call-template name="CharacterString"/>
   </country>
</xsl:template>

<xsl:template match="electronicMailAddress">
   <electronicMailAddress>
      <xsl:call-template name="CharacterString"/>
   </electronicMailAddress>
</xsl:template>




<xsl:template name="phone">
  
   <phone>
      <xsl:call-template name="CI_Telephone"/>
   </phone>
   
</xsl:template>


<xsl:template name="CI_Telephone">

    
     <xsl:apply-templates select="voice"/>
     <xsl:apply-templates select="facsimile"/>

</xsl:template>



<xsl:template match="voice">
   <voice>
      <xsl:call-template name="CharacterString"/>
   </voice>
</xsl:template>

<xsl:template match="facsimile">
  <facsimile>
      <xsl:call-template name="CharacterString"/>
   </facsimile>
</xsl:template>



<xsl:template match="organisationName">
  
 <organisationName>
      <xsl:call-template name="CharacterString"/>
   </organisationName>
   
</xsl:template>



<xsl:template match="individualName">
  
 <individualName>
      <xsl:call-template name="CharacterString"/>
   </individualName>
   
</xsl:template>



<xsl:template match="role">
     <role>
	 <xsl:call-template name="CI_RoleCode"/>         
    </role>
</xsl:template>



<xsl:template name="CI_RoleCode">
  
    <CI_RoleCode>
		<xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?CI_RoleCode</xsl:attribute>
             <xsl:attribute name="codeListValue"><xsl:value-of select="."/></xsl:attribute>
   </CI_RoleCode>    
   
</xsl:template>





<xsl:template match="hierarchyLevel">

     <hierarchyLevel>
        <xsl:call-template name="MD_ScopeCode"/>
     </hierarchyLevel>
   
  </xsl:template>


 <xsl:template match="fileIdentifier">

     <fileIdentifier>
        <xsl:call-template name="CharacterString"/>
     </fileIdentifier>
   
  </xsl:template>


   

  
  <xsl:template name="MD_ScopeCode">
        
        <MD_ScopeCode>
             <xsl:attribute name="codeList">http://www.tc211.org/ISO19139/resources/codeList.xml?MD_ScopeCode</xsl:attribute>
                 <xsl:attribute name="codeListValue"><xsl:value-of select="."/></xsl:attribute>
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
      <xsl:value-of select="."/>
  </Date>
</xsl:template>

<xsl:template name="URL">
   <URL>
      <xsl:value-of select="."/>
  </URL>
</xsl:template>

<xsl:template name="positiveInteger">
   <positiveInteger>
      <xsl:value-of select="."/>
  </positiveInteger>
</xsl:template>


<xsl:template name="Real">
   <Real>
      <xsl:value-of select="."/>
  </Real>
</xsl:template>


<xsl:template name="Decimal">
   <Decimal>
      <xsl:value-of select="."/>
  </Decimal>
</xsl:template>


<xsl:template name="_Number">
  
      <xsl:value-of select="."/>
  
</xsl:template>

  
  
</xsl:stylesheet>
