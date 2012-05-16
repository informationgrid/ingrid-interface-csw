/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.constants;

import javax.xml.namespace.QName;

public enum TypeName {
    MD_METADATA {
        @Override
        public QName getQName() {
            return Namespace.ISO_METADATA.getQName();
        }
    },
    RECORD {
        @Override
        public QName getQName() {
            return Namespace.CSW_RECORD.getQName();
        }
    },
    DATASET {
        @Override
        public QName getQName() {
            return Namespace.CSW_DATASET.getQName();
        }
    };

    /**
     * Get the QName of a typename constant
     * 
     * @return
     */
    public abstract QName getQName();

    /**
     * Get TypeName from qualified string value
     * 
     * @param qName
     * @return Null if not found.
     */
    public static TypeName getFromQualifiedString(String qName) {
        for (TypeName tName : values()) {
            if ((tName.getQName().getPrefix() + ":" + tName.getQName().getLocalPart()).equalsIgnoreCase(qName)) {
                return tName;
            }
        }
        return null;
    }

}
