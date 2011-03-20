<?xml version="1.0"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:idf="http://www.portalu.de/IDF/1.0"
	xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:gml="http://www.opengis.net/gml">
	<xsl:output method="xml" />

	<xsl:template match="idf:idfMdMetadata">
		<gmd:Metadata>
			<xsl:apply-templates select="@*|node()" />
		</gmd:Metadata>
	</xsl:template>
	
	<xsl:template match="*[(namespace-uri() = 'http://www.isotc211.org/2005/gmd' or namespace-uri() = 'http://www.isotc211.org/2005/gco' or namespace-uri() = 'http://www.opengis.net/gml') and namespace-uri() != 'http://www.portalu.de/IDF/1.0']">
		<xsl:copy><xsl:apply-templates select="@*|node()" /></xsl:copy>
	</xsl:template>

    <xsl:template match="idf:last-modified" />
    <xsl:template match="idf:superiorParty" />
    <xsl:template match="idf:subordinatedParty" />
    <xsl:template match="idf:responsibleParty" />
    <xsl:template match="idf:objectReference" />
    <xsl:template match="@orig-uuid" />
    <xsl:template match="@type" />
    <xsl:template match="idf:superiorReference" />
    <xsl:template match="idf:subordinatedReference" />
    <xsl:template match="idf:crossReference" />

	<xsl:template match="idf:idfResponsibleParty">
		<gmd:CI_ResponsibleParty>
			<xsl:apply-templates select="@*|node()" />
		</gmd:CI_ResponsibleParty>
	</xsl:template>

	<!--

		<xsl:template match="idf:idfMdMetadata"> <gmd:MD_Metadata>
		<xsl:apply-templates select="node()" /> </gmd:MD_Metadata>
		</xsl:template> <xsl:template match="idf:idfMdMetadata/node()">
		<xsl:choose> <xsl:when test="idf:*"> </xsl:when> <xsl:otherwise>
		<xsl:copy> <xsl:apply-templates /> </xsl:copy> </xsl:otherwise>
		</xsl:choose> </xsl:template> <xsl:template
		match="idf:idfMdMetadata//*/node()"> <xsl:choose> <xsl:when
		test="idf:*"> </xsl:when> <xsl:otherwise> <xsl:copy>
		<xsl:apply-templates /> </xsl:copy> </xsl:otherwise> </xsl:choose>
		</xsl:template>
	-->

	<!--
		<xsl:template match="idf:idfMdMetadata"> <gmd:MD_Metadata>
		<xsl:apply-templates/> </gmd:MD_Metadata> </xsl:template>

		<xsl:template match="idf:idfMdMetadata/idf:*" /> <xsl:template
		match="idf:idfMdMetadata/gmd:*"> <xsl:copy> <xsl:copy-of select="*"/>
		</xsl:copy> </xsl:template>
	-->

</xsl:stylesheet>