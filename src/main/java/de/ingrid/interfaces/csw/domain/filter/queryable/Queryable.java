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

	Subject                (new CharacterString()),
	Title                  (new CharacterString()),
	Abstract               (new CharacterString()),
	AnyText                (new CharacterString()),
	Format                 (new CharacterString()),
	Identifier             (new Identifier()),
	Modified               (new Date()),
	Type                   (new Codelist()),
	BoundingBox            (new BoundingBox()),
	CRS                    (new Identifier()),

	RevisionDate           (new Date()),
	AlternateTitle         (new CharacterString()),
	CreationDate           (new Date()),
	PublicationDate        (new Date()),
	OrganisationName       (new CharacterString()),
	HasSecurityConstraints (new Boolean()),
	Language               (new CharacterString()),
	ResourceIdentifier     (new Identifier()),
	ParentIdentifier       (new Identifier()),
	KeywordType            (new Codelist()),

	Author                 (new CharacterString()),

	Unknown                (new CharacterString());

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
