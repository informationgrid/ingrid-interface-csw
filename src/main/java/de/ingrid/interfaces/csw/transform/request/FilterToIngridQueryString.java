
/*
 * date: 21.10.2005
 */

package de.ingrid.interfaces.csw.transform.request;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import de.ingrid.interfaces.csw.analyse.SessionParameters;
import de.ingrid.interfaces.csw.exceptions.CSWFilterException;
import de.ingrid.interfaces.csw.exceptions.CSWInvalidParameterValueException;
import de.ingrid.interfaces.csw.exceptions.CSWMissingParameterValueException;
import de.ingrid.interfaces.csw.exceptions.CSWNoApplicableCodeException;
import de.ingrid.interfaces.csw.exceptions.CSWOperationNotSupportedException;
import de.ingrid.interfaces.csw.tools.PatternTools;
import de.ingrid.interfaces.csw.transform.ComparisonOps;
import de.ingrid.interfaces.csw.transform.ComparisonOpsImpl;
import de.ingrid.interfaces.csw.transform.Expression;
import de.ingrid.interfaces.csw.transform.ExpressionImpl;
import de.ingrid.interfaces.csw.transform.Filter;
import de.ingrid.interfaces.csw.transform.FilterConst;
import de.ingrid.interfaces.csw.transform.FilterOperation;
import de.ingrid.interfaces.csw.transform.LogicalOps;
import de.ingrid.interfaces.csw.transform.SpatialOps;
import de.ingrid.interfaces.csw.transform.SpatialOpsImpl;
import de.ingrid.interfaces.csw.transform.ComparisonOps.CompOperation;
import de.ingrid.interfaces.csw.transform.ComparisonOps.PropertyIsBetween;
import de.ingrid.interfaces.csw.transform.ComparisonOps.PropertyIsEqualTo;
import de.ingrid.interfaces.csw.transform.ComparisonOps.PropertyIsGreaterThan;
import de.ingrid.interfaces.csw.transform.ComparisonOps.PropertyIsGreaterThanOrEqualTo;
import de.ingrid.interfaces.csw.transform.ComparisonOps.PropertyIsLessThan;
import de.ingrid.interfaces.csw.transform.ComparisonOps.PropertyIsLessThanOrEqualTo;
import de.ingrid.interfaces.csw.transform.ComparisonOps.PropertyIsLike;
import de.ingrid.interfaces.csw.transform.ComparisonOps.PropertyIsNull;
import de.ingrid.interfaces.csw.transform.ComparisonOpsImpl.PropertyIsBetweenImpl;
import de.ingrid.interfaces.csw.transform.Expression.BaseExpr;
import de.ingrid.interfaces.csw.transform.Expression.Literal;
import de.ingrid.interfaces.csw.transform.Expression.PropertyName;
import de.ingrid.interfaces.csw.transform.ExpressionImpl.LiteralImpl;
import de.ingrid.interfaces.csw.transform.LogicalOps.Logic;
import de.ingrid.interfaces.csw.transform.LogicalOps.Not;
import de.ingrid.interfaces.csw.transform.SpatialOps.Box;
import de.ingrid.interfaces.csw.transform.SpatialOps.Disjoint;
import de.ingrid.interfaces.csw.transform.SpatialOps.Intersects;
import de.ingrid.interfaces.csw.transform.SpatialOps.Spatial;
import de.ingrid.interfaces.csw.transform.SpatialOpsImpl.BoxImpl;
import de.ingrid.interfaces.csw.utils.UtilsCSWDate;

/**
 * 
 * This class generates an OGC filter encoding into a string for an ingrid query
 * 
 */
public class FilterToIngridQueryString {

	/**
	 * the stringbuffer to hold the created query string
	 */
	private StringBuffer sb = new StringBuffer();

	/**
	 * flag that shows if the current operation is not
	 */
	private boolean not = false;

	/**
	 * the current name of the field
	 */
	private String field = "";

	/**
	 * colon
	 */
	private final String colon = ":";

	/**
	 * greater than
	 */
	private final String greaterThan = ">";

	/**
	 * greater than or equal to</code>
	 */
	private final String greaterThanOrEqualTo = ">=";

	/**
	 * less than
	 */
	private final String lessThan = "<";

	/**
	 * less than or equal to</code>
	 */
	private final String lessThanOrEqualTo = "<=";

	/**
	 * left bracket
	 */
	private final String leftBracket = "(";

	/**
	 * right bracket
	 */
	private final String rightBracket = ")";

	/**
	 * the left square bracket
	 */
	private final String leftSquareBracket = "[";

	/**
	 * the right square bracket
	 */
	private final String rightSquareBracket = "]";

	/**
	 * the left curly bracket
	 */
	private final String leftCurlyBracket = "{";

	/**
	 * the right curly bracket
	 */
	private final String rightCurlyBracket = "}";

	/**
	 * the log object
	 */
	private static Log log = LogFactory.getLog(FilterToIngridQueryString.class);
	
	private SessionParameters _session;

	/**
	 * constructor.
	 */
	public FilterToIngridQueryString(SessionParameters session) {
		_session = session;
	}

