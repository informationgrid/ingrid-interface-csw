/**
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.harvest.impl;

import java.io.Serializable;

import de.ingrid.interfaces.csw.cache.AbstractFileCache;
import de.ingrid.interfaces.csw.harvest.ibus.IdfRecordPreProcessor;
import de.ingrid.interfaces.csw.tools.IdfUtils;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.idf.IdfTool;

/**
 * A cache that stores InGrid records.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public class RecordCache extends AbstractFileCache<Record> implements Serializable {

    private static final long serialVersionUID = RecordCache.class.getName().hashCode();

    private IdfRecordPreProcessor processor;

    @Override
    public Serializable getCacheId(Record document) throws Exception {
        // TODO might be optimized by caching the id
        Serializable id = IdfUtils.getRecordId(document);
        if (id == null) {
            // make sure the record has a appropriate uuid, if the IDF document has no gmd:fileIdentifier element.
            log.warn("IDF has no gmd:fileIdentifier Element set.");
        }
        return id;
    }

    @Override
    public String serializeDocument(Serializable id, Record document) {
        if (processor != null) {
            processor.process(document);
        }
        return IdfTool.getIdfDataFromRecord(document);
    }

    @Override
    public Record unserializeDocument(Serializable id, String str) {
        return IdfTool.createIdfRecord(str, true);
    }

    @Override
    public AbstractFileCache<Record> newInstance() {
        return new RecordCache();
    }

    public void setProcessor(IdfRecordPreProcessor processor) {
        this.processor = processor;
    }

}