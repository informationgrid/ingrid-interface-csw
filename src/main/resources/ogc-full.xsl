<?xml version="1.0" encoding="UTF-8"?>
<!--
  **************************************************-
  ingrid-interface-csw
  ==================================================
  Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:idf="http://www.portalu.de/IDF/1.0"
                xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:gco="http://www.isotc211.org/2005/gco"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:dct="http://purl.org/dc/terms/"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:csw="http://www.opengis.net/cat/csw/2.0.2"
                exclude-result-prefixes="idf xsi">
    <xsl:output method="xml"/>
    <xsl:strip-space elements="*"/>

    <xsl:template match="/">
        <csw:Record>
            <!-- Apply the following templates -->
            <xsl:apply-templates select="//gmd:fileIdentifier"/>
            <xsl:apply-templates
                    select="//gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation//gmd:CI_Date//gmd:date"/>
            <xsl:apply-templates select="//gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation//gmd:title"/>
            <xsl:apply-templates select="//gmd:hierarchyLevel"/>
            <xsl:apply-templates select="//gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword"/>
            <xsl:apply-templates select="//gmd:dateStamp"/>
            <xsl:apply-templates select="//gmd:identificationInfo/gmd:MD_DataIdentification/gmd:abstract"/>
            <xsl:apply-templates
                    select="//gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation//gmd:alternateTitle"/>
            <xsl:apply-templates
                    select="//gmd:resourceConstraints//gmd:accessConstraints | //gmd:resourceConstraints//gmd:useConstraints"/>
            <xsl:apply-templates
                    select="//gmd:dataQualityInfo//gmd:lineage/gmd:LI_Lineage/gmd:source/gmd:LI_Source/gmd:description"/>
            <xsl:apply-templates
                    select="//gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine//gmd:CI_OnlineResource | //gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine//idf:idfOnlineResource"/>
        </csw:Record>
    </xsl:template>

    <!-- uuid -->
    <xsl:template match="gmd:fileIdentifier">
        <dc:identifier>
            <xsl:value-of select="gco:CharacterString"/>
        </dc:identifier>
    </xsl:template>
    <!-- date -->
    <xsl:template match="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation//gmd:CI_Date//gmd:date">
        <dc:date>
            <xsl:value-of select="gco:DateTime"/>
        </dc:date>
    </xsl:template>
    <!-- title -->
    <xsl:template match="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation//gmd:title">
        <dc:title>
            <xsl:value-of select="gco:CharacterString"/>
        </dc:title>
    </xsl:template>
    <!-- type -->
    <xsl:template match="gmd:hierarchyLevel">
        <dc:type>
            <xsl:value-of select="gmd:MD_ScopeCode"/>
        </dc:type>
    </xsl:template>
    <!-- keywords -->
    <xsl:template match="gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword">
        <dc:subject>
            <xsl:value-of select="gco:CharacterString"/>
        </dc:subject>
    </xsl:template>
    <!-- modified -->
    <xsl:template match="gmd:dateStamp">
        <dct:modified>
            <xsl:value-of select="gco:Date"/>
        </dct:modified>
    </xsl:template>
    <!-- abstract -->
    <xsl:template match="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation//gmd:alternateTitle">
        <dct:abstract>
            <xsl:value-of select="gco:CharacterString"/>
        </dct:abstract>
    </xsl:template>
    <!-- description -->
    <xsl:template match="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:abstract">
        <dc:description>
            <xsl:value-of select="gco:CharacterString"/>
        </dc:description>
    </xsl:template>
    <!-- rights -->
    <xsl:template match="gmd:resourceConstraints//gmd:accessConstraints | gmd:resourceConstraints//gmd:useConstraints">
        <dc:rights>
            <xsl:value-of select="gmd:MD_RestrictionCode"/>
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
        <dc:URI description="{gmd:name/gco:CharacterString}">
            <xsl:value-of select="gmd:linkage/gmd:URL"/>
        </dc:URI>
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

</xsl:stylesheet>