/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw2.filter.impl.geotools;

import de.ingrid.interfaces.csw2.exceptions.CSWInvalidParameterValueException;

public class PropertyNameMapping {

	public static String map(String name, FilterVisitorContext ctx) throws Exception {
		String mappedName = null;

		// common queryable elements
		
		// all text fields
		if (name.equalsIgnoreCase("AnyText")) {
			// anytext --> content
			mappedName = "";
			// MD_Metadata/identificationInfo/MD_DataIdentification/descriptiveKeywords/MD_Keywords/keyword
		} else if (name.equalsIgnoreCase("subject")) {
			mappedName = "t04_search.searchterm";
			// MD_Metadata/identificationInfo/MD_DataIdentification/citation/title
		} else if (name.equalsIgnoreCase("title")) {
			mappedName = "title";
			// MD_Metadata/identificationInfo/MD_DataIdentification/abstract
		} else if (name.equalsIgnoreCase("abstract")) {
			mappedName = "summary";
			// MD_Metadata.distributionInfo.MD_Distribution.distributionFormat.MD_Format.name
		} else if (name.equalsIgnoreCase("format")) {
			mappedName = "t0110_avail_format.format_value";
			// MD_Metadata.fileIdentifier
		} else if (name.equalsIgnoreCase("identifier")) {
			mappedName = "t01_object.obj_id";
			// MD_Metadata.dateStamp .Date
		} else if (name.equalsIgnoreCase("Modified")) {
			mappedName = "t01_object.mod_time";
			// MD_Metadata.hierarchyLevel.MD_ScopeCode@codeListValue
		} else if (name.equalsIgnoreCase("type")) {
			// TODO: must map to t011_obj_geo.hierarchy_level or t01_object.obj_class
			// depending on the value of the query parameter (see mapping)
			mappedName = "";
		} else if (name.equalsIgnoreCase("Association")) {
			// TODO: NOT supported
			throw new CSWInvalidParameterValueException("Search for PropertyName '" + name + "' is not supported by this server.", "PropertyName");
			// MD_Metadata.identificationInfo.MD_DataIdentification.extent.EX_Extent.geographicElement.EX_GeographicBoundingBox.westBoundLongitude
		} else if (name.equalsIgnoreCase("WestBoundLongitude")) {
			mappedName = "x1";
			// MD_Metadata.identificationInfo.MD_DataIdentification.extent.EX_Extent.geographicElement.EX_GeographicBoundingBox.eastBoundLongitude
		} else if (name.equalsIgnoreCase("EastBoundLongitude")) {
			mappedName = "x2";
			// MD_Metadata.identificationInfo.MD_DataIdentification.extent.EX_Extent.geographicElement.EX_GeographicBoundingBox.southBoundLatitude
		} else if (name.equalsIgnoreCase("SouthBoundLatitude")) {
			mappedName = "y1";
			// MD_Metadata.identificationInfo.MD_DataIdentification.extent.EX_Extent.geographicElement.EX_GeographicBoundingBox.northBoundLatitude
		} else if (name.equalsIgnoreCase("NorthBoundLatitude")) {
			mappedName = "y2";			
			// MD_Metadata.referenceSystemInfo.MD_ReferenceSystem.referenceSystemIdentifier.RS_Identifier.codeSpace
		} else if (name.equalsIgnoreCase("Authority")) {
			// TODO: value of the query must be transformed into a wildcard query
			mappedName = "t011_obj_geo.referencesystem_id";	
			// MD_Metadata.referenceSystemInfo.MD_ReferenceSystem.referenceSystemIdentifier.RS_Identifier.code
		} else if (name.equalsIgnoreCase("ID")) {
			// TODO: value of the query must be transformed into a wildcard query
			mappedName = "t011_obj_geo.referencesystem_id";			
			// MD_Metadata.referenceSystemInfo.MD_ReferenceSystem.referenceSystemIdentifier.RS_Identifier.version
		} else if (name.equalsIgnoreCase("Version")) {
			// TODO: NOT supported
			throw new CSWInvalidParameterValueException("Search for PropertyName '" + name + "' is not supported by this server.", "PropertyName");
			
		// Additional queryable properties common to all information resources
			
			// MD_Metadata.identificationInfo.AbstractMD_Identification.citation.CI_Citation.date.CI_Date[dateType.CI_DateTypeCode.@codeListValue='revision'].date.Date
		} else if (name.equalsIgnoreCase("RevisionDate")) {
			mappedName = "t0113_dataset_reference.type:3 t0113_dataset_reference.reference_date";
			// MD_Metadata/identificationInfo/MD_DataIdentification/citation/alternateTitle
		} else if (name.equalsIgnoreCase("AlternateTitle")) {
			mappedName = "t01_object.dataset_alternate_name";
			// MD_Metadata.identificationInfo.AbstractMD_Identification.citation.CI_Citation.date.CI_Date[dateType.CI_DateTypeCode.@codeListValue='creation'].date.Date
		} else if (name.equalsIgnoreCase("CreationDate")) {
			mappedName = "t0113_dataset_reference.type:1 t0113_dataset_reference.reference_date";
			// MD_Metadata.identificationInfo.AbstractMD_Identification.citation.CI_Citation.date.CI_Date[dateType.CI_DateTypeCode.@codeListValue='publication'].date.Date
		} else if (name.equalsIgnoreCase("PublicationDate")) {
			mappedName = "t0113_dataset_reference.type:2 t0113_dataset_reference.reference_date";
			// MD_Metadata.identificationInfo.AbstractMD_Identification.pointOfContact.CI_ResponsibleParty.organisationName
		} else if (name.equalsIgnoreCase("OrganisationName")) {
			mappedName = "t02_address.institution";
			// Boolean: MD_Metadata.AbstractMD_Identification.resourceConstraints.MD_SecurityConstraints
		} else if (name.equalsIgnoreCase("HasSecurityConstraints")) {
			mappedName = "object_access.restriction_key:[0 TO 9]";
			// MD_Metadata.identificationInfo.AbstractMD_Identification.citation.CI_Citation.identifier.MD_Identifier.code		
		} else if (name.equalsIgnoreCase("ResourceIdentifier")) {
			mappedName = "t011_obj_geo.datasource_uuid";
			// MD_Metadata.parentIdentifier
		} else if (name.equalsIgnoreCase("ParentIdentifier")) {
			mappedName = "parent.object_node.obj_uuid";
			// MD_Identification.descriptiveKeywords.MD_Keywords.type
		} else if (name.equalsIgnoreCase("KeywordType")) {
			// TODO: must map value to searchterm_value.type (mapping required)
			mappedName = "";

		// Additional queryable properties (dataset, datasetcollection, application)			
			
			// MD_Metadata.identificationInfo.MD_DataIdentification.topicCategory
		} else if (name.equalsIgnoreCase("TopicCategory")) {
			// TODO: value must be transformed via CodeList 527
			mappedName = "";
			// MD_Metadata.identificationInfo.MD_DataIdentification.language
		} else if (name.equalsIgnoreCase("ResourceLanguage")) {
			// TODO: value must be transformed to ISO 639-1
			mappedName = "t01_object.data_language_code";
			// MD_Metadata.identificationInfo.MD_DataIdentification.extent.EX_Extent.geographicElement.EX_GeographicDescription.geographicIdentifier.MD_Identifier.code
		} else if (name.equalsIgnoreCase("GeographicDescriptionCode")) {
			mappedName = "spatial_ref_value.name_value";
			// MD_Metadata.identificationInfo.MD_DataIdentification.spatialResolution.MD_Resolution.equivalentScale.MD_RepresentativeFraction.denominator
		} else if (name.equalsIgnoreCase("Denominator")) {
			mappedName = "t011_obj_geo_scale.scale";
			// MD_Metadata.identificationInfo.MD_DataIdentification.spatialResolution.MD_Resolution.distance.gco:Distance
		} else if (name.equalsIgnoreCase("DistanceValue")) {
			mappedName = "t011_obj_geo_scale.resolution_ground";
			// MD_Metadata.identificationInfo.MD_DataIdentification.spatialResolution.MD_Resolution.distance.gco:Distance@uom
		} else if (name.equalsIgnoreCase("DistanceUOM")) {
			// TODO: not mappable. "meter" is implicit set when t011_obj_geo_scale.resolution_ground is filled, "dpi"is implicit set when t011_obj_geo_scale.resolution_scan is filled
			mappedName = "";
			// MD_Metadata.identificationInfo.MD_DataIdentification.extent.EX_Extent.temporalElement.EX_TemporalExtent.extent.TimePeriod.beginPosition
		} else if (name.equalsIgnoreCase("TempExtent_begin")) {
			// TODO: Ingrid Query field depends on values in TempExtent_begin and TempExtent_end (see mapping)
			// TODO: value must be transformed to IGC DateTime Format
			mappedName = "t1";
			// MD_Metadata.identificationInfo.MD_DataIdentification.extent.EX_Extent.temporalElement.EX_TemporalExtent.extent.TimePeriod.endPosition
		} else if (name.equalsIgnoreCase("TempExtent_end")) {
			// TODO: Ingrid Query field depends on values in TempExtent_begin and TempExtent_end (see mapping)
			// TODO: value must be transformed to IGC DateTime Format
			mappedName = "t2";

		// Additional queryable properties (service)
			
			// MD_Metadata.identificationInfo.SV_ServiceIdentification.serviceType
		} else if (name.equalsIgnoreCase("ServiceType")) {
			// TODO: should be mapped to t011_obj_serv.serv_type_key via 5100 (attn: special treatment required)
			mappedName = "t011_obj_serv.type";
			// MD_Metadata.identificationInfo.SV_ServiceIdentification.serviceTypeVersion
		} else if (name.equalsIgnoreCase("ServiceTypeVersion")) {
			mappedName = "t011_obj_serv_version.version";
			// MD_Metadata.identificationInfo.SV_ServiceIdentification.containsOperations.SV_OperationMetadata.operationName
		} else if (name.equalsIgnoreCase("Operation")) {
			mappedName = "t011_obj_serv_operation.name";
			// MD_Metadata.identificationInfo.SV_ServiceIdentification.couplingType.SV_CouplingType.code@codeListValue
		} else if (name.equalsIgnoreCase("CouplingType")) {
			// TODO: must map to object_reference.special_ref, as value is only "tight" supported only if special_ref=3345 (Basisdaten) 
			mappedName = "";
			// MD_Metadata.identificationInfo.SV_ServiceIdentification.operatesOn.MD_DataIdentification.citation.CI_Citation.identifier
		} else if (name.equalsIgnoreCase("OperatesOn")) {
			mappedName = "object_reference.obj_to_uuid";
			// MD_Metadata.identificationInfo.SV_ServiceIdentification.coupledResource.SV_CoupledResource.identifier
		} else if (name.equalsIgnoreCase("OperatesOnIdentifier")) {
			mappedName = "object_reference.obj_to_uuid";
			// MD_Metadata.identificationInfo.SV_ServiceIdentification.coupledResource.SV_CoupledResource.operationName
		} else if (name.equalsIgnoreCase("OperatesOnName")) {
			// TODO: not supported at the moment. To support it the mapping must be extended to map also the name of the referenced object to the index
			mappedName = "";			
		} else {
			throw new CSWInvalidParameterValueException("Search for PropertyName '" + name + "' is not supported by this server.", "PropertyName");
		}

		return mappedName;
	}

}
