/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2026 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.domain.constants;

import java.util.Hashtable;
import java.util.Map;

import javax.xml.namespace.QName;

public enum Namespace {

    CSW {
        @Override
        public QName getQName() {
            return nsCSW;
        }
    },
    CSW_2_0_2 {
        @Override
        public QName getQName() {
            return nsCSW_2_0_2;
        }
    },
    OWS {
        @Override
        public QName getQName() {
            return nsOWS;
        }
    },
    ISO {
        @Override
        public QName getQName() {
            return nsISO;
        }
    },
    GML {
        @Override
        public QName getQName() {
            return nsGML;
        }
    },
    OGC {
        @Override
        public QName getQName() {
            return nsOGC;
        }
    },
    GMD {
        @Override
        public QName getQName() {
            return nsGMD;
        }
    },

    CSW_PROFILE {
        @Override
        public QName getQName() {
            return nsCSW_PROFILE;
        }
    },
    CSW_RECORD {
        @Override
        public QName getQName() {
            return nsCSW_RECORD;
        }
    },
    CSW_DATASET {
        @Override
        public QName getQName() {
            return nsCSW_DATASET;
        }
    };

    // conflicting with GMD when retrieving by uri
//    ISO_METADATA {
//        @Override
//        public QName getQName() {
//            return nsISO_METADATA;
//        }
//    };

    /**
     * Get the QName of a namespace constant
     *
     * @return
     */
    public abstract QName getQName();

    public static Namespace getByUri(String name) {
        Namespace ns = nameMapping.get(name);
        if (ns == null) {
            throw new IllegalArgumentException("The namespace '" + name + "' is unknown.");
        }
        return ns;
    }

    /**
     * namespaces
     */
    private static final QName nsCSW = new QName("http://www.opengis.net/cat/csw", "", "csw");
    private static final QName nsCSW_2_0_2 = new QName(nsCSW.getNamespaceURI() + "/2.0.2", "", nsCSW.getPrefix());
    private static final QName nsOWS = new QName("http://www.opengis.net/ows", "", "ows");
    private static final QName nsISO = new QName("http://www.opengis.net/cat/csw/apiso/1.0", "", "iso");
    private static final QName nsGML = new QName("http://www.opengis.net/gml/3.2", "", "gml");
    private static final QName nsOGC = new QName("http://www.opengis.net/ogc", "", "ogc");
    private static final QName nsGMD = new QName("http://www.isotc211.org/2005/gmd", "", "gmd");

    private static final QName nsCSW_PROFILE = new QName(nsCSW.getNamespaceURI(), "profile", nsCSW.getPrefix());
    private static final QName nsCSW_RECORD = new QName(nsCSW.getNamespaceURI(), "Record", nsCSW.getPrefix());
    private static final QName nsCSW_DATASET = new QName(nsCSW.getNamespaceURI(), "dataset", nsCSW.getPrefix());

    // private static final QName nsISO_METADATA = new QName(nsGMD.getNamespaceURI(), "MD_Metadata", nsGMD.getPrefix());

    /**
     * Namespace URI to namespace mapping
     */
    private static Map<String, Namespace> nameMapping = null;

    static {
        nameMapping = new Hashtable<String, Namespace>();
        for (Namespace ns : Namespace.values()) {
            nameMapping.put(ns.getQName().getNamespaceURI(), ns);
        }
    }
}
