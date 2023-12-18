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
    <xsl:output method="xml"/>
    <!-- Copy all elements from full -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    <!-- Exclude the following -->
    <xsl:template match="//dc:description"/>
    <xsl:template match="//dc:subject"/>
    <xsl:template match="//dct:abstract"/>
    <xsl:template match="//dc:rights"/>
    <xsl:template match="//dc:URI"/>
    <xsl:template match="//dct:modified"/>
    <xsl:template match="//dc:source"/>

    <!-- Apply templates without the 'excludeElements' mode -->
    <xsl:template match="/">
        <output>
            <xsl:apply-templates select="@*|node()"/>
        </output>
    </xsl:template>
</xsl:stylesheet>