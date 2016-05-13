/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.domain;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;

import de.ingrid.interfaces.csw.catalog.Manager;
import de.ingrid.interfaces.csw.catalog.impl.TestManager;
import de.ingrid.interfaces.csw.domain.filter.impl.LuceneFilterParser;
import de.ingrid.interfaces.csw.mapping.impl.CSWRecordCache;
import de.ingrid.interfaces.csw.search.impl.LuceneSearcher;
import de.ingrid.interfaces.csw.server.CSWServlet;
import de.ingrid.interfaces.csw.server.ServerFacade;
import de.ingrid.interfaces.csw.server.impl.GenericServer;
import de.ingrid.interfaces.csw.tools.LuceneTools;
import de.ingrid.interfaces.csw.tools.SoapNamespaceContext;
import de.ingrid.utils.xml.ConfigurableNamespaceContext;
import de.ingrid.utils.xml.Csw202NamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

public abstract class OperationTestBase extends TestCase {

    private static final String LIVE_INDEX_PATH = "tmp/index/live";
    private static final String CSW_CACHE_PATH = "tmp/cache/csw";

    private LuceneSearcher searcher = null;
    private Manager manager = null;
    private CSWServlet servlet = null;

    protected static XPathUtils xpath;
    static {
        ConfigurableNamespaceContext cnc = new ConfigurableNamespaceContext();
        cnc.addNamespaceContext(new Csw202NamespaceContext());
        cnc.addNamespaceContext(new SoapNamespaceContext());
        xpath = new XPathUtils(cnc);
    }

    @Override
    @Before
    public void setUp() throws Exception {
        // setup searcher
        FileUtils.deleteDirectory(new File(LIVE_INDEX_PATH));
        FileUtils.copyDirectory(new File("src/test/resources/index"), new File(LIVE_INDEX_PATH));

        FileUtils.deleteDirectory(new File(CSW_CACHE_PATH));
        FileUtils.copyDirectory(new File("src/test/resources/cache"), new File(CSW_CACHE_PATH));

        CSWRecordCache cache = new CSWRecordCache();
        cache.setCachePath(new File(CSW_CACHE_PATH));

        this.searcher = new LuceneSearcher();
        this.searcher.setIndexPath(new File(LIVE_INDEX_PATH));
        this.searcher.setFilterParser(new LuceneFilterParser());
        this.searcher.setRecordRepository(cache);
        this.searcher.setLuceneTools(new LuceneTools());
        this.searcher.start();

        this.manager = new TestManager();

        // create servlet
        GenericServer server = new GenericServer();
        server.setSearcher(this.searcher);
        server.setManager(manager);

        ServerFacade serverFacade = new ServerFacade();
        serverFacade.setCswServerImpl(server);

        this.servlet = new CSWServlet();
        this.servlet.setServerFacade(serverFacade);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        this.searcher.stop();
    }

    /**
     * InputStream class used with servlet response
     */
    protected class TestServletInputStream extends ServletInputStream {
        Reader buf = null;

        public TestServletInputStream(String input) {
            this.buf = new StringReader(input);
        }

        @Override
        public int read() throws IOException {
            return this.buf.read();
        }
    }

    /**
     * OutputStream class used with servlet response
     */
    protected class TestServletOutputStream extends ServletOutputStream {
        StringBuffer buf = null;

        public TestServletOutputStream(StringBuffer buf) {
            this.buf = buf;
        }

        @Override
        public void write(int c) throws IOException {
            this.buf.append((char)c);
        }
    }

    /**
     * Get the csw servlet
     * @return CSWServlet
     */
    protected CSWServlet getServlet() {
        return this.servlet;
    }

    /**
     * Set up default expectations for get requests.
     * @param context
     * @param request
     * @param response
     * @param result
     * @param requestStr
     * @param additionalParams (maybe null)
     * @throws IOException
     */
    protected void setupDefaultGetExpectations(Mockery context, final HttpServletRequest request,
            final HttpServletResponse response, StringBuffer result, final String requestStr,
            Map<String, String> additionalParams) throws IOException {
        final ServletOutputStream sos = new TestServletOutputStream(result);
        final List<String> fixedParameters = Arrays.asList(new String[]{"SERVICE", "REQUEST", "version", "partner"});
        final List<String> parameters = new ArrayList<String>(fixedParameters);
        if (additionalParams != null) {
            for (final Map.Entry<String, String> entry : additionalParams.entrySet()) {
                parameters.add(entry.getKey());
            }
        }
        context.checking(new Expectations() {{
            this.allowing(request).getParameterNames(); this.will(returnEnumeration(parameters));
            this.allowing(request).getParameter("SERVICE"); this.will(returnValue("CSW"));
            this.allowing(request).getParameter("REQUEST"); this.will(returnValue(requestStr));
            this.allowing(request).getParameter("version"); this.will(returnValue("2.0.2"));
            this.allowing(request).getParameter("partner"); this.will(returnValue("test"));
            this.allowing(response).setContentType("application/xml");
            this.allowing(response).setCharacterEncoding("UTF-8");
            this.allowing(response).getOutputStream(); this.will(returnValue(sos));
        }});
        if (additionalParams != null) {
            for (final Map.Entry<String, String> entry : additionalParams.entrySet()) {
                context.checking(new Expectations() {{
                    this.allowing(request).getParameter(entry.getKey()); this.will(returnValue(entry.getValue()));
                }});
            }
        }
    }

