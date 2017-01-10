/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/**
 * CSW 2.0.2 AP ISO 1.0 Record (full) to Lucene Document mapping according to mapping IGC 1.0.3
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 *
 * The following global variable are passed from the application:
 *
 * @param recordNode A IDF record instance, that defines the input
 * @param document A lucene Document instance, that defines the output
 * @param log A Log instance
 * @param geometryMapper A de.ingrid.interfaces.csw.index.impl.IngridGeoTKLuceneIndexer instance
 * @param XPATH A de.ingrid.utils.xpath.XPathUtils instance
 * 
 * The document already has a field "docid" with the current lucene document id.
 *
 */
if (javaVersion.indexOf( "1.8" ) === 0) {
	load("nashorn:mozilla_compat.js");
}

importPackage(Packages.org.apache.lucene.document);
importPackage(Packages.de.ingrid.interfaces.csw.tools);
importPackage(Packages.de.ingrid.utils.udk);
importPackage(Packages.de.ingrid.utils.xpath);
importPackage(Packages.org.w3c.dom);
importPackage(Packages.de.ingrid.interfaces.csw.index.impl);




if (log.isDebugEnabled()) {
	log.debug("Mapping record "+recordId+" to lucene document");
}

// define one-to-one mappings
/**  each entry consists off the following possible values:
	
	indexField: The name of the field in the index the data will be put into.
	     xpath: The xpath expression for the data in the XML input file. Multiple xpath 
	     		results will be put in the same index field.
	 transform: The transformation to be executed on the value
	  		     funct: The transformation function to use.
	   		    params: The parameters for the transformation function additional to the value 
	  		            from the xpath expression that is always the first parameter. 
	   execute: The function to be executed. No xpath value is obtained. Instead the recordNode of the 
				source XML is put as default parameter to the function. All other parameters are ignored.
	  	         funct: The function to execute.
	  	        params: The parameters for the function additional to the recordNode 
	  		            that is always the first parameter.
	 tokenized: If set to false no tokenizing will take place before the value is put into the index.
*/
var transformationDescriptions = [
		

        // IngGrid specific index fields
        {	"indexField":"id",
        	"xpath":"//gmd:fileIdentifier/gco:CharacterString",
        	"tokenized":false,
			"toLowerCase":false
        },
		{	"indexField":"partner",
			"xpath":"//idf:html/@partner"
		}, 
		{	"indexField":"partner_sort",
        	"tokenized":false,
			"xpath":"//idf:html/@partner"
		}, 
		{	"indexField":"provider",
			"xpath":"//idf:html/@provider"
		}, 
		{	"indexField":"provider_sort",
        	"tokenized":false,
			"xpath":"//idf:html/@provider"
		}, 
		{	"indexField":"iplug",
			"xpath":"//idf:html/@iplug"
		}, 
        
        // Core Queriables, OGC 07-045, Table 6
		{	"indexField":"subject",
			"xpath":"//gmd:identificationInfo//gmd:descriptiveKeywords//gmd:keyword/gco:CharacterString"
		}, 
		{	"indexField":"subject",
			"xpath":"//gmd:identificationInfo//gmd:descriptiveKeywords//gmd:keyword//gmd:LocalisedCharacterString"
		}, 
		{	"indexField":"subject_sort",
			"tokenized":false,
			"xpath":"//gmd:identificationInfo//gmd:descriptiveKeywords//gmd:keyword/gco:CharacterString"
		}, 
		{	"indexField":"subject_sort",
			"tokenized":false,
			"xpath":"//gmd:identificationInfo//gmd:descriptiveKeywords//gmd:keyword//gmd:LocalisedCharacterString"
		}, 
		{	"indexField":"title",
			"tokenized":true,
			"xpath":"//gmd:identificationInfo//gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString"
		}, 
		{	"indexField":"title_sort",
			"tokenized":false,
			"xpath":"//gmd:identificationInfo//gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString"
		}, 
		{	"indexField":"abstract",
			"tokenized":true,
			"xpath":"//gmd:identificationInfo//gmd:abstract/gco:CharacterString"
		},
		{	"indexField":"format",
			"tokenized":true,
			"xpath":"//gmd:distributionInfo/gmd:MD_Distribution/gmd:distributionFormat/gmd:MD_Format/gmd:name/gco:CharacterString"
		},
		{	"indexField":"identifier",
			"xpath":"//gmd:fileIdentifier/gco:CharacterString"
		},
		{	"indexField":"modified",
			"tokenized":false,
			"xpath":"//gmd:dateStamp/gco:DateTime | //gmd:dateStamp/gco:Date[not(../gco:DateTime)]"
		},
		{	"indexField":"type",
			"xpath":"//gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue",
			"defaultValue":"dataset"
		},
		{	"indexField":"type_sort",
			"xpath":"//gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue",
			"tokenized":false,
			"defaultValue":"dataset"
		},
		{	"execute":{
				"funct":mapGeographicElements,
				"params":[recordNode]
			}
		},
		{	"execute":{
				"funct":mapReferenceSystem,
				"params":[recordNode]
			}
		},
	    // Additional queryables, OGC 07-045, Table 10
		{	"indexField":"revisiondate",
			"tokenized":false,
			"xpath":"//gmd:identificationInfo//gmd:CI_Citation/gmd:date/gmd:CI_Date[gmd:dateType/gmd:CI_DateTypeCode/@codeListValue='revision']/gmd:date/gco:DateTime | //gmd:identificationInfo//gmd:CI_Citation/gmd:date/gmd:CI_Date[gmd:dateType/gmd:CI_DateTypeCode/@codeListValue='revision']/gmd:date/gco:Date[not(../gco:DateTime)]"
		},
		{	"indexField":"alternatetitle",
			"xpath":"//gmd:identificationInfo//gmd:citation/gmd:CI_Citation/gmd:alternateTitle/gco:CharacterString"
		}, 
		{	"indexField":"creationdate",
			"tokenized":false,
			"xpath":"//gmd:identificationInfo//gmd:CI_Citation/gmd:date/gmd:CI_Date[gmd:dateType/gmd:CI_DateTypeCode/@codeListValue='creation']/gmd:date/gco:DateTime | //gmd:identificationInfo//gmd:CI_Citation/gmd:date/gmd:CI_Date[gmd:dateType/gmd:CI_DateTypeCode/@codeListValue='creation']/gmd:date/gco:Date[not(../gco:DateTime)]"
		},
		{	"indexField":"publicationdate",
			"tokenized":false,
			"xpath":"//gmd:identificationInfo//gmd:CI_Citation/gmd:date/gmd:CI_Date[gmd:dateType/gmd:CI_DateTypeCode/@codeListValue='publication']/gmd:date/gco:DateTime | //gmd:identificationInfo//gmd:CI_Citation/gmd:date/gmd:CI_Date[gmd:dateType/gmd:CI_DateTypeCode/@codeListValue='publication']/gmd:date/gco:Date[not(../gco:DateTime)]"
		},
		{	"indexField":"organisationname",
			"xpath":"//gmd:identificationInfo//gmd:pointOfContact//gmd:organisationName/gco:CharacterString"
		},
		{	"indexField":"organisationname_sort",
			"tokenized":false,
			"xpath":"//gmd:identificationInfo//gmd:pointOfContact//gmd:organisationName/gco:CharacterString"
		},
		{	"execute":{
				"funct":mapHasSecurityConstraints,
				"params":[recordNode]
			}
		},
		{	"indexField":"language",
			"xpath":" //idf:idfMdMetadata/gmd:language/gmd:LanguageCode/@codeListValue | //idf:idfMdMetadata/gmd:language/gco:CharacterString"
		},
		{	"indexField":"resourceidentifier",
			"xpath":"//gmd:identificationInfo//gmd:identifier//gmd:code/gco:CharacterString"
		},
		{	"indexField":"parentidentifier",
			"xpath":"//gmd:parentIdentifier/gco:CharacterString"
		},
		{	"indexField":"keywordtype",
			"xpath":"//gmd:identificationInfo//gmd:descriptiveKeywords//gmd:type/gmd:MD_KeywordTypeCode/@codeListValue"
		}, 
		// Additional queryable properties (dataset, datasetcollection, application), OGC 07-045, Table 11, 12, 13
		{	"indexField":"topiccategory",
			"xpath":"//gmd:identificationInfo//gmd:topicCategory//gmd:MD_TopicCategoryCode"
		},
		{	"indexField":"resourcelanguage",
			"xpath":"//gmd:identificationInfo//gmd:language/gco:CharacterString"
		},
		{	"indexField":"geographicdescriptioncode",
			"xpath":"//gmd:identificationInfo//gmd:geographicIdentifier/gmd:MD_Identifier/gmd:code/gco:CharacterString"
		},
		{	"indexField":"denominator",
			"tokenized":false,
			"xpath":"//gmd:spatialResolution/gmd:MD_Resolution/gmd:equivalentScale/gmd:MD_RepresentativeFraction/gmd:denominator/gco:Integer"
		},
		{	"indexField":"distancevalue",
			"tokenized":false,
			"xpath":"//gmd:spatialResolution/gmd:MD_Resolution/gmd:distance/gco:Distance"
		},
		{	"indexField":"distanceuom",
			"tokenized":false,
			"xpath":"//gmd:spatialResolution/gmd:MD_Resolution/gmd:distance/gco:Distance/@uom"
		},
		{	"indexField":"tempextent_begin",
			"tokenized":false,
			"xpath":"//gmd:temporalElement/gmd:EX_TemporalExtent/gmd:extent/gml:TimePeriod/gml:beginPosition"
		},
		{	"indexField":"tempextent_end",
			"tokenized":false,
			"xpath":"//gmd:temporalElement/gmd:EX_TemporalExtent/gmd:extent/gml:TimePeriod/gml:endPosition"
		},
		// Additional queryable properties (service), OGC 07-045, Table 14
		{	"indexField":"servicetype",
			"xpath":"//srv:serviceType/gco:LocalName"
		},
		{	"indexField":"servicetypeversion",
			"xpath":"//srv:serviceTypeVersion/gco:CharacterString"
		},
		{	"indexField":"operation",
			"xpath":"//srv:SV_OperationMetadata/srv:operationName/gco:CharacterString"
		},
		{	"indexField":"operateson",
			"xpath":"//srv:operatesOn/@xlink:href"
		},
		{	"indexField":"operatesonidentifier",
			"xpath":"//srv:coupledResource//srv:identifier/gco:CharacterString"
		},
		{	"indexField":"operatesonname",
			"xpath":"//srv:coupledResource//srv:operationName/gco:CharacterString"
		},
		{	"indexField":"couplingtype",
			"xpath":"//srv:couplingType/srv:SV_CouplingType/@codeListValue"
		},
		// InGrid specific ISO additional ISO based queryables
		{	"indexField":"hierarchylevelname",
			"xpath":"//gmd:hierarchyLevelName/gco:CharacterString"
		},
		// default value, used to select all records
		{	"indexField":"metafile",
			"defaultValue":"doc"
		},
		// lineage as AdditionalQueryables
		{	"indexField":"lineage",
			"xpath":"//idf:idfMdMetadata/gmd:dataQualityInfo/*/gmd:lineage/*/gmd:statement/gco:CharacterString"
		},
		// TODO: degree as AdditionalQueryables
		{	"indexField":"degree",
			"xpath":"//idf:idfMdMetadata/gmd:dataQualityInfo/*/gmd:report/*/gmd:result/*/gmd:pass/gco:Boolean"
		},
		// ResponsiblePartyRole as AdditionalQueryables
		{	"indexField":"responsiblepartyrole",
			"xpath":"//gmd:identificationInfo/*/gmd:pointOfContact/*/gmd:role/*/@codeListValue"
		},
		// TODO: ConditionApplyingToAccessAndUse as AdditionalQueryables
		{	"indexField":"conditionapplyingtoaccessanduse",
			"xpath":"//gmd:identificationInfo/*/gmd:resourceConstraints/*/gmd:useLimitation/gco:CharacterString"
		},
		// TODO: AccessConstraints as AdditionalQueryables
		{	"indexField":"accessconstraints",
			"xpath":"//gmd:identificationInfo/*/gmd:resourceConstraints/*/gmd:accessConstraints/*/@codeListValue"
		},
		// TODO: OtherConstraints as AdditionalQueryables
		{	"indexField":"otherconstraints",
			"xpath":"//gmd:identificationInfo/*/gmd:resourceConstraints/*/gmd:otherConstraints/gco:CharacterString"
		},
		// TODO: Classification as AdditionalQueryables
		{	"indexField":"classification",
			"xpath":"//gmd:identificationInfo/*/gmd:resourceConstraints/*/gmd:classification/*/@codeListValue"
		},
		// TODO: SpecificationTitle as AdditionalQueryables
		{	"indexField":"specificationtitle",
			"xpath":"//gmd:dataQualityInfo/*/gmd:report/*/gmd:result/*/gmd:specification/*/gmd:title/gco:CharacterString"
		},
		// TODO: SpecificationDate as AdditionalQueryables
		{	"indexField":"specificationdate",
			"xpath":"//gmd:dataQualityInfo/*/gmd:report/*/gmd:result/*/gmd:specification/*/gmd:date/*/gmd:date/gco:Date"
		},
		// TODO: SpecificationDateType as AdditionalQueryables
		{	"indexField":"specificationdatetype",
			"xpath":"//gmd:dataQualityInfo/*/gmd:report/*/gmd:result/*/gmd:specification/*/gmd:date/*/gmd:dateType/*/@codeListValue"
		}
		
	];

