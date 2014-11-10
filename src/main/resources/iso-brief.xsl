<?xml version="1.0" encoding="UTF-8"?>
<!--
  **************************************************-
  ingrid-interface-csw
  ==================================================
  Copyright (C) 2014 wemove digital solutions GmbH
  ==================================================
  Licensed under the EUPL, Version 1.1 or – as soon they will be
  approved by the European Commission - subsequent versions of the
  EUPL (the "Licence");
  
  You may not use this work except in compliance with the Licence.
  You may obtain a copy of the Licence at:
  
  http://ec.europa.eu/idabc/eupl5
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the Licence is distributed on an "AS IS" basis,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the Licence for the specific language governing permissions and
  limitations under the Licence.
  **************************************************#
  -->


<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
										xmlns:csw="http://www.opengis.net/cat/csw/2.0.2"
										xmlns:gmd="http://www.isotc211.org/2005/gmd"
										xmlns:gco="http://www.isotc211.org/2005/gco"
										xmlns:srv="http://www.isotc211.org/2005/srv"
										xmlns:ows="http://www.opengis.net/ows"
										xmlns:geonet="http://www.fao.org/geonetwork"
                                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	
	<xsl:param name="displayInfo"/>
	
	<!-- =================================================================== -->

	<xsl:template match="gmd:MD_Metadata|*[@gco:isoType='gmd:MD_Metadata']">
		<xsl:variable name="info" select="geonet:info"/>
		<xsl:copy>
            <xsl:apply-templates select="@xsi:schemaLocation"/>  
			<xsl:apply-templates select="gmd:fileIdentifier"/>
			<xsl:apply-templates select="gmd:hierarchyLevel"/>
			<xsl:apply-templates select="gmd:identificationInfo"/>
		</xsl:copy>
	</xsl:template>

    <!-- =================================================================== -->

    <!-- keep schema location in MD_Metadata, see INGRID-2306 -->
    <xsl:template match="@xsi:schemaLocation">
        <xsl:attribute name="xsi:schemaLocation">
            <xsl:value-of select="."/>
        </xsl:attribute>
    </xsl:template>

	<!-- =================================================================== -->

	<xsl:template match="gmd:MD_DataIdentification|
		*[@gco:isoType='gmd:MD_DataIdentification']|
		srv:SV_ServiceIdentification">
		<xsl:copy>
			<xsl:apply-templates select="gmd:citation"/>
			<xsl:apply-templates select="gmd:graphicOverview"/>
			<xsl:apply-templates select="gmd:extent[child::gmd:EX_Extent[child::gmd:geographicElement]]|
				srv:extent[child::gmd:EX_Extent[child::gmd:geographicElement]]"/>
			<xsl:apply-templates select="srv:serviceType"/>
			<xsl:apply-templates select="srv:serviceTypeVersion"/>
		</xsl:copy>
	</xsl:template>

	<!-- =================================================================== -->

	<xsl:template match="gmd:MD_BrowseGraphic">
		<xsl:copy>
			<xsl:apply-templates select="gmd:fileName"/>
		</xsl:copy>
	</xsl:template>
	
	<!-- =================================================================== -->
	
	<xsl:template match="gmd:EX_Extent">
        <xsl:copy>
        	<xsl:apply-templates select="gmd:geographicElement[child::gmd:EX_GeographicBoundingBox]"/>
        </xsl:copy>
	</xsl:template>
	
	<xsl:template match="gmd:EX_GeographicBoundingBox">
        <xsl:copy>
            <xsl:apply-templates select="gmd:westBoundLongitude"/>
        	<xsl:apply-templates select="gmd:eastBoundLongitude"/>
            <xsl:apply-templates select="gmd:southBoundLatitude"/>
        	<xsl:apply-templates select="gmd:northBoundLatitude"/>
        </xsl:copy>
	</xsl:template>
	
	<!-- =================================================================== -->
	
	<xsl:template match="gmd:CI_Citation">
        <xsl:copy>
        	<xsl:apply-templates select="gmd:title"/>
        </xsl:copy>
    </xsl:template>
	
	<!-- === copy template ================================================= -->

	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
    </xsl:template>

    <!-- =================================================================== -->

</xsl:stylesheet>



