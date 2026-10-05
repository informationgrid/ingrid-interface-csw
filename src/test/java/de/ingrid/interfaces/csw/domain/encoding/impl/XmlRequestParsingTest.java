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
package de.ingrid.interfaces.csw.domain.encoding.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;

import de.ingrid.interfaces.csw.tools.StringUtils;

/**
 * Request XML must keep ordinary CSW documents intact and must not resolve external entities.
 */
public class XmlRequestParsingTest {

    @Test
    void parsesOrdinaryRequestAndPredefinedEntities() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<GetCapabilities service=\"CSW\" xmlns=\"http://www.opengis.net/cat/csw/2.0.2\">"
                + "<AcceptVersions><Version>a&amp;b</Version></AcceptVersions>"
                + "</GetCapabilities>";

        Node body = new XMLEncoding().extractRequestBody(requestWithBody(xml, "text/xml"));

        assertEquals("GetCapabilities", body.getLocalName());
        assertEquals("CSW", body.getAttributes().getNamedItem("service").getNodeValue());
        assertEquals("a&b", body.getTextContent().replaceAll("\\s+", ""));
    }

    @Test
    void rejectsExternalEntityOnXmlPost() throws Exception {
        Path secret = Files.createTempFile("csw-entity", ".txt");
        String marker = "csw-entity-marker";
        Files.write(secret, marker.getBytes(StandardCharsets.UTF_8));
        try {
            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<!DOCTYPE GetCapabilities [<!ENTITY ext SYSTEM \"" + secret.toUri() + "\">]>"
                    + "<GetCapabilities service=\"CSW\" xmlns=\"http://www.opengis.net/cat/csw/2.0.2\">"
                    + "&ext;</GetCapabilities>";

            RuntimeException error = assertThrows(RuntimeException.class,
                    () -> new XMLEncoding().extractRequestBody(requestWithBody(xml, "text/xml")));
            assertFalse(String.valueOf(error.getMessage()).contains(marker));
            if (error.getCause() != null) {
                assertFalse(String.valueOf(error.getCause().getMessage()).contains(marker));
            }
        } finally {
            Files.deleteIfExists(secret);
        }
    }

    @Test
    void rejectsExternalEntityInKvpConstraint() throws Exception {
        Path secret = Files.createTempFile("csw-entity", ".txt");
        String marker = "csw-entity-marker";
        Files.write(secret, marker.getBytes(StandardCharsets.UTF_8));
        try {
            String xml = "<?xml version=\"1.0\"?>"
                    + "<!DOCTYPE Filter [<!ENTITY ext SYSTEM \"" + secret.toUri() + "\">]>"
                    + "<Filter xmlns=\"http://www.opengis.net/ogc\">&ext;</Filter>";

            Exception error = assertThrows(Exception.class, () -> StringUtils.stringToDocument(xml));
            assertFalse(String.valueOf(error.getMessage()).contains(marker));
        } finally {
            Files.deleteIfExists(secret);
        }
    }

    @Test
    void parsesSoapRequestBody() throws Exception {
        byte[] soapBytes = Files.readAllBytes(Paths.get("src/test/resources/requests/get_capabilities_invalid_4_soap.xml"));
        String soap = new String(soapBytes, StandardCharsets.UTF_8);

        Node body = new Soap12Encoding().extractRequestBody(requestWithBody(soap, "application/soap+xml"));

        assertEquals("GetCapabilities", body.getLocalName());
        assertEquals("CSW", body.getAttributes().getNamedItem("service").getNodeValue());
        assertTrue(body.getTextContent().contains("0.0.6"));
    }

    @Test
    void rejectsExternalEntityOnSoapPost() throws Exception {
        Path secret = Files.createTempFile("csw-entity", ".txt");
        String marker = "csw-entity-marker";
        Files.write(secret, marker.getBytes(StandardCharsets.UTF_8));
        try {
            String soap = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<!DOCTYPE soapenv:Envelope [<!ENTITY ext SYSTEM \"" + secret.toUri() + "\">]>"
                    + "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\">"
                    + "<soapenv:Body><GetCapabilities service=\"CSW\" "
                    + "xmlns=\"http://www.opengis.net/cat/csw/2.0.2\">&ext;</GetCapabilities>"
                    + "</soapenv:Body></soapenv:Envelope>";

            RuntimeException error = assertThrows(RuntimeException.class,
                    () -> new Soap12Encoding().extractRequestBody(requestWithBody(soap, "application/soap+xml")));
            assertFalse(String.valueOf(error.getMessage()).contains(marker));
        } finally {
            Files.deleteIfExists(secret);
        }
    }

    private static HttpServletRequest requestWithBody(String body, String contentType) {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        Map<String, String> headers = Collections.singletonMap("Content-Type", contentType);
        return (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class<?>[] { HttpServletRequest.class },
                (proxy, method, args) -> {
                    switch (method.getName()) {
                    case "getInputStream":
                        return new ByteArrayServletInputStream(bytes);
                    case "getHeaderNames":
                        return Collections.enumeration(headers.keySet());
                    case "getHeader":
                        return headers.get(args[0]);
                    case "getContentType":
                        return contentType;
                    case "getHeaders":
                        String value = headers.get(args[0]);
                        Enumeration<String> values = value == null
                                ? Collections.emptyEnumeration()
                                : Collections.enumeration(Collections.singletonList(value));
                        return values;
                    default:
                        Class<?> returnType = method.getReturnType();
                        if (returnType.equals(boolean.class)) {
                            return false;
                        }
                        if (returnType.equals(int.class)) {
                            return 0;
                        }
                        if (returnType.equals(long.class)) {
                            return 0L;
                        }
                        return null;
                    }
                });
    }

    private static final class ByteArrayServletInputStream extends ServletInputStream {
        private final StringReader reader;

        private ByteArrayServletInputStream(byte[] body) {
            this.reader = new StringReader(new String(body, StandardCharsets.UTF_8));
        }

        @Override
        public int read() throws IOException {
            return this.reader.read();
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
        }
    }
}