// iterate over all transformation descriptions
var value;
for (var i in transformationDescriptions) {
	var t = transformationDescriptions[i];
	
	// check for execution (special function)
	if (hasValue(t.execute)) {
		if (log.isDebugEnabled()) {
			log.debug("Execute function: " + t.execute.funct.name)
		}
		call_f(t.execute.funct, t.execute.params)
	} else {
		if (log.isDebugEnabled()) {
			log.debug("Working on " + t.indexField)
		}
		var tokenized = true;
		var toLowerCase = true;
		// iterate over all xpath results
		if (log.isDebugEnabled()) {
			log.debug("Executing XPATH: " + t.xpath)
		}
		var nodeList = XPATH.getNodeList(recordNode, t.xpath);
		if (log.isDebugEnabled()) {
			log.debug("Found: " + nodeList.getLength() + " nodes.")
		}
		if (nodeList && nodeList.getLength() > 0) {
			for (j=0; j<nodeList.getLength(); j++ ) {
				if (log.isDebugEnabled()) {
					log.debug("Working on node: " + (j+1) + "/" + nodeList.getLength() + ".")
				}
				value = nodeList.item(j).getTextContent().trim();
				if (log.isDebugEnabled()) {
					log.debug("Found value: " + value)
				}
				// check for transformation
				if (hasValue(t.transform)) {
					var args = new Array(value);
					if (hasValue(t.transform.params)) {
						args = args.concat(t.transform.params);
					}
					value = call_f(t.transform.funct,args);
				}
				if (hasValue(value)) {
					// check for NOT tokenized
					if (hasValue(t.tokenized)) {
						if (!t.tokenized) {
							tokenized = false;
						}
					}
					// check for NOT lowerCase
					if (hasValue(t.toLowerCase)) {
						if (!t.toLowerCase) {
							toLowerCase = false;
						}
					}
					if (toLowerCase) value = value.toLowerCase(); 
					addToDoc(t.indexField, value, tokenized);
				}
			}
		} else {
			// no node found for this xpath
			if (t.defaultValue) {
				value = t.defaultValue;
				// check for transformation
				if (hasValue(t.transform)) {
					var args = new Array(value);
					if (hasValue(t.transform.params)) {
						args = args.concat(t.transform.params);
					}
					value = call_f(t.transform.funct,args);
				}
				if (hasValue(value)) {
					// check for NOT tokenized
					if (hasValue(t.tokenized)) {
						if (!t.tokenized) {
							tokenized = false;
						}
					}
					// check for NOT lowerCase
					if (hasValue(t.toLowerCase)) {
						if (!t.toLowerCase) {
							toLowerCase = false;
						}
					}
					if (toLowerCase) value = value.toLowerCase(); 
					addToDoc(t.indexField, value, tokenized);
				}
			}
		}
	}
}


