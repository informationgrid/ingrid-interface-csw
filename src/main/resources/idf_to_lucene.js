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
		{	"indexField":"subject",
			"tokenized":true,
			"xpath":"//gmd:identificationInfo//gmd:descriptiveKeywords//gmd:keyword/gco:CharacterString"
		}, 
		{	"indexField":"title",
			"tokenized":true,
			"xpath":"//gmd:identificationInfo//gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString"
		},
		{	"indexField":"abstract",
			"xpath":"//gmd:identificationInfo//gmd:abstract/gco:CharacterString"
		},
		{	"indexField":"format",
			"tokenized":true,
			"xpath":"//gmd:distributionInfo/gmd:MD_Distribution/gmd:distributionFormat/gmd:MD_Format/gmd:name/gco:CharacterString"
		},
		{	"indexField":"identifier",
			"tokenized":false,
			"xpath":"//gmd:fileIdentifier/gco:CharacterString"
		},
		{	"indexField":"id",
			"tokenized":false,
			"xpath":"//gmd:fileIdentifier/gco:CharacterString"
		},
		{	"indexField":"parentidentifier",
			"tokenized":false,
			"xpath":"//gmd:parentIdentifier/gco:CharacterString"
		},
		{	"indexField":"modified",
			"xpath":"//gmd:dateStamp/gco:DateTime | //gmd:dateStamp/gco:Date[not(../gco:DateTime)]",
			"transform":{
				"funct":UtilsCSWDate.mapDateFromIso8601ToIndex
			}
		},
		{	"indexField":"revisiondate",
			"tokenized":false,
			"xpath":"//gmd:identificationInfo//gmd:CI_Citation/gmd:date/gmd:CI_Date[gmd:dateType/gmd:CI_DateTypeCode/@codeListValue='revision']/gmd:date/gco:DateTime | //gmd:identificationInfo//gmd:CI_Citation/gmd:date/gmd:CI_Date[gmd:dateType/gmd:CI_DateTypeCode/@codeListValue='revision']/gmd:date/gco:Date[not(../gco:DateTime)]"
		},
		{	"indexField":"type",
			"xpath":"//gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue",
			"defaultValue":"dataset"
		},
		{	"execute":{
				"funct":mapGeographicElements,
				"params":[recordNode]
			}
		},
		{	"indexField":"metafile",
			"defaultValue":"doc"
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
		// iterate over all xpath results
		var nodeList = XPATH.getNodeList(recordNode, t.xpath);
		if (nodeList && nodeList.getLength() > 0) {
			for (j=0; j<nodeList.getLength(); j++ ) {
				value = nodeList.item(j).getTextContent()
				// check for transformation
				if (hasValue(t.transform)) {
					var args = new Array(value);
					if (hasValue(t.transform.params)) {
						args = args.concat(t.transform.params);
					}
					value = call_f(t.transform.funct,args);
				}
				// check for NOT tokenized
				if (hasValue(t.tokenized)) {
					if (!t.tokenized) {
						tokenized = false;
					}
				}
				if (hasValue(value)) {
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
				// check for NOT tokenized
				if (hasValue(t.tokenized)) {
					if (!t.tokenized) {
						tokenized = false;
					}
				}
				if (hasValue(value)) {
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
	var boundingBoxes = XPATH.getNodeList(recordNode, "//gmd:identificationInfo//gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox");
	if (hasValue(boundingBoxes)) {
		var westList = java.lang.reflect.Array.newInstance(java.lang.Double, boundingBoxes.getLength());
		var eastList = java.lang.reflect.Array.newInstance(java.lang.Double, boundingBoxes.getLength());
		var southList = java.lang.reflect.Array.newInstance(java.lang.Double, boundingBoxes.getLength());
		var northList = java.lang.reflect.Array.newInstance(java.lang.Double, boundingBoxes.getLength());
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




function addToDoc(field, content, tokenized) {
	if (typeof content != "undefined" && content != null) {
		if (log.isDebugEnabled()) {
			log.debug("Add '" + field + "'='" + content + "' to lucene index");
		}
		var analyzed = Field.Index.ANALYZED;
		if (!tokenized) analyzed = Field.Index.NOT_ANALYZED;
		document.add(new Field(field, content, Field.Store.YES, analyzed));
		document.add(new Field("anytext", content, Field.Store.NO, analyzed));
		document.add(new Field("anytext", LuceneTools.filterTerm(content), Field.Store.NO, Field.Index.ANALYZED));
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

function call_f(f,args)
{
  f.call_self = function(ars)
  {
    var callstr = "";
    if (hasValue(ars)) {
	    for(var i = 0; i < ars.length; i++)
	    {
	      callstr += "ars["+i+"]";
	      if(i < ars.length - 1)
	      {
	        callstr += ',';
	      }
	    }
	}
    return eval("this("+callstr+")");
  };

  return f.call_self(args);
}


