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
package de.ingrid.interfaces.csw.tools;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Document builders that do not resolve external entities, DTDs, or remote schemas.
 * CSW request documents are schema-less XML and do not need a DTD.
 */
public final class SecureXml {

    private static final String DISALLOW_DOCTYPE = "http://apache.org/xml/features/disallow-doctype-decl";
    private static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    private static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
    private static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";

    private static final EntityResolver REJECT_EXTERNAL_ENTITIES = (publicId, systemId) -> {
        throw new SAXException("External XML entity resolution is disabled");
    };

    private static final ErrorHandler QUIET_ERROR_HANDLER = new ErrorHandler() {
        @Override
        public void warning(SAXParseException exception) {
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            throw exception;
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            throw exception;
        }
    };

    private SecureXml() {
    }

    public static DocumentBuilderFactory newDocumentBuilderFactory() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature(DISALLOW_DOCTYPE, true);
            factory.setFeature(EXTERNAL_GENERAL_ENTITIES, false);
            factory.setFeature(EXTERNAL_PARAMETER_ENTITIES, false);
            factory.setFeature(LOAD_EXTERNAL_DTD, false);
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("Cannot configure a secure XML parser", e);
        }
        try {
            factory.setXIncludeAware(false);
        } catch (UnsupportedOperationException ignored) {
            // XInclude is already off when the parser does not support it.
        }
        factory.setExpandEntityReferences(false);
        try {
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        } catch (IllegalArgumentException ignored) {
            // Some parsers reject JAXP 1.5 access properties. The features above still apply.
        }
        return factory;
    }

    public static DocumentBuilder newDocumentBuilder(boolean namespaceAware) {
        DocumentBuilderFactory factory = newDocumentBuilderFactory();
        factory.setNamespaceAware(namespaceAware);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            harden(builder);
            return builder;
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("Cannot configure a secure XML parser", e);
        }
    }

    /**
     * Re-apply parser callbacks that {@link DocumentBuilder#reset()} clears.
     */
    public static void harden(DocumentBuilder builder) {
        builder.setEntityResolver(REJECT_EXTERNAL_ENTITIES);
        builder.setErrorHandler(QUIET_ERROR_HANDLER);
    }
}