function transformGeneric(val, mappings, caseSensitive) {
	for (var t in mappings) {
		for (var key in t) {
			if (caseSensitive) {
				if (key == val) {
					return t[key];
				}
			} else {
				if (key.toLowerCase() == val.toLowerCase()) {
					return t[key];
				}
			}
		}
	}
	return null;
}

function mapGeographicElements(recordNode) {
	var boundingBoxes = XPATH.getNodeList(recordNode, "//gmd:identificationInfo//gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox");
	if (hasValue(boundingBoxes)) {
	    var westList, eastList, southList, northList;
	    if (javaVersion.indexOf( "1.8" ) === 0) {
    	    var DoubleArrayType = Java.type("java.lang.Double[]");
            westList = new DoubleArrayType(boundingBoxes.getLength());
            eastList = new DoubleArrayType(boundingBoxes.getLength());
            southList = new DoubleArrayType(boundingBoxes.getLength());
            northList = new DoubleArrayType(boundingBoxes.getLength());
	    } else {
	        westList = java.lang.reflect.Array.newInstance(java.lang.Double, boundingBoxes.getLength());
	        eastList = java.lang.reflect.Array.newInstance(java.lang.Double, boundingBoxes.getLength());
	        southList = java.lang.reflect.Array.newInstance(java.lang.Double, boundingBoxes.getLength());
	        northList = java.lang.reflect.Array.newInstance(java.lang.Double, boundingBoxes.getLength());
	    }
		for (i=0; i<boundingBoxes.getLength(); i++ ) {
			if (hasValue(boundingBoxes.item(i)) && hasValue(XPATH.getString(boundingBoxes.item(i), "gmd:westBoundLongitude/gco:Decimal"))) {
				westList[i] = XPATH.getDouble(boundingBoxes.item(i), "gmd:westBoundLongitude/gco:Decimal");
				eastList[i] = XPATH.getDouble(boundingBoxes.item(i), "gmd:eastBoundLongitude/gco:Decimal");
				southList[i] = XPATH.getDouble(boundingBoxes.item(i), "gmd:southBoundLatitude/gco:Decimal");
				northList[i] = XPATH.getDouble(boundingBoxes.item(i), "gmd:northBoundLatitude/gco:Decimal");
			}
		}
		geometryMapper.addBoundingBox(document, westList, eastList, southList, northList, new java.lang.Integer(4326));
	}
}

