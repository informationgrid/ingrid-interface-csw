/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.domain.query.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;

import de.ingrid.interfaces.csw.domain.constants.ConfigurationKeys;
import de.ingrid.interfaces.csw.domain.constants.ConstraintLanguage;
import de.ingrid.interfaces.csw.domain.constants.ElementSetName;
import de.ingrid.interfaces.csw.domain.constants.Namespace;
import de.ingrid.interfaces.csw.domain.constants.OutputFormat;
import de.ingrid.interfaces.csw.domain.constants.ResultType;
import de.ingrid.interfaces.csw.domain.constants.TypeName;
import de.ingrid.interfaces.csw.domain.query.CSWQuery;

public class GenericQuery implements Serializable, CSWQuery {

    private static final long serialVersionUID = GenericQuery.class.getName().hashCode();

    protected Namespace schema = null;
    protected Namespace outputSchema = null;
    protected OutputFormat outputFormat = null;
    protected String version = null;
    protected ElementSetName elementSetName = null;

    /**
     * GetRecords specific
     */
    protected TypeName[] typeNames = null;
    protected ResultType resultType = null;
    protected ConstraintLanguage constraintLanguage = null;
    protected String constraintLanguageVersion = null;
    protected Document constraint = null;
    protected Document sort = null;
    protected int startPosition = 1;
    protected int maxRecords = 0;

    /**
     * GetRecordById specific
     */
    protected List<String> ids = null;

    /**
     * Constructor
     */
    public GenericQuery() {
        // set defaults according to
        // OpenGIS Catalogue Services Specification 2.0.2 - ISO Metadata
        // Application Profile 8.2.2.1.1
        this.schema = Namespace.CSW_2_0_2;
        this.outputSchema = Namespace.GMD;
        this.outputFormat = OutputFormat.APPLICATION_XML;
        this.version = ConfigurationKeys.CSW_VERSION_2_0_2;
        this.elementSetName = ElementSetName.FULL;
        this.typeNames = new TypeName[] { TypeName.RECORD };
        this.resultType = ResultType.HITS;
        this.constraintLanguage = ConstraintLanguage.FILTER;
        this.constraintLanguageVersion = "1.1.0";
        this.startPosition = 1;
        this.maxRecords = 10;
    }

    @Override
    public void setSchema(Namespace schema) {
        this.schema = schema;
    }

    @Override
    public Namespace getSchema() {
        return this.schema;
    }

    @Override
    public void setOutputSchema(Namespace schema) {
        this.outputSchema = schema;
    }

    @Override
    public Namespace getOutputSchema() {
        return this.outputSchema;
    }

    @Override
    public void setOutputFormat(OutputFormat format) {
        this.outputFormat = format;
    }

    @Override
    public OutputFormat getOutputFormat() {
        return this.outputFormat;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public void setTypeNames(TypeName[] typeNames) {
        this.typeNames = typeNames;
    }

    @Override
    public TypeName[] getTypeNames() {
        return this.typeNames;
    }

    @Override
    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }

    @Override
    public ResultType getResultType() {
        return this.resultType;
    }

    @Override
    public void setElementSetName(ElementSetName elementSetName) {
        this.elementSetName = elementSetName;
    }

    @Override
    public ElementSetName getElementSetName() {
        return this.elementSetName;
    }

    @Override
    public void setConstraintLanguage(ConstraintLanguage language) {
        this.constraintLanguage = language;
    }

    @Override
    public ConstraintLanguage getConstraintLanguage() {
        return this.constraintLanguage;
    }

    @Override
    public void setConstraintLanguageVersion(String version) {
        this.constraintLanguageVersion = version;
    }

    @Override
    public String getConstraintLanguageVersion() {
        return this.constraintLanguageVersion;
    }

    @Override
    public void setConstraint(Document filter) {
        this.constraint = filter;
    }

    @Override
    public Document getConstraint() {
        return this.constraint;
    }

    @Override
    public void setStartPosition(int position) {
        if (position == 0) {
            this.startPosition = position + 1;
        } else {
            this.startPosition = position;
        }
    }

    @Override
    public int getStartPosition() {
        return this.startPosition;
    }

    @Override
    public void setMaxRecords(int max) {
        this.maxRecords = max;
    }

    @Override
    public int getMaxRecords() {
        return this.maxRecords;
    }

    @Override
    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    @Override
    public List<String> getIds() {
        return this.ids;
    }

    @Override
    public void setId(String idStr) {
        this.ids = new ArrayList<String>();
        if (idStr != null) {
            String[] idList = idStr.split("\\s*,\\s*");
            for (String id : idList) {
                this.ids.add(id);
            }
        }
    }

    @Override
    public Document getSort() {
        return this.sort;
    }

    @Override
    public void setSortBy(Document sort) {
        this.sort = sort;
    }
}
