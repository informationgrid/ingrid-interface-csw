/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw2.filter.impl.geotools;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.AbstractFilter;
import org.geotools.filter.visitor.DefaultFilterVisitor;
import org.opengis.filter.And;
import org.opengis.filter.BinaryLogicOperator;
import org.opengis.filter.ExcludeFilter;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Id;
import org.opengis.filter.IncludeFilter;
import org.opengis.filter.Not;
import org.opengis.filter.Or;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.Multiply;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.expression.Subtract;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.Beyond;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.DWithin;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import de.ingrid.interfaces.csw2.exceptions.CSWOperationNotSupportedException;

public class IngridFilterVisitor extends DefaultFilterVisitor {

	/** The logging object **/
	private static Log log = LogFactory.getLog(FilterParserImpl.class);

	@Override
	public Object visit(And filter, Object data) {
		return visit((BinaryLogicOperator) filter, data);
	}

	@Override
	public Object visit(Or filter, Object data) {
		return visit((BinaryLogicOperator) filter, data);
	}

	@Override
	public Object visit(Not filter, Object data) {
		return visit((BinaryLogicOperator) filter, data);
	}

	@Override
	public Object visit(Id filter, Object data) {
		throw new RuntimeException(new CSWOperationNotSupportedException(
				"The enumeration of feature instances (FeatureId) is not supported.", "FeatureId"));
	}

	@Override
	public Object visit(PropertyIsEqualTo filter, Object data) {
		data = super.visit(filter, data);
		return data;
	}

	@Override
	public Object visit(PropertyIsLike filter, Object data) {
		if (data instanceof FilterVisitorContext) {
			FilterVisitorContext ctx = (FilterVisitorContext) data;
			ctx.setProperty(FilterProperty.CURRENT_COMPARISON_OPERATOR, filter);

			// handle the property name
			data = filter.getExpression().accept(this, data);
			// handle literal
			FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(null);
			data = filterFactory.literal(filter.getLiteral()).accept(this, data);
		}
		return data;
	}

	@Override
	public Object visit(PropertyIsLessThan filter, Object data) {
		if (data instanceof FilterVisitorContext) {
			FilterVisitorContext ctx = (FilterVisitorContext) data;
			ctx.setProperty(FilterProperty.CURRENT_COMPARISON_OPERATOR, filter);
			data = super.visit(filter, data);
		}
		return data;
	}

	@Override
	public Object visit(Disjoint filter, Object data) {
		if (data instanceof FilterVisitorContext) {
			FilterVisitorContext ctx = (FilterVisitorContext) data;
			if (!(ctx.getProperty(FilterProperty.CURRENT_LOGICAL_OPERATION) instanceof Not)) {
				throw new RuntimeException(new CSWOperationNotSupportedException(
						"The search for areas that do not touch the BBOX (Disjoint) in not supported.", "Disjoint"));
			}
			ctx.setProperty(FilterProperty.CURRENT_SPATIAL_OPERATOR_NAME_TYPE, filter);
			// skip the evaluation of the property name since it is not used
			// here
			// data = filter.getExpression1().accept( this, data );
			data = filter.getExpression2().accept(this, data);
		}
		return data;
	}

	@Override
	public Object visit(Intersects filter, Object data) {
		if (data instanceof FilterVisitorContext) {
			FilterVisitorContext ctx = (FilterVisitorContext) data;
			ctx.setProperty(FilterProperty.CURRENT_SPATIAL_OPERATOR_NAME_TYPE, filter);
			// skip the evaluation of the property name since it is not used
			// here
			// data = filter.getExpression1().accept( this, data );
			data = filter.getExpression2().accept(this, data);
		}
		return data;
	}

	@Override
	public Object visit(Contains filter, Object data) {
		if (data instanceof FilterVisitorContext) {
			FilterVisitorContext ctx = (FilterVisitorContext) data;
			ctx.setProperty(FilterProperty.CURRENT_SPATIAL_OPERATOR_NAME_TYPE, filter);
			// skip the evaluation of the property name since it is not used
			// here
			// data = filter.getExpression1().accept( this, data );
			data = filter.getExpression2().accept(this, data);
		}
		return data;
	}

