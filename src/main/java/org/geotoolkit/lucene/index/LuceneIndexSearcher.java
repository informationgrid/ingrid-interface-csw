/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.lucene.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;
import org.geotoolkit.lucene.IndexingException;
import org.geotoolkit.lucene.LuceneUtils;
import org.geotoolkit.lucene.SearchingException;
import org.geotoolkit.lucene.filter.SerialChainFilter;
import org.geotoolkit.lucene.filter.SpatialQuery;
import org.geotoolkit.lucene.tree.TreeIndexReaderWrapper;
import org.geotoolkit.util.FileUtilities;


/**
 * An Lucene index searcher. allowing to perform query on the index.
 * 
 * @author Guilhem legal (Geomatys)
 * @module pending
 */
public class LuceneIndexSearcher extends IndexLucene {

    /**
     * This is the index searcher of Lucene.
     */
    protected IndexSearcher searcher;

    /**
     * This is the index reader of Lucene.
     */
    protected IndexReader reader;
    
    /**
     * A default Query requesting all the document
     */
    private final static Query SIMPLE_QUERY = new TermQuery(new Term("metafile", "doc"));

    /**
     * A map of cached request
     */
    private final Map<SpatialQuery, Set<String>> cachedQueries = new ConcurrentHashMap<SpatialQuery, Set<String>>();

    /**
     * The maximum size of the map of queries.
     */
    private static final int MAX_CACHED_QUERIES_SIZE = 50;

    /**
     * A flag indicating if the cache system for query is enabled.
     */
    private boolean isCacheEnabled;

    /**
     * A list of metadata ID ordered by DocID.
     */
    private List<String> identifiers;
    
    /**
     * A list of numeric fields names.
     */
    private Map<String, Character> numericFields;
    
    /**
     * A flag indicating if all the geometry indexed are envelope.
     * if set, no JTS filter will be applied on geometry search (only R-tree search)
     */
    private final boolean envelopeOnly;
    
    /**
     * Build a new index searcher.
     *
     * @param configDir The configuration directory where to build the index directory.
     * @param serviceID the "ID" of the service (allow multiple index in the same directory). The value "" is allowed.
     * @param analyzer  A lucene Analyzer (Default is ClassicAnalyzer)
     */
    public LuceneIndexSearcher(final File configDir, final String serviceID, final Analyzer analyzer) throws IndexingException {
        this(configDir, serviceID, analyzer, false);
    }
    
    /**
     * Build a new index searcher.
     *
     * @param configDir The configuration directory where to build the index directory.
     * @param serviceID the "ID" of the service (allow multiple index in the same directory). The value "" is allowed.
     * @param analyzer  A lucene Analyzer (Default is ClassicAnalyzer)
     * @param envelopeOnly A flag indicating if all the geometry indexed are envelope.
     */
    public LuceneIndexSearcher(final File configDir, final String serviceID, final Analyzer analyzer, final boolean envelopeOnly) throws IndexingException {
        super(analyzer);
        this.envelopeOnly = envelopeOnly;
        if (envelopeOnly) {
            LOGGER.info("envelope only mode activated");
        }
        try {
            // we get the last index directory
            long maxTime = 0;
            File currentIndexDirectory = null;
            if (configDir != null && configDir.exists() && configDir.isDirectory()) {
                for (File indexDirectory : configDir.listFiles(new IndexDirectoryFilter(serviceID))) {
                    String suffix = indexDirectory.getName();
                    suffix = suffix.substring(suffix.lastIndexOf('-') + 1);
                    try {
                        long currentTime = Long.parseLong(suffix);
                        if (currentTime > maxTime) {
                            maxTime = currentTime;
                            currentIndexDirectory = indexDirectory;
                        }
                    } catch (NumberFormatException ex) {
                        LOGGER.log(Level.WARNING, "Unable to parse the timestamp:{0}", suffix);
                    }
                }
            }
            if (currentIndexDirectory != null && currentIndexDirectory.exists()) {
                setFileDirectory(currentIndexDirectory);
                try {
                    this.numericFields          = new HashMap<String, Character>();
                    final File numericFieldFile = new File(currentIndexDirectory, "numericFields.properties");
                    final Properties prop       = FileUtilities.getPropertiesFromFile(numericFieldFile);
                    for (String fieldName : prop.stringPropertyNames()) {
                        this.numericFields.put(fieldName, ((String)prop.get(fieldName)).charAt(0));
                    }
                    
                } catch (IOException ex) {
                    LOGGER.log(Level.WARNING, "IO exception while reading numericFields file", ex);
                }
             } else {
                throw new IndexingException("The index searcher can't find a index directory.");
            }
            isCacheEnabled        = true;
            initSearcher();
            initIdentifiersList();

        } catch (CorruptIndexException ex) {
            throw new IndexingException("Corruption encountered during index searcher creation", ex);
        } catch (ParseException ex) {
            throw new IndexingException("Failure to parse during index searcher creation", ex);
        } catch (IOException ex) {
            throw new IndexingException("IO Exception during index searcher creation", ex);
        }  catch (SearchingException ex) {
            throw new IndexingException("Searching Exception during index searcher creation", ex);
        }
        
    }

