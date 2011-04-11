<?xml version="1.0"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:idf="http://www.portalu.de/IDF/1.0"
	xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:gco="http://www.isotc211.org/2005/gco"
	xmlns:gml="http://www.opengis.net/gml" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	exclude-result-prefixes="idf xsi">
	<xsl:output method="xml" />
	<xsl:strip-space elements="*"/>

	<xsl:template match="idf:idfMdMetadata">
		<gmd:MD_Metadata>
			<xsl:apply-templates select="@*|node()" />
		</gmd:MD_Metadata>
	</xsl:template>

	<xsl:template match="@*|*[(namespace-uri() = 'http://www.isotc211.org/2005/gmd' or namespace-uri() = 'http://www.isotc211.org/2005/gco' or namespace-uri() = 'http://www.opengis.net/gml') and namespace-uri() != 'http://www.portalu.de/IDF/1.0']">
		<xsl:element name="{name(.)}" namespace="{namespace-uri(.)}">
			<xsl:copy-of select="namespace::*[name(.)!='idf' and name(.)!='fn' and name(.)!='fo' and name(.)!='ms' and name(.)!='csw' and name(.)!='wfs' and name(.)!='xsi' and name(.)!='xs' and name(.)!='']" />
			<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<!--  filter IDF_ResponsibleParty_Type specific elements -->
	<xsl:template match="idf:last-modified" />
	<xsl:template match="idf:additionalOnlineResource" />
	<xsl:template match="idf:hierarchyParty" />
	<xsl:template match="idf:subordinatedParty" />
	<xsl:template match="idf:responsibleParty" />
	<xsl:template match="idf:objectReference" />
	<xsl:template match="@type" />
	<!--  filter IDF_MD_Metadata_Type specific elements -->
	<xsl:template match="idf:superiorReference" />
	<xsl:template match="idf:subordinatedReference" />
	<xsl:template match="idf:crossReference" />
	<xsl:template match="idf:additionalDataSection" />
	<!--  both -->
	<xsl:template match="@orig-uuid" />
	<xsl:template match="@xsi:schemaLocation" />

<!--
    Trim Text nodes 
 -->
    <xsl:template match='text()'>
        <xsl:variable name="x" select="string(.)"/>
        <xsl:value-of select="substring($x, 
    string-length(substring-before($x, substring(normalize-space($x), 1, 1))) + 1,
                  string-length($x) - string-length(substring-before($x, substring(normalize-space($x), 1,1))) - string-length(substring-after($x,substring(normalize-space($x),string-length(normalize-space($x)), 1))))"/>        
    </xsl:template>

	<xsl:template match="idf:idfResponsibleParty">
		<gmd:CI_ResponsibleParty>
			<xsl:apply-templates select="@*|node()" />
		</gmd:CI_ResponsibleParty>
	</xsl:template>

</xsl:stylesheet>