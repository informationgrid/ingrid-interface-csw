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
/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.filter.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.filter.SpatialFilterType;
import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.geotoolkit.geometry.jts.SRIDGenerator.Version;
import org.geotoolkit.gml.GeometrytoJTS;
import org.geotoolkit.gml.xml.v311.AbstractGeometryType;
import org.geotoolkit.gml.xml.v311.EnvelopeType;
import org.geotoolkit.gml.xml.v311.LineStringType;
import org.geotoolkit.gml.xml.v311.PointType;
import org.geotoolkit.lucene.filter.LuceneOGCFilter;
import org.geotoolkit.lucene.filter.SerialChainFilter;
import org.geotoolkit.lucene.filter.SpatialQuery;
import org.geotoolkit.ogc.xml.v110.AbstractIdType;
import org.geotoolkit.ogc.xml.v110.BBOXType;
import org.geotoolkit.ogc.xml.v110.BinaryComparisonOpType;
import org.geotoolkit.ogc.xml.v110.BinaryLogicOpType;
import org.geotoolkit.ogc.xml.v110.BinarySpatialOpType;
import org.geotoolkit.ogc.xml.v110.ComparisonOpsType;
import org.geotoolkit.ogc.xml.v110.DistanceBufferType;
import org.geotoolkit.ogc.xml.v110.FilterType;
import org.geotoolkit.ogc.xml.v110.LogicOpsType;
import org.geotoolkit.ogc.xml.v110.PropertyIsBetweenType;
import org.geotoolkit.ogc.xml.v110.PropertyIsLikeType;
import org.geotoolkit.ogc.xml.v110.PropertyIsNullType;
import org.geotoolkit.ogc.xml.v110.PropertyNameType;
import org.geotoolkit.ogc.xml.v110.SpatialOpsType;
import org.geotoolkit.ogc.xml.v110.UnaryLogicOpType;
import org.geotoolkit.xml.MarshallerPool;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vividsolutions.jts.geom.Geometry;

import de.ingrid.interfaces.csw.cache.AbstractFileCache;
import de.ingrid.interfaces.csw.domain.exceptions.CSWFilterException;
import de.ingrid.interfaces.csw.domain.filter.FilterParser;
import de.ingrid.interfaces.csw.domain.filter.queryable.Date;
import de.ingrid.interfaces.csw.domain.filter.queryable.Queryable;
import de.ingrid.interfaces.csw.domain.filter.queryable.QueryableType;
import de.ingrid.interfaces.csw.domain.query.CSWQuery;
import de.ingrid.utils.xml.Csw202NamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * A FilterParser that creates a Lucene query from an ogc filter document.
 */
@Service
public class LuceneFilterParser implements FilterParser {

    protected final static Log log = LogFactory.getLog(AbstractFileCache.class);

    private static final String QUERY_CONSTRAINT_LOCATOR = "Query/Constraint";
    private static final String INVALID_PARAMETER_CODE = "InvalidParameter";

    protected static final String PARSE_ERROR_MSG = "The service was unable to parse the Date: ";
    protected static final String UNKNOW_CRS_ERROR_MSG = "Unknow Coordinate Reference System: ";
    protected static final String INCORRECT_BBOX_DIM_ERROR_MSG = "The dimensions of the bounding box are incorrect: ";
    protected static final String FACTORY_BBOX_ERROR_MSG = "Factory exception while parsing spatial filter BBox: ";

    protected static final FilterFactory2 FF = (FilterFactory2) FactoryFinder.getFilterFactory(new Hints(
            Hints.FILTER_FACTORY, FilterFactory2.class));

    /** Tool for evaluating xpath **/
    private XPathUtils xpath = new XPathUtils(new Csw202NamespaceContext());

    private Unmarshaller filterUnmarshaller;

    private static final String defaultField = "metafile:doc";