    /**
     * initialize the IndexSearcher of this index.
     */
    private void initSearcher() throws CorruptIndexException, IOException {
        final File indexDirectory = getFileDirectory();
        readTree();
        reader = IndexReader.open(LuceneUtils.getAppropriateDirectory(indexDirectory));
        final IndexReader indexReader  = new TreeIndexReaderWrapper(reader, rTree, envelopeOnly);
        searcher                  = new IndexSearcher(indexReader);
        LOGGER.log(Level.INFO, "Creating new Index Searcher with index directory:{0}", indexDirectory.getPath());
       
    }

    /**
     * Fill the list of identifiers ordered by doc ID
     */
    private void initIdentifiersList() throws IOException, CorruptIndexException, ParseException, SearchingException {
        identifiers = new ArrayList<String>();
        for (int i = 0; i < searcher.maxDoc(); i++) {
            final String metadataID = getMatchingID(searcher.doc(i));
            identifiers.add(i, metadataID);
        }
        LOGGER.log(logLevel, "{0} records found.", identifiers.size());
    }

    /**
     * Refresh the searcher (must be call after deleting document from the index for example)
     *
     * @throws org.constellation.lucene.IndexingException
     */
    public void refresh() throws IndexingException {
        try {
            initSearcher();
            initIdentifiersList();
            cachedQueries.clear();
            LOGGER.log(logLevel, "refreshing index searcher");
        } catch (ParseException ex) {
            throw new IndexingException("Parse exception encountered during refreshing the index searcher", ex);
        } catch (SearchingException ex) {
            throw new IndexingException("Searching exception encountered during refreshing the index searcher", ex);
        } catch (CorruptIndexException ex) {
            throw new IndexingException("Corruption exception encountered during refreshing the index searcher", ex);
        } catch (IOException ex) {
            throw new IndexingException("IO Exception during refreshing the index searcher", ex);
        }
    }

    /**
     * This method proceed a lucene search and to verify that the identifier exist.
     * If it exist it return the database ID.
     *
     * @param id A simple Term query on "identifier field".
     *
     * @return A database id.
     */
    public String identifierQuery(final String id) throws SearchingException {
        try {
            final TermQuery query = new TermQuery(new Term(getIdentifierSearchField(), id));
            final Set<String> results = new LinkedHashSet<String>();
            final int maxRecords = searcher.maxDoc();
            if (maxRecords == 0) {
                LOGGER.warning("There is no document in the index");
                return null;
            }
            final TopDocs hits = searcher.search(query, maxRecords);
            for (ScoreDoc doc : hits.scoreDocs) {
                results.add(searcher.doc(doc.doc, new IDFieldSelector()).get("id"));
            }
            if (results.size() > 1) {
                LOGGER.log(Level.WARNING, "multiple record in lucene index for identifier: {0}", id);
            }
            if (!results.isEmpty()) {
                return results.iterator().next();
            }
        } catch (IOException ex) {
            throw new SearchingException("Parse Exception while performing lucene request", ex);
        }
        return null;
    }

    /**
     * Return the name of the identifier field used in the identifierQuery method.
     * 
     * @return the name of the identifier field.
     */
    public String getIdentifierSearchField() {
        return "id";
    }
    
    public Map<String, Character> getNumericFields() {
        return numericFields;
    }

    /**
     * This method return the database ID of a matching Document
     *
     * @param doc A matching document.
     *
     * @return A database id.
     */
    public String getMatchingID(final Document doc) throws SearchingException {
        return doc.get("id");
    }

