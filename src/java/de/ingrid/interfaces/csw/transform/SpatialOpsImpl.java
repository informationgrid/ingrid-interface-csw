package de.ingrid.interfaces.csw.transform;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.interfaces.csw.analyse.Namespaces;
import de.ingrid.interfaces.csw.exceptions.CSWFilterException;
import de.ingrid.interfaces.csw.exceptions.CSWOperationNotSupportedException;
import de.ingrid.interfaces.csw.tools.XMLTools;

public class SpatialOpsImpl implements SpatialOps {

	private Element element = null;

	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}

	public SpatialOpsImpl(Element element) {
		this.element = element;
	}

	/**
	 * creates a new empty logical operation tag
	 */
	public static SpatialOps createSpatial_Ops(Document doc, String ops) {
		// Debug.debugMethodBegin( "SpatialOpsImpl", "createSpatial_Ops" );

		SpatialOps op = new SpatialOpsImpl(doc.createElement(ops));

		// Debug.debugMethodEnd();
		return op;
	}

	/**
	 * returns the operation to be performed
	 */
	public Spatial getSpatialOperation() throws CSWFilterException {
		// Debug.debugMethodBegin( this, "getSpatialOperation" );

		Spatial spatial = null;

		try {

			if (element.getLocalName().equals(FilterConst.BBOX)) {
				spatial = new BoxImpl();
			} else if (element.getLocalName().equals(FilterConst.Beyond)) {
				spatial = new BeyondImpl();
			} else if (element.getLocalName().equals(FilterConst.Contains)) {
				spatial = new ContainsImpl();
			} else if (element.getLocalName().equals(FilterConst.Crosses)) {
				spatial = new CrossesImpl();
			} else if (element.getLocalName().equals(FilterConst.Disjoint)) {
				spatial = new DisjointImpl();
			} else if (element.getLocalName().equals(FilterConst.Equals)) {
				spatial = new EqualsImpl();
			} else if (element.getLocalName().equals(FilterConst.Inside)) {
				spatial = new InsideImpl();
			} else if (element.getLocalName().equals(FilterConst.Intersects)) {
				spatial = new IntersectsImpl();

			} else if (element.getLocalName().equals(FilterConst.Overlaps)) {
				spatial = new OverlapsImpl();
			} else if (element.getLocalName().equals(FilterConst.Touches)) {
				spatial = new TouchesImpl();
			} else if (element.getLocalName().equals(FilterConst.Within)) {
				spatial = new WithinImpl();
			}

		} catch (Exception e) {
			throw new CSWFilterException("invalid tag name: "
					+ element.getLocalName()
					+ " could not create operation class.\n" + e);
		}

		// Debug.debugMethodEnd();
		return spatial;
	}

	// //////////////////////////////////////////////////////////////////////
	// inner classes //
	// //////////////////////////////////////////////////////////////////////

	/**
	 * root class of all spatia operation classs
	 */
	public class SpatialImpl implements Spatial {

		public Element getAsElement() {
			return element;
		}

		/**
		 * returns the name of the property that should be compared
		 */
		public Expression.PropertyName getPropertyName() {
			// Debug.debugMethodBegin( this, "getPropertyName" );

			// get property name tag
			// Element elem =
			// (Element)element.getElementsByTagName(FilterConst.PropertyName).item(0);

			Element elem = (Element) element.getElementsByTagNameNS(
					element.getNamespaceURI(), FilterConst.PropertyName)
					.item(0);

			ExpressionImpl expr = new ExpressionImpl(elem);

			Expression.PropertyName pn = (Expression.PropertyName) expr
					.getExpression();

			// Debug.debugMethodEnd();
			return pn;
		}

		/**
		 * @see #getPropertyName
		 */
		public void setPropertyName(Expression.PropertyName name) {
			// Debug.debugMethodBegin( this, "setPropertyName" );

			// remove property tag if already exists
			XMLTools.removeNamedChildNodes(element, FilterConst.PropertyName);

			Element elem = ((ExpressionImpl.PropertyNameImpl) name)
					.getAsElement();
			XMLTools.insertNodeInto(elem, element);

			// Debug.debugMethodEnd();
		}

		/**
		 * returns the geometry that's the target of comparison
		 */
		/*
		 * public GMLGeometry getGeometry() throws CSWFilterException {
		 * //Debug.debugMethodBegin( this, "getGeometry" );
		 *  // get the child node (element) that contains the geometry //
		 * operator Element elem = getGeometryElement();
		 * 
		 * GMLGeometry geom = null; try { geom = GMLFactory.createGMLGeometry(
		 * elem ); } catch(Exception e) { throw new CSWFilterException("could
		 * not create Geometry within Filter" + "encoding spatial ops"); }
		 * 
		 * //Debug.debugMethodEnd(); return geom; }
		 * 
		 */
		/**
		 * @see #getGeometry
		 */
		/*
		 * public void setGeometry(GMLGeometry geometry) {
		 * //Debug.debugMethodBegin( this, "setGeometry" );
		 *  // get the child node (element) that contains the geometry //
		 * operator. If an error occurs no geometry exists. Else // it has to be
		 * removed first try { Element elem = getGeometryElement();
		 * element.removeChild( elem ); } catch(Exception e) {}
		 * 
		 * XMLTools.insertNodeInto( ((GMLGeometry_Impl)geometry).getAsElement(),
		 * element );
		 * 
		 * //Debug.debugMethodEnd(); }
		 */

		public Element getGeometryElement() throws Exception {
			NodeList nl = element.getChildNodes();
			Node nodeListItem = null;
			Element elemBox = null;

			if (nl != null && nl.getLength() > 0) {
				for (int i = 0; i < nl.getLength(); i++) {
					nodeListItem = nl.item(i);

					if (nodeListItem instanceof Element) {
						if (nodeListItem.getLocalName().equals(FilterConst.BOX)) {
							elemBox = (Element) nodeListItem;

							// test if namespace is correct
							String nameSpaceURI = elemBox.getNamespaceURI();

							if (nameSpaceURI != null
									&& !"".equals(nameSpaceURI)
									&& !Namespaces.GML.equals(nameSpaceURI)) {

								Exception e = new CSWOperationNotSupportedException(
										"Geometry Element Box within namespace URI '"
												+ nameSpaceURI
												+ "' is not supported.", "Filter");
								throw e;
							}

							return elemBox;
						} else if (nodeListItem.getLocalName().equals(FilterConst.BBOX)) {
							elemBox = (Element) nodeListItem.getLastChild();

							if (elemBox.getLocalName().equals(FilterConst.BOX)) {
								return elemBox;
							}
						} else if (nodeListItem.getLocalName().equals(FilterConst.ENVELOPE)) {
							elemBox = (Element) nodeListItem;
							// test if namespace is correct
							String nameSpaceURI = elemBox.getNamespaceURI();

							if (nameSpaceURI != null
									&& !"".equals(nameSpaceURI)
									&& !Namespaces.GML.equals(nameSpaceURI)) {

								Exception e = new CSWOperationNotSupportedException(
										"Geometry Element Box within namespace URI '"
												+ nameSpaceURI
												+ "' is not supported.", "Filter");
								throw e;
							}
							return elemBox;
						}
					}
				}
			}

			throw new CSWFilterException("No geometry found");
		}
	}

	/**
	 * represents the comparison if two geomerties are spatial equal
	 */
	public class EqualsImpl extends SpatialImpl implements Equals {
	}

	/**
	 * represents the comparison if two geomerties are spatial disjoint
	 */
	public class DisjointImpl extends SpatialImpl implements Disjoint {
	}

	/**
	 * represents the comparison if two geomerties spatially intersects
	 */
	public class IntersectsImpl extends SpatialImpl implements Intersects {

	}

	/**
	 * represents the comparison if two geomerties spatialy touches
	 */
	public class TouchesImpl extends SpatialImpl implements Touches {
	}

	/**
	 * represents the comparison if two geomerties spatially crosses
	 */
	public class CrossesImpl extends SpatialImpl implements Crosses {
	}

	/**
	 * represents the comparison if the first geometry is within a distance
	 * around the second
	 */
	public class WithinImpl extends SpatialImpl implements Within {

		/**
		 * returns the max distance of the first geometry to the second to be
		 * selected
		 */
		public Distance getDistance() {
			throw new NoSuchMethodError("Operation not supported yet");
		}

		/**
		 * @see #getDistance
		 */
		public void setDistance(Distance distance) {
			throw new NoSuchMethodError("Operation not supported yet");
		}

	}

	/**
	 * represents the comparison if the first geometry contains the second
	 */
	public class ContainsImpl extends SpatialImpl implements Contains {
	}

	/**
	 * represents the comparison if the first geometry is inside the second
	 */
	public class InsideImpl extends SpatialImpl implements Inside {
	}

	/**
	 * represents the comparison if the first geometry overlaps the second
	 */
	public class OverlapsImpl extends SpatialImpl implements Overlaps {
	}

	/**
	 * represents the comparison if the first geometry is beyond a distance two
	 * the second
	 */
	public class BeyondImpl extends SpatialImpl implements Beyond {

		/**
		 * returns the max distance of the first geometry to the second to be
		 * selected
		 */
		public Distance getDistance() {
			throw new NoSuchMethodError("Operation not supported yet");
		}

		/**
		 * @see #getDistance
		 */
		public void setDistance(Distance distance) {
			throw new NoSuchMethodError("Operation not supported yet");
		}

	}

	/**
	 * represents the comparison if the first geometry is within the box
	 */
	public class BoxImpl extends SpatialImpl implements Box {

		/**
		 * returns the Box the first geometry is compared to
		 */
		/*
		 * public GMLBox getBox() throws CSWFilterException {
		 * //Debug.debugMethodBegin( this, "getBox" );
		 *  // get the child node (element) that contains the geometry //
		 * operator that have to be a GMLBox GMLBox geom = new GMLBox_Impl(
		 * getGeometryElement() );
		 * 
		 * //Debug.debugMethodEnd(); return geom; }
		 */
	}

	public class DistanceImpl implements Distance {

		/**
		 * returns the distance
		 */
		public double getDistance() {
			throw new NoSuchMethodError("Operation not supported yet");
		}

		/**
		 * sets the distance
		 */
		public void setDistance(double distance) {
			throw new NoSuchMethodError("Operation not supported yet");
		}

		/**
		 * returns the units the distance is measured at
		 */
		public String getUnits() {
			throw new NoSuchMethodError("Operation not supported yet");
		}

		/**
		 * @see #getUnits
		 */
		public void setUnits(String units) {
			throw new NoSuchMethodError("Operation not supported yet");
		}

	}

}
