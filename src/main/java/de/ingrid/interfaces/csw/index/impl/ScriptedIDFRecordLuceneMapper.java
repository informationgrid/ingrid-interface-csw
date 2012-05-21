/**
 * 
 */
package de.ingrid.interfaces.csw.index.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.interfaces.csw.config.ConfigurationProvider;
import de.ingrid.interfaces.csw.index.RecordLuceneMapper;
import de.ingrid.interfaces.csw.tools.IdfUtils;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * @author joachim@wemove.com
 * 
 */
@Service
public class ScriptedIDFRecordLuceneMapper implements RecordLuceneMapper {

    final protected static Log log = LogFactory.getLog(ScriptedIDFRecordLuceneMapper.class);

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

    private CompiledScript compiledScript;

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ingrid.interfaces.csw.index.RecordLuceneMapper#map(de.ingrid.utils
     * .dsc.Record)
     */
    @Override
    public Document map(Record record, Map<String, Object> utils) throws Exception {
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
                if (this.engine instanceof Compilable) {
                    Compilable compilable = (Compilable) this.engine;
                    this.compiledScript = compilable.compile(new InputStreamReader(input));
                }
            }
            org.w3c.dom.Document idfDoc = IdfUtils.getIdfDocument(record);
            Bindings bindings = this.engine.createBindings();
            bindings.put("recordId", IdfUtils.getRecordId(idfDoc));
            bindings.put("recordNode", idfDoc);
            bindings.put("document", document);
            document.add(new Field("docid", ((Integer)utils.get("docid")).toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
            bindings.put("log", log);
            XPathUtils xpathUtils = new XPathUtils(new IDFNamespaceContext());
            bindings.put("XPATH", xpathUtils);
            for (Entry<String, Object> entry : utils.entrySet()) {
                bindings.put(entry.getKey(), entry.getValue());
            }
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

    public void setConfigurationProvider(ConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }
    
}