	@Override
	public Object visit(Within filter, Object data) {
		if (data instanceof FilterVisitorContext) {
			FilterVisitorContext ctx = (FilterVisitorContext) data;
			ctx.setProperty(FilterProperty.CURRENT_SPATIAL_OPERATOR_NAME_TYPE, filter);
			// skip the evaluation of the property name since it is not used
			// here
			// data = filter.getExpression1().accept( this, data );
			data = filter.getExpression2().accept(this, data);
		}
		return data;
	}

	@Override
	public Object visit(final BBOX filter, Object data) {
		if (data instanceof FilterVisitorContext) {
			FilterVisitorContext ctx = (FilterVisitorContext) data;
			ctx.setProperty(FilterProperty.CURRENT_SPATIAL_OPERATOR_NAME_TYPE, filter);
			// skip the evaluation of the property name since it is not used
			// here
			// data = filter.getExpression1().accept( this, data );
			data = filter.getExpression2().accept(this, data);
		}
		return data;
	}

	@Override
	public Object visit(Add expression, Object data) {
		throw new RuntimeException(new CSWOperationNotSupportedException("Arithmetic operator 'Add' is not supported.",
				"Add"));
	}

	@Override
	public Object visit(Beyond filter, Object data) {
		throw new RuntimeException(new CSWOperationNotSupportedException("Spatial operator 'Beyond' is not supported.",
				"Beyond"));
	}

	@Override
	public Object visit(Crosses filter, Object data) {
		throw new RuntimeException(new CSWOperationNotSupportedException(
				"Spatial operator 'Crosses' is not supported.", "Beyond"));
	}

	@Override
	public Object visit(Divide expression, Object data) {
		throw new RuntimeException(new CSWOperationNotSupportedException(
				"Arithmetic operator 'Divide' is not supported.", "Divide"));
	}

	@Override
	public Object visit(DWithin filter, Object data) {
		throw new RuntimeException(new CSWOperationNotSupportedException(
				"Spatial operator 'DWithin' is not supported.", "DWithin"));
	}

	@Override
	public Object visit(Equals filter, Object data) {
		throw new RuntimeException(new CSWOperationNotSupportedException("Spatial operator 'Equals' is not supported.",
				"Equals"));
	}

	@Override
	public Object visit(ExcludeFilter filter, Object data) {
		throw new RuntimeException(new CSWOperationNotSupportedException("Exclude filter is not supported.",
				"ExcludeFilter"));
	}

	@Override
	public Object visit(Function expression, Object data) {
		throw new RuntimeException(new CSWOperationNotSupportedException("Functions are not supported.", "Function"));
	}

	@Override
	public Object visit(IncludeFilter filter, Object data) {
		throw new RuntimeException(new CSWOperationNotSupportedException("Include filter is not supported.",
				"IncludeFilter"));
	}

	@Override
	public Object visit(Multiply expression, Object data) {
		throw new RuntimeException(new CSWOperationNotSupportedException(
				"Arithmetic operator 'Multiply' is not supported.", "Multiply"));
	}

	@Override
	public Object visit(Overlaps filter, Object data) {
		throw new RuntimeException(new CSWOperationNotSupportedException(
				"Spatial operator 'Overlaps' is not supported.", "Overlaps"));
	}

	@Override
	public Object visit(PropertyIsBetween filter, Object data) {
		if (data instanceof FilterVisitorContext) {
			FilterVisitorContext ctx = (FilterVisitorContext) data;
			ctx.setProperty(FilterProperty.CURRENT_COMPARISON_OPERATOR, filter);
			// overwrite super behavior because we need a different order
			// of execution here. Expression has to be visited first, to be able
			// to store the property name.
			data = filter.getExpression().accept(this, data);
			data = filter.getLowerBoundary().accept(this, data);
			data = filter.getUpperBoundary().accept(this, data);
		} else {
			data = super.visit(filter, data);
		}
		return data;
	}

