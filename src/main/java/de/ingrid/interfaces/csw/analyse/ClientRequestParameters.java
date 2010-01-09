package de.ingrid.interfaces.csw.analyse;

import java.util.ArrayList;

/**
 * Contains parameter definitions of requests coming from a client querying our CSW interface.
 * 
 * @author Dirk Schwarzmann
 *
 */
public class ClientRequestParameters implements RequestParameters {
	public static final String GETCAPABILITIES = "GetCapabilities";
	public static final String GETRECORDS = "GetRecords";
	public static final String GETRECORDBYID = "GetRecordById";
	public static final String DESCRIBERECORD = "DescribeRecord";
	
	public static final String REQUEST = "REQUEST";
	public static final String SERVICE = "SERVICE";
	public static final String ACCEPTVERSIONS = "ACCEPTVERSIONS";
	public static final String VERSION = "VERSION";
	public static final String TYPENAMES = "TYPENAMES";
	public static final ArrayList TYPENAMES_VALUES = new ArrayList();
	public static final String RESULTTYPE = "RESULTTYPE";
	public static final ArrayList RESULTTYPE_VALUES = new ArrayList();
	public static final String CONSTRAINT = "CONSTRAINT";
	public static final String CONSTRAINTLANGUAGE = "CONSTRAINTLANGUAGE";
	public static final ArrayList CONSTRAINTLANGUAGE_VALUES = new ArrayList();
	public static final String CONSTRAINTLANGUAGEVERSION = "CONSTRAINT_LANGUAGE_VERSION";
	public static final String ELEMENTNAME = "ELEMENTNAME";
	public static final String ELEMENTSETNAME = "ELEMENTSETNAME";
	public static final ArrayList ELEMENTNAME_VALUES = new ArrayList();
	
	static {
		// Specify only upper case strings (needed for case insensitve comparison)
		TYPENAMES_VALUES.add("SERVICE");
		TYPENAMES_VALUES.add("DATASET");
		TYPENAMES_VALUES.add("DATASETCOLLECTION");
		TYPENAMES_VALUES.add("APPLICATION");
		
		RESULTTYPE_VALUES.add("HITS");
		RESULTTYPE_VALUES.add("RESULTS");
		RESULTTYPE_VALUES.add("VALIDATE");
		
		CONSTRAINTLANGUAGE_VALUES.add("CQL_TEXT");
		CONSTRAINTLANGUAGE_VALUES.add("FILTER");
		
		ELEMENTNAME_VALUES.add("BRIEF");
		ELEMENTNAME_VALUES.add("SUMMARY");
		ELEMENTNAME_VALUES.add("FULL");
	}
}
