//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.04.18 at 04:34:42 PM MESZ 
//


package de.ingrid.interfaces.csw.domain.filter.ogc1_1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * Value of a speed, with its units. Uses the MeasureType with the restriction that the unit of measure referenced by uom must be suitable for a velocity, such as metres per second or miles per hour.
 * 
 * <p>Java class for SpeedType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SpeedType">
 *   &lt;simpleContent>
 *     &lt;restriction base="&lt;http://www.opengis.net/gml>MeasureType">
 *     &lt;/restriction>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpeedType")
public class SpeedType
    extends MeasureType
{


}
