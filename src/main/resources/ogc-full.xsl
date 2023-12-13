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
                xmlns:gmx="http://www.isotc211.org/2005/gmx"
                xmlns:gml="http://www.opengis.net/gml/3.2" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:gts="http://www.isotc211.org/2005/gts" xmlns:srv="http://www.isotc211.org/2005/srv"
                xmlns:igctx="https://www.ingrid-oss.eu/schemas/igctx"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                xmlns:dct="http://purl.org/dc/terms/"
                xmlns:ows="http://www.opengis.net/ows"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:geonet="http://www.fao.org/geonetwork" xmlns:csw="http://www.opengis.net/cat/csw/2.0.2"
                exclude-result-prefixes="idf xsi">
    <xsl:output method="xml" />

    <xsl:template match="/">
        <csw:Record>
            <!-- Apply the following templates -->
            <xsl:apply-templates select="//gmd:fileIdentifier"/>
            <xsl:apply-templates select="//gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation//gmd:CI_Date//gmd:date"/>
            <xsl:apply-templates select="//gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation//gmd:title"/>
        </csw:Record>
    </xsl:template>

    <xsl:template match="gmd:fileIdentifier">
        <dc:identifier>
            <xsl:value-of select="gco:CharacterString"/>
        </dc:identifier>
    </xsl:template>
    <xsl:template match="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation//gmd:CI_Date//gmd:date">
        <dc:date>
            <xsl:value-of select="gco:DateTime"/>
        </dc:date>
    </xsl:template>
    <xsl:template match="gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation//gmd:title">
        <dc:title>
            <xsl:value-of select="gco:CharacterString"/>
        </dc:title>
    </xsl:template>



</xsl:stylesheet>