	@Override
	public Object visit(PropertyIsGreaterThan filter, Object data) {
		// TODO Auto-generated method stub
		return super.visit(filter, data);
	}

	@Override
	public Object visit(PropertyIsGreaterThanOrEqualTo filter, Object data) {
		// TODO Auto-generated method stub
		return super.visit(filter, data);
	}

	@Override
	public Object visit(PropertyIsLessThanOrEqualTo filter, Object data) {
		// TODO Auto-generated method stub
		return super.visit(filter, data);
	}

	@Override
	public Object visit(PropertyIsNotEqualTo filter, Object data) {
		// TODO Auto-generated method stub
		return super.visit(filter, data);
	}

	@Override
	public Object visit(PropertyIsNull filter, Object data) {
		// TODO Auto-generated method stub
		return super.visit(filter, data);
	}

	@Override
	public Object visit(Subtract expression, Object data) {
		// TODO Auto-generated method stub
		return super.visit(expression, data);
	}

	@Override
	public Object visit(Touches filter, Object data) {
		// TODO Auto-generated method stub
		return super.visit(filter, data);
	}

	@Override
	public Object visitNullFilter(Object data) {
		// TODO Auto-generated method stub
		return super.visitNullFilter(data);
	}

	@Override
	public Object visit(Literal expression, Object data) {
		if (data instanceof FilterVisitorContext) {
			FilterVisitorContext ctx = (FilterVisitorContext) data;
			Object value = expression.getValue();
			if (value instanceof Geometry) {
				Geometry p = (Geometry) value;
				Envelope e = p.getEnvelopeInternal();
				ctx.appendQueryString("x1:" + e.getMinX() + " x2:" + e.getMaxX() + " y1:" + e.getMinY() + " y2:"
						+ e.getMaxY());
				// the Disjoint can only reach this point if there is a logical
				// NO operator involved
				// Not Disjoint == Intersects == BBOX
				if (ctx.getProperty(FilterProperty.CURRENT_SPATIAL_OPERATOR_NAME_TYPE) instanceof Intersects
						|| ctx.getProperty(FilterProperty.CURRENT_SPATIAL_OPERATOR_NAME_TYPE) instanceof BBOX
						|| ctx.getProperty(FilterProperty.CURRENT_SPATIAL_OPERATOR_NAME_TYPE) instanceof Disjoint) {
					ctx.appendQueryString(" coord:intersect");
				} else if (ctx.getProperty(FilterProperty.CURRENT_SPATIAL_OPERATOR_NAME_TYPE) instanceof Contains) {
					ctx.appendQueryString(" coord:include");
				} else if (ctx.getProperty(FilterProperty.CURRENT_SPATIAL_OPERATOR_NAME_TYPE) instanceof Within) {
					ctx.appendQueryString(" coord:inside");
				}
			} else {
				// map the property name to an InGrid property
				String ingridPropertyName = null;

				// convert the value according to the current property
				String curPropertyName = (String) ctx.getProperty(FilterProperty.CURRENT_PROPERTY_NAME);
				try {
					ingridPropertyName = PropertyNameMapping.map(curPropertyName, ctx);
				} catch (Exception e) {
					if (log.isInfoEnabled()) {
						log.info("Queriable '"+curPropertyName+" is not supported.");
					}
					throw new RuntimeException(new CSWOperationNotSupportedException(
							"Queriable '"+curPropertyName+" is not supported.", "Filterquery"));
				}
				if (ingridPropertyName != null && ingridPropertyName.length() > 0) {
					// build the query syntax according to the comparison
					// operator
					AbstractFilter comparisonOperator = (AbstractFilter) ctx
							.getProperty(FilterProperty.CURRENT_COMPARISON_OPERATOR);
					if (comparisonOperator instanceof PropertyIsLessThan) {
						ctx.appendQueryString(ingridPropertyName + ":");
						ctx.appendQueryString("[0 TO "
								+ PropertyValueConverter.convert(curPropertyName, value.toString()) + "]");
					} else if (comparisonOperator instanceof PropertyIsBetween) {
						Object lowerBetweenBoundary = ctx
								.getProperty(FilterProperty.CURRENT_PROPERTY_BETWEEN_LOWER_BOUNDARY);
						if (lowerBetweenBoundary == null) {
							ctx.setProperty(FilterProperty.CURRENT_PROPERTY_BETWEEN_LOWER_BOUNDARY, value);
						} else {
							String lowerBoundaryString = PropertyValueConverter.convert(curPropertyName,
									lowerBetweenBoundary);
							String upperBoundaryString = PropertyValueConverter.convert(curPropertyName, value);
							ctx.appendQueryString(ingridPropertyName + ":");
							ctx.appendQueryString("[" + lowerBoundaryString + " TO " + upperBoundaryString + "]");
						}
					} else if (comparisonOperator instanceof PropertyIsLike) {
						String literal = value.toString();
						literal = literal.replaceAll("\\" + ((PropertyIsLike) comparisonOperator).getWildCard(), "\\*");
						literal = literal.replaceAll("\\" + ((PropertyIsLike) comparisonOperator).getSingleChar(),
								"\\?");
						literal = literal.replaceAll("\\" + ((PropertyIsLike) comparisonOperator).getEscape() + "\\?",
								((PropertyIsLike) comparisonOperator).getSingleChar());
						literal = literal.replaceAll("\\" + ((PropertyIsLike) comparisonOperator).getEscape() + "\\*",
								((PropertyIsLike) comparisonOperator).getWildCard());
						if (literal.startsWith("*") || literal.startsWith("?")) {
							if (log.isInfoEnabled()) {
								log.info("Leading wildcard found in PropertyIsLike literal '" + literal + ".");
							}
							throw new RuntimeException(new CSWOperationNotSupportedException(
									"Leading wildcards not supported.", "PropertyIsLike"));
						} else {
							ctx.appendQueryString(ingridPropertyName + ":");
							ctx.appendQueryString(literal);
						}
					} else {
						ctx.appendQueryString(ingridPropertyName + ":");
						ctx.appendQueryString(value.toString());
					}
				}

			}
		}
		data = super.visit(expression, data);
		return data;
	}