    /**
     * This method proceed a lucene search and returns a list of ID.
     *
     * @param spatialQuery The lucene query string with spatials filters.
     *
     * @return A List of metadata identifiers.
     */
    public Set<String> doSearch(final SpatialQuery spatialQuery) throws SearchingException {
        try {
            final long start = System.currentTimeMillis();
            final Set<String> results = new LinkedHashSet<String>();

            //we look for a cached Query
            if (isCacheEnabled && cachedQueries.containsKey(spatialQuery)) {
                final Set<String> cachedResults = cachedQueries.get(spatialQuery);
                LOGGER.log(logLevel, "returning result from cache ({0} matching documents)", results.size());
                return cachedResults;
            }

            int maxRecords = searcher.maxDoc();
            if (maxRecords == 0) {
                LOGGER.warning("The index seems to be empty.");
                maxRecords = 1;
            }

            final String field       = "title";
            String stringQuery       = spatialQuery.getQuery();
            final QueryParser parser = new ExtendedQueryParser(Version.LUCENE_36, field, analyzer, numericFields);
            parser.setDefaultOperator(Operator.AND);
            
            // remove term:* query
            stringQuery = removeOnlyWildchar(stringQuery);
            
            
            // we enable the leading wildcard mode if the first character of the query is a '*'
            if (stringQuery.indexOf(":*") != -1 || stringQuery.indexOf(":?") != -1 || stringQuery.indexOf(":(*") != -1
             || stringQuery.indexOf(":(+*") != -1 || stringQuery.indexOf(":+*") != -1) {
                parser.setAllowLeadingWildcard(true);
                LOGGER.log(Level.FINER, "Allowing leading wildChar");
                BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
            }
            
            // search terms beginning with a '-' are not allowed and lead to parse exception
            if (stringQuery.indexOf(":-") != -1)
            	stringQuery = stringQuery.replaceAll(":-", ":\\\\-");

            //we set off the mecanism setting all the character to lower case
            // we do that for range queries only for now. TODO see if we need to set it every time
            if (stringQuery.contains(" TO ")) {
                parser.setLowercaseExpandedTerms(false);
            }
            final Query query;
            if (!stringQuery.isEmpty()) {
               query = parser.parse(stringQuery);
            } else {
                query = SIMPLE_QUERY;
            }
            
            LOGGER.log(Level.FINER, "QueryType:{0}", query.getClass().getName());
            final Filter filter = spatialQuery.getSpatialFilter();
            final int operator  = spatialQuery.getLogicalOperator();
            final Sort sort     = spatialQuery.getSort();
            String sorted = "";
            if (sort != null) {
                sorted = "\norder by: " + sort.toString();
            }
            String f = "";
            if (filter != null) {
                f = '\n' + filter.toString();
            }
            String operatorValue = "";
            if (!(operator == SerialChainFilter.AND || (operator == SerialChainFilter.OR && filter == null))) {
                operatorValue = '\n' + SerialChainFilter.valueOf(operator);
            }
            LOGGER.log(logLevel, "Searching for: " + query.toString(field) + operatorValue +  f + sorted + "\nmax records: " + maxRecords);

            // simple query with an AND
            if (operator == SerialChainFilter.AND || (operator == SerialChainFilter.OR && filter == null)) {
                final TopDocs docs;
                if (sort != null) {
                    docs = searcher.search(query, filter, maxRecords, sort);
                } else {
                    docs = searcher.search(query, filter, maxRecords);
                }
                for (ScoreDoc doc : docs.scoreDocs) {
                    results.add(identifiers.get(doc.doc));
                }

            // for a OR we need to perform many request
            } else if (operator == SerialChainFilter.OR) {
                final TopDocs hits1;
                final TopDocs hits2;
                if (sort != null) {
                    hits1 = searcher.search(query, null, maxRecords, sort);
                    hits2 = searcher.search(SIMPLE_QUERY, spatialQuery.getSpatialFilter(), maxRecords, sort);
                } else {
                    hits1 = searcher.search(query, maxRecords);
                    hits2 = searcher.search(SIMPLE_QUERY, spatialQuery.getSpatialFilter(), maxRecords);
                }
                for (ScoreDoc doc : hits1.scoreDocs) {
                    results.add(identifiers.get(doc.doc));
                }
                for (ScoreDoc doc : hits2.scoreDocs) {
                    final String id = identifiers.get(doc.doc);
                    results.add(id);
                }

            // for a NOT we need to perform many request
            } else if (operator == SerialChainFilter.NOT) {
                final TopDocs hits1;
                if (sort != null) {
                    hits1 = searcher.search(query, filter, maxRecords, sort);
                } else {
                    hits1 = searcher.search(query, filter, maxRecords);
                }
                final Set<String> unWanteds = new LinkedHashSet<String>();
                for (ScoreDoc doc : hits1.scoreDocs) {
                    unWanteds.add(identifiers.get(doc.doc));
                }

                final TopDocs hits2;
                if (sort != null) {
                    hits2 = searcher.search(SIMPLE_QUERY, null, maxRecords, sort);
                } else {
                    hits2 = searcher.search(SIMPLE_QUERY, maxRecords);
                }
                for (ScoreDoc doc : hits2.scoreDocs) {
                    final String id = identifiers.get(doc.doc);
                    if (!unWanteds.contains(id)) {
                        results.add(id);
                    }
                }

            } else {
                throw new IllegalArgumentException("unsupported logical Operator");
            }

            // if we have some subQueries we execute it separely and merge the result
            if (spatialQuery.getSubQueries().size() > 0) {

                if (operator == SerialChainFilter.OR && query.equals(SIMPLE_QUERY)) {
                    results.clear();
                }
                
                for (SpatialQuery sub : spatialQuery.getSubQueries()) {
                    final Set<String> subResults = doSearch(sub);
                    if (operator == SerialChainFilter.AND) {
                        final Set<String> toRemove   = new HashSet<String>();
                        for (String r : results) {
                            if (!subResults.contains(r)) {
                                toRemove.add(r);
                            }
                        }
                        results.removeAll(toRemove);
                    } else if (operator == SerialChainFilter.OR){
                        results.addAll(subResults);
                        
                    } else {
                        LOGGER.warning("unimplemented case in doSearch");
                    }
                }
            }

            //we put the query in cache
            putInCache(spatialQuery, results);
            
            LOGGER.log(logLevel, results.size() + " total matching documents (" + (System.currentTimeMillis() - start) + "ms)");
            return results;
        } catch (ParseException ex) {
            throw new SearchingException("Parse Exception while performing lucene request", ex);
        } catch (IOException ex) {
           throw new SearchingException("IO Exception while performing lucene request", ex);
        }
    }

