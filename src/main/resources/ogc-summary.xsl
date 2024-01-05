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

    <!-- Apply templates without the 'excludeElements' mode -->
    <xsl:template match="/">
        <xsl:apply-templates select="@*|node()"/>
    </xsl:template>
</xsl:stylesheet>