    @Override
    public SpatialQuery parse(CSWQuery cswQuery) throws Exception {

        Document filterDoc = cswQuery.getConstraint();

        if (this.filterUnmarshaller == null) {
            MarshallerPool marshallerPool = new MarshallerPool(
                    "org.geotoolkit.ogc.xml.v110:org.geotoolkit.gml.xml.v311:org.geotoolkit.gml.xml.v321");
            this.filterUnmarshaller = marshallerPool.acquireUnmarshaller();
        }

        if (filterDoc == null) {
            return new SpatialQuery(defaultField);
        }

        JAXBElement<FilterType> filterEl = this.filterUnmarshaller.unmarshal(filterDoc, FilterType.class);
        FilterType filter = filterEl.getValue();

        SpatialQuery query = null;
        if (filter != null) {
            Filter nullFilter = null;
            // process logical operators like AND, OR, ...
            if (filter.getLogicOps() != null) {
                query = this.processLogicalOperator(filter.getLogicOps());
            }
            // process comparison operators: PropertyIsLike, IsNull, IsBetween,
            // ...
            else if (filter.getComparisonOps() != null) {
                query = new SpatialQuery(this.processComparisonOperator(filter.getComparisonOps()), nullFilter,
                        SerialChainFilter.AND);
            }
            // process spatial constraint : BBOX, Beyond, Overlaps, ...
            else if (filter.getSpatialOps() != null) {
                query = new SpatialQuery("", this.processSpatialOperator(filter.getSpatialOps()), SerialChainFilter.AND);
            }
            // process id
            else if (filter.getId() != null) {
                query = new SpatialQuery(this.processIDOperator(filter.getId()), nullFilter, SerialChainFilter.AND);
            }
        }

        Document sortBy = cswQuery.getSort();
        if (sortBy != null) {
            NodeList sortProperties = this.xpath.getNodeList(sortBy, "//csw:SortProperty");
            if (sortProperties != null && sortProperties.getLength() > 0) {
                List<SortField> sortFields = new ArrayList<SortField>();
                for (int i = 0; i < sortProperties.getLength(); i++) {
                    Node sortProperty = sortProperties.item(i);
                    String propertyName = this.xpath.getString(sortProperty, "//csw:PropertyName");
                    String sortOrder = this.xpath.getString(sortProperty, "//csw:SortOrder");
                    if (sortOrder == null) {
                        sortOrder = "ASC";
                    }
                    // TODO determine type of sort field by queryable type
                    sortFields.add(new SortField(propertyName + "_sort", SortField.STRING, sortOrder
                            .equalsIgnoreCase("DESC") ? true : false));
                }
                SortField[] a = sortFields.toArray(new SortField[0]);
                query.setSort(new Sort(a));
            }
        }

        return query;
    }