    /**
     * Set up default expectations for get requests with partner parameter set.
     * @param context
     * @param request
     * @param response
     * @param result
     * @param requestStr
     * @throws IOException
     */
    protected void setupPartnerParameterGetExpectations(Mockery context, final HttpServletRequest request,
            final HttpServletResponse response, StringBuffer result, final String requestStr) throws IOException {
        final ServletOutputStream sos = new TestServletOutputStream(result);
        final List<String> parameters = Arrays.asList(new String[]{"SERVICE", "REQUEST", "version", "partner"});
        context.checking(new Expectations() {{
            this.allowing(request).getParameterNames(); this.will(returnEnumeration(parameters));
            this.allowing(request).getParameter("SERVICE"); this.will(returnValue("CSW"));
            this.allowing(request).getParameter("REQUEST"); this.will(returnValue(requestStr));
            this.allowing(request).getParameter("version"); this.will(returnValue("2.0.2"));
            this.allowing(request).getParameter("partner"); this.will(returnValue("test"));
            this.allowing(response).setContentType("application/xml");
            this.allowing(response).setCharacterEncoding("UTF-8");
            this.allowing(response).getOutputStream(); this.will(returnValue(sos));
        }});
    }



    /**
     * Set up default expectations for post requests.
     * @param context
     * @param request
     * @param response
     * @param result
     * @param requestStr
     * @throws IOException
     */
    protected void setupDefaultPostExpectations(Mockery context, final HttpServletRequest request,
            final HttpServletResponse response, StringBuffer result, String requestStr) throws IOException {
        final ServletInputStream sis = new TestServletInputStream(requestStr);
        final ServletOutputStream sos = new TestServletOutputStream(result);
        context.checking(new Expectations() {{
            this.allowing(request).getHeaderNames();
            this.allowing(request).getContentType(); this.will(returnValue("application/xml"));
            this.allowing(request).getInputStream(); this.will(returnValue(sis));
            this.allowing(request).getHeader("Keep-Alive"); this.will(returnValue(null));
            this.allowing(request).getParameter("partner"); this.will(returnValue(""));
            this.allowing(request).getParameter("provider"); this.will(returnValue(""));
            this.allowing(request).getParameter("iplug"); this.will(returnValue(""));
            this.allowing(response).setStatus(HttpServletResponse.SC_OK);
            this.allowing(response).setHeader("Content-Type", "application/xml; charset=utf-8");
            this.allowing(response).setHeader(this.with(any(String.class)), this.with(any(String.class)));
            this.allowing(response).setContentType("application/xml");
            this.allowing(response).setCharacterEncoding("UTF-8");
            this.allowing(response).getOutputStream(); this.will(returnValue(sos));
        }});
    }

    /**
     * Set up default expectations for soap requests.
     * @param context
     * @param request
     * @param response
     * @param result
     * @param requestStr
     * @throws IOException
     */
    protected void setupDefaultSoapExpectations(Mockery context, final HttpServletRequest request,
            final HttpServletResponse response, StringBuffer result, String requestStr) throws IOException {
    	setupDefaultSoapExpectations(context, request, response, result, requestStr, null);
    }
    
    /**
     * Set up default expectations for soap requests.
     * @param context
     * @param request
     * @param response
     * @param result
     * @param requestStr
     * @param additionalParams (maybe null)
     * @throws IOException
     */
    protected void setupDefaultSoapExpectations(Mockery context, final HttpServletRequest request,
            final HttpServletResponse response, StringBuffer result, String requestStr,
            final Map<String, String> additionalParams) throws IOException {
        final ServletInputStream sis = new TestServletInputStream(requestStr);
        final ServletOutputStream sos = new TestServletOutputStream(result);
        context.checking(new Expectations() {{
            this.allowing(request).getHeaderNames();
            this.allowing(request).getContentType(); this.will(returnValue("application/soap+xml"));
            this.allowing(request).getInputStream(); this.will(returnValue(sis));
            this.allowing(request).getHeader("Keep-Alive"); this.will(returnValue(null));
            this.allowing(request).getParameter("partner"); this.will(returnValue(null));
            this.allowing(request).getParameter("provider"); this.will(returnValue(null));
            this.allowing(request).getParameter("iplug"); this.will(returnValue(null));
            this.allowing(response).setStatus(HttpServletResponse.SC_OK);
            this.allowing(response).setHeader("Content-Type", "application/soap+xml; charset=utf-8");
            this.allowing(response).setHeader(this.with(any(String.class)), this.with(any(String.class)));
            this.allowing(response).setContentType("application/soap+xml");
            this.allowing(response).setCharacterEncoding("UTF-8");
            this.allowing(response).getOutputStream(); this.will(returnValue(sos));
        }});
        if (additionalParams != null) {
            for (final Map.Entry<String, String> entry : additionalParams.entrySet()) {
                context.checking(new Expectations() {{
                    this.allowing(request).getParameter(entry.getKey()); this.will(returnValue(entry.getValue()));
                }});
            }
        }
    }
}
