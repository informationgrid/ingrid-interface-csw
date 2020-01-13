/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
    
    // Additional queryable properties (service), TG 3.1, Table 5-7
    LINEAGE					(new CharacterString()),
    RESPONSIBLEPARTYROLE	(new CharacterString()),
    DEGREE					(new Boolean()),
    CONDITIONAPPLYINGTOACCESSANDUSE (new CharacterString()),
    ACCESSCONSTRAINTS		(new CharacterString()),
    OTHERCONSTRAINTS		(new CharacterString()),
    CLASSIFICATION			(new CharacterString()),
    SPECIFICATIONTITLE		(new CharacterString()),
    SPECIFICATIONDATE		(new Date()),
    SPECIFICATIONDATETYPE	(new CharacterString()),

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