	@Override
	public Object visit(PropertyName expression, Object data) {
		if (data instanceof FilterVisitorContext) {
			FilterVisitorContext ctx = (FilterVisitorContext) data;
			// store the property name for later reference
			ctx.setProperty(FilterProperty.CURRENT_PROPERTY_NAME, expression.getPropertyName());
		}
		data = super.visit(expression, data);
		return data;
	}

	protected Object visit(BinaryLogicOperator op, Object data) {
		String opStr = null;
		if (op instanceof And)
			opStr = " AND ";
		if (op instanceof Or)
			opStr = " OR ";
		if (op instanceof Not)
			opStr = " NOT ";

		if (data instanceof FilterVisitorContext) {
			FilterVisitorContext ctx = (FilterVisitorContext) data;
			// store logical operation for later use
			ctx.setProperty(FilterProperty.CURRENT_LOGICAL_OPERATION, op);

			ctx.appendQueryString("(");
			if (op.getChildren() != null) {
				Iterator<Filter> iterator = op.getChildren().iterator();
				while (iterator.hasNext()) {
					// reset all flag properties in context
					ctx.setProperty(FilterProperty.CURRENT_COMPARISON_OPERATOR, null);
					ctx.setProperty(FilterProperty.CURRENT_SPATIAL_OPERATOR_NAME_TYPE, null);
					ctx.setProperty(FilterProperty.CURRENT_PROPERTY_NAME, null);
					ctx.setProperty(FilterProperty.CURRENT_PROPERTY_BETWEEN_LOWER_BOUNDARY, null);
					iterator.next().accept(this, ctx);

					if (iterator.hasNext()) {
						ctx.appendQueryString(opStr);
					}
				}
			}
			ctx.appendQueryString(")");
		}
		return data;
	}
}
