//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.04.18 at 04:34:42 PM MESZ 
//


package de.ingrid.interfaces.csw.domain.filter.ogc1_1_0;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class RingAbstract
    extends JAXBElement<AbstractRingType>
{

    protected final static QName NAME = new QName("http://www.opengis.net/gml", "_Ring");

    public RingAbstract(AbstractRingType value) {
        super(NAME, ((Class) AbstractRingType.class), null, value);
    }

    public RingAbstract() {
        super(NAME, ((Class) AbstractRingType.class), null, null);
    }

}