    /**
     * Build a piece of Lucene query with the specified Logical filter.
     *
     * @param logicOpsEl
     * @return SpatialQuery
     * @throws CSWFilterException
     */
    private SpatialQuery processLogicalOperator(JAXBElement<? extends LogicOpsType> logicOpsEl)
            throws CSWFilterException {
        List<SpatialQuery> subQueries = new ArrayList<SpatialQuery>();
        StringBuilder queryBuilder = new StringBuilder();
        List<Filter> filters = new ArrayList<Filter>();

        String operator = logicOpsEl.getName().getLocalPart();
        LogicOpsType logicOps = logicOpsEl.getValue();
        if (logicOps instanceof BinaryLogicOpType) {
            BinaryLogicOpType binary = (BinaryLogicOpType) logicOps;
            queryBuilder.append('(');

            // process comparison operators: PropertyIsLike, IsNull, IsBetween,
            // ...
            for (JAXBElement<? extends ComparisonOpsType> el : binary.getComparisonOps()) {
                queryBuilder.append(this.processComparisonOperator(el));
                queryBuilder.append(" ").append(operator.toUpperCase()).append(" ");
            }

            // process logical operators like AND, OR, ...
            for (JAXBElement<? extends LogicOpsType> el : binary.getLogicOps()) {
                boolean writeOperator = true;

                SpatialQuery sq = this.processLogicalOperator(el);
                String subQuery = sq.getQuery();
                Filter subFilter = sq.getSpatialFilter();

                // if the sub spatial query contains both term search and
                // spatial search we create a subQuery
                if ((subFilter != null && !subQuery.equals(defaultField)) || sq.getSubQueries().size() != 0
                        || (sq.getLogicalOperator() == SerialChainFilter.NOT && sq.getSpatialFilter() == null)) {
                    subQueries.add(sq);
                    writeOperator = false;
                } else {
                    if (subQuery.equals("")) {
                        writeOperator = false;
                    } else {
                        queryBuilder.append(subQuery);
                    }
                    if (subFilter != null) {
                        filters.add(subFilter);
                    }
                }

                if (writeOperator) {
                    queryBuilder.append(" ").append(operator.toUpperCase()).append(" ");
                } else {
                    writeOperator = true;
                }
            }

            // process spatial constraint : BBOX, Beyond, Overlaps, ...
            for (JAXBElement<? extends SpatialOpsType> el : binary.getSpatialOps()) {
                // for the spatial filter we don't need to write into the Lucene
                // query
                filters.add(this.processSpatialOperator(el));
            }

            // remove the last Operator and add a ') '
            int pos = queryBuilder.length() - (operator.length() + 2);
            if (pos > 0) {
                queryBuilder.delete(queryBuilder.length() - (operator.length() + 2), queryBuilder.length());
            }
            queryBuilder.append(')');
        } else if (logicOps instanceof UnaryLogicOpType) {
            UnaryLogicOpType unary = (UnaryLogicOpType) logicOps;

            // process comparison operator: PropertyIsLike, IsNull, IsBetween,
            // ...
            if (unary.getComparisonOps() != null) {
                queryBuilder.append(this.processComparisonOperator(unary.getComparisonOps()));
            }
            // process spatial constraint : BBOX, Beyond, Overlaps, ...
            else if (unary.getSpatialOps() != null) {
                filters.add(this.processSpatialOperator(unary.getSpatialOps()));
            }
            // process logical Operators like AND, OR, ...
            else if (unary.getLogicOps() != null) {
                SpatialQuery sq = this.processLogicalOperator(unary.getLogicOps());
                String subQuery = sq.getQuery();
                Filter subFilter = sq.getSpatialFilter();

                if ((sq.getLogicalOperator() == SerialChainFilter.OR && subFilter != null && !subQuery
                        .equals(defaultField))
                        || (sq.getLogicalOperator() == SerialChainFilter.NOT)) {
                    subQueries.add(sq);
                } else {
                    if (!subQuery.equals("")) {
                        queryBuilder.append(subQuery);
                    }
                    if (subFilter != null) {
                        filters.add(sq.getSpatialFilter());
                    }
                }
            }
        }

        String query = queryBuilder.toString();
        if (query.equals("()")) {
            query = "";
        }

        int logicalOperand = SerialChainFilter.valueOf(operator);
        Filter spatialFilter = this.getSpatialFilterFromList(logicalOperand, filters, query);
        SpatialQuery response = new SpatialQuery(query, spatialFilter, logicalOperand);
        response.setSubQueries(subQueries);
        return response;
    }

