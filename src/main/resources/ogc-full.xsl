<?xml version="1.0" encoding="UTF-8"?>
<!--
  **************************************************-
  ingrid-interface-csw
  ==================================================
  Copyright (C) 2014 - 2024 wemove digital solutions GmbH
  ==================================================
  Licensed under the EUPL, Version 1.2 or – as soon they will be
  approved by the European Commission - subsequent versions of the
  EUPL (the "Licence");
  
  You may not use this work except in compliance with the Licence.
  You may obtain a copy of the Licence at:
  
  https://joinup.ec.europa.eu/software/page/eupl
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the Licence is distributed on an "AS IS" basis,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the Licence for the specific language governing permissions and
  limitations under the Licence.
  **************************************************#
  -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:idf="http://www.portalu.de/IDF/1.0"
                xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:gco="http://www.isotc211.org/2005/gco"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:dct="http://purl.org/dc/terms/"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:csw="http://www.opengis.net/cat/csw/2.0.2"
                xmlns:gmx="http://www.isotc211.org/2005/gmx"
                xmlns:ows="http://www.opengis.net/ows"
                xmlns:srv="http://www.isotc211.org/2005/srv"
                exclude-result-prefixes="idf xsi">
    <xsl:output method="xml"/>
    <xsl:strip-space elements="*"/>

    <xsl:template match="/">
        <csw:Record xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dct="http://purl.org/dc/terms/"
                    xmlns:geonet="http://www.fao.org/geonetwork" xmlns:ows="http://www.opengis.net/ows">
            <!-- Apply the following templates -->
            <xsl:apply-templates select="//gmd:fileIdentifier"/>  <!-- uuid -->
            <xsl:apply-templates
                    select="//gmd:identificationInfo//gmd:citation//gmd:CI_Date//gmd:date"/> <!-- date -->
            <xsl:apply-templates select="//gmd:identificationInfo//gmd:citation//gmd:title"/> <!-- title -->
            <xsl:apply-templates select="//gmd:hierarchyLevel"/> <!-- type -->
            <xsl:apply-templates select="//gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword"/> <!-- keywords -->
            <xsl:apply-templates select="//gmd:dateStamp"/> <!-- modified -->
            <xsl:apply-templates
                    select="//gmd:identificationInfo//gmd:citation//gmd:alternateTitle"/> <!-- abstract -->
            <xsl:apply-templates select="//gmd:identificationInfo//gmd:abstract"/> <!-- description -->
            <xsl:apply-templates
                    select="//gmd:resourceConstraints//gmd:accessConstraints | //gmd:resourceConstraints//gmd:useConstraints"/> <!-- rights -->
            <xsl:apply-templates select="//gmd:identificationInfo//gmd:language"/> <!-- language -->
            <xsl:apply-templates
                    select="//gmd:dataQualityInfo//gmd:lineage/gmd:LI_Lineage/gmd:source/gmd:LI_Source/gmd:description"/> <!-- source -->
            <xsl:apply-templates
                    select="//gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine//gmd:CI_OnlineResource | //gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine//idf:idfOnlineResource"/> <!-- links -->
            <xsl:apply-templates
                    select="//gmd:identificationInfo//gmd:extent/gmd:EX_Extent//gmd:geographicElement/gmd:EX_GeographicBoundingBox | //gmd:identificationInfo//srv:extent/gmd:EX_Extent//gmd:geographicElement/gmd:EX_GeographicBoundingBox"/> <!-- bounding box -->
        </csw:Record>
    </xsl:template>

    <!-- uuid -->
    <xsl:template match="gmd:fileIdentifier">
        <dc:identifier>
            <xsl:value-of select="gco:CharacterString"/>
        </dc:identifier>
    </xsl:template>
    <!-- date -->
    <xsl:template match="gmd:identificationInfo//gmd:citation//gmd:CI_Date//gmd:date">
        <dc:date>
            <xsl:value-of select="gco:DateTime"/>
        </dc:date>
    </xsl:template>
    <!-- title -->
    <xsl:template match="gmd:identificationInfo//gmd:citation//gmd:title">
        <dc:title>
            <xsl:value-of select="gco:CharacterString"/>
        </dc:title>
    </xsl:template>
    <!-- type -->
    <xsl:template match="gmd:hierarchyLevel">
        <!-- type: might be saved as codeListValue attribute instead as text in the matching tag -->
        <xsl:choose>
            <xsl:when test="gmd:MD_ScopeCode!=''">
                <dc:type>
                    <xsl:value-of select="gmd:MD_ScopeCode"/>
                </dc:type>
            </xsl:when>
            <xsl:otherwise>
                <dc:type>
                    <xsl:value-of select="gmd:MD_ScopeCode/@codeListValue"/>
                </dc:type>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- keywords -->
    <xsl:template match="gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword">
        <xsl:choose>
            <xsl:when test="gmx:Anchor">
                <dc:subject>
                    <xsl:value-of select="gmx:Anchor"/>
                </dc:subject>
            </xsl:when>
            <xsl:when test="gco:CharacterString">
                <dc:subject>
                    <xsl:value-of select="gco:CharacterString"/>
                </dc:subject>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    <!-- modified -->
    <xsl:template match="gmd:dateStamp">
        <dct:modified>
            <xsl:value-of select="gco:Date"/>
        </dct:modified>
    </xsl:template>
    <!-- abstract -->
    <xsl:template match="gmd:identificationInfo//gmd:citation//gmd:alternateTitle">
        <dct:abstract>
            <xsl:value-of select="gco:CharacterString"/>
        </dct:abstract>
    </xsl:template>
    <!-- description -->
    <xsl:template match="gmd:identificationInfo//gmd:abstract">
        <dc:description>
            <xsl:value-of select="gco:CharacterString"/>
        </dc:description>
    </xsl:template>
    <!-- language -->
    <xsl:template match="gmd:identificationInfo//gmd:language">
        <dc:language>
            <xsl:value-of select="gmd:LanguageCode/@codeListValue"/>
        </dc:language>
    </xsl:template>
    <!-- rights -->
    <xsl:template
            match="gmd:resourceConstraints//gmd:accessConstraints[gmd:MD_RestrictionCode[normalize-space()]] | gmd:resourceConstraints//gmd:useConstraints[gmd:MD_RestrictionCode[normalize-space()]]">
        <dc:rights>
            <xsl:value-of select="normalize-space(gmd:MD_RestrictionCode)"/>
        </dc:rights>
    </xsl:template>
    <!-- source -->
    <xsl:template
            match="gmd:dataQualityInfo//gmd:lineage/gmd:LI_Lineage/gmd:source/gmd:LI_Source/gmd:description">
        <dc:source>
            <xsl:value-of select="gco:CharacterString"/>
        </dc:source>
    </xsl:template>
    <!-- links -->
    <xsl:template
            match="gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine//gmd:CI_OnlineResource | gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine//idf:idfOnlineResource">

        <!-- set service type and version  -->
        <xsl:variable name="serviceTypeVersion" select="//srv:serviceTypeVersion/gco:CharacterString"/>
        <!-- <xsl:variable name="serviceType" select="//srv:serviceType/gco:LocalName"/> not used for now -->

        <!-- handle serviceTypeVersion: extract and remove version numbers -->
        <xsl:variable name="serviceTypeResult">
            <xsl:choose>
                <!-- check if the last char of serviceType is a (version) number and call template if yes -->
                <xsl:when test="contains('0123456789', substring($serviceTypeVersion, string-length(.)))">
                    <xsl:call-template name="stripVersion">
                        <xsl:with-param name="input" select="$serviceTypeVersion"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$serviceTypeVersion"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="extractedProtocol" select="translate(substring-after($serviceTypeResult, ':'), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>
        <xsl:variable name="uri" select="gmd:linkage/gmd:URL" />

        <dc:URI>
            <!-- add protocol if it is contained in the uri text or it is equal to csw -->
            <xsl:if test="contains($uri, normalize-space($extractedProtocol)) or contains($uri, 'csw')">
                <xsl:attribute name="protocol">
                    <xsl:value-of select="normalize-space($serviceTypeResult)"/>
                </xsl:attribute>
            </xsl:if>

            <xsl:attribute name="description">
                <xsl:value-of select="gmd:name/gco:CharacterString"/>
            </xsl:attribute>

            <xsl:value-of select="$uri"/>
        </dc:URI>
    </xsl:template>

    <!-- bounding box -->
    <xsl:template
            match="gmd:identificationInfo//gmd:extent/gmd:EX_Extent//gmd:geographicElement/gmd:EX_GeographicBoundingBox | gmd:identificationInfo//srv:extent/gmd:EX_Extent//gmd:geographicElement/gmd:EX_GeographicBoundingBox">
        <ows:BoundingBox crs="urn:ogc:def:crs:EPSG::4326">
            <ows:LowerCorner>
                <xsl:value-of select="gmd:westBoundLongitude/gco:Decimal"/>
                <xsl:text> </xsl:text>
                <xsl:value-of select="gmd:southBoundLatitude/gco:Decimal"/>
            </ows:LowerCorner>
            <ows:UpperCorner>
                <xsl:value-of select="gmd:eastBoundLongitude/gco:Decimal"/>
                <xsl:text> </xsl:text>
                <xsl:value-of select="gmd:northBoundLatitude/gco:Decimal"/>
            </ows:UpperCorner>
        </ows:BoundingBox>
    </xsl:template>


    <!-- Template for trimming strings -->
    <xsl:template name="left-trim">
        <xsl:param name="s"/>
        <xsl:choose>
            <xsl:when test="substring($s, 1, 1) = ''">
                <xsl:value-of select="$s"/>
            </xsl:when>
            <xsl:when test="normalize-space(substring($s, 1, 1)) = ''">
                <xsl:call-template name="left-trim">
                    <xsl:with-param name="s" select="substring($s, 2)"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$s"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="right-trim">
        <xsl:param name="s"/>
        <xsl:choose>
            <xsl:when test="substring($s, 1, 1) = ''">
                <xsl:value-of select="$s"/>
            </xsl:when>
            <xsl:when test="normalize-space(substring($s, string-length($s))) = ''">
                <xsl:call-template name="right-trim">
                    <xsl:with-param name="s"
                                    select="substring($s, 1, string-length($s) - 1)"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$s"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="trim">
        <xsl:param name="s"/>
        <xsl:call-template name="right-trim">
            <xsl:with-param name="s">
                <xsl:call-template name="left-trim">
                    <xsl:with-param name="s" select="$s"/>
                </xsl:call-template>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <!-- Strip (version) numbers and in-between dots starting from the end -->
    <xsl:template name="stripVersion">
        <xsl:param name="input"/>
        <xsl:param name="output" select="''"/>
        <xsl:choose>
            <xsl:when test="$input != ''">
                <!-- If there's still input, process it, start from end -->
                <xsl:variable name="lastChar" select="substring($input, string-length($input))"/>
                <xsl:choose>
                    <!-- If the last character is a digit or dot, do not include it -->
                    <xsl:when test="contains('0123456789.', $lastChar)">
                        <xsl:call-template name="stripVersion">
                            <xsl:with-param name="input" select="substring($input, 1, string-length($input) - 1)"/>
                            <xsl:with-param name="output" select="$output"/>
                        </xsl:call-template>
                    </xsl:when>
                    <!-- If it's not a digit or dot, include it in the output and continue -->
                    <xsl:otherwise>
                        <xsl:call-template name="stripVersion">
                            <xsl:with-param name="input" select="substring($input, 1, string-length($input) - 1)"/>
                            <xsl:with-param name="output" select="concat($lastChar, $output)"/>
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <!-- If there's no more input, output the result -->
            <xsl:otherwise>
                <xsl:value-of select="$output"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
