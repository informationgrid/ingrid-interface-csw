<?xml version="1.0"?>
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
				xmlns:gmx="http://www.isotc211.org/2005/gmx"
				xmlns:gml32="http://www.opengis.net/gml/3.2" xmlns:gml="http://www.opengis.net/gml" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				xmlns:gts="http://www.isotc211.org/2005/gts" xmlns:srv="http://www.isotc211.org/2005/srv"
				xmlns:xlink="http://www.w3.org/1999/xlink"
				exclude-result-prefixes="idf xsi gml32">
	<xsl:output omit-xml-declaration="yes" indent="yes"/>
	<xsl:strip-space elements="*" />


	<xsl:template match="idf:idfMdMetadata">
		<gmd:MD_Metadata>
			<xsl:apply-templates select="@*|node()" />
		</gmd:MD_Metadata>
	</xsl:template>
	<xsl:template match="@uuid[parent::idf:idfMdMetadata]">
		<xsl:attribute name="uuid">
			<xsl:value-of select="."/>
		</xsl:attribute>
	</xsl:template>
	<xsl:template match="@id[parent::idf:idfMdMetadata]">
		<xsl:attribute name="id">
			<xsl:value-of select="."/>
		</xsl:attribute>
	</xsl:template>
	<!-- keep schema location in MD_Metadata, see INGRID-2306 -->
	<xsl:template match="@xsi:schemaLocation[parent::idf:idfMdMetadata]">
		<xsl:attribute name="xsi:schemaLocation">
			<xsl:value-of select="."/>
		</xsl:attribute>
	</xsl:template>

	<xsl:template
			match="@*|*[(namespace-uri() = 'http://www.isotc211.org/2005/gmd' or namespace-uri() = 'http://www.isotc211.org/2005/gco' or namespace-uri() = 'http://www.opengis.net/gml'  or namespace-uri() = 'http://www.opengis.net/gml/3.2' or namespace-uri() = 'http://www.isotc211.org/2005/gmx' or namespace-uri() = 'http://www.isotc211.org/2005/gts' or namespace-uri() = 'http://www.isotc211.org/2005/srv') and namespace-uri() != 'http://www.portalu.de/IDF/1.0']">
		<xsl:element name="{name(.)}" namespace="{namespace-uri(.)}">
			<xsl:copy-of
					select="namespace::*[name(.)!='gml' and name(.)!='idf' and name(.)!='srv' and name(.)!='ms' and name(.)!='csw' and name(.)!='xlink' and name(.)!='xsi' and name(.)!='xs' and name(.)!='']" />
			<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

	<!-- START transform 5.2.1 -> 4.6.9 -->
	<xsl:template match="gmd:MD_ScopeCode[./@codeList='http://standards.iso.org/iso/19139/resources/gmxCodelists.xml#MD_ScopeCode']">
		<xsl:variable name="codeListValue" select="@codeListValue" />
		<gmd:MD_ScopeCode codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/gmxCodelists.xml#MD_ScopeCode" codeListValue="{$codeListValue}"><xsl:value-of select="@codeListValue"/></gmd:MD_ScopeCode>
	</xsl:template>
	<xsl:template match="gmd:CI_RoleCode[./@codeList='http://standards.iso.org/iso/19139/resources/gmxCodelists.xml#CI_RoleCode']">
		<xsl:variable name="codeListValue" select="@codeListValue" />
		<gmd:CI_RoleCode codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/gmxCodelists.xml#CI_RoleCode" codeListValue="{$codeListValue}"></gmd:CI_RoleCode>
	</xsl:template>
	<xsl:template match="gmd:CI_DateTypeCode[./@codeList='http://standards.iso.org/iso/19139/resources/gmxCodelists.xml#CI_DateTypeCode']">
		<xsl:variable name="codeListValue" select="@codeListValue" />
		<gmd:CI_DateTypeCode codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/gmxCodelists.xml#CI_DateTypeCode" codeListValue="{$codeListValue}"></gmd:CI_DateTypeCode>
	</xsl:template>
	<xsl:template match="gmd:MD_MaintenanceFrequencyCode[./@codeList='http://standards.iso.org/iso/19139/resources/gmxCodelists.xml#MD_MaintenanceFrequencyCode']">
		<xsl:variable name="codeListValue" select="@codeListValue" />
		<gmd:MD_MaintenanceFrequencyCode codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/gmxCodelists.xml#MD_MaintenanceFrequencyCode" codeListValue="{$codeListValue}"></gmd:MD_MaintenanceFrequencyCode>
	</xsl:template>
	<xsl:template match="gmd:MD_SpatialRepresentationTypeCode[./@codeList='http://standards.iso.org/iso/19139/resources/gmxCodelists.xml#MD_SpatialRepresentationTypeCode']">
		<xsl:variable name="codeListValue" select="@codeListValue" />
		<gmd:MD_SpatialRepresentationTypeCode codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/gmxCodelists.xml#MD_SpatialRepresentationTypeCode" codeListValue="{$codeListValue}"></gmd:MD_SpatialRepresentationTypeCode>
	</xsl:template>
	<xsl:template match="gmd:MD_CharacterSetCode[./@codeList='http://standards.iso.org/iso/19139/resources/gmxCodelists.xml#MD_CharacterSetCode']">
		<xsl:variable name="codeListValue" select="@codeListValue" />
		<gmd:MD_CharacterSetCode codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/gmxCodelists.xml#MD_CharacterSetCode" codeListValue="{$codeListValue}"></gmd:MD_CharacterSetCode>
	</xsl:template>
	<xsl:template match="gmd:MD_RestrictionCode[./@codeList='http://standards.iso.org/iso/19139/resources/gmxCodelists.xml#MD_RestrictionCode']">
		<xsl:variable name="codeListValue" select="@codeListValue" />
		<gmd:MD_RestrictionCode codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/gmxCodelists.xml#MD_RestrictionCode" codeListValue="{$codeListValue}"></gmd:MD_RestrictionCode>
	</xsl:template>
	<xsl:template match="gmd:MD_Keywords[gmd:type/gmd:MD_KeywordTypeCode/@codeList='http://standards.iso.org/iso/19139/resources/gmxCodelists.xml#MD_KeywordTypeCode']">
		<xsl:variable name="codeListValue" select="gmd:type/gmd:MD_KeywordTypeCode/@codeListValue" />
		<xsl:variable name="thesaurusNode" select="./gmd:thesaurusName" />
		<xsl:choose>
			<xsl:when test="$thesaurusNode">
				<gmd:MD_Keywords>
					<xsl:apply-templates select="@*|node()"/>
				</gmd:MD_Keywords>
			</xsl:when>
			<xsl:otherwise>
				<gmd:MD_Keywords>
					<xsl:apply-templates select="gmd:keyword" />
					<gmd:type>
						<gmd:MD_KeywordTypeCode codeList="http://www.tc211.org/ISO19139/resources/codeList.xml#MD_KeywordTypeCode" codeListValue="{$codeListValue}"></gmd:MD_KeywordTypeCode>
					</gmd:type>
					<gmd:thesaurusName>
						<gmd:CI_Citation>
							<gmd:title>
								<gco:CharacterString>OGDD-Kategorien</gco:CharacterString>
							</gmd:title>
							<gmd:date>
								<gmd:CI_Date>
									<gmd:date>
										<gco:Date>2012-11-27</gco:Date>
									</gmd:date>
									<gmd:dateType>
										<gmd:CI_DateTypeCode codeList="http://standards.iso.org/iso/19139/resources/gmxCodelists.xml#CI_DateTypeCode" codeListValue="publication">publication</gmd:CI_DateTypeCode>
									</gmd:dateType>
								</gmd:CI_Date>
							</gmd:date>
						</gmd:CI_Citation>
					</gmd:thesaurusName>
				</gmd:MD_Keywords>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="gmd:RS_Identifier">
		<gmd:RS_Identifier>
			<gmd:code>
				<gco:CharacterString><xsl:value-of select="gmd:code/gmx:Anchor"/></gco:CharacterString>
			</gmd:code>
			<gmd:codeSpace>
				<gco:CharacterString>EPSG</gco:CharacterString>
			</gmd:codeSpace>
		</gmd:RS_Identifier>
	</xsl:template>
	<xsl:template match="gmd:resourceConstraints[gmd:MD_LegalConstraints/gmd:useConstraints/gmd:MD_RestrictionCode/@codeListValue='otherRestrictions']">
        <gmd:resourceConstraints>
			<gmd:MD_LegalConstraints>
				<gmd:useLimitation>
					<gco:CharacterString>Nutzungsbedingungen: <xsl:call-template name="replace-licence-codelist"><xsl:with-param name="text" select="gmd:MD_LegalConstraints/gmd:otherConstraints[1]/gco:CharacterString" /></xsl:call-template></gco:CharacterString>
				</gmd:useLimitation>
			</gmd:MD_LegalConstraints>
		</gmd:resourceConstraints>
		<gmd:resourceConstraints>
			<gmd:MD_LegalConstraints>
				<gmd:useConstraints>
					<gmd:MD_RestrictionCode codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/gmxCodelists.xml#MD_RestrictionCode" codeListValue="license"/>
				</gmd:useConstraints>
				<gmd:useConstraints>
					<gmd:MD_RestrictionCode codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/gmxCodelists.xml#MD_RestrictionCode" codeListValue="otherRestrictions"/>
				</gmd:useConstraints>
				<gmd:otherConstraints>
					<gco:CharacterString>Nutzungsbedingungen: <xsl:call-template name="replace-licence-codelist"><xsl:with-param name="text" select="gmd:MD_LegalConstraints/gmd:otherConstraints[1]/gco:CharacterString" /></xsl:call-template></gco:CharacterString>
				</gmd:otherConstraints>
				<gmd:otherConstraints>
					<gco:CharacterString><xsl:value-of select="gmd:MD_LegalConstraints/gmd:otherConstraints[2]/gco:CharacterString"/></gco:CharacterString>
				</gmd:otherConstraints>
				<gmd:otherConstraints>
					<gco:CharacterString><xsl:call-template name="replace-licence-codelist"><xsl:with-param name="text" select="gmd:MD_LegalConstraints/gmd:otherConstraints[3]/gco:CharacterString" /></xsl:call-template></gco:CharacterString>
				</gmd:otherConstraints>
			</gmd:MD_LegalConstraints>
		</gmd:resourceConstraints>
	</xsl:template>
	<xsl:template match="gmd:resourceConstraints[gmd:MD_LegalConstraints/gmd:accessConstraints/gmd:MD_RestrictionCode/@codeListValue='otherRestrictions']">
		<gmd:resourceConstraints>
			<gmd:MD_LegalConstraints>
				<gmd:accessConstraints>
					<gmd:MD_RestrictionCode codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/gmxCodelists.xml#MD_RestrictionCode" codeListValue="otherRestrictions">otherRestrictions</gmd:MD_RestrictionCode>
				</gmd:accessConstraints>
				<gmd:otherConstraints>
					<gco:CharacterString><xsl:value-of select="gmd:MD_LegalConstraints/gmd:otherConstraints[1]/gmx:Anchor"/></gco:CharacterString>
				</gmd:otherConstraints>
			</gmd:MD_LegalConstraints>
		</gmd:resourceConstraints>
	</xsl:template>
	<xsl:template match="accessConstraints[parent::idf:idfLegalBasisConstraints]">
		<gmd:resourceConstraints>
			<gmd:MD_LegalConstraints>
				<gmd:accessConstraints>
					<gmd:MD_RestrictionCode codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/codelist/gmxCodelists.xml#MD_RestrictionCode" codeListValue="otherRestrictions">otherRestrictions</gmd:MD_RestrictionCode>
				</gmd:accessConstraints>
				<gmd:otherConstraints>
					<gco:CharacterString><xsl:value-of select="gmd:MD_LegalConstraints/gmd:otherConstraints[1]/gco:CharacterString"/></gco:CharacterString>
				</gmd:otherConstraints>
			</gmd:MD_LegalConstraints>
		</gmd:resourceConstraints>
	</xsl:template>
	<xsl:template match="gmd:keyword/gco:CharacterString">
		<gco:CharacterString>
			<xsl:call-template name="replace-opendata-categories"><xsl:with-param name="text" select="." /></xsl:call-template>
		</gco:CharacterString>
	</xsl:template>

	<xsl:template name="replace-opendata-categories">
		<xsl:param name="text" />
		<xsl:choose>
			<xsl:when test="$text = ''" >
				<!-- Prevent this routine from hanging -->
				<xsl:value-of select="$text" />
			</xsl:when>
			<xsl:when test="$text = 'AGRI'" >
				<xsl:value-of select="'Wirtschaft und Arbeit'" />
			</xsl:when>
			<xsl:when test="$text = 'EDUC'" >
				<xsl:value-of select="'Kultur, Freizeit, Sport und Tourismus'" />
			</xsl:when>
			<xsl:when test="$text = 'ENVI'" >
				<xsl:value-of select="'Umwelt und Klima'" />
			</xsl:when>
			<xsl:when test="$text = 'ENER'" >
				<xsl:value-of select="'Umwelt und Klima'" />
			</xsl:when>
			<xsl:when test="$text = 'TRAN'" >
				<xsl:value-of select="'Transport und Verkehr'" />
			</xsl:when>
			<xsl:when test="$text = 'TECH'" >
				<xsl:value-of select="'Bildung und Wissenschaft'" />
			</xsl:when>
			<xsl:when test="$text = 'ECON'" >
				<xsl:value-of select="'Wirtschaft und Arbeit'" />
			</xsl:when>
			<xsl:when test="$text = 'SOCI'" >
				<xsl:value-of select="'Bevölkerung'" />
			</xsl:when>
			<xsl:when test="$text = 'HEAL'" >
				<xsl:value-of select="'Gesundheit'" />
			</xsl:when>
			<xsl:when test="$text = 'GOVE'" >
				<xsl:value-of select="'Politik und Wahlen'" />
			</xsl:when>
			<xsl:when test="$text = 'REGI'" >
				<xsl:value-of select="'Öffentliche Verwaltung, Haushalt und Steuern'" />
			</xsl:when>
			<xsl:when test="$text = 'JUST'" >
				<xsl:value-of select="'Gesetze und Justiz'" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

    <xsl:template name="replace-licence-codelist">
        <xsl:param name="text" />
        <xsl:choose>
            <xsl:when test="contains($text, 'Datenlizenz Deutschland Namensnennung 2.0')">
                <xsl:call-template name="string-replace-all">
                    <xsl:with-param name="text" select="$text" />
                    <xsl:with-param name="replace" select="'Datenlizenz Deutschland Namensnennung 2.0'" />
                    <xsl:with-param name="by" select="'Datenlizenz Deutschland – Namensnennung – Version 2.0'" />
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$text" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

	<xsl:template match="@gml:*">
		<xsl:attribute name="gml:{local-name()}" namespace="http://www.opengis.net/gml">
			<xsl:value-of select="." />
		</xsl:attribute>
	</xsl:template>

	<xsl:template match="gml:*">
		<xsl:element name="gml:{local-name()}" namespace="http://www.opengis.net/gml">
			<xsl:apply-templates select="node() | @*" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="gml32:*">
		<xsl:element name="gml:{local-name()}" namespace="http://www.opengis.net/gml">
			<xsl:apply-templates select="node() | @*" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="@uom">
		<xsl:attribute name="gml:{local-name()}" namespace="http://www.opengis.net/gml">
			<xsl:value-of select="." />
		</xsl:attribute>
	</xsl:template>

	<xsl:template match="@gml32:*">
		<xsl:attribute name="gml:{local-name()}" namespace="http://www.opengis.net/gml">
			<xsl:value-of select="." />
		</xsl:attribute>
	</xsl:template>

	<!-- END transform 5.2.1 -> 4.6.9 -->

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
	<xsl:template match="idf:hasAccessConstraint" />
	<xsl:template match="idf:exportCriteria" />
	<xsl:template match="idf:additionalDataSection" />
	<!--  filter IDF_OnlineResource_Type specific elements -->
	<xsl:template match="idf:attachedToField" />
	<!-- filter shared and common attributes -->
	<xsl:template match="@orig-uuid" />
	<!-- filter idf:abstract -->
	<xsl:template match="idf:abstract" />

	<!-- filter idf:keyword and transform them to gmd:keyword -->
	<xsl:template match="idf:keyword">
		<gco:CharacterString>
			<xsl:apply-templates select="node()" />
		</gco:CharacterString>
	</xsl:template>

	<!--
	Trim Text nodes
	-->
	<xsl:template match='text()'>
		<xsl:call-template name="trim">
			<xsl:with-param name="s" select="." />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="idf:idfResponsibleParty">
		<gmd:CI_ResponsibleParty>
			<xsl:apply-templates select="@*|node()" />
		</gmd:CI_ResponsibleParty>
	</xsl:template>
	<xsl:template match="@uuid[parent::idf:idfResponsibleParty]">
		<xsl:attribute name="uuid">
			<xsl:value-of select="."/>
		</xsl:attribute>
	</xsl:template>
	<xsl:template match="@id[parent::idf:idfResponsibleParty]">
		<xsl:attribute name="id">
			<xsl:value-of select="."/>
		</xsl:attribute>
	</xsl:template>

	<xsl:template match="idf:idfOnlineResource">
		<gmd:CI_OnlineResource>
			<xsl:apply-templates select="@*|node()" />
		</gmd:CI_OnlineResource>
	</xsl:template>
	<xsl:template match="@uuid[parent::idf:idfOnlineResource]">
		<xsl:attribute name="uuid">
			<xsl:value-of select="."/>
		</xsl:attribute>
	</xsl:template>
	<xsl:template match="@id[parent::idf:idfOnlineResource]">
		<xsl:attribute name="id">
			<xsl:value-of select="."/>
		</xsl:attribute>
	</xsl:template>

	<xsl:template match="idf:idfLegalBasisConstraints">
		<gmd:MD_LegalConstraints>
			<xsl:apply-templates select="@*|node()" />
		</gmd:MD_LegalConstraints>
	</xsl:template>
	<xsl:template match="@uuid[parent::idf:idfLegalBasisConstraints]">
		<xsl:attribute name="uuid">
			<xsl:value-of select="."/>
		</xsl:attribute>
	</xsl:template>
	<xsl:template match="@id[parent::idf:idfLegalBasisConstraints]">
		<xsl:attribute name="id">
			<xsl:value-of select="."/>
		</xsl:attribute>
	</xsl:template>


	<!-- Template for trimming strings -->
	<xsl:template name="left-trim">
		<xsl:param name="s" />
		<xsl:choose>
			<xsl:when test="substring($s, 1, 1) = ''">
				<xsl:value-of select="$s" />
			</xsl:when>
			<xsl:when test="normalize-space(substring($s, 1, 1)) = ''">
				<xsl:call-template name="left-trim">
					<xsl:with-param name="s" select="substring($s, 2)" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$s" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="right-trim">
		<xsl:param name="s" />
		<xsl:choose>
			<xsl:when test="substring($s, 1, 1) = ''">
				<xsl:value-of select="$s" />
			</xsl:when>
			<xsl:when test="normalize-space(substring($s, string-length($s))) = ''">
				<xsl:call-template name="right-trim">
					<xsl:with-param name="s"
									select="substring($s, 1, string-length($s) - 1)" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$s" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="trim">
		<xsl:param name="s" />
		<xsl:call-template name="right-trim">
			<xsl:with-param name="s">
				<xsl:call-template name="left-trim">
					<xsl:with-param name="s" select="$s" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

    <xsl:template name="string-replace-all">
        <xsl:param name="text" />
        <xsl:param name="replace" />
        <xsl:param name="by" />
        <xsl:choose>
            <xsl:when test="$text = '' or $replace = ''or not($replace)" >
                <!-- Prevent this routine from hanging -->
                <xsl:value-of select="$text" />
            </xsl:when>
            <xsl:when test="contains($text, $replace)">
                <xsl:value-of select="substring-before($text,$replace)" />
                <xsl:value-of select="$by" />
                <xsl:call-template name="string-replace-all">
                    <xsl:with-param name="text" select="substring-after($text,$replace)" />
                    <xsl:with-param name="replace" select="$replace" />
                    <xsl:with-param name="by" select="$by" />
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$text" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