    /**
     * Build a piece of Lucene query with the specified Comparison filter.
     *
     * @param comparisonOpsEl
     * @return String
     * @throws CSWFilterException
     */
    protected String processComparisonOperator(JAXBElement<? extends ComparisonOpsType> comparisonOpsEl)
            throws CSWFilterException {
        StringBuilder response = new StringBuilder();

        ComparisonOpsType comparisonOps = comparisonOpsEl.getValue();
        if (comparisonOps instanceof PropertyIsLikeType) {
            PropertyIsLikeType pil = (PropertyIsLikeType) comparisonOps;

            // get the field
            String propertyName = "";
            String propertyNameLocal = "";
            if (pil.getPropertyName() != null) {
                propertyName = pil.getPropertyName().getContent();
                propertyNameLocal = this.removePrefix(propertyName);
                response.append(propertyNameLocal.toLowerCase()).append(':');
            } else {
                throw new CSWFilterException("Missing propertyName parameter for propertyIsLike operator.",
                        INVALID_PARAMETER_CODE, QUERY_CONSTRAINT_LOCATOR);
            }

            // get the queryable represented by the field
            Queryable queryableProperty = Queryable.UNKNOWN;
            try {
                queryableProperty = Queryable.valueOf(propertyNameLocal.toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new CSWFilterException("Unknown queryable: " + propertyName);
            }

            // get the value of the field
            if (pil.getLiteral() != null && pil.getLiteral() != null) {
                // format the value by replacing the specified special char by
                // the Lucene special char
                String value = pil.getLiteral();
                if (pil.getWildCard() != null) {
                    value = value.replace(pil.getWildCard(), "*");
                }
                if (pil.getSingleChar() != null) {
                    value = value.replace(pil.getSingleChar(), "?");
                }
                if (pil.getEscapeChar() != null) {
                    value = value.replace(pil.getEscapeChar(), "\\");
                }

                // for a date remove the time zone
                if (queryableProperty.getType() instanceof Date) {
                    value = value.replace("Z", "");
                }
                response.append(this.maskPhrase(value));
            } else {
                throw new CSWFilterException("Missing literal parameter for propertyIsLike operator.",
                        INVALID_PARAMETER_CODE, QUERY_CONSTRAINT_LOCATOR);
            }
        } else if (comparisonOps instanceof PropertyIsNullType) {
            PropertyIsNullType pin = (PropertyIsNullType) comparisonOps;

            // get the field
            if (pin.getPropertyName() != null) {
                response.append(this.removePrefix(pin.getPropertyName().getContent().toLowerCase())).append(':')
                .append("null");
            } else {
                throw new CSWFilterException("Missing propertyName parameter for propertyIsNull operator.",
                        INVALID_PARAMETER_CODE, QUERY_CONSTRAINT_LOCATOR);
            }
        } else if (comparisonOps instanceof PropertyIsBetweenType) {
            // TODO PropertyIsBetweenType
            throw new UnsupportedOperationException("Not supported yet.");
        } else if (comparisonOps instanceof BinaryComparisonOpType) {
            BinaryComparisonOpType bc = (BinaryComparisonOpType) comparisonOps;
            String propertyName = bc.getPropertyName();
            String propertyNameLocal = this.removePrefix(propertyName);
            String literal = bc.getLiteral().getStringValue();
            String operator = comparisonOpsEl.getName().getLocalPart();

            if (propertyName == null || literal == null) {
                throw new CSWFilterException(
                        "Missing propertyName or literal parameter for binary comparison operator.",
                        INVALID_PARAMETER_CODE, QUERY_CONSTRAINT_LOCATOR);
            } else {
                // get the queryable represented by the field
                Queryable queryableProperty = Queryable.UNKNOWN;
                try {
                    queryableProperty = Queryable.valueOf(propertyNameLocal.toUpperCase());
                } catch (IllegalArgumentException ex) {
                    throw new CSWFilterException("Unknown queryable: " + propertyName);
                }
                QueryableType queryableType = queryableProperty.getType();

                propertyNameLocal = propertyNameLocal.toLowerCase();

                // property == value -> property:"value"
                if (operator.equals("PropertyIsEqualTo")) {
                    // add '*' to date fields to return also parts of the  Dates AND to disable analyzing of the date field
                    if (queryableType instanceof Date && !literal.endsWith("*")) {
                        literal = literal.concat("*");
                    }
                    response.append(propertyNameLocal).append(":").append(this.maskPhrase(literal));
                }
                // property != value -> metafile:doc NOT property:"value"
                else if (operator.equals("PropertyIsNotEqualTo")) {
                    response.append(defaultField).append(" NOT ");
                    response.append(propertyNameLocal).append(":").append(this.maskPhrase(literal));
                }
                // property >= value -> property:[value UPPER_BOUND]
                else if (operator.equals("PropertyIsGreaterThanOrEqualTo")) {
                    if (queryableType instanceof Date) {
                        literal = literal.replace("Z", "");
                    }
                    response.append(propertyNameLocal).append(":[").append(literal).append(' ').append(
                            queryableType.getUpperBound()).append("]");
                }
                // property > value -> property:{value UPPER_BOUND}
                else if (operator.equals("PropertyIsGreaterThan")) {
                    if (queryableType instanceof Date) {
                        literal = literal.replace("Z", "");
                    }
                    response.append(propertyNameLocal).append(":{").append(literal).append(' ').append(
                            queryableType.getUpperBound()).append("}");
                }
                // property < value -> property:{LOWER_BOUND value}
                else if (operator.equals("PropertyIsLessThan")) {
                    if (queryableType instanceof Date) {
                        literal = literal.replace("Z", "");
                    }
                    response.append(propertyNameLocal).append(":{").append(queryableType.getLowerBound()).append(' ')
                    .append(literal).append("}");
                }
                // property <= value -> property:[LOWER_BOUND value]
                else if (operator.equals("PropertyIsLessThanOrEqualTo")) {
                    if (queryableType instanceof Date) {
                        literal = literal.replace("Z", "");
                    }
                    response.append(propertyNameLocal).append(":[").append(queryableType.getLowerBound()).append(' ')
                    .append(literal).append("]");
                } else {
                    throw new CSWFilterException("Unkwnow comparison operator: " + operator, INVALID_PARAMETER_CODE,
                            QUERY_CONSTRAINT_LOCATOR);
                }
            }
        }
        return response.toString();
    }

    /**
     * Build a piece of Lucene query with the specified Spatial filter.
     *
     * @param spatialOpsEl
     * @return Filter
     * @throws CSWFilterException
     */
    protected Filter processSpatialOperator(JAXBElement<? extends SpatialOpsType> spatialOpsEl)
            throws CSWFilterException {
        LuceneOGCFilter spatialfilter = null;

        SpatialOpsType spatialOps = spatialOpsEl.getValue();
        if (spatialOps instanceof BBOXType) {
            BBOXType bbox = (BBOXType) spatialOps;
            String propertyName = bbox.getPropertyName();
            String crsName = bbox.getSRS();

            // make sure we DO have a valid srs
            // joachim@wemove.com at 21.05.2012
            if (crsName == null || crsName.length() == 0) {
                crsName = "EPSG:4326";
            }

            // verify that all the parameters are specified
            if (propertyName == null) {
                throw new CSWFilterException("Missing propertyName parameter for BBOX operator.",
                        INVALID_PARAMETER_CODE, QUERY_CONSTRAINT_LOCATOR);
            } else if (!propertyName.contains("BoundingBox")) {
                throw new CSWFilterException("The propertyName parameter for BBOX operator is not a BoundingBox.",
                        INVALID_PARAMETER_CODE, QUERY_CONSTRAINT_LOCATOR);
            }
            if (bbox.getEnvelope() == null && bbox.getEnvelopeWithTimePeriod() == null) {
                throw new CSWFilterException("Missing envelope parameter for BBOX operator.", INVALID_PARAMETER_CODE,
                        QUERY_CONSTRAINT_LOCATOR);
            }

            // transform the EnvelopeEntry in GeneralEnvelope
            spatialfilter = LuceneOGCFilter.wrap(FF.bbox(LuceneOGCFilter.GEOMETRY_PROPERTY, bbox.getMinX(), bbox
                    .getMinY(), bbox.getMaxX(), bbox.getMaxY(), crsName));
        } else if (spatialOps instanceof DistanceBufferType) {
            DistanceBufferType dist = (DistanceBufferType) spatialOps;
            double distance = dist.getDistance();
            String units = dist.getDistanceUnits();
            JAXBElement<?> geomEl = dist.getAbstractGeometry();
            String operator = spatialOpsEl.getName().getLocalPart();

            // verify that all the parameters are specified
            if (dist.getPropertyName() == null) {
                throw new CSWFilterException("Missing propertyName parameter for distanceBuffer operator.",
                        INVALID_PARAMETER_CODE, QUERY_CONSTRAINT_LOCATOR);
            }
            if (units == null) {
                throw new CSWFilterException("Missing units parameter for distanceBuffer operator.",
                        INVALID_PARAMETER_CODE, QUERY_CONSTRAINT_LOCATOR);
            }
            if (geomEl == null || geomEl.getValue() == null) {
                throw new CSWFilterException("Missing geometry object for distanceBuffer operator.",
                        INVALID_PARAMETER_CODE, QUERY_CONSTRAINT_LOCATOR);
            }

            Object gml = geomEl.getValue();
            Geometry geometry = null;
            String crsName = null;

            // transform the GML envelope into JTS polygon
            try {
                if (gml instanceof PointType) {
                    PointType gmlPoint = (PointType) gml;
                    crsName = gmlPoint.getSrsName();
                    geometry = GeometrytoJTS.toJTS(gmlPoint);
                } else if (gml instanceof LineStringType) {
                    LineStringType gmlLine = (LineStringType) gml;
                    crsName = gmlLine.getSrsName();
                    geometry = GeometrytoJTS.toJTS(gmlLine);
                } else if (gml instanceof EnvelopeType) {
                    EnvelopeType gmlEnvelope = (EnvelopeType) gml;
                    crsName = gmlEnvelope.getSrsName();
                    geometry = GeometrytoJTS.toJTS(gmlEnvelope);
                }

                if (operator.equals("DWithin")) {
                    spatialfilter = LuceneOGCFilter.wrap(FF.dwithin(LuceneOGCFilter.GEOMETRY_PROPERTY, FF
                            .literal(geometry), distance, units));
                } else if (operator.equals("Beyond")) {
                    spatialfilter = LuceneOGCFilter.wrap(FF.beyond(LuceneOGCFilter.GEOMETRY_PROPERTY, FF
                            .literal(geometry), distance, units));
                } else {
                    throw new CSWFilterException("Unknow DistanceBuffer operator: " + operator, INVALID_PARAMETER_CODE,
                            QUERY_CONSTRAINT_LOCATOR);
                }
            } catch (NoSuchAuthorityCodeException e) {
                throw new CSWFilterException(UNKNOW_CRS_ERROR_MSG + crsName, INVALID_PARAMETER_CODE,
                        QUERY_CONSTRAINT_LOCATOR);
            } catch (FactoryException e) {
                throw new CSWFilterException(FACTORY_BBOX_ERROR_MSG + e.getMessage(), INVALID_PARAMETER_CODE,
                        QUERY_CONSTRAINT_LOCATOR);
            } catch (IllegalArgumentException e) {
                throw new CSWFilterException(INCORRECT_BBOX_DIM_ERROR_MSG + e.getMessage(), INVALID_PARAMETER_CODE,
                        QUERY_CONSTRAINT_LOCATOR);
            }
        } else if (spatialOps instanceof BinarySpatialOpType) {
            BinarySpatialOpType binSpatial = (BinarySpatialOpType) spatialOps;

            String propertyName = null;
            String operator = spatialOpsEl.getName().getLocalPart();
            operator = operator.toUpperCase();
            Object gmlGeometry = null;

            // the propertyName
            if (binSpatial.getPropertyName() != null && binSpatial.getPropertyName().getValue() != null) {
                PropertyNameType p = binSpatial.getPropertyName().getValue();
                propertyName = p.getContent();
            }

            // geometric object: envelope
            if (binSpatial.getEnvelope() != null && binSpatial.getEnvelope().getValue() != null) {
                gmlGeometry = binSpatial.getEnvelope().getValue();
            }

            if (binSpatial.getAbstractGeometry() != null && binSpatial.getAbstractGeometry().getValue() != null) {
                AbstractGeometryType ab = binSpatial.getAbstractGeometry().getValue();

                // geometric object: point
                if (ab instanceof PointType) {
                    gmlGeometry = ab;
                }
                // geometric object: Line
                else if (ab instanceof LineStringType) {
                    gmlGeometry = ab;
                } else if (ab == null) {
                    throw new IllegalArgumentException("null value in BinarySpatialOp type");
                } else {
                    throw new IllegalArgumentException("unknow BinarySpatialOp type: " + ab.getClass().getSimpleName());
                }
            }

            if (propertyName == null && gmlGeometry == null) {
                throw new CSWFilterException("Missing propertyName or geometry parameter for binary spatial operator.",
                        INVALID_PARAMETER_CODE, QUERY_CONSTRAINT_LOCATOR);
            }
            SpatialFilterType filterType = null;
            try {
                filterType = SpatialFilterType.valueOf(operator);
            } catch (IllegalArgumentException ex) {
                log.error("Unknow spatial filter type");
            }
            if (filterType == null) {
                throw new CSWFilterException("Unknow FilterType: " + operator, INVALID_PARAMETER_CODE,
                        QUERY_CONSTRAINT_LOCATOR);
            }

            String crsName = "undefined CRS";
            try {
                Geometry filterGeometry = null;
                if (gmlGeometry instanceof EnvelopeType) {
                    // transform the EnvelopeEntry in GeneralEnvelope
                    EnvelopeType gmlEnvelope = (EnvelopeType) gmlGeometry;
                    crsName = gmlEnvelope.getSrsName();
                    filterGeometry = GeometrytoJTS.toJTS(gmlEnvelope);
                } else if (gmlGeometry instanceof PointType) {
                    PointType gmlPoint = (PointType) gmlGeometry;
                    crsName = gmlPoint.getSrsName();
                    filterGeometry = GeometrytoJTS.toJTS(gmlPoint);
                } else if (gmlGeometry instanceof LineStringType) {
                    LineStringType gmlLine = (LineStringType) gmlGeometry;
                    crsName = gmlLine.getSrsName();
                    filterGeometry = GeometrytoJTS.toJTS(gmlLine);
                }

                int srid = SRIDGenerator.toSRID(crsName, Version.V1);
                filterGeometry.setSRID(srid);

                switch (filterType) {
                case CONTAINS:
                    spatialfilter = LuceneOGCFilter.wrap(FF.contains(LuceneOGCFilter.GEOMETRY_PROPERTY, FF
                            .literal(filterGeometry)));
                    break;
                case CROSSES:
                    spatialfilter = LuceneOGCFilter.wrap(FF.crosses(LuceneOGCFilter.GEOMETRY_PROPERTY, FF
                            .literal(filterGeometry)));
                    break;
                case DISJOINT:
                    spatialfilter = LuceneOGCFilter.wrap(FF.disjoint(LuceneOGCFilter.GEOMETRY_PROPERTY, FF
                            .literal(filterGeometry)));
                    break;
                case EQUALS:
                    spatialfilter = LuceneOGCFilter.wrap(FF.equal(LuceneOGCFilter.GEOMETRY_PROPERTY, FF
                            .literal(filterGeometry)));
                    break;
                case INTERSECTS:
                    spatialfilter = LuceneOGCFilter.wrap(FF.intersects(LuceneOGCFilter.GEOMETRY_PROPERTY, FF
                            .literal(filterGeometry)));
                    break;
                case OVERLAPS:
                    spatialfilter = LuceneOGCFilter.wrap(FF.overlaps(LuceneOGCFilter.GEOMETRY_PROPERTY, FF
                            .literal(filterGeometry)));
                    break;
                case TOUCHES:
                    spatialfilter = LuceneOGCFilter.wrap(FF.touches(LuceneOGCFilter.GEOMETRY_PROPERTY, FF
                            .literal(filterGeometry)));
                    break;
                case WITHIN:
                    spatialfilter = LuceneOGCFilter.wrap(FF.within(LuceneOGCFilter.GEOMETRY_PROPERTY, FF
                            .literal(filterGeometry)));
                    break;
                default:
                    log.info("using default filter within");
                    spatialfilter = LuceneOGCFilter.wrap(FF.within(LuceneOGCFilter.GEOMETRY_PROPERTY, FF
                            .literal(filterGeometry)));
                    break;
                }
            } catch (NoSuchAuthorityCodeException e) {
                throw new CSWFilterException(UNKNOW_CRS_ERROR_MSG + crsName, INVALID_PARAMETER_CODE,
                        QUERY_CONSTRAINT_LOCATOR);
            } catch (FactoryException e) {
                throw new CSWFilterException(FACTORY_BBOX_ERROR_MSG + e.getMessage(), INVALID_PARAMETER_CODE,
                        QUERY_CONSTRAINT_LOCATOR);
            } catch (IllegalArgumentException e) {
                throw new CSWFilterException(INCORRECT_BBOX_DIM_ERROR_MSG + e.getMessage(), INVALID_PARAMETER_CODE,
                        QUERY_CONSTRAINT_LOCATOR);
            }
        }
        return spatialfilter;
    }

    /**
     * Build a piece of Lucene query with the specified id filter.
     *
     * @param idsOpsEl
     * @return String
     * @throws CSWFilterException
     */
    private String processIDOperator(List<JAXBElement<? extends AbstractIdType>> idsOpsEl) {
        StringBuilder response = new StringBuilder();

        // TODO processIDOperator
        if (true) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        return response.toString();
    }

    /**
     * Remove the prefix from a propertyName.
     *
     * @param propertyName
     * @return String
     */
    private String removePrefix(String propertyName) {
        int i = propertyName.indexOf(':');
        if (i != -1) {
            propertyName = propertyName.substring(i + 1, propertyName.length());
        }
        return propertyName;
    }

    /**
     * Create a spatial filter for the given filter list.
     *
     * @param operator
     * @param filters
     * @param query
     * @return Filter
     */
    protected Filter getSpatialFilterFromList(int logicalOperand, List<Filter> filters, String query) {
        Filter spatialFilter = null;
        if (filters.size() == 1) {
            if (logicalOperand == SerialChainFilter.NOT) {
                int[] filterType = { SerialChainFilter.NOT };
                spatialFilter = new SerialChainFilter(filters, filterType);
                if (query.equals("")) {
                    logicalOperand = SerialChainFilter.AND;
                }
            } else {
                spatialFilter = filters.get(0);
            }
        } else if (filters.size() > 1) {
            int[] filterType = new int[filters.size() - 1];
            for (int i = 0; i < filterType.length; i++) {
                filterType[i] = logicalOperand;
            }
            spatialFilter = new SerialChainFilter(filters, filterType);
        }
        return spatialFilter;
    }

    private String maskPhrase(String str) {
        if (str.contains(" ") || str.contains(":")) {
            return "\"" + str + "\"";
        } else {
            return str;
        }
    }

}
