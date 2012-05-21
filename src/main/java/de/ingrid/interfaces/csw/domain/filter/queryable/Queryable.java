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

	SUBJECT                (new CharacterString()),
	TITLE                  (new CharacterString()),
	ABSTRACT               (new CharacterString()),
	ANYTEXT                (new CharacterString()),
	FORMAT                 (new CharacterString()),
	IDENTIFIER             (new Identifier()),
	MODIFIED               (new Date()),
	TYPE                   (new Codelist()),
	BOUNDINGBOX            (new BoundingBox()),
	CRS                    (new Identifier()),

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
	
	SERVICETYPE            (new CharacterString()),
    OPERATESON             (new CharacterString()),
    OPERATESONIDENTIFIER   (new CharacterString()),
    OPERATESONNAME          (new CharacterString()),
    COUPLINGTYPE            (new CharacterString()),

	AUTHOR                 (new CharacterString()),

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