function mapReferenceSystem(recordNode) {
	var referenceSystemIdentifiers = XPATH.getNodeList(recordNode, "//gmd:referenceSystemInfo//gmd:RS_Identifier");
	if (hasValue(referenceSystemIdentifiers)) {
		for (i=0; i<referenceSystemIdentifiers.getLength(); i++ ) {
			var authority = XPATH.getString(referenceSystemIdentifiers.item(i), "gmd:codeSpace/gco:CharacterString");
			var id = XPATH.getString(referenceSystemIdentifiers.item(i), "gmd:code/gco:CharacterString");
			var version = XPATH.getString(referenceSystemIdentifiers.item(i), "gmd:version/gco:CharacterString");
			if (hasValue(authority) || hasValue(id) || hasValue(version)) {
				var crsString = "urn:ogc:def:objectType:" + authority + ":" + version + ":" + id;
				addToDoc("crs", crsString, false);
			}
		}
	}
}


function mapHasSecurityConstraints(recordNode) {
	if (XPATH.nodeExists(recordNode, "//gmd:identificationInfo//gmd:MD_SecurityConstraints")) {
		addToDoc("hassecurityconstraints", "true", false);
	} else {
		addToDoc("hassecurityconstraints", "false", false);
	}
}


function addToDoc(field, content, tokenized) {
	if (typeof content != "undefined" && content != null) {
		if (log.isDebugEnabled()) {
			log.debug("Add '" + field + "'='" + content + "' to lucene index");
		}
		var analyzed = Field.Index.ANALYZED;
		if (!tokenized) analyzed = Field.Index.NOT_ANALYZED;
		document.add(new Field(field, content, Field.Store.YES, analyzed));
		document.add(new Field("anytext", content, Field.Store.NO, analyzed));
		document.add(new Field("anytext", luceneTools.filterTerm(content), Field.Store.NO, Field.Index.ANALYZED));
	}
}

function addNumericToDoc(field, content) {
	if (typeof content != "undefined" && content != null) {
        try {
    		if (log.isDebugEnabled()) {
    			log.debug("Add numeric '" + field + "'='" + content + "' to lucene index.");
    		}
            document.add(new NumericField(field, Field.Store.YES, true).setDoubleValue(content));
        } catch (e) {
            if (log.isDebugEnabled()) {
                log.debug("Value '" + content + "' is not a number. Ignoring field '" + field + "'.");
            }
        }
	}
}


function hasValue(val) {
	if (typeof val == "undefined") {
		return false; 
	} else if (val == null) {
		return false; 
	} else if (typeof val == "string" && val == "") {
		return false;
	} else {
	  return true;
	}
}

function call_f(f, args) {
    if (hasValue(args)) {
        if (args.length === 0)
            return f();
        else if (args.length === 1)
            return f(args[0]);
        else if (args.length === 2)
            return f(args[0], args[1]);
        else if (args.length === 3)
            return f(args[0], args[1], args[2]);
        else if (args.length === 4)
            return f(args[0], args[1], args[2], args[3]);
        else
            log.error("Function call does not support number of arguments: "
                    + args.length);

    } else {
        return f();
    }
}