	/**
	 * does the generation of the query string
	 * 
	 * @param filter
	 *            Filter
	 * @return StringBuffer sb the ingrid query string
	 * @throws Exception
	 *             e
	 */
	public final String generateQueryFromFilter(final Filter filter)
	throws Exception {
        if (log.isDebugEnabled()) {
        	log.debug("enter");
        }
		if (filter == null) {
			return "";
		}
		int type = filter.getOperationType();

		try {
			switch (type) {
			case FilterConst.COMPARISON:
				runComparison(filter.getComparisonOps());
				break;
			case FilterConst.LOGICAL:
				runLogicalExpr(filter.getLogicalOps());
				break;
			case FilterConst.SPATIAL:
				runSpatExpr(filter.getSpatialOps());
				break;
			case FilterConst.UNKNOWNOPERATION:
				throw new CSWOperationNotSupportedException(
						"This filter operation is not supported by this server.", "Filter");
			default:
				throw new CSWOperationNotSupportedException(
						"This filter operation is not supported by this server.", "Filter");
			}
		} catch (CSWOperationNotSupportedException e) {
			throw e;
		} catch (CSWInvalidParameterValueException e) {
			throw e;
		} catch (CSWMissingParameterValueException e) {
			throw e;
		} catch (CSWFilterException e) {
			throw new CSWNoApplicableCodeException(e + " in method FilterToIngridQueryString.generateQueryFromFilter(...)");
			// catch all other exceptions
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.debug(e + " in method FilterToIngridQueryString.generateQueryFromFilter(...)", e);
			}
			throw new CSWNoApplicableCodeException(e + " in method FilterToIngridQueryString.generateQueryFromFilter(...)");
		}