    public static String removeOnlyWildchar(String s) {
        final String pattern = "[^: +\\(]*:\\* ";
        s = s.replaceAll(pattern, "metafile:doc ");
        
        final String pattern2 = "[^: +\\(]*:\\*$";
        s = s.replaceAll(pattern2, "metafile:doc");
        
        final String pattern3 = "[^: +\\(]*:[(][*][)]";
        s = s.replaceAll(pattern3, "metafile:doc");
        
        final String pattern4 = "[^: +\\(]*:\\*[)]";
        s = s.replaceAll(pattern4, "metafile:doc)");
        
        return s;
    }
    
    /**
     * Add a query and its results to the cache.
     * if the map has reach the maximum size the older query is removed from the cache.
     *
     * @param query a Lucene spatial query.
     * @param results A list of metadataIdentifier.
     */
    private void putInCache(final SpatialQuery query, final Set<String> results) {
        if (isCacheEnabled) {
            // if we had reach the maximum cache size we remove the first request
            if (cachedQueries.size() >= MAX_CACHED_QUERIES_SIZE) {
                cachedQueries.remove(cachedQueries.keySet().iterator().next());
            }
            cachedQueries.put(query, results);
        }
    }

    /**
     * Free the resources when closing the searcher.
     */
    @Override
    public void destroy() {
        super.destroy();
        LOGGER.info("shutting down index searcher");
        try {
            if (searcher != null) {
            	if (reader != null) {
            		reader.close();
            	}
            	searcher.close();
            }
            cachedQueries.clear();
        } catch (IOException ex) {
            LOGGER.warning("IOException while closing the index searcher");
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Exception while closing the index searcher", ex);
        }
    }

    public boolean isCacheEnabled() {
        return isCacheEnabled;
    }

    public void setCacheEnabled(boolean isCacheEnabled) {
        this.isCacheEnabled = isCacheEnabled;
    }


}
