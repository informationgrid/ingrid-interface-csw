/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.filter.queryable;

/**
 * List of known queryables.
 * 
 * @author ingo@wemove.com
 */
public enum Queryable {

    // Core Queriables, OGC 07-045, Table 6
    SUBJECT                (new CharacterString()),
	TITLE                  (new CharacterString()),
    ABSTRACT            (new CharacterString()),
	ANYTEXT                (new CharacterString()),
	FORMAT                 (new CharacterString()),
	IDENTIFIER             (new Identifier()),
	MODIFIED               (new Date()),
	TYPE                   (new Codelist()),
	BOUNDINGBOX            (new BoundingBox()),
	CRS                    (new Identifier()),
    // Additional queryables, OGC 07-045, Table 10
	REVISIONDATE           (new Date()),
	ALTERNATETITLE         (new CharacterString()),
	CREATIONDATE           (new Date()),
	PUBLICATIONDATE        (new Date()),
	ORGANISATIONNAME       (new CharacterString()),
	HASSECURITYCONSTRAINTS (new Boolean()),
	LANGUAGE               (new CharacterString()),
	RESOURCEIDENTIFIER     (new Identifier()),
	PARENTIDENTIFIER       (new Identifier()),
	KEYWORDTYPE            (new Codelist()),
	// Additional queryable properties (dataset, datasetcollection, application), OGC 07-045, Table 11, 12, 13
	TOPICCATEGORY          (new CharacterString()),
	RESOURCELANGUAGE       (new CharacterString()),
	GEOGRAPHICDESCRIPTIONCODE (new CharacterString()),
	DENOMINATOR            (new CharacterString()),
	DISTANCEVALUE          (new CharacterString()),
	DISTANCEUOM          (new CharacterString()),
	TEMPEXTENT_BEGIN       (new Date()),
    TEMPEXTENT_END       (new Date()),

    // Additional queryable properties (service), OGC 07-045, Table 14
	SERVICETYPE            (new CharacterString()),
	SERVICETYPEVERSION     (new CharacterString()),
	OPERATION              (new CharacterString()),
    OPERATESON             (new CharacterString()),
    OPERATESONIDENTIFIER   (new CharacterString()),
    OPERATESONNAME          (new CharacterString()),
    COUPLINGTYPE            (new CharacterString()),
    
    // Additional queryable properties (service), OGC 07-045, Table 16
    LINEAGE					(new CharacterString()),

    // InGrid specific ISO additional ISO based queryables
    HIERARCHYLEVELNAME      (new CharacterString()),
    
    // InGrid specific queryables
    PARTNER                 (new CharacterString()),
    PROVIDER                 (new CharacterString()),
    IPLUG                 (new CharacterString()),

	UNKNOWN                (new CharacterString());

	/**
	 * The type of the queryable
	 */
	private final QueryableType type;

	/**
	 * Constructor.
	 * @param type
	 */
	Queryable(QueryableType type) {
		this.type = type;
	}

	/**
	 * Get the type of the queryable.
	 * @return QueryableType
	 */
	public QueryableType getType() {
		return this.type;
	}
}
