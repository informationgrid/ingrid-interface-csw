/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.index.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.List;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.interfaces.csw.config.ConfigurationProvider;
import de.ingrid.interfaces.csw.harvest.impl.RecordCache;
import de.ingrid.interfaces.csw.index.Indexer;
import de.ingrid.interfaces.csw.tools.IdfUtils;
import de.ingrid.utils.dsc.Record;

/**
 * LuceneIndexer is used to put InGrid records into a Lucene index.
 * 
 * @author ingo@wemove.com
 */
@Service
public class LuceneIndexer implements Indexer {

    final protected static Log log = LogFactory.getLog(LuceneIndexer.class);

    /**
     * The path to the Lucene index
     */
    private File indexPath = null;

    /**
     * The update job configuration provider
     */
    @Autowired
    private ConfigurationProvider configurationProvider;
    
    /**
     * The script engine that runs the mapping script
     */
    private ScriptEngine engine;

    /**
     * The script that defines the mapping from InGrid record into a Lucene
     * document
     */
    private File mappingScript;

    private boolean compile = false;
    private CompiledScript compiledScript;

    @Override
    public void run(List<RecordCache> recordCacheList) throws Exception {

        if (log.isDebugEnabled()) {
            log.debug("Running indexer");
        }
        
        // overwrite indexer path with configuration
        if (configurationProvider != null) {
            this.indexPath = configurationProvider.getNewIndexPath();
        }
        
        // delete older indexing destination
        if (this.indexPath.exists()) {
            this.indexPath.delete();
        }

        int indexedRecords = 0;
        IndexWriter index = null;
        try {
            // open the index
            Directory indexDir = new SimpleFSDirectory(this.indexPath);
            index = new IndexWriter(indexDir, new StandardAnalyzer(Version.LUCENE_CURRENT), true,
                    IndexWriter.MaxFieldLength.LIMITED);

            // iterate over all caches an all records
            for (RecordCache cache : recordCacheList) {
                if (log.isDebugEnabled()) {
                    log.debug("Indexing cache " + cache.getCachePath());
                }
                for (Serializable cacheId : cache.getCachedIds()) {
                    Record record = cache.get(cacheId);
                    if (log.isDebugEnabled()) {
                        log.debug("Indexing record " + cacheId);
                    }
                    // map record data using script
                    Document doc = this.map(record);

                    // put the record into the index
                    index.addDocument(doc);
                    indexedRecords++;
                }
            }
            index.optimize();
        } finally {
            if (index != null) {
                index.close();
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Created index from " + indexedRecords + " records");
        }
    }

    /**
     * Map the given InGrid record to a Lucene document.
     * 
     * @param record
     * @return Document
     * @throws Exception
     */
    protected Document map(Record record) throws Exception {
        Document document = new Document();

        // overwrite indexer path with configuration
        if (configurationProvider != null) {
            this.mappingScript = configurationProvider.getMappingScript();
        }

        if (this.mappingScript == null) {
            log.error("Mapping script is not set!");
            throw new IllegalArgumentException("Mapping script is not set!");
        }

        InputStream input = null;
        try {
            input = new FileInputStream(this.mappingScript);
            if (this.engine == null) {
                String scriptName = this.mappingScript.getName();
                String extension = scriptName.substring(scriptName.lastIndexOf('.') + 1, scriptName.length());
                ScriptEngineManager mgr = new ScriptEngineManager();
                this.engine = mgr.getEngineByExtension(extension);
                if (this.compile) {
                    if (this.engine instanceof Compilable) {
                        Compilable compilable = (Compilable) this.engine;
                        this.compiledScript = compilable.compile(new InputStreamReader(input));
                    }
                }
            }
            org.w3c.dom.Document idfDoc = IdfUtils.getIdfDocument(record);
            Bindings bindings = this.engine.createBindings();
            bindings.put("recordId", IdfUtils.getRecordId(idfDoc));
            bindings.put("recordNode", idfDoc);
            bindings.put("document", document);
            bindings.put("log", log);
            if (this.compiledScript != null) {
                this.compiledScript.eval(bindings);
            } else {
                this.engine.eval(new InputStreamReader(input), bindings);
            }
        } catch (Exception ex) {
            log.error("Error mapping InGrid record to lucene document.", ex);
            throw ex;
        } finally {
            if (input != null) {
                input.close();
            }
        }
        return document;
    }

    @Override
    public File getIndexPath() {
        return this.indexPath;
    }

    /**
     * Set the path to the Lucene index
     * 
     * @param indexPath
     */
    public void setIndexPath(File indexPath) {
        this.indexPath = indexPath;
    }

    /**
     * Get the mapping script
     * 
     * @return Resource
     */
    public File getMappingScript() {
        return this.mappingScript;
    }

    /**
     * Set the mapping script
     * 
     * @param mappingScript
     */
    public void setMappingScript(File mappingScript) {
        this.mappingScript = mappingScript;
    }

    /**
     * Check if the mapping script should be compiled
     * 
     * @return Boolean
     */
    public boolean isCompile() {
        return this.compile;
    }

    /**
     * Set whether the mapping script should be compiled
     * 
     * @param compile
     */
    public void setCompile(boolean compile) {
        this.compile = compile;
    }
}