        if (log.isDebugEnabled()) {
        	log.debug("exiting, returning string: " + sb.toString());
        }
		// filter invalid query syntax that could not be handled before
        return sb.toString().replaceAll("(( AND)*|( OR)*|\\s|)\\s*\\(\\s\\)", "");
	}

	/**
	 * runs a comparison expression
	 * 
	 * @param comp
	 *            ComparisonOps
	 * @throws Exception
	 *             e
	 */
	private void runComparison(final ComparisonOps comp) throws Exception {
		ComparisonOps.CompOperation co = null;

		try {
			co = comp.getCompOperation();
		} catch (CSWFilterException e) {
			log.error(e.getMessage() + ": ", e);
			throw e;
		}

		if (co instanceof ComparisonOps.PropertyIsBetween) {
			runPropertyIsBetween(co);
		} else if (co instanceof ComparisonOps.PropertyIsNull) {
			runPropertyIsNull(co);
		} else if (co instanceof ComparisonOps.PropertyIsEqualTo) {
			runPropertyIsEqualTo(co);
		} else if (co instanceof ComparisonOps.PropertyIsLike) {
			runPropertyIsLike(co);
		} else if (co instanceof ComparisonOps.PropertyIsGreaterThan) {
			runPropertyIsGreaterThan(false, co);
		} else if (co instanceof ComparisonOps.PropertyIsGreaterThanOrEqualTo) {
			runPropertyIsGreaterThan(true, co);
		} else if (co instanceof ComparisonOps.PropertyIsLessThan) {
			runPropertyIsLessThan(false, co);
		} else if (co instanceof ComparisonOps.PropertyIsLessThanOrEqualTo) {
			runPropertyIsLessThan(true, co);
		}
	}

	/**
	 * runs PropertyIsNull operator
	 * 
	 * @param co
	 *            ComparisonOps.CompOperation
	 * @throws Exception
	 *             e
	 */
	private void runPropertyIsNull(final ComparisonOps.CompOperation co)
	throws Exception {
        if (log.isDebugEnabled()) {
        	log.debug("entering");
        }

		if (not) {
			sb.append(" NOT ");
			not = false;
		}

		sb.append(leftBracket);
		runExpr(co.getFirstExpression());
		sb.append("null");
		sb.append(rightBracket);

        if (log.isDebugEnabled()) {
        	log.debug("exiting");
        }
	}

	/**
	 * FIXME test PropertyIsBetween runs a PropertyIsBetween operation
	 * 
	 * @param co
	 *            ComparisonOps.CompOperation
	 * @throws Exception
	 *             e
	 */
	private void runPropertyIsBetween(final ComparisonOps.CompOperation co)
	throws Exception {
        if (log.isDebugEnabled()) {
        	log.debug("entering");
        }

		if (not) {
			sb.append(" NOT ");
			not = false;
		}

		sb.append(leftBracket);
		runExpr(co.getFirstExpression());
		sb.append(leftSquareBracket);
		String lowerBoundary = ((ComparisonOpsImpl.PropertyIsBetweenImpl) co)
				.getLowerBoundary().getBoundaryValue();
		String upperBoundary = ((ComparisonOpsImpl.PropertyIsBetweenImpl) co)
				.getUpperBoundary().getBoundaryValue();

		// FIXME dates coords
		// if (PatternTools.isDateField(field)) {
		//			
		// lowerBoundary = PatternTools.toValidDateFormat(lowerBoundary, true);
		//		    
		// upperBoundary = PatternTools.toValidDateFormat(upperBoundary, true);
		//			
		// } else if (PatternTools.isCoordField(field)) {
		//			
		// lowerBoundary = PatternTools.toLuceneIndexCoordFormat(lowerBoundary);
		//			
		// upperBoundary = PatternTools.toLuceneIndexCoordFormat(upperBoundary);
		//            
		// }

		sb.append(lowerBoundary);
		sb.append(" TO ");
		sb.append(upperBoundary);
		sb.append(rightSquareBracket);
		sb.append(rightBracket);

        if (log.isDebugEnabled()) {
        	log.debug("exiting");
        }
	}

	/**
	 * runs a logical expression
	 * 
	 * @param logOps
	 *            LogicalOps
	 * @throws Exception
	 *             e
	 */
	private void runLogicalExpr(final LogicalOps logOps) throws Exception {
        if (log.isDebugEnabled()) {
        	log.debug("entering");
        }

		// get first operation (if lo == Not it's the only operation)
		LogicalOps.Logic logic = logOps.getLogicalOperation();

		if (logic instanceof LogicalOps.Not) {
			not = true;
			sb = deletePreOperator(sb);
			sb.append(" NOT ");
		} else {
			sb.append(leftBracket);
		}

		FilterOperation fo = logic.getFirstFilterOperation();

		if (fo instanceof ComparisonOps) {
			runComparison((ComparisonOps) fo);
		} else if (fo instanceof LogicalOps) {
			runLogicalExpr((LogicalOps) fo);
		} else if (fo instanceof SpatialOps) {
			runSpatExpr((SpatialOps) fo);
		}

		if (logic instanceof LogicalOps.Not) {
			return;
		}

		// get additional operations if lo != Not
		FilterOperation[] fos = logic.getAdditionalFilterOperations();

		for (int i = 0; i < fos.length; i++) {
			sb.append(" " + logic.getOpName().toUpperCase() + " ");

			if (fos[i] instanceof ComparisonOps) {
				runComparison((ComparisonOps) fos[i]);
			} else if (fos[i] instanceof LogicalOps) {
				runLogicalExpr((LogicalOps) fos[i]);
			} else if (fos[i] instanceof SpatialOps) {
				runSpatExpr((SpatialOps) fos[i]);
			}
		}

		sb.append(rightBracket);
        if (log.isDebugEnabled()) {
        	log.debug("exiting");
        }
	}

	/**
	 * runs a spatial expression like a bounding box
	 * 
	 * @param spat
	 *            SpatialOps
	 * @throws Exception
	 *             e
	 */
	private void runSpatExpr(final SpatialOps spat) throws Exception {
        if (log.isDebugEnabled()) {
        	log.debug("entering");
        }

		// Disjoint is equivalent to 'not Intersects' (OR 'not BBOX')
		boolean operationIsDisjoint = false;
		SpatialOps.Spatial spatial = spat.getSpatialOperation();

		if (spatial instanceof SpatialOps.Disjoint) {
			spatial = ((SpatialOpsImpl) spat).new BoxImpl();
			operationIsDisjoint = true;
		} else if (spatial instanceof SpatialOps.Box) {
			operationIsDisjoint = false;
		} else if (spatial instanceof SpatialOps.Intersects) {
			operationIsDisjoint = false;
			spatial = ((SpatialOpsImpl) spat).new BoxImpl();
		} else {
			throw new CSWOperationNotSupportedException(
					"Spatial operation is not supported by this server",
					"Geometry/Envelope/Spatial");
		}

		if (not) {
			operationIsDisjoint = !operationIsDisjoint;
			not = false;
		}

		// get box object from the spatial object
		SpatialOpsImpl.BoxImpl box = (SpatialOpsImpl.BoxImpl) spatial;

		Element elemGeometry = box.getGeometryElement();
		NodeList nlCoords = elemGeometry.getChildNodes();

		String minx = null;
		String miny = null;
		String maxx = null;
		String maxy = null;
		
		String coordErrorLocator = null;
		Node node = null;
		String nodeValue = null;
		
		if (nlCoords.item(0).getLocalName().equalsIgnoreCase("coordinates")) {
			// 2006-10-13 Dirk Schwarzmann: This is the type "<gml:coordinates...>"
			coordErrorLocator = "gml:coordinates";
			Element elemCoords = (Element) nlCoords.item(0);
			
			if (elemCoords.getChildNodes() == null || elemCoords.getChildNodes().getLength() == 0) {
				throw new CSWMissingParameterValueException("Values of coordinates are missing", coordErrorLocator);
			}
			// Get the text node value:
			node = elemCoords.getChildNodes().item(0);
			nodeValue = node.getNodeValue();
			
			// Get the attributes for cs, decimal and ts
			// Default values
			String cs = ",";
			String decimal = ".";
			String ts = " ";
			String attrValue = null;
			
			/*
			 * Be careful! Since we use a regular expression later on to split the substrings,
			 * delimiters like a dot (.) for attribute "cs" will usually result in errors!
			 * This problem may not arise because floating point numbers _must_ use a dot as
			 * the separator between the integer value and the position after the decimal point,
			 * so it cannot be used for delimiting other values.
			 */
			attrValue = elemCoords.getAttribute(FilterConst.ATTR_BOX_CS);
			if (!attrValue.equals("")) {
				cs = new String(attrValue);
			}
			attrValue = elemCoords.getAttribute(FilterConst.ATTR_BOX_TS);
			if (!attrValue.equals("")) {
				ts = new String(attrValue);
			}
			attrValue = elemCoords.getAttribute(FilterConst.ATTR_BOX_DECIMAL);
			if (!attrValue.equals("")) {
				decimal = new String(attrValue);
			}
			
			// Extract the coordinate information from the value
			// First, break it into two pairs of x/y coordinates
			// Then, break every pair into the single coordinates
			String[] coordPairs = nodeValue.split(ts);
			if (coordPairs.length != 2) {
				throw new CSWInvalidParameterValueException("Values of coordinates are not correct (wrong amount of coordinate tuples)", coordErrorLocator);
			}
			
			// Grab the minimal boundary coord
			String[] coords = coordPairs[0].split(cs);
			if (coords.length != 2) {
				throw new CSWInvalidParameterValueException("Values of coordinates are not correct (only X-Y pairs are allowed)", coordErrorLocator);
			}
			minx = coords[0];
			miny = coords[1];
			
			// Grab the maximal boundary coord
			coords = coordPairs[1].split(cs);
			if (coords.length != 2) {
				throw new CSWInvalidParameterValueException("Values of coordinates are not correct (only X-Y pairs are allowed)", coordErrorLocator);
			}
			maxx = coords[0];
			maxy = coords[1];
		} else if (nlCoords.item(0).getLocalName().equalsIgnoreCase("coord")) {
			// 2006-10-13 Dirk Schwarzmann: This is the type "<gml:coord><gml:X>..."
			coordErrorLocator = "gml:coord";
			
			if (nlCoords.getLength() != 2) {
				throw new CSWInvalidParameterValueException("Values of coordinates are not correct (wrong amount of coordinate tuples)", coordErrorLocator);
			}
			
			// Grab the minimal boundary coord
			Node coordXY = null;
			coordXY = nlCoords.item(0);
			if (coordXY.getChildNodes().getLength() != 2) {
				throw new CSWInvalidParameterValueException("Values of coordinates are not correct (only X-Y pairs are allowed)", coordErrorLocator);
			}
			node = coordXY.getChildNodes().item(0);
			if (node.getLocalName().equals("X")) {
				minx = node.getChildNodes().item(0).getNodeValue();
			}
			if (node.getLocalName().equals("Y")) {
				miny = node.getChildNodes().item(0).getNodeValue();
			}
			node = coordXY.getChildNodes().item(1);
			if (node.getLocalName().equals("X")) {
				minx = node.getChildNodes().item(0).getNodeValue();
			}
			if (node.getLocalName().equals("Y")) {
				miny = node.getChildNodes().item(0).getNodeValue();
			}
			
			// Grab the maximal boundary coord
			coordXY = nlCoords.item(1);
			if (coordXY.getChildNodes().getLength() != 2) {
				throw new CSWInvalidParameterValueException("Values of coordinates are not correct (only X-Y pairs are allowed)", coordErrorLocator);
			}
			node = coordXY.getChildNodes().item(0);
			if (node.getLocalName().equals("X")) {
				maxx = node.getChildNodes().item(0).getNodeValue();
			}
			if (node.getLocalName().equals("Y")) {
				maxy = node.getChildNodes().item(0).getNodeValue();
			}
			node = coordXY.getChildNodes().item(1);
			if (node.getLocalName().equals("X")) {
				maxx = node.getChildNodes().item(0).getNodeValue();
			}
			if (node.getLocalName().equals("Y")) {
				maxy = node.getChildNodes().item(0).getNodeValue();
			}
		} else if (nlCoords.item(0).getLocalName().equalsIgnoreCase("lowerCorner")) {
			coordErrorLocator = "gml:envelope";
			try {
				String lowerCorner = nlCoords.item(0).getTextContent().trim();
				String upperCorner = nlCoords.item(1).getTextContent().trim();
				String[] lowerCornerCoords = lowerCorner.split(" ");
				String[] upperCornerCoords = upperCorner.split(" ");
				minx = lowerCornerCoords[0];
				miny = lowerCornerCoords[1];
				maxx = upperCornerCoords[0];
				maxy = upperCornerCoords[1];
			} catch (Exception e) {
				throw new CSWInvalidParameterValueException(
						"Values of lowerCorner, upperCorner are not correct", coordErrorLocator);
			}
			
		}

		// test coordinates
		try {
			Double.parseDouble(minx);
			Double.parseDouble(miny);
			Double.parseDouble(maxx);
			Double.parseDouble(maxy);
		} catch (NumberFormatException nfe) {
			throw new CSWInvalidParameterValueException(
					"Values of coordinates are not correct (not a valid number)", coordErrorLocator);
		}

		if (operationIsDisjoint) {
			sb.append(" NOT ");
		}

		sb.append(leftBracket);
		sb.append(" x1:" + minx);
		sb.append(" y1:" + miny);
		sb.append(" x2:" + maxx);
		sb.append(" y2:" + maxy);
		sb.append(" (coord:inside) ");
		sb.append(rightBracket);

        if (log.isDebugEnabled()) {
        	log.debug("exiting, coords: " + sb.toString());
        }
	}

	/**
	 * runs a single expression e.g. a property
	 * 
	 * @param expr
	 *            Expression
	 * @throws Exception
	 *             e
	 */
	private void runExpr(final Expression expr) throws Exception {
		Expression.BaseExpr eb = expr.getExpression();

		if (eb instanceof Expression.PropertyName) {
			String s = ((Expression.PropertyName) eb).getPropertyName();
			s = mapPropToField(s);

			if (!"".equals(s)) {
				sb.append(s + colon);
			}

			field = s;
		} else
		// if the property is a literal its value is taken
		// to compare it to a value stored within the 'database'.
		if (eb instanceof Expression.Literal) {
			Object obj = null;

			try {
				// TODO decode literal?
				obj = ((Expression.Literal) eb).getLiteral();
			} catch (CSWFilterException e) {
				log.error(e.getMessage(), e);
				throw e;
			}

			if (obj instanceof String) {
				String literal = (String) obj;
				literal = literal.trim();

				// Leading wildcards and singleChars are not possible with
				// lucene!
				// Therefore we have to remove them!
				while (literal.startsWith("*") && literal.length() != 1) {
					literal = literal.substring(1);
				}

				while (literal.startsWith("?") && literal.length() != 1) {
					literal = literal.substring(1);
				}

				// Ignore a possible time stamp in the date.
				// TODO: make it more generic!!!
				if (field.equals("t01_object.create_time")) {
					int timeStartPos = literal.indexOf("T");
					if (timeStartPos > -1) {
						log.warn("Found field CreationDate: truncate extended time stamp from its value");
						literal = literal.substring(0, timeStartPos);
					}
				}
				// Ignore a possible time stamp in the date.
				// TODO: make it more generic!!!
				if (field.equals("t01_object.mod_time")) {
					int timeStartPos = literal.indexOf("T");
					if (timeStartPos > -1) {
						log.warn("Found field CreationDate: truncate extended time stamp from its value");
						literal = literal.substring(0, timeStartPos);
					}
				}
				// Ignore a possible time stamp in the date.
				// TODO: make it more generic!!!
				if (field.equals("t1")) {
					int timeStartPos = literal.indexOf("T");
					if (timeStartPos > -1) {
						log.warn("Found field CreationDate: truncate extended time stamp from its value");
						literal = literal.substring(0, timeStartPos);
					}
				}
				// Ignore a possible time stamp in the date.
				// TODO: make it more generic!!!
				if (field.equals("t2")) {
					int timeStartPos = literal.indexOf("T");
					if (timeStartPos > -1) {
						log.warn("Found field CreationDate: truncate extended time stamp from its value");
						literal = literal.substring(0, timeStartPos);
					}
				}
				// Ignore a possible time stamp in the date.
				// TODO: make it more generic!!!
				if (field.indexOf("t0113_dataset_reference.reference_date") > -1) {
					int timeStartPos = literal.indexOf("T");
					if (timeStartPos > -1) {
						log.warn("Found field CreationDate: truncate extended time stamp from its value");
						literal = literal.substring(0, timeStartPos);
					}
				}
				
				// transform <.. ..> to "<.. ..>"
				if (literal.indexOf(' ') > -1) {
					literal = "\"" + literal + "\"";
				}
				
				sb.append(literal);
			} else if (obj instanceof Double || obj instanceof Integer) {
				if (field.equals("WEST") || field.equals("OST")
						|| field.equals("SUED") || field.equals("NORD")) {
					String coord = obj.toString();
					coord = coord.trim();
					sb.append(coord);
				} else {
					sb.append(obj.toString());
				}
			}
		} else {
			throw new NoSuchMethodError(expr + " not supported at the moment");
			// TODO other expressions
		}
	}

	/**
	 * runs a PropertyIsGreaterThan or a PropertyIsGreaterThanOrEqual to if
	 * orEqualTo is true
	 * 
	 * @param orEqualTo
	 *            boolean
	 * @param co
	 *            ComparisonOps.CompOperation
	 * @throws Exception
	 *             e
	 */
	private void runPropertyIsGreaterThan(final boolean orEqualTo, final ComparisonOps.CompOperation co)
	throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("runPropertyIsGreaterThan orEqualTo: " + orEqualTo);
		}

		if (not) {
			not = false;
		}

		runExpr(co.getFirstExpression());
		String value = ((Expression.Literal)co.getSecondExpression().getExpression()).getLiteral().toString();

		if (UtilsCSWDate.isCSWDate(value)) {
			sb.append("[").append(UtilsCSWDate.getQueryDateStyle(value)).append(" TO 99990101]");
		} else {
			if (orEqualTo) {
				sb.append(this.greaterThanOrEqualTo);
			} else {
				sb.append(this.greaterThan);
			}
			runExpr(co.getSecondExpression());
		}

		if (log.isDebugEnabled()) {
			log.debug("exiting, property=" + sb.toString());
		}
	}

	/**
	 * runs a PropertyIsLessThan or a PropertyIsLessThanOrEqual to if orEqualTo
	 * is true
	 * 
	 * @param orEqualTo
	 *            boolean
	 * @param co
	 *            ComparisonOps.CompOperation
	 * @throws Exception
	 *             e
	 */
	private void runPropertyIsLessThan(final boolean orEqualTo, final ComparisonOps.CompOperation co)
	throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("runPropertyIsLessThan orEqualTo: " + orEqualTo);
		}

		if (not) {
			not = false;
		}

		runExpr(co.getFirstExpression());
		String value = ((Expression.Literal)co.getSecondExpression().getExpression()).getLiteral().toString();

		if (UtilsCSWDate.isCSWDate(value)) {
			sb.append("[00000101 TO ").append(UtilsCSWDate.getQueryDateStyle(value)).append("]");
		} else {
			if (orEqualTo) {
				sb.append(this.lessThanOrEqualTo);
			} else {
				sb.append(this.lessThan);
			}
			runExpr(co.getSecondExpression());
		}
		
		if (log.isDebugEnabled()) {
			log.debug("exiting, property=" + sb.toString());
		}
	}

	/**
	 * runs a PropertyIsEqualTo
	 * 
	 * @param co
	 *            ComparisonOps.CompOperation
	 * @throws Exception
	 *             e
	 */
	private void runPropertyIsEqualTo(final ComparisonOps.CompOperation co)
	throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("entering");
		}

		if (not) {
			not = false;
		}
		
		// check for type query
		if (co.getFirstExpression().getExpression() instanceof Expression.PropertyName && co.getSecondExpression().getExpression() instanceof Expression.Literal) {
			String property = ((Expression.PropertyName) co.getFirstExpression().getExpression()).getPropertyName();
			String inPropWithoutNS = property.indexOf(':') > -1 ? property.substring(property.indexOf(':') + 1, property.length()): property;

			if (inPropWithoutNS.equalsIgnoreCase("type")) {	
				String type = (String)((Expression.Literal)  co.getSecondExpression().getExpression()).getLiteral();
				if (type.equalsIgnoreCase("service") || type.equalsIgnoreCase("application")) {
					_session.setTypeNameIsService(true);
				} else if (type.equalsIgnoreCase("dataset") || type.equalsIgnoreCase("datasetcollection")) {
					_session.setTypeNameIsDataset(true);
				} else if (type.equalsIgnoreCase("nonGeographicDataset")) {
					_session.setTypeNameIsNonGeographicDataset(true);
				}
				sb = deletePreOperator(sb);
			} else {
				runExpr(co.getFirstExpression());
				// check for date string pattern
				// adapt date string pattern if nessecary
				String value = ((Expression.Literal)co.getSecondExpression().getExpression()).getLiteral().toString();
				if (UtilsCSWDate.isCSWDate(value)) {
					value = UtilsCSWDate.getQueryDateStyle(value);
				}
				runExpr(co.getSecondExpression());
			}
		} else {
			runExpr(co.getFirstExpression());
			// check for date string pattern
			// adapt date string pattern if nessecary
			String value = ((Expression.Literal)co.getSecondExpression().getExpression()).getLiteral().toString();
			if (UtilsCSWDate.isCSWDate(value)) {
				value = UtilsCSWDate.getQueryDateStyle(value);
			}
			runExpr(co.getSecondExpression());
		}


		if (log.isDebugEnabled()) {
			log.debug("exiting");
		}
	}

	/**
	 * runs a PropertyIsLike
	 * 
	 * @param co
	 *            ComparisonOps.CompOperation
	 * @throws Exception
	 *             e
	 */
	private void runPropertyIsLike(final ComparisonOps.CompOperation co)
	throws Exception {
		// Wildcards ersetzen mit '*'
		// SingleChars ersetzen mit '?'
		// Escape char beruecksichtigen
		ComparisonOps.PropertyIsLike propIsLike = (ComparisonOps.PropertyIsLike) co;
		char charWildcard = propIsLike.getWildCard();
		char charSingleChar = propIsLike.getSingleChar();
		char charEscapeChar = propIsLike.getEscape();

		if (charWildcard == ' ') {
			throw new CSWMissingParameterValueException(
					"Attribute 'wildCard' of Element "
							+ "'PropertyIsLike' is not present.", "wildCard");
		}

		if (charSingleChar == ' ') {
			throw new CSWMissingParameterValueException(
					"Attribute 'singleChar' of Element "
							+ "'PropertyIsLike' is not present.", "singleChar");
		}

		if (charEscapeChar == ' ') {
			throw new CSWMissingParameterValueException("Attribute 'escapeChar'  or  'escape' "
							+ "of Element 'PropertyIsLike' is not present.", "escapeChar");
		}

		ExpressionImpl.LiteralImpl literal = (ExpressionImpl.LiteralImpl) propIsLike.getLiteral();
		Object obj = literal.getLiteral();

		if (obj instanceof String) {
			String literalValue = ((String) obj).trim();
			
			char[] charArray = literalValue.toCharArray();
			literalValue = "";
			char currentChar;

			// jeden Buchstaben durchgehen
			for (int i = 0; i < charArray.length; i++) {
				currentChar = charArray[i];

				if (currentChar == charEscapeChar) {
					i++;
					currentChar = charArray[i];
					literalValue = literalValue + currentChar;
				} else if (currentChar == charWildcard) {
					literalValue = literalValue + '*';
				} else if (currentChar == charSingleChar) {
					literalValue = literalValue + '?';
				} else {
					literalValue = literalValue + currentChar;
				}
			} // end for

			// Leading wildcards and singleChars are not possible with
			// lucene!
			// Therefore we have to remove them!
			while (literalValue.startsWith("*") && literalValue.length() != 1) {
				literalValue = literalValue.substring(1);
			}

			while (literalValue.startsWith("?") && literalValue.length() != 1) {
				literalValue = literalValue.substring(1);
			}
			
			literal.setLiteral(literalValue);
			propIsLike.setLiteral(literal);
		}

		if (not) {
			not = false;
		}
		runExpr(co.getFirstExpression());
		runExpr(co.getSecondExpression());
	}

	/**
	 * deletes operators like AND / OR which occur directly before a NOT
	 * operator. Cause: some query parsers can't handle this.
	 * 
	 * @param stringBuffer
	 *            StringBuffer
	 * @return stringBuffer StringBuffer
	 */
	public final StringBuffer deletePreOperator(final StringBuffer stringBuffer) {
		// the length of 'AND' plus a blank
		final int andLength = 4;
		// the length of 'OR' plus a blank
		final int orLength = 3;
		int sbLength = stringBuffer.length();
		int lastIndexOfAND = stringBuffer.lastIndexOf("AND");
		int lastIndexOfOR = stringBuffer.lastIndexOf("OR");
		
		if (lastIndexOfAND != -1 && lastIndexOfAND + andLength == sbLength) {
			stringBuffer.delete(lastIndexOfAND, sbLength);
		}

		if (lastIndexOfOR != -1 && lastIndexOfOR + orLength == sbLength) {
			stringBuffer.delete(lastIndexOfOR, sbLength);
		}

		return stringBuffer;

	}

	/**
	 * maps the OGC property names to the internal names (ingrid)
	 * 
	 * @param inprop
	 *            String property string
	 * @return outprop String
	 * @throws Exception
	 *             e
	 */
	private String mapPropToField(final String inprop) throws Exception {
        if (log.isDebugEnabled()) {
        	log.debug("mapProperty: input property: " + inprop);
        }

		String inPropWithoutNS = inprop.indexOf(':') > -1 ? inprop.substring(inprop.indexOf(':') + 1, inprop.length()): inprop;
		String outprop = null;

		// common queryable elements
		
		// all text fields
		if (inPropWithoutNS.equalsIgnoreCase("AnyText")) {
			// anytext --> content
			outprop = "";
			// MD_Metadata/identificationInfo/MD_DataIdentification/descriptiveKeywords/MD_Keywords/keyword
		} else if (inPropWithoutNS.equalsIgnoreCase("subject")) {
			outprop = "t04_search.searchterm";
			// MD_Metadata/identificationInfo/MD_DataIdentification/citation/title
		} else if (inPropWithoutNS.equalsIgnoreCase("title")) {
			outprop = "title";
			// MD_Metadata/identificationInfo/MD_DataIdentification/abstract
		} else if (inPropWithoutNS.equalsIgnoreCase("abstract")) {
			outprop = "summary";
			// MD_Metadata.distributionInfo.MD_Distribution.distributionFormat.MD_Format.name
		} else if (inPropWithoutNS.equalsIgnoreCase("format")) {
			outprop = "t0110_avail_format.format_value";
			// MD_Metadata.fileIdentifier
		} else if (inPropWithoutNS.equalsIgnoreCase("identifier")) {
			outprop = "t01_object.obj_id";
			// MD_Metadata.dateStamp .Date
		} else if (inPropWithoutNS.equalsIgnoreCase("Modified")) {
			outprop = "t01_object.mod_time";
			// MD_Metadata.hierarchyLevel.MD_ScopeCode@codeListValue
		} else if (inPropWithoutNS.equalsIgnoreCase("type")) {
			// TODO: must map to t011_obj_geo.hierarchy_level or t01_object.obj_class
			// depending on the value of the query parameter (see mapping)
			outprop = "";
		} else if (inPropWithoutNS.equalsIgnoreCase("Association")) {
			// TODO: NOT supported
			throw new CSWInvalidParameterValueException("Search for PropertyName '" + inprop + "' is not supported by this server.", "PropertyName");
			// MD_Metadata.identificationInfo.MD_DataIdentification.extent.EX_Extent.geographicElement.EX_GeographicBoundingBox.westBoundLongitude
		} else if (inPropWithoutNS.equalsIgnoreCase("WestBoundLongitude")) {
			outprop = "x1";
			// MD_Metadata.identificationInfo.MD_DataIdentification.extent.EX_Extent.geographicElement.EX_GeographicBoundingBox.eastBoundLongitude
		} else if (inPropWithoutNS.equalsIgnoreCase("EastBoundLongitude")) {
			outprop = "x2";
			// MD_Metadata.identificationInfo.MD_DataIdentification.extent.EX_Extent.geographicElement.EX_GeographicBoundingBox.southBoundLatitude
		} else if (inPropWithoutNS.equalsIgnoreCase("SouthBoundLatitude")) {
			outprop = "y1";
			// MD_Metadata.identificationInfo.MD_DataIdentification.extent.EX_Extent.geographicElement.EX_GeographicBoundingBox.northBoundLatitude
		} else if (inPropWithoutNS.equalsIgnoreCase("NorthBoundLatitude")) {
			outprop = "y2";			
			// MD_Metadata.referenceSystemInfo.MD_ReferenceSystem.referenceSystemIdentifier.RS_Identifier.codeSpace
		} else if (inPropWithoutNS.equalsIgnoreCase("Authority")) {
			// TODO: value of the query must be transformed into a wildcard query
			outprop = "t011_obj_geo.referencesystem_id";	
			// MD_Metadata.referenceSystemInfo.MD_ReferenceSystem.referenceSystemIdentifier.RS_Identifier.code
		} else if (inPropWithoutNS.equalsIgnoreCase("ID")) {
			// TODO: value of the query must be transformed into a wildcard query
			outprop = "t011_obj_geo.referencesystem_id";			
			// MD_Metadata.referenceSystemInfo.MD_ReferenceSystem.referenceSystemIdentifier.RS_Identifier.version
		} else if (inPropWithoutNS.equalsIgnoreCase("Version")) {
			// TODO: NOT supported
			throw new CSWInvalidParameterValueException("Search for PropertyName '" + inprop + "' is not supported by this server.", "PropertyName");
			
		// Additional queryable properties common to all information resources
			
			// MD_Metadata.identificationInfo.AbstractMD_Identification.citation.CI_Citation.date.CI_Date[dateType.CI_DateTypeCode.@codeListValue='revision'].date.Date
		} else if (inPropWithoutNS.equalsIgnoreCase("RevisionDate")) {
			outprop = "t0113_dataset_reference.type:3 t0113_dataset_reference.reference_date";
			// MD_Metadata/identificationInfo/MD_DataIdentification/citation/alternateTitle
		} else if (inPropWithoutNS.equalsIgnoreCase("AlternateTitle")) {
			outprop = "t01_object.dataset_alternate_name";
			// MD_Metadata.identificationInfo.AbstractMD_Identification.citation.CI_Citation.date.CI_Date[dateType.CI_DateTypeCode.@codeListValue='creation'].date.Date
		} else if (inPropWithoutNS.equalsIgnoreCase("CreationDate")) {
			outprop = "t0113_dataset_reference.type:1 t0113_dataset_reference.reference_date";
			// MD_Metadata.identificationInfo.AbstractMD_Identification.citation.CI_Citation.date.CI_Date[dateType.CI_DateTypeCode.@codeListValue='publication'].date.Date
		} else if (inPropWithoutNS.equalsIgnoreCase("PublicationDate")) {
			outprop = "t0113_dataset_reference.type:2 t0113_dataset_reference.reference_date";
			// MD_Metadata.identificationInfo.AbstractMD_Identification.pointOfContact.CI_ResponsibleParty.organisationName
		} else if (inPropWithoutNS.equalsIgnoreCase("OrganisationName")) {
			outprop = "t02_address.institution";
			// Boolean: MD_Metadata.AbstractMD_Identification.resourceConstraints.MD_SecurityConstraints
		} else if (inPropWithoutNS.equalsIgnoreCase("HasSecurityConstraints")) {
			outprop = "object_access.restriction_key:[0 TO 9]";
			// MD_Metadata.identificationInfo.AbstractMD_Identification.citation.CI_Citation.identifier.MD_Identifier.code		
		} else if (inPropWithoutNS.equalsIgnoreCase("ResourceIdentifier")) {
			outprop = "t011_obj_geo.datasource_uuid";
			// MD_Metadata.parentIdentifier
		} else if (inPropWithoutNS.equalsIgnoreCase("ParentIdentifier")) {
			outprop = "parent.object_node.obj_uuid";
			// MD_Identification.descriptiveKeywords.MD_Keywords.type
		} else if (inPropWithoutNS.equalsIgnoreCase("KeywordType")) {
			// TODO: must map value to searchterm_value.type (mapping required)
			outprop = "";

		// Additional queryable properties (dataset, datasetcollection, application)			
			
			// MD_Metadata.identificationInfo.MD_DataIdentification.topicCategory
		} else if (inPropWithoutNS.equalsIgnoreCase("TopicCategory")) {
			// TODO: value must be transformed via CodeList 527
			outprop = "";
			// MD_Metadata.identificationInfo.MD_DataIdentification.language
		} else if (inPropWithoutNS.equalsIgnoreCase("ResourceLanguage")) {
			// TODO: value must be transformed to ISO 639-1
			outprop = "t01_object.data_language_code";
			// MD_Metadata.identificationInfo.MD_DataIdentification.extent.EX_Extent.geographicElement.EX_GeographicDescription.geographicIdentifier.MD_Identifier.code
		} else if (inPropWithoutNS.equalsIgnoreCase("GeographicDescriptionCode")) {
			outprop = "location";
			// MD_Metadata.identificationInfo.MD_DataIdentification.spatialResolution.MD_Resolution.equivalentScale.MD_RepresentativeFraction.denominator
		} else if (inPropWithoutNS.equalsIgnoreCase("Denominator")) {
			outprop = "t011_obj_geo_scale.scale";
			// MD_Metadata.identificationInfo.MD_DataIdentification.spatialResolution.MD_Resolution.distance.gco:Distance
		} else if (inPropWithoutNS.equalsIgnoreCase("DistanceValue")) {
			outprop = "t011_obj_geo_scale.resolution_ground";
			// MD_Metadata.identificationInfo.MD_DataIdentification.spatialResolution.MD_Resolution.distance.gco:Distance@uom
		} else if (inPropWithoutNS.equalsIgnoreCase("DistanceUOM")) {
			// TODO: not mappable. "meter" is implicit set when t011_obj_geo_scale.resolution_ground is filled, "dpi"is implicit set when t011_obj_geo_scale.resolution_scan is filled
			outprop = "";
			// MD_Metadata.identificationInfo.MD_DataIdentification.extent.EX_Extent.temporalElement.EX_TemporalExtent.extent.TimePeriod.beginPosition
		} else if (inPropWithoutNS.equalsIgnoreCase("TempExtent_begin")) {
			// TODO: Ingrid Query field depends on values in TempExtent_begin and TempExtent_end (see mapping)
			// TODO: value must be transformed to IGC DateTime Format
			outprop = "t1";
			// MD_Metadata.identificationInfo.MD_DataIdentification.extent.EX_Extent.temporalElement.EX_TemporalExtent.extent.TimePeriod.endPosition
		} else if (inPropWithoutNS.equalsIgnoreCase("TempExtent_end")) {
			// TODO: Ingrid Query field depends on values in TempExtent_begin and TempExtent_end (see mapping)
			// TODO: value must be transformed to IGC DateTime Format
			outprop = "t2";

		// Additional queryable properties (service)
			
			// MD_Metadata.identificationInfo.SV_ServiceIdentification.serviceType
		} else if (inPropWithoutNS.equalsIgnoreCase("ServiceType")) {
			// TODO: should be mapped to t011_obj_serv.serv_type_key via 5100 (attn: special treatment required)
			outprop = "t011_obj_serv.type";
			// MD_Metadata.identificationInfo.SV_ServiceIdentification.serviceTypeVersion
		} else if (inPropWithoutNS.equalsIgnoreCase("ServiceTypeVersion")) {
			outprop = "t011_obj_serv_version.version";
			// MD_Metadata.identificationInfo.SV_ServiceIdentification.containsOperations.SV_OperationMetadata.operationName
		} else if (inPropWithoutNS.equalsIgnoreCase("Operation")) {
			outprop = "t011_obj_serv_operation.name";
			// MD_Metadata.identificationInfo.SV_ServiceIdentification.couplingType.SV_CouplingType.code@codeListValue
		} else if (inPropWithoutNS.equalsIgnoreCase("CouplingType")) {
			// TODO: must map to object_reference.special_ref, as value is only "tight" supported only if special_ref=3345 (Basisdaten) 
			outprop = "";
			// MD_Metadata.identificationInfo.SV_ServiceIdentification.operatesOn.MD_DataIdentification.citation.CI_Citation.identifier
		} else if (inPropWithoutNS.equalsIgnoreCase("OperatesOn")) {
			outprop = "object_reference.obj_to_uuid";
			// MD_Metadata.identificationInfo.SV_ServiceIdentification.coupledResource.SV_CoupledResource.identifier
		} else if (inPropWithoutNS.equalsIgnoreCase("OperatesOnIdentifier")) {
			outprop = "object_reference.obj_to_uuid";
			// MD_Metadata.identificationInfo.SV_ServiceIdentification.coupledResource.SV_CoupledResource.operationName
		} else if (inPropWithoutNS.equalsIgnoreCase("OperatesOnName")) {
			// TODO: not supported at the moment. To support it the mapping must be extended to map also the name of the referenced object to the index
			outprop = "";			
		} else {
			throw new CSWInvalidParameterValueException("Search for PropertyName '" + inprop + "' is not supported by this server.", "PropertyName");
		}

        if (log.isDebugEnabled()) {
        	log.debug("exiting returning string out property: " + outprop);
        }

		return outprop;
	}
}
