//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.04.18 at 04:34:42 PM MESZ 
//


package de.ingrid.interfaces.csw.domain.filter.ogc1_1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BBOXType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BBOXType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}SpatialOpsType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ogc}PropertyName" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}Envelope"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BBOXType", namespace = "http://www.opengis.net/ogc", propOrder = {
    "propertyName",
    "envelope"
})
public class BBOXType
    extends SpatialOpsType
{

    @XmlElement(name = "PropertyName")
    protected PropertyNameType propertyName;
    @XmlElement(name = "Envelope", namespace = "http://www.opengis.net/gml", required = true)
    protected EnvelopeType envelope;

    /**
     * Gets the value of the propertyName property.
     * 
     * @return
     *     possible object is
     *     {@link PropertyNameType }
     *     
     */
    public PropertyNameType getPropertyName() {
        return propertyName;
    }

    /**
     * Sets the value of the propertyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link PropertyNameType }
     *     
     */
    public void setPropertyName(PropertyNameType value) {
        this.propertyName = value;
    }

    /**
     * Gets the value of the envelope property.
     * 
     * @return
     *     possible object is
     *     {@link EnvelopeType }
     *     
     */
    public EnvelopeType getEnvelope() {
        return envelope;
    }

    /**
     * Sets the value of the envelope property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnvelopeType }
     *     
     */
    public void setEnvelope(EnvelopeType value) {
        this.envelope = value;
    }

